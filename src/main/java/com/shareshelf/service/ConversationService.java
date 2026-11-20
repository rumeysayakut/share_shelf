
package com.shareshelf.service;

import com.shareshelf.entity.*;
import com.shareshelf.repository.BorrowingRepository;
import com.shareshelf.repository.ConversationRepository;
import com.shareshelf.repository.MessageRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final BorrowingRepository borrowingRepository;

    public ConversationService(
            ConversationRepository conversationRepository,
            MessageRepository messageRepository,
            BorrowingRepository borrowingRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.borrowingRepository = borrowingRepository;
    }

    @Transactional
    public Conversation getOrCreateConversation(Long borrowingId, User currentUser) {
        validateUser(currentUser);

        Borrowing borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Ödünç alma isteği bulunamadı."
                ));

        boolean isOwner = borrowing.getBookShare()
                .getUser().getId().equals(currentUser.getId());

        boolean isBorrower = borrowing.getBorrower()
                .getId().equals(currentUser.getId());

        if (!isOwner && !isBorrower) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Bu sohbete erişim yetkiniz yok."
            );
        }

        if (!isConversationAllowed(borrowing.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Bu istek için henüz sohbet açılamaz."
            );
        }

        return conversationRepository.findByBorrowing_Id(borrowingId)
                .orElseGet(() -> conversationRepository.save(
                        Conversation.builder()
                                .borrowing(borrowing)
                                .bookShare(borrowing.getBookShare())
                                .userOne(borrowing.getBookShare().getUser())
                                .userTwo(borrowing.getBorrower())
                                .build()
                ));
    }

    @Transactional
    public List<Conversation> getUserConversations(User currentUser) {
        validateUser(currentUser);
        return conversationRepository.findAllByParticipantId(currentUser.getId());
    }

    @Transactional
    public Conversation getAccessibleConversation(Long conversationId, User currentUser) {
        validateUser(currentUser);

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Sohbet bulunamadı."
                ));

        boolean isParticipant =
                conversation.getUserOne().getId().equals(currentUser.getId()) ||
                conversation.getUserTwo().getId().equals(currentUser.getId());

        if (!isParticipant) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Bu sohbete erişim yetkiniz yok."
            );
        }

        return conversation;
    }

    @Transactional
    public List<Message> getMessages(Long conversationId, User currentUser) {
        getAccessibleConversation(conversationId, currentUser);
        return messageRepository.findMessagesByConversationId(conversationId);
    }

    @Transactional
    public void sendMessage(Long conversationId, String content, User currentUser) {
        if (content == null || content.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Boş mesaj gönderemezsiniz."
            );
        }

        if (content.length() > 5000) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Mesaj en fazla 5000 karakter olabilir."
            );
        }

        Conversation conversation = getAccessibleConversation(conversationId, currentUser);

        Message message = Message.builder()
                .conversation(conversation)
                .sender(currentUser)
                .content(content.trim())
                .build();

        messageRepository.save(message);

        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);
    }

    private boolean isConversationAllowed(BorrowingStatus status) {
        return status == BorrowingStatus.ACCEPTED ||
                status == BorrowingStatus.BORROWED ||
                status == BorrowingStatus.RETURN_PENDING ||
                status == BorrowingStatus.RETURNED ||
                status == BorrowingStatus.OVERDUE ||
                status == BorrowingStatus.DISPUTED;
    }

    private void validateUser(User currentUser) {
        if (currentUser == null || currentUser.getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Bu işlem için giriş yapmalısınız."
            );
        }
    }
}
