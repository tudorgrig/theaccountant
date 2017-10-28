package com.TheAccountant.model.payment;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by Florin on 8/27/2017.
 */
@Entity
@Table(name = "PAYMENT_STRIPE")
public class PaymentStripe {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    @Column(name = "charge_id", nullable = false)
    private String chargeId;

    @NotNull
    @Column(name = "creation_date", nullable = false)
    private Date creationDate;

    @NotNull
    private String status;

    @NotNull
    private Boolean paid;

    @NotNull
    private Boolean refunded;

    @NotNull
    @Column(name = "amount_cents", nullable = false)
    private Long amountCents;

    private Long amountCentsRefunded;

    @NotNull
    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "charge_outcome")
    private String chargeOutcome;

    @Column(name = "charge_email")
    private String chargeEmail;

    @Column(name = "payment_description")
    private String paymentDescription;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getChargeId() {
        return chargeId;
    }

    public void setChargeId(String chargeId) {
        this.chargeId = chargeId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public Boolean getRefunded() {
        return refunded;
    }

    public void setRefunded(Boolean refunded) {
        this.refunded = refunded;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getChargeOutcome() {
        return chargeOutcome;
    }

    public void setChargeOutcome(String chargeOutcome) {
        this.chargeOutcome = chargeOutcome;
    }

    public String getChargeEmail() {
        return chargeEmail;
    }

    public void setChargeEmail(String chargeEmail) {
        this.chargeEmail = chargeEmail;
    }

    public String getPaymentDescription() {
        return paymentDescription;
    }

    public void setPaymentDescription(String paymentDescription) {
        this.paymentDescription = paymentDescription;
    }

    public Long getAmountCents() {
        return amountCents;
    }

    public void setAmountCents(Long amountCents) {
        this.amountCents = amountCents;
    }

    public Long getAmountCentsRefunded() {
        return amountCentsRefunded;
    }

    public void setAmountCentsRefunded(Long amountCentsRefunded) {
        this.amountCentsRefunded = amountCentsRefunded;
    }
}
