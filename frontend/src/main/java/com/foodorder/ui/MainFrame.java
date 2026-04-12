package com.foodorder.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import com.foodorder.client.ApiClient;
import com.foodorder.model.CartStore;
import com.foodorder.model.FoodModel;
import com.foodorder.model.UserSession;

public class MainFrame extends JFrame {
    private final ApiClient apiClient = new ApiClient();
    private final DecimalFormat moneyFormat = new DecimalFormat("#,##0");

    private JPanel foodPanel;
    private JButton cartButton;
    private JButton logoutButton;

    public MainFrame() {
        setTitle("Đặt Đồ Ăn Online - Menu");
        setSize(900, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        loadFoods();
    }

    private ImageIcon loadIcon(String path) {
        java.net.URL resource = getClass().getResource(path);
        return resource != null ? new ImageIcon(resource) : new ImageIcon();
    }

    private void initComponents() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(255, 87, 34));
        headerPanel.setPreferredSize(new Dimension(900, 60));

        ImageIcon appIcon = loadIcon("/icons/app.png");
        ImageIcon cartIcon = loadIcon("/icons/cart.png");

        String title = UserSession.getInstance().getFullName() == null
                ? "Đặt Đồ Ăn Online"
                : "Xin chào, " + UserSession.getInstance().getFullName();

        JLabel titleLabel = new JLabel(title, appIcon, SwingConstants.LEFT);
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

        foodPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        foodPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(foodPanel);

        cartButton.addActionListener(e -> new OrderFrame().setVisible(true));

        logoutButton.addActionListener(e -> {
            UserSession.getInstance().clear();
            CartStore.getInstance().clear();
            new LoginFrame().setVisible(true);
            dispose();
        });

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadFoods() {
        foodPanel.removeAll();
        try {
            FoodModel[] foods = apiClient.get("/foods/available", FoodModel[].class);
            if (foods == null || foods.length == 0) {
                foodPanel.setLayout(new BorderLayout());
                JLabel emptyLabel = new JLabel("Hiện chưa có món ăn khả dụng", SwingConstants.CENTER);
                emptyLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                foodPanel.add(emptyLabel, BorderLayout.CENTER);
            } else {
                foodPanel.setLayout(new GridLayout(0, 3, 10, 10));
                for (FoodModel food : foods) {
                    foodPanel.add(createFoodCard(food));
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }

        foodPanel.revalidate();
        foodPanel.repaint();
    }

    private JPanel createFoodCard(FoodModel food) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        card.setBackground(Color.WHITE);

        JLabel iconLabel = new JLabel("", SwingConstants.CENTER);
        iconLabel.setPreferredSize(new Dimension(150, 150));
        
        // Lấy ảnh từ URL nếu có, nếu không dùng icon cứng
        if (food.getImageUrl() != null && !food.getImageUrl().isEmpty()) {
            try {
                java.net.URL imageUrl = new java.net.URL(food.getImageUrl());
                ImageIcon icon = new ImageIcon(imageUrl);
                // Scale ảnh về kích thước phù hợp
                java.awt.Image scaledImage = icon.getImage().getScaledInstance(150, 150, java.awt.Image.SCALE_SMOOTH);
                iconLabel.setIcon(new ImageIcon(scaledImage));
            } catch (Exception e) {
                // Nếu URL không hợp lệ, dùng icon cứng
                String iconPath = getIconPathForFood(food.getName());
                iconLabel.setIcon(loadIcon(iconPath));
            }
        } else {
            String iconPath = getIconPathForFood(food.getName());
            iconLabel.setIcon(loadIcon(iconPath));
        }

        JLabel nameLabel = new JLabel(food.getName(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel priceLabel = new JLabel(formatMoney(food.getPrice()), SwingConstants.CENTER);
        priceLabel.setForeground(new Color(255, 87, 34));

        JLabel stockLabel = new JLabel("Tồn kho: " + food.getStockQuantity(), SwingConstants.CENTER);
        stockLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JButton addButton = new JButton("Thêm vào giỏ");
        addButton.setBackground(new Color(255, 87, 34));
        addButton.setForeground(Color.WHITE);
        addButton.setEnabled(food.getStockQuantity() != null && food.getStockQuantity() > 0);
        addButton.addActionListener(e -> {
            CartStore.getInstance().addOrIncrease(food);
            JOptionPane.showMessageDialog(this, food.getName() + " đã thêm vào giỏ hàng!");
        });

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(iconLabel, BorderLayout.NORTH);
        centerPanel.add(nameLabel, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.add(priceLabel);
        infoPanel.add(stockLabel);
        centerPanel.add(infoPanel, BorderLayout.SOUTH);

        card.add(centerPanel, BorderLayout.CENTER);
        card.add(addButton, BorderLayout.SOUTH);
        card.setPreferredSize(new Dimension(230, 190));

        return card;
    }

    private String formatMoney(BigDecimal price) {
        BigDecimal normalized = price == null ? BigDecimal.ZERO : price;
        return moneyFormat.format(normalized) + "đ";
    }

    private String getIconPathForFood(String name) {
        if (name == null) {
            return "/icons/food.png";
        }
        String normalized = name.toLowerCase();
        if (normalized.contains("pizza")) {
            return "/icons/Pizza.png";
        }
        if (normalized.contains("burger")) {
            return "/icons/Burger.png";
        }
        if (normalized.contains("phở") || normalized.contains("pho")) {
            return "/icons/pho.png";
        }
        if (normalized.contains("cơm") || normalized.contains("com")) {
            return "/icons/comtam.png";
        }
        if (normalized.contains("taco")) {
            return "/icons/tacos.png";
        }
        if (normalized.contains("sushi")) {
            return "/icons/sushi.png";
        }
        return "/icons/food.png";
    }
}
