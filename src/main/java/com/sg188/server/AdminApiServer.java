package com.sg188.server;

import Data.DataCode;
import Manager.Manager;
import Service.HanderMessage;
import SqlConnection.DbMore;
import com.sg188.PhucLoi.PhucLoi;
import com.sg188.Shop.DiscountStore;
import com.sg188.Shop.Store;
import com.sg188.lib.Log;
import com.sg188.real.Char;
import com.sg188.server.lib.Message;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * HTTP API server cho phep website goi reload ma khong can tat mo server.
 * Chi can goi: GET http://127.0.0.1:{port}/api/{action}?token={token}
 */
public class AdminApiServer {

    private static AdminApiServer instance;
    private HttpServer httpServer;
    private int port = 8081;
    private String token = "langla_local_admin_2026";
    private volatile boolean running = false;

    private AdminApiServer() {}

    public static AdminApiServer gI() {
        if (instance == null) {
            instance = new AdminApiServer();
        }
        return instance;
    }

    public void start(int port, String token) {
        if (running) {
            Log.warn("[AdminApiServer] Server da chay roi, bo qua.");
            return;
        }
        this.port = port;
        this.token = token;
        new Thread(() -> {
            try {
                httpServer = HttpServer.create(new InetSocketAddress(port), 0);

                // --- Reload endpoints ---
                httpServer.createContext("/api/reload-giftcode", new ReloadHandler("giftcode"));
                httpServer.createContext("/api/reload-shop", new ReloadHandler("shop"));
                httpServer.createContext("/api/reload-discount", new ReloadHandler("discount"));
                httpServer.createContext("/api/reload-phucloi", new ReloadHandler("phucloi"));
                httpServer.createContext("/api/reload-shop-rank", new ReloadHandler("shop_rank"));
                httpServer.createContext("/api/reload-item", new ReloadHandler("item"));
                httpServer.createContext("/api/reload-danhhieu", new ReloadHandler("danhhieu"));
                httpServer.createContext("/api/reload-event", new ReloadHandler("event"));
                httpServer.createContext("/api/reload-all", new ReloadHandler("all"));
                httpServer.createContext("/api/status", new StatusHandler());

                httpServer.setExecutor(Executors.newFixedThreadPool(4));
                httpServer.start();
                running = true;
                Log.info("[AdminApiServer] Da khoi dong tren port " + port);
                Log.info("[AdminApiServer] Token: " + token);
                Log.info("[AdminApiServer] Cac endpoint:");
                Log.info("  GET /api/reload-giftcode  - Tai lai giftcode");
                Log.info("  GET /api/reload-shop      - Tai lai shop");
                Log.info("  GET /api/reload-discount  - Tai lai giam gia shop");
                Log.info("  GET /api/reload-phucloi  - Tai lai phuc loi");
                Log.info("  GET /api/reload-shop-rank - Tai lai shop rank");
                Log.info("  GET /api/reload-item      - Tai lai item template");
                Log.info("  GET /api/reload-danhhieu - Tai lai danh hieu");
                Log.info("  GET /api/reload-event     - Tai lai cau hinh su kien");
                Log.info("  GET /api/reload-all      - Tai lai TAT CA");
                Log.info("  GET /api/status          - Kiem tra trang thai server");
            } catch (IOException e) {
                Log.error("[AdminApiServer] Loi khoi dong server: " + e.getMessage(), e);
            }
        }, "AdminApiServer-Thread").start();
    }

    public void stop() {
        if (httpServer != null && running) {
            httpServer.stop(1);
            running = false;
            Log.info("[AdminApiServer] Da dung.");
        }
    }

    public boolean isRunning() {
        return running;
    }

    // --- Gui thong bao den tat ca nguoi choi online ---
    private static void notifyOnlinePlayers(String message) {
        new Thread(() -> {
            try {
                List<Char> chars = ServerManager.getChars();
                for (Char _char : chars) {
                    try {
                        if (_char != null && _char.user != null && !_char.isClean) {
                            _char.user.session.sendMessage(
                                HanderMessage.SendThongBao(message, HanderMessage.WHITE)
                            );
                        }
                    } catch (Exception ignored) {}
                }
            } catch (Exception e) {
                Log.error("[AdminApiServer] Loi gui thong bao online: " + e.getMessage());
            }
        }, "NotifyPlayers-Thread").start();
    }

