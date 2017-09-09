package com.TheAccountant.dao;

import com.TheAccountant.model.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by florinIacob on 27.08.2017.
 * Data access object class for entity {@link Payment}
 */
@Transactional
public interface PaymentDao extends JpaRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p WHERE p.user.userId = :userId AND lower(p.paymentType) = lower(:paymentType) ORDER BY p.creationDate DESC ")
    List<Payment> findByUserAndPaymentType(@Param("userId") Long userId, @Param("paymentType") String paymentType);
}
