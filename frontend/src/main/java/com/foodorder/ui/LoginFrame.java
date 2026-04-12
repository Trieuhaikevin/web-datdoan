package com.foodorder.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.foodorder.client.ApiClient;
import com.foodorder.model.LoginRequestModel;
import com.foodorder.model.LoginResponse;
import com.foodorder.model.UserSession;

public class LoginFrame extends JFrame {
    private final ApiClient apiClient = new ApiClient();

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    public LoginFrame() {
        setTitle("Đặt Đồ Ăn Online - Đăng Nhập");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        java.net.URL iconUrl = getClass().getResource("/icons/app.png");
        if (iconUrl != null) {
            setIconImage(new ImageIcon(iconUrl).getImage());
        }
        initComponents();
    }

    private ImageIcon loadIcon(String path) {
        java.net.URL resource = getClass().getResource(path);
        return resource != null ? new ImageIcon(resource) : new ImageIcon();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Đặt Đồ Ăn Online", loadIcon("/icons/app.png"), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setIconTextGap(10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Email:"), gbc);
        emailField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Mật khẩu:"), gbc);
        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        loginButton = new JButton("Đăng Nhập");
        loginButton.setBackground(new Color(255, 87, 34));
        loginButton.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(loginButton, gbc);

        registerButton = new JButton("Đăng Ký");
        gbc.gridx = 1;
        panel.add(registerButton, gbc);

        loginButton.addActionListener(e -> login());

        registerButton.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            dispose();
        });

        add(panel);
    }

    private void login() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email và mật khẩu không được để trống!");
            return;
        }

        try {
            LoginResponse response = apiClient.post("/auth/login", new LoginRequestModel(email, password), LoginResponse.class);
            UserSession.getInstance().setUser(
                    response.getUserId(),
                    response.getFullName(),
                    response.getEmail(),
                    response.getRole(),
                    response.getPhone(),
                    response.getAddress()
            );

            if (UserSession.getInstance().isAdmin()) {
                new AdminFrame().setVisible(true);
            } else {
                new MainFrame().setVisible(true);
            }
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
