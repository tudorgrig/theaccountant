package com.TheAccountant.model.payment;

import com.TheAccountant.model.user.AppUser;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by Florin on 8/27/2017.
 */
@Entity
@Table(name = "PAYMENT")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id")
    private AppUser user;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "payment_stripe_id")
    private PaymentStripe paymentStripe;

    @NotNull
    @Column(name = "payment_type")
    private String paymentType;

    @Column(name = "payment_description")
    private String paymentDescription;

    @NotNull
    @Column(name = "amount_cents")
    private Long amountCents;

    @NotNull
    private String currency;

    @NotNull
    @Column(name = "creation_date")
    private Date creationDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public PaymentStripe getPaymentStripe() {
        return paymentStripe;
    }

    public void setPaymentStripe(PaymentStripe paymentStripe) {
        this.paymentStripe = paymentStripe;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
