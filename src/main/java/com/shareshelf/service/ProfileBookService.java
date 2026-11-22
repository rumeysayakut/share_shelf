
package com.shareshelf.service;

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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileBookService {

    private final BookRepository bookRepository;
    private final BookShareRepository bookShareRepository;
    private final BorrowingRepository borrowingRepository;
    private final BookShareMapper bookShareMapper;

    private static final List<BorrowingStatus> BLOCKING_STATUSES = List.of(
            BorrowingStatus.PENDING,
            BorrowingStatus.ACCEPTED,
            BorrowingStatus.HANDOVER_PENDING,
            BorrowingStatus.BORROWED,
            BorrowingStatus.RETURN_PENDING,
            BorrowingStatus.OVERDUE,
            BorrowingStatus.DISPUTED
    );

    @Transactional(readOnly = true)
    public List<BookShareResponseDto> getMySharedBooks(User currentUser) {
        return bookShareRepository.findAllByUser_Id(currentUser.getId())
                .stream()
                .filter(share -> share.getShareType() == ShareType.LOAN)
                .filter(share -> share.getBook().getDeletedAt() == null)
                .map(bookShareMapper::toResponseDto)
                .toList();
    }

    @Transactional
    public void deleteMySharedBook(Long bookId, User currentUser) {
        Book book = bookRepository
                .findByIdAndCreatedBy_IdAndDeletedAtIsNull(
                        bookId,
                        currentUser.getId()
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Kitap bulunamadı veya bu kitabı silme yetkiniz yok."
                ));

        List<BookShare> myShares =
                bookShareRepository.findAllByUser_IdAndBook_Id(
                        currentUser.getId(),
                        bookId
                );

        if (myShares.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Bu kitap paylaşım listenizde bulunamadı."
            );
        }

        boolean hasActiveBorrowing = myShares.stream()
                .anyMatch(share -> borrowingRepository
                        .existsByBookShare_IdAndStatusIn(
                                share.getId(),
                                BLOCKING_STATUSES
                        ));

        if (hasActiveBorrowing) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Kitap için devam eden bir ödünç işlemi var. İşlem tamamlanmadan kitap silinemez."
            );
        }

        book.setDeletedAt(LocalDateTime.now());

        myShares.forEach(share ->
                share.setStatus(BookShareStatus.CLOSED)
        );

        bookRepository.save(book);
        bookShareRepository.saveAll(myShares);
    }
}
