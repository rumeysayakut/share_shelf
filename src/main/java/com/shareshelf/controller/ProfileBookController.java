
package com.shareshelf.controller;

import com.shareshelf.dto.response.BookShareResponseDto;
import com.shareshelf.entity.User;
import com.shareshelf.service.ProfileBookService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profile/shared-books")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class ProfileBookController {

    private final ProfileBookService profileBookService;

    @GetMapping
    public ResponseEntity<List<BookShareResponseDto>> getMySharedBooks(
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(
                profileBookService.getMySharedBooks(currentUser)
        );
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteMySharedBook(
            @PathVariable Long bookId,
            @AuthenticationPrincipal User currentUser) {

        profileBookService.deleteMySharedBook(bookId, currentUser);

        return ResponseEntity.noContent().build();
    }
}
