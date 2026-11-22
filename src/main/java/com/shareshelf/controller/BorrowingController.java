
package com.shareshelf.controller;

import com.shareshelf.entity.User;
import com.shareshelf.service.BorrowingService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/borrowings")
public class BorrowingController {

    private final BorrowingService borrowingService;

    public BorrowingController(BorrowingService borrowingService) {
        this.borrowingService = borrowingService;
    }

    @PostMapping("/shares/{shareId}/request")
    public String createRequest(
            @PathVariable Long shareId,
            @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes) {

        try {
            borrowingService.createRequest(shareId, currentUser);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Ödünç alma isteğiniz kitap sahibine gönderildi."
            );
        } catch (ResponseStatusException exception) {
            if (exception.getStatusCode() == HttpStatus.CONFLICT) {
                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        exception.getReason()
                );
            } else {
                throw exception;
            }
        }

        return "redirect:/shares";
    }

    @GetMapping({"", "/requests"})
    public String borrowingRequests(
            @RequestParam(defaultValue = "incoming") String view,
            @AuthenticationPrincipal User currentUser,
            Model model) {

        boolean sentView = "sent".equals(view);

        if (sentView) {
            model.addAttribute(
                    "requests",
                    borrowingService.getMyRequests(currentUser)
            );
        } else {
            model.addAttribute(
                    "requests",
                    borrowingService.getIncomingRequests(currentUser)
            );
        }

        model.addAttribute("view", sentView ? "sent" : "incoming");
        model.addAttribute("isAuthenticated", true);

        return "borrowings/index";
    }

    @PostMapping("/{borrowingId}/accept")
    public String acceptRequest(
            @PathVariable Long borrowingId,
            @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes) {

        borrowingService.acceptRequest(borrowingId, currentUser);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "İstek kabul edildi. Kitap sahibiyle artık sohbet edebilirsiniz."
        );

        return "redirect:/borrowings";
    }

    @PostMapping("/{borrowingId}/reject")
    public String rejectRequest(
            @PathVariable Long borrowingId,
            @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes) {

        borrowingService.rejectRequest(borrowingId, currentUser);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Ödünç alma isteği reddedildi."
        );

        return "redirect:/borrowings";
    }

    @PostMapping("/{borrowingId}/confirm-handover")
    public String confirmHandover(
            @PathVariable Long borrowingId,
            @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes) {

        var result = borrowingService.confirmHandover(borrowingId, currentUser);

        if (result.getStatus().name().equals("BORROWED")) {
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Teslimat iki tarafça da onaylandı. Kitap artık ödünç verilmiş durumda."
            );
        } else {
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Onayınız kaydedildi. Diğer tarafın da teslimatı onaylaması bekleniyor."
            );
        }

        return "redirect:/conversations/borrowing/" + borrowingId;
    }

    @PostMapping("/{borrowingId}/request-return")
    public String requestReturn(
            @PathVariable Long borrowingId,
            @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes) {

        borrowingService.requestReturn(borrowingId, currentUser);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "İade talebiniz kitap sahibine iletildi. Kitabı teslim ettikten sonra sahibinin onayını bekleyin."
        );

        return "redirect:/conversations/borrowing/" + borrowingId;
    }

    @PostMapping("/{borrowingId}/confirm-return")
    public String confirmReturn(
            @PathVariable Long borrowingId,
            @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes) {

        borrowingService.confirmReturn(borrowingId, currentUser);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Kitabın iadesini onayladınız. Paylaşımınız yeniden aktif hâle geldi."
        );

        return "redirect:/conversations/borrowing/" + borrowingId;
    }

    @PostMapping("/{borrowingId}/cancel")
    public String cancelAcceptedRequest(
            @PathVariable Long borrowingId,
            @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes) {

        borrowingService.cancelAcceptedRequest(borrowingId, currentUser);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Paylaşım görüşmesi iptal edildi. Kitap yeniden paylaşımda."
        );

        return "redirect:/borrowings";
    }

    @PostMapping("/{borrowingId}/hide")
    public String hideRequest(
            @PathVariable Long borrowingId,
            @RequestParam(defaultValue = "incoming") String view,
            @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes) {

        borrowingService.hideRequest(borrowingId, currentUser);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "İstek yalnızca senin listenden kaldırıldı."
        );

        return "redirect:/borrowings?view=" +
                ("sent".equals(view) ? "sent" : "incoming");
    }
}
