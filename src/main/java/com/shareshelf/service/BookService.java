
package com.shareshelf.service;

import com.shareshelf.dto.request.BookRequestDto;
import com.shareshelf.dto.request.BookSummaryRequestDto;
import com.shareshelf.dto.response.BookResponseDto;
import com.shareshelf.dto.response.BookSummaryResponseDto;
import com.shareshelf.entity.Book;
import com.shareshelf.entity.User;
import com.shareshelf.entity.UserRole;
import com.shareshelf.mapper.BookMapper;
import com.shareshelf.repository.BookRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Validated
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final GeminiService geminiService;

    public BookService(
            BookRepository bookRepository,
            BookMapper bookMapper,
            GeminiService geminiService) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
        this.geminiService = geminiService;
    }

    public List<BookResponseDto> getAllBooks() {
        return bookRepository.findAll(
                Sort.by(Sort.Direction.DESC, "createdAt")
        ).stream()
                .map(bookMapper::toResponseDto)
                .toList();
    }

    public BookResponseDto getBookById(Long id) {
        return bookMapper.toResponseDto(findBookById(id));
    }

    public BookResponseDto getBookForEdit(Long id, User currentUser) {
        Book book = findBookById(id);
        checkPermission(book, currentUser);
        return bookMapper.toResponseDto(book);
    }

    public BookSummaryResponseDto previewSummary(
            @Valid BookSummaryRequestDto request) {

        String title = request.getTitle().trim();
        String author = request.getAuthor().trim();

        String description =
                geminiService.generateBookSummary(title, author);

        return new BookSummaryResponseDto(description);
    }

    @Transactional
    public BookResponseDto createBook(
            @Valid BookRequestDto request,
            User currentUser) {

        checkAuthenticatedUser(currentUser);

        String title = request.getTitle().trim();
        String author = request.getAuthor().trim();
        String isbn = normalizeIsbn(request.getIsbn());

        checkDuplicateBook(title, author, isbn, null);

        Book book = bookMapper.toEntity(request);
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setDescription(request.getDescription().trim());
        book.setCreatedBy(currentUser);

        Book savedBook = bookRepository.save(book);

        return bookMapper.toResponseDto(savedBook);
    }

    @Transactional
    public BookResponseDto updateBook(
            Long id,
            @Valid BookRequestDto request,
            User currentUser) {

        Book book = findBookById(id);

        checkPermission(book, currentUser);

        String title = request.getTitle().trim();
        String author = request.getAuthor().trim();
        String isbn = normalizeIsbn(request.getIsbn());

        checkDuplicateBook(title, author, isbn, id);

        bookMapper.updateEntity(request, book);

        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setDescription(request.getDescription().trim());

        return bookMapper.toResponseDto(bookRepository.save(book));
    }

    public BookSummaryResponseDto regenerateSummary(
            Long id,
            User currentUser) {

        Book book = findBookById(id);
        checkPermission(book, currentUser);

        String description = geminiService.generateBookSummary(
                book.getTitle(),
                book.getAuthor()
        );

        return new BookSummaryResponseDto(description);
    }

    private Book findBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Kitap bulunamadı."
                ));
    }

    private void checkAuthenticatedUser(User currentUser) {
        if (currentUser == null || currentUser.getId() == null) {
            throw new AccessDeniedException(
                    "Bu işlem için giriş yapmalısınız."
            );
        }
    }

    private void checkPermission(Book book, User currentUser) {
        checkAuthenticatedUser(currentUser);

        boolean isAdmin =
                currentUser.getRole() == UserRole.ADMIN;

        boolean isCreator =
                book.getCreatedBy() != null
                && book.getCreatedBy().getId()
                        .equals(currentUser.getId());

        if (!isAdmin && !isCreator) {
            throw new AccessDeniedException(
                    "Bu kitabı düzenleme yetkiniz yok."
            );
        }
    }

    private void checkDuplicateBook(
            String title,
            String author,
            String isbn,
            Long excludedBookId) {

        boolean duplicateTitleAndAuthor;

        if (excludedBookId == null) {
            duplicateTitleAndAuthor =
                    bookRepository.existsByTitleIgnoreCaseAndAuthorIgnoreCase(
                            title, author
                    );
        } else {
            duplicateTitleAndAuthor =
                    bookRepository
                            .existsByTitleIgnoreCaseAndAuthorIgnoreCaseAndIdNot(
                                    title, author, excludedBookId
                            );
        }

        if (duplicateTitleAndAuthor) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Bu kitap katalogda zaten mevcut."
            );
        }

        if (isbn != null) {
            boolean duplicateIsbn;

            if (excludedBookId == null) {
                duplicateIsbn = bookRepository.existsByIsbn(isbn);
            } else {
                duplicateIsbn =
                        bookRepository.existsByIsbnAndIdNot(
                                isbn, excludedBookId
                        );
            }

            if (duplicateIsbn) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Bu ISBN numarasına sahip kitap zaten mevcut."
                );
            }
        }
    }

    private String normalizeIsbn(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            return null;
        }

        return isbn.replaceAll("[\\s-]", "").toUpperCase();
    }
}
