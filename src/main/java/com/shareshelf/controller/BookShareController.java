package com.shareshelf.controller;

import com.shareshelf.dto.request.BookRequestDto;
import com.shareshelf.dto.request.BookShareRequestDto;
import com.shareshelf.dto.request.UserLibraryRequestDto;
import com.shareshelf.dto.response.BookResponseDto;
import com.shareshelf.entity.ReadingStatus;
import com.shareshelf.entity.User;
import com.shareshelf.entity.UserLibrary;
import com.shareshelf.repository.UserLibraryRepository;
import com.shareshelf.service.BookService;
import com.shareshelf.service.BookShareService;
import com.shareshelf.service.UserLibraryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/shares")
public class BookShareController {

    private final BookShareService bookShareService;
    private final BookService bookService;
    private final UserLibraryService userLibraryService;
    private final UserLibraryRepository userLibraryRepository;

    public BookShareController(
            BookShareService bookShareService,
            BookService bookService,
            UserLibraryService userLibraryService,
            UserLibraryRepository userLibraryRepository) {
        this.bookShareService = bookShareService;
        this.bookService = bookService;
        this.userLibraryService = userLibraryService;
        this.userLibraryRepository = userLibraryRepository;
    }

    @GetMapping
    public String getActiveShares(
            @RequestParam(defaultValue = "discover") String view,
            Model model,
            @AuthenticationPrincipal User currentUser) {

        boolean authenticated = currentUser != null;
        boolean mySharesView = "mine".equals(view) && authenticated;

        model.addAttribute("shares",
                mySharesView
                        ? bookShareService.getActiveSharesByUserId(currentUser.getId())
                        : bookShareService.getActiveShares());

        model.addAttribute("isAuthenticated", authenticated);
        model.addAttribute(
                "currentUserId",
                authenticated ? currentUser.getId() : null
        );
        model.addAttribute("activeView", mySharesView ? "mine" : "discover");

        return "shares/list";
    }

    @GetMapping("/new")
    public String showCreateShareForm(
            Model model,
            @AuthenticationPrincipal User currentUser) {
        List<UserLibrary> userBooks =
                userLibraryRepository.findAllByUserId(currentUser.getId());
        model.addAttribute("userBooks", userBooks);
        model.addAttribute("shareRequest", new BookShareRequestDto());
        model.addAttribute("isAuthenticated", true);
        return "shares/create";
    }

    @PostMapping
    public String createShare(
            @ModelAttribute("shareRequest") BookShareRequestDto request,
            @AuthenticationPrincipal User currentUser) {
        if (request.getBookId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Lütfen bir kitap seçin."
            );
        }
        boolean ownsBook = userLibraryRepository.existsByUserIdAndBookId(
                currentUser.getId(),
                request.getBookId()
        );
        if (!ownsBook) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Yalnızca kendi kitaplığınızdaki kitapları paylaşabilirsiniz."
            );
        }
        bookShareService.createShare(request, currentUser);
        return "redirect:/shares";
    }

    @GetMapping("/new-book")
    public String showNewBookForm(Model model) {
        model.addAttribute("bookRequest", new BookRequestDto());
        model.addAttribute("isAuthenticated", true);
        return "shares/create-book";
    }

    @PostMapping("/new-book")
    public String createNewBookForShare(
            @Valid @ModelAttribute("bookRequest") BookRequestDto bookRequest,
            BindingResult bindingResult,
            @RequestParam(required = false) String conditionDescription,
            @RequestParam(required = false) String shareDescription,
            @RequestParam(required = false) MultipartFile coverImage,
            @AuthenticationPrincipal User currentUser,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isAuthenticated", true);
            return "shares/create-book";
        }
        BookResponseDto savedBook =
                bookService.createBook(bookRequest, currentUser, coverImage);
        UserLibraryRequestDto libraryRequest =
                new UserLibraryRequestDto();
        libraryRequest.setBookId(savedBook.getId());
        libraryRequest.setReadingStatus(ReadingStatus.WANT_TO_READ);
        userLibraryService.addBook(libraryRequest, currentUser);
        BookShareRequestDto shareRequest =
                new BookShareRequestDto();
        shareRequest.setBookId(savedBook.getId());
        shareRequest.setConditionDescription(conditionDescription);
        shareRequest.setDescription(shareDescription);
        bookShareService.createShare(shareRequest, currentUser);
        return "redirect:/shares";
    }

    @PostMapping("/{shareId}/close")
    public String closeShare(
            @PathVariable Long shareId,
            @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes) {
        bookShareService.closeShare(shareId, currentUser);
        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Kitap paylaşımın kaldırıldı."
        );
        return "redirect:/shares?view=mine";
    }
}