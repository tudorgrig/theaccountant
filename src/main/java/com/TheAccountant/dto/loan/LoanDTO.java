package com.TheAccountant.dto.loan;

import com.TheAccountant.dto.abstracts.CurrencyHolderDTO;
import com.TheAccountant.dto.counterparty.CounterpartyDTO;
import java.sql.Timestamp;

/**
 * Created by tudor.grigoriu on 6/3/2017.
 */
public class LoanDTO extends CurrencyHolderDTO {

    private long id;
    private CounterpartyDTO counterparty;
    private Boolean receiving;
    private Boolean active;
    private String description;
    private Timestamp untilDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CounterpartyDTO getCounterparty() {
        return counterparty;
    }

    public void setCounterparty(CounterpartyDTO counterparty) {
        this.counterparty = counterparty;
    }

    public Boolean getReceiving() {
        return receiving;
    }

    public void setReceiving(Boolean receiving) {
        this.receiving = receiving;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getUntilDate() {
        return untilDate;
    }

    public void setUntilDate(Timestamp untilDate) {
        this.untilDate = untilDate;
    }
}
