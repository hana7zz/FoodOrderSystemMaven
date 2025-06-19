package org.example.chat;

import java.io.*;
import java.net.*;

public class ChatServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private boolean running = false;

    public static boolean isServerRunning = false; // 静态标志位

    public void start() throws IOException {
        if (isServerRunning) {
            System.out.println("服务器已在运行中。");
            return;
        }

        serverSocket = new ServerSocket(8888);
        isServerRunning = true;
        running = true;
        System.out.println("服务器启动，等待客户端连接...");
        clientSocket = serverSocket.accept();
        System.out.println("客户端已连接");

        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true);

        new Thread(() -> {
            try {
                String msg;
                while (running && (msg = in.readLine()) != null) {
                    System.out.println("收到客户端消息: " + msg);
                    if ("exit".equalsIgnoreCase(msg.trim())) {
                        System.out.println("收到退出指令，服务器将关闭");
                        stop();
                        break;
                    }
                    out.println(msg); // 回显
                }
            } catch (IOException e) {
                if (running) e.printStackTrace();
            }
        }).start();
    }

    public void stop() {
        running = false;
        isServerRunning = false;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
            if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
            System.out.println("服务器已关闭");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
