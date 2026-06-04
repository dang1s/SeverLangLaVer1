package com.sg188.server;

import Data.DataCode;
import Data.DataMenuNpc;
import EventClick.ConfigChuyenCanTuan;
import EventClick.ConfigCuaCaiTuan;
import Manager.Manager;
import MapService.Map;
import MapService.world.MapLangCo;
import MapService.world.WorldManager;
import SqlConnection.*;
import com.event.Event;
import com.sg188.PhucLoi.PhucLoi;
import com.sg188.Shop.DiscountStore;
import com.sg188.Shop.Store;
import com.sg188.data.DataCenter;
import com.sg188.lib.Log;
import com.sg188.lib.Utlis;
import com.sg188.real.Char;
import com.sg188.real.ItemDrop;
import com.sg188.real.MountManager;
import com.sg188.real.SelectCard;
import com.sg188.server.handler.ServerSocketHandler;
import com.sg188.server.lib.Client;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import Service.ScheduledExecutor;
import SqlConnection.MongoDbConnection;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import MapService.*;
import Service.*;
import com.sg188.clan.*;
import com.sun.management.OperatingSystemMXBean;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextPane;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import jdk.jshell.execution.Util;
import market.MarketManager;
import java.text.SimpleDateFormat;
import EventClick.ConfigNhiDong;
import EventClick.ConfigLuyenTap;
import EventClick.ConfigCuongHoa;
import EventClick.ConfigCongHienTuan;// 
import EventClick.ConfigCuongHoaTuan;
// VongQuayNap disabled - not used
// import EventClick.VongQuayNapConfig;
import Manager.RankingRewardManager;
import com.sg188.real.Item;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import com.sg188.server.Config;
import com.sg188.server.lib.Message;

public class Main extends WindowAdapter implements ActionListener {
    public static java.util.concurrent.ConcurrentHashMap<Integer, Session> activeSessions = new java.util.concurrent.ConcurrentHashMap<>();

    public static MyServerSocket serverMain;
    public static MyServerSocket serverCheckOnline;
    public static Vector vecClient = new Vector();
    private JSpinner startSpin;
    private JSpinner endSpin;
    public static int NUM_CLIENTS = 0;
    public static boolean logData = false;
    public static boolean BaoTri = false;
    public static ServerSocket server;
    public static boolean start;
    public static int id;
    private JLabel actionLabel; // Đã có sẵn
    private JLabel footerLabel; // THÊM DÒNG NÀY VÀO ĐÂY

