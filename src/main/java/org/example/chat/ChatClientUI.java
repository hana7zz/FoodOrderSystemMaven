package org.example.chat;

import javax.swing.*;
import java.awt.event.*;

public class ChatClientUI extends JFrame {
    private JTextArea ClientChatArea;
    private JTextArea ServerChatArea;
    private JTextField ClientInputField;
    private JTextField ServerInputField;
    private JButton ClientSendButton;
    private JButton ServerSendButton;
    private JScrollPane ClientChatBlock;
    private JScrollPane ServerChatBlock;
    private JButton ClearClientButton;
    private JButton ClearServerButton;
    private JLabel Client;
    private JLabel Server;
    private JPanel root;

    private ChatClientConnection connection;

    public ChatClientUI(String host, int port) {
        setTitle("在线订餐");
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setContentPane(root);
        setVisible(true);

        ClientChatBlock.setViewportView(ClientChatArea);
        ServerChatBlock.setViewportView(ServerChatArea);
        ClientChatArea.setEditable(false);
        ServerChatArea.setEditable(false);

        connection = new ChatClientConnection(this, host, port);

        ClientSendButton.addActionListener(e ->
                connection.sendMessage(ClientInputField.getText(), "客人"));
        ServerSendButton.addActionListener(e ->
                connection.sendMessage(ServerInputField.getText(), "酒店"));
        // 添加回车发送监听器
        ClientInputField.addActionListener(e ->
                connection.sendMessage(ClientInputField.getText(), "客人"));
        ServerInputField.addActionListener(e ->
                connection.sendMessage(ServerInputField.getText(), "酒店"));

        ClearClientButton.addActionListener(e -> {
            ClientInputField.setText("");
            ClientChatArea.setText("");
        });

        ClearServerButton.addActionListener(e -> {
            ServerInputField.setText("");
            ServerChatArea.setText("");
        });


        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                connection.close();
                ((JFrame) e.getWindow()).dispose();
            }
        });
    }

    public void appendChat(String sender, String message) {
        SwingUtilities.invokeLater(() -> {
            ClientChatArea.append(message + "\n");
            ServerChatArea.append(message + "\n");
            ClientChatArea.setCaretPosition(ClientChatArea.getDocument().getLength());
            ServerChatArea.setCaretPosition(ServerChatArea.getDocument().getLength());
        });
    }

    public void clearClientInput() {
        ClientInputField.setText("");
    }

    public void clearServerInput() {
        ServerInputField.setText("");
    }
}
