package com.sg188.server;

import Manager.RankingRewardManager;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

/**
 * Dialog Trao Quà Bảng Xếp Hạng - Giao diện Dark Neon
 */
public class RankingRewardDialog extends JDialog {
    
    private JComboBox<String> comboBoxTops;
    private JLabel lblTitle;
    private JLabel lblTop;
    private JLabel lblInfo;
    private JButton btnPhatQua;
    private JButton btnCancel;
    
    public String[] topNames = new String[]{
        "Top Cao Thủ (Top 1)",
        "Top Nạp Nhiều (Top 2)",
        "Top Tài Phú (Top 3)",
        "Top Chuyên Cần (Top 4)",
        "Top Gia Tộc (Top 5)",
        "Top Nhi Đồng (Top 6)",
        "Top Của Cải Tuần (Top 7)",
        "Top Cường Hóa Tuần (Top 8)",
        "Top Chuyên Cần Tuần (Top 9)",
        "Top Cống Hiến Tuần (Top 10)",
        "Top Lôi Đài Tháng (Top 11)",
        "Top Luyện Tập Tuần (Top 12)"
    };
    
    private int selectedTopIndex = -1;
    
    public RankingRewardDialog(JFrame parent) {
        super(parent, true);
        initializeComponent();
    }
    
    private void initializeComponent() {
        this.setTitle("CẤU HÌNH THỜI GIAN SỰ KIỆN");
        
        // --- THÊM ICON CHO CỬA SỔ ---
        try {
            URL iconURL = getClass().getResource("/icon.png");
            if (iconURL != null) {
                ImageIcon img = new ImageIcon(iconURL);
                this.setIconImage(img.getImage());
            }
        } catch (Exception e) {
            System.err.println("Không thể tải icon: " + e.getMessage());
        }

        this.setSize(450, 280); 
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setResizable(false);
        
        // Background màu tối đậm (Deep Dark) theo ảnh
        this.getContentPane().setBackground(new Color(25, 26, 34)); 
        this.setLayout(null);
        
        int yPos = 25;
        
        // Tiêu đề chính màu Xanh Neon
        lblTitle = new JLabel("TRAO QUÀ SỰ KIỆN ĐUA TOP");
        lblTitle.setBounds(0, yPos, 450, 30);
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblTitle.setForeground(new Color(0, 255, 150)); 
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(lblTitle);
        yPos += 55;
        
        // Nhãn "Trao quà:"
        lblTop = new JLabel("Trao quà:");
        lblTop.setBounds(40, yPos, 80, 25);
        lblTop.setFont(new Font("Tahoma", Font.BOLD, 13));
        lblTop.setForeground(Color.WHITE);
        this.add(lblTop);
        
        // ComboBox (Màu xanh xám Slate theo ảnh mẫu)
        comboBoxTops = new JComboBox<>(topNames);
        comboBoxTops.setBounds(130, yPos, 270, 30);
        comboBoxTops.setFont(new Font("Tahoma", Font.PLAIN, 13));
        comboBoxTops.setBackground(new Color(158, 179, 194)); 
        comboBoxTops.setForeground(Color.BLACK);
        comboBoxTops.setSelectedIndex(0);
        comboBoxTops.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedTopIndex = comboBoxTops.getSelectedIndex();
            }
        });
        this.add(comboBoxTops);
        yPos += 45;
        
        // Thông tin ghi chú (Màu ghi sáng)
        lblInfo = new JLabel("<html>• Hệ thống sẽ quét danh sách và gửi vật phẩm qua thư.<br>• Vui lòng kiểm tra kỹ trước khi xác nhận.</html>");
        lblInfo.setBounds(130, yPos, 270, 40);
        lblInfo.setFont(new Font("Tahoma", Font.ITALIC, 11));
        lblInfo.setForeground(new Color(180, 180, 180)); 
        this.add(lblInfo);
        yPos += 65;
        
        // Nút HỦY (Màu trắng, chữ đen, viền mảnh)
        btnCancel = new JButton("HỦY");
        btnCancel.setBounds(50, yPos, 160, 40);
        btnCancel.setBackground(Color.WHITE);
        btnCancel.setForeground(Color.BLACK);
        btnCancel.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnCancel.setFocusPainted(false);
        btnCancel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        this.add(btnCancel);
        
        // Nút XÁC NHẬN (Màu xanh Neon rực rỡ)
        btnPhatQua = new JButton("XÁC NHẬN TRAO");
        btnPhatQua.setBounds(230, yPos, 170, 40);
        btnPhatQua.setBackground(new Color(0, 255, 150));
        btnPhatQua.setForeground(Color.BLACK);
        btnPhatQua.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnPhatQua.setFocusPainted(false);
        btnPhatQua.setBorder(new LineBorder(new Color(0, 200, 120), 1));
        btnPhatQua.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnPhatQua_Click();
            }
        });
        this.add(btnPhatQua);
        
        this.getRootPane().setDefaultButton(btnPhatQua);
    }
    
    private void btnPhatQua_Click() {
        selectedTopIndex = comboBoxTops.getSelectedIndex();
        if (selectedTopIndex < 0) return;
        
        String topName = topNames[selectedTopIndex];
        
        int result = JOptionPane.showConfirmDialog(this,
            "Xác nhận trao quà cho " + topName + "?\nQuà sẽ được gửi ngay lập tức qua thư.",
            "XÁC NHẬN",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (result == JOptionPane.YES_OPTION) {
            btnPhatQua.setEnabled(false);
            btnPhatQua.setText("ĐANG GỬI...");

            new Thread(() -> {
                boolean success = RankingRewardManager.manualRewardTopPlayers(selectedTopIndex, true);
                
                SwingUtilities.invokeLater(() -> {
                    if (success) {
                        JOptionPane.showMessageDialog(this, "✅ Đã gửi quà thành công cho " + topName);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "❌ Lỗi hệ thống khi trao quà!", "LỖI", JOptionPane.ERROR_MESSAGE);
                        btnPhatQua.setEnabled(true);
                        btnPhatQua.setText("XÁC NHẬN TRAO");
                    }
                });
            }).start();
        }
    }

    public int getSelectedTopIndex() {
        return selectedTopIndex;
    }
}