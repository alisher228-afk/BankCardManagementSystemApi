package org.akusher.bankcardmanagementsystemapi.repository;

import jakarta.persistence.LockModeType;
import org.akusher.bankcardmanagementsystemapi.entity.Account;
import org.akusher.bankcardmanagementsystemapi.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account,Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.id = :id")
    Optional<Account> findByIdForUpdate(@Param("id") Long id);

    Page<Account> findByUser(User user, Pageable pageable);

    Optional<Account> findByIdAndUser(Long id, User user);
}

