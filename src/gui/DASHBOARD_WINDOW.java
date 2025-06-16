package gui;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

import controller.database;

import java.awt.event.ActionListener;

public class DASHBOARD_WINDOW extends JFrame {
    private JComboBox<String> yearBox, monthBox, dayBox, viewTypeBox;
    private JLabel incomeLabel, expenseLabel;
    private JPanel chartPanel;

    public DASHBOARD_WINDOW() {
        setTitle("Income/Expense Report");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Dropdown Panel
        JPanel topPanel = new JPanel();

        viewTypeBox = new JComboBox<>(new String[]{"Day", "Month", "Year"});
        yearBox = new JComboBox<>(getYears());
        monthBox = new JComboBox<>(getMonths());
        dayBox = new JComboBox<>(getDays());

        // Set default to latest date and Month view
        java.time.LocalDate now = java.time.LocalDate.now();
        String currentYear = String.valueOf(now.getYear());
        String currentMonth = String.format("%02d", now.getMonthValue());
        String currentDay = String.format("%02d", now.getDayOfMonth());

        viewTypeBox.setSelectedItem("Month");
        yearBox.setSelectedItem(currentYear);
        monthBox.setSelectedItem(currentMonth);
        dayBox.setSelectedItem(currentDay);

        topPanel.add(new JLabel("View:"));
        topPanel.add(viewTypeBox);
        topPanel.add(new JLabel("Year:"));
        topPanel.add(yearBox);
        topPanel.add(new JLabel("Month:"));
        topPanel.add(monthBox);
        topPanel.add(new JLabel("Day:"));
        topPanel.add(dayBox);

        incomeLabel = new JLabel("Income: 0");
        expenseLabel = new JLabel("Expense: 0");
        topPanel.add(incomeLabel);
        topPanel.add(expenseLabel);

        add(topPanel, BorderLayout.NORTH);

        chartPanel = new JPanel();
        add(chartPanel, BorderLayout.CENTER);

        // Auto-calculate on dropdown changes
        ActionListener dropdownListener = e -> calculateAndDisplay();
        viewTypeBox.addActionListener(dropdownListener);
        yearBox.addActionListener(dropdownListener);
        monthBox.addActionListener(dropdownListener);
        dayBox.addActionListener(dropdownListener);

        // Auto-load the default view
        calculateAndDisplay();

        setVisible(true);

    }


    private String[] getYears() {
        return new String[]{"2025", "2024", "2023"};
    }

    private String[] getMonths() {
        return new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    }

    private String[] getDays() {
        String[] days = new String[31];
        for (int i = 1; i <= 31; i++) {
            days[i - 1] = String.format("%02d", i);
        }
        return days;
    }

    private void calculateAndDisplay() {
        String viewType = (String) viewTypeBox.getSelectedItem();
        String year = (String) yearBox.getSelectedItem();
        String month = (String) monthBox.getSelectedItem();
        String day = (String) dayBox.getSelectedItem();

        String condition = "";
        if (viewType.equals("Day")) {
            condition = year + "-" + month + "-" + day;
        } else if (viewType.equals("Month")) {
            condition = year + "-" + month;
        } else {
            condition = year;
        }

        double totalIncome = 0;
        double totalExpense = 0;
        double budget = 0;

        try (Connection con = database.getConnection()) {
            String sql = switch (viewType) {
                case "Day" -> "SELECT type, amount FROM transactions WHERE date = ?";
                case "Month" -> "SELECT type, amount FROM transactions WHERE date LIKE ?";
                case "Year" -> "SELECT type, amount FROM transactions WHERE date LIKE ?";
                default -> throw new IllegalStateException("Unexpected value: " + viewType);
            };

            PreparedStatement stmt = con.prepareStatement(sql);
            if (viewType.equals("Day")) {
                stmt.setString(1, condition);
            } else {
                stmt.setString(1, condition + "%");
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String type = rs.getString("type");
                double amount = rs.getDouble("amount");
                if ("Income".equalsIgnoreCase(type)) {
                    totalIncome += amount;
                } else if ("Expense".equalsIgnoreCase(type)) {
                    totalExpense += amount;
                }
            }

            // Get budget from settings
            PreparedStatement budgetStmt = con.prepareStatement("SELECT budget FROM settings LIMIT 1");
            ResultSet budgetRs = budgetStmt.executeQuery();
            if (budgetRs.next()) {
                budget = budgetRs.getDouble("budget");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        incomeLabel.setText("Income: " + totalIncome);
        expenseLabel.setText("Expense: " + totalExpense);
        displayChart(totalIncome,totalExpense, budget);
    }

    private void displayChart(double income, double expense, double budget) {
        chartPanel.removeAll();

        JPanel barChart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();

                double max = Math.max(Math.max(income, expense), budget);
                int barWidth = 100;
                int spacing = 50;
                int baseX = 100;
                int baseY = h - 100;
                int chartHeight = h - 150;

                int incomeBarHeight = (int) ((income / max) * chartHeight);
                int expenseBarHeight = (int) ((expense / max) * chartHeight);
                int budgetBarHeight = (int) ((budget / max) * chartHeight);

                // Income Bar
                g2.setColor(Color.BLUE);
                g2.fillRect(baseX, baseY - incomeBarHeight, barWidth, incomeBarHeight);
                g2.drawString("Income", baseX + 20, baseY + 20);
                g2.drawString(String.valueOf((int) income), baseX + 20, baseY - incomeBarHeight - 10);

                // Expense Bar
                int expenseX = baseX + barWidth + spacing;
                g2.setColor(Color.RED);
                g2.fillRect(expenseX, baseY - expenseBarHeight, barWidth, expenseBarHeight);
                g2.drawString("Expense", expenseX + 20, baseY + 20);
                g2.drawString(String.valueOf((int) expense), expenseX + 20, baseY - expenseBarHeight - 10);

                // Budget Bar
                int budgetX = expenseX + barWidth + spacing;
                g2.setColor(Color.GREEN.darker());
                g2.fillRect(budgetX, baseY - budgetBarHeight, barWidth, budgetBarHeight);
                g2.drawString("Budget", budgetX + 20, baseY + 20);
                g2.drawString(String.valueOf((int) budget), budgetX + 20, baseY - budgetBarHeight - 10);
            }
        };

        chartPanel.setLayout(new BorderLayout());
        chartPanel.add(barChart, BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();
    }


}