    private Frame frame;
    private JTextPane infoTextArea;
    private final ConcurrentHashMap<Thread, AtomicBoolean> threadFlags = new ConcurrentHashMap<>();
    private long lastReloadTime = 0;
    private static final long RELOAD_COOLDOWN = 1000;
    private final ScheduledExecutorService autoTopRewardScheduler = Executors.newScheduledThreadPool(4, new ThreadFactory() {
        private int counter = 0;
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "AutoTopReward-Worker-" + counter++);
            t.setDaemon(true);
            t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    });
    private boolean showingGiftCodeList = false;
    private ScheduledFuture<?> autoTopRewardFuture;
    private ScheduledFuture<?> managedEventBaselineFuture;
    private ScheduledFuture<?> managedEventRewardFuture;
    private ScheduledFuture<?> dhngStartFuture;
    private ScheduledFuture<?> dhngRankingFuture;
    private final List<Char> dhngParticipants = new ArrayList<>();
    private final ConcurrentHashMap<Integer, Integer> dhngScores = new ConcurrentHashMap<>();
    private final List<Char> dhngTop16 = new ArrayList<>();
    private final List<Char[]> dhngPairs = new ArrayList<>();
    private volatile boolean dhngBaoDanh = false;
    private volatile boolean dhngBatDau = false;
    private volatile boolean dhngVongLoai = false;
    private volatile boolean dhngTuKet = false;
    private volatile boolean dhngBanKet = false;
    private volatile boolean dhngChungKet = false;
    private int autoTopRewardIndex = -1;
    private String autoTopRewardTime = "";

    public Main() {
        try {
            frame = new Frame("Làng Lá - Server Manager");
            InputStream is = getClass().getClassLoader().getResourceAsStream("icon.png");
            if (is != null) {
                byte[] data = new byte[is.available()];
                is.read(data);
                ImageIcon img = new ImageIcon(data);
                frame.setIconImage(img.getImage());
            }
            frame.setSize(820, 620);
            frame.setBackground(new Color(10, 10, 15)); // Nền tối sâu hơn
            frame.setResizable(false);
            frame.addWindowListener(this);
            frame.setLayout(null);

            // --- Panel nút bấm với GridLayout 6 hàng x 2 cột ---
            javax.swing.JPanel sidePanel = new javax.swing.JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(25, 25, 35));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                    g2.setColor(new Color(0, 255, 190, 50));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                    g2.dispose();
                }
            };
            sidePanel.setOpaque(false);
            sidePanel.setLayout(new GridLayout(6, 2, 10, 10));
            sidePanel.setBounds(24, 56, 292, 510);

            // Nhãn tiêu đề cho panel
            String[] buttonLabels = {
                "Bảo Trì",
                "Buff Level",
                "Gửi Đồ",
                "Reload GiftCode",
                "Reload All",
                "Đổi Sự Kiện",
                "Đổi Kinh Nghiệm",
                "MANAGER EVENT",
                "Trao Quà Đua Top",
                "Skip Nhiệm Vụ",
                "DHNG",
                "Đóng Server"
            };
            String[] buttonCommands = {
                "BaoTri",
                "Sendlevel",
                "SendItem",
                "ReloadGiftCode",
                "ReloadAll",
                "SelectEvent",
                "ChangeExp",
                "MANAGEREVENT",
                "TRAOQUADUATOP",
                "SkipQuest",
                "dhng",
                "CloseServer"
            };

            for (int i = 0; i < buttonLabels.length; i++) {
                JButton button = createControlButton(buttonLabels[i]);
                button.setActionCommand(buttonCommands[i]);
                button.setBackground(new Color(40, 40, 55));
                button.setForeground(Color.WHITE);
                button.setFont(new Font("Consolas", Font.BOLD, 12));
                button.addActionListener(this);

                JPanel buttonWrapper = new JPanel(new java.awt.BorderLayout());
                buttonWrapper.setOpaque(true);
                buttonWrapper.setBackground(new Color(28, 30, 43));
                buttonWrapper.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(78, 88, 112), 1),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
                buttonWrapper.add(button, java.awt.BorderLayout.CENTER);
                sidePanel.add(buttonWrapper);

                final int index = i;
                button.addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) {
                        button.setBackground(new Color(0, 255, 190));
                        button.setForeground(Color.BLACK);
                        buttonWrapper.setBackground(new Color(35, 39, 56));
                    }

                    public void mouseExited(MouseEvent e) {
                        button.setBackground(new Color(40, 40, 55));
                        button.setForeground(Color.WHITE);
                        buttonWrapper.setBackground(new Color(28, 30, 43));
                    }
                });
            }

            infoTextArea = new JTextPane();
            infoTextArea.setEditable(false);
            infoTextArea.setFont(new Font("Consolas", Font.BOLD, 13));
            infoTextArea.setBackground(new Color(15, 15, 20));
            infoTextArea.setForeground(new Color(0, 255, 190));

            JScrollPane scrollPane = new JScrollPane(infoTextArea);
            scrollPane.setBounds(340, 50, 460, 480);
            scrollPane.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0, 255, 190, 80), 1));
            frame.add(scrollPane);

            actionLabel = new JLabel("Hệ thống đã sẵn sàng");
            actionLabel.setBounds(340, 540, 460, 34);
            actionLabel.setOpaque(true);
            actionLabel.setBackground(new Color(25, 25, 35));
            actionLabel.setForeground(new Color(0, 255, 190));
            actionLabel.setHorizontalAlignment(SwingConstants.CENTER);
            actionLabel.setFont(new Font("Consolas", Font.BOLD, 13));
            actionLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0, 255, 190, 30), 1));

            footerLabel = new JLabel(" Create Panel Server By PQM ");

            footerLabel.setBounds(0, 570, 820, 30);

            footerLabel.setForeground(new Color(0, 255, 190, 200)); // Màu xanh Neon thanh mảnh
            footerLabel.setFont(new Font("Consolas", Font.BOLD, 15)); // Font PLAIN cho thanh mảnh

            footerLabel.setHorizontalAlignment(SwingConstants.CENTER);

            frame.add(footerLabel);
            javax.swing.JPanel timePanel = new javax.swing.JPanel(new GridLayout(2, 2, 5, 5));

            startSpin = createDateSpinner(ConfigNhiDong.START_STR); //
            endSpin = createDateSpinner(ConfigNhiDong.END_STR);     //
            startSpin = createDateSpinner(ConfigLuyenTap.START_STR);
            endSpin = createDateSpinner(ConfigLuyenTap.END_STR);
            startSpin = createDateSpinner(ConfigCuongHoaTuan.START_STR);
            endSpin = createDateSpinner(ConfigCuongHoaTuan.END_STR);
            startSpin = createDateSpinner(ConfigChuyenCanTuan.START_STR);

            actionLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    clearActionMessage();
                }
            });

            javax.swing.Timer timer = new javax.swing.Timer(1000, e -> updateInfo());

            timer.start();

            frame.add(sidePanel);
            frame.add(scrollPane);
            frame.add(actionLabel);

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void windowClosing(WindowEvent e) {
        int confirm = JOptionPane.showConfirmDialog(frame,
                "Bạn có chắc chắn muốn đóng server không?",
                "Xác nhận đóng server", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            frame.dispose();
            Main.BaoTri = true;
            Clan.running = false;
            MarketManager.gI().stop();
            try {
                LuckyDrawManager.getInstance().stop();
                List<Char> chars = ServerManager.getChars();
                for (Char _char : chars) {
                    try {
                        if (_char != null && _char.user != null) {
                            _char.idDiaCung = -1;
                            _char.idCamThuat = -1;
                            _char.idKhuLuyenTap = -1;
                            _char.flush();
                            _char.user.session.clean();
                        }
                    } catch (Exception ea) {
                        ea.printStackTrace();
                    }
                }
                Log.debug("Hoan tat luu data");
                List<Clan> clans = Clan.getClanDAO().getAll();
                synchronized (clans) {
                    for (Clan clan : clans) {
                        Clan.getClanDAO().update(clan);
                    }
                }
                Log.debug("Hoan tat luu data clan");
                for (CreateGiftCode.Code code : DataCode.Codes) {
                    DbMore.saveGiftcode(code);
                }
                Manager.gI().saveFilePurchases();
                Manager.gI().saveToFile();
            } catch (Exception ez) {
                Log.error("Loi bao tri", ez);
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            // Properly shutdown all resource pools
            ScheduledExecutor.shutdown();
            ConnectionPool.shutdown();
            try {
                MongoDbConnection.close();
                Log.info("MongoDB connection closed");
            } catch (Exception ex) {
                Log.error("Error closing MongoDB connection", ex);
            }
            Log.info("All resource pools shutdown complete");
            System.exit(0);
        }
    }

    private void updateInfo() {
        double gb = 1024.0 * 1024.0 * 1024.0; // Chuyển từ bytes sang GB
        OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        
        // Lấy RAM thực tế của máy (không phải chỉ JVM)
        long totalPhysicalMemory = 0;
        long freePhysicalMemory = 0;
        long usedPhysicalMemory = 0;
        
        try {
            totalPhysicalMemory = osBean.getTotalPhysicalMemorySize();
            freePhysicalMemory = osBean.getFreePhysicalMemorySize();
            usedPhysicalMemory = totalPhysicalMemory - freePhysicalMemory;
            
            // Đảm bảo giá trị hợp lệ
            if (totalPhysicalMemory <= 0) {
                // Fallback về JVM memory nếu không lấy được system memory
                Runtime runtime = Runtime.getRuntime();
                totalPhysicalMemory = runtime.maxMemory();
                usedPhysicalMemory = runtime.totalMemory() - runtime.freeMemory();
            }
        } catch (Exception e) {
            // Nếu lỗi, dùng JVM memory làm fallback
            Runtime runtime = Runtime.getRuntime();
            totalPhysicalMemory = runtime.maxMemory();
            usedPhysicalMemory = runtime.totalMemory() - runtime.freeMemory();
        }
        
        // Lấy CPU usage của toàn hệ thống
        double systemCpuLoad = 0;
        try {
            // Thử lấy system CPU load trước (CPU của toàn hệ thống)
            systemCpuLoad = osBean.getSystemCpuLoad() * 100;
            // Nếu không hợp lệ, thử process CPU load
            if (systemCpuLoad < 0 || Double.isNaN(systemCpuLoad) || Double.isInfinite(systemCpuLoad)) {
                systemCpuLoad = osBean.getProcessCpuLoad() * 100;
                // Nếu vẫn không hợp lệ, dùng 0
                if (systemCpuLoad < 0 || Double.isNaN(systemCpuLoad) || Double.isInfinite(systemCpuLoad)) {
                    systemCpuLoad = 0;
                }
            }
        } catch (Exception e) {
            // Nếu có lỗi, thử process CPU load
            try {
                systemCpuLoad = osBean.getProcessCpuLoad() * 100;
                if (systemCpuLoad < 0 || Double.isNaN(systemCpuLoad) || Double.isInfinite(systemCpuLoad)) {
                    systemCpuLoad = 0;
                }
            } catch (Exception ex) {
                systemCpuLoad = 0;
            }
        }

        try {
            StyledDocument doc = infoTextArea.getStyledDocument();
            
            if (showingGiftCodeList) {
                // Nếu đang hiển thị giftcode, chỉ cập nhật phần đầu (giữ nguyên danh sách giftcode)
                String existingContent = doc.getText(0, doc.getLength());
                int eventIndex = existingContent.lastIndexOf("🎪 Sự kiện");
                if (eventIndex >= 0) {
                    int eventLineEnd = existingContent.indexOf("\n", eventIndex);
                    if (eventLineEnd >= 0) {
                        // Xóa phần đầu (từ đầu đến hết dòng "Sự kiện")
                        doc.remove(0, eventLineEnd + 1);
                        // Thêm lại phần đầu với thông tin mới
                        appendText(doc, " 👤 Online    : ", Color.WHITE);
                        appendText(doc, ServerManager.getNumberOnline() + " người\n", Color.ORANGE);
                        appendText(doc, " 🔗 Session   : ", Color.WHITE);
                        appendText(doc, ServerManager.getUsers().size() + " kết nối\n", Color.ORANGE);
                        appendText(doc, " 📦 Memory    : ", Color.WHITE);
                        int memoryPercent = (int)((usedPhysicalMemory * 100.0) / totalPhysicalMemory);
                        appendText(doc, String.format("%.2f / %.2f GB (%d%%)\n",
                                usedPhysicalMemory / gb, totalPhysicalMemory / gb, memoryPercent), getColor(memoryPercent));
                        appendText(doc, " 💻 CPU SRC   : ", Color.WHITE);
                        appendText(doc, String.format("%.2f%%\n", systemCpuLoad), getColor((long) systemCpuLoad));
                        appendText(doc, " 🔄 Thread    : ", Color.WHITE);
                        appendText(doc, Thread.activeCount() + " threads\n", Color.CYAN);
                        appendText(doc, " ------------------------------------------\n", Color.DARK_GRAY);
                        appendText(doc, " 🆙 Kinh nghiệm : ", Color.WHITE);
                        appendText(doc, "x" + getExpServerRate() + "\n", new Color(0, 255, 190));
                        appendText(doc, " 🎪 Sự kiện    : ", Color.WHITE);
                        String eventDisplay = getFriendlyEventName(Config.getInstance().getEvent());
                        appendText(doc, eventDisplay + "\n", new Color(0, 255, 190));
                    }
                }
            } else {
                // Nếu không hiển thị giftcode, cập nhật bình thường
                doc.remove(0, doc.getLength());
                appendText(doc, " 👤 Online    : ", Color.WHITE);
                appendText(doc, ServerManager.getNumberOnline() + " người\n", Color.ORANGE);
                appendText(doc, " 🔗 Session   : ", Color.WHITE);
                appendText(doc, ServerManager.getUsers().size() + " kết nối\n", Color.ORANGE);
                appendText(doc, " 📦 Memory    : ", Color.WHITE);
                int memoryPercent = (int)((usedPhysicalMemory * 100.0) / totalPhysicalMemory);
                appendText(doc, String.format("%.2f / %.2f GB (%d%%)\n",
                        usedPhysicalMemory / gb, totalPhysicalMemory / gb, memoryPercent), getColor(memoryPercent));
                appendText(doc, " 💻 CPU SRC   : ", Color.WHITE);
                appendText(doc, String.format("%.2f%%\n", systemCpuLoad), getColor((long) systemCpuLoad));
                appendText(doc, " 🔄 Thread    : ", Color.WHITE);
                appendText(doc, Thread.activeCount() + " threads\n", Color.CYAN);
                appendText(doc, " ------------------------------------------\n", Color.DARK_GRAY);
                appendText(doc, " 🆙 Kinh nghiệm : ", Color.WHITE);
                appendText(doc, "x" + getExpServerRate() + "\n", new Color(0, 255, 190));
                appendText(doc, " 🎪 Sự kiện    : ", Color.WHITE);
                String eventDisplay = getFriendlyEventName(Config.getInstance().getEvent());
                appendText(doc, eventDisplay + "\n", new Color(0, 255, 190));
            }

        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private String getFriendlyEventName(String eventClass) {
        // Nếu rỗng hoặc class là None/Null
        if (eventClass == null || eventClass.isEmpty() || eventClass.toLowerCase().contains("none")) {
            return "Đang tắt sự kiện ❌";
        }
        // So khớp chính xác từ config.properties
        if (eventClass.contains("Christmas")) {
            return "Giáng Sinh An Lành 🎄";
        }
        if (eventClass.contains("TetNguyenDan")) {
            return "Tết Nguyên Đán 🧧";
        }
        if (eventClass.contains("Summer")) {
            return "Mùa Hè Sôi Động ☀️";
        }
        if (eventClass.contains("Halloween")) {
            return "Đêm Halloween 🎃";
        }
        if (eventClass.contains("GioTo")) {
            return "Giỗ Tổ Hùng Vương 👑";
        }

        return "Class: " + eventClass; // Hiện class nếu không nằm trong danh sách
    }

    private Color getColor(long Percentage) {
        if (Percentage > 80) {
            return Color.RED;
        } else if (Percentage > 50) {
            return Color.ORANGE;
        } else {
            return Color.GREEN;
        }
    }

    private void appendText(StyledDocument doc, String text, Color color) throws BadLocationException {
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setForeground(attrs, color);
        doc.insertString(doc.getLength(), text, attrs);
    }

    private JButton createControlButton(String label) {
        JButton button = new JButton("<html><div style='text-align:center;'>" + label + "</div></html>");
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setMargin(new Insets(6, 8, 6, 8));
        button.setBackground(new Color(40, 40, 55));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Consolas", Font.BOLD, 13));
        return button;
    }

    private void clearActionMessage() {
        actionLabel.setText("");
        actionLabel.setBackground(Color.LIGHT_GRAY);
    }
    
    private void showGiftCodeList() {
        try {
            StyledDocument doc = infoTextArea.getStyledDocument();
            
            // Tìm vị trí kết thúc phần thông tin hệ thống (sau "Sự kiện")
            String existingContent = doc.getText(0, doc.getLength());
            int endIndex = existingContent.length();
            
            // Tìm dòng "Sự kiện" để biết vị trí kết thúc phần thông tin hệ thống
            int eventIndex = existingContent.lastIndexOf("🎪 Sự kiện");
            if (eventIndex >= 0) {
                // Tìm vị trí kết thúc dòng "Sự kiện"
                int eventLineEnd = existingContent.indexOf("\n", eventIndex);
                if (eventLineEnd >= 0) {
                    endIndex = eventLineEnd + 1;
                }
            } else {
                // Nếu không tìm thấy "Sự kiện", tìm vị trí bắt đầu danh sách giftcode cũ (nếu có)
                int giftCodeIndex = existingContent.lastIndexOf("📋 DANH SÁCH GIFTCODE");
                if (giftCodeIndex >= 0) {
                    // Tìm vị trí bắt đầu dòng chứa "📋 DANH SÁCH GIFTCODE"
                    int lineStart = existingContent.lastIndexOf("\n", giftCodeIndex);
                    if (lineStart >= 0) {
                        endIndex = lineStart + 1;
                    } else {
                        endIndex = giftCodeIndex;
                    }
                }
            }
            
            // Xóa phần sau "Sự kiện" hoặc sau danh sách giftcode cũ (nếu có) và thêm danh sách giftcode mới
            if (endIndex < doc.getLength()) {
                doc.remove(endIndex, doc.getLength() - endIndex);
            }
            
            // Thêm danh sách giftcode vào cuối
            appendText(doc, " ------------------------------------------\n", Color.DARK_GRAY);
            appendText(doc, " 📋 DANH SÁCH GIFTCODE\n", new Color(0, 255, 190));
            appendText(doc, "═══════════════════════════════════════\n", Color.DARK_GRAY);
            appendText(doc, "Tổng số: ", Color.WHITE);
            appendText(doc, DataCode.Codes.size() + " giftcode\n\n", new Color(0, 255, 190));
            
            int index = 1;
            for (CreateGiftCode.Code code : DataCode.Codes) {
                appendText(doc, "[" + index + "] ", Color.WHITE);
                appendText(doc, "Code: ", Color.WHITE);
                appendText(doc, code.Code + "\n", new Color(0, 255, 190));
                
                if (code.Bac > 0 || code.BacKhoa > 0 || code.Vang > 0 || code.VangKhoa > 0 || code.Exp > 0) {
                    appendText(doc, "    └─ ", Color.DARK_GRAY);
                    List<String> rewards = new ArrayList<>();
                    if (code.Bac > 0) rewards.add("Bạc: " + code.Bac);
                    if (code.BacKhoa > 0) rewards.add("Bạc Khóa: " + code.BacKhoa);
                    if (code.Vang > 0) rewards.add("Vàng: " + code.Vang);
                    if (code.VangKhoa > 0) rewards.add("Vàng Khóa: " + code.VangKhoa);
                    if (code.Exp > 0) rewards.add("Exp: " + code.Exp);
                    appendText(doc, String.join(" | ", rewards) + "\n", Color.ORANGE);
                }
                
                if (code.infoItem != null) {
                    appendText(doc, "    └─ ", Color.DARK_GRAY);
                    appendText(doc, "Item: " + code.infoItem.getItemTemplate().name, Color.CYAN);
                    if (code.infoItem.amount > 1) {
                        appendText(doc, " x" + code.infoItem.amount, Color.CYAN);
                    }
                    appendText(doc, "\n", Color.CYAN);
                }
                
                if (code.count < 9999) {
                    appendText(doc, "    └─ ", Color.DARK_GRAY);
                    appendText(doc, "Số lần dùng còn lại: " + code.count + "\n", Color.YELLOW);
                }
                
                appendText(doc, "\n", Color.WHITE);
                index++;
            }
            
            appendText(doc, "═══════════════════════════════════════\n", Color.DARK_GRAY);
            
            // Đảm bảo showingGiftCodeList = true để bảng giftcode được giữ nguyên
            showingGiftCodeList = true;
            
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Init();
        // VongQuayNap disabled - not used
        // VongQuayNapConfig.loadConfig();
        // scheduleMonthlyVongQuayNapReset();
        ConfigNhiDong.loadConfig();   // Nạp giờ Nhi Đồng
        ConfigLuyenTap.loadConfig();  // Nạp giờ Luyện Tập
        ConfigCuongHoa.loadConfig();  // Nạp giờ Cường Hóa

        // Khởi chạy luồng kiểm tra thời gian để tự động thông báo trong game
        ConfigLuyenTap.startCheckTimeThread();
        ConfigNhiDong.startCheckTimeThread();
        ConfigCuongHoa.startCheckTimeThread();
        ConfigChuyenCanTuan.startCheckTimeThread();
        System.out.println(">>> [Hệ Thống] Đã nạp toàn bộ cấu hình sự kiện!");

        // Khoi dong Admin Web API Server (cho phep website goi reload)
        AdminApiServer.gI().start(
            Config.getInstance().getAdminWebPort(),
            Config.getInstance().getAdminWebToken()
        );

        startServerSocket();

    }

    private static void startServerSocket() {
        try {
            server = new ServerSocket(Config.getInstance().getGameServerPort());
            start = true;
            id = 0;
            Log.info("Start server Success!");
            while (start) {
                try {
                    Socket client = server.accept();
                    if (Main.BaoTri) {
                        client.close();
                        continue;
                    }
                    String ip = client.getInetAddress().getHostAddress();
                    Session cl = new Session(client, ++id);
                    cl.IPAddress = ip;
                } catch (Exception e) {
                }
            }
        } catch (IOException e) {
        }
    }

    private static void Init() {
        Log.info("╔═══════════════════════════════════════════════════════════╗");
        Log.info("║              SERVER GAME KHOI DONG THANH CONG              ║");
        Log.info("╚═══════════════════════════════════════════════════════════╝");
        new Thread(() -> {
            try (Scanner sc = new Scanner(System.in)) {
                String value = sc.next();
                if (value.equals("save")) {
                    List<Char> chars = ServerManager.getChars();
                    for (Char _char : chars) {
                        try {
                            if (_char != null) {
                                _char.flush();
                                _char.user.session.clean();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("Done");
                }
                if (value.equals("clan")) {
                    List<Clan> clans = Clan.getClanDAO().getAll();
                    synchronized (clans) {
                        for (Clan clan : clans) {
                            Clan.getClanDAO().update(clan);
                        }
                    }
                    Log.debug("Hoan tat luu data clan");
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }, "Console-Input-Handler").start();
        Config.getInstance().load();
        try {
            DBData.openConnection();
        } catch (SQLException e) {
            Log.error("CRITICAL: Could not open DBData connection during startup!", e);
        }
        if (!GraphicsEnvironment.isHeadless()) {
            new Main();
        } else {
            Log.info("[ADMIN WEB] Headless mode detected, bo qua desktop GUI");
        }
        MobManager.getInstance().initMobNew();
        MountManager.getInstance().loadMount();
        DataCenter.gI().readArrDataGame(true);
        Manager.gI().loadItem();
        Manager.gI().loadDanhHieuNew();
        Store.getInstance().load();
        DiscountStore.getInstance().load();
        PhucLoi.getInstance().loadWelfare();
        PhucLoi.getInstance().load();
        Manager.gI().readShopRank();
        DataMenuNpc.LoadTextNpc();
        DataMenuNpc.loadDataText();
        DbMore.LoadGiftCode();
        Manager.gI().loadRewardTop();
        // DBData.closeConnection(); // Removed - HikariCP pool should stay open for application lifetime
        ItemDrop.Init();
        Event.init();
        Event event = Event.getEvent();
        if (event != null) {
            event.loadEventPoint();
        }
        Thread update = new Thread(new AutoSaveData(), "Thread-SaveData");
        update.setDaemon(true);
        update.start();
        Thread market = new Thread(MarketManager.gI(), "Market-Manager");
        market.setDaemon(true);
        market.start();
        Map.createMap();
        MapThreadWatchdog.getInstance().start();
        Manager.gI().loadItemPurchases();
        Manager.gI().loadListTrangBi();
        Manager.gI().loadCountServer();
        //  Manager.RankingRewardManager.initialize(); // Khởi tạo hệ thống phát quà top

        LuckyDrawManager.getInstance().add(new LuckyDraw("Vòng xoay vip", (byte) 0));
        Thread threadLuckyDraw = new Thread(LuckyDrawManager.getInstance(), "Vòng-Xoay-LuckyDraw");
        threadLuckyDraw.setDaemon(true);
        threadLuckyDraw.setPriority(Thread.MIN_PRIORITY);
        threadLuckyDraw.start();
        BossManager.gI().initBoss();
        BossManager.gI().initBossViThu();
        BossManager.gI().initBossHangViThu();
        BossManager.gI().updateBossHangViThu(7, 0, 0);
        BossManager.gI().updateBossHangViThu(10, 0, 0);
        BossManager.gI().updateBossHangViThu(13, 0, 0);
        BossManager.gI().updateBossHangViThu(17, 0, 0);
        BossManager.gI().updateBossHangViThu(20, 0, 0);
        BossManager.gI().updateBossViThu(16, 30, 0);
        BossManager.gI().updateBossViThu(19, 0, 0);
        BossManager.gI().updateBossViThu(20, 0, 0);
        BossManager.gI().updateBossViThu(7, 0, 0);
        BossManager.gI().updateBossViThu(10, 30, 0);
        BossManager.gI().updateBoss(9, 0, 0);
        BossManager.gI().updateBoss(10, 0, 0);
        BossManager.gI().updateBoss(11, 0, 0);
        BossManager.gI().updateBoss(14, 0, 0);
        BossManager.gI().updateBoss(19, 0, 0);
        BossManager.gI().updateBoss(21, 0, 0);
        BossManager.gI().updateBossSK(6, 0, 0);
        BossManager.gI().updateBossSK(9, 0, 0);
        BossManager.gI().updateBossSK(12, 0, 0);
//        Manager.gI().updateBossSK(18, 0, 0);
//        Manager.gI().updateBossSK(20, 0, 0);
//        Manager.gI().updateBossSK(22, 0, 0); // tắt nếu loi
        Manager.gI().updateDeadForest(6, 50, 0);
        Manager.gI().updateDeadForest(9, 50, 0);
        Manager.gI().updateDeadForest(10, 50, 0);
        Manager.gI().updateDeadForest(12, 50, 0);
        Manager.gI().updateDeadForest(15, 50, 0);
        Manager.gI().updateDeadForest(18, 50, 0);
        Manager.gI().updateDaiHoi(20, 30, 0);
        Manager.gI().updateDaiChienNhanGia3(9, 55, 0);
        Manager.gI().updatePhucLoi(0, 0, 0);
        WorldManager.getInstance().start();
        //ConfigLuyenTap.loadConfig();
        Clan.getClanDAO().load();
        openServerSocket();
        Log.info("╔═══════════════════════════════════════════════════════════╗");
        Log.info("║         SERVER DA SAN SANG - CHO CLIENT KET NOI            ║");
        Log.info("╚═══════════════════════════════════════════════════════════╝");
        Log.info("Thread Server: " + Thread.activeCount());
    }

    public static String getIp(Socket soc) {
        return split(soc.getRemoteSocketAddress().toString(), ":", 0)[0].replaceAll("/", "");
    }

    public static String getPort(Socket soc) {
        return split(soc.getRemoteSocketAddress().toString(), ":", 0)[1];
    }

    public static String[] split(String var0, String var1, int var2) {
        int var3;
        String[] var4;
        if ((var3 = var0.indexOf(var1)) >= 0) {
            var4 = split(var0.substring(var3 + var1.length()), var1, var2 + 1);
        } else {
            var4 = new String[var2 + 1];
            var3 = var0.length();
        }

        var4[var2] = var0.substring(0, var3);
        return var4;
    }

    private static void openServerSocket() {
        serverCheckOnline = new MyServerSocket(Config.getInstance().getHealthCheckPort(), new ServerSocketHandler() {

            @Override
            public void socketConnet(Socket socket) {
                try {
                    socket.getOutputStream().write(0);
                    socket.getOutputStream().flush();
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    try {
                        socket.close();
                    } catch (Exception exception) {
                    }
                }
            }

            @Override
            public void serverClose() {
                System.exit(0);
            }
        });
        serverCheckOnline.open();
    }

    public void socketConnet(Socket socket) {
        try {
            socket.getOutputStream().write(0);
            socket.getOutputStream().flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (Exception exception) {
            }
        }
    }

    public void serverClose() {
        System.exit(0);
    }

    public static synchronized void addClient(Client aThis) {
        if (vecClient.contains(aThis)) {
            return;
        }
        try {
            aThis.create();
        } catch (Exception ex) {
            ex.printStackTrace();
            aThis.clean();
            return;
        }
        vecClient.add(aThis);
    }

    public static synchronized void removeClient(Client aThis) {
        aThis.clean();
        try {
            vecClient.remove(aThis);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void HeThongCTG(String str, int type) {
        List<Char> list = ServerManager.getChars();
        for (Char pl : list) {
            if (pl != null && pl.user != null && !pl.isClean) {
                pl.user.session.sendMessage(HanderMessage.SendCTG_HeThong(str, type));
            }
        }
    }

    public static void sendRandomMessage() {
        String[] messages = {
            "Đổi Coin Tại Kinkaku Trường Konoha",
            "Hãy Chơi Game Văn Minh Để Phát Triển Cộng Đồng Lành Mạnh! "
        };
        Main.HeThongCTG(messages[new java.util.Random().nextInt(messages.length)], 2);
    }

    public static void maintance() {
        Main.BaoTri = true;
        try {
            if (Clan.running) {
                Clan.running = false;
            }
            if (MarketManager.gI() != null) {
                MarketManager.gI().stop();
            }
            if (LuckyDrawManager.getInstance() != null) {
                LuckyDrawManager.getInstance().stop();
            }
            
            HeThongCTG("Hệ thống chuẩn bị bảo trì sau 30s nữa, Các nhẫn giả hay lưu ý out để tránh mất dữ liệu", 2);
            Thread.sleep(30000);
            
            List<Char> chars = ServerManager.getChars();
            if (chars != null) {
                for (Char _char : chars) {
                    try {
                        if (_char != null && _char.user != null) {
                            _char.idDiaCung = -1;
                            _char.idCamThuat = -1;
                            _char.idKhuLuyenTap = -1;
                            _char.flush();
                            if (_char.user.session != null) {
                                _char.user.session.clean();
                            }
                        }
                    } catch (Exception e) {
                        Log.error("Lỗi khi lưu dữ liệu nhân vật: " + (_char != null && _char.Info != null ? _char.Info.name : "unknown"), e);
                    }
                }
            }
            Log.debug("Hoan tat luu data");
            
            // Lưu dữ liệu giftcode
            if (DataCode.Codes != null) {
                for (CreateGiftCode.Code code : DataCode.Codes) {
                    try {
                        DbMore.saveGiftcode(code);
                    } catch (Exception e) {
                        Log.error("Lỗi khi lưu giftcode: " + (code != null ? code.Code : "unknown"), e);
                    }
                }
            }
            
            // Lưu các file khác
            if (Manager.gI() != null) {
                try {
                    Manager.gI().saveFilePurchases();
                } catch (Exception e) {
                    Log.error("Lỗi khi lưu file purchases", e);
                }
                try {
                    Manager.gI().saveToFile();
                } catch (Exception e) {
                    Log.error("Lỗi khi lưu file", e);
                }
            }
            
            Log.debug("Hoan tat luu data clan");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Log.error("Bảo trì bị gián đoạn", e);
        } catch (Exception e) {
            Log.error("Loi bao tri", e);
        } finally {
            ScheduledExecutor.shutdown();
            ConnectionPool.shutdown();
            try {
                MongoDbConnection.close();
            } catch (Exception ex) {
                Log.error("Error closing MongoDB connection", ex);
            }
            Log.info("All resource pools shutdown complete");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();

        switch (actionCommand) {
            case "BaoTri":
                new Thread(() -> {
                    try {
                        List<Char> chars22 = ServerManager.getChars();
                        if (chars22 != null) {
                            for (Char _char2 : chars22) {
                                try {
                                    if (_char2 != null && _char2.user != null && _char2.service != null) {
                                        _char2.user.service.alertMessage("Chuẩn bị bảo trì sau 30s");
                                    }
                                } catch (Exception ex) {
                                    // Bỏ qua lỗi khi gửi thông báo
                                }
                            }
                        }
                        Main.maintance();
                        // Đợi một chút để đảm bảo mọi thứ đã được lưu
                        Thread.sleep(1000);
                        System.exit(0); // Exit code 0 = thành công
                    } catch (Exception ex) {
                        Log.error("Lỗi khi thực hiện bảo trì", ex);
                        System.exit(0); // Vẫn exit với code 0 để không báo lỗi Maven
                    }
                }).start();
                showActionMessage("Bạn vừa thực hiện bảo trì. Server sẽ tắt sau 30 giây.");
                break;
            case "Sendlevel":
                JFrameSendBufflevel.run();
                showActionMessage("Bạn vừa buff level.");
                break;
            case "SendItem":
                JFrameSendItem.run();
                showActionMessage("Bạn vừa gửi đồ.");
                break;
            case "ReloadGiftCode":
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastReloadTime < RELOAD_COOLDOWN) {
                    actionLabel.setText("Vui lòng đợi " + ((RELOAD_COOLDOWN - (currentTime - lastReloadTime)) / 1000) + " giây!");
                    actionLabel.setBackground(Color.YELLOW);
                    return;
                }
                actionLabel.setText("Đang tải lại giftcode...");
                actionLabel.setBackground(new Color(0, 255, 190));
                try {
                    DataCode.Codes.clear();
                    DbMore.LoadGiftCode();
                    int count = DataCode.Codes.size();
                    actionLabel.setText("Tải lại " + count + " giftcode thành công!");
                    actionLabel.setBackground(Color.GREEN);
                    lastReloadTime = currentTime;

                    // Hiển thị danh sách giftcode trong dialog riêng
                    showGiftCodeDialog();
                } catch (Exception ex) {
                    actionLabel.setText("Lỗi khi tải lại giftcode: " + ex.getMessage());
                    actionLabel.setBackground(Color.RED);
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Lỗi khi tải lại giftcode", ex);
                }
                break;
            case "ReloadAll":
                reloadAllServer();
                break;
            default:
                break;
            case "SelectEvent":
                showSelectEventDialog();
                break;
            case "ChangeExp":
                showChangeExpDialog();
                break;
            case "MANAGEREVENT":
                showManagerEventTimeDialog();
                break;
            case "TRAOQUADUATOP": {
                showTopRewardOptionsDialog();
                // Dialog sẽ tự xử lý việc chọn top và phát quà
                break;
            }
            case "SkipQuest":
                showSkipQuestDialog();
                break;
            case "dhng":
                startDhngEventManually();
                break;
            case "CloseServer":
                closeServerWithConfirm();
                break;
        }
    }
    
    private void startDhngEventManually() {
        startDhngEvent();
        showActionMessage("Đã kích hoạt Đại Hội Nhẫn Giả (DHNG) thủ công.");
    }
    
    private void doiKinhNghiem() {
        String input = JOptionPane.showInputDialog(frame, "Nhập hệ số EXP (Ví dụ: 1, 2, 5):");
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        try {
            // Chuyển sang kiểu int
            int rate = Integer.parseInt(input.trim());
            if (rate <= 0) {
                return;
            }


            // Lưu vào file config.properties (sẽ lưu dạng game.exp=2)
            if (updateConfigProperty("game.exp", String.valueOf(rate))) {
                showActionMessage("Đã lưu EXP x" + rate);
                applyExpRateRuntime(rate);
            }
        } catch (Exception e) {
            showActionMessage("Lỗi: Vui lòng nhập số nguyên!");
        }
    }

    private void showTopRewardOptionsDialog() {
        String[] options = {"Setup quà trao top", "Bật auto trao quà", "Đóng"};
        int choice = JOptionPane.showOptionDialog(
                frame,
                "Chọn chức năng cho trao quà đua top:",
                "Trao Quà Đua Top",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 0) {
            RankingRewardDialog dialog = new RankingRewardDialog(null);
            dialog.setVisible(true);
        } else if (choice == 1) {
            showAutoTopRewardDialog();
        }
    }

    private String[] getRankingTopNames() {
        return new String[]{
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
    }

    private void showAutoTopRewardDialog() {
        String[] topNames = getRankingTopNames();
        JComboBox<String> topCombo = new JComboBox<>(topNames);
        SpinnerDateModel timeModel = new SpinnerDateModel();
        JSpinner timeSpinner = new JSpinner(timeModel);
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm:ss");
        timeSpinner.setEditor(timeEditor);

        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
        panel.add(new JLabel("Chọn top cần auto trao quà:"));
        panel.add(topCombo);
        panel.add(new JLabel("Thời điểm tự động trao quà mỗi ngày:"));
        panel.add(timeSpinner);

        if (autoTopRewardIndex >= 0 && autoTopRewardIndex < topNames.length) {
            topCombo.setSelectedIndex(autoTopRewardIndex);
        }

        int result = JOptionPane.showConfirmDialog(
                frame,
                panel,
                "Bật Auto Trao Quà Đua Top",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime((Date) timeSpinner.getValue());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        scheduleAutoTopReward(topCombo.getSelectedIndex(), hour, minute, second, topNames[topCombo.getSelectedIndex()]);
    }

    private void scheduleAutoTopReward(int topIndex, int hour, int minute, int second, String topName) {
        if (autoTopRewardFuture != null && !autoTopRewardFuture.isCancelled()) {
            autoTopRewardFuture.cancel(false);
        }

        autoTopRewardIndex = topIndex;
        autoTopRewardTime = String.format("%02d:%02d:%02d", hour, minute, second);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.withHour(hour).withMinute(minute).withSecond(second).withNano(0);
        if (!nextRun.isAfter(now)) {
            nextRun = nextRun.plusDays(1);
        }

        long initialDelay = Duration.between(now, nextRun).getSeconds();
        long period = TimeUnit.DAYS.toSeconds(1);

        autoTopRewardFuture = autoTopRewardScheduler.scheduleAtFixedRate(() -> {
            try {
                boolean success = RankingRewardManager.manualRewardTopPlayers(topIndex, true);
                if (success) {
                    Log.info("[AUTO TOP REWARD] Đã auto trao quà cho " + topName + " lúc " + autoTopRewardTime);
                } else {
                    Log.warn("[AUTO TOP REWARD] Auto trao quà thất bại cho " + topName);
                }
            } catch (Exception ex) {
                Log.error("[AUTO TOP REWARD] Lỗi auto trao quà cho " + topName, ex);
            }
        }, initialDelay, period, TimeUnit.SECONDS);

        showActionMessage("Đã bật auto trao quà cho " + topName + " lúc " + autoTopRewardTime);
    }

    private final SimpleDateFormat sdfEvent = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    private void showManagerEventTimeDialog() {
        JDialog dialog = new JDialog(frame, "CẤU HÌNH THỜI GIAN SỰ KIỆN", true);
        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(new Color(25, 25, 35));

        JLabel lbTitle = new JLabel("CẤU HÌNH SỰ KIỆN ĐUA TOP");
        lbTitle.setFont(new Font("Consolas", Font.BOLD, 16));
        lbTitle.setForeground(new Color(0, 255, 190));
        lbTitle.setBounds(0, 15, 460, 30);
        lbTitle.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(lbTitle);

        String[] applyOptions = {"Sự kiện", "Đua top", "Tuần tháng", "Vong quay nap"};
        JComboBox<String> cbApply = new JComboBox<>(applyOptions);
        cbApply.setBounds(160, 115, 250, 30);
        cbApply.setBackground(new Color(35, 35, 45));
        cbApply.setForeground(Color.WHITE);
        mainPanel.add(cbApply);

        final JSpinner spinStart = createDateSpinner(ConfigNhiDong.START_STR);
        spinStart.setBounds(160, 155, 250, 30);
        final JSpinner spinEnd = createDateSpinner(ConfigNhiDong.END_STR);
        spinEnd.setBounds(160, 195, 250, 30);

        JLabel lbDesc = new JLabel("Thời gian này áp dụng cho các nhóm bảng xếp hạng trong game.");
        lbDesc.setForeground(new Color(180, 220, 255));
        lbDesc.setBounds(20, 55, 420, 25);
        mainPanel.add(lbDesc);

        JLabel lbDesc2 = new JLabel("Khi đến giờ kết thúc sẽ tự động trao quà theo bảng rewards gửi qua thư.");
        lbDesc2.setForeground(new Color(180, 220, 255));
        lbDesc2.setBounds(20, 80, 420, 25);
        mainPanel.add(lbDesc2);

        JLabel lbSelect = new JLabel("Áp dụng:");
        lbSelect.setForeground(Color.WHITE);
        lbSelect.setBounds(30, 115, 120, 30);
        mainPanel.add(lbSelect);

        JLabel lbS = new JLabel("Bắt đầu:");
        lbS.setForeground(Color.WHITE);
        lbS.setBounds(30, 155, 100, 30);
        mainPanel.add(lbS);

        JLabel lbE = new JLabel("Kết thúc:");
        lbE.setForeground(Color.WHITE);
        lbE.setBounds(30, 195, 100, 30);
        mainPanel.add(lbE);

        cbApply.addActionListener(e -> {
            try {
                String selected = String.valueOf(cbApply.getSelectedItem());
                String startValue;
                String endValue;

                if ("Đua top".equals(selected)) {
                    startValue = getEventProperty("duatop.start", ConfigNhiDong.START_STR);
                    endValue = getEventProperty("duatop.end", ConfigNhiDong.END_STR);
                    spinEnd.setEnabled(true);
                } else if ("Tuần tháng".equals(selected)) {
                    startValue = ConfigChuyenCanTuan.START_STR;
                    endValue = sdfEvent.format(new java.util.Date(ConfigChuyenCanTuan.END));
                    spinEnd.setEnabled(false);
                } else if ("Vong quay nap".equals(selected)) {
                    // VongQuayNap disabled - not used
                    /*
                    startValue = getVongQuayNapStartValue();
                    endValue = getVongQuayNapEndValue();
                    spinEnd.setEnabled(true);
                    */
                    spinEnd.setEnabled(false);
                } else {
                    startValue = ConfigNhiDong.START_STR;
                    endValue = ConfigNhiDong.END_STR;
                    spinEnd.setEnabled(true);
                }

//                spinStart.setValue(sdfEvent.parse(startValue));
//                spinEnd.setValue(sdfEvent.parse(endValue));
            } catch (Exception ex) {
                Log.error("Lỗi đổi nhóm áp dụng thời gian sự kiện", ex);
            }
        });
        cbApply.setSelectedIndex(0);

        JButton btnSave = new JButton("LƯU CẤU HÌNH");
        btnSave.setBackground(new Color(0, 255, 190));
        btnSave.setBounds(240, 245, 170, 35);
        btnSave.addActionListener(e -> {
            try {
                String selected = String.valueOf(cbApply.getSelectedItem());
                String startStr = sdfEvent.format(spinStart.getValue());
                String endStr = sdfEvent.format(spinEnd.getValue());
                long startMillis = sdfEvent.parse(startStr).getTime();
                long endMillis = sdfEvent.parse(endStr).getTime();

                if (endMillis <= startMillis) {
                    JOptionPane.showMessageDialog(dialog, "Thời gian kết thúc phải lớn hơn thời gian bắt đầu.");
                    return;
                }

                if ("Đua top".equals(selected)) {
                    updateEventProperty("duatop.start", startStr);
                    updateEventProperty("duatop.end", endStr);
                    showActionMessage("Đã lưu thời gian cho nhóm Đua top");
                    JOptionPane.showMessageDialog(dialog, "Đã lưu thời gian cho nhóm Đua top.");
                } else if ("Tuần tháng".equals(selected)) {
                    ConfigChuyenCanTuan.saveConfig(startStr);
                    ConfigCuongHoaTuan.saveConfig(startStr);
                    ConfigCuaCaiTuan.saveConfig(startStr);
                    ConfigCongHienTuan.saveConfig(startStr);
                    showActionMessage("Đã lưu thời gian cho nhóm Tuần tháng");
                    JOptionPane.showMessageDialog(dialog, "Đã lưu thời gian cho nhóm Tuần tháng.");
                } else if ("Vong quay nap".equals(selected)) {
                    // VongQuayNap disabled - not used
                    /*
                    if (!VongQuayNapConfig.saveConfig(startMillis, endMillis)) {
                        JOptionPane.showMessageDialog(dialog, "Khong the luu cau hinh Vong quay nap vao SQL.");
                        return;
                    }
                    VongQuayNapConfig.loadConfig();
                    showActionMessage("Da luu thoi gian Vong quay nap");
                    JOptionPane.showMessageDialog(dialog, "Da luu thoi gian Vong quay nap.");
                    */
                } else {
                    saveManagedEventConfig(startStr, endStr, startMillis, endMillis);
                    JOptionPane.showMessageDialog(dialog, "Đã lưu thời gian nhóm Sự kiện và bật auto trao quà lúc kết thúc.");
                }
                dialog.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        JButton btnCancel = new JButton("HỦY");
        btnCancel.setBounds(50, 245, 170, 35);
        btnCancel.addActionListener(e -> dialog.dispose());

        mainPanel.add(spinStart);
        mainPanel.add(spinEnd);
        mainPanel.add(btnSave);
        mainPanel.add(btnCancel);

        dialog.add(mainPanel);
        dialog.setSize(460, 350);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    // VongQuayNap disabled - not used
    /*
    private String getVongQuayNapStartValue() {
        if (VongQuayNapConfig.isValid()) {
            return sdfEvent.format(new Date(VongQuayNapConfig.StartTimeMillis));
        }
        return sdfEvent.format(new Date());
    }

    private String getVongQuayNapEndValue() {
        if (VongQuayNapConfig.isValid()) {
            return sdfEvent.format(new Date(VongQuayNapConfig.EndTimeMillis));
        }
        return sdfEvent.format(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7)));
    }
    */

    private void showGiftCodeDialog() {
        JDialog dialog = new JDialog(frame, "DANH SÁCH GIFTCODE", false);
        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(new Color(25, 25, 35));

        JLabel lbTitle = new JLabel("📋 DANH SÁCH GIFTCODE");
        lbTitle.setFont(new Font("Consolas", Font.BOLD, 16));
        lbTitle.setForeground(new Color(0, 255, 190));
        lbTitle.setBounds(0, 15, 600, 30);
        lbTitle.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(lbTitle);

        // Tạo JTextPane để hiển thị danh sách giftcode
        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setFont(new Font("Consolas", Font.PLAIN, 13));
        textPane.setBackground(new Color(15, 15, 20));
        textPane.setForeground(new Color(0, 255, 190));

        // Tạo StyledDocument để format text
        StyledDocument doc = textPane.getStyledDocument();
        SimpleAttributeSet attrSet = new SimpleAttributeSet();

        try {
            // Thêm tiêu đề
            StyleConstants.setForeground(attrSet, new Color(0, 255, 190));
            StyleConstants.setBold(attrSet, true);
            doc.insertString(doc.getLength(), "═══════════════════════════════════════════════════════════\n", attrSet);
            doc.insertString(doc.getLength(), "Tổng số: " + DataCode.Codes.size() + " giftcode\n", attrSet);
            doc.insertString(doc.getLength(), "═══════════════════════════════════════════════════════════\n\n", attrSet);

            // Thêm danh sách giftcode
            int index = 1;
            for (CreateGiftCode.Code code : DataCode.Codes) {
                // Số thứ tự và code
                StyleConstants.setForeground(attrSet, Color.WHITE);
                StyleConstants.setBold(attrSet, false);
                doc.insertString(doc.getLength(), "[" + index + "] ", attrSet);
                doc.insertString(doc.getLength(), "Code: ", attrSet);
                
                StyleConstants.setForeground(attrSet, new Color(0, 255, 190));
                doc.insertString(doc.getLength(), code.Code + "\n", attrSet);

                // Phần thưởng (Bạc, Vàng, Exp)
                if (code.Bac > 0 || code.BacKhoa > 0 || code.Vang > 0 || code.VangKhoa > 0 || code.Exp > 0) {
                    StyleConstants.setForeground(attrSet, Color.DARK_GRAY);
                    doc.insertString(doc.getLength(), "    └─ ", attrSet);
                    
                    List<String> rewards = new ArrayList<>();
                    if (code.Bac > 0) rewards.add("Bạc: " + code.Bac);
                    if (code.BacKhoa > 0) rewards.add("Bạc Khóa: " + code.BacKhoa);
                    if (code.Vang > 0) rewards.add("Vàng: " + code.Vang);
                    if (code.VangKhoa > 0) rewards.add("Vàng Khóa: " + code.VangKhoa);
                    if (code.Exp > 0) rewards.add("Exp: " + code.Exp);
                    
                    StyleConstants.setForeground(attrSet, Color.ORANGE);
                    doc.insertString(doc.getLength(), String.join(" | ", rewards) + "\n", attrSet);
                }

                // Item
                if (code.infoItem != null) {
                    StyleConstants.setForeground(attrSet, Color.DARK_GRAY);
                    doc.insertString(doc.getLength(), "    └─ ", attrSet);
                    StyleConstants.setForeground(attrSet, Color.CYAN);
                    doc.insertString(doc.getLength(), "Item: " + code.infoItem.getItemTemplate().name, attrSet);
                    if (code.infoItem.amount > 1) {
                        doc.insertString(doc.getLength(), " x" + code.infoItem.amount, attrSet);
                    }
                    doc.insertString(doc.getLength(), "\n", attrSet);
                }

                // Số lần dùng còn lại
                if (code.count < 9999) {
                    StyleConstants.setForeground(attrSet, Color.DARK_GRAY);
                    doc.insertString(doc.getLength(), "    └─ ", attrSet);
                    StyleConstants.setForeground(attrSet, Color.YELLOW);
                    doc.insertString(doc.getLength(), "Số lần dùng còn lại: " + code.count + "\n", attrSet);
                }

                doc.insertString(doc.getLength(), "\n", attrSet);
                index++;
            }

            doc.insertString(doc.getLength(), "═══════════════════════════════════════════════════════════\n", attrSet);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        // Tạo JScrollPane để cuộn
        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setBounds(20, 60, 560, 400);
        scrollPane.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0, 255, 190, 80), 1));
        mainPanel.add(scrollPane);

        // Nút Đóng
        JButton btnClose = new JButton("ĐÓNG");
        btnClose.setBackground(new Color(0, 255, 190));
        btnClose.setForeground(Color.BLACK);
        btnClose.setFont(new Font("Consolas", Font.BOLD, 14));
        btnClose.setBounds(240, 480, 120, 35);
        btnClose.addActionListener(e -> dialog.dispose());
        mainPanel.add(btnClose);

        dialog.add(mainPanel);
        dialog.setSize(600, 560);
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    private void showSelectEventDialog() {
        JDialog dialog = new JDialog(frame, "ĐỔI SỰ KIỆN", true);
        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(new Color(25, 25, 35));

        JLabel lbTitle = new JLabel("ĐỔI SỰ KIỆN");
        lbTitle.setFont(new Font("Consolas", Font.BOLD, 16));
        lbTitle.setForeground(new Color(0, 255, 190));
        lbTitle.setBounds(0, 15, 420, 30);
        lbTitle.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(lbTitle);

        String[] eventNames = {"Giáng Sinh", "Tết Nguyên Đán", "Hè 2026", "Halloween", "Giỗ Tổ ", "Tắt Sự Kiện"};
        String[] eventClasses = {"com.event.Christmas", "com.event.TetNguyenDan", "com.event.SummerEvent2026", "com.event.Halloween", "com.event.GioTo", "com.event.None"};

        // Tìm sự kiện hiện tại - map từ class sang tên
        String currentEventClass = Config.getInstance().getEvent();
        int currentIndex = 0;
        if (currentEventClass != null && !currentEventClass.isEmpty()) {
            if (currentEventClass.contains("Christmas")) {
                currentIndex = 0;
            } else if (currentEventClass.contains("TetNguyenDan")) {
                currentIndex = 1;
            } else if (currentEventClass.contains("Summer")) {
                currentIndex = 2;
            } else if (currentEventClass.contains("Halloween")) {
                currentIndex = 3;
            } else if (currentEventClass.contains("GioTo")) {
                currentIndex = 4;
            } else {
                currentIndex = 5; // Tắt Sự Kiện
            }
        } else {
            currentIndex = 5; // Tắt Sự Kiện
        }

        JComboBox<String> cbEvent = new JComboBox<>(eventNames);
        cbEvent.setSelectedIndex(currentIndex);
        cbEvent.setBounds(130, 60, 250, 30);
        cbEvent.setBackground(new Color(35, 35, 45));
        cbEvent.setForeground(Color.WHITE);
        cbEvent.setFont(new Font("Consolas", Font.PLAIN, 13));
        mainPanel.add(cbEvent);

        JLabel lbSelect = new JLabel("Sự kiện:");
        lbSelect.setForeground(Color.WHITE);
        lbSelect.setFont(new Font("Consolas", Font.PLAIN, 13));
        lbSelect.setBounds(30, 60, 100, 30);
        mainPanel.add(lbSelect);

        // Nút Lưu
        JButton btnSave = new JButton("LƯU CẤU HÌNH");
        btnSave.setBackground(new Color(0, 255, 190));
        btnSave.setForeground(Color.BLACK);
        btnSave.setFont(new Font("Consolas", Font.BOLD, 14));
        btnSave.setBounds(220, 120, 160, 35);
        btnSave.addActionListener(e -> {
            try {
                String selectedName = cbEvent.getSelectedItem().toString();
                String classToSave = "";
                for (int i = 0; i < eventNames.length; i++) {
                    if (eventNames[i].equals(selectedName)) {
                        classToSave = eventClasses[i];
                        break;
                    }
                }

                // Lưu vào config.properties
                if (updateConfigProperty("game.event", classToSave)) {
                    showActionMessage("Đã lưu: " + selectedName);
                    updateInfo();
                    if (applySelectedEventRuntime(classToSave, selectedName)) {
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialog,
                                "Da luu config nhung khong the ap dung su kien ngay lap tuc.",
                                "Canh bao",
                                JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(dialog, "Lỗi lưu config!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnCancel = new JButton("HỦY");
        btnCancel.setBackground(new Color(60, 60, 70));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFont(new Font("Consolas", Font.BOLD, 14));
        btnCancel.setBounds(40, 120, 160, 35);
        btnCancel.addActionListener(e -> dialog.dispose());

        mainPanel.add(btnSave);
        mainPanel.add(btnCancel);

        dialog.add(mainPanel);
        dialog.setSize(420, 200);
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    private void showChangeExpDialog() {
        JDialog dialog = new JDialog(frame, "ĐỔI KINH NGHIỆM", true);
        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(new Color(25, 25, 35));

        JLabel lbTitle = new JLabel("ĐỔI KINH NGHIỆM");
        lbTitle.setFont(new Font("Consolas", Font.BOLD, 16));
        lbTitle.setForeground(new Color(0, 255, 190));
        lbTitle.setBounds(0, 15, 420, 30);
        lbTitle.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(lbTitle);

        JLabel lbExp = new JLabel("Hệ số EXP:");
        lbExp.setForeground(Color.WHITE);
        lbExp.setFont(new Font("Consolas", Font.PLAIN, 13));
        lbExp.setBounds(30, 60, 100, 30);
        mainPanel.add(lbExp);

        // TextField để nhập EXP
        javax.swing.JTextField txtExp = new javax.swing.JTextField();
        txtExp.setBounds(130, 60, 250, 30);
        txtExp.setBackground(new Color(30, 30, 45));
        txtExp.setForeground(Color.WHITE);
        txtExp.setFont(new Font("Consolas", Font.PLAIN, 13));
        txtExp.setCaretColor(Color.WHITE);
        txtExp.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0, 255, 190, 80), 1));
        txtExp.setText(String.valueOf(getExpServerRate()));
        mainPanel.add(txtExp);

        // Nút Lưu
        JButton btnSave = new JButton("LƯU CẤU HÌNH");
        btnSave.setBackground(new Color(0, 255, 190));
        btnSave.setForeground(Color.BLACK);
        btnSave.setFont(new Font("Consolas", Font.BOLD, 14));
        btnSave.setBounds(220, 110, 160, 35);
        btnSave.addActionListener(e -> {
            try {
                String input = txtExp.getText().trim();
                if (input.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng nhập hệ số EXP!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int rate = Integer.parseInt(input);
                if (rate <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Hệ số EXP phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }


                // Lưu vào file config.properties
                if (updateConfigProperty("game.exp", String.valueOf(rate))) {
                    showActionMessage("Đã lưu EXP x" + rate);
                    applyExpRateRuntime(rate);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Lỗi lưu config!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: Vui lòng nhập số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnCancel = new JButton("HỦY");
        btnCancel.setBackground(new Color(60, 60, 70));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFont(new Font("Consolas", Font.BOLD, 14));
        btnCancel.setBounds(40, 110, 160, 35);
        btnCancel.addActionListener(e -> dialog.dispose());

        mainPanel.add(btnSave);
        mainPanel.add(btnCancel);

        dialog.add(mainPanel);
        dialog.setSize(420, 190);
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    private void saveManagedEventConfig(String startStr, String endStr, long startMillis, long endMillis) {
        ConfigNhiDong.saveConfig(startStr, endStr);
        ConfigLuyenTap.saveConfig(startStr, endStr);
        ConfigCuongHoa.saveConfig(startStr, endStr);
        updateEventProperty("napnhieu.start", startStr);
        updateEventProperty("napnhieu.end", endStr);
        scheduleManagedEventBaseline(startMillis);
        scheduleManagedEventReward(endMillis);
        showActionMessage("Đã đồng bộ thời gian top sự kiện và auto trao quà lúc kết thúc");
    }

    private void scheduleManagedEventBaseline(long startMillis) {
        if (managedEventBaselineFuture != null && !managedEventBaselineFuture.isCancelled()) {
            managedEventBaselineFuture.cancel(false);
        }

        long delayMillis = Math.max(0L, startMillis - System.currentTimeMillis());
        managedEventBaselineFuture = autoTopRewardScheduler.schedule(() -> {
            try {
                RankingRewardManager.captureManagedEventBaselines();
                Log.info("[EVENT TOP BASELINE] Đã chụp mốc điểm bắt đầu cho top sự kiện");
            } catch (Exception ex) {
                Log.error("[EVENT TOP BASELINE] Lỗi chụp mốc điểm top sự kiện", ex);
            }
        }, delayMillis, TimeUnit.MILLISECONDS);
    }

    private void scheduleManagedEventReward(long endMillis) {
        if (managedEventRewardFuture != null && !managedEventRewardFuture.isCancelled()) {
            managedEventRewardFuture.cancel(false);
        }

        long delayMillis = endMillis - System.currentTimeMillis();
        if (delayMillis <= 0) {
            Log.warn("[EVENT TOP REWARD] Không lên lịch vì thời gian kết thúc đã qua");
            return;
        }

        managedEventRewardFuture = autoTopRewardScheduler.schedule(() -> {
            try {
                boolean success = RankingRewardManager.rewardManagedEventTops();
                if (success) {
                    Log.info("[EVENT TOP REWARD] Đã auto trao quà top sự kiện theo giờ kết thúc");
                } else {
                    Log.warn("[EVENT TOP REWARD] Hết giờ nhưng không có dữ liệu/quà để trao");
                }
            } catch (Exception ex) {
                Log.error("[EVENT TOP REWARD] Lỗi auto trao quà top sự kiện", ex);
            }
        }, delayMillis, TimeUnit.MILLISECONDS);
    }

    private boolean updateEventProperty(String key, String value) {
        try {
            Properties props = new Properties();
            File file = new File("event.properties");
            if (file.exists()) {
                try (FileInputStream in = new FileInputStream(file)) {
                    props.load(in);
                }
            }
            props.setProperty(key, value);
            try (FileOutputStream out = new FileOutputStream(file)) {
                props.store(out, null);
            }
            return true;
        } catch (Exception ex) {
            Log.error("Lỗi cập nhật event.properties: " + key, ex);
            return false;
        }
    }

    private String getEventProperty(String key, String defaultValue) {
        try {
            Properties props = new Properties();
            File file = new File("event.properties");
            if (file.exists()) {
                try (FileInputStream in = new FileInputStream(file)) {
                    props.load(in);
                }
            }
            return props.getProperty(key, defaultValue);
        } catch (Exception ex) {
            Log.error("Lỗi đọc event.properties: " + key, ex);
            return defaultValue;
        }
    }

    private JSpinner createDateSpinner(String dateStr) {
        try {
            java.util.Date date = (dateStr == null || dateStr.isEmpty())
                    ? new java.util.Date()
                    : sdfEvent.parse(dateStr);

            SpinnerDateModel model = new SpinnerDateModel(date, null, null, java.util.Calendar.SECOND);
            JSpinner spinner = new JSpinner(model);

            // Định dạng hiển thị
            JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "dd-MM-yyyy HH:mm:ss");
            spinner.setEditor(editor);

            // --- CHỈNH MÀU TỐI TẠI ĐÂY ---
            // Lấy trường văn bản bên trong Spinner
            javax.swing.JFormattedTextField txt = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();

            txt.setEditable(true);
            txt.setBackground(new Color(30, 30, 45)); // Màu nền tối (giống panel)
            txt.setForeground(Color.WHITE);          // Màu chữ trắng
            txt.setCaretColor(Color.WHITE);         // Màu con trỏ nhấp nháy trắng
            txt.setBorder(null);                    // Xóa viền trắng mặc định

            return spinner;
        } catch (Exception e) {
            return new JSpinner(new SpinnerDateModel());
        }
    }

    private void showActionMessage(String message) {
        actionLabel.setText(message);
        actionLabel.setBackground(Color.YELLOW);
    }

    private void startDhngEvent() {
        synchronized (dhngParticipants) {
            dhngParticipants.clear();
            dhngScores.clear();
            dhngTop16.clear();
            dhngPairs.clear();
            dhngBaoDanh = true;
            dhngBatDau = false;
            dhngVongLoai = false;
            dhngTuKet = false;
            dhngBanKet = false;
            dhngChungKet = false;
        }

        List<Char> onlinePlayers = new ArrayList<>();
        try {
            onlinePlayers.addAll(ServerManager.getChars());
        } catch (Exception ignored) {
        }

        for (Char player : onlinePlayers) {
            if (player == null || player.user == null || player.Info == null) {
                continue;
            }
            try {
                if (player.level() < 10) {
                    continue;
                }
                synchronized (dhngParticipants) {
                    if (!dhngParticipants.contains(player)) {
                        dhngParticipants.add(player);
                        dhngScores.putIfAbsent(player.id, 0);
                    }
                }
                player.user.service.alertMessage("Bạn đã được báo danh vào Đại Hội Nhẫn Giả!");
            } catch (Exception ex) {
                Log.error("Lỗi thêm người chơi vào DHNG", ex);
            }
        }

        if (dhngStartFuture != null && !dhngStartFuture.isCancelled()) {
            dhngStartFuture.cancel(false);
        }
        dhngStartFuture = autoTopRewardScheduler.schedule(this::beginDhngCombat, 10, TimeUnit.SECONDS);
        HeThongCTG("DHNG đã được kích hoạt. Báo danh 10 giây rồi tự bắt đầu.", 2);
        showActionMessage("DHNG đã kích hoạt: báo danh 10 giây rồi tự bắt đầu");
    }

    private void beginDhngCombat() {
        List<Char> participants;
        synchronized (dhngParticipants) {
            participants = new ArrayList<>(dhngParticipants);
            dhngBaoDanh = false;
            dhngBatDau = true;
        }

        if (participants.isEmpty()) {
            showActionMessage("DHNG không có người tham gia.");
            return;
        }

        for (Char player : participants) {
            if (player == null || player.user == null || player.Info == null) {
                continue;
            }
            try {
                if (player.zone != null) {
                    player.zone.removeChar(player);
                }
                player.setXY((short) 515, (short) 471);
                Map.maps[49].addChar(player);
                player.service.sendPointMap(player.pointDaiHoi);
                player.user.service.alertMessage("Chào mừng đến Đại Hội Nhẫn Giả! Hãy kiếm điểm!");
                player.user.service.alertMessage("Chào mừng đến Đại Hội Nhẫn Giả! Hãy kiếm điểm!");
            } catch (Exception ex) {
                Log.error("Lỗi teleport DHNG cho " + player.Info.name, ex);
            }
        }

        if (dhngRankingFuture != null && !dhngRankingFuture.isCancelled()) {
            dhngRankingFuture.cancel(false);
        }
        dhngRankingFuture = autoTopRewardScheduler.scheduleAtFixedRate(this::sendDhngRankingUpdate, 0, 30, TimeUnit.SECONDS);
        autoTopRewardScheduler.schedule(this::endDhngEvent, 20, TimeUnit.MINUTES);
    }

    private void sendDhngRankingUpdate() {
        if (!dhngBatDau && !dhngVongLoai && !dhngTuKet && !dhngBanKet && !dhngChungKet) {
            return;
        }
        List<Char> recipients = new ArrayList<>();
        try {
            for (Char player : ServerManager.getChars()) {
                if (player != null && player.zone != null && player.zone.isDaiHoiVoThuat()) {
                    recipients.add(player);
                }
            }
        } catch (Exception ignored) {
        }
        if (recipients.isEmpty()) {
            return;
        }
        List<java.util.Map.Entry<Integer, Integer>> ranking = new ArrayList<>(dhngScores.entrySet());
        ranking.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        StringBuilder sb = new StringBuilder("DHNG TOP: ");
        for (int i = 0; i < Math.min(10, ranking.size()); i++) {
            int pid = ranking.get(i).getKey();
            String name = "ID:" + pid;
            for (Char c : recipients) {
                if (c != null && c.id == pid) {
                    name = c.Info.name;
                    break;
                }
            }
            if (i > 0) sb.append(" | ");
            sb.append(i + 1).append('.').append(name).append('(').append(ranking.get(i).getValue()).append(')');
        }
        for (Char player : recipients) {
            try {
                player.user.service.alertMessage(sb.toString());
            } catch (Exception ignored) {
            }
        }
    }

    private void endDhngEvent() {
        dhngBatDau = false;
        dhngVongLoai = false;
        dhngTuKet = false;
        dhngBanKet = false;
        dhngChungKet = false;
        dhngBaoDanh = false;
        HeThongCTG("DHNG đã kết thúc.", 2);

        synchronized (dhngParticipants) {
            for (Char player : dhngParticipants) {
                if (player == null || player.user == null) {
                    continue;
                }
                try {
                    if (player.zone != null) {
                        player.zone.removeChar(player);
                    }
                    player.setXY((short) 100, (short) 100);
                    Map.maps[1].addChar(player);
                } catch (Exception ignored) {
                }
            }
            dhngParticipants.clear();
            dhngScores.clear();
            dhngTop16.clear();
            dhngPairs.clear();
        }
    }

    private boolean updateConfigProperty(String key, String value) {
        try {
            java.io.File file = new java.io.File("config.properties");
            if (!file.exists()) {
                return false;
            }

            java.util.List<String> lines = java.nio.file.Files.readAllLines(file.toPath());
            boolean found = false;

            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).trim().startsWith(key + "=")) {
                    lines.set(i, key + "=" + value);
                    found = true;
                    break;
                }
            }

            if (!found) {
                lines.add(key + "=" + value);
            }

            java.nio.file.Files.write(file.toPath(), lines);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void applyExpRateRuntime(int rate) {
        Config.getInstance().setRuntimeGameExpRate(rate);
        showActionMessage("Da ap dung EXP x" + rate + " ngay lap tuc");
        HeThongCTG("He so kinh nghiem may chu da duoc doi thanh x" + rate, 2);
        updateInfo();
    }

    private boolean applySelectedEventRuntime(String eventClassName, String eventDisplayName) {
        try {
            java.util.List<Char> onlinePlayers = new java.util.ArrayList<>(ServerManager.getChars());
            for (Char player : onlinePlayers) {
                if (player == null) {
                    continue;
                }
                try {
                    if (player.getEventPoint() != null) {
                        player.updateEventPoint();
                    }
                } catch (Exception ex) {
                    Log.error("Loi luu diem su kien truoc khi reload cho " + player.Info.name, ex);
                }
                player.setEventPoint(null);
            }

            Config.getInstance().setRuntimeEvent(eventClassName);
            Event.reload(eventClassName);

            Event event = Event.getEvent();
            if (event != null) {
                event.loadEventPoint();
                for (Char player : onlinePlayers) {
                    if (player == null) {
                        continue;
                    }
                    try {
                        player.loadEventPoint();
                    } catch (Exception ex) {
                        Log.error("Loi reload su kien cho " + player.Info.name, ex);
                    }
                }
            }

            String message = event == null
                    ? "Da tat su kien ngay lap tuc"
                    : "Da doi su kien sang " + eventDisplayName + " ngay lap tuc";
            showActionMessage(message);
            HeThongCTG(message, 2);
            updateInfo();
            return true;
        } catch (Exception ex) {
            Log.error("Loi ap dung su kien runtime", ex);
            return false;
        }
    }

    private int getExpServerRate() {
        try {
            java.util.Properties props = new java.util.Properties();
            try (java.io.InputStream input = new java.io.FileInputStream("config.properties")) {
                props.load(new java.io.InputStreamReader(input, java.nio.charset.StandardCharsets.UTF_8));
            }
            String value = props.getProperty("game.exp");
            if (value == null || value.trim().isEmpty()) {
                return 1;
            }
            return Math.max(1, Integer.parseInt(value.trim()));
        } catch (Exception e) {
            return 1;
        }
    }

    // VongQuayNap disabled - not used
    /*
    private static void scheduleMonthlyVongQuayNapReset() {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.LocalDateTime nextReset = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        if (!nextReset.isAfter(now)) {
            nextReset = nextReset.plusMonths(1);
        }
        long delay = java.time.Duration.between(now, nextReset).toMillis();
        java.util.concurrent.ScheduledExecutorService monthlyResetScheduler = java.util.concurrent.Executors.newScheduledThreadPool(2, new ThreadFactory() {
            private int counter = 0;
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "MonthlyReset-Worker-" + counter++);
                t.setDaemon(true);
                t.setPriority(Thread.MIN_PRIORITY);
                return t;
            }
        });
        monthlyResetScheduler.scheduleAtFixedRate(() -> {
            try {
                VongQuayNapConfig.resetMonthlySeason();
                VongQuayNapConfig.loadConfig();
                Log.info("[VONG QUAY NAP] Da auto reset mùa mới vào ngày 1 hàng tháng");
            } catch (Exception ex) {
                Log.error("[VONG QUAY NAP] Lỗi auto reset tháng", ex);
            }
        }, delay, java.util.concurrent.TimeUnit.DAYS.toMillis(31), java.util.concurrent.TimeUnit.MILLISECONDS);
    }
    */

    private void closeServerWithConfirm() {
        int confirm = JOptionPane.showConfirmDialog(frame,
                "Bạn có chắc chắn muốn đóng server không?\nTất cả người chơi sẽ bị ngắt kết nối!",
                "Xác nhận đóng Server",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            new Thread(() -> {
                try {
                    showActionMessage("Đang đóng server...");
                    actionLabel.setBackground(Color.ORANGE);

                    // Thông báo cho người chơi
                    HeThongCTG("Server se dong sau 10s!", 2);

                    Thread.sleep(10000);

                    // Lưu dữ liệu trước khi đóng
                    List<Char> chars = ServerManager.getChars();
                    for (Char _char : chars) {
                        try {
                            if (_char != null && _char.user != null) {
                                _char.flush();
                                _char.user.session.clean();
                            }
                        } catch (Exception e) {
                            Log.error("Loi luu char khi dong server: " + _char.Info.name, e);
                        }
                    }

                    Log.info("Hoan tat luu data khi dong server");
                    ScheduledExecutor.shutdown();
                    ConnectionPool.shutdown();
                    try {
                        MongoDbConnection.close();
                    } catch (Exception ex) {
                        Log.error("Error closing MongoDB connection", ex);
                    }
                    Log.info("All resource pools shutdown complete");
                    System.exit(0);
                } catch (Exception ex) {
                    Log.error("Loi khi dong server", ex);
                    showActionMessage("Loi dong server: " + ex.getMessage());
                    actionLabel.setBackground(Color.RED);
                }
            }, "CloseServer-Thread").start();
        }
    }

    private void reloadAllServer() {
        new Thread(() -> {
            try {
                showActionMessage("Đang reload toàn bộ server...");
                actionLabel.setBackground(Color.ORANGE);
                DataCode.Codes.clear();
                DbMore.LoadGiftCode();
                Log.info("[RELOAD ALL] Da reload GiftCode: " + DataCode.Codes.size() + " codes");
                Store.getInstance().load();
                DiscountStore.getInstance().load();
                Log.info("[RELOAD ALL] Da reload Shop");
                PhucLoi.getInstance().loadWelfare();
                PhucLoi.getInstance().load();
                Log.info("[RELOAD ALL] Da reload PhucLoi");
                Manager.gI().readShopRank();
                Log.info("[RELOAD ALL] Da reload ShopRank");
                Manager.gI().loadItem();
                Log.info("[RELOAD ALL] Da reload Item");
                showActionMessage("Reload All thanh cong!");
                actionLabel.setBackground(Color.GREEN);
                Log.info("[RELOAD ALL] Hoan tat reload toan bo server!");

            } catch (Exception ex) {
                Log.error("[RELOAD ALL] Loi khi reload server", ex);
                showActionMessage("Loi reload: " + ex.getMessage());
                actionLabel.setBackground(Color.RED);
            }
        }, "ReloadAll-Thread").start();
    }

    private void showSkipQuestDialog() {
        JDialog dialog = new JDialog(frame, "SKIP NHIỆM VỤ", true);
        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(new Color(25, 25, 35));

        JLabel lbTitle = new JLabel("SKIP NHIỆM VỤ");
        lbTitle.setFont(new Font("Consolas", Font.BOLD, 16));
        lbTitle.setForeground(new Color(0, 255, 190));
        lbTitle.setBounds(0, 15, 420, 30);
        lbTitle.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(lbTitle);

        JLabel lbName = new JLabel("Tên người chơi:");
        lbName.setForeground(Color.WHITE);
        lbName.setFont(new Font("Consolas", Font.PLAIN, 13));
        lbName.setBounds(30, 60, 120, 30);
        mainPanel.add(lbName);

        // ComboBox gợi ý người đang online (gõ để lọc)
        java.util.List<String> onlinePlayers = new java.util.ArrayList<>();
        try {
            java.util.List<Char> chars = ServerManager.getChars();
            for (Char ch : chars) {
                if (ch != null && ch.Info != null && ch.Info.name != null && !ch.Info.name.isEmpty()) {
                    onlinePlayers.add(ch.Info.name);
                }
            }
            onlinePlayers.sort(String.CASE_INSENSITIVE_ORDER);
        } catch (Exception ex) {
            Log.error("Lỗi khi lấy danh sách người chơi online: " + ex.getMessage());
        }

        final java.util.List<String> allOnlineNames = new java.util.ArrayList<>(onlinePlayers);
        final javax.swing.JComboBox<String> cbName = new javax.swing.JComboBox<>(onlinePlayers.toArray(new String[0]));
        cbName.setEditable(true);
        cbName.setBounds(150, 60, 230, 30);
        cbName.setBackground(new Color(30, 30, 45));
        cbName.setForeground(Color.WHITE);
        cbName.setFont(new Font("Consolas", Font.PLAIN, 13));
        cbName.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0, 255, 190, 80), 1));

        final javax.swing.JTextField editor = (javax.swing.JTextField) cbName.getEditor().getEditorComponent();
        editor.setBackground(new Color(30, 30, 45));
        editor.setForeground(Color.WHITE);
        editor.setCaretColor(Color.WHITE);

        final boolean[] isAdjusting = new boolean[]{false};
        editor.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void updateModel() {
                if (isAdjusting[0]) return;
                isAdjusting[0] = true;
                final String text = editor.getText();
                javax.swing.SwingUtilities.invokeLater(() -> {
                    try {
                        javax.swing.DefaultComboBoxModel<String> model = new javax.swing.DefaultComboBoxModel<>();
                        String q = text == null ? "" : text.trim().toLowerCase();
                        for (String name : allOnlineNames) {
                            if (q.isEmpty() || name.toLowerCase().contains(q)) {
                                model.addElement(name);
                            }
                        }
                        cbName.setModel(model);
                        cbName.setSelectedItem(text);
                        editor.setText(text);
                        editor.setCaretPosition(text != null ? text.length() : 0);
                        if (model.getSize() > 0) {
                            cbName.setPopupVisible(true);
                        }
                    } finally {
                        isAdjusting[0] = false;
                    }
                });
            }

            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateModel();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateModel();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateModel();
            }
        });

        // Tooltip nhỏ để biết đang gợi ý theo online
        cbName.setToolTipText("Gợi ý theo danh sách người đang online (" + allOnlineNames.size() + ")");
        mainPanel.add(cbName);

        // Nhập số bước skip
        JLabel lbStep = new JLabel("Số bước skip:");
        lbStep.setForeground(Color.WHITE);
        lbStep.setFont(new Font("Consolas", Font.PLAIN, 13));
        lbStep.setBounds(30, 95, 120, 30);
        mainPanel.add(lbStep);

        javax.swing.JSpinner spStep = new javax.swing.JSpinner(new javax.swing.SpinnerNumberModel(1, 1, 999, 1));
        spStep.setBounds(150, 95, 80, 30);
        spStep.setFont(new Font("Consolas", Font.PLAIN, 13));
        spStep.setBackground(new Color(30, 30, 45));
        spStep.setForeground(Color.WHITE);
        spStep.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0, 255, 190, 80), 1));
        try {
            javax.swing.JComponent ed = spStep.getEditor();
            if (ed instanceof javax.swing.JSpinner.DefaultEditor) {
                javax.swing.JTextField tf = ((javax.swing.JSpinner.DefaultEditor) ed).getTextField();
                tf.setBackground(new Color(30, 30, 45));
                tf.setForeground(Color.WHITE);
                tf.setCaretColor(Color.WHITE);
            }
        } catch (Exception ignored) {
        }
        mainPanel.add(spStep);

        // Nút Skip
        JButton btnSkip = new JButton("SKIP NHIỆM VỤ");
        btnSkip.setBackground(new Color(0, 255, 190));
        btnSkip.setForeground(Color.BLACK);
        btnSkip.setFont(new Font("Consolas", Font.BOLD, 14));
        btnSkip.setBounds(220, 150, 160, 35);
        btnSkip.addActionListener(e -> {
            try {
                String playerName = "";
                Object selected = cbName.getEditor().getItem();
                if (selected != null) {
                    playerName = selected.toString().trim();
                }
                if (playerName.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng nhập tên người chơi!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Char targetChar = ServerManager.findCharByName(playerName);
                if (targetChar == null) {
                    JOptionPane.showMessageDialog(dialog, "Không tìm thấy người chơi: " + playerName, "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Skip nhiệm vụ bằng cách tăng index
                if (targetChar.taskMain != null && targetChar.taskMain.vStep != null) {
                    int size = targetChar.taskMain.vStep.size();
                    if (size <= 0) {
                        JOptionPane.showMessageDialog(dialog, "Nhiệm vụ hiện tại không hợp lệ!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    int skip = 1;
                    try {
                        skip = ((Number) spStep.getValue()).intValue();
                    } catch (Exception ignored) {
                    }
                    if (skip < 1) skip = 1;

                    // Hệ nhiệm vụ: index == vStep.size() là trạng thái "đi trả nhiệm vụ" (vượt qua bước cuối)
                    int maxIndex = size;
                    int oldIndex = targetChar.taskMain.index;
                    int newIndex = Math.min(oldIndex + skip, maxIndex);

                    if (newIndex <= oldIndex) {
                        JOptionPane.showMessageDialog(dialog, "Người chơi đã hoàn thành nhiệm vụ hiện tại!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    int realSkip = newIndex - oldIndex;
                    targetChar.taskMain.index = newIndex;
                    targetChar.taskMain.count = 0;

                    // Cập nhật client (đúng flow taskNext/updateTakingStep)
                    try {
                        targetChar.getService().sendMessage(new Message((byte) 5));
                    } catch (Exception ignored) {
                    }
                    try {
                        targetChar.getService().sendTaskInfo();
                    } catch (Exception ignored) {
                    }
                    try {
                        targetChar.getService().sendTaskStep(targetChar.taskMain.index);
                    } catch (Exception ignored) {
                    }
                    targetChar.getService().serverMessage("Admin đã skip " + realSkip + " bước nhiệm vụ cho bạn!");
                    showActionMessage("Đã skip " + realSkip + " bước cho: " + playerName);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Người chơi không có nhiệm vụ nào đang làm!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnCancel = new JButton("HỦY");
        btnCancel.setBackground(new Color(60, 60, 70));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFont(new Font("Consolas", Font.BOLD, 14));
        btnCancel.setBounds(40, 150, 160, 35);
        btnCancel.addActionListener(e -> dialog.dispose());

        mainPanel.add(btnSkip);
        mainPanel.add(btnCancel);

        dialog.add(mainPanel);
        dialog.setSize(420, 235);
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

}
