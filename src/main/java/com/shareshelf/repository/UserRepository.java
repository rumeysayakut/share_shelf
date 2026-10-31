
package com.shareshelf.repository;

import com.shareshelf.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByIdAndDeletedAtIsNull(Long id);

    List<User> findAllByDeletedAtIsNull();

    boolean existsByEmailAndIdNot(String email, Long id);

    Optional<User> findByEmailAndDeletedAtIsNull(String email);
    
}
