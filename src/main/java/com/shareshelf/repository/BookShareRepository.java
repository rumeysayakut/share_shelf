
package com.shareshelf.repository;

import com.shareshelf.entity.BookShare;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookShareRepository extends JpaRepository<BookShare, Long> {

    Optional<BookShare> findByIdAndUser_Id(Long shareId, Long userId);

    List<BookShare> findAllByUser_Id(Long userId);

    List<BookShare> findAllByUser_IdAndBook_Id(Long userId, Long bookId);
}
