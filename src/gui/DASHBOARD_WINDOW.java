package gui;

import javax.swing.*;
import java.awt.*;

public class DASHBOARD_WINDOW extends JFrame {
    private JPanel mainPanel;
    JButton btn_back = new JButton("Back");
    public DASHBOARD_WINDOW() {
        setTitle("Dashboard - Expense Management");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        add(mainPanel);

        addDashboardComponents();

        setVisible(true);
    }

    private void addDashboardComponents() {
        // Title
        JLabel title = new JLabel("Dashboard Summary", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        mainPanel.add(title, BorderLayout.NORTH);

        // Summary Panels
        JPanel summaryPanel = new JPanel(new GridLayout(1, 2, 20, 10));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        summaryPanel.setBackground(new Color(245, 245, 245));

        JLabel incomeLabel = new JLabel("<html><h2>₹25,000</h2><p>Total Income</p></html>", SwingConstants.CENTER);
        incomeLabel.setOpaque(true);
        incomeLabel.setBackground(new Color(144, 238, 144));
        incomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        incomeLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        JLabel expenseLabel = new JLabel("<html><h2>₹18,000</h2><p>Total Expenses</p></html>", SwingConstants.CENTER);
        expenseLabel.setOpaque(true);
        expenseLabel.setBackground(new Color(255, 182, 193));
        expenseLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        expenseLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        summaryPanel.add(incomeLabel);
        summaryPanel.add(expenseLabel);

        mainPanel.add(summaryPanel, BorderLayout.CENTER);

        // Chart Placeholder
        JPanel chartPanel = new JPanel();
        chartPanel.setPreferredSize(new Dimension(800, 250));
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBorder(BorderFactory.createTitledBorder("Expense Chart"));

        JLabel chartPlaceholder = new JLabel("Chart will be displayed here.");
        chartPlaceholder.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        chartPanel.add(chartPlaceholder);

        mainPanel.add(chartPanel, BorderLayout.SOUTH);
        btn_back.setSize(300, 50);
        btn_back.setBackground(Color.GRAY);
        btn_back.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        btn_back.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        btn_back.setOpaque(true);
        mainPanel.add(btn_back, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DASHBOARD_WINDOW::new);
    }
}