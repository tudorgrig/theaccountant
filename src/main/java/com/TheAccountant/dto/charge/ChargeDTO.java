package com.TheAccountant.dto.charge;

/**
 * Created by Florin on 7/30/2017.
 */
public class ChargeDTO {

    private String description;
    private long amount;
    private Currency currency;
    private String stripeEmail;
    private String stripeToken;
    private Boolean paymentApproved;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getStripeEmail() {
        return stripeEmail;
    }

    public void setStripeEmail(String stripeEmail) {
        this.stripeEmail = stripeEmail;
    }

    public String getStripeToken() {
        return stripeToken;
    }

    public void setStripeToken(String stripeToken) {
        this.stripeToken = stripeToken;
    }

    public Boolean getPaymentApproved() {
        return paymentApproved;
    }

    public void setPaymentApproved(Boolean paymentApproved) {
        this.paymentApproved = paymentApproved;
    }

    public enum Currency {
        EUR, USD;
    }

    @Override
    public String toString() {
        return "ChargeDTO{" +
                "description='" + description + '\'' +
                ", amount=" + amount +
                ", currency=" + currency +
                ", stripeEmail='" + stripeEmail + '\'' +
                ", stripeToken='" + stripeToken + '\'' +
                '}';
    }
}
