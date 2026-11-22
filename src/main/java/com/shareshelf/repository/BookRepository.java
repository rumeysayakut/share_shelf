
package com.shareshelf.repository;

import com.shareshelf.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

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

    boolean existsByIsbnAndDeletedAtIsNull(String isbn);

    boolean existsByTitleIgnoreCaseAndAuthorIgnoreCaseAndDeletedAtIsNull(
            String title,
            String author
    );

    boolean existsByTitleIgnoreCaseAndAuthorIgnoreCaseAndIdNotAndDeletedAtIsNull(
            String title,
            String author,
            Long id
    );

    boolean existsByIsbnAndIdNotAndDeletedAtIsNull(String isbn, Long id);

    Optional<Book> findByIdAndCreatedBy_IdAndDeletedAtIsNull(
            Long id,
            Long userId
    );
}
