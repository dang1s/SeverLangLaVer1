
package com.sg188.server;

import com.sg188.data.DataCenter;
import com.sg188.real.Char;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.border.Border;

public class JFrameSendBufflevel extends JFrame {

    private javax.swing.JTextField level;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JButton xacnhan;
    private javax.swing.JTextField name;
    private JFrame controlPanel;

    public JFrameSendBufflevel() {
        initComponents();
    }

    private void initComponents() {
        // Thiết lập màu nền cho frame
        getContentPane().setBackground(new Color(10, 10, 15));

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        level = new javax.swing.JTextField();
        name = new javax.swing.JTextField();
        xacnhan = new javax.swing.JButton();

        level.setText("0");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Buff Item Bẩn");

        jLabel1.setText("Tên Nhân Vật");
        jLabel2.setText("Nhập level");
        
        // Thiết lập màu cho các label - màu xanh neon
        Color neonGreen = new Color(0, 255, 190);
        jLabel1.setForeground(neonGreen);
        jLabel2.setForeground(neonGreen);
        
        // Thiết lập màu cho các text field - nền tối, chữ xanh neon
        Color darkBg = new Color(25, 25, 35);
        name.setBackground(darkBg);
        name.setForeground(neonGreen);
        name.setCaretColor(neonGreen);
        name.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 255, 190, 50), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        level.setBackground(darkBg);
        level.setForeground(neonGreen);
        level.setCaretColor(neonGreen);
        level.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 255, 190, 50), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        xacnhan.setText("Xác nhận");
        // Thiết lập màu cho nút xác nhận
        xacnhan.setBackground(darkBg);
        xacnhan.setForeground(neonGreen);
        xacnhan.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 190, 50), 1));
        xacnhan.setFocusPainted(false);
        xacnhan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                xacnhan.setBackground(neonGreen);
                xacnhan.setForeground(Color.BLACK);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                xacnhan.setBackground(darkBg);
                xacnhan.setForeground(neonGreen);
            }
        });
        xacnhan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                xacnhanActionPerformed(evt);
            }

            private void xacnhanActionPerformed(ActionEvent evt) {
                if (name.getText().equals("") || level.getText().equals("")) {
                    JOptionPane.showMessageDialog(rootPane, "Vui lòng nhập đủ các trường!");
                } else {
                    Char p = ServerManager.findCharByName(name.getText());
                    if (p != null) {
                        if (!checkNumber(level.getText())) {
                            JOptionPane.showMessageDialog(rootPane, "Sai định dạng!");
                            return;
                        }
                        try {
                            int levelValue = Integer.parseInt(level.getText());
                            if (levelValue <= 0 || levelValue > 150) {
                                JOptionPane.showMessageDialog(rootPane, "Dữ liệu không đúng ");
                                return;
                            }
                            p.setExp(DataCenter.gI().GetExpFormLevel(levelValue));
                        } catch (NumberFormatException e) {
                            JOptionPane.showMessageDialog(rootPane, "Sai định dạng số!");
                        }
                    } else {
                        JOptionPane.showMessageDialog(rootPane, "Người này không tồn tại hoặc không online!");
                    }
                }
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel1)
                                        .addComponent(jLabel2))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(level)
                                        .addComponent(name, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
                                .addContainerGap(30, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(90, 90, 90)
                                .addComponent(xacnhan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel2)
                                        .addComponent(level, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(30, 30, 30)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(xacnhan))
                                .addContainerGap(30, Short.MAX_VALUE))
        );

        pack();
    }

    public static boolean checkNumber(String str) {
        return str.matches("[0-9]+");
    }

    public static void run() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JFrameSendBufflevel().setVisible(true);
            }
        });
    }
}