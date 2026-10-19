
package com.shareshelf.repository;

import com.shareshelf.entity.ReadingListItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadingListItemRepository extends JpaRepository<ReadingListItem, Long> {
}
