//package gui;
//
//import controller.database;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.*;
//import java.sql.*;
//import java.time.LocalDate;
//
//public class ADD_TRANSACTIONS_WINDOW extends JFrame {
//    private JComboBox<String> typeCombo;
//    private JComboBox<String> categoryCombo;
//    private JTextField amountField;
//    private JTextField dateField;
//    private JTextArea notesArea;
//    private JLabel currencyLabel;
//
//    public ADD_TRANSACTIONS_WINDOW(String name , String post) {
//        setTitle("Add Transaction");
//        setSize(500, 450);
//        setLocationRelativeTo(null);
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        setLayout(null);
//        getContentPane().setBackground(new Color(230, 245, 255));
//
//        initComponents();
//        loadCurrency();
//        loadCategories();
//        setVisible(true);
//    }
//
//    private void initComponents() {
//        JLabel title = new JLabel("Add Transaction");
//        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
//        title.setBounds(150, 20, 250, 30);
//        add(title);
//
//        // Type
//        JLabel typeLabel = new JLabel("Type:");
//        typeLabel.setBounds(50, 70, 100, 25);
//        add(typeLabel);
//
//        typeCombo = new JComboBox<>(new String[]{"Income", "Expense"});
//        typeCombo.setBounds(150, 70, 200, 25);
//        typeCombo.addActionListener(e -> loadCategories());
//        add(typeCombo);
//
//        // Category
//        JLabel categoryLabel = new JLabel("Category:");
//        categoryLabel.setBounds(50, 110, 100, 25);
//        add(categoryLabel);
//
//        categoryCombo = new JComboBox<>();
//        categoryCombo.setBounds(150, 110, 200, 25);
//        add(categoryCombo);
//
//        // Amount + currency label
//        JLabel amountLabel = new JLabel("Amount:");
//        amountLabel.setBounds(50, 150, 100, 25);
//        add(amountLabel);
//
//        amountField = new JTextField();
//        amountField.setBounds(150, 150, 200, 25);
//        add(amountField);
//
//        currencyLabel = new JLabel("");
//        currencyLabel.setBounds(360, 150, 100, 25);
//        add(currencyLabel);
//
//        // Date
//        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
//        dateLabel.setBounds(50, 190, 150, 25);
//        add(dateLabel);
//
//        dateField = new JTextField(LocalDate.now().toString());
//        dateField.setBounds(200, 190, 150, 25);
//        add(dateField);
//
//        // Notes
//        JLabel notesLabel = new JLabel("Notes:");
//        notesLabel.setBounds(50, 230, 100, 25);
//        add(notesLabel);
//
//        notesArea = new JTextArea();
//        notesArea.setLineWrap(true);
//        notesArea.setWrapStyleWord(true);
//        JScrollPane scrollPane = new JScrollPane(notesArea);
//        scrollPane.setBounds(150, 230, 250, 70);
//        add(scrollPane);
//
//        // Add button
//        JButton addButton = new JButton("Add Transaction");
//        addButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
//        addButton.setBackground(new Color(52, 152, 219));
//        addButton.setForeground(Color.WHITE);
//        addButton.setBounds(150, 320, 200, 40);
//        addButton.setFocusPainted(false);
//        addButton.addActionListener(e -> addTransaction());
//        add(addButton);
//    }
//
//    private void loadCurrency() {
//        // read latest currency from settings
//        try (Connection conn = database.getConnection();
//             PreparedStatement ps = conn.prepareStatement(
//                     "SELECT currency FROM settings ORDER BY id DESC LIMIT 1");
//             ResultSet rs = ps.executeQuery()) {
//            if (rs.next()) {
//                currencyLabel.setText(rs.getString("currency"));
//            } else {
//                currencyLabel.setText("");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            currencyLabel.setText("");
//        }
//    }
//
//    private void loadCategories() {
//        categoryCombo.removeAllItems();
//        String type = (String) typeCombo.getSelectedItem(); // “Income” or “Expense”
//        if (type == null) return;
//
//        try (Connection conn = database.getConnection();
//             PreparedStatement ps = conn.prepareStatement(
//                     "SELECT name FROM categories WHERE type = ?")) {
//            ps.setString(1, type);
//            try (ResultSet rs = ps.executeQuery()) {
//                boolean any = false;
//                while (rs.next()) {
//                    categoryCombo.addItem(rs.getString("name"));
//                    any = true;
//                }
//                if (!any) {
//                    categoryCombo.addItem("-- No Categories Found --");
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            categoryCombo.addItem("-- Error Loading --");
//        }
//    }
//
//    private void addTransaction() {
//        String type = (String) typeCombo.getSelectedItem();
//        String category = (String) categoryCombo.getSelectedItem();
//        String amountText = amountField.getText().trim();
//        String dateText = dateField.getText().trim();
//        String notes = notesArea.getText().trim();
//
//        if (type == null || category == null
//                || category.startsWith("--")
//                || amountText.isEmpty() || dateText.isEmpty()) {
//            JOptionPane.showMessageDialog(this, "Please fill all required fields with valid values.");
//            return;
//        }
//
//        double amount;
//        Date sqlDate;
//        try {
//            amount = Double.parseDouble(amountText);
//            sqlDate = Date.valueOf(dateText); // throws if invalid
//        } catch (NumberFormatException ex) {
//            JOptionPane.showMessageDialog(this, "Amount must be a valid number.");
//            return;
//        } catch (IllegalArgumentException ex) {
//            JOptionPane.showMessageDialog(this, "Date must be in YYYY-MM-DD format.");
//            return;
//        }
//
//        String insertSql = "INSERT INTO transactions (type, category, amount, date, notes) VALUES (?, ?, ?, ?, ?)";
//        try (Connection conn = database.getConnection();
//             PreparedStatement ps = conn.prepareStatement(insertSql)) {
//            ps.setString(1, type);
//            ps.setString(2, category);
//            ps.setDouble(3, amount);
//            ps.setDate(4, sqlDate);
//            ps.setString(5, notes);
//            int rows = ps.executeUpdate();
//            if (rows > 0) {
//                JOptionPane.showMessageDialog(this, "Transaction added successfully.");
//                amountField.setText("");
//                notesArea.setText("");
//            } else {
//                JOptionPane.showMessageDialog(this, "Failed to add transaction.");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(this, "Error adding transaction: " + e.getMessage());
//        }
//    }
//}
package gui;

