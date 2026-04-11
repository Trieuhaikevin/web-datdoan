package com.foodorder.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

public class MainFrame extends JFrame {
    private JPanel foodPanel;
    private JButton cartButton;
    private JButton logoutButton;

    public MainFrame() {
        setTitle("Đặt Đồ Ăn Online - Menu");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private ImageIcon loadIcon(String path) {
        java.net.URL resource = getClass().getResource(path);
        return resource != null ? new ImageIcon(resource) : new ImageIcon();
    }

    private void initComponents() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(255, 87, 34));
        headerPanel.setPreferredSize(new Dimension(800, 60));

        final ImageIcon appIcon = loadIcon("/icons/app.png");
        final ImageIcon cartIcon = loadIcon("/icons/cart.png");

        JLabel titleLabel = new JLabel("Đặt Đồ Ăn Online", appIcon, SwingConstants.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setIconTextGap(10);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(255, 87, 34));

        cartButton = new JButton("Giỏ hàng", cartIcon);
        cartButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        logoutButton = new JButton("Đăng xuất");
        buttonPanel.add(cartButton);
        buttonPanel.add(logoutButton);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        // Food Grid
        foodPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        foodPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Sample foods
        final String[] foods = {"Pizza", "Burger", "Phở", 
                         "Cơm tấm", "Tacos", "Sushi"};
        final String[] prices = {"85,000đ", "65,000đ", "55,000đ",
                          "45,000đ", "75,000đ", "120,000đ"};

        for (int i = 0; i < foods.length; i++) {
            foodPanel.add(createFoodCard(foods[i], prices[i]));
        }

        JScrollPane scrollPane = new JScrollPane(foodPanel);

        // Actions
        cartButton.addActionListener(e -> {
            new OrderFrame().setVisible(true);
        });

        logoutButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void addToCart(String name, String price) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(OrderFrame.CART_FILE, true))) {
            writer.println(name + "|" + price + "|1|" + price);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu giỏ hàng: " + e.getMessage());
        }
    }

    private JPanel createFoodCard(String name, String price) {
        final JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        card.setBackground(Color.WHITE);

        // Load specific icon for each food
        final String iconPath = getIconPathForFood(name);
        final ImageIcon foodIcon = loadIcon(iconPath);
        final JLabel iconLabel = new JLabel(foodIcon, SwingConstants.CENTER);
        iconLabel.setPreferredSize(new Dimension(64, 64));

        final JLabel nameLabel = new JLabel(name, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));

        final JLabel priceLabel = new JLabel(price, SwingConstants.CENTER);
        priceLabel.setForeground(new Color(255, 87, 34));

        final JButton addButton = new JButton("Thêm vào giỏ");
        addButton.setBackground(new Color(255, 87, 34));
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> {
            addToCart(name, price);
            JOptionPane.showMessageDialog(this, name + " đã thêm vào giỏ hàng!");
        });

        final JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(iconLabel, BorderLayout.NORTH);
        centerPanel.add(nameLabel, BorderLayout.CENTER);
        centerPanel.add(priceLabel, BorderLayout.SOUTH);

        card.add(centerPanel, BorderLayout.CENTER);
        card.add(addButton, BorderLayout.SOUTH);
        card.setPreferredSize(new Dimension(200, 180));

        return card;
    }

    private String getIconPathForFood(String name) {
        switch (name) {
            case "Pizza": return "/icons/Pizza.png";
            case "Burger": return "/icons/Burger.png";
            case "Phở": return "/icons/pho.png";
            case "Cơm tấm": return "/icons/comtam.png";
            case "Tacos": return "/icons/tacos.png";
            case "Sushi": return "/icons/sushi.png";
            default: return "/icons/food.png";
        }
    }
}