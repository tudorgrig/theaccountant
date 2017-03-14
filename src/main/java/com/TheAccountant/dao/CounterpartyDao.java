package com.TheAccountant.dao;

import com.TheAccountant.model.counterparty.Counterparty;
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
public interface CounterpartyDao extends JpaRepository<Counterparty, Long> {

    @Query("SELECT DISTINCT C FROM Counterparty C WHERE C.user.username = ?1 ORDER BY email DESC")
    List<Counterparty> fetchAll(String username);
}
