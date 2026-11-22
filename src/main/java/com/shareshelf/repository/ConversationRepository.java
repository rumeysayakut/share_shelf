
package com.shareshelf.repository;

import com.shareshelf.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findByBorrowing_Id(Long borrowingId);

    @Query("""
            SELECT c FROM Conversation c
            JOIN FETCH c.bookShare bs
            JOIN FETCH bs.book
            JOIN FETCH c.userOne
            JOIN FETCH c.userTwo
            WHERE (c.userOne.id = :userId AND c.userOneDeletedAt IS NULL)
               OR (c.userTwo.id = :userId AND c.userTwoDeletedAt IS NULL)
            ORDER BY c.updatedAt DESC
            """)
    List<Conversation> findAllByParticipantId(@Param("userId") Long userId);
}
