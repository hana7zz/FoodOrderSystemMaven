package org.example;

import org.apache.ibatis.session.SqlSession;
import org.example.chat.ChatClientUI;
import org.example.chat.ChatServer;
import org.example.mybatis.MyBatisUtil;
import org.example.mybatis.UserMapper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class Order{
    private JPanel orderForm;
    private JTextField roomNumberTxt;
    private JTextField dateTxt;
    private JTextField foodNumberTxt;
    private JButton confirmButton;
    private JButton cancelButton;
    private JRadioButton breakfastRadioButton;
    private JRadioButton lunchRadioButton;
    private JRadioButton dinnerRadioButton;
    private JTable infoTable;
    private JScrollPane demo;
    private JButton fetchAllButton;
    private JButton updateButton;
    private JButton clearButton;
    private JButton OnlineOrderButton;

    private DefaultTableModel tableModel;   // 表格模型
    private ButtonGroup timeButtonGroup;    // 用餐时间单选按钮分组
    private static boolean chatStarted = false;


    public Order() {
        // 把三个时间段的单选按钮加入分组，确保只能选一个
        timeButtonGroup = new ButtonGroup();
        timeButtonGroup.add(breakfastRadioButton);
        timeButtonGroup.add(lunchRadioButton);
        timeButtonGroup.add(dinnerRadioButton);

        setupTable();  // 设置表格样式
        setupFrame(); // 创建并显示窗体
        setupListeners(); // 添加监听器
    }


    // 设置订单信息表格的样式与模型
    private void setupTable() {
        String[] columns = {"订单编号", "房间编号", "日期", "食物编号", "何时配送", "价格"};
        tableModel = new DefaultTableModel(columns, 0);
        infoTable.setModel(tableModel);
        infoTable.setRowHeight(40);
        JTableHeader header = infoTable.getTableHeader();
        header.setFont(new Font("微软雅黑", Font.BOLD, 24));
        header.setPreferredSize(new Dimension(header.getWidth(), 50));
    }

    private void setupFrame() {
        JFrame frame = new JFrame("酒店餐饮订单管理");
        frame.setContentPane(orderForm);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1900, 1300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    //  添加各类按钮和表格选择的事件监听器
    private void setupListeners() {
        confirmButton.addActionListener(e -> handleConfirm());
        cancelButton.addActionListener(e -> handleDelete());
        fetchAllButton.addActionListener(e -> handleFetchAll());
        updateButton.addActionListener(e -> handleUpdate());
        clearButton.addActionListener(e -> clearInputs());
        OnlineOrderButton.addActionListener(e -> {
            if (chatStarted) {
                JOptionPane.showMessageDialog(null, "已有订单进行中，请先完成当前订单！");
                return;
            }

            chatStarted = true;

            // 启动服务器线程
            new Thread(() -> {
                try {
                    ChatServer.main(null);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();

            // 延迟后启动客户端
            try {
                Thread.sleep(300); // 可视情况调整
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            // 启动客户端并设置窗口关闭时释放标志位
            SwingUtilities.invokeLater(() -> {
                ChatClientUI ui = new ChatClientUI("127.0.0.1", 8888);
                ui.setVisible(true);

                // 监听窗口关闭，释放标志位
                ui.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent e) {
                        chatStarted = false;
                    }
                });
            });
        });

        // 监听表格行点击事件，自动填充输入框
        infoTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = infoTable.getSelectedRow();
            if (selectedRow != -1) {
                roomNumberTxt.setText((String) tableModel.getValueAt(selectedRow, 1));
                dateTxt.setText((String) tableModel.getValueAt(selectedRow, 2));
                foodNumberTxt.setText((String) tableModel.getValueAt(selectedRow, 3));
                String time = (String) tableModel.getValueAt(selectedRow, 4);
                switch (time) {
                    case "早上": breakfastRadioButton.setSelected(true); break;
                    case "中午": lunchRadioButton.setSelected(true); break;
                    case "晚上": dinnerRadioButton.setSelected(true); break;
                }
            }
        });

    }

    private void handleConfirm() {
        String room = roomNumberTxt.getText().trim();
        String date = dateTxt.getText().trim();
        String food = foodNumberTxt.getText().trim();

        if (room.isEmpty() || date.isEmpty() || food.isEmpty()) {
            showMessage("请填写完整信息！");
            return;
        }

        String time = "";
        String price = "";
        if (breakfastRadioButton.isSelected()) {
            time = "早上";
            price = "10";
        } else if (lunchRadioButton.isSelected()) {
            time = "中午";
            price = "25";
        } else if (dinnerRadioButton.isSelected()) {
            time = "晚上";
            price = "20";
        } else {
            showMessage("请选择用餐时间！");
            return;
        }

        User user = new User(room, date, food, time, price);

        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            mapper.insert(user);
            int id = user.getId();
            if (id != -1) {
                tableModel.addRow(new Object[]{id, room, date, food, time, price});
                clearInputs();
            } else {
                showMessage("订单插入失败");
            }
        }
    }

    private void handleDelete() {
        int selectedRow = infoTable.getSelectedRow();
        if (selectedRow != -1) {
            int orderId = (int) tableModel.getValueAt(selectedRow, 0);
            try (SqlSession session = MyBatisUtil.getSqlSession()) {
                UserMapper mapper = session.getMapper(UserMapper.class);
                mapper.delete(orderId);
                tableModel.removeRow(selectedRow);
                clearInputs();
            }
        } else {
            showMessage("请选择要删除的订单");
        }
    }

    private void handleUpdate() {
        int selectedRow = infoTable.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String room = roomNumberTxt.getText().trim();
            String date = dateTxt.getText().trim();
            String food = foodNumberTxt.getText().trim();

            if (room.isEmpty() || date.isEmpty() || food.isEmpty()) {
                showMessage("请填写完整信息！");
                return;
            }

            String time = "";
            String price = "";
            if (breakfastRadioButton.isSelected()) {
                time = "早上"; price = "10";
            } else if (lunchRadioButton.isSelected()) {
                time = "中午"; price = "25";
            } else if (dinnerRadioButton.isSelected()) {
                time = "晚上"; price = "20";
            } else {
                showMessage("请选择用餐时间！");
                return;
            }

            User user = new User(room, date, food, time, price);
            user.setId(id); // 设置 ID 以便 MyBatis 知道更新哪条记录

            try (SqlSession session = MyBatisUtil.getSqlSession()) {
                UserMapper mapper = session.getMapper(UserMapper.class);
                mapper.update(user);

                tableModel.setValueAt(room, selectedRow, 1);
                tableModel.setValueAt(date, selectedRow, 2);
                tableModel.setValueAt(food, selectedRow, 3);
                tableModel.setValueAt(time, selectedRow, 4);
                tableModel.setValueAt(price, selectedRow, 5);

                clearInputs();
                showMessage("订单修改成功！");
            }
        } else {
            showMessage("请选择要修改的订单");
        }
    }

    private void handleFetchAll() {
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            List<User> orders = mapper.fetchAll();

            tableModel.setRowCount(0); // 清空表格
            for (User u : orders) {
                tableModel.addRow(new Object[]{
                        u.getId(), u.getRoomNumber(), u.getDate(),
                        u.getFoodNumber(), u.getTime(), u.getPrice()
                });
            }
        }
    }


    // 清空所有输入
    private void clearInputs() {
        roomNumberTxt.setText("");
        dateTxt.setText("");
        foodNumberTxt.setText("");
        timeButtonGroup.clearSelection();
    }

    // 显示提示信息
    private void showMessage(String msg) {
        JOptionPane.showMessageDialog(orderForm, msg, "提示", JOptionPane.INFORMATION_MESSAGE);
    }


    public static void main(String[] args) {
        new Order();
    }
}