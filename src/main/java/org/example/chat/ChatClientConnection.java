package org.example.chat;

import javax.swing.*;
import java.io.*;
import java.net.*;

public class ChatClientConnection {
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;
    private volatile boolean running = true;
    private final ChatClientUI ui;

    public ChatClientConnection(ChatClientUI ui, String host, int port) {
        this.ui = ui;
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            new Thread(this::receiveMessages).start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "无法连接服务器: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendMessage(String text, String senderLabel) {
        if (text == null || text.trim().isEmpty()) return;

        if ("/q".equals(text.trim()) && "客人".equals(senderLabel)) {
            JOptionPane.showMessageDialog(null, "订单已结束，聊天窗口即将关闭。");
            new javax.swing.Timer(200, e -> {
                close();
                ui.dispose();
            }).start();
            return;
        }

        if (out != null) {
            out.println(senderLabel + "：" + text.trim());
        }

        if ("客人".equals(senderLabel)) {
            ui.clearClientInput();
        } else {
            ui.clearServerInput();
        }
    }

    private void receiveMessages() {
        try {
            String response;
            while (running && (response = in.readLine()) != null) {
                if (!response.trim().isEmpty()) {
                    ui.appendChat("服务器", response.trim());
                }
            }
        } catch (IOException e) {
            if (running) e.printStackTrace();
        } finally {
            close();
        }
    }

    public void close() {
        running = false;
        try {
            if (out != null) {
                out.println("exit");
                out.flush();
                out.close();
            }
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
