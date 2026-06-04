package com.sg188.server;

import com.sg188.lib.Log;
import com.sg188.real.Char;
import com.sg188.real.Item;
import com.sg188.server.ServerManager;
import com.sg188.data.DataCenter;
import Template.TemplateThu;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;

public class JFrameSendItem extends JFrame {

    private JTextField idItem;
    private JTextField vang;
    private JTextField bac;
    private JTextField backhoa;
    private JTextField vangkhoa;
    private JComboBox<String> nameComboBox;
    private JTextField quantity;
    private JTextField strOptions; // Thêm trường nhập options
    private JTextField titleThu; // Tiêu đề thư
    private JTextField nameNguoiGui; // Tên người gửi
    private JTextField noiDungThu; // Nội dung thư
    private JTextField soNgayThu; // Số ngày hết hạn thư
    private JRadioButton radioTrucTiep; // Gửi trực tiếp
    private JRadioButton radioHoMThu; // Gửi qua hòm thư
    private ButtonGroup groupSendMethod; // Nhóm radio button
    private JPanel panelHoMThu; // Panel chứa các trường hòm thư
    private JButton xacnhan;
    private JLabel jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6, jLabel7, jLabel8, jLabel9;
    private JLabel itemNameLabel; // Label hiển thị tên item
    private JLabel optionsExampleLabel; // Label hiển thị ví dụ options
    private JPanel panelTaiNguyen; // Panel chứa các trường tài nguyên
    private JButton btnAddTaiNguyen; // Nút thêm tài nguyên
    private java.util.Set<String> selectedTaiNguyen; // Set các loại tài nguyên đã chọn

    public JFrameSendItem() {
        initComponents();
    }

    private void initComponents() {
        // Thiết lập màu nền cho frame
        getContentPane().setBackground(new Color(10, 10, 15));
        jLabel1 = new JLabel("Tên");
        jLabel2 = new JLabel("ID Item");
        jLabel3 = new JLabel("Số lượng");
        jLabel4 = new JLabel("Bạc Khóa");
        jLabel5 = new JLabel("Bạc");
        jLabel6 = new JLabel("Vàng");
        jLabel7 = new JLabel("Send Item");
        jLabel8 = new JLabel("Vàng Khóa");
        jLabel9 = new JLabel("Options");
        
        // Label hiển thị tên item
        itemNameLabel = new JLabel("");
        itemNameLabel.setForeground(new Color(0, 255, 190, 180)); // Màu xanh neon mờ hơn
        itemNameLabel.setFont(new Font("Consolas", Font.ITALIC, 11));
        
        // Label hiển thị ví dụ options
        optionsExampleLabel = new JLabel("<html><font color='#00FFBE' size='2'>VD: 57,0,500;62,0,500 hoặc để trống</font></html>");
        optionsExampleLabel.setFont(new Font("Consolas", Font.ITALIC, 9));
        
        // Thiết lập màu cho các label - màu xanh neon
        Color neonGreen = new Color(0, 255, 190);
        jLabel1.setForeground(neonGreen);
        jLabel2.setForeground(neonGreen);
        jLabel3.setForeground(neonGreen);
        jLabel4.setForeground(neonGreen);
        jLabel5.setForeground(neonGreen);
        jLabel6.setForeground(neonGreen);
        jLabel7.setForeground(neonGreen);
        jLabel8.setForeground(neonGreen);
        jLabel7.setFont(new Font("Consolas", Font.BOLD, 18));

        quantity = new JTextField("1");
        bac = new JTextField("0");
        vang = new JTextField("0");
        backhoa = new JTextField("0");
        vangkhoa = new JTextField("0");
        idItem = new JTextField("0");
        strOptions = new JTextField(""); // Options cho item
        titleThu = new JTextField("Quà từ Admin");
        nameNguoiGui = new JTextField("Hệ thống");
        noiDungThu = new JTextField("Admin gửi tặng bạn quà!");
        soNgayThu = new JTextField("7"); // Mặc định 7 ngày
        
        // Thiết lập màu cho các text field - nền tối, chữ xanh neon
        Color darkBg = new Color(25, 25, 35);
        for (JTextField field : new JTextField[]{quantity, bac, vang, backhoa, vangkhoa, idItem, strOptions, titleThu, nameNguoiGui, noiDungThu, soNgayThu}) {
            field.setBackground(darkBg);
            field.setForeground(neonGreen);
            field.setCaretColor(neonGreen);
            field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 255, 190, 50), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
        }
        