    // --- Reload Shop dong thoi xoa strOption cu ---
    private static void reloadStore() {
        synchronized (Store.getInstance().items) {
            Store.getInstance().items.clear();
            Store.getInstance().load();
        }
    }

    private static void reloadDiscountStore() {
        synchronized (DiscountStore.getInstance().items) {
            DiscountStore.getInstance().items.clear();
            DiscountStore.getInstance().load();
        }
    }

    // --- Handler chung cho tat ca cac endpoint reload ---
    private static class ReloadHandler implements HttpHandler {
        private final String target;

        ReloadHandler(String target) {
            this.target = target;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            if (!"GET".equalsIgnoreCase(method)) {
                sendResponse(exchange, 405, "{\"error\":\"Chi chap nhan GET\"}");
                return;
            }

            String query = exchange.getRequestURI().getQuery();
            String providedToken = getQueryParam(query, "token");
            AdminApiServer apiServer = AdminApiServer.gI();
            if (providedToken == null || !providedToken.equals(apiServer.token)) {
                sendResponse(exchange, 401, "{\"error\":\"Token khong hop le\"}");
                return;
            }

            long startTime = System.currentTimeMillis();
            String ip = exchange.getRemoteAddress().getAddress().getHostAddress();

            try {
                Map<String, Object> result = new HashMap<>();
                AtomicInteger codesBefore = new AtomicInteger(DataCode.Codes.size());

                switch (target) {
                    case "giftcode": {
                        int sizeBefore = DataCode.Codes.size();
                        DataCode.Codes.clear();
                        DbMore.LoadGiftCode();
                        result.put("codes_loaded", DataCode.Codes.size());
                        result.put("message", "Reload GiftCode thanh cong!");
                        Log.info("[AdminApiServer] Reload GiftCode tu " + ip + ": "
                            + sizeBefore + " -> " + DataCode.Codes.size() + " codes");
                        notifyOnlinePlayers("[GiftCode] Da cap nhat ma qua tang!");
                        break;
                    }

                    case "shop": {
                        int sizeBefore = Store.getInstance().items.size();
                        reloadStore();
                        result.put("items_loaded", Store.getInstance().items.size());
                        result.put("message", "Reload Shop thanh cong!");
                        Log.info("[AdminApiServer] Reload Shop tu " + ip + ": "
                            + sizeBefore + " -> " + Store.getInstance().items.size() + " items");
                        notifyOnlinePlayers("[Shop] Da cap nhat du lieu cua hang!");
                        break;
                    }

                    case "discount": {
                        int sizeBefore = DiscountStore.getInstance().items.size();
                        reloadDiscountStore();
                        result.put("items_loaded", DiscountStore.getInstance().items.size());
                        result.put("message", "Reload Discount Store thanh cong!");
                        Log.info("[AdminApiServer] Reload Discount Store tu " + ip + ": "
                            + sizeBefore + " -> " + DiscountStore.getInstance().items.size() + " items");
                        notifyOnlinePlayers("[Shop] Da cap nhat du lieu giam gia!");
                        break;
                    }

                    case "phucloi":
                        PhucLoi.getInstance().loadWelfare();
                        PhucLoi.getInstance().load();
                        result.put("message", "Reload PhucLoi thanh cong!");
                        Log.info("[AdminApiServer] Reload PhucLoi tu " + ip);
                        notifyOnlinePlayers("[PhucLoi] Da cap nhat phuc loi!");
                        break;

                    case "shop_rank":
                        Manager.gI().readShopRank();
                        result.put("message", "Reload Shop Rank thanh cong!");
                        Log.info("[AdminApiServer] Reload Shop Rank tu " + ip);
                        notifyOnlinePlayers("[Shop] Da cap nhat shop hang!");
                        break;

                    case "item":
                        Manager.gI().loadItem();
                        result.put("message", "Reload Item thanh cong!");
                        Log.info("[AdminApiServer] Reload Item tu " + ip);
                        break;

                    case "danhhieu":
                        Manager.gI().loadDanhHieuNew();
                        result.put("message", "Reload Danh Hieu thanh cong!");
                        Log.info("[AdminApiServer] Reload Danh Hieu tu " + ip);
                        break;

                    case "event":
                        result.put("message", "Reload Event thanh cong!");
                        Log.info("[AdminApiServer] Reload Event tu " + ip);
                        break;

                    case "all": {
                        Map<String, String> reloadResults = new HashMap<>();

                        int gcBefore = DataCode.Codes.size();
                        DataCode.Codes.clear();
                        DbMore.LoadGiftCode();
                        reloadResults.put("giftcode", gcBefore + " -> " + DataCode.Codes.size() + " codes");
                        notifyOnlinePlayers("[GiftCode] Da cap nhat ma qua tang!");

                        int shopBefore = Store.getInstance().items.size();
                        reloadStore();
                        reloadResults.put("shop", shopBefore + " -> " + Store.getInstance().items.size() + " items");
                        notifyOnlinePlayers("[Shop] Da cap nhat du lieu cua hang!");

                        int discBefore = DiscountStore.getInstance().items.size();
                        reloadDiscountStore();
                        reloadResults.put("discount", discBefore + " -> " + DiscountStore.getInstance().items.size() + " items");
                        notifyOnlinePlayers("[Shop] Da cap nhat giam gia!");

                        PhucLoi.getInstance().loadWelfare();
                        PhucLoi.getInstance().load();
                        reloadResults.put("phucloi", "OK");
                        notifyOnlinePlayers("[PhucLoi] Da cap nhat phuc loi!");

                        Manager.gI().readShopRank();
                        reloadResults.put("shop_rank", "OK");

                        Manager.gI().loadItem();
                        reloadResults.put("item", "OK");

                        Manager.gI().loadDanhHieuNew();
                        reloadResults.put("danhhieu", "OK");

                        result.put("reload_details", reloadResults);
                        result.put("message", "Reload All thanh cong!");
                        Log.info("[AdminApiServer] Reload ALL tu " + ip + " | GiftCode: "
                            + DataCode.Codes.size() + " codes | Shop: " + Store.getInstance().items.size());
                        break;
                    }

                    default:
                        sendResponse(exchange, 404, "{\"error\":\"Endpoint khong ton tai\"}");
                        return;
                }

                result.put("success", true);
                result.put("elapsed_ms", System.currentTimeMillis() - startTime);
                result.put("from_ip", ip);
                sendResponse(exchange, 200, toJson(result));

            } catch (Exception e) {
                Log.error("[AdminApiServer] Loi reload " + target + ": " + e.getMessage(), e);
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("error", "Loi reload: " + e.getMessage());
                error.put("elapsed_ms", System.currentTimeMillis() - startTime);
                sendResponse(exchange, 500, toJson(error));
            }
        }
    }

