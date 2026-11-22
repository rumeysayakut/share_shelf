
package com.shareshelf.repository;

import com.shareshelf.entity.Borrowing;
import com.shareshelf.entity.BorrowingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("""
            SELECT b FROM Borrowing b
            WHERE b.borrower.id = :borrowerId
            AND b.borrowerDeletedAt IS NULL
            ORDER BY b.requestedAt DESC
            """)
    List<Borrowing> findVisibleRequestsByBorrowerId(
            @Param("borrowerId") Long borrowerId
    );

    @Query("""
            SELECT b FROM Borrowing b
            WHERE b.bookShare.user.id = :ownerId
            AND b.status IN :statuses
            AND b.ownerDeletedAt IS NULL
            ORDER BY b.requestedAt DESC
            """)
    List<Borrowing> findVisibleIncomingRequests(
            @Param("ownerId") Long ownerId,
            @Param("statuses") Collection<BorrowingStatus> statuses
    );

    List<Borrowing> findAllByBorrower_IdOrderByRequestedAtDesc(
            Long borrowerId
    );

    List<Borrowing> findAllByBorrower_IdAndStatusIn(
            Long borrowerId,
            Collection<BorrowingStatus> statuses
    );
}
