
package com.shareshelf.repository;

import com.shareshelf.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn);

    boolean existsByTitleIgnoreCaseAndAuthorIgnoreCase(
            String title,
            String author
    );

    boolean existsByTitleIgnoreCaseAndAuthorIgnoreCaseAndIdNot(
            String title,
            String author,
            Long id
    );

    boolean existsByIsbnAndIdNot(String isbn, Long id);
}
