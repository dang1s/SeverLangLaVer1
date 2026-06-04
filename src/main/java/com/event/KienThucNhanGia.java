package com.event;

import Data.TYPEMENU;
import Manager.Manager;
import MapService.Map;
import MapService.Zone;
import com.sg188.lib.Utlis;
import com.sg188.real.Char;
import com.sg188.real.Item;
import com.sg188.real.Npc;
import com.sg188.real.XYEntity;
import com.sg188.server.Main;
import com.sg188.server.lib.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class KienThucNhanGia {

    public static boolean OPENED;

    private static final int[] REWARD_ITEM_IDS = {174, 175, 179, 216, 217, 218, 218, 248, 278, 302, 315};

    private static final String[][] DEFAULT_QUESTION_BANK = {
            {"Làng Lá nằm ở khu vực nào?", "Hỏa quốc", "Phong quốc", "Thủy quốc"},
            {"Hokage đệ nhất là ai?", "Hashirama", "Tobirama", "Hiruzen"},
            {"Hokage đệ nhị là ai?", "Tobirama", "Hashirama", "Hiruzen"},
            {"Hokage đệ tam là ai?", "Hiruzen", "Minato", "Tsunade"},
            {"Hokage đệ tứ là ai?", "Minato", "Kakashi", "Naruto"},
            {"Hokage đệ ngũ là ai?", "Tsunade", "Sakura", "Kurenai"},
            {"Hokage đệ lục là ai?", "Kakashi", "Naruto", "Jiraiya"},
            {"Hokage đệ thất là ai?", "Naruto", "Sasuke", "Boruto"},
            {"Sharingan thuộc gia tộc nào?", "Uchiha", "Hyuga", "Senju"},
            {"Byakugan thuộc gia tộc nào?", "Hyuga", "Uchiha", "Nara"},
            {"Vĩ thú chín đuôi tên gì?", "Kurama", "Shukaku", "Gyuki"},
            {"Vĩ thú một đuôi tên gì?", "Shukaku", "Kurama", "Isobu"},
            {"Vĩ thú tám đuôi tên gì?", "Gyuki", "Kurama", "Shukaku"},
            {"Sấm sét thuộc hệ gì?", "Lôi", "Phong", "Hỏa"},
            {"Hệ phong là hệ gì?", "Gió", "Lửa", "Sấm"},
            {"Hệ thổ là hệ gì?", "Đất", "Lửa", "Gió"},
            {"Ai là thầy đầu tiên của Naruto?", "Iruka", "Kakashi", "Jiraiya"},
            {"Ai là thầy của Team 7?", "Kakashi", "Asuma", "Gai"},
            {"Ai là thầy của Sasuke lúc còn ở Konoha?", "Kakashi", "Orochimaru", "Itachi"},
            {"Ai là thầy của Sakura?", "Tsunade", "Kurenai", "Anko"},
            {"Ai là cha của Naruto?", "Minato", "Jiraiya", "Kakashi"},
            {"Ai là mẹ của Naruto?", "Kushina", "Tsunade", "Kurenai"},
            {"Ai là anh trai của Sasuke?", "Itachi", "Shisui", "Obito"},
            {"Ai là cha của Sasuke?", "Fugaku", "Itachi", "Obito"},
            {"Ai là mẹ của Sasuke?", "Mikoto", "Kushina", "Tsunade"},
            {"Ai là người sáng lập Làng Lá?", "Hashirama", "Tobirama", "Madara"},
            {"Ai là người sáng lập Làng Sương Mù?", "Tobirama", "Hashirama", "Yagura"},
            {"Ai là người sáng lập Làng Cát?", "Rasa", "Gaara", "Chiyo"},
            {"Ai là người sáng lập Làng Mây?", "Raikage", "Danzo", "Darui"},
            {"Naruto thuộc gia tộc nào?", "Uzumaki", "Uchiha", "Hyuga"},
            {"Sasuke thuộc gia tộc nào?", "Uchiha", "Senju", "Nara"},
            {"Hinata thuộc gia tộc nào?", "Hyuga", "Uzumaki", "Haruno"},
            {"Shikamaru thuộc gia tộc nào?", "Nara", "Akimichi", "Yamanaka"},
            {"Ino thuộc gia tộc nào?", "Yamanaka", "Nara", "Hyuga"},
            {"Choji thuộc gia tộc nào?", "Akimichi", "Yamanaka", "Aburame"},
            {"Jiraiya là một trong bộ ba gì?", "Tam nin huyền thoại", "Thất kiếm làng sương", "Cửu vĩ nhân trụ lực"},
            {"Tsunade nổi tiếng với khả năng gì?", "Y thuật", "Ảo thuật", "Phong ấn vĩ thú"},
            {"Orochimaru nổi tiếng với gì?", "Thí nghiệm cấm thuật", "Y thuật", "Điều khiển cát"},
            {"Kakashi còn được gọi là gì?", "Ninja sao chép", "Ác quỷ làng sương", "Thần tốc Konoha"},
            {"Gaara điều khiển nguyên tố nào?", "Cát", "Nước", "Lửa"},
            {"Temari thường sử dụng vũ khí gì?", "Quạt", "Kiếm", "Cung"},
            {"Kankuro nổi tiếng với gì?", "Rối", "Cát", "Phong ấn"},
            {"Neji sở hữu nhãn thuật gì?", "Byakugan", "Sharingan", "Rinnegan"},
            {"Lee nổi bật ở lĩnh vực nào?", "Thể thuật", "Ảo thuật", "Y thuật"},
            {"Gai là thầy của ai?", "Lee", "Naruto", "Shikamaru"},
            {"Asuma là thầy của đội nào?", "Đội 10", "Đội 7", "Đội 8"},
            {"Kurenai là thầy của đội nào?", "Đội 8", "Đội 7", "Đội 10"},
            {"Akatsuki là tổ chức gì?", "Tổ chức tội phạm", "Lực lượng cảnh vệ", "Đơn vị y tế"},
            {"Pain tên thật là gì?", "Nagato", "Yahiko", "Konan"},
            {"Konan thường dùng năng lực gì?", "Giấy", "Băng", "Đất"},
            {"Itachi từng thuộc tổ chức nào?", "Akatsuki", "Anbu gốc", "Thất kiếm làng sương"},
            {"Obito thường đeo mặt nạ có danh nghĩa gì?", "Tobi", "Pain", "Zabuza"},
            {"Rinnegan xuất hiện đầu tiên nổi bật ở ai?", "Nagato", "Kakashi", "Gaara"},
            {"Cửu vĩ được phong ấn trong ai?", "Naruto", "Sasuke", "Gaara"},
            {"Nhất vĩ được phong ấn trong ai?", "Gaara", "Naruto", "Bee"},
            {"Bát vĩ là cộng sự của ai?", "Killer Bee", "Darui", "A"},
            {"Minato nổi tiếng với thuật gì?", "Phi lôi thần", "Chidori", "Sa trói"},
            {"Sasuke thường dùng chiêu điện nào?", "Chidori", "Rasengan", "Kage mane"},
            {"Naruto nổi tiếng với chiêu gì?", "Rasengan", "Chidori", "Amaterasu"},
            {"Amaterasu là ngọn lửa gì?", "Lửa đen", "Lửa xanh", "Lửa trắng"},
            {"Susanoo thường gắn với nhãn thuật nào?", "Mangekyo Sharingan", "Byakugan", "Tiên thuật"},
            {"Bạch nhãn cho khả năng gì?", "Nhìn xuyên và quan sát chakra", "Sao chép nhẫn thuật", "Hồi sinh"},
            {"Sharingan cho khả năng gì?", "Quan sát và sao chép", "Điều khiển cát", "Tạo giấy nổ"},
            {"Konoha còn gọi là gì?", "Làng Lá", "Làng Cát", "Làng Mây"},
            {"Sunagakure còn gọi là gì?", "Làng Cát", "Làng Lá", "Làng Đá"},
            {"Kirigakure còn gọi là gì?", "Làng Sương Mù", "Làng Mây", "Làng Cỏ"},
            {"Kumogakure còn gọi là gì?", "Làng Mây", "Làng Đá", "Làng Mưa"},
            {"Iwagakure còn gọi là gì?", "Làng Đá", "Làng Mây", "Làng Sương"},
            {"Amegakure còn gọi là gì?", "Làng Mưa", "Làng Cỏ", "Làng Cát"}
    };

    public static final List<String> listQuest = Manager.listQuest;
    public static String[][] listAnswer = Manager.listAnswer;
    public static String[] listAnswerCorrect = Manager.listAnswerCorrect;

    private static final short DEFAULT_MAP_ID = 86;
    private static final short NPC_ID = 30;
    private static volatile short activeMapId = DEFAULT_MAP_ID;
    private static volatile boolean moving = false;
    private static volatile Thread moveThread;
    private static final ConcurrentHashMap<Integer, Integer> runtimeNpcIndexByZone = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, Npc> runtimeNpcByZone = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, Short> runtimeNpcYByZone = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, Boolean> runtimeMoveRightByZone = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, Boolean> runtimeFlyModeByZone = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, Boolean> runtimeMovingPhaseByZone = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, Long> runtimePhaseUntilByZone = new ConcurrentHashMap<>();
    private static final int MOVE_MIN_STEP = 7;
    private static final int MOVE_MAX_STEP = 7;
    private static final int MOVE_TICK_MS = 250;
    private static final long MOVE_PHASE_MS = 30_000L;
    private static final long PAUSE_PHASE_MS = 15_000L;

    static {
        ensureQuestionBankLoaded();
    }

    public static void randomQuest(Char ch) {
        int questionCount = getAvailableQuestionCount();
        if (questionCount <= 0) {
            ch.indexQuestion = -1;
            return;
        }
        if (ch.listQuestionUsed.size() >= questionCount) {
            ch.listQuestionUsed.clear();
        }
        if (ch.listQuestionUsed.isEmpty()) {
            ch.indexQuestion = Utlis.nextInt(0, questionCount - 1);
            return;
        }
        int indexQuest;
        do {
            indexQuest = Utlis.nextInt(0, questionCount - 1);
        } while (ch.listQuestionUsed.contains(indexQuest));
        ch.indexQuestion = indexQuest;
    }

    public static void showMenuSelect(Char ch) {
        if (ch.doneKienThucNhanGia) {
            ch.service.sendTextNPC("Bạn đã hoàn thành Kiến Thức Nhẫn Giả rồi, không thể tham gia tiếp.", "");
            return;
        }
        ch.typeMenu = TYPEMENU.MENU_SELELCT_KIEN_THUC_NHAN_GIA;
        ch.service.sendTextNPC("Xin chào " + ch.Info.name + "?", "Trắc nghiệm kiến thức nhẫn giả");
    }

    public static void showQuestion(Char ch) {
        if (ch == null) {
            return;
        }
        if (ch.doneKienThucNhanGia) {
            ch.service.sendTextNPC("Bạn đã hoàn thành Kiến Thức Nhẫn Giả rồi, không thể tham gia tiếp.", "");
            return;
        }
        if (System.currentTimeMillis() < ch.timeWaitKTND) {
            showWaitMessage(ch);
            return;
        }
        if (ch.pointCorrectQuestionKTND >= 30) {
            ch.service.sendTextNPC("Các hạ đã hoàn thành 30 câu hỏi, hãy tới chỗ Ginkaku để nhận thưởng.", "");
            return;
        }
        if (ch.indexQuestion == -1) {
            randomQuest(ch);
        }
        if (!hasQuestionData(ch.indexQuestion)) {
            ch.service.alertMessage("Dữ liệu câu hỏi Kiến Thức Nhẫn Giả đang thiếu.");
            return;
        }

        prepareQuestionForPlayer(ch);
        ch.typeMenu = TYPEMENU.MENU_SELELCT_KIEN_THUC_NHAN_GIA;
        ch.service.sendTextNPC(getQuestionText(ch.indexQuestion), buildCurrentPlayerOptions(ch));
    }

    public static void checkAnser(Char ch, String answerPlayer) {
        try {
            if (isCorrectPlayerAnswer(ch, answerPlayer)) {
                continuteQuestion(ch);
            } else {
                falseQuestion(ch);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void checkAnswer(Char ch, byte answerIndex) {
        try {
            if (ch == null) {
                return;
            }
            if (ch.doneKienThucNhanGia) {
                ch.service.alertMessage("Bạn đã hoàn thành Kiến Thức Nhẫn Giả rồi.");
                return;
            }
            if (!OPENED) {
                ch.service.alertMessage("Chưa tới thời gian hoạt động Kiến Thức Nhẫn Giả");
                return;
            }
            if (System.currentTimeMillis() < ch.timeWaitKTND) {
                showWaitMessage(ch);
                return;
            }
            if (answerIndex < 0 || ch.currentQuestionOptionsKTNG == null || answerIndex >= ch.currentQuestionOptionsKTNG.length) {
                ch.service.alertMessage("Đáp án không hợp lệ.");
                return;
            }

            String answerPlayer = ch.currentQuestionOptionsKTNG[answerIndex];
            if (isCorrectPlayerAnswer(ch, answerPlayer)) {
                continuteQuestion(ch);
            } else {
                falseQuestion(ch);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void rewardFinish(Char ch) {
        if (ch.pointCorrectQuestionKTND == 30) {
            ch.doneKienThucNhanGia = true;
            ch.pointCorrectQuestionKTND = 0;
            ch.indexQuestion = -1;
            ch.timeWaitKTND = 0;
            ch.listQuestionUsed.clear();
            ch.currentQuestionOptionsKTNG = null;
            ch.currentCorrectAnswerKTNG = null;
            applyCompletionBenefits(ch);
        } else {
            ch.service.sendTextNPC("Bạn cần phải hoàn thành 30 câu hỏi từ Tsunade mới có thể nhận thưởng nha.", "");
        }
    }

    public static void continuteQuestion(Char ch) {
        if (!finishQuestion(ch)) {
            ch.listQuestionUsed.add(ch.indexQuestion);
            randomQuest(ch);
            ch.pointCorrectQuestionKTND++;
            waitContinuteQuestion(ch);
        }
    }

    public static boolean finishQuestion(Char ch) {
        if (ch.pointCorrectQuestionKTND == 29) {
            ch.pointCorrectQuestionKTND++;
            rewardFinish(ch);
            return true;
        }
        return false;
    }

    public static void waitContinuteQuestion(Char ch) {
        switch (ch.pointCorrectQuestionKTND) {
            case 10, 15, 24 -> {
                ch.service.sendTextNPC(
                        "Các hạ trả lời rất tốt, hãy chờ thêm 60 giây để trả lời câu hỏi tiếp theo.\nHiện tại đã hoàn thành "
                                + ch.pointCorrectQuestionKTND
                                + " câu hỏi, hoàn thành 30 câu hỏi sẽ có phần thưởng ở chỗ Ginkaku.",
                        ""
                );
                ch.timeWaitKTND = System.currentTimeMillis() + 60000;
            }
            default -> {
                if (!hasQuestionData(ch.indexQuestion)) {
                    ch.service.alertMessage("Dữ liệu câu hỏi Kiến Thức Nhẫn Giả đang thiếu.");
                    return;
                }
                prepareQuestionForPlayer(ch);
                ch.typeMenu = TYPEMENU.MENU_SELELCT_KIEN_THUC_NHAN_GIA;
                ch.service.sendTextNPC(getQuestionText(ch.indexQuestion), buildCurrentPlayerOptions(ch));
            }
        }
    }

    public static void falseQuestion(Char ch) {
        ch.timeWaitKTND = System.currentTimeMillis() + 15000;
        ch.service.sendTextNPC("Trả lời sai, vui lòng chờ 15 giây", "");
    }

    private static synchronized void ensureQuestionBankLoaded() {
        if (hasUsableQuestionBank()) {
            return;
        }

        if (listAnswer.length < DEFAULT_QUESTION_BANK.length) {
            String[][] expandedAnswers = new String[DEFAULT_QUESTION_BANK.length][4];
            System.arraycopy(listAnswer, 0, expandedAnswers, 0, listAnswer.length);
            listAnswer = expandedAnswers;
            Manager.listAnswer = expandedAnswers;
        }
        if (listAnswerCorrect.length < DEFAULT_QUESTION_BANK.length) {
            String[] expandedCorrectAnswers = new String[DEFAULT_QUESTION_BANK.length];
            System.arraycopy(listAnswerCorrect, 0, expandedCorrectAnswers, 0, listAnswerCorrect.length);
            listAnswerCorrect = expandedCorrectAnswers;
            Manager.listAnswerCorrect = expandedCorrectAnswers;
        }

        listQuest.clear();
        for (int i = 0; i < listAnswer.length; i++) {
            listAnswer[i] = new String[4];
        }

        for (int i = 0; i < DEFAULT_QUESTION_BANK.length; i++) {
            String[] row = DEFAULT_QUESTION_BANK[i];
            listQuest.add(row[0]);
            listAnswer[i][0] = row[1];
            listAnswer[i][1] = row[2];
            listAnswer[i][2] = row[3];
            listAnswer[i][3] = "";
            listAnswerCorrect[i] = row[1];
        }
    }

    private static boolean hasUsableQuestionBank() {
        if (listQuest.size() < DEFAULT_QUESTION_BANK.length || listAnswerCorrect.length < DEFAULT_QUESTION_BANK.length) {
            return false;
        }
        for (int i = 0; i < DEFAULT_QUESTION_BANK.length; i++) {
            if (listQuest.get(i) == null || listQuest.get(i).isBlank()) {
                return false;
            }
            if (listAnswer[i] == null || listAnswer[i].length < 3) {
                return false;
            }
            if (listAnswerCorrect[i] == null || listAnswerCorrect[i].isBlank()) {
                return false;
            }
        }
        return true;
    }

    private static void prepareQuestionForPlayer(Char ch) {
        String correctAnswer = listAnswerCorrect[ch.indexQuestion];
        List<String> shuffled = new ArrayList<>();
        for (String answer : listAnswer[ch.indexQuestion]) {
            if (answer != null && !answer.isBlank()) {
                shuffled.add(answer);
            }
        }
        Collections.shuffle(shuffled);
        ch.currentQuestionOptionsKTNG = shuffled.toArray(new String[0]);
        ch.currentCorrectAnswerKTNG = correctAnswer;
    }

    private static String buildCurrentPlayerOptions(Char ch) {
        if (ch.currentQuestionOptionsKTNG == null || ch.currentQuestionOptionsKTNG.length == 0) {
            prepareQuestionForPlayer(ch);
        }
        return String.join(";", ch.currentQuestionOptionsKTNG);
    }

    private static boolean isCorrectPlayerAnswer(Char ch, String answerPlayer) {
        if (ch == null || ch.currentCorrectAnswerKTNG == null || answerPlayer == null) {
            return false;
        }
        return normalizeAnswer(ch.currentCorrectAnswerKTNG).equals(normalizeAnswer(answerPlayer));
    }

    private static void applyCompletionBenefits(Char ch) {
        ch.Info.chuyenCan += 5;
        ch.Info.chuyenCanTuan += 5;
        ch.msgUpdateDataChar();
        ch.msgGetInfo();
        grantRandomRewardItem(ch);
    }

    private static void grantRandomRewardItem(Char ch) {
        if (ch.getCountNullItemBag() < 3) {
            ch.warningBagFull();
            ch.service.serverMessage("Bạn đã hoàn thành Kiến Thức Nhẫn Giả, nhận +5 chuyên cần và +5 chuyên cần tuần. Túi đầy nên chưa nhận được 3 vật phẩm thưởng.");
            return;
        }

        for (int i = 0; i < 3; i++) {
            int rewardId = REWARD_ITEM_IDS[ThreadLocalRandom.current().nextInt(REWARD_ITEM_IDS.length)];
            Item rewardItem = new Item(rewardId);
            rewardItem.isLock = true;
            rewardItem.amount = 1;
            ch.addItem(rewardItem);
            ch.msgAddItemBag(rewardItem);
        }
        ch.service.serverMessage("Bạn đã hoàn thành Kiến Thức Nhẫn Giả, nhận +5 chuyên cần, +5 chuyên cần tuần và ngẫu nhiên 3 lệnh bài.");
    }

    private static int getAvailableQuestionCount() {
        ensureQuestionBankLoaded();
        return Math.min(Math.min(listQuest.size(), listAnswer.length), listAnswerCorrect.length);
    }

    private static boolean hasQuestionData(int questionIndex) {
        return questionIndex >= 0
                && questionIndex < getAvailableQuestionCount()
                && questionIndex < listAnswer.length
                && listAnswer[questionIndex] != null;
    }

    private static String getQuestionText(int questionIndex) {
        return listQuest.get(questionIndex);
    }

    private static String normalizeAnswer(String value) {
        return value == null ? "" : value.trim().replace('\u00A0', ' ').replaceAll("\\s+", " ").toLowerCase();
    }

    private static void showWaitMessage(Char ch) {
        long remainMs = Math.max(0L, ch.timeWaitKTND - System.currentTimeMillis());
        long remainSecond = (remainMs + 999L) / 1000L;
        ch.service.sendTextNPC("Các hạ cần chờ thêm " + remainSecond + " giây để tiếp tục trả lời câu hỏi.", "");
    }

    public static boolean isEventRunning() {
        return OPENED;
    }

    public static synchronized void startEvent() {
        OPENED = true;
        try {
            activeMapId = DEFAULT_MAP_ID;
            ensureRuntimeNpc();
            setNpcVisible(true);
            startMoveThread();
            Main.HeThongCTG("KTNG đã mở, Tsunade đang ở Trường Konoha", 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized void stopEvent() {
        OPENED = false;
        moving = false;
        try {
            if (moveThread != null) {
                moveThread.interrupt();
                moveThread = null;
            }
            setNpcVisible(false);
            removeRuntimeNpc();
            Main.HeThongCTG("KTNG đã đóng", 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void ensureRuntimeNpc() {
        try {
            Map map = Map.maps[activeMapId];
            if (map == null || map.getZones() == null || map.getZones().isEmpty()) {
                return;
            }
            runtimeNpcIndexByZone.clear();
            runtimeNpcByZone.clear();
            runtimeNpcYByZone.clear();
            runtimeMoveRightByZone.clear();
            runtimeFlyModeByZone.clear();
            runtimeMovingPhaseByZone.clear();
            runtimePhaseUntilByZone.clear();
            for (Zone zone : map.getZones()) {
                if (zone == null || zone.npcs == null || zone.npcs.isEmpty()) {
                    continue;
                }
                Npc base = zone.npcs.get(0);
                if (base == null) {
                    continue;
                }
                Npc quiz = base.cloneNpc();
                if (quiz == null) {
                    continue;
                }
                quiz.id = NPC_ID;
                quiz.status = 2;
                quiz.dir = 2;
                quiz.cx = (short) 600;
                quiz.cy = (short) 430;
                quiz.idEntity = zone.npcs.size();
                zone.npcs.add(quiz);
                int newIdx = zone.npcs.size() - 1;
                runtimeNpcIndexByZone.put(zone.zoneID, newIdx);
                runtimeNpcByZone.put(zone.zoneID, quiz);
                runtimeNpcYByZone.put(zone.zoneID, quiz.cy);
                runtimeMoveRightByZone.put(zone.zoneID, true);
                runtimeFlyModeByZone.put(zone.zoneID, false);
                runtimeMovingPhaseByZone.put(zone.zoneID, true);
                runtimePhaseUntilByZone.put(zone.zoneID, System.currentTimeMillis() + MOVE_PHASE_MS);

                for (Char p : zone.players) {
                    if (p != null && p.user != null && p.service != null) {
                        p.service.sendIntoMap();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void removeRuntimeNpc() {
        try {
            Map map = Map.maps[activeMapId];
            if (map == null || map.getZones() == null || map.getZones().isEmpty()) {
                return;
            }
            for (Zone zone : map.getZones()) {
                if (zone == null || zone.npcs == null || zone.npcs.isEmpty()) {
                    continue;
                }
                Npc runtimeNpc = runtimeNpcByZone.get(zone.zoneID);
                if (runtimeNpc != null) {
                    zone.npcs.remove(runtimeNpc);
                    for (int i = 0; i < zone.npcs.size(); i++) {
                        Npc npc = zone.npcs.get(i);
                        if (npc != null) {
                            npc.idEntity = i;
                        }
                    }
                    for (Char p : zone.players) {
                        if (p != null && p.user != null && p.service != null) {
                            p.service.sendIntoMap();
                        }
                    }
                }
            }
            runtimeNpcIndexByZone.clear();
            runtimeNpcByZone.clear();
            runtimeNpcYByZone.clear();
            runtimeMoveRightByZone.clear();
            runtimeFlyModeByZone.clear();
            runtimeMovingPhaseByZone.clear();
            runtimePhaseUntilByZone.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setNpcVisible(boolean visible) {
        Map map = Map.maps[activeMapId];
        if (map == null || map.getZones() == null || map.getZones().isEmpty()) {
            return;
        }
        for (Zone zone : map.getZones()) {
            if (zone == null || zone.npcs == null) {
                continue;
            }
            Npc runtimeNpc = runtimeNpcByZone.get(zone.zoneID);
            if (runtimeNpc == null) {
                continue;
            }
            runtimeNpc.status = (byte) (visible ? 2 : 0);
            Integer idx = runtimeNpcIndexByZone.get(zone.zoneID);
            if (idx == null || idx < 0 || idx >= zone.npcs.size() || zone.npcs.get(idx) != runtimeNpc) {
                int realIdx = zone.npcs.indexOf(runtimeNpc);
                if (realIdx < 0) {
                    continue;
                }
                idx = realIdx;
                runtimeNpcIndexByZone.put(zone.zoneID, realIdx);
            }
            broadcastNpcUpdate(zone, idx, runtimeNpc);
        }
    }

    private static void startMoveThread() {
        if (moving) {
            return;
        }
        moving = true;
        moveThread = new Thread(() -> {
            while (moving && OPENED && !Main.BaoTri) {
                try {
                    Map map = Map.maps[activeMapId];
                    if (map != null && map.getZones() != null) {
                        for (Zone zone : map.getZones()) {
                            if (zone == null || zone.npcs == null) {
                                continue;
                            }
                            Npc npc = runtimeNpcByZone.get(zone.zoneID);
                            if (npc == null) {
                                continue;
                            }
                            Integer runtimeIdx = runtimeNpcIndexByZone.get(zone.zoneID);
                            if (runtimeIdx == null || runtimeIdx < 0 || runtimeIdx >= zone.npcs.size() || zone.npcs.get(runtimeIdx) != npc) {
                                int realIdx = zone.npcs.indexOf(npc);
                                if (realIdx < 0) {
                                    continue;
                                }
                                runtimeIdx = realIdx;
                                runtimeNpcIndexByZone.put(zone.zoneID, runtimeIdx);
                            }

                            moveNpcRandom(zone, npc);
                            broadcastNpcUpdate(zone, runtimeIdx, npc);
                        }
                    }
                    Thread.sleep(MOVE_TICK_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            moving = false;
        }, "KTNG-Move-Tsunade");
        moveThread.setDaemon(true);
        moveThread.start();
    }

    private static void moveNpcRandom(Zone zone, Npc npc) {
        long now = System.currentTimeMillis();
        boolean movingPhase = runtimeMovingPhaseByZone.getOrDefault(zone.zoneID, true);
        long phaseUntil = runtimePhaseUntilByZone.getOrDefault(zone.zoneID, now + MOVE_PHASE_MS);

        if (now >= phaseUntil) {
            movingPhase = !movingPhase;
            runtimeMovingPhaseByZone.put(zone.zoneID, movingPhase);
            runtimePhaseUntilByZone.put(zone.zoneID, now + (movingPhase ? MOVE_PHASE_MS : PAUSE_PHASE_MS));
            if (movingPhase && ThreadLocalRandom.current().nextBoolean()) {
                runtimeMoveRightByZone.put(zone.zoneID, !runtimeMoveRightByZone.getOrDefault(zone.zoneID, true));
            }
        }

        boolean moveRight = runtimeMoveRightByZone.getOrDefault(zone.zoneID, true);
        int minX = 120;
        int maxX = 1750;
        int baseY = runtimeNpcYByZone.getOrDefault(zone.zoneID, (short) 430);

        if (!movingPhase) {
            npc.status = 0;
            return;
        }

        int step = ThreadLocalRandom.current().nextInt(MOVE_MIN_STEP, MOVE_MAX_STEP + 1);
        int nextX = npc.cx + (moveRight ? step : -step);
        if (nextX >= maxX) {
            nextX = maxX;
            moveRight = false;
        } else if (nextX <= minX) {
            nextX = minX;
            moveRight = true;
        }

        int probeY = npc.cy;
        int groundY = -1;
        if (zone != null) {
            XYEntity ground = zone.getXYBlockMapNotCheck(nextX, probeY);
            if (ground != null) {
                groundY = ground.cy;
            }
        }

        boolean flyMode;
        int nextY;
        int dropThreshold = baseY + 18;
        boolean hasSafeGround = groundY > 0 && Math.abs(groundY - probeY) <= 36 && groundY <= dropThreshold;
        if (hasSafeGround) {
            flyMode = false;
            nextY = groundY;
            npc.status = 2;
        } else {
            flyMode = true;
            int flyCenter = Math.max(360, baseY - 35);
            int flyTop = flyCenter - 30;
            int flyBottom = flyCenter + 12;
            int deltaY = ThreadLocalRandom.current().nextInt(-3, 4);
            nextY = npc.cy + deltaY;
            if (nextY < flyTop) {
                nextY = flyTop;
            }
            if (nextY > flyBottom) {
                nextY = flyBottom;
            }
            npc.status = 7;
        }

        npc.cx = (short) nextX;
        npc.cy = (short) nextY;
        npc.dir = (byte) (moveRight ? 2 : 3);

        runtimeMoveRightByZone.put(zone.zoneID, moveRight);
        runtimeFlyModeByZone.put(zone.zoneID, flyMode);
    }

    private static void broadcastNpcUpdate(Zone zone, int npcIndex, Npc npc) {
        try {
            Message m = new Message((byte) 2);
            m.writeShort(npcIndex);
            m.writeByte(npc.status);
            m.writeShort(npc.cx);
            m.writeShort(npc.cy);
            zone.SendMessageInZone(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void scheduleAutoOpen(int hour, int minute, int second) {
        Utlis.schedule(KienThucNhanGia::startEvent, hour, minute, second);
    }

    public static boolean isQuizNpcClick(Char ch, int npcIndex) {
        if (!OPENED || ch == null || ch.zone == null || ch.zone.map == null) {
            return false;
        }
        if (ch.zone.map.mapID != activeMapId) {
            return false;
        }
        if (npcIndex < 0 || npcIndex >= ch.zone.npcs.size()) {
            return false;
        }

        Integer runtimeIdx = runtimeNpcIndexByZone.get(ch.zone.zoneID);
        if (runtimeIdx == null) {
            return false;
        }
        if (runtimeIdx == npcIndex) {
            return true;
        }
        Npc runtimeNpc = runtimeNpcByZone.get(ch.zone.zoneID);
        return runtimeNpc != null && ch.zone.npcs.get(npcIndex) == runtimeNpc;
    }

    public static void onOpenQuizFromNpc(Char ch) {
        if (ch == null) {
            return;
        }
        if (ch.doneKienThucNhanGia) {
            ch.service.alertMessage("Bạn đã hoàn thành Kiến Thức Nhẫn Giả rồi, không thể tham gia tiếp.");
            return;
        }
        if (!OPENED) {
            ch.service.alertMessage("Chưa tới thời gian hoạt động Kiến Thức Nhẫn Giả");
            return;
        }

        Npc runtimeNpc = runtimeNpcByZone.get(ch.zone.zoneID);
        Integer runtimeIdx = runtimeNpcIndexByZone.get(ch.zone.zoneID);
        if (runtimeNpc != null && runtimeIdx != null) {
            boolean movingPhase = runtimeMovingPhaseByZone.getOrDefault(ch.zone.zoneID, true);
            if (!movingPhase) {
                runtimeNpc.dir = (byte) (runtimeNpc.cx < ch.Info.cx ? 2 : 3);
            }
            runtimeNpc.status = runtimeFlyModeByZone.getOrDefault(ch.zone.zoneID, false) ? (byte) 3 : (byte) 2;
            broadcastNpcUpdate(ch.zone, runtimeIdx, runtimeNpc);
        }

        if (System.currentTimeMillis() < ch.timeWaitKTND) {
            showWaitMessage(ch);
            return;
        }
        showQuestion(ch);
    }
}
