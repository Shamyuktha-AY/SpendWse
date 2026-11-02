package edu.spendsense.ui;

import edu.spendsense.config.AppConfig;
import edu.spendsense.dao.TransactionDAO;
import edu.spendsense.exception.InvalidAmountException;
import edu.spendsense.model.Category;
import edu.spendsense.model.Expense;
import edu.spendsense.model.FinancialTransaction;
import edu.spendsense.model.Income;
import edu.spendsense.service.TransactionService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class SpendSenseFrame extends JFrame {
    private final TransactionService service =
            new TransactionService(new TransactionDAO());

    private final JComboBox<String> typeBox = new JComboBox<>(new String[]{"EXPENSE", "INCOME"});
    private final JComboBox<Category> categoryBox = new JComboBox<>();
    private final JComboBox<String> natureBox = new JComboBox<>(new String[]{"NEED", "WANT"});
    private final JTextField amountField = new JTextField(12);
    private final JTextField descriptionField = new JTextField(18);
    private final JTextField dateField = new JTextField(LocalDate.now().toString(), 10);
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"ID", "Type", "Amount", "Category ID", "Need/Want", "Description", "Date"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    public SpendSenseFrame() {
        setTitle(AppConfig.APP_TITLE);
        setSize(1050, 650);
        setMinimumSize(new Dimension(850, 520));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        buildScreen();
        loadCategories();
        loadTransactions();
    }

    private void buildScreen() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel heading = new JLabel("SpendSense | Smart Personal Expense Tracker", SwingConstants.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 22));
        main.add(heading, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(0, 4, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Transaction Details"));
        form.add(new JLabel("Type"));
        form.add(typeBox);
        form.add(new JLabel("Category"));
        form.add(categoryBox);
        form.add(new JLabel("Amount"));
        form.add(amountField);
        form.add(new JLabel("Need / Want (for expense)"));
        form.add(natureBox);
        form.add(new JLabel("Description"));
        form.add(descriptionField);
        form.add(new JLabel("Date (yyyy-mm-dd)"));
        form.add(dateField);

        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update Selected");
        JButton deleteButton = new JButton("Delete Selected");
        JButton refreshButton = new JButton("Refresh");
        JButton clearButton = new JButton("Clear");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.add(addButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);
        buttons.add(refreshButton);
        buttons.add(clearButton);

        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.add(form, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Transaction History"));

        main.add(top, BorderLayout.CENTER);
        main.add(scrollPane, BorderLayout.SOUTH);
        setContentPane(main);

        addButton.addActionListener(event -> addTransaction());
        updateButton.addActionListener(event -> updateSelected());
        deleteButton.addActionListener(event -> deleteSelected());
        refreshButton.addActionListener(event -> loadTransactions());
        clearButton.addActionListener(event -> clearForm());

        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                fillFormFromSelectedRow();
            }
        });

        typeBox.addActionListener(event -> updateNatureControl());
        updateNatureControl();
    }

    private void updateNatureControl() {
        boolean isExpense = "EXPENSE".equals(typeBox.getSelectedItem());
        natureBox.setEnabled(isExpense);
    }

    private FinancialTransaction createTransactionFromForm() throws InvalidAmountException {
        String amountText = amountField.getText().trim();
        service.validateAmount(amountText);

        Category category = (Category) categoryBox.getSelectedItem();
        if (category == null) {
            throw new InvalidAmountException("No category found. Add categories in MySQL first.");
        }

        BigDecimal amount = new BigDecimal(amountText);
        String description = descriptionField.getText().trim();
        LocalDate date;
        try {
            date = LocalDate.parse(dateField.getText().trim());
        } catch (Exception exception) {
            throw new InvalidAmountException("Date must be in yyyy-mm-dd format.");
        }

        String type = (String) typeBox.getSelectedItem();
        if ("INCOME".equals(type)) {
            return new Income(AppConfig.USER_ID, category.getCategoryId(),
                    amount, description, date);
        }
        return new Expense(AppConfig.USER_ID, category.getCategoryId(),
                amount, description, date, (String) natureBox.getSelectedItem());
    }

    private void addTransaction() {
        try {
            service.addTransaction(createTransactionFromForm());
            showMessage("Transaction added successfully.");
            loadTransactions();
            clearForm();
        } catch (InvalidAmountException exception) {
            showError(exception.getMessage());
        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    private void updateSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            showError("Select a transaction row first.");
            return;
        }
        int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        try {
            service.updateTransaction(id, createTransactionFromForm(), AppConfig.USER_ID);
            showMessage("Transaction updated successfully.");
            loadTransactions();
            clearForm();
        } catch (InvalidAmountException exception) {
            showError(exception.getMessage());
        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            showError("Select a transaction row first.");
            return;
        }
        int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        int choice = JOptionPane.showConfirmDialog(this,
                "Delete selected transaction?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) return;

        try {
            service.deleteTransaction(id, AppConfig.USER_ID);
            showMessage("Transaction deleted successfully.");
            loadTransactions();
            clearForm();
        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    private void loadCategories() {
        try {
            categoryBox.removeAllItems();
            List<Category> categories = new TransactionDAO().findCategories();
            for (Category category : categories) categoryBox.addItem(category);
        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    private void loadTransactions() {
        try {
            tableModel.setRowCount(0);
            List<FinancialTransaction> transactions =
                    service.getTransactions(AppConfig.USER_ID);
            for (FinancialTransaction transaction : transactions) {
                tableModel.addRow(new Object[]{
                        transaction.getTransactionId(),
                        transaction.getTransactionType(),
                        transaction.getAmount(),
                        transaction.getCategoryId(),
                        transaction.getExpenseNature(),
                        transaction.getDescription(),
                        transaction.getTransactionDate()
                });
            }
        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    private void fillFormFromSelectedRow() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        typeBox.setSelectedItem(tableModel.getValueAt(row, 1).toString());
        amountField.setText(tableModel.getValueAt(row, 2).toString());
        String nature = String.valueOf(tableModel.getValueAt(row, 4));
        natureBox.setSelectedItem(nature);
        descriptionField.setText(String.valueOf(tableModel.getValueAt(row, 5)));
        dateField.setText(String.valueOf(tableModel.getValueAt(row, 6)));
        updateNatureControl();
    }

    private void clearForm() {
        amountField.setText("");
        descriptionField.setText("");
        dateField.setText(LocalDate.now().toString());
        table.clearSelection();
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "SpendSense", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Error", JOptionPane.WARNING_MESSAGE);
    }

    private void showDatabaseError(SQLException exception) {
        JOptionPane.showMessageDialog(this,
                "Could not connect to/read the database.\n"
                + "Check MySQL is running, DB password in DBConnection.java,\n"
                + "and that spendsense_db tables exist.\n\nDetails: " + exception.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}