        // Tạo RadioButton để chọn cách gửi
        radioTrucTiep = new JRadioButton("Gửi trực tiếp", true);
        radioHoMThu = new JRadioButton("Gửi qua hòm thư", false);
        radioTrucTiep.setForeground(neonGreen);
        radioHoMThu.setForeground(neonGreen);
        radioTrucTiep.setBackground(new Color(10, 10, 15));
        radioHoMThu.setBackground(new Color(10, 10, 15));
        radioTrucTiep.setOpaque(true);
        radioHoMThu.setOpaque(true);
        
        groupSendMethod = new ButtonGroup();
        groupSendMethod.add(radioTrucTiep);
        groupSendMethod.add(radioHoMThu);
        
        // Tạo mainPanel trước để có thể sử dụng trong listener
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(10, 10, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel chứa các trường hòm thư (ẩn mặc định)
        panelHoMThu = new JPanel();
        panelHoMThu.setLayout(new GridLayout(4, 2, 10, 10));
        panelHoMThu.setBackground(new Color(10, 10, 15));
        panelHoMThu.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0, 255, 190, 50), 1),
            "Thông tin hòm thư",
            0, 0,
            new Font("Consolas", Font.BOLD, 12),
            neonGreen
        ));
        
        JLabel labelTitle = new JLabel("Tiêu đề:");
        labelTitle.setForeground(neonGreen);
        panelHoMThu.add(labelTitle);
        panelHoMThu.add(titleThu);
        
        JLabel labelNguoiGui = new JLabel("Người gửi:");
        labelNguoiGui.setForeground(neonGreen);
        panelHoMThu.add(labelNguoiGui);
        panelHoMThu.add(nameNguoiGui);
        
        JLabel labelNoiDung = new JLabel("Nội dung:");
        labelNoiDung.setForeground(neonGreen);
        panelHoMThu.add(labelNoiDung);
        panelHoMThu.add(noiDungThu);
        
        JLabel labelSoNgay = new JLabel("Số ngày:");
        labelSoNgay.setForeground(neonGreen);
        panelHoMThu.add(labelSoNgay);
        panelHoMThu.add(soNgayThu);
        
        // Ẩn panel hòm thư mặc định
        panelHoMThu.setVisible(false);
        panelHoMThu.setMaximumSize(new Dimension(0, 0)); // Để không chiếm không gian khi ẩn
        
        // Không cần listener vì panel hòm thư sẽ luôn ẩn
        
        // Thêm listener để hiển thị tên item khi nhập ID
        idItem.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateItemName();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateItemName();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateItemName();
            }
        });
        
        // Lấy danh sách người chơi đang online
        java.util.List<String> onlinePlayers = new java.util.ArrayList<>();
        try {
            java.util.List<Char> chars = ServerManager.getChars();
            for (Char ch : chars) {
                if (ch != null && ch.Info != null && ch.Info.name != null && !ch.Info.name.isEmpty()) {
                    onlinePlayers.add(ch.Info.name);
                }
            }
            // Sắp xếp theo tên
            java.util.Collections.sort(onlinePlayers);
        } catch (Exception e) {
            Log.error("Lỗi khi lấy danh sách người chơi online: " + e.getMessage());
        }
        
        // Tạo ComboBox với danh sách người chơi online
        nameComboBox = new JComboBox<>(onlinePlayers.toArray(new String[0]));
        nameComboBox.setEditable(true); // Cho phép gõ để tìm kiếm
        nameComboBox.setBackground(darkBg);
        nameComboBox.setForeground(neonGreen);
        nameComboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 255, 190, 50), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        // Thiết lập màu cho ComboBox editor
        JTextField editor = (JTextField) nameComboBox.getEditor().getEditorComponent();
        editor.setBackground(darkBg);
        editor.setForeground(neonGreen);
        editor.setCaretColor(neonGreen);
        
        xacnhan = new JButton("Xác nhận");
        // Thiết lập màu cho nút xác nhận
        xacnhan.setBackground(new Color(25, 25, 35));
        xacnhan.setForeground(neonGreen);
        xacnhan.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 190, 50), 1));
        xacnhan.setFocusPainted(false);
        xacnhan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                xacnhan.setBackground(neonGreen);
                xacnhan.setForeground(Color.BLACK);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                xacnhan.setBackground(new Color(25, 25, 35));
                xacnhan.setForeground(neonGreen);
            }
        });

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Send Item");

        xacnhan.addActionListener(evt -> xacnhanActionPerformed(evt));

        // Sử dụng layout đơn giản hơn với BorderLayout và JPanel
        getContentPane().setLayout(new BorderLayout());
        
        // Tiêu đề
        JLabel titleLabel = new JLabel("Send Item");
        titleLabel.setFont(new Font("Consolas", Font.BOLD, 18));
        titleLabel.setForeground(neonGreen);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(15));
        
        // Panel chọn cách gửi
        JPanel panelMethod = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelMethod.setBackground(new Color(10, 10, 15));
        JLabel labelMethod = new JLabel("Cách gửi:");
        labelMethod.setForeground(neonGreen);
        panelMethod.add(labelMethod);
        panelMethod.add(radioTrucTiep);
        panelMethod.add(radioHoMThu);
        mainPanel.add(panelMethod);
        mainPanel.add(Box.createVerticalStrut(10));
        
        // Panel thông tin cơ bản
        JPanel panelInfo = new JPanel(new GridLayout(0, 2, 10, 10));
        panelInfo.setBackground(new Color(10, 10, 15));
        panelInfo.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0, 255, 190, 50), 1),
            "Thông tin",
            0, 0,
            new Font("Consolas", Font.BOLD, 12),
            neonGreen
        ));
        
        panelInfo.add(jLabel1);
        panelInfo.add(nameComboBox);
        panelInfo.add(jLabel2);
        JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        itemPanel.setBackground(new Color(10, 10, 15));
        itemPanel.add(idItem);
        itemPanel.add(itemNameLabel);
        panelInfo.add(itemPanel);
        panelInfo.add(jLabel3);
        panelInfo.add(quantity);
        panelInfo.add(jLabel9);
        JPanel optionsPanel = new JPanel(new BorderLayout(5, 0));
        optionsPanel.setBackground(new Color(10, 10, 15));
        optionsPanel.add(strOptions, BorderLayout.CENTER);
        optionsPanel.add(optionsExampleLabel, BorderLayout.SOUTH);
        panelInfo.add(optionsPanel);
        
        mainPanel.add(panelInfo);
        mainPanel.add(Box.createVerticalStrut(10));
        
        // Panel Tài nguyên - tách riêng ra ngoài GridLayout để có thể mở rộng
        JPanel panelTaiNguyenContainer = new JPanel();
        panelTaiNguyenContainer.setLayout(new BoxLayout(panelTaiNguyenContainer, BoxLayout.Y_AXIS));
        panelTaiNguyenContainer.setBackground(new Color(10, 10, 15));
        panelTaiNguyenContainer.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0, 255, 190, 50), 1),
            "Tài nguyên",
            0, 0,
            new Font("Consolas", Font.BOLD, 12),
            neonGreen
        ));
        
        // Tạo panel tài nguyên với nút thêm
        panelTaiNguyen = new JPanel();
        panelTaiNguyen.setLayout(new BoxLayout(panelTaiNguyen, BoxLayout.Y_AXIS));
        panelTaiNguyen.setBackground(new Color(10, 10, 15));
        panelTaiNguyen.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Nút thêm tài nguyên
        btnAddTaiNguyen = new JButton("+");
        btnAddTaiNguyen.setBackground(new Color(25, 25, 35));
        btnAddTaiNguyen.setForeground(neonGreen);
        btnAddTaiNguyen.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 190, 50), 1));
        btnAddTaiNguyen.setFocusPainted(false);
        btnAddTaiNguyen.setPreferredSize(new Dimension(30, 25));
        btnAddTaiNguyen.setFont(new Font("Consolas", Font.BOLD, 14));
        btnAddTaiNguyen.addActionListener(e -> showTaiNguyenDialog());
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnPanel.setBackground(new Color(10, 10, 15));
        btnPanel.add(btnAddTaiNguyen);
        panelTaiNguyen.add(btnPanel);
        
        selectedTaiNguyen = new java.util.HashSet<>();
        
        // Ẩn các trường tài nguyên ban đầu
        bac.setVisible(false);
        backhoa.setVisible(false);
        vang.setVisible(false);
        vangkhoa.setVisible(false);
        
        panelTaiNguyenContainer.add(panelTaiNguyen);
        mainPanel.add(panelTaiNguyenContainer);
        mainPanel.add(Box.createVerticalStrut(10));
        
        // Panel hòm thư không hiển thị (luôn ẩn)
        
        // Nút xác nhận
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(10, 10, 15));
        buttonPanel.add(xacnhan);
        mainPanel.add(buttonPanel);
        
        getContentPane().add(mainPanel, BorderLayout.CENTER);

        // Thiết lập màu cho các label trong panel
        for (Component comp : panelInfo.getComponents()) {
            if (comp instanceof JLabel) {
                ((JLabel) comp).setForeground(neonGreen);
            }
        }
        for (Component comp : panelHoMThu.getComponents()) {
            if (comp instanceof JLabel) {
                ((JLabel) comp).setForeground(neonGreen);
            }
        }
        
        setSize(500, 600);
        setResizable(false);
    }

    private void xacnhanActionPerformed(ActionEvent evt) {
        // Lấy tên từ ComboBox (có thể là text đã gõ hoặc item đã chọn)
        String playerName = "";
        Object selected = nameComboBox.getSelectedItem();
        if (selected != null) {
            playerName = selected.toString().trim();
        }
        
        // Kiểm tra tên là bắt buộc
        if (playerName.isEmpty()) {
            JOptionPane.showMessageDialog(rootPane, "Vui lòng nhập hoặc chọn tên người chơi!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            Char chars = ServerManager.findCharByName(playerName);
            if (chars == null) {
                JOptionPane.showMessageDialog(rootPane, "Người này không tồn tại hoặc không online!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Kiểm tra cách gửi
            boolean isHoMThu = radioHoMThu.isSelected();
            
            if (isHoMThu) {
                // Gửi qua hòm thư
                sendViaLetter(chars, playerName);
            } else {
                // Gửi trực tiếp
                sendDirectly(chars, playerName);
            }
            
            // Thông báo thành công
            JOptionPane.showMessageDialog(rootPane, "Gửi thành công cho " + playerName + "!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            this.dispose(); // Đóng cửa sổ sau khi gửi thành công
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(rootPane, "Lỗi định dạng số: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            Log.error("Lỗi định dạng số trong SendItem: " + e.getMessage(), e);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane, "Lỗi không xác định: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            Log.error("Lỗi trong SendItem: " + e.getMessage(), e);
        }
    }
    
    private void sendDirectly(Char chars, String playerName) {
        // Xử lý Item (nếu có)
        String idItemText = idItem.getText().trim();
        String quantityText = quantity.getText().trim();
        String optionsText = strOptions.getText().trim();
        
        if (!idItemText.isEmpty() && !quantityText.isEmpty()) {
            if (!checkNumber(idItemText) || !checkNumber(quantityText)) {
                JOptionPane.showMessageDialog(rootPane, "ID Item và Số lượng phải là số nguyên dương!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int id = Integer.parseInt(idItemText);
            int amount = Integer.parseInt(quantityText);
            
            if (id > 0 && amount > 0) {
                try {
                    Item itADD = new Item(id);
                    if (itADD.getItemTemplate() != null) {
                        // Thêm options nếu có
                        if (!optionsText.isEmpty()) {
                            itADD.strOptions = optionsText;
                        }
                        
                        if (itADD.getItemTemplate().isXepChong) {
                            itADD.amount = amount;
                        } else {
                            itADD.amount = 1;
                            for (int i = 1; i < amount; i++) {
                                Item itA = new Item(id);
                                if (!optionsText.isEmpty()) {
                                    itA.strOptions = optionsText;
                                }
                                chars.addItem(itA);
                                chars.msgAddItemBag(itA);
                            }
                        }
                        if (chars.addItem(itADD)) {
                            chars.msgAddItemBag(itADD);
                            Log.debug("addITEM success: " + id + " x" + amount + " options: " + optionsText);
                        } else {
                            JOptionPane.showMessageDialog(rootPane, "Không thể thêm item vào túi (túi đầy?)", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(rootPane, "Item ID không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(rootPane, "Lỗi khi tạo item: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    Log.error("Lỗi khi tạo item: " + e.getMessage(), e);
                    return;
                }
            }
        }
        
        // Xử lý Bạc (nếu có)
        String bacText = bac.getText().trim();
        if (!bacText.isEmpty()) {
            if (!checkNumber(bacText)) {
                JOptionPane.showMessageDialog(rootPane, "Bạc phải là số nguyên dương!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            long bacValue = Long.parseLong(bacText);
            if (bacValue > 0) {
                chars.addBac(bacValue);
            }
        }
        
        // Xử lý Bạc Khóa (nếu có)
        String backhoaText = backhoa.getText().trim();
        if (!backhoaText.isEmpty()) {
            if (!checkNumber(backhoaText)) {
                JOptionPane.showMessageDialog(rootPane, "Bạc Khóa phải là số nguyên dương!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            long backhoaValue = Long.parseLong(backhoaText);
            if (backhoaValue > 0) {
                chars.addBacKhoa(backhoaValue);
            }
        }
        
        // Xử lý Vàng (nếu có)
        String vangText = vang.getText().trim();
        if (!vangText.isEmpty()) {
            if (!checkNumber(vangText)) {
                JOptionPane.showMessageDialog(rootPane, "Vàng phải là số nguyên dương!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            long vangValue = Long.parseLong(vangText);
            if (vangValue > 0) {
                chars.addVang(vangValue);
            }
        }
        
        // Xử lý Vàng Khóa (nếu có)
        String vangkhoaText = vangkhoa.getText().trim();
        if (!vangkhoaText.isEmpty()) {
            if (!checkNumber(vangkhoaText)) {
                JOptionPane.showMessageDialog(rootPane, "Vàng Khóa phải là số nguyên dương!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            long vangkhoaValue = Long.parseLong(vangkhoaText);
            if (vangkhoaValue > 0) {
                chars.addVangKhoa(vangkhoaValue);
            }
        }
    }
    
    private void sendViaLetter(Char chars, String playerName) {
        // Kiểm tra các trường hòm thư
        String titleText = titleThu.getText().trim();
        String nameNguoiGuiText = nameNguoiGui.getText().trim();
        String noiDungText = noiDungThu.getText().trim();
        String soNgayText = soNgayThu.getText().trim();
        
        if (titleText.isEmpty()) {
            JOptionPane.showMessageDialog(rootPane, "Vui lòng nhập tiêu đề thư!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (nameNguoiGuiText.isEmpty()) {
            nameNguoiGuiText = "Hệ thống";
        }
        
        if (noiDungText.isEmpty()) {
            noiDungText = "Admin gửi tặng bạn quà!";
        }
        
        int soNgay = 7; // Mặc định 7 ngày
        if (!soNgayText.isEmpty()) {
            if (!checkNumber(soNgayText)) {
                JOptionPane.showMessageDialog(rootPane, "Số ngày phải là số nguyên dương!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            soNgay = Integer.parseInt(soNgayText);
            if (soNgay <= 0) {
                soNgay = 7;
            }
        }
        
        // Tạo thư
        TemplateThu thu = new TemplateThu();
        
        // Tính ID thư - tìm ID lớn nhất và cộng 1
        int maxId = 0;
        for (TemplateThu t : chars.letters) {
            if (t.id > maxId) {
                maxId = t.id;
            }
        }
        int id = maxId + 1;
        if (id > Short.MAX_VALUE) {
            id = 1; // Reset về 1 nếu vượt quá giới hạn
        }
        
        thu.id = (short) id;
        thu.isSucess = false; // Khởi tạo trạng thái chưa nhận
        thu.Title = titleText;
        thu.NameNguoiGui = nameNguoiGuiText;
        thu.NoiDungThu = noiDungText;
        thu.Bac = 0;
        thu.BacKhoa = 0;
        thu.Vang = 0;
        thu.VangKhoa = 0;
        thu.Exp = 0;
        thu.Item = null;
        thu.TimeEnd = System.currentTimeMillis() + (soNgay * 86400000L);
        
        // Xử lý Item (nếu có)
        String idItemText = idItem.getText().trim();
        String quantityText = quantity.getText().trim();
        String optionsText = strOptions.getText().trim();
        
        if (!idItemText.isEmpty() && !quantityText.isEmpty()) {
            if (!checkNumber(idItemText) || !checkNumber(quantityText)) {
                JOptionPane.showMessageDialog(rootPane, "ID Item và Số lượng phải là số nguyên dương!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int idItem = Integer.parseInt(idItemText);
            int amount = Integer.parseInt(quantityText);
            
            if (idItem > 0 && amount > 0) {
                try {
                    Item itemThu = new Item(idItem);
                    if (itemThu.getItemTemplate() != null) {
                        itemThu.amount = amount;
                        itemThu.isLock = true;
                        // Thêm options nếu có
                        if (!optionsText.isEmpty()) {
                            itemThu.strOptions = optionsText;
                        } else {
                            itemThu.strOptions = "";
                        }
                        thu.Item = itemThu;
                    } else {
                        JOptionPane.showMessageDialog(rootPane, "Item ID không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(rootPane, "Lỗi khi tạo item: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    Log.error("Lỗi khi tạo item cho thư: " + e.getMessage(), e);
                    return;
                }
            }
        }
        
        // Xử lý tiền (nếu có)
        String bacText = bac.getText().trim();
        if (!bacText.isEmpty() && checkNumber(bacText)) {
            long bacValue = Long.parseLong(bacText);
            if (bacValue > 0) {
                thu.Bac = (int) Math.min(bacValue, Integer.MAX_VALUE);
            }
        }
        
        String backhoaText = backhoa.getText().trim();
        if (!backhoaText.isEmpty() && checkNumber(backhoaText)) {
            long backhoaValue = Long.parseLong(backhoaText);
            if (backhoaValue > 0) {
                thu.BacKhoa = (int) Math.min(backhoaValue, Integer.MAX_VALUE);
            }
        }
        
        String vangText = vang.getText().trim();
        if (!vangText.isEmpty() && checkNumber(vangText)) {
            long vangValue = Long.parseLong(vangText);
            if (vangValue > 0) {
                thu.Vang = (int) Math.min(vangValue, Integer.MAX_VALUE);
            }
        }
        
        String vangkhoaText = vangkhoa.getText().trim();
        if (!vangkhoaText.isEmpty() && checkNumber(vangkhoaText)) {
            long vangkhoaValue = Long.parseLong(vangkhoaText);
            if (vangkhoaValue > 0) {
                thu.VangKhoa = (int) Math.min(vangkhoaValue, Integer.MAX_VALUE);
            }
        }
        
        // Thêm thư vào danh sách
        chars.letters.add(thu);
        
        // Reload thư cho người chơi
        if (chars.getService() != null) {
            chars.getService().reloadLetter();
        }
        
        Log.debug("Gửi thư thành công cho " + playerName + " - ID: " + id);
    }

    private void updateItemName() {
        String idText = idItem.getText().trim();
        if (idText.isEmpty() || idText.equals("0")) {
            itemNameLabel.setText("");
            return;
        }
        
        try {
            int itemId = Integer.parseInt(idText);
            if (itemId > 0 && DataCenter.gI() != null && DataCenter.gI().ItemTemplate != null) {
                if (itemId < DataCenter.gI().ItemTemplate.length) {
                    com.sg188.data.ItemTemplate template = DataCenter.gI().ItemTemplate[itemId];
                    if (template != null && template.name != null) {
                        itemNameLabel.setText(template.name);
                        itemNameLabel.setForeground(new Color(0, 255, 190, 200)); // Màu xanh neon
                    } else {
                        itemNameLabel.setText("(Không tồn tại)");
                        itemNameLabel.setForeground(new Color(255, 100, 100, 200)); // Màu đỏ nhạt
                    }
                } else {
                    itemNameLabel.setText("(ID không hợp lệ)");
                    itemNameLabel.setForeground(new Color(255, 100, 100, 200)); // Màu đỏ nhạt
                }
            } else {
                itemNameLabel.setText("");
            }
        } catch (NumberFormatException e) {
            itemNameLabel.setText("(Sai định dạng)");
            itemNameLabel.setForeground(new Color(255, 100, 100, 200)); // Màu đỏ nhạt
        } catch (Exception e) {
            itemNameLabel.setText("");
            Log.error("Lỗi khi lấy tên item: " + e.getMessage());
        }
    }

    private void showTaiNguyenDialog() {
        // Tạo dialog để chọn loại tài nguyên
        JDialog dialog = new JDialog(this, "Chọn Tài Nguyên", true);
        dialog.getContentPane().setBackground(new Color(10, 10, 15));
        dialog.setLayout(new BorderLayout());
        
        Color neonGreen = new Color(0, 255, 190);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(10, 10, 15));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JCheckBox cbBac = new JCheckBox("Bạc");
        JCheckBox cbBacKhoa = new JCheckBox("Bạc Khóa");
        JCheckBox cbVang = new JCheckBox("Vàng");
        JCheckBox cbVangKhoa = new JCheckBox("Vàng Khóa");
        
        // Thiết lập màu cho checkbox
        for (JCheckBox cb : new JCheckBox[]{cbBac, cbBacKhoa, cbVang, cbVangKhoa}) {
            cb.setBackground(new Color(10, 10, 15));
            cb.setForeground(neonGreen);
            cb.setSelected(selectedTaiNguyen.contains(cb.getText()));
        }
        
        contentPanel.add(cbBac);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(cbBacKhoa);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(cbVang);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(cbVangKhoa);
        
        JButton btnOK = new JButton("Xác nhận");
        btnOK.setBackground(new Color(25, 25, 35));
        btnOK.setForeground(neonGreen);
        btnOK.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 190, 50), 1));
        btnOK.setFocusPainted(false);
        btnOK.addActionListener(e -> {
            // Cập nhật danh sách đã chọn
            selectedTaiNguyen.clear();
            if (cbBac.isSelected()) selectedTaiNguyen.add("Bạc");
            if (cbBacKhoa.isSelected()) selectedTaiNguyen.add("Bạc Khóa");
            if (cbVang.isSelected()) selectedTaiNguyen.add("Vàng");
            if (cbVangKhoa.isSelected()) selectedTaiNguyen.add("Vàng Khóa");
            
            // Cập nhật UI
            updateTaiNguyenPanel();
            dialog.dispose();
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(10, 10, 15));
        buttonPanel.add(btnOK);
        
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setSize(250, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private JPanel createTaiNguyenRow(String labelText, JTextField field, String key) {
        Color neonGreen = new Color(0, 255, 190);
        JLabel label = new JLabel(labelText);
        label.setForeground(neonGreen);
        label.setPreferredSize(new Dimension(80, 25));
        label.setMinimumSize(new Dimension(80, 25));
        
        JButton btnRemove = new JButton("–");
        btnRemove.setBackground(new Color(25, 25, 35));
        btnRemove.setForeground(new Color(255, 100, 100));
        btnRemove.setBorder(BorderFactory.createLineBorder(new Color(255, 100, 100, 50), 1));
        btnRemove.setFocusPainted(false);
        btnRemove.setPreferredSize(new Dimension(25, 25));
        btnRemove.setFont(new Font("Consolas", Font.BOLD, 12));
        btnRemove.addActionListener(e -> {
            selectedTaiNguyen.remove(key);
            updateTaiNguyenPanel();
        });
        
        // Sử dụng FlowLayout để đảm bảo các component hiển thị đúng
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        rowPanel.setBackground(new Color(10, 10, 15));
        rowPanel.add(btnRemove);
        rowPanel.add(label);
        field.setPreferredSize(new Dimension(100, 25));
        rowPanel.add(field);
        
        return rowPanel;
    }
    
    private void updateTaiNguyenPanel() {
        // Xóa các component cũ (trừ nút thêm ở index 0)
        while (panelTaiNguyen.getComponentCount() > 1) {
            panelTaiNguyen.remove(1);
        }
        
        // Ẩn tất cả các trường
        bac.setVisible(false);
        backhoa.setVisible(false);
        vang.setVisible(false);
        vangkhoa.setVisible(false);
        
        // Hiển thị các trường đã chọn
        Color neonGreen = new Color(0, 255, 190);
        
        if (selectedTaiNguyen.contains("Bạc")) {
            JPanel rowPanel = createTaiNguyenRow("Bạc:", bac, "Bạc");
            panelTaiNguyen.add(rowPanel);
            panelTaiNguyen.add(Box.createVerticalStrut(3));
            bac.setVisible(true);
        }
        
        if (selectedTaiNguyen.contains("Bạc Khóa")) {
            JPanel rowPanel = createTaiNguyenRow("Bạc Khóa:", backhoa, "Bạc Khóa");
            panelTaiNguyen.add(rowPanel);
            panelTaiNguyen.add(Box.createVerticalStrut(3));
            backhoa.setVisible(true);
        }
        
        if (selectedTaiNguyen.contains("Vàng")) {
            JPanel rowPanel = createTaiNguyenRow("Vàng:", vang, "Vàng");
            panelTaiNguyen.add(rowPanel);
            panelTaiNguyen.add(Box.createVerticalStrut(3));
            vang.setVisible(true);
        }
        
        if (selectedTaiNguyen.contains("Vàng Khóa")) {
            JPanel rowPanel = createTaiNguyenRow("Vàng Khóa:", vangkhoa, "Vàng Khóa");
            panelTaiNguyen.add(rowPanel);
            panelTaiNguyen.add(Box.createVerticalStrut(3));
            vangkhoa.setVisible(true);
        }
        
        // Cập nhật lại toàn bộ layout
        panelTaiNguyen.revalidate();
        panelTaiNguyen.repaint();
        // Cập nhật lại panel cha
        SwingUtilities.invokeLater(() -> {
            Container parent = panelTaiNguyen.getParent();
            while (parent != null && !(parent instanceof JFrame)) {
                parent.revalidate();
                parent.repaint();
                parent = parent.getParent();
            }
            if (parent != null) {
                parent.revalidate();
                parent.repaint();
            }
        });
    }

    public static boolean checkNumber(String str) {
        return str.matches("\\d+");
    }

    public static void run() {
        java.awt.EventQueue.invokeLater(() -> new JFrameSendItem().setVisible(true));
    }
}