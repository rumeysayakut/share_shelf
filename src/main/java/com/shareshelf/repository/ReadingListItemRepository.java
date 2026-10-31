package com.shareshelf.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shareshelf.entity.ReadingListItem;

public interface ReadingListItemRepository extends JpaRepository<ReadingListItem, Long> {

    List<ReadingListItem> findAllByReadingListId(Long readingListId);

    Optional<ReadingListItem> findByReadingListIdAndBookId(Long readingListId, Long bookId);

    boolean existsByReadingListIdAndBookId(Long readingListId, Long bookId);

    void deleteAllByReadingListId(Long readingListId);
}