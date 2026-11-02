package edu.spendsense.service;

import edu.spendsense.dao.TransactionRepository;
import edu.spendsense.exception.InvalidAmountException;
import edu.spendsense.model.FinancialTransaction;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class TransactionService {
    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    public List<FinancialTransaction> getTransactions(int userId) throws SQLException {
        return repository.findAll(userId);
    }

    public void addTransaction(FinancialTransaction transaction) throws SQLException, InvalidAmountException {
        validate(transaction);
        repository.add(transaction);
    }

    public void updateTransaction(int id, FinancialTransaction transaction, int userId)
            throws SQLException, InvalidAmountException {
        validate(transaction);
        repository.update(id, transaction, userId);
    }

    public void deleteTransaction(int id, int userId) throws SQLException {
        repository.delete(id, userId);
    }

    // Method overloading: validate a BigDecimal amount or a full transaction.
    public void validateAmount(BigDecimal amount) throws InvalidAmountException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero.");
        }
    }

    public void validateAmount(String amountText) throws InvalidAmountException {
        try {
            validateAmount(new BigDecimal(amountText.trim()));
        } catch (NumberFormatException exception) {
            throw new InvalidAmountException("Enter a valid numeric amount.");
        }
    }

    private void validate(FinancialTransaction transaction) throws InvalidAmountException {
        validateAmount(transaction.getAmount());
        if (transaction.getDescription() == null || transaction.getDescription().isBlank()) {
            throw new InvalidAmountException("Description cannot be empty.");
        }
        if (transaction.getCategoryId() <= 0) {
            throw new InvalidAmountException("Please select a category.");
        }
        if (transaction.getTransactionDate() == null) {
            throw new InvalidAmountException("Please enter a date in yyyy-mm-dd format.");
        }
        // Polymorphism: calls the overridden method in Income or Expense.
        if (!transaction.getTransactionType().equals("INCOME")
                && !transaction.getTransactionType().equals("EXPENSE")) {
            throw new InvalidAmountException("Invalid transaction type.");
        }
    }
}
