
package com.shareshelf.controller;

import com.shareshelf.entity.Conversation;
import com.shareshelf.entity.User;
import com.shareshelf.service.ConversationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping
    public String listConversations(
            @AuthenticationPrincipal User currentUser,
            Model model) {

        model.addAttribute(
                "conversations",
                conversationService.getUserConversations(currentUser)
        );

        model.addAttribute("currentUserId", currentUser.getId());
        model.addAttribute("isAuthenticated", true);

        return "conversations/index";
    }

    @GetMapping("/borrowing/{borrowingId}")
    public String showConversation(
            @PathVariable Long borrowingId,
            @AuthenticationPrincipal User currentUser,
            Model model) {

        Conversation conversation = conversationService.getOrCreateConversation(
                borrowingId,
                currentUser
        );

        model.addAttribute("conversation", conversation);
        model.addAttribute(
                "messages",
                conversationService.getMessages(conversation.getId(), currentUser)
        );
        model.addAttribute("currentUserId", currentUser.getId());
        model.addAttribute("isAuthenticated", true);

        return "conversations/detail";
    }

    @PostMapping("/{conversationId}/delete")
    public String deleteConversation(
            @PathVariable Long conversationId,
            @AuthenticationPrincipal User currentUser) {

        conversationService.deleteConversation(conversationId, currentUser);

        return "redirect:/conversations";
    }

    @PostMapping("/{conversationId}/messages")
    public String sendMessage(
            @PathVariable Long conversationId,
            @RequestParam String content,
            @AuthenticationPrincipal User currentUser) {

        conversationService.sendMessage(
                conversationId,
                content,
                currentUser
        );

        Conversation conversation = conversationService.getAccessibleConversation(
                conversationId,
                currentUser
        );

        return "redirect:/conversations/borrowing/" +
                conversation.getBorrowing().getId();
    }
}