import controller.database;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

public class ADD_TRANSACTIONS_WINDOW extends JFrame {
    private JComboBox<String> typeCombo;
    private JComboBox<String> categoryCombo;
    private JTextField amountField;
    private JTextField dateField;
    private JTextArea notesArea;
    private JLabel currencyLabel;

    // Store name and post
    private final String userName;
    private final String userPost;

    public ADD_TRANSACTIONS_WINDOW(String name, String post) {
        this.userName = name;
        this.userPost = post;

        setTitle("Add Transaction");
        setSize(500, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(230, 245, 255));

        initComponents();
        loadCurrency();
        loadCategories();
        setVisible(true);
    }

    private void initComponents() {
        JLabel title = new JLabel("Add Transaction");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBounds(150, 20, 250, 30);
        add(title);

        // Type
        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setBounds(50, 70, 100, 25);
        add(typeLabel);

        typeCombo = new JComboBox<>(new String[]{"Income", "Expense"});
        typeCombo.setBounds(150, 70, 200, 25);
        typeCombo.addActionListener(e -> loadCategories());
        add(typeCombo);

        // Category
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setBounds(50, 110, 100, 25);
        add(categoryLabel);

        categoryCombo = new JComboBox<>();
        categoryCombo.setBounds(150, 110, 200, 25);
        add(categoryCombo);

        // Amount + currency label
        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setBounds(50, 150, 100, 25);
        add(amountLabel);

        amountField = new JTextField();
        amountField.setBounds(150, 150, 200, 25);
        add(amountField);

        currencyLabel = new JLabel("");
        currencyLabel.setBounds(360, 150, 100, 25);
        add(currencyLabel);

        // Date
        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
        dateLabel.setBounds(50, 190, 150, 25);
        add(dateLabel);

        dateField = new JTextField(LocalDate.now().toString());
        dateField.setBounds(200, 190, 150, 25);
        add(dateField);

        // Notes
        JLabel notesLabel = new JLabel("Notes:");
        notesLabel.setBounds(50, 230, 100, 25);
        add(notesLabel);

        notesArea = new JTextArea();
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(notesArea);
        scrollPane.setBounds(150, 230, 250, 70);
        add(scrollPane);

        // Add button
        JButton addButton = new JButton("Add Transaction");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        addButton.setBackground(new Color(52, 152, 219));
        addButton.setForeground(Color.WHITE);
        addButton.setBounds(150, 320, 200, 40);
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> addTransaction());
        add(addButton);
    }

    private void loadCurrency() {
        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT currency FROM settings ORDER BY id DESC LIMIT 1");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                currencyLabel.setText(rs.getString("currency"));
            } else {
                currencyLabel.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
            currencyLabel.setText("");
        }
    }

    private void loadCategories() {
        categoryCombo.removeAllItems();
        String type = (String) typeCombo.getSelectedItem();
        if (type == null) return;

        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT name FROM categories WHERE type = ?")) {
            ps.setString(1, type);
            try (ResultSet rs = ps.executeQuery()) {
                boolean any = false;
                while (rs.next()) {
                    categoryCombo.addItem(rs.getString("name"));
                    any = true;
                }
                if (!any) {
                    categoryCombo.addItem("-- No Categories Found --");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            categoryCombo.addItem("-- Error Loading --");
        }
    }

//    private void addTransaction() {
//        String type = (String) typeCombo.getSelectedItem();
//        String category = (String) categoryCombo.getSelectedItem();
//        String amountText = amountField.getText().trim();
//        String dateText = dateField.getText().trim();
//        String notes = notesArea.getText().trim();
//
//        if (type == null || category == null
//                || category.startsWith("--")
//                || amountText.isEmpty() || dateText.isEmpty()) {
//            JOptionPane.showMessageDialog(this, "Please fill all required fields with valid values.");
//            return;
//        }
//
//        double amount;
//        Date sqlDate;
//        try {
//            amount = Double.parseDouble(amountText);
//            sqlDate = Date.valueOf(dateText);
//        } catch (NumberFormatException ex) {
//            JOptionPane.showMessageDialog(this, "Amount must be a valid number.");
//            return;
//        } catch (IllegalArgumentException ex) {
//            JOptionPane.showMessageDialog(this, "Date must be in YYYY-MM-DD format.");
//            return;
//        }
//
//        String insertSql = "INSERT INTO transactions (type, category, amount, date, notes, name, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
//        try (Connection conn = database.getConnection();
//             PreparedStatement ps = conn.prepareStatement(insertSql)) {
//            ps.setString(1, type);
//            ps.setString(2, category);
//            ps.setDouble(3, amount);
//            ps.setDate(4, sqlDate);
//            ps.setString(5, notes);
//            ps.setString(6, userName);
//            ps.setString(7, userPost);
//
//            int rows = ps.executeUpdate();
//            if (rows > 0) {
//                JOptionPane.showMessageDialog(this, "Transaction added successfully.");
//                amountField.setText("");
//                notesArea.setText("");
//            } else {
//                JOptionPane.showMessageDialog(this, "Failed to add transaction.");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(this, "Error adding transaction: " + e.getMessage());
//        }
//    }
private void addTransaction() {
    String type = (String) typeCombo.getSelectedItem();
    String category = (String) categoryCombo.getSelectedItem();
    String amountText = amountField.getText().trim();
    String dateText = dateField.getText().trim();
    String notes = notesArea.getText().trim();
    String currency = currencyLabel.getText().trim();

    if (type == null || category == null
            || category.startsWith("--")
            || amountText.isEmpty() || dateText.isEmpty()
            || currency.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please fill all required fields with valid values.");
        return;
    }

    double amount;
    Date sqlDate;
    try {
        amount = Double.parseDouble(amountText);
        sqlDate = Date.valueOf(dateText);
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Amount must be a valid number.");
        return;
    } catch (IllegalArgumentException ex) {
        JOptionPane.showMessageDialog(this, "Date must be in YYYY-MM-DD format.");
        return;
    }

    String insertSql = "INSERT INTO transactions (type, category, amount, currency, date, notes, name, status) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = database.getConnection();
         PreparedStatement ps = conn.prepareStatement(insertSql)) {
        ps.setString(1, type);
        ps.setString(2, category);
        ps.setDouble(3, amount);
        ps.setString(4, currency); // new currency field
        ps.setDate(5, sqlDate);
        ps.setString(6, notes);
        ps.setString(7, userName);
        ps.setString(8, userPost);

        int rows = ps.executeUpdate();
        if (rows > 0) {
            JOptionPane.showMessageDialog(this, "Transaction added successfully.");
            amountField.setText("");
            notesArea.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add transaction.");
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error adding transaction: " + e.getMessage());
    }
}

}
