package edu.spendsense.dao;

import edu.spendsense.model.Category;
import edu.spendsense.model.FinancialTransaction;

import java.sql.SQLException;
import java.util.List;

public interface TransactionRepository {
    List<FinancialTransaction> findAll(int userId) throws SQLException;
    List<Category> findCategories() throws SQLException;
    void add(FinancialTransaction transaction) throws SQLException;
    void update(int transactionId, FinancialTransaction transaction, int userId) throws SQLException;
    void delete(int transactionId, int userId) throws SQLException;
}
