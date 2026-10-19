
package com.shareshelf.repository;

import com.shareshelf.entity.UserLibrary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLibraryRepository extends JpaRepository<UserLibrary, Long> {
}
