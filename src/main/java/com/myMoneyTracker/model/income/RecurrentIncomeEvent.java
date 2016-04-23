package com.myMoneyTracker.model.income;

import com.myMoneyTracker.model.expense.Expense;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by tudor.grigoriu on 22.04.2016.
 */
@Entity
@Table(name = "recurrent_income_event")
public class RecurrentIncomeEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "income_id")
    @NotNull
    private Income income;

    @Column(name = "start_day", nullable = false)
    private Integer startDay;

    @Column(name = "start_month", nullable = false)
    private Integer startMonth;

    @Column(name = "frequency", nullable = false)
    private String frequency;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getStartDay() {
        return startDay;
    }

    public void setStartDay(Integer startDay) {
        this.startDay = startDay;
    }

    public Integer getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(Integer startMonth) {
        this.startMonth = startMonth;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public Income getIncome() {
        return income;
    }

    public void setIncome(Income income) {
        this.income = income;
    }
}
