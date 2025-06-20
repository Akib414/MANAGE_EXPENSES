package gui;

import controller.database;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class MANAGE_CATEGORIES_WINDOW extends JFrame {

    private JTextField categoryNameField;
    private JComboBox<String> typeComboBox;
    private JTable categoryTable;
    private DefaultTableModel tableModel;

    public MANAGE_CATEGORIES_WINDOW() {
        setTitle("Manage Categories");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        initComponents();
        loadCategories();
        setVisible(true);
    }

    private void initComponents() {
        JLabel titleLabel = new JLabel("Manage Categories");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setBounds(200, 10, 250, 30);
        add(titleLabel);

        JLabel nameLabel = new JLabel("Category Name:");
        nameLabel.setBounds(30, 60, 120, 25);
        add(nameLabel);

        categoryNameField = new JTextField();
        categoryNameField.setBounds(150, 60, 170, 25);
        add(categoryNameField);

        JLabel typeLabel = new JLabel("Category Type:");
        typeLabel.setBounds(330, 60, 100, 25);
        add(typeLabel);

        String[] types = {"Income", "Expense"};
        typeComboBox = new JComboBox<>(types);
        typeComboBox.setBounds(430, 60, 120, 25);
        add(typeComboBox);

        JButton addButton = new JButton("Add");
        addButton.setBounds(150, 100, 100, 30);
        addButton.setBackground(new Color(46, 204, 113));
        addButton.setForeground(Color.WHITE);
        add(addButton);

        JButton deleteButton = new JButton("Delete Selected");
        deleteButton.setBounds(270, 100, 150, 30);
        deleteButton.setBackground(new Color(231, 76, 60));
        deleteButton.setForeground(Color.WHITE);
        add(deleteButton);

        // Table
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Type"}, 0);
        categoryTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(categoryTable);
        scrollPane.setBounds(30, 150, 520, 230);
        add(scrollPane);

        addButton.addActionListener(e -> addCategory());
        deleteButton.addActionListener(e -> deleteSelectedCategory());
    }

    private void loadCategories() {
        tableModel.setRowCount(0);
        try (Connection conn = database.getConnection()) {
            String sql = "SELECT id, name, type FROM categories ORDER BY id DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("type")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading categories: " + ex.getMessage());
        }
    }

    private void addCategory() {
        String name = categoryNameField.getText().trim();
        String type = (String) typeComboBox.getSelectedItem();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a category name.");
            return;
        }

        try (Connection conn = database.getConnection()) {
            String sql = "INSERT INTO categories (name, type) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, type);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Category added.");
                categoryNameField.setText("");
                loadCategories();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add category.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void deleteSelectedCategory() {
        int row = categoryTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a category to delete.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this category?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = database.getConnection()) {
            String sql = "DELETE FROM categories WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Category deleted.");
                loadCategories();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete category.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
