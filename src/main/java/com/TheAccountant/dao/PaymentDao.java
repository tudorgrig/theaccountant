package com.TheAccountant.dao;

import com.TheAccountant.model.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

/**
 * Created by florinIacob on 27.08.2017.
 * Data access object class for entity {@link Payment}
 */
@Transactional
public interface PaymentDao extends JpaRepository<Payment, Long> {

}
