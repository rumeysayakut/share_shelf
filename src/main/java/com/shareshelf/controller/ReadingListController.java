
package com.shareshelf.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shareshelf.dto.request.ReadingListItemRequestDto;
import com.shareshelf.dto.request.ReadingListRequestDto;
import com.shareshelf.dto.response.ReadingListItemResponseDto;
import com.shareshelf.dto.response.ReadingListResponseDto;
import com.shareshelf.entity.User;
import com.shareshelf.service.ReadingListService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reading-lists")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class ReadingListController {

    private final ReadingListService readingListService;

    @PostMapping
    public ResponseEntity<ReadingListResponseDto> createList(
            @Valid @RequestBody ReadingListRequestDto request,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(readingListService.createList(request, currentUser));
    }

    @GetMapping
    public ResponseEntity<List<ReadingListResponseDto>> getMyLists(
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(readingListService.getMyLists(currentUser));
    }

    @GetMapping("/{listId}")
    public ResponseEntity<ReadingListResponseDto> getListById(
            @PathVariable Long listId,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(readingListService.getListById(listId, currentUser));
    }

    @PutMapping("/{listId}")
    public ResponseEntity<ReadingListResponseDto> updateList(
            @PathVariable Long listId,
            @Valid @RequestBody ReadingListRequestDto request,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(
                readingListService.updateList(listId, request, currentUser));
    }

    @DeleteMapping("/{listId}")
    public ResponseEntity<Void> deleteList(
            @PathVariable Long listId,
            @AuthenticationPrincipal User currentUser) {

        readingListService.deleteList(listId, currentUser);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{listId}/books")
    public ResponseEntity<ReadingListItemResponseDto> addBookToList(
            @PathVariable Long listId,
            @Valid @RequestBody ReadingListItemRequestDto request,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(readingListService.addBookToList(listId, request, currentUser));
    }

    @GetMapping("/{listId}/books")
    public ResponseEntity<List<ReadingListItemResponseDto>> getListBooks(
            @PathVariable Long listId,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(readingListService.getListBooks(listId, currentUser));
    }

    @DeleteMapping("/{listId}/books/{bookId}")
    public ResponseEntity<Void> removeBookFromList(
            @PathVariable Long listId,
            @PathVariable Long bookId,
            @AuthenticationPrincipal User currentUser) {

        readingListService.removeBookFromList(listId, bookId, currentUser);
        return ResponseEntity.noContent().build();
    }
}
