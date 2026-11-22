
package com.shareshelf.repository;

import com.shareshelf.entity.Borrowing;
import com.shareshelf.entity.BorrowingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {

    boolean existsByBookShare_IdAndBorrower_IdAndStatusIn(
            Long bookShareId,
            Long borrowerId,
            Collection<BorrowingStatus> statuses
    );

    boolean existsByBookShare_IdAndStatusIn(
            Long bookShareId,
            Collection<BorrowingStatus> statuses
    );

    Optional<Borrowing> findFirstByBookShare_IdAndBorrower_IdAndStatusIn(
            Long bookShareId,
            Long borrowerId,
            Collection<BorrowingStatus> statuses
    );

    Optional<Borrowing> findByIdAndBookShare_User_Id(
            Long borrowingId,
            Long ownerId
    );

    Optional<Borrowing> findFirstByBookShare_IdOrderByRequestedAtDesc(
            Long bookShareId
    );

    List<Borrowing> findAllByBookShare_User_IdAndStatusOrderByRequestedAtDesc(
            Long ownerId,
            BorrowingStatus status
    );

    List<Borrowing> findAllByBookShare_User_IdAndStatusInOrderByRequestedAtDesc(
            Long ownerId,
            Collection<BorrowingStatus> statuses
    );

    List<Borrowing> findAllByBorrower_IdOrderByRequestedAtDesc(
            Long borrowerId
    );

    List<Borrowing> findAllByBorrower_IdAndStatusIn(
            Long borrowerId,
            Collection<BorrowingStatus> statuses
    );
}
