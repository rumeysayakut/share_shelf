
package com.shareshelf.repository;

import com.shareshelf.entity.BookQuote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookQuoteRepository extends JpaRepository<BookQuote, Long> {
}
