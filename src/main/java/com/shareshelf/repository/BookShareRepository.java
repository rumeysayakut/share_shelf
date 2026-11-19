
package com.shareshelf.repository;

import com.shareshelf.entity.BookShare;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookShareRepository extends JpaRepository<BookShare, Long> {

    Optional<BookShare> findByIdAndUser_Id(Long shareId, Long userId);
}
