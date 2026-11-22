
package com.shareshelf.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.shareshelf.dto.request.BookShareRequestDto;
import com.shareshelf.dto.response.BookShareResponseDto;
import com.shareshelf.entity.Book;
import com.shareshelf.entity.BookShare;
import com.shareshelf.entity.BookShareStatus;
import com.shareshelf.entity.BorrowingStatus;
import com.shareshelf.entity.ShareType;
import com.shareshelf.entity.User;
import com.shareshelf.mapper.BookShareMapper;
import com.shareshelf.repository.BookRepository;
import com.shareshelf.repository.BookShareRepository;
import com.shareshelf.repository.BorrowingRepository;

import jakarta.transaction.Transactional;

@Service
public class BookShareService {

    private final BookShareRepository bookShareRepository;
    private final BookRepository bookRepository;
    private final BorrowingRepository borrowingRepository;
    private final BookShareMapper bookShareMapper;

    public BookShareService(
            BookShareRepository bookShareRepository,
            BookRepository bookRepository,
            BorrowingRepository borrowingRepository,
            BookShareMapper bookShareMapper) {
        this.bookShareRepository = bookShareRepository;
        this.bookRepository = bookRepository;
        this.borrowingRepository = borrowingRepository;
        this.bookShareMapper = bookShareMapper;
    }

    @Transactional
    public BookShareResponseDto createShare(
            BookShareRequestDto request,
            User currentUser) {

        if (currentUser == null || currentUser.getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Kitap paylaşmak için giriş yapmalısınız."
            );
        }

        if (request.getBookId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Kitap seçmelisiniz."
            );
        }

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Kitap bulunamadı."
                ));

        BookShare bookShare = bookShareMapper.toEntity(request);

        bookShare.setBook(book);
        bookShare.setUser(currentUser);
        bookShare.setShareType(ShareType.LOAN);
        bookShare.setStatus(BookShareStatus.ACTIVE);
        bookShare.setDepositAmount(null);
        bookShare.setSalePrice(null);

        return bookShareMapper.toResponseDto(
                bookShareRepository.save(bookShare)
        );
    }

    public List<BookShareResponseDto> getActiveShares() {
        return bookShareRepository.findAll()
                .stream()
                .filter(share -> share.getStatus() == BookShareStatus.ACTIVE)
                .filter(share -> share.getShareType() == ShareType.LOAN)
                .map(bookShareMapper::toResponseDto)
                .toList();
    }

    @Transactional
    public List<BookShareResponseDto> getActiveSharesByUserId(Long userId) {
        return bookShareRepository.findAll()
                .stream()
                .filter(share ->
                        share.getStatus() == BookShareStatus.ACTIVE ||
                        share.getStatus() == BookShareStatus.RESERVED
                )
                .filter(share -> share.getShareType() == ShareType.LOAN)
                .filter(share -> share.getUser() != null)
                .filter(share -> share.getUser().getId().equals(userId))
                .map(this::toMyShareResponseDto)
                .toList();
    }

    private BookShareResponseDto toMyShareResponseDto(BookShare share) {
        BookShareResponseDto response = bookShareMapper.toResponseDto(share);

        borrowingRepository
                .findFirstByBookShare_IdOrderByRequestedAtDesc(share.getId())
                .ifPresent(borrowing -> {
                    if (isConversationAvailable(borrowing.getStatus())) {
                        response.setConversationBorrowingId(borrowing.getId());
                    }
                });

        return response;
    }

    private boolean isConversationAvailable(BorrowingStatus status) {
        return status == BorrowingStatus.ACCEPTED ||
                status == BorrowingStatus.HANDOVER_PENDING ||
                status == BorrowingStatus.BORROWED ||
                status == BorrowingStatus.RETURN_PENDING ||
                status == BorrowingStatus.RETURNED ||
                status == BorrowingStatus.OVERDUE ||
                status == BorrowingStatus.DISPUTED;
    }

    @Transactional
    public void closeShare(Long shareId, User currentUser) {
        if (currentUser == null || currentUser.getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Bu işlem için giriş yapmalısınız."
            );
        }

        BookShare bookShare = bookShareRepository
                .findByIdAndUser_Id(shareId, currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Paylaşım bulunamadı veya bu işlem için yetkiniz yok."
                ));

        if (bookShare.getStatus() != BookShareStatus.ACTIVE) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Bu paylaşım şu anda kaldırılamaz."
            );
        }

        bookShare.setStatus(BookShareStatus.CLOSED);
        bookShareRepository.save(bookShare);
    }
}
