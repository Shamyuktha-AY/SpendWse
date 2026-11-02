package edu.spendsense.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Income extends FinancialTransaction {
    public Income(int userId, int categoryId, BigDecimal amount, String description,
                  LocalDate date) {
        super(userId, categoryId, amount, description, date, "NEED");
    }

    @Override
    public String getTransactionType() {
        return "INCOME";
    }
}
