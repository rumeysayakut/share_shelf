
package com.shareshelf.repository;

import com.shareshelf.entity.BookShare;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookShareRepository extends JpaRepository<BookShare, Long> {
}
