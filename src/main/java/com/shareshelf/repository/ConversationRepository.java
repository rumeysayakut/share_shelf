package com.shareshelf.repository;

import com.shareshelf.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findByBorrowing_Id(Long borrowingId);

    Optional<Conversation> findByIdAndUserOne_IdOrIdAndUserTwo_Id(
            Long firstConversationId,
            Long firstUserId,
            Long secondConversationId,
            Long secondUserId
    );
}