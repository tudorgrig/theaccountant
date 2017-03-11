package com.TheAccountant.dao;

import com.TheAccountant.model.loan.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Florin on 3/11/2017.
 */
@Transactional
@Repository
public interface LoanDao extends JpaRepository<Loan, Long> {

    @Query("SELECT DISTINCT L FROM Loan L WHERE L.user.username = ?1 ORDER BY L.creationDate DESC")
    List<Loan> fetchAll(String username);

    @Query(value = "SELECT DISTINCT l.* FROM loan l WHERE l.user_id = ?1 ORDER BY l.creationDate DESC LIMIT ?2 OFFSET ?3", nativeQuery = true)
    List<Loan> fetchAll(long userId, int limit, int offset);

    @Query("SELECT DISTINCT L FROM Loan L WHERE L.user.username = ?1 and L.active = ?2 ORDER BY L.creationDate DESC")
    List<Loan> findByActive(String username, boolean active);

    @Query("SELECT DISTINCT L FROM Loan L WHERE L.user.username = ?1 AND L.counterparty.id = ?2 ORDER BY L.creationDate DESC")
    List<Loan> findByCounterparty(String username, long counterpartyId);

    @Query("SELECT DISTINCT L FROM Loan L WHERE L.user.username = ?1 AND L.counterparty.id = ?2 AND L.active = ?3 ORDER BY L.creationDate DESC")
    List<Loan> findByCounterpartyAndActive(String username, long counterpartyId, boolean active);
}
