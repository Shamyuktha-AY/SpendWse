package edu.spendsense.dao;

import edu.spendsense.database.DBConnection;
import edu.spendsense.model.Category;
import edu.spendsense.model.Expense;
import edu.spendsense.model.FinancialTransaction;
import edu.spendsense.model.Income;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO implements TransactionRepository {

    @Override
    public List<FinancialTransaction> findAll(int userId) throws SQLException {
        List<FinancialTransaction> list = new ArrayList<>();
        String sql = """
                SELECT transaction_id, user_id, category_id, amount,
                       transaction_type, expense_nature, description, transaction_date
                FROM transactions
                WHERE user_id = ?
                ORDER BY transaction_date DESC, transaction_id DESC
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    String type = result.getString("transaction_type");
                    String nature = result.getString("expense_nature");
                    FinancialTransaction transaction;

                    if ("INCOME".equalsIgnoreCase(type)) {
                        transaction = new Income(
                                result.getInt("user_id"), result.getInt("category_id"),
                                result.getBigDecimal("amount"), result.getString("description"),
                                result.getDate("transaction_date").toLocalDate());
                    } else {
                        transaction = new Expense(
                                result.getInt("user_id"), result.getInt("category_id"),
                                result.getBigDecimal("amount"), result.getString("description"),
                                result.getDate("transaction_date").toLocalDate(), nature);
                    }
                    transaction.setTransactionId(result.getInt("transaction_id"));
                    list.add(transaction);
                }
            }
        }
        return list;
    }

    @Override
    public List<Category> findCategories() throws SQLException {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT category_id, category_name, category_type FROM categories ORDER BY category_name";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                categories.add(new Category(result.getInt("category_id"),
                        result.getString("category_name"), result.getString("category_type")));
            }
        }
        return categories;
    }

    @Override
    public void add(FinancialTransaction transaction) throws SQLException {
        String sql = """
                INSERT INTO transactions
                (user_id, category_id, amount, transaction_type, expense_nature, description, transaction_date)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            fillStatement(statement, transaction);
            statement.executeUpdate();
        }
    }

    @Override
    public void update(int transactionId, FinancialTransaction transaction, int userId) throws SQLException {
        String sql = """
                UPDATE transactions
                SET category_id = ?, amount = ?, transaction_type = ?,
                    expense_nature = ?, description = ?, transaction_date = ?
                WHERE transaction_id = ? AND user_id = ?
                """;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, transaction.getCategoryId());
            statement.setBigDecimal(2, transaction.getAmount());
            statement.setString(3, transaction.getTransactionType());
            statement.setString(4, transaction.getExpenseNature());
            statement.setString(5, transaction.getDescription());
            statement.setDate(6, Date.valueOf(transaction.getTransactionDate()));
            statement.setInt(7, transactionId);
            statement.setInt(8, userId);
            statement.executeUpdate();
        }
    }

    @Override
    public void delete(int transactionId, int userId) throws SQLException {
        String sql = "DELETE FROM transactions WHERE transaction_id = ? AND user_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, transactionId);
            statement.setInt(2, userId);
            statement.executeUpdate();
        }
    }

    private void fillStatement(PreparedStatement statement, FinancialTransaction transaction)
            throws SQLException {
        statement.setInt(1, transaction.getUserId());
        statement.setInt(2, transaction.getCategoryId());
        statement.setBigDecimal(3, transaction.getAmount());
        statement.setString(4, transaction.getTransactionType());
        statement.setString(5, transaction.getExpenseNature());
        statement.setString(6, transaction.getDescription());
        statement.setDate(7, Date.valueOf(transaction.getTransactionDate()));
    }
}
