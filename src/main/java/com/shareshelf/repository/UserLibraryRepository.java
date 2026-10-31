
package com.shareshelf.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shareshelf.entity.UserLibrary;

public interface UserLibraryRepository extends JpaRepository<UserLibrary, Long> {

    List<UserLibrary> findAllByUserId(Long userId);

    Optional<UserLibrary> findByUserIdAndBookId(Long userId, Long bookId);

    boolean existsByUserIdAndBookId(Long userId, Long bookId);
}
