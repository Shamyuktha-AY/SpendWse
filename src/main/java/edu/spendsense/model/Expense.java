package edu.spendsense.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Expense extends FinancialTransaction {
    public Expense(int userId, int categoryId, BigDecimal amount, String description,
                   LocalDate date, String expenseNature) {
        super(userId, categoryId, amount, description, date, expenseNature);
    }

    @Override
    public String getTransactionType() {
        return "EXPENSE";
    }
}
