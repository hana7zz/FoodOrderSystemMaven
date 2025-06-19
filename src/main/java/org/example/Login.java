package org.example;

import javax.swing.*;

public class Login {
    private JPanel loginForm; // 窗口的主面板
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    // 构造函数 创建对象时进行初始化
    public Login() {
        // // 创建并显示窗体
        setupFrame();

        // 监听loginButton
        loginButton.addActionListener(e -> handleLogin());
    }

    private void setupFrame() {
        JFrame frame = new JFrame("登录");
        frame.setContentPane(loginForm);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null); // 窗口居中显示
        frame.setVisible(true);
    }

    private void handleLogin() {
        // 获取输入的用户名和密码(去除首尾空格)
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (isValidLogin(username, password)) {
            showMessage("登录成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
            closeWindow();
            new org.example.Order(); // 进入主界面
        } else {
            showMessage("用户名或密码错误！", "错误", JOptionPane.ERROR_MESSAGE);
            usernameField.setText("");    // 清空用户名
            passwordField.setText("");    // 清空密码
            usernameField.requestFocus(); // 焦点重新放到用户名输入框
        }
    }

    private boolean isValidLogin(String user, String pass) {
        return "admin".equals(user) && "123456".equals(pass);
    }

    private void showMessage(String msg, String title, int type) {
        JOptionPane.showMessageDialog(loginForm, msg, title, type);
    }

    private void closeWindow() {
        // 获取登录表单的父级JFrame窗口
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(loginForm);
        if (frame != null) {
            frame.dispose();
        }
    }

    public static void main(String[] args) {
        new Login();
    }
}
