
package com.shareshelf.controller;

import com.shareshelf.entity.User;
import com.shareshelf.service.ReadingListMoveService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reading-lists")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class ReadingListMoveController {

    private final ReadingListMoveService readingListMoveService;

    @PostMapping("/{sourceListId}/books/{bookId}/move/{targetListId}")
    public ResponseEntity<Void> moveBook(
            @PathVariable Long sourceListId,
            @PathVariable Long bookId,
            @PathVariable Long targetListId,
            @AuthenticationPrincipal User currentUser) {

        readingListMoveService.moveBook(
                sourceListId,
                targetListId,
                bookId,
                currentUser
        );

        return ResponseEntity.noContent().build();
    }
}
