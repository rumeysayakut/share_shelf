
package com.shareshelf.service;

import com.shareshelf.dto.response.BorrowingResponseDto;
import com.shareshelf.entity.*;
import com.shareshelf.mapper.BorrowingMapper;
import com.shareshelf.repository.BookShareRepository;
import com.shareshelf.repository.BorrowingRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BorrowingService {

    private final BorrowingRepository borrowingRepository;
    private final BookShareRepository bookShareRepository;
    private final BorrowingMapper borrowingMapper;

    private final List<BorrowingStatus> activeStatuses = List.of(
            BorrowingStatus.ACCEPTED,
            BorrowingStatus.BORROWED,
            BorrowingStatus.RETURN_PENDING,
            BorrowingStatus.OVERDUE,
            BorrowingStatus.DISPUTED
    );

    private final List<BorrowingStatus> requestStatuses = List.of(
            BorrowingStatus.PENDING,
            BorrowingStatus.ACCEPTED,
            BorrowingStatus.BORROWED,
            BorrowingStatus.RETURN_PENDING,
            BorrowingStatus.OVERDUE,
            BorrowingStatus.DISPUTED
    );

    private final List<BorrowingStatus> incomingStatuses = List.of(
            BorrowingStatus.PENDING,
            BorrowingStatus.ACCEPTED,
            BorrowingStatus.BORROWED,
            BorrowingStatus.RETURN_PENDING,
            BorrowingStatus.RETURNED,
            BorrowingStatus.OVERDUE,
            BorrowingStatus.DISPUTED
    );

    public BorrowingService(
            BorrowingRepository borrowingRepository,
            BookShareRepository bookShareRepository,
            BorrowingMapper borrowingMapper) {
        this.borrowingRepository = borrowingRepository;
        this.bookShareRepository = bookShareRepository;
        this.borrowingMapper = borrowingMapper;
    }

    @Transactional
    public BorrowingResponseDto createRequest(
            Long bookShareId,
            User currentUser) {

        if (currentUser == null || currentUser.getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Ödünç alma isteği göndermek için giriş yapmalısınız."
            );
        }

        BookShare bookShare = bookShareRepository.findById(bookShareId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Kitap paylaşımı bulunamadı."
                ));

        if (bookShare.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Kendi paylaştığınız kitabı ödünç alamazsınız."
            );
        }

        if (bookShare.getShareType() != ShareType.LOAN ||
                bookShare.getStatus() != BookShareStatus.ACTIVE) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Bu kitap şu anda ödünç alınamaz."
            );
        }

        var existingRequest =
                borrowingRepository.findFirstByBookShare_IdAndBorrower_IdAndStatusIn(
                        bookShareId,
                        currentUser.getId(),
                        requestStatuses
                );

        if (existingRequest.isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Bu kitap için zaten aktif bir isteğiniz bulunuyor."
            );
        }

        Borrowing borrowing = Borrowing.builder()
                .bookShare(bookShare)
                .borrower(currentUser)
                .status(BorrowingStatus.PENDING)
                .build();

        Borrowing savedBorrowing = borrowingRepository.save(borrowing);

        return borrowingMapper.toResponseDto(savedBorrowing);
    }

    public boolean hasActiveRequest(Long bookShareId, User currentUser) {

        if (currentUser == null || currentUser.getId() == null) {
            return false;
        }

        return borrowingRepository
                .existsByBookShare_IdAndBorrower_IdAndStatusIn(
                        bookShareId,
                        currentUser.getId(),
                        requestStatuses
                );
    }

    public Set<Long> getActiveRequestShareIds(User currentUser) {

        if (currentUser == null || currentUser.getId() == null) {
            return Set.of();
        }

        return borrowingRepository
                .findAllByBorrower_IdAndStatusIn(
                        currentUser.getId(),
                        requestStatuses
                )
                .stream()
                .map(borrowing -> borrowing.getBookShare().getId())
                .collect(Collectors.toSet());
    }

    public List<BorrowingResponseDto> getMyRequests(User currentUser) {

        if (currentUser == null || currentUser.getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "İsteklerinizi görüntülemek için giriş yapmalısınız."
            );
        }

        return borrowingRepository
                .findAllByBorrower_IdOrderByRequestedAtDesc(currentUser.getId())
                .stream()
                .map(borrowingMapper::toResponseDto)
                .toList();
    }

    @Transactional
    public BorrowingResponseDto acceptRequest(
            Long borrowingId,
            User currentUser) {

        Borrowing borrowing = findOwnerRequest(borrowingId, currentUser);

        if (borrowing.getStatus() != BorrowingStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Bu istek artık beklemede değil."
            );
        }

        BookShare bookShare = borrowing.getBookShare();

        if (bookShare.getStatus() != BookShareStatus.ACTIVE) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Bu kitap başka bir işlem nedeniyle kullanılamıyor."
            );
        }

        boolean anotherBorrowingExists =
                borrowingRepository.existsByBookShare_IdAndStatusIn(
                        bookShare.getId(),
                        activeStatuses
                );

        if (anotherBorrowingExists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Bu kitap başka bir kullanıcıya zaten verilmiş."
            );
        }

        borrowing.setStatus(BorrowingStatus.ACCEPTED);
        borrowing.setAcceptedAt(LocalDateTime.now());

        bookShare.setStatus(BookShareStatus.RESERVED);

        borrowingRepository.save(borrowing);
        bookShareRepository.save(bookShare);

        return borrowingMapper.toResponseDto(borrowing);
    }

    @Transactional
    public BorrowingResponseDto rejectRequest(
            Long borrowingId,
            User currentUser) {

        Borrowing borrowing = findOwnerRequest(borrowingId, currentUser);

        if (borrowing.getStatus() != BorrowingStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Bu istek artık beklemede değil."
            );
        }

        borrowing.setStatus(BorrowingStatus.REJECTED);

        return borrowingMapper.toResponseDto(
                borrowingRepository.save(borrowing)
        );
    }

    public List<BorrowingResponseDto> getIncomingRequests(User currentUser) {

        if (currentUser == null || currentUser.getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Bu sayfayı görüntülemek için giriş yapmalısınız."
            );
        }

        return borrowingRepository
                .findAllByBookShare_User_IdAndStatusInOrderByRequestedAtDesc(
                        currentUser.getId(),
                        incomingStatuses
                )
                .stream()
                .map(borrowingMapper::toResponseDto)
                .toList();
    }

    private Borrowing findOwnerRequest(Long borrowingId, User currentUser) {

        if (currentUser == null || currentUser.getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "İşlem yapmak için giriş yapmalısınız."
            );
        }

        return borrowingRepository
                .findByIdAndBookShare_User_Id(
                        borrowingId,
                        currentUser.getId()
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "İstek bulunamadı veya bu işlem için yetkiniz yok."
                ));
    }
}
