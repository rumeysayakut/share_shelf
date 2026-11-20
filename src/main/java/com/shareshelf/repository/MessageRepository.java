package com.shareshelf.repository;

import com.shareshelf.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("""
            SELECT m FROM Message m
            JOIN FETCH m.sender
            WHERE m.conversation.id = :conversationId
            ORDER BY m.sentAt ASC
            """)
    List<Message> findMessagesByConversationId(
            @Param("conversationId") Long conversationId
    );
}