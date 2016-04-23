package com.myMoneyTracker.model.expense;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by tudor.grigoriu on 22.04.2016.
 */
@Entity
@Table(name = "recurrent_expense_event")
public class RecurrentExpenseEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "expense_id")
    private Expense expense;

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

    public Expense getExpense() {
        return expense;
    }

    public void setExpense(Expense expense) {
        this.expense = expense;
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
}
