
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
            BorrowingStatus.HANDOVER_PENDING,
            BorrowingStatus.BORROWED,
            BorrowingStatus.RETURN_PENDING,
            BorrowingStatus.OVERDUE,
            BorrowingStatus.DISPUTED
    );

    private final List<BorrowingStatus> requestStatuses = List.of(
            BorrowingStatus.PENDING,
            BorrowingStatus.ACCEPTED,
            BorrowingStatus.HANDOVER_PENDING,
            BorrowingStatus.BORROWED,
            BorrowingStatus.RETURN_PENDING,
            BorrowingStatus.OVERDUE,
            BorrowingStatus.DISPUTED
    );

    private final List<BorrowingStatus> incomingStatuses = List.of(
            BorrowingStatus.PENDING,
            BorrowingStatus.ACCEPTED,
            BorrowingStatus.HANDOVER_PENDING,
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
    public BorrowingResponseDto createRequest(Long bookShareId, User currentUser) {
        validateUser(currentUser);

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

        return borrowingMapper.toResponseDto(
                borrowingRepository.save(borrowing)
        );
    }

    public boolean hasActiveRequest(Long bookShareId, User currentUser) {
        if (currentUser == null || currentUser.getId() == null) {
            return false;
        }

        return borrowingRepository.existsByBookShare_IdAndBorrower_IdAndStatusIn(
                bookShareId,
                currentUser.getId(),
                requestStatuses
        );
    }

    public Set<Long> getActiveRequestShareIds(User currentUser) {
        if (currentUser == null || currentUser.getId() == null) {
            return Set.of();
        }

        return borrowingRepository.findAllByBorrower_IdAndStatusIn(
                        currentUser.getId(),
                        requestStatuses
                )
                .stream()
                .map(borrowing -> borrowing.getBookShare().getId())
                .collect(Collectors.toSet());
    }

    public List<BorrowingResponseDto> getMyRequests(User currentUser) {
        validateUser(currentUser);

        return borrowingRepository
                .findVisibleRequestsByBorrowerId(currentUser.getId())
                .stream()
                .map(borrowingMapper::toResponseDto)
                .toList();
    }

    @Transactional
    public BorrowingResponseDto acceptRequest(Long borrowingId, User currentUser) {
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
    public BorrowingResponseDto rejectRequest(Long borrowingId, User currentUser) {
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

    @Transactional
    public BorrowingResponseDto confirmHandover(Long borrowingId, User currentUser) {
        validateUser(currentUser);

        Borrowing borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Ödünç alma isteği bulunamadı."
                ));

        boolean isOwner = borrowing.getBookShare().getUser()
                .getId().equals(currentUser.getId());

        boolean isBorrower = borrowing.getBorrower()
                .getId().equals(currentUser.getId());

        if (!isOwner && !isBorrower) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Bu işlem için yetkiniz yok."
            );
        }

        if (borrowing.getStatus() != BorrowingStatus.ACCEPTED &&
                borrowing.getStatus() != BorrowingStatus.HANDOVER_PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Bu işlem şu anda teslim onayı beklemiyor."
            );
        }

        if (isOwner) {
            if (borrowing.isOwnerHandoverConfirmed()) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Teslim ettiğinizi zaten onayladınız."
                );
            }

            borrowing.setOwnerHandoverConfirmed(true);
        }

        if (isBorrower) {
            if (borrowing.isBorrowerHandoverConfirmed()) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Teslim aldığınızı zaten onayladınız."
                );
            }

            borrowing.setBorrowerHandoverConfirmed(true);
        }

        if (borrowing.isOwnerHandoverConfirmed() &&
                borrowing.isBorrowerHandoverConfirmed()) {
            borrowing.setStatus(BorrowingStatus.BORROWED);
            borrowing.setBorrowedAt(LocalDateTime.now());
        } else {
            borrowing.setStatus(BorrowingStatus.HANDOVER_PENDING);
        }

        return borrowingMapper.toResponseDto(
                borrowingRepository.save(borrowing)
        );
    }

    @Transactional
    public BorrowingResponseDto requestReturn(Long borrowingId, User currentUser) {
        validateUser(currentUser);

        Borrowing borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Ödünç alma işlemi bulunamadı."
                ));

        if (!borrowing.getBorrower().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "İade talebini yalnızca kitabı ödünç alan kişi oluşturabilir."
            );
        }

        if (borrowing.getStatus() != BorrowingStatus.BORROWED &&
                borrowing.getStatus() != BorrowingStatus.OVERDUE) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Bu kitap için şu anda iade talebi oluşturulamaz."
            );
        }

        borrowing.setStatus(BorrowingStatus.RETURN_PENDING);
        borrowing.setReturnRequestedAt(LocalDateTime.now());

        return borrowingMapper.toResponseDto(
                borrowingRepository.save(borrowing)
        );
    }

    @Transactional
    public BorrowingResponseDto confirmReturn(Long borrowingId, User currentUser) {
        validateUser(currentUser);

        Borrowing borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Ödünç alma işlemi bulunamadı."
                ));

        if (!borrowing.getBookShare().getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Kitabın iadesini yalnızca kitap sahibi onaylayabilir."
            );
        }

        if (borrowing.getStatus() != BorrowingStatus.RETURN_PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Bu işlem şu anda iade onayı beklemiyor."
            );
        }

        borrowing.setStatus(BorrowingStatus.RETURNED);
        borrowing.setReturnedAt(LocalDateTime.now());

        BookShare bookShare = borrowing.getBookShare();
        bookShare.setStatus(BookShareStatus.ACTIVE);

        bookShareRepository.save(bookShare);

        return borrowingMapper.toResponseDto(
                borrowingRepository.save(borrowing)
        );
    }

    @Transactional
    public BorrowingResponseDto cancelAcceptedRequest(
            Long borrowingId,
            User currentUser) {
        validateUser(currentUser);

        Borrowing borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Ödünç alma isteği bulunamadı."
                ));

        boolean isOwner = borrowing.getBookShare().getUser()
                .getId().equals(currentUser.getId());

        boolean isBorrower = borrowing.getBorrower()
                .getId().equals(currentUser.getId());

        if (!isOwner && !isBorrower) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Bu işlem için yetkiniz yok."
            );
        }

        if (borrowing.getStatus() != BorrowingStatus.ACCEPTED &&
                borrowing.getStatus() != BorrowingStatus.HANDOVER_PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Bu aşamada paylaşım iptal edilemez."
            );
        }

        if (borrowing.isOwnerHandoverConfirmed() ||
                borrowing.isBorrowerHandoverConfirmed()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Teslimat onayı verildikten sonra paylaşım iptal edilemez. Önce kitap iade edilmelidir."
            );
        }

        borrowing.setStatus(BorrowingStatus.CANCELLED);

        BookShare bookShare = borrowing.getBookShare();
        bookShare.setStatus(BookShareStatus.ACTIVE);

        bookShareRepository.save(bookShare);

        return borrowingMapper.toResponseDto(
                borrowingRepository.save(borrowing)
        );
    }

    public List<BorrowingResponseDto> getIncomingRequests(User currentUser) {
        validateUser(currentUser);

        return borrowingRepository
                .findVisibleIncomingRequests(currentUser.getId(), incomingStatuses)
                .stream()
                .map(borrowingMapper::toResponseDto)
                .toList();
    }

    @Transactional
    public void hideRequest(Long borrowingId, User currentUser) {
        validateUser(currentUser);

        Borrowing borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Ödünç alma isteği bulunamadı."
                ));

        boolean isOwner = borrowing.getBookShare().getUser()
                .getId().equals(currentUser.getId());

        boolean isBorrower = borrowing.getBorrower()
                .getId().equals(currentUser.getId());

        if (!isOwner && !isBorrower) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Bu isteği gizleme yetkiniz yok."
            );
        }

        BorrowingStatus status = borrowing.getStatus();

        boolean canHide =
                status == BorrowingStatus.PENDING ||
                status == BorrowingStatus.REJECTED ||
                status == BorrowingStatus.CANCELLED ||
                status == BorrowingStatus.RETURNED;

        if (!canHide) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Devam eden ödünç işlemleri gizlenemez."
            );
        }

        if (isOwner) {
            borrowing.setOwnerDeletedAt(LocalDateTime.now());
        } else {
            borrowing.setBorrowerDeletedAt(LocalDateTime.now());
        }

        borrowingRepository.save(borrowing);
    }

    private Borrowing findOwnerRequest(Long borrowingId, User currentUser) {
        validateUser(currentUser);

        return borrowingRepository.findByIdAndBookShare_User_Id(
                        borrowingId,
                        currentUser.getId()
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "İstek bulunamadı veya bu işlem için yetkiniz yok."
                ));
    }

    private void validateUser(User currentUser) {
        if (currentUser == null || currentUser.getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Bu işlem için giriş yapmalısınız."
            );
        }
    }
}