    // --- Handler kiem tra trang thai ---
    private static class StatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Chi chap nhan GET\"}");
                return;
            }

            Map<String, Object> status = new HashMap<>();
            status.put("success", true);
            status.put("status", "running");
            status.put("giftcode_count", DataCode.Codes.size());
            status.put("shop_count", Store.getInstance().items.size());
            status.put("discount_count", DiscountStore.getInstance().items.size());
            status.put("online_count", ServerManager.getChars().size());
            status.put("timestamp", System.currentTimeMillis());
            sendResponse(exchange, 200, toJson(status));
        }
    }

    // --- Utilities ---
    private static void sendResponse(HttpExchange exchange, int statusCode, String response) {
        try {
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
            byte[] bytes = response.getBytes("UTF-8");
            exchange.sendResponseHeaders(statusCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        } catch (IOException e) {
            Log.error("[AdminApiServer] Loi gui response: " + e.getMessage());
        }
    }

    private static String getQueryParam(String query, String param) {
        if (query == null || query.isEmpty()) return null;
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2 && kv[0].equals(param)) {
                return kv[1];
            }
        }
        return null;
    }

    private static String toJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) sb.append(",");
            first = false;
            sb.append("\"").append(entry.getKey()).append("\":");
            Object v = entry.getValue();
            if (v instanceof Map) {
                sb.append(toJson((Map<String, Object>) v));
            } else if (v instanceof String) {
                sb.append("\"").append(escapeJson(v.toString())).append("\"");
            } else if (v instanceof Boolean || v instanceof Number) {
                sb.append(v);
            } else if (v == null) {
                sb.append("null");
            } else {
                sb.append("\"").append(escapeJson(v.toString())).append("\"");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
