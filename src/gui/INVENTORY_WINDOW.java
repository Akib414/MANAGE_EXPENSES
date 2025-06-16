package gui;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class INVENTORY_WINDOW extends JFrame {

    private JTextField txtProductName, txtQuantity;
    private JTable inventoryTable;
    private DefaultTableModel tableModel;

    public INVENTORY_WINDOW() {
        setTitle("Inventory Manager");
        setSize(850, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Title panel
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setBackground(new Color(240, 248, 255));

        JLabel title = new JLabel("Inventory Management System", SwingConstants.CENTER);
        title.setFont(new Font("Verdana", Font.BOLD, 24));
        title.setForeground(new Color(52, 152, 219));
        topPanel.add(title);

        // Input & buttons panel
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.setBackground(new Color(245, 255, 250));

        txtProductName = new JTextField(15);
        txtQuantity = new JTextField(5);

        JButton btnAddProduct = new JButton("Add Product");
        JButton btnDeleteProduct = new JButton("Delete Product");
        JButton btnAddQuantity = new JButton("Add Quantity");

        styleButton(btnAddProduct, new Color(46, 204, 113));
        styleButton(btnDeleteProduct, new Color(231, 76, 60));
        styleButton(btnAddQuantity, new Color(241, 196, 15));

        inputPanel.add(new JLabel("Product Name:"));
        inputPanel.add(txtProductName);
        inputPanel.add(new JLabel("Quantity:"));
        inputPanel.add(txtQuantity);
        inputPanel.add(btnAddProduct);
        inputPanel.add(btnDeleteProduct);
        inputPanel.add(btnAddQuantity);

        topPanel.add(inputPanel);
        add(topPanel, BorderLayout.NORTH);

        // Inventory Table
        tableModel = new DefaultTableModel(new String[]{"Product Name", "Quantity"}, 0);
        inventoryTable = new JTable(tableModel);
        inventoryTable.setRowHeight(25);
        inventoryTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button actions
        btnAddProduct.addActionListener(e -> addProduct());
        btnDeleteProduct.addActionListener(e -> deleteProduct());
        btnAddQuantity.addActionListener(e -> addQuantity());

        // Load inventory on start
        loadInventory();

        setVisible(true);
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setFocusPainted(false);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
    }

    // Adds new product to DB
    private void addProduct() {
        String name = txtProductName.getText().trim();
        String qtyText = txtQuantity.getText().trim();
        if (name.isEmpty() || qtyText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter product name and quantity.");
            return;
        }
        try {
            int qty = Integer.parseInt(qtyText);
            try (Connection conn = controller.database.getConnection()) {
                String query = "INSERT INTO inventory (product_name, quantity) VALUES (?, ?)";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, name);
                ps.setInt(2, qty);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Product added.");
                loadInventory();
            } catch (SQLException ex) {
                if (ex.getErrorCode() == 1062) // Duplicate entry
                    JOptionPane.showMessageDialog(this, "Product already exists.");
                else
                    JOptionPane.showMessageDialog(this, "Error adding product: " + ex.getMessage());
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantity must be a valid number.");
        }
    }

    // Deletes product from DB
    private void deleteProduct() {
        String name = txtProductName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter product name to delete.");
            return;
        }
        try (Connection conn = controller.database.getConnection()) {
            String query = "DELETE FROM inventory WHERE product_name = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, name);
            int affected = ps.executeUpdate();
            if (affected > 0) {
                JOptionPane.showMessageDialog(this, "Product deleted.");
            } else {
                JOptionPane.showMessageDialog(this, "Product not found.");
            }
            loadInventory();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting product: " + ex.getMessage());
        }
    }

    // Adds quantity to existing product
    private void addQuantity() {
        String name = txtProductName.getText().trim();
        String qtyText = txtQuantity.getText().trim();
        if (name.isEmpty() || qtyText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter product name and quantity.");
            return;
        }
        try {
            int qtyToAdd = Integer.parseInt(qtyText);
            try (Connection conn = controller.database.getConnection()) {
                String selectQuery = "SELECT quantity FROM inventory WHERE product_name = ?";
                PreparedStatement selectPs = conn.prepareStatement(selectQuery);
                selectPs.setString(1, name);
                ResultSet rs = selectPs.executeQuery();
                if (rs.next()) {
                    int currentQty = rs.getInt("quantity");
                    int newQty = currentQty + qtyToAdd;

                    String updateQuery = "UPDATE inventory SET quantity = ? WHERE product_name = ?";
                    PreparedStatement updatePs = conn.prepareStatement(updateQuery);
                    updatePs.setInt(1, newQty);
                    updatePs.setString(2, name);
                    updatePs.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Quantity updated.");
                } else {
                    JOptionPane.showMessageDialog(this, "Product not found.");
                }
                loadInventory();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantity must be a number.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating quantity: " + ex.getMessage());
        }
    }

    // Loads inventory data into the table
    private void loadInventory() {
        tableModel.setRowCount(0); // clear table
        try (Connection conn = controller.database.getConnection()) {
            String query = "SELECT product_name, quantity FROM inventory";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("product_name"));
                row.add(rs.getInt("quantity"));
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading inventory: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(INVENTORY_WINDOW::new);
    }
}
