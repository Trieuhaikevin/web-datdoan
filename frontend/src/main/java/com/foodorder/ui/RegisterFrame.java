package com.foodorder.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.foodorder.client.ApiClient;
import com.foodorder.model.RegisterRequestModel;

public class RegisterFrame extends JFrame {
    private final ApiClient apiClient = new ApiClient();

    private JTextField fullNameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField phoneField;
    private JTextField addressField;
    private JButton registerButton;
    private JButton backButton;

    public RegisterFrame() {
        setTitle("Đặt Đồ Ăn Online - Đăng Ký");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);

        JLabel titleLabel = new JLabel("Đăng Ký Tài Khoản");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Họ tên:"), gbc);
        fullNameField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(fullNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Email:"), gbc);
        emailField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Mật khẩu:"), gbc);
        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Số điện thoại:"), gbc);
        phoneField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Địa chỉ:"), gbc);
        addressField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(addressField, gbc);

        registerButton = new JButton("Đăng Ký");
        registerButton.setBackground(new Color(255, 87, 34));
        registerButton.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(registerButton, gbc);

        backButton = new JButton("Quay Lại");
        gbc.gridx = 1;
        panel.add(backButton, gbc);

        registerButton.addActionListener(e -> register());
        backButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        add(panel);
    }

    private void register() {
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Họ tên, email và mật khẩu không được để trống!");
            return;
        }

        try {
            apiClient.post("/auth/register",
                    new RegisterRequestModel(fullName, email, password, phone, address),
                    Object.class);
            JOptionPane.showMessageDialog(this, "Đăng ký thành công!");
            new LoginFrame().setVisible(true);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}
