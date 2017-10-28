package com.TheAccountant.dao;

import com.TheAccountant.model.payment.PaymentStripe;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

/**
 * Created by florinIacob on 27.08.2017.
 * Data access object class for entity {@link PaymentStripe}
 */
@Transactional
public interface PaymentStripeDao extends JpaRepository<PaymentStripe, Long> {

}
