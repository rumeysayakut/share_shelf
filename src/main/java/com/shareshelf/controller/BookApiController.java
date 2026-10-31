
package com.shareshelf.controller;

import com.shareshelf.dto.request.BookRequestDto;
import com.shareshelf.dto.request.BookSummaryRequestDto;
import com.shareshelf.dto.response.BookResponseDto;
import com.shareshelf.dto.response.BookSummaryResponseDto;
import com.shareshelf.entity.User;
import com.shareshelf.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookApiController {

    private final BookService bookService;

    @PostMapping("/summary-preview")
    public BookSummaryResponseDto previewSummary(
            @Valid @RequestBody BookSummaryRequestDto request) {
        return bookService.previewSummary(request);
    }

    @PostMapping
    public BookResponseDto createBook(
            @Valid @RequestBody BookRequestDto request,
            @AuthenticationPrincipal User currentUser) {
        return bookService.createBook(request, currentUser);
    }
}
