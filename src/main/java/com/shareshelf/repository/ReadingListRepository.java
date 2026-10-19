
package com.shareshelf.repository;

import com.shareshelf.entity.ReadingList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadingListRepository extends JpaRepository<ReadingList, Long> {
}
