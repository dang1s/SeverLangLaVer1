package com.sg188.server;

import Data.DataCode;
import Data.DataMenuNpc;
import Manager.Manager;
import MapService.Map;
import MapService.world.MapLangCo;
import MapService.world.WorldManager;
import SqlConnection.*;
import com.event.Event;
import com.sg188.PhucLoi.PhucLoi;
import com.sg188.Shop.Store;
import com.sg188.data.DataCenter;
import com.sg188.lib.Log;
import com.sg188.lib.Utlis;
import com.sg188.real.Char;
import com.sg188.real.ItemDrop;
import com.sg188.real.SelectCard;
import com.sg188.server.handler.ServerSocketHandler;
import com.sg188.server.lib.Client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

import MapService.*;
import Service.*;
import com.sg188.clan.*;
import jdk.jshell.execution.Util;
import market.MarketManager;

public class Main {

    public static MyServerSocket serverMain;
    public static MyServerSocket serverCheckOnline;
    public static Vector vecClient = new Vector();

    public static int NUM_CLIENTS = 0;
    public static boolean logData = false;
    public static boolean BaoTri = false;
    public static ServerSocket server;
    public static boolean start;
    public static int id;

    public static void main(String[] args) {
        Init();
        startServerSocket();
    }

    private static void startServerSocket() {
        try {
            server = new ServerSocket(2907);
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
        new Thread(() -> {
            Scanner sc = new Scanner(System.in);
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
        }).start();
        Config.getInstance().load();
        try {
            DBData.openConnection();
        } catch (SQLException e) {
        }
        DataCenter.gI().readArrDataGame(true);
        Manager.gI().loadItem();
        Manager.gI().loadDanhHieuNew();
        Store.getInstance().load();
        PhucLoi.getInstance().loadWelfare();
        PhucLoi.getInstance().load();
        Manager.gI().readShopRank();
        DataMenuNpc.LoadTextNpc();
        DataMenuNpc.loadDataText();
        DbMore.LoadGiftCode();
        Manager.gI().loadRewardTop();
        DBData.closeConnection();
        ItemDrop.Init();
        Event.init();
        Event event = Event.getEvent();
        if (event != null) {
            event.loadEventPoint();
        }
        Thread update = new Thread(new AutoSaveData());
        update.setName("Thread SaveData");
        update.start();
        Thread market = new Thread(MarketManager.gI());
        market.setName("Market");
        market.start();
        Map.createMap();
        Manager.gI().loadItemPurchases();
        Manager.gI().loadListTrangBi();
        Manager.gI().loadCountServer();
        LuckyDrawManager.getInstance().add(new LuckyDraw("Vòng xoay vip", (byte) 0));
        Thread threadLuckyDraw = new Thread(LuckyDrawManager.getInstance());
        threadLuckyDraw.setName("Vòng xoay");
        threadLuckyDraw.start();
        BossManager.gI().initBoss();
        BossManager.gI().updateBoss(9, 0, 0);
        BossManager.gI().updateBoss(14, 0, 0);
        BossManager.gI().updateBoss(19, 0, 0);
//        BossManager.gI().updateBossSK(6,0,0);
//        BossManager.gI().updateBossSK(9,0,0);
//        BossManager.gI().updateBossSK(12,0,0);
//        BossManager.gI().updateBossSK(18,0,0);
//        BossManager.gI().updateBossSK(20,0,0);
//        BossManager.gI().updateBossSK(22,0,0);
        Manager.gI().updateDeadForest(6, 50, 0);
        Manager.gI().updateDeadForest(9, 50, 0);
        Manager.gI().updateDeadForest(12, 50, 0);
        Manager.gI().updateDeadForest(15, 50, 0);
        Manager.gI().updateDeadForest(18, 50, 0);
        Manager.gI().updateDaiHoi(20, 20, 0);
        Manager.gI().updatePhucLoi(0, 0, 0);
        WorldManager.getInstance().start();
        // MongoDbConnection.connect();
        Clan.getClanDAO().load();
//        AutoMaintenance.maintenance(23, 59, 30);
        openServerSocket();
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
//        serverMain = new MyServerSocket(2907, new ServerSocketHandler() {
//            @Override
//            public void socketConnet(Socket socket) {
//                Client client = new Client(socket);
//                client.indexClient = NUM_CLIENTS++;
//                if(Main.BaoTri){
//                    try {
//                        socket.close();
//                    } catch (IOException e) {
//                    }
//                }
//                if (client.isConnected()) {
//                    Main.addClient(client);
//                    client.session.start();
//                } else {
//                    client.clean();
//                }
//            }
//
//            @Override
//            public void serverClose() {
//                System.exit(0);
//            }
//        });
//        serverMain.open();
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
                "Làng Lá - Đại Chiến Konoha chính thức khai mở Alpha Test miễn phí! ",
                "Muốn đổi vàng, đổi bạc hãy tới NPC Ginkaku nhé! ",
                "Tham gia Box Zalo, tương tác với Fanpage Làng Lá - Đại Chiến Konoha để nhận thêm quà! ",
                "Làng Lá - Đại Chiến Konoha chúc các bạn chơi game vui vẻ! "
        };
        Main.HeThongCTG(messages[new java.util.Random().nextInt(messages.length)], 2);
    }


    public static void maintance() {
        Main.BaoTri = true;
        Clan.running = false;
        MarketManager.gI().stop();
        try {
            LuckyDrawManager.getInstance().stop();
            HeThongCTG("Hệ thống chuẩn bị bảo trì sau 30s nữa, Các nhẫn giả hay lưu ý out để tránh mất dữ liệu", 2);
            Thread.sleep(30000);
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
                } catch (Exception e) {
                    e.printStackTrace();
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
        } catch (Exception e) {
            Log.error("Loi bao tri", e);
        }
    }
}
