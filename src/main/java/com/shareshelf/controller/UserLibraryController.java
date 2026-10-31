
package com.shareshelf.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shareshelf.dto.request.UserLibraryRequestDto;
import com.shareshelf.dto.response.UserLibraryResponseDto;
import com.shareshelf.entity.ReadingStatus;
import com.shareshelf.entity.User;
import com.shareshelf.service.UserLibraryService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user-library")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class UserLibraryController {

    private final UserLibraryService userLibraryService;

    @PostMapping
    public ResponseEntity<UserLibraryResponseDto> addBook(
            @RequestBody UserLibraryRequestDto request,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userLibraryService.addBook(request, currentUser));
    }

    @GetMapping
    public ResponseEntity<List<UserLibraryResponseDto>> getMyLibrary(
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(userLibraryService.getMyLibrary(currentUser));
    }

    @PatchMapping("/{bookId}/status")
    public ResponseEntity<UserLibraryResponseDto> updateReadingStatus(
            @PathVariable Long bookId,
            @RequestParam ReadingStatus readingStatus,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(
                userLibraryService.updateReadingStatus(bookId, readingStatus, currentUser));
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> removeBook(
            @PathVariable Long bookId,
            @AuthenticationPrincipal User currentUser) {

        userLibraryService.removeBook(bookId, currentUser);
        return ResponseEntity.noContent().build();
    }
}
