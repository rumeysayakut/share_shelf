package com.shareshelf.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shareshelf.entity.ReadingList;

public interface ReadingListRepository extends JpaRepository<ReadingList, Long> {

    List<ReadingList> findAllByUserId(Long userId);

    Optional<ReadingList> findByIdAndUserId(Long id, Long userId);
}