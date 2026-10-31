
package com.shareshelf.controller;

import com.shareshelf.dto.request.BookRequestDto;
import com.shareshelf.dto.response.BookResponseDto;
import com.shareshelf.dto.response.BookSummaryResponseDto;
import com.shareshelf.entity.User;
import com.shareshelf.service.BookService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public String getAllBooks(
            Model model,
            @AuthenticationPrincipal User currentUser) {

        List<BookResponseDto> books = bookService.getAllBooks();
        model.addAttribute("books", books);
        model.addAttribute("isAuthenticated", currentUser != null);

        return "books/list";
    }

    @GetMapping("/{id}")
    public String getBookById(
            @PathVariable Long id,
            Model model,
            @AuthenticationPrincipal User currentUser) {
        model.addAttribute("book", bookService.getBookById(id));
        model.addAttribute("isAuthenticated", currentUser != null);
        return "books/detail";
    }

    @GetMapping("/new")
    public String showCreateBookForm(Model model) {
        model.addAttribute("bookRequest", new BookRequestDto());
        return "books/create";
    }

    @PostMapping
    public String createBook(
            @Valid @ModelAttribute("bookRequest") BookRequestDto request,
            BindingResult bindingResult,
            @AuthenticationPrincipal User currentUser,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "books/create";
        }

        bookService.createBook(request, currentUser);
        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String showEditBookForm(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser,
            Model model) {

        BookResponseDto book =
                bookService.getBookForEdit(id, currentUser);

        model.addAttribute("book", book);
        model.addAttribute("bookRequest", toRequestDto(book));

        return "books/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateBook(
            @PathVariable Long id,
            @Valid @ModelAttribute("bookRequest") BookRequestDto request,
            BindingResult bindingResult,
            @AuthenticationPrincipal User currentUser,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("book",
                    bookService.getBookForEdit(id, currentUser));
            return "books/edit";
        }

        bookService.updateBook(id, request, currentUser);
        return "redirect:/books/" + id;
    }

    @PostMapping("/{id}/summary-preview")
    public String regenerateSummary(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser,
            Model model) {

        BookResponseDto book =
                bookService.getBookForEdit(id, currentUser);

        BookSummaryResponseDto summary =
                bookService.regenerateSummary(id, currentUser);

        BookRequestDto request = toRequestDto(book);
        request.setDescription(summary.getDescription());

        model.addAttribute("book", book);
        model.addAttribute("bookRequest", request);

        return "books/edit";
    }

    private BookRequestDto toRequestDto(BookResponseDto book) {
        BookRequestDto request = new BookRequestDto();
        request.setTitle(book.getTitle());
        request.setAuthor(book.getAuthor());
        request.setIsbn(book.getIsbn());
        request.setPublisher(book.getPublisher());
        request.setPublicationYear(book.getPublicationYear());
        request.setPageCount(book.getPageCount());
        request.setCoverImageUrl(book.getCoverImageUrl());
        request.setDescription(book.getDescription());
        return request;
    }
}
