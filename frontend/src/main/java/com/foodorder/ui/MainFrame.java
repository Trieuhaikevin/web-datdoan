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
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.foodorder.client.ApiClient;
import com.foodorder.model.CartStore;
import com.foodorder.model.FoodModel;
import com.foodorder.model.NotificationStore;
import com.foodorder.model.OrderModel;
import com.foodorder.model.UserSession;

public class MainFrame extends JFrame {
    private final ApiClient apiClient = new ApiClient();
    private final DecimalFormat moneyFormat = new DecimalFormat("#,##0");

    private JPanel foodPanel;
    private JButton cartButton;
    private JButton logoutButton;
    private Timer orderPollTimer;
    private Long lastNotifiedOrderId;
    private String lastNotifiedStatus;
    private final NotificationStore notificationStore = new NotificationStore();

    public MainFrame() {
        setTitle("Đặt Đồ Ăn Online - Menu");
        setSize(900, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        loadFoods();
        startOrderPolling();
    }

    private void startOrderPolling() {
        Long userId = UserSession.getInstance().getUserId();
        if (userId == null) {
            return;
        }
        // poll every 10 seconds
        orderPollTimer = new Timer(10000, e -> {
            new Thread(() -> {
                try {
                    OrderModel latest = apiClient.get("/orders/user/" + userId + "/latest", OrderModel.class);
                    if (latest != null) {
                        Long id = latest.getId();
                        String status = latest.getStatus();
                        boolean changed = false;
                        if (lastNotifiedOrderId == null || !lastNotifiedOrderId.equals(id)) {
                            changed = true;
                        } else if (lastNotifiedStatus == null || !lastNotifiedStatus.equals(status)) {
                            changed = true;
                        }
                        if (changed) {
                            // If this order/status was already acknowledged, skip notifying
                            if (notificationStore.isAcknowledged(id, status)) {
                                lastNotifiedOrderId = id;
                                lastNotifiedStatus = status;
                            } else {
                                lastNotifiedOrderId = id;
                                lastNotifiedStatus = status;
                                SwingUtilities.invokeLater(() -> {
                                    JOptionPane.showMessageDialog(this,
                                            "Đơn " + (id != null ? "#" + id : "") + " thay đổi trạng thái: " + status);
                                });
                                // Persist acknowledgement so the same order+status won't notify again
                                notificationStore.acknowledge(id, status);
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
            }).start();
        });
        orderPollTimer.setInitialDelay(3000);
        orderPollTimer.start();
    }

    // Small quantity selector with - [value] + layout
    private static class QuantitySelector extends javax.swing.JPanel {
        private final javax.swing.JButton minusBtn = new javax.swing.JButton("-");
        private final javax.swing.JButton plusBtn = new javax.swing.JButton("+");
        private final javax.swing.JTextField valueField = new javax.swing.JTextField();
        private int max;

        public QuantitySelector(int initial, int max) {
            this.max = Math.max(1, max);
            setLayout(new java.awt.BorderLayout());
            minusBtn.setFocusable(false);
            plusBtn.setFocusable(false);
            valueField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
            valueField.setEditable(false);
            valueField.setText(String.valueOf(Math.max(1, Math.min(initial, this.max))));
            minusBtn.addActionListener(e -> decrement());
            plusBtn.addActionListener(e -> increment());
            add(minusBtn, java.awt.BorderLayout.WEST);
            add(valueField, java.awt.BorderLayout.CENTER);
            add(plusBtn, java.awt.BorderLayout.EAST);
            setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200,200,200)));
        }

        private void decrement() {
            int v = getValue();
            if (v > 1) {
                v--;
                valueField.setText(String.valueOf(v));
            }
        }

        private void increment() {
            int v = getValue();
            if (v < max) {
                v++;
                valueField.setText(String.valueOf(v));
            }
        }

        public int getValue() {
            try {
                return Integer.parseInt(valueField.getText());
            } catch (Exception e) {
                return 1;
            }
        }

        public void setMax(int max) {
            this.max = Math.max(1, max);
            int v = getValue();
            if (v > this.max) {
                valueField.setText(String.valueOf(this.max));
            }
        }
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
                // Group foods by categoryName (preserve insertion order)
                java.util.Map<String, java.util.List<FoodModel>> groups = new java.util.LinkedHashMap<>();
                for (FoodModel food : foods) {
                    String cat = food.getCategoryName() == null ? "Những món khác" : food.getCategoryName();
                    groups.computeIfAbsent(cat, k -> new java.util.ArrayList<>()).add(food);
                }

                foodPanel.setLayout(new javax.swing.BoxLayout(foodPanel, javax.swing.BoxLayout.Y_AXIS));
                for (java.util.Map.Entry<String, java.util.List<FoodModel>> entry : groups.entrySet()) {
                    String catName = entry.getKey();
                    java.util.List<FoodModel> list = entry.getValue();

                    JPanel section = new JPanel(new BorderLayout());
                    section.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

                    JLabel header = new JLabel(catName);
                    header.setFont(new Font("Arial", Font.BOLD, 16));
                    header.setBorder(BorderFactory.createEmptyBorder(0, 8, 6, 0));
                    section.add(header, BorderLayout.NORTH);

                    JPanel grid = new JPanel(new GridLayout(0, 3, 10, 10));
                    for (FoodModel food : list) {
                        grid.add(createFoodCard(food));
                    }
                    section.add(grid, BorderLayout.CENTER);

                    foodPanel.add(section);
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
        iconLabel.setPreferredSize(new Dimension(100, 80));
        
        // Lấy ảnh từ URL nếu có, nếu không dùng icon cứng
        if (food.getImageUrl() != null && !food.getImageUrl().isEmpty()) {
            try {
                java.net.URL imageUrl = new java.net.URL(food.getImageUrl());
                ImageIcon icon = new ImageIcon(imageUrl);
                // Scale ảnh về kích thước nhỏ hơn để tránh che chữ
                java.awt.Image scaledImage = icon.getImage().getScaledInstance(100, 70, java.awt.Image.SCALE_SMOOTH);
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

        JLabel nameLabel = new JLabel(formatHtml(food.getName()), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 13));

        // category label removed from individual cards (categories shown as sections)

        JLabel priceLabel = new JLabel(formatMoney(food.getPrice()), SwingConstants.CENTER);
        priceLabel.setForeground(new Color(255, 87, 34));

        JLabel stockLabel = new JLabel("Tồn kho: " + food.getStockQuantity(), SwingConstants.CENTER);
        stockLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        int maxQty = food.getStockQuantity() == null ? 1 : food.getStockQuantity();
        QuantitySelector quantitySelector = new QuantitySelector(1, maxQty);
        quantitySelector.setPreferredSize(new Dimension(120, 28));

        JButton addButton = new JButton("Thêm vào giỏ");
        addButton.setBackground(new Color(255, 87, 34));
        addButton.setForeground(Color.WHITE);
        addButton.setEnabled(food.getStockQuantity() != null && food.getStockQuantity() > 0);
        addButton.addActionListener(e -> {
            int quantity = quantitySelector.getValue();
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0");
                return;
            }
            if (food.getStockQuantity() != null && quantity > food.getStockQuantity()) {
                JOptionPane.showMessageDialog(this, "Số lượng vượt quá tồn kho");
                return;
            }
            CartStore.getInstance().addOrIncrease(food, quantity);
            JOptionPane.showMessageDialog(this, food.getName() + " đã thêm vào giỏ hàng!");
        });

        // Vertical layout so name sits clearly under image and can wrap
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new javax.swing.BoxLayout(centerPanel, javax.swing.BoxLayout.Y_AXIS));
        iconLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        nameLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        centerPanel.add(iconLabel);
        centerPanel.add(javax.swing.Box.createVerticalStrut(6));
        centerPanel.add(nameLabel);
        centerPanel.add(javax.swing.Box.createVerticalStrut(6));

        JPanel infoPanel = new JPanel(new GridLayout(3, 1));
        infoPanel.add(priceLabel);
        infoPanel.add(stockLabel);
        infoPanel.add(quantitySelector);
        centerPanel.add(infoPanel);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(addButton, BorderLayout.CENTER);

        card.add(centerPanel, BorderLayout.CENTER);
        card.add(bottomPanel, BorderLayout.SOUTH);
        card.setPreferredSize(new Dimension(230, 240));

        return card;
    }

    private String formatMoney(BigDecimal price) {
        BigDecimal normalized = price == null ? BigDecimal.ZERO : price;
        return moneyFormat.format(normalized) + "đ";
    }

    private String formatHtml(String text) {
        if (text == null) {
            return "";
        }
        String escaped = text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\n", "<br/>");
        return "<html><div style='text-align:center;width:200px;white-space:normal;'>" + escaped + "</div></html>";
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
