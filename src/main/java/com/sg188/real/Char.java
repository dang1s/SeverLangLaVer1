package com.sg188.real;

import Data.DataSkill;
import Data.TYPEMENU;
import EventClick.ClickEvent;
import InfoChar.InfoChar;
import InfoChar.InfoEff;
import InfoChar.InfoGame;
import InfoChar.InfoGiftCode;
import InfoChar.InfoInventory;
import InfoChar.InfoPoint;
import InfoChar.InfoSkill;
import InfoChar.PointHokage;
import InfoChar.InfoPhucLoi;
import Manager.Manager;
import MapService.Map;
import MapService.world.*;
import Service.HanderEff;
import Service.HanderMessage;
import Service.ScheduledExecutor;
import SqlConnection.CharDB;
import SqlConnection.Connect;
import Template.TemplateThu;
import com.event.Event;
import com.event.Summer;
import com.event.eventpoint.EventPoint;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.sg188.PhucLoi.PhucLoi;
import com.sg188.PhucLoi.TemplatePL;
import com.sg188.clan.Clan;
import com.sg188.clan.ClanDAO;
import com.sg188.clan.Member;
import com.sg188.data.*;
import com.sg188.lib.CaptchaUtil;
import com.sg188.lib.Log;
import com.sg188.lib.Utlis;
import com.sg188.party.Group;
import com.sg188.party.MemberGroup;
import com.sg188.server.*;
import com.sg188.server.handler.IActionItem;
import com.sg188.server.lib.Message;
import com.sg188.server.lib.Writer;

import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import MapService.*;
import Service.*;
import Data.DataCmd;

import com.sg188.task.Task;
import com.sg188.task.TaskFactory;
import com.sg188.task.TaskName;
import com.sg188.task.TaskOrder;
import lombok.Getter;
import lombok.Setter;
import market.MarketManager;
import org.apache.commons.lang3.time.DateUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Char extends Body {
    public User user;
    @Getter
    public Service service;
    public long timeCTG = 0;
    public byte id_he = -1;
    public long eff5buff = 0;
    public ArrayList<Effect> listEffect = new ArrayList<Effect>();
    public short expTemp;
    public int id;
    public int idDungeonEvent = -1;
    private long getTimeOnline;
    public boolean isGiaoItem;
    @Getter
    @Setter
    private AbsSelectCard selectCard;
    public short buffKLT;
    public Zone zone;
    public int numBox;
    public boolean isClean = false;
    public boolean createMob;
    public boolean cloneLive;
    public byte idListTVM;
    public byte idListTVMSilver;
    public short buffRuou;
    public boolean isCuuSat;
    public long delaySkill;
    public long timemsg;
    public short taskId = 1;
    public Task taskMain;
    public byte nAnswer = 0;
    public boolean buaBaoHo;
    public boolean buaUeTho;
    public byte msglastrec;
    public boolean canRevive = true;
    private List<World> worlds;// danh sách world đang tham gia
    public HashMap<String, Friend> friends, enemies;
    public int idCamThuat = -1;
    public int idKhuLuyenTap = -1;
    public int idDiaCung = -1;
    public Trade trade;
    public Trader myTrade;
    public Trader partnerTrade;
    public Invite invite;
    private boolean newDay;
    public long lastClickTime = 0;
    public long debounceDelay = 1000;
    public Lock lockEff = new ReentrantLock();
    public Lock benefits = new ReentrantLock();

    private boolean saving;
    public String nameInvite = "";
    public String inviteName = "";
    @Getter
    private Group group;
    public String captcha;
    public boolean isCatchItem;
    public ScheduledFuture<?> future;
    public boolean isWheelGold;
    public boolean isWheelSilver;
    @Setter
    private EventPoint eventPoint;
    public boolean taskSeal;
    public String typeSeal = "";
    public byte stepSeal;
    public ArrayList<TaskOrder> taskOrders;
    public byte countFinishDay;
    int countLoopBoss;
    private long lastTimeTeleport;
    public short mobBird;
    public boolean isBiDuoc;
    public boolean isSecurity;
    public boolean isUnlockSecurity;
    public int timeRemoveSecurity;
    public String passwordSecurity = "";
    public int idCharPk = -1;
    public boolean isUpdateOption;
    public long timeChangeName;
    public long timeOutClan;
    public byte typeMenu;
    private List<Item> tanto = new ArrayList<>();
    private List<Item> aoChoang = new ArrayList<>();
    private List<Item> thoiTrang = new ArrayList<>();
    public int pointDungeon;
    public boolean tuLuyenChau;
    public short timeOffline;
    public boolean inLangCo;
    public long delayHp;
    public long delayMp;
    public boolean isTyVo;
    public Lock lock = new ReentrantLock();
    public int idCuuSat;
    public int isFatal;
    public int pointDaiHoi;
    public int pointTranhDoat;
    public List<Item> huphach = new ArrayList<>();
    public boolean isDatTre;
    public boolean isCauCa;
    public Treasure treasure;
    public List<TemplateThu> letters = new ArrayList<>();
    public List<Integer> shoprank = new ArrayList<>();
    public long theGiuTien;
    public int bagAdd;


//    public boolean updateHp;
//    public boolean updateMp;
//    public long delayMS;

    public Char() {
    }

    public void DoLoginGame() {
        InfoGame = new InfoGame();
//        if (level() < 12) {
//            setExp(DataCenter.gI().GetExpFormLevel(12));
//        }
        Point.hp = maxHP;
        Point.mp = maxMP;
        CheckNextDay();
        if (Info.numct < 17) {
            Info.numct = 17;
        }
        service.resetTaskOrder(0);
        service.resetTaskOrder(1);
        if (taskOrders == null) {
            taskOrders = new ArrayList<>();
        } else {
            for (TaskOrder task : taskOrders) {
                if (task.taskId == TaskOrder.TASK_DAY)
                    service.sendTaskOrder(task);
                else
                    service.sendTaskBoss(task);
            }
        }
//        removeItemBugLevl();
        getTimeOnline = System.currentTimeMillis() + 60000;
        id = Info.idEntity;
        for (Effect effect : listEffect) {
            HanderEff.getPointEff(this, effect);
        }
        service.sendNumCt();
        checkHSD();
        getService().reloadLetter();
        this.worlds = new ArrayList<>();
        invite = new Invite();
        Clan clan = this.clan;
        if (clan != null) {
            Member mem = clan.getMemberByName(Info.name);
            if (mem != null) {
                mem.setOnline(true);
                mem.setChar(this);
            }
        }
        service.showGiaToc();
        if (isSecurity) {
            service.unlockSecurity();
        }
        setUpdateOption();

        if (timeRemoveSecurity > 0) {
            if (timeRemoveSecurity * 1000 < System.currentTimeMillis()) {
                isUnlockSecurity = false;
                isSecurity = false;
                passwordSecurity = "";
            }
        }
        if (treasure != null) {
            if (treasure.getId() < 0 || treasure.getQuantity() < 0 || (treasure.getId() > 3 && treasure.getQuantity() != 177) || treasure.getQuantity() > 6) {
                treasure = null;
            }
        }
    }

    public void extendBag(int num) {
        bagAdd += num;
        Item[] _arrItemBag = new Item[Bag.arrItemBag.length + num];
        for (int i = 0; i < Bag.arrItemBag.length; i++) {
            _arrItemBag[i] = Bag.arrItemBag[i];
        }
        Bag.arrItemBag = _arrItemBag;
    }

    public void addSlotBag() {
        if (Bag.vang < 200) {
            getService().warningMessage("Bạn không đủ vàng");
            return;
        }
        if (bagAdd >= 900) {
            getService().warningMessage("Túi đồ đã tới giới hạn");
            return;
        }
        addVang(-200);
        extendBag(9);
        getService().serverMessage("Mở rộng túi đồ thành công");
        service.sendChar();
    }


    public void setUpdateOption() {
        if (isUpdateOption) {
            return;
        }
        isUpdateOption = true;
        for (Item item : Bag.arrItemBody) {
            if (item != null && item.level >= 18 && item.getItemTemplate().type != 14) {
                int level = item.level;
                item.level = 17;
                item.a(0);
                item.a(level);
            }
        }
        for (Item item : Bag.arrItemBody2) {
            if (item != null && item.level >= 18 && item.getItemTemplate().type != 14) {
                int level = item.level;
                item.level = 17;
                item.a(0);
                item.a(level);
            }
        }
        msgUpdateItemBody();
        msgUpdateItemBody_Orther();
    }

    public void checkHSD() {
        for (Item item : Bag.arrItemBody) {
            if (item != null) {
                if (item.expiry > 0) {
                    if (item.expiry < System.currentTimeMillis()) {
                        Bag.arrItemBody[item.index] = null;
                    }
                }
                if (item.getItemTemplate().type == 14 && item.level > 18) {
                    Bag.arrItemBody[item.index] = null;
                }
            }
        }
        for (Item item : Bag.arrItemBody2) {
            if (item != null) {
                if (item.expiry > 0) {
                    if (item.expiry < System.currentTimeMillis()) {
                        Bag.arrItemBody2[item.index] = null;
                    }
                }
                if (item.getItemTemplate().type == 14 && item.level > 18) {
                    Bag.arrItemBody2[item.index] = null;
                }
            }
        }
        for (Item item : Bag.arrItemBox) {
            if (item != null) {
                if (item.expiry > 0) {
                    if (item.expiry < System.currentTimeMillis()) {
                        Bag.arrItemBox[item.index] = null;
                    }
                }
                if (item.getItemTemplate().type == 14 && item.level > 18) {
                    Bag.arrItemBox[item.index] = null;
                }
            }
        }
        for (Item item : Bag.arrItemBag) {
            if (item != null) {
                if (item.expiry > 0) {
                    if (item.expiry < System.currentTimeMillis()) {
                        removeItem(item, true);
                    }
                }
                if (item.getItemTemplate().type == 14 && item.level > 18) {
                    Bag.arrItemBag[item.index] = null;
                }
            }
        }
        msgUpdateItemBody();
        for (int i = letters.size() - 1; i >= 0; i--) {
            TemplateThu thu = letters.get(i);
            if (thu.TimeEnd <= System.currentTimeMillis()) {
                letters.remove(i);
            }
        }
    }


    public Char(User user, int selectChar, String name) {
        Info = new InfoChar();
        Effs = new InfoEff();
        phucLoi = new InfoPhucLoi();
        GiftCode = new InfoGiftCode();
        Bag = new InfoInventory();
        Point = new InfoPoint();
        Info.pointHokage = new PointHokage();
        this.user = user;
        Info.name = name;
        taskId = 0;
        Info.idChar = (byte) selectChar;
        if (Info.idChar >= 5 && Info.idChar <= 8) {
            Info.gioiTinh = 0;
        } else {
            Info.gioiTinh = 1;
        }
        Info.idClass = 0;
        Info.idhe = getLopFormSelectChar(Info.idChar);
        Skill = new InfoSkill(Info.idClass);
        //this.addEffect(new Effect(0, 7, System.currentTimeMillis(), 86400000));
        if (Info.idClass == 1 || Info.idClass == 5) {
            Point.arrayTiemNang[0] = 10;
            Point.arrayTiemNang[1] = 0;
            Point.arrayTiemNang[2] = 5;
            Point.arrayTiemNang[3] = 5;
        } else {
            Point.arrayTiemNang[0] = 0;
            Point.arrayTiemNang[1] = 0;
            Point.arrayTiemNang[2] = 15;
            Point.arrayTiemNang[3] = 5;
        }
        //updateTiemNang();
        //setExp(DataCenter.gI().GetExpFormLevel(70));
        id = Info.idEntity = user.ID_USER;
    }

    public synchronized void selectCard(Message ms) {
        try {
            byte index = ms.readByte();

            if (selectCard != null) {
                if (index < 0 || index > 39) {
                    return;
                }

                selectCard.select(this, index);
            }
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public int GetTaiPhu() {
        int taiPhu = 0;
        for (Item item : Bag.arrItemBody) {
            if (item != null) {
                taiPhu += 15 + 50 * item.level;
                if (item.level == 4) {
                    taiPhu += 100;
                }
                if (item.level >= 8) {
                    taiPhu += 200;
                }
                if (item.level >= 12) {
                    taiPhu += 300;
                }
                if (item.level >= 14) {
                    taiPhu += 400;
                }
                if (item.level >= 16) {
                    taiPhu += 800;
                }
                if (item.isHokage()) {
                    taiPhu += taiPhu * 3 / 100;
                }
                if (item.isItemUpdate()) {
                    taiPhu += taiPhu * 6 / 100;
                }
                if (item.X()) {
                    taiPhu += taiPhu * 9 / 100;
                }
            }
        }
        for (Item itemPhu : Bag.arrItemBody2) {
            if (itemPhu != null) {
                taiPhu += 15 + 50 * itemPhu.level;
                if (itemPhu.level == 4) {
                    taiPhu += 100;
                }
                if (itemPhu.level >= 8) {
                    taiPhu += 200;
                }
                if (itemPhu.level >= 12) {
                    taiPhu += 300;
                }
                if (itemPhu.level >= 14) {
                    taiPhu += 400;
                }
                if (itemPhu.level >= 16) {
                    taiPhu += 800;
                }
                if (itemPhu.isHokage()) {
                    taiPhu += taiPhu * 3 / 100;
                }
                if (itemPhu.W()) {
                    taiPhu += taiPhu * 6 / 100;
                }
                if (itemPhu.X()) {
                    taiPhu += taiPhu * 9 / 100;
                }
            }
        }
        return taiPhu;
    }

    public void writeInfo() {
        try {
            Message m = Message.c((byte) -73);
            m.writeInt(maxHP);
            m.writeInt(Point.hp);
            m.writeInt(maxMP);
            m.writeInt(Point.mp);
            m.writeByte(Info.lvPk);

            m.writeShort(getExpBuff()); // Tăng kinh nghiệm đánh quái
            m.writeInt(GetTaiPhu());
            m.writeShort(Info.chuyenCan);// Điểm chuyên cần
            m.writeShort(Info.chuyenCanTuan); // Điểm chuyên cần tuần
            m.writeLong(Info.cuaCai); // Điểm của cải
            m.writeInt(Info.cuaCaiTuan); // Điểm của cải tuần
            m.writeInt(Info.cuongHoa); // Điểm cường hóa
            m.writeInt(Info.cuongHoaTuan); // Điểm cường hóa tuần
            m.writeShort(Info.loiDai); // Điểm lôi đài tháng
            m.writeLong(Info.luyenTap); // Điểm luyện tập
            m.writeInt(Bag.pointNAP); // Điểm nạp nhiều
            m.writeShort(Info.pointHokage.Dai); // Điểm hokage đai trán
            m.writeShort(Info.pointHokage.Ao); // Điểm hokage áo
            m.writeShort(Info.pointHokage.BaoTay);// Điểm hokage bao tay
            m.writeShort(Info.pointHokage.Quan);// Điểm hokage quần
            m.writeShort(Info.pointHokage.Giay);// Điểm hokage giày
            m.writeShort(Info.pointHokage.Vk);// Điểm hokage vũ khí
            m.writeShort(Info.pointHokage.Day);// Điểm hokage dây thừng
            m.writeShort(Info.pointHokage.Moc); // Điểm hokage móc sắt
            m.writeShort(Info.pointHokage.OngTieu); // Điểm hokage ống tiêu
            m.writeShort(Info.pointHokage.Tui); // Điểm hokage túi nhẫn giả
            m.writeByte(Bag.stnSo); // Sách tiềm năng sơ / max 3
            m.writeByte(Bag.stnTrung);// Sách tiềm năng trung / max 29
            m.writeByte(Bag.stnCao);// Sách tiềm năng cao / max 1
            m.writeByte(Bag.sknSo); // Sách kỹ năng sơ / max 3
            m.writeByte(Bag.sknTrung); // Sách kỹ năng trung / max 30
            m.writeByte(Bag.sknCao); // Sách kỹ năng cao / max 1
            m.writeByte(Bag.Banh);  // Bánh ít bảo / max 10
            m.writeByte(28);
            m.writeByte(29);
            m.writeByte(30);
            user.session.sendMessage(m);
        } catch (IOException ex) {
            Logger.getLogger(Char.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void writeInfoOrther(Char pl) {
        try {
            Message m = Message.c((byte) -73);
            m.writeInt(pl.maxHP);
            m.writeInt(pl.Point.hp);
            m.writeInt(pl.maxMP);
            m.writeInt(pl.Point.mp);
            m.writeByte(pl.Info.lvPk);

            m.writeShort(pl.getExpBuff()); // Tăng kinh nghiệm đánh quái
            m.writeInt(pl.GetTaiPhu());
            m.writeShort(pl.Info.chuyenCan);// Điểm chuyên cần
            m.writeShort(pl.Info.chuyenCanTuan); // Điểm chuyên cần tuần
            m.writeLong(pl.Info.cuaCai); // Điểm của cải
            m.writeInt(pl.Info.cuaCaiTuan); // Điểm của cải tuần
            m.writeInt(pl.Info.cuongHoa); // Điểm cường hóa
            m.writeInt(pl.Info.cuongHoaTuan); // Điểm cường hóa tuần
            m.writeShort(pl.Info.loiDai); // Điểm lôi đài tháng
            m.writeLong(pl.Info.luyenTap); // Điểm luyện tập
            m.writeInt(pl.Bag.pointNAP); // Điểm nạp nhiều
            m.writeShort(pl.Info.pointHokage.Dai); // Điểm hokage đai trán
            m.writeShort(pl.Info.pointHokage.Ao); // Điểm hokage áo
            m.writeShort(pl.Info.pointHokage.BaoTay);// Điểm hokage bao tay
            m.writeShort(pl.Info.pointHokage.Quan);// Điểm hokage quần
            m.writeShort(pl.Info.pointHokage.Giay);// Điểm hokage giày
            m.writeShort(pl.Info.pointHokage.Vk);// Điểm hokage vũ khí
            m.writeShort(pl.Info.pointHokage.Day);// Điểm hokage dây thừng
            m.writeShort(pl.Info.pointHokage.Moc); // Điểm hokage móc sắt
            m.writeShort(pl.Info.pointHokage.OngTieu); // Điểm hokage ống tiêu
            m.writeShort(pl.Info.pointHokage.Tui); // Điểm hokage túi nhẫn giả
            m.writeByte(pl.Bag.stnSo); // Sách tiềm năng sơ / max 3
            m.writeByte(pl.Bag.stnTrung);// Sách tiềm năng trung / max 29
            m.writeByte(pl.Bag.stnCao);// Sách tiềm năng cao / max 1
            m.writeByte(pl.Bag.sknSo); // Sách kỹ năng sơ / max 3
            m.writeByte(pl.Bag.sknTrung); // Sách kỹ năng trung / max 30
            m.writeByte(pl.Bag.sknCao); // Sách kỹ năng cao / max 1
            m.writeByte(pl.Bag.Banh);  // Bánh ít bảo / max 10
            m.writeByte(28);
            m.writeByte(29);
            m.writeByte(30);
            user.session.sendMessage(m);
        } catch (IOException ex) {
            Logger.getLogger(Char.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void CheckNextDay() {
        Date now = new Date();
        Date date = Utlis.getDate(Info.timeLogin);
        boolean isNewDay = !DateUtils.isSameDay(date, now);
        if (isNewDay) {
            resetNewDay();
        }
        timeOffline += TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - Info.timeLogin);
        Info.timeLogin = System.currentTimeMillis();
    }

    public void resetNewDay() {
        phucLoi.timeOnline = 0;
        if (phucLoi.theThang > System.currentTimeMillis()) {
            addVang(200);
            addVangKhoa(1500);
        } else {
            phucLoi.theThang = -1;
        }
        countFinishDay = 10;
        countLoopBoss = 3;
        Info.countTBGT = 50;
        phucLoi.soNgayOnline++;
        removeItemsWithIDPhucLoi(0);
        removeItemsWithIDPhucLoi(5);
        removeItemsWithIDPhucLoi(7);
        phucLoi.napNgay = 0;
        phucLoi.tieuNgay = 0;
        Info.countRuong = 0;
        pointDaiHoi = 0;
        pointTranhDoat = 0;
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.MONDAY) {
            removeItemsWithIDPhucLoi(6);
            removeItemsWithIDPhucLoi(8);
            removeItemsWithIDPhucLoi(1);
            phucLoi.tieuTuan = 0;
            phucLoi.napTuan = 0;
            phucLoi.soNgayOnline = 0;
        }
        Info.countCamThuat = 1;
        idCamThuat = -1;
        idKhuLuyenTap = -1;
        idDungeonEvent = -1;
        idDiaCung = -1;
        Info.countHu = 0;
        Info.countUseBinhHoatLuc = 0;
        Info.countDauCoc = 0;

        newDay = true;
    }

    public void takingTask() {
        if (taskMain == null) {
            getService().sendTaskStep(-1);
            updateTaskLevelUp();
        }
    }

    public int getTaskNpcId() {
        if (taskMain != null) {
            if (taskId == TaskName.NV_NHAN_GIA_HOC_VIEN) {
                if (taskMain.index == 8) {
                    return getThayFormId();
                }
            }
            if (taskMain.index > taskMain.vStep.size() - 1 || taskMain.index < 0) {
                return taskMain.template.getIdNpc();
            } else {
                return taskMain.vStep.get(taskMain.index).idNpc;
            }

        } else {
            if (taskId >= DataCenter.gI().TaskTemplate.length) {
                return -1;
            }
            return DataCenter.gI().TaskTemplate[taskId].getIdNpc();
        }
    }

    public int getIdItemTask(int mobId) {
        if (taskMain != null) {
            if (taskMain.index < 0 || taskMain.index > taskMain.vStep.size() - 1)
                return -1;
            int idMob = taskMain.vStep.get(taskMain.index).idMob;
            int itemId = taskMain.vStep.get(taskMain.index).idItem;
            if (mobId == idMob) {
                return itemId;
            }
        }
        return -1;
    }

    public void updateTaskLevelUp() {
//        if (taskMain != null && taskMain.index == 0 && taskMain.template.getLeveRequire() > 0
//                && this.level >= taskMain.template.getLeveRequire()) {
//            taskNext();
//        }
    }

    public void updateTaskPickItem(Item item) {
        if (taskMain != null) {
            if (taskMain.index < 0 || taskMain.index > taskMain.vStep.size() - 1)
                return;
            int itemId = taskMain.vStep.get(taskMain.index).idItem;
            if (item.id == itemId) {
                updateTaskCount(1);
            }
        }
    }

    public void updateTaskKillMonster(Mob mob) {
        if (taskMain != null) {
            if (taskMain.index < 0 || taskMain.index > taskMain.vStep.size() - 1)
                return;
            int modId = taskMain.vStep.get(taskMain.index).idMob;
            if (mob.id == modId && getIdItemTask(mob.id) == -1) {
                createMob = false;
                updateTaskCount(1);
            }
        }
    }

    public int getItemStepTask() {
        if (taskMain != null) {
            if (taskMain.index < 0 || taskMain.index > taskMain.vStep.size() - 1)
                return -1;
            return taskMain.vStep.get(taskMain.index).idItem;

        }
        return -1;
    }

    public void updateTaskCount(int count) {
        if (taskMain != null) {
            try {
                taskMain.count += count;
                getService().updateTaskCount(taskMain.count);
                if (taskMain.count >= taskMain.vStep.get(taskMain.index).require) {
                    taskNext();
                }
            } catch (Exception ex) {
                Log.error("err: " + ex.getMessage(), ex);
            }
        }
    }

    public void taskNext() {
        if (taskMain != null) {
            taskMain.index++;
            taskMain.count = 0;
            getService().sendTaskInfo();
        }
    }

    public void updateTask() {
        if (taskMain != null) {
            RewardTask();
            taskMain = null;
            taskId++;
            getService().sendTaskInfo();
        }
    }

    public synchronized void flush() {
        if (!saving) {
            try {
                saving = true;

                ObjectMapper json = new ObjectMapper();
                JSONArray bags = new JSONArray();
                for (int i = 0; i < this.Bag.arrItemBag.length; i++) {
                    try {
                        if (Bag.arrItemBag[i] != null) {
                            bags.add(Bag.arrItemBag[i].toJSONObject());

                        }
                    } catch (Exception e) {
                        Log.error("error save item bag", e);
                    }
                }
                JSONArray body = new JSONArray();
                for (int i = 0; i < this.Bag.arrItemBody.length; i++) {
                    try {
                        if (Bag.arrItemBody[i] != null) {
                            body.add(Bag.arrItemBody[i].toJSONObject());

                        }
                    } catch (Exception e) {
                        Log.error("error save item body", e);
                    }
                }
                JSONArray body2 = new JSONArray();
                for (int i = 0; i < this.Bag.arrItemBody2.length; i++) {
                    try {
                        if (Bag.arrItemBody2[i] != null) {
                            body2.add(Bag.arrItemBody2[i].toJSONObject());

                        }
                    } catch (Exception e) {
                        Log.error("error save item body2", e);
                    }
                }
                JSONArray box = new JSONArray();
                for (int i = 0; i < this.Bag.arrItemBox.length; i++) {
                    try {
                        if (Bag.arrItemBox[i] != null) {
                            box.add(Bag.arrItemBox[i].toJSONObject());

                        }
                    } catch (Exception e) {
                        Log.error("error save item body2", e);
                    }
                }
                JSONArray boxext = new JSONArray();
                for (int i = 0; i < this.Bag.arrItemExtend.length; i++) {
                    try {
                        if (Bag.arrItemExtend[i] != null) {
                            boxext.add(Bag.arrItemExtend[i].toJSONObject());

                        }
                    } catch (Exception e) {
                        Log.error("error save item extend", e);
                    }
                }
                JSONArray skillViThu = new JSONArray();
                for (int i = 0; i < this.Bag.arrItemSkillViThu.length; i++) {
                    try {
                        if (Bag.arrItemSkillViThu[i] != null) {
                            skillViThu.add(Bag.arrItemSkillViThu[i].toJSONObject());

                        }
                    } catch (Exception e) {
                        Log.error("error save item skill vi thu", e);
                    }
                }
                JSONArray info = new JSONArray();
                JSONObject obj = Info.toJSONObject();
                obj.put("idcamThuat", idCamThuat);
                obj.put("idKlT", idKhuLuyenTap);
                obj.put("idDiaCung", idDiaCung);
                obj.put("idDungeonEvent", idDungeonEvent);
                JSONObject taskSeal = new JSONObject();
                taskSeal.put("isTask", this.taskSeal);
                taskSeal.put("type", typeSeal);
                taskSeal.put("step", stepSeal);
                obj.put("taskseal", taskSeal);
                JSONObject security = new JSONObject();
                security.put("isSecurity", isSecurity);
                security.put("timeRemoveSecurity", timeRemoveSecurity);
                security.put("passwordSecurity", passwordSecurity);
                obj.put("security", security);
                obj.put("nvth", countFinishDay);
                obj.put("countLoopBoss", this.countLoopBoss);
                obj.put("isUpdateOption", this.isUpdateOption);
                obj.put("timeChangeName", this.timeChangeName);
                obj.put("timeOutClan", this.timeOutClan);
                obj.put("timeOffline", this.timeOffline);
                obj.put("inLangCo", this.inLangCo);
                obj.put("pointDaiHoi", this.pointDaiHoi);
                obj.put("pointTranhDoat", this.pointTranhDoat);
                obj.put("taskId", taskId);
                if (treasure != null) {
                    JSONObject _treasure = new JSONObject();
                    _treasure.put("id", treasure.getId());
                    _treasure.put("quantity", treasure.getQuantity());
                    _treasure.put("index", treasure.getIndex());
                    obj.put("treasure", _treasure);
                }
                if (shoprank.size() > 0) {
                    Gson gson = new Gson();
                    JsonArray jsonArray = gson.toJsonTree(shoprank).getAsJsonArray();
                    obj.put("shoprank", jsonArray);
                }
                obj.put("theGiuTien", theGiuTien);
                obj.put("bagAdd", bagAdd);
                info.add(obj);
                JSONArray pl = new JSONArray();
                pl.add(phucLoi.toJSONObject());
                if (phucLoi.listPl.size() > 0) {
                    for (int i = 0; i < phucLoi.listPl.size(); i++) {
                        pl.add(phucLoi.listPl.get(i).toJSONObject());
                    }
                }
                JSONArray inventory = new JSONArray();
                Bag.taiPhu = GetTaiPhu();
                inventory.add(Bag.toJSONObject());
                JSONArray point = new JSONArray();
                point.add(Point.toJSONObject());
                JSONArray giftcode = new JSONArray();
                if (GiftCode != null) {
                    if (GiftCode.GiftDaNhan != null)
                        for (int i = 0; i < GiftCode.GiftDaNhan.size(); i++) {
                            giftcode.add(GiftCode.GiftDaNhan.get(i));
                        }
                }
                JSONArray thu = new JSONArray();
                for (int i = 0; i < this.letters.size(); i++) {
                    thu.add(this.letters.get(i).toJSONObject());
                }
                JSONArray effect = new JSONArray();
                for (int i = 0; i < this.listEffect.size(); i++) {
                    effect.add(this.listEffect.get(i).toJSONObject());
                }
                JSONArray hokage = new JSONArray();
                hokage.add(Info.pointHokage.toJSONObject());
                JSONArray danhhieu = new JSONArray();
                for (int i = 0; i < Info.danhHieus.size(); i++) {
                    danhhieu.add(Info.danhHieus.get(i).toJSONObject());
                }
                JSONArray skillvithu = new JSONArray();
                for (int i = 0; i < listSkill.size(); i++) {
                    SkillClan skill = listSkill.get(i);
                    if (skill != null) {
                        skillvithu.add(skill.toJSONObject());
                    }
                }
                JSONArray friends = new JSONArray();
                if (this.friends != null) {
                    Friend[] fr = getFriends();
                    for (Friend friend : fr) {
                        friends.add(friend.toJSONObject());
                    }
                }
                JSONArray enemies = new JSONArray();
                if (this.enemies != null) {
                    Friend[] es = getEnemies();
                    for (Friend enemy : es) {
                        enemies.add(enemy.toJSONObject());
                    }
                }
                JSONArray taskOrders = new JSONArray();
                for (TaskOrder task : this.taskOrders) {
                    JSONObject taska = new JSONObject();
                    taska.put("taskId", task.taskId);
                    taska.put("count", task.count);
                    taska.put("maxCount", task.maxCount);
                    taska.put("killId", task.killId);
                    taska.put("mapId", task.mapId);
                    taskOrders.add(taska);
                }
                String task = "";
                if (this.taskMain != null) {
                    JSONObject t = new JSONObject();
                    t.put("id", this.taskMain.taskId);
                    t.put("index", this.taskMain.index);
                    t.put("count", this.taskMain.count);
                    task = t.toJSONString();
                }
                String skill = json.writeValueAsString(Skill);
                String jinfo = info.toJSONString();
                String jbag = bags.toJSONString();
                String jBody = body.toJSONString();
                String jbody2 = body2.toJSONString();
                String jbox = box.toJSONString();
                String jbagext = boxext.toJSONString();
                String jSkillvithu = skillViThu.toJSONString();
                String jphucloi = pl.toJSONString();
                String jiventory = inventory.toJSONString();
                String jpoin = point.toJSONString();
                String gift = giftcode.toJSONString();
                String jthu = thu.toJSONString();
                String jeff = effect.toJSONString();
                String poinhokage = hokage.toJSONString();
                String jdanhhieu = danhhieu.toJSONString();
                String jskill = skillvithu.toJSONString();
                String jFriends = friends.toJSONString();
                String jEnemies = enemies.toJSONString();
                String jTaskOder = taskOrders.toJSONString();
//                try {
//                    boolean flag = false;
//                    Long last = ServerManager.HASH_MAP.get(this.id);
//                    long now = System.currentTimeMillis();
//                    if (last != null) {
//                        if (now - last < 300000) {
//                            flag = true;
//                        }
//                    }
//                    if (!flag) {
//                        MongoCollection collection = MongoDbConnection.getCollection("player");
//                        Document document = new Document();
//                        document.put("player_id", this.id);
//                        document.put("jinfo", jinfo);
//                        document.put("jbag", jbag);
//                        document.put("jBody", jBody);
//                        document.put("jBody", jBody);
//                        document.put("jbody2", jbody2);
//                        document.put("jbox", jbox);
//                        document.put("jbagext", jbagext);
//                        document.put("jSkillvithu", jSkillvithu);
//                        document.put("jphucloi", jphucloi);
//                        document.put("jiventory", jiventory);
//                        document.put("jpoin", jpoin);
//                        document.put("gift", gift);
//                        document.put("jthu", jthu);
//                        document.put("poinhokage", poinhokage);
//                        document.put("jdanhhieu", jdanhhieu);
//                        document.put("jct", jct);
//                        document.put("jskill", jskill);
//                        document.put("update_at", now);
//                        collection.insertOne(document);
//                        ServerManager.HASH_MAP.put(this.id, now);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                try (Connection conn = Connect.getConnection();) {
                    try (PreparedStatement ps = conn.prepareStatement("UPDATE player set info = ? ,inventory = ?,bag = ?,body = ?,body2 = ?,box = ?,bagext = ?,skillvithu = ?, skill = ? , point = ? ,thu = ?,code = ?,effect =?,phucLoi =?,hokage =?,danhhieu = ?,listskill = ?,level =?,topnap =?,toptaiphu =?,friends=?,enemies=?,task=?,taskMain=?,bac=?,vang=? where Name = ?");) {
                        ps.setString(1, jinfo);
                        ps.setString(2, jiventory);
                        ps.setString(3, jbag);
                        ps.setString(4, jBody);
                        ps.setString(5, jbody2);
                        ps.setString(6, jbox);
                        ps.setString(7, jbagext);
                        ps.setString(8, jSkillvithu);
                        ps.setString(9, skill);
                        ps.setString(10, jpoin);
                        ps.setString(11, jthu);
                        ps.setString(12, gift);
                        ps.setString(13, jeff);
                        ps.setString(14, jphucloi);
                        ps.setString(15, poinhokage);
                        ps.setString(16, jdanhhieu);
                        ps.setString(17, jskill);
                        ps.setString(18, String.valueOf(level()));
                        ps.setString(19, String.valueOf(Bag.pointNapNew));
                        ps.setString(20, String.valueOf(GetTaiPhu()));
                        ps.setString(21, jFriends);
                        ps.setString(22, jEnemies);
                        ps.setString(23, jTaskOder);
                        ps.setString(24, task);
                        ps.setInt(25, Bag.bac);
                        ps.setInt(26, Bag.vang);
                        ps.setString(27, this.Info.name);
                        ps.executeUpdate();
                    } catch (Exception e) {
                        Log.error("Loi update data cua player: " + this.Info.name, e);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                updateEventPoint();
            } catch (Exception e) {
                Log.error("saveData charName: " + this.Info.name + " ex: " + e.getMessage(), e);
            } finally {
                saving = false;
            }
        }
    }

    public int getPointDaiHoi() {
        return pointDaiHoi;
    }

    public void RewardTask() {
//        if (getCountNullItemBag() < 5) {
//            service.alertMessage("Hành trang không đủ chỗ trống");
//            return;
//        }
        TaskTemplate taskTemplate = taskMain.template;
        addExp(1000);
        addBacKhoa(taskTemplate.amountBacKhoa);
        addBac(taskTemplate.amountBac);
        addVangKhoa(taskTemplate.amountVangKhoa);
        if (taskTemplate.strItem != null && taskTemplate.strItem.length() > 0) {
            String[] var1 = taskTemplate.strItem.split("@");
            Item var2;
            (var2 = new Item()).id = Short.parseShort(var1[0]);
            var2.isLock = Boolean.parseBoolean(var1[1]);
            var2.expiry = Long.parseLong(var1[2]);
            var2.setAmount(Integer.parseInt(var1[3]));
            var2.he = this.Info.idhe;
            var2.level = Byte.parseByte(var1[5]);
            if (var1.length > 6) {
                var2.strOptions = var1[6];
                if (var2.getItemTemplate().gioiTinh != 2 && var2.getItemTemplate().gioiTinh != this.Info.gioiTinh) {
                    for (int var3 = 0; var3 < DataCenter.gI().ItemTemplate.length; ++var3) {
                        ItemTemplate var5;
                        if ((var5 = DataCenter.gI().ItemTemplate[var3]).gioiTinh == this.Info.gioiTinh && var5.type == var2.getItemTemplate().type && var5.levelNeed == var2.getItemTemplate().levelNeed) {
                            var2.id = var5.id;
                            break;
                        }
                    }
                }
                var2.getItemOption();
                if (var2.id == 134) {
                    switch (Info.idhe) {
                        case 1:
                            var2.strOptions = "57,0,500;62,0,500";
                            break;
                        case 2:
                            var2.strOptions = "54,0,500;58,0,500";
                            break;
                        case 3:
                            var2.strOptions = "55,0,500;59,0,500";
                            break;
                        case 4:
                            var2.strOptions = "56,0,500;60,0,500";
                            break;
                        case 5:
                            var2.strOptions = "53,0,500;61,0,500";
                            break;
                    }
                }
            }
            if (var2.id != 134)
                var2.createItemOptions();
            addItem(var2);
            this.user.session.sendMessage(HanderMessage.SendThongBao("Nhận " + var2.amount + " " + var2.getItemTemplate().name, HanderMessage.WHITE));
        }
    }

    public Item FindItemBag(int Id) {
        for (Item item : this.Bag.arrItemBag) {
            if (item != null && item.id == Id) {
                return item;
            }
        }
        return null;
    }

    public boolean removeItems(int id, int quantityNeeded) {
        Item lockedItem = null;
        Item unlockedItem = null;

        // Tìm item khoá và không khoá có ID tương ứng
        for (Item item : this.Bag.arrItemBag) {
            if (item != null && item.id == id) {
                if (item.isLock) {
                    lockedItem = item;
                } else {
                    unlockedItem = item;
                }
            }
        }

        // Sử dụng item khoá trước
        if (lockedItem != null && lockedItem.amount >= quantityNeeded) {
            lockedItem.amount -= quantityNeeded;
            if (lockedItem.amount == 0) {
                removeItem(lockedItem, true);
                msgRemoveItemBag(lockedItem);
            }
        } else if (lockedItem != null && unlockedItem != null) {
            if (lockedItem.amount + unlockedItem.amount < quantityNeeded) {
                return false;
            }
            int remaining = quantityNeeded - lockedItem.amount;
            removeItem(lockedItem, true); // Sử dụng hết item khoá
            msgRemoveItemBag(lockedItem);

            // Sử dụng item không khoá nếu cần
            if (unlockedItem != null && unlockedItem.amount > remaining) {
                unlockedItem.amount -= remaining;
                if (unlockedItem.amount == 0) {
                    removeItem(unlockedItem, true);
                    msgRemoveItemBag(unlockedItem);
                } else {
                    msgUseItemBag(unlockedItem);
                }
            } else if (unlockedItem != null) {
                removeItem(unlockedItem, true); // Sử dụng hết item không khoá
                msgRemoveItemBag(unlockedItem);
            }
        } else if (unlockedItem != null && unlockedItem.amount >= quantityNeeded) {
            // Chỉ sử dụng item không khoá nếu không có item khoá
            unlockedItem.amount -= quantityNeeded;
            if (unlockedItem.amount == 0) {
                removeItem(unlockedItem, true);
                msgRemoveItemBag(unlockedItem);
            }
        } else {
            return false;
        }
        return true;
    }


//    public Item FindItemBagLock(int Id) {
//        for (Item item : this.Bag.arrItemBag) {
//            if (item != null && item.id == Id&&item.isLock) {
//                return item;
//            }
//        }
//        return null;
//    }

    public Item FindItemBagUnlock(int Id) {
        for (Item item : this.Bag.arrItemBag) {
            if (item != null && item.id == Id && !item.isLock) {
                return item;
            }
        }
        return null;
    }

    public byte getThayFormId() {
        switch (this.getLopFormSelectChar(this.Info.idChar)) {
            case 1:
                return 4;
            case 2:
                return 7;
            case 3:
                return 6;
            case 4:
                return 8;
            case 5:
                return 5;
        }
        return -1;
    }

    public byte getLopFormSelectChar(byte i) {
        switch (i) {
            case 0:
            case 5:
                return 1;
            case 1:
            case 6:
                return 2;
            case 2:
            case 7:
                return 3;
            case 3:
            case 8:
                return 4;
            case 4:
                return 5;

        }
        return 0;
    }

    //
    public void writeMe(Writer writer) throws IOException {
        writer.writeUTF(Info.username);
        writer.writeInt(Info.idEntity);
        writer.writeUTF(Info.name);
        writer.writeByte(Info.gioiTinh);
        writer.writeByte(Info.idChar);
        writer.writeByte(Info.idhe);
        writer.writeByte(Info.idClass);
        writer.writeByte(0);
        writer.writeByte(Info.lvPk);
        writer.writeInt(GetTaiPhu());
        writer.writeShort(movementSpeed);
        writer.writeByte(Info.sachChienDau);
        writer.writeInt(Point.hp);
        writer.writeInt(maxHP);
        writer.writeInt(Point.mp);
        writer.writeInt(maxMP);
        writer.writeLong(Point.exp);
        writer.writeInt(Bag.bac);
        writer.writeInt(Bag.bacKhoa);
        writer.writeInt(Bag.vang);
        writer.writeInt(Bag.vangKhoa);

        writer.writeShort(taskId);
        if (taskMain != null) {
            writer.writeByte(taskMain.index);
            writer.writeShort(taskMain.count);
        } else {
            writer.writeByte(-1);
            writer.writeShort(0);
        }
        writer.writeInt(Point.hoatLuc);
        writer.writeInt(Bag.pointNAP);

        writer.writeShort(Bag.arrItemBag.length);
        ArrayList<Item> listItem = new ArrayList<Item>();
        Utlis.getArrayListNotNull(Bag.arrItemExtend, listItem);
        writer.writeByte(listItem.size());
        for (int i = 0; i < listItem.size(); i++) {
            Item item = listItem.get(i);
            writer.writeShort(item.id);
            writer.writeBoolean(item.isLock);
            writer.writeByte(item.index);
        }
        writeItemBody(writer, Bag.arrItemBody);
        writeItemBody(writer, Bag.arrItemBody2);
        writeItemBag(writer, Bag.arrItemBag);

        writeEffect(writer);

        writeThu(writer);
        writeFriend(writer);
        writeEnemy(writer);

        writeSkill(writer);

        writeDanhHieu(writer);

        writer.writeByte(Info.rank);

        writer.writeByte(Info.selectCaiTrang);
        writer.writeInt(0);//timeChatColor
        writer.writeByte(0);//numTaskDoneKTNG
        writer.writeBoolean(false);//doneTaskKTNG
        writerSkillViThu(writer);

    }




    private void writerSkillViThu(Writer writer) {
        try {
            writer.writeByte(listSkill.size());
            for (int i = 0; i < listSkill.size(); i++) {
                SkillClan skill = listSkill.get(i);
                writer.writeByte(skill.id);
                writer.writeByte(skill.levelNeed);
            }
        } catch (IOException e) {
        }
    }

    public void write(Writer writer) throws IOException {
        writer.writeByte(Info.status);
        writer.writeUTF(Info.name);
        writer.writeByte(Info.idChar);
        writer.writeByte(Info.gioiTinh);
        writer.writeByte(Info.idClass);
        writer.writeByte(InfoGame.TypePk);
        writer.writeByte(Info.lvPk);
        writer.writeShort(movementSpeed);

        writer.writeInt(Point.hp);
        writer.writeInt(maxHP);
        writer.writeInt(Point.mp);
        writer.writeInt(maxMP);

        writer.writeLong(Point.exp);

        writer.writeShort(Info.cx);
        writer.writeShort(Info.cy);

        writer.writeByte(InfoGame.statusGD);

        writeItemBody(writer, Bag.arrItemBody);

        writeEffect(writer);

        writeDanhHieu(writer);

        writer.writeByte(Info.rank);

        writer.writeByte(Info.selectCaiTrang);

    }


    public void writeItemBody(Writer writer, Item[] arrItem) throws IOException {
        ArrayList<Item> listItem = new ArrayList<Item>();
        Utlis.getArrayListNotNull(arrItem, listItem);
        writer.writeByte(listItem.size());
        for (int i = 0; i < listItem.size(); i++) {
            Item item = listItem.get(i);
            writer.writeShort(item.id);
            writer.writeBoolean(item.isLock);
            writer.writeLong(item.expiry);
            writer.writeByte(item.he);
            writer.writeByte(item.level);
            writer.writeUTF(item.strOptions);
        }
    }


    public void writeEffect(Writer writer) throws IOException {
        writer.writeByte(listEffect.size());
        for (int i = 0; i < listEffect.size(); i++) {
            Effect eff = listEffect.get(i);
            /*
             var1.readShort(), var1.readInt(), var1.readLong(), var1.readInt()
             */
            eff.write(writer);
        }
    }

    public DanhHieuNew getDanhHieuNewById(int id) {
        try {
            for (DanhHieuNew danhHieuNew : Manager.gI().danhHieuNews) {
                if (danhHieuNew.idIitem == id) {
                    return danhHieuNew;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(Char.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void writeDanhHieu(Writer writer) throws IOException {
        if (Info.danhHieus.size() > 127) {
            Info.danhHieus.subList(127, Info.danhHieus.size()).clear();
        }
        writer.writeByte(Info.danhHieus.size());
        if (Info.danhHieus.size() > 0) {
            for (int i = 0; i < Info.danhHieus.size(); i++) {
                DanhHieu danhHieu = Info.danhHieus.get(i);
                if (danhHieu != null) {
                    writer.writeUTF(danhHieu.TextDanhHieu);
                    writer.writeInt(getTime(danhHieu));// time
                    writer.writeBoolean(danhHieu.isNew);
                    if (danhHieu.isNew) {
                        DanhHieuNew danhHieuNew = getDanhHieuNewById(danhHieu.idItem);
                        if (danhHieuNew != null) {
                            writer.writeInt(danhHieuNew.x);
                            writer.writeInt(danhHieuNew.y);
                            writer.writeInt(danhHieuNew.timeMs);
                            writer.writeInt(danhHieuNew.size);
                            writer.writeByte(danhHieuNew.data.length);
                            for (int j = 0; j < danhHieuNew.data.length; j++) {
                                writer.writeShort(danhHieuNew.data[j]);
                            }
                        } else {
                            writer.writeInt(-1);
                            writer.writeInt(-1);
                            writer.writeInt(-1);
                            writer.writeInt(-1);
                            writer.writeByte(0);
                        }
                    }
                } else {
                    writer.writeUTF("");
                    writer.writeInt(-1);// time
                    writer.writeBoolean(false);
                }
            }
        }
//        writer.writeUTF("ABC");
//        writer.writeInt(-1);// time
//        writer.writeInt(1);
        writer.writeByte(Info.selectDanhHieu);


    }

    private static int getTime(DanhHieu dh) {
        int time;
        long timeStart = dh.timeStart;
        long timeend = dh.timeEnd; //
        long endTimeMillis = timeStart + timeend;
        time = (int) (endTimeMillis / 1000);
        return time;
    }

    public void doiDanhHieu() {
        try {
            Message m = Message.c((byte) -75);
            m.writeInt(Info.idEntity);
            writeDanhHieu(m.writer);
//            m.writeByte(index);
            user.session.sendMessage(m);
        } catch (IOException ex) {

        }

    }

    public void writeItemBag(Writer writer, Item[] arrItem) throws IOException {
        List<Item> itemList = Arrays.stream(arrItem)
                .filter(item -> item != null) // Lọc, chỉ giữ các phần tử không null
                .collect(Collectors.toList());
        writer.writeShort(itemList.size());
        for (Item item : itemList) {
            if (item != null) {
                item.write(writer);
            }
        }
    }


    public void writeThu(Writer writer) throws IOException {
        writer.writeShort(letters.size());
        for (TemplateThu thu : letters) {
            writer.writeShort(thu.id);
            writer.writeBoolean(thu.isSucess);
            writer.writeUTF(thu.NameNguoiGui);
            writer.writeUTF(thu.Title);
            writer.writeUTF(thu.NoiDungThu);
            writer.writeInt(thu.Bac);
            writer.writeInt(thu.BacKhoa);
            writer.writeInt(thu.Vang);
            writer.writeInt(thu.VangKhoa);
            writer.writeLong(thu.Exp);
            writer.writeInt((int) (thu.TimeEnd / 1000 + 2000000));
            if (thu.Item == null) {
                writer.writeShort(-1);
            } else {
                thu.Item.write(writer);
            }
        }
    }

    public void writeFriend(Writer writer) throws IOException {
        Friend[] friends = getFriends();
        writer.writeShort(friends.length);
        for (int i = 0; i < friends.length; i++) {
            Friend friend = friends[i];
            writer.writeUTF(friend.name);
            writer.writeByte(friend.type);
            writer.writeBoolean(friend.isFriend);
        }


    }

    public void writeEnemy(Writer writer) throws IOException {
        writer.writeShort(0);

    }

    public void writeSkill(Writer writer) throws IOException {
        writer.writeShort(Skill.skillFight.id);
        writer.writeShort(Skill.arraySkill.length);
        for (Skill arraySkill : Skill.arraySkill) {

            writer.writeShort(arraySkill.id);
        }
    }

    public Skill getSkillWithIdTemplate(int id) {
        for (int i = 0; i < Skill.arraySkill.length; i++) {
            if (Skill.arraySkill[i].idTemplate == id) {
                return Skill.arraySkill[i];
            }
        }
        return null;
    }

    public void clean() {
//        if (InfoGame.ZoneGame != null) {
//            if(InfoGame.ToDoi !=null){
//                HanderToDoi.LeaveToDoi(this);
//            }
//            InfoGame.ZoneGame.removeChar(this);
//            InfoGame.ZoneGame = null;
//        }
        isClean = true;
        try {
            Arena arena = (Arena) findWorld(World.ARENA);
            if (arena != null) {
                arena.out(this);
            }
        } catch (Exception e) {

        }
        try {
            if (idCharPk != -1) {
                service.resuiltTyVo(id, (byte) 1);
                Char pl = ServerManager.findCharById(idCharPk);
                InfoGame.TypePk = 0;
                idCharPk = -1;
                if (pl != null && pl.user != null && !pl.isClean) {
                    pl.service.resuiltTyVo(id, (byte) 1);
                    pl.InfoGame.TypePk = 0;
                    pl.idCharPk = -1;
                }
            }
        } catch (Exception e) {

        }
        try {
            if (future != null && !future.isDone() && !future.isCancelled()) {
                future.cancel(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (worlds != null) {
            synchronized (worlds) {
                worlds.forEach((t) -> {
                    if (!t.isClosed()) {
                        t.removeMember(this);
                    }
                });
            }
        }
        if (this.clan != null) {
            Member mem = this.clan.getMemberByName(Info.name);
            if (mem != null) {
                mem.setOnline(false);
                mem.setChar(null);
            }
        }
        if (this.group != null) {
            outParty();
        }
        if (trade != null) {
            trade.closeUITrade();
        }
        if (this.worlds != null) {
            this.worlds.clear();
            this.worlds = null;
        }
        if (zone != null) {
            zone.removeChar(this);
            zone = null;
        }
        ServerManager.removeChar(this);
    }

    public void cleanAll() {

    }


    public void update() {
        try {
            if (user != null) {
                long time = System.currentTimeMillis();
                if (getTimeOnline < time) {
                    getTimeOnline = time + 60000;
                    phucLoi.timeOnline += 60064;
                }
                if (invite != null) {
                    invite.update();
                }
                for (int i = listEffect.size() - 1; i >= 0; i--) {
                    listEffect.get(i).update(this);
                }
            }
        } catch (Exception ex) {
            Log.error("Player update loi name:" + Info.name, ex);
        }
    }

    public void addHp(float hp) {
        if (!InfoGame.isDie) {
            this.Point.hp += hp;
            if (this.Point.hp >= maxHP) {
                this.Point.hp = maxHP;
            }
            if (Point.hp < 0) {
                if (inLangCo)
                    Map.maps[Info.mapReSpawm].addChar(this);
                InfoGame.isDie = true;
                Point.hp = 0;
            }
            msgUpdateHp();
        }
    }

    public void addMp(float Mp) {
        if (!InfoGame.isDie) {
            this.Point.mp += Mp;
            if (this.Point.mp >= maxMP) {
                this.Point.mp = maxMP;
            }
            if (Point.mp < 0) {
                Point.mp = 0;
            }
            msgUpdateMp();
        }
    }

    public boolean addItem(Item item) {

        if (item == null) {
            return false;
        }
        if (item.getItemTemplate().isXepChong) {
            for (int i = 0; i < Bag.arrItemBag.length; i++) {
                if (Bag.arrItemBag[i] != null && Bag.arrItemBag[i].id == item.id && item.expiry == Bag.arrItemBag[i].expiry && item.isLock == Bag.arrItemBag[i].isLock) {
                    item.setAmount(Bag.arrItemBag[i].getAmount() + item.getAmount());
                    item.index = (short) i;
                    Bag.arrItemBag[i] = item;
                    return true;
                }
            }
            for (int i = 0; i < Bag.arrItemBag.length; i++) {
                if (Bag.arrItemBag[i] == null) {
                    item.index = (short) i;
                    Bag.arrItemBag[i] = item;
                    return true;
                }
            }
        } else {
            for (int i = 0; i < Bag.arrItemBag.length; i++) {
                if (Bag.arrItemBag[i] == null) {
                    item.index = (short) i;
                    Bag.arrItemBag[i] = item;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkAddItem(Item item) {
        if (item.getItemTemplate().isXepChong) {
            for (int i = 0; i < Bag.arrItemBag.length; i++) {
                if (Bag.arrItemBag[i] != null && Bag.arrItemBag[i].id == item.id && item.expiry == Bag.arrItemBag[i].expiry && item.isLock == Bag.arrItemBag[i].isLock) {
                    return true;
                }
            }
            for (int i = 0; i < Bag.arrItemBag.length; i++) {
                if (Bag.arrItemBag[i] == null) {
                    return true;
                }
            }
        } else {
            for (int i = 0; i < Bag.arrItemBag.length; i++) {
                if (Bag.arrItemBag[i] == null) {
                    return true;
                }
            }
        }
        return false;
    }

    public int level() {
        long var1 = Point.exp;

        int var3;
        for (var3 = 0; var3 < DataCenter.gI().exps.length && var1 >= DataCenter.gI().exps[var3]; ++var3) {
            var1 -= DataCenter.gI().exps[var3];
        }

        return var3;
    }

    public int GetIndexBagNull() {
        for (int i = 0; i < Bag.arrItemBag.length; i++) {
            if (Bag.arrItemBag[i] == null) {
                return i;
            }
        }
        return -1;
    }

    public void useItem(short index) {
        if (index < 0 || index >= Bag.arrItemBag.length || Bag.arrItemBag[index] == null) {
            return;
        }
        if (isSecurity && !isUnlockSecurity) {
            service.warningMessage("Vui lòng mở khoá bảo mật trước khi sử dụng tính năng này");
            return;
        }
        Item item = Bag.arrItemBag[index];
        if (item.amount < 0) {
            return;
        }
        int idItemTask = getItemStepTask();
        Log.debug(item.id);
        ItemTemplate itemTemplate = item.getItemTemplate();
        if (itemTemplate.levelNeed > this.level() || itemTemplate.gioiTinh != 2 && itemTemplate.gioiTinh != Info.gioiTinh || itemTemplate.idClass != 0 && itemTemplate.idClass != Info.idClass) {
            return;
        }
        if (idItemTask != -1) {
            if (taskId == TaskName.NV_CANH_BAO_DAN_LANG && idItemTask == item.id) {
                if (isCauCa) {
                    return;
                }
                isCauCa = true;
                long timeStart = System.currentTimeMillis();
                service.sendMessage(HanderMessage.takingItem(4500, Info.idEntity, taskMain.vStep.get(taskMain.index).STR, idItemTask));
                while (isCauCa) {
                    if (timeStart + 5000 < System.currentTimeMillis()) {
                        isCauCa = false;
                        removeItem(item);
                        user.session.sendMessage(HanderMessage.TestMess7(Info.idEntity));
                        taskNext();
                        return;
                    }
                }
                return;
            }
            if (taskId == TaskName.NV_BAT_KE_NGHE_LEN && idItemTask == item.id) {
                if (isCauCa) {
                    return;
                }
                isCauCa = true;
                long timeStart = System.currentTimeMillis();
                service.sendMessage(HanderMessage.takingItem(4500, Info.idEntity, "Đang tìm bảo vật", idItemTask));
                while (isCauCa) {
                    if (timeStart + 4700 < System.currentTimeMillis()) {
                        isCauCa = false;
                        removeItem(item);
                        user.session.sendMessage(HanderMessage.TestMess7(Info.idEntity));
                        if (!taskMain.vStep.get(taskMain.index).STR_ITEM.isEmpty()) {
                            Item item1 = new Item(Integer.parseInt(taskMain.vStep.get(taskMain.index).STR_ITEM));
                            item1.isLock = true;
                            addItem(item1);
                            msgAddItemBag(item1);
                        }
                        taskNext();
                        return;
                    }
                }
                return;
            }
            if (taskId == TaskName.NV_XOA_BO_CAM_THUAT && idItemTask == item.id) {
                if (isCauCa) {
                    return;
                }
                isCauCa = true;
                long timeStart = System.currentTimeMillis();
                service.sendMessage(HanderMessage.takingItem(4500, Info.idEntity, taskMain.vStep.get(taskMain.index).STR, idItemTask));
                while (isCauCa) {
                    if (timeStart + 5000 < System.currentTimeMillis()) {
                        isCauCa = false;
                        removeItem(item);
                        user.session.sendMessage(HanderMessage.TestMess7(Info.idEntity));
                        taskNext();
                        return;
                    }
                }
                return;
            }
            if ((taskId == TaskName.NV_TRUY_TIM_BI_KIP || taskId == TaskName.NV_BAO_VAT_LANG_LA) && idItemTask == item.id) {
                if (taskId == TaskName.NV_TRUY_TIM_BI_KIP && Info.cx != 1196 && Info.cy != 287) {
                    return;
                }
                if (taskId == TaskName.NV_BAO_VAT_LANG_LA && Info.cx != 530 && Info.cy != 142) {
                    return;
                }
                if (isCauCa) {
                    return;
                }
                isCauCa = true;
                long timeStart = System.currentTimeMillis();
                service.sendMessage(HanderMessage.takingItem(4500, Info.idEntity, "Đang tìm bảo vật", idItemTask));
                while (isCauCa) {
                    if (timeStart + 4700 < System.currentTimeMillis()) {
                        isCauCa = false;
                        removeItem(item);
                        user.session.sendMessage(HanderMessage.TestMess7(Info.idEntity));
                        if (!taskMain.vStep.get(taskMain.index).STR_ITEM.isEmpty()) {
                            Item item1 = new Item(Integer.parseInt(taskMain.vStep.get(taskMain.index).STR_ITEM));
                            item1.isLock = true;
                            addItem(item1);
                            msgAddItemBag(item1);
                        }
                        taskNext();
                        return;
                    }
                }
                return;
            }
            if ((taskId == TaskName.NV_CHUA_LANH_VET_THUONG || taskId == TaskName.NV_HOAN_TRA_BAO_VAT) && idItemTask == item.id) {
                if (isCauCa) {
                    return;
                }
                isCauCa = true;
                long timeStart = System.currentTimeMillis();
                service.sendMessage(HanderMessage.takingItem(4500, Info.idEntity, taskMain.vStep.get(taskMain.index).STR, idItemTask));
                while (isCauCa) {
                    if (timeStart + 5000 < System.currentTimeMillis()) {
                        isCauCa = false;
                        removeItem(item);
                        user.session.sendMessage(HanderMessage.TestMess7(Info.idEntity));
                        if (!taskMain.vStep.get(taskMain.index).STR_ITEM.isEmpty()) {
                            Item item1 = new Item(Integer.parseInt(taskMain.vStep.get(taskMain.index).STR_ITEM));
                            item1.isLock = true;
                            addItem(item1);
                            msgAddItemBag(item1);
                        }
                        if (taskId == TaskName.NV_HOAN_TRA_BAO_VAT) {
                            updateTaskCount(1);
                        } else
                            taskNext();
                        return;
                    }
                }
                return;
            }
            if (item.id == idItemTask)
                updateTaskCount(1);
        }


        if (item.id == 421) {

            Item dh = new Item(443);
            Item itm = new Item(7);
            for (int i = 0; i < 1; i++) {
                addItem(itm);
            }
            Info.BuffEXP = 20;
            Info.rank = 5;
            addItem(dh);
            service.sendChar();
            removeItem(item);
            msgUseItemBag(item);
            user.session.sendMessage(HanderMessage.resetScreen());
        }


        if (item.id == 594) {


//            if (Info.idTask > 7 && Info.idTask < 9) {
//                service.alertMessage("Làm nhiệm vụ nhập học đi đã ông");
//                return;
//            }
//
//            if (Info.idStep - readSTRformTask().length() > 8) {
//                service.alertMessage("Nhận thưởng rồi skip tiếp!");
//                return;
//            }
//            Info.idStep++;
//            removeItem(item);
//            msgUseItemBag(item);
        }

        if (item.id == 168 || item.id == 167) {
            if (item.id == 167) {
                removeItem(item);
                msgUseItemBag(item);
            }
            String[] size = {"Trường Konoha Gakuen", "Làng,Làng Sương Mù", "Khu rừng chết", "Đại chiến nhẫn giả lần 3", "Đại hội nhẫn giả"};
            StringBuilder str2 = new StringBuilder();
            for (int i = 0; i < size.length; i++) {
                if (i > 0) {
                    str2.append(";");
                }
                str2.append(size[i]);
            }
            Message m = new Message((byte) -8);
            try {
                m.writer.writeUTF("Di chuyển nhanh qua các làng");
                m.writer.writeUTF(str2.toString());
                user.session.sendMessage(m);
            } catch (IOException ex) {
                Logger.getLogger(Char.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (item.id == 153) {
            if (Bag.stnSo >= 3) {
                return;
            }
            Bag.stnSo++;
            Point.diemTiemNang += 10;
            msgUpdateDataChar();
            removeItem(item);
            msgUseItemBag(item);
        } else if (item.id == 154) {
            if (Bag.stnTrung >= 2) {
                return;
            }
            Bag.stnTrung++;
            Point.diemTiemNang += 20;
            msgUpdateDataChar();
            removeItem(item);
            msgUseItemBag(item);
        } else if (item.id == 155) {
            if (Bag.stnCao >= 1) {
                return;
            }
            Bag.stnCao++;
            Point.diemTiemNang += 30;
            msgUpdateDataChar();
            removeItem(item);
            msgUseItemBag(item);
        }

        //AddItem: 109
        //UseItem: 116
        //UpdateItem: -4
        //Remove: 110
        if (item.isTypeTrangBi()) {
            if (taskId == 0 && taskMain != null && taskMain.index == 0) {
                taskNext();
            }
            if (taskId == TaskName.NV_NHAN_GIA_HOC_VIEN && taskMain != null && taskMain.index == 9) {
                taskNext();
            }
            item.isLock = true;
            removeItem(item);
            msgUseItemBag(item);
            Item body = Bag.arrItemBody[item.getItemTemplate().type];
            if (body != null) {
//                Bag.arrItemBag[item.index] = Bag.arrItemBody[item.getItemTemplate().type];
                addItem(body);
                msgGetInfo();
                msgSendArrItemBag();
            }
            item.index = item.getItemTemplate().type;
            Bag.arrItemBody[item.index] = item;
            msgUpdateItemBody_Orther();
            msgGetInfo();
            msgUpdateStatusChar();
            updateTiemNang();
        } else if (item.getItemTemplate().type == 34) {
            if (Info.danhHieus.size() > 126) {
                service.alertMessage("Qua nhieu danh hieu roi");
                return;
            }
            removeItem(item);
            msgUseItemBag(item);
            DanhHieu danhHieu = new DanhHieu();
            danhHieu.TextDanhHieu = item.getItemTemplate().name;
            danhHieu.GetDanhHieu();
            danhHieu.timeStart = System.currentTimeMillis();
            danhHieu.timeEnd = danhHieu.timeStart + item.expiry;
            if (item.getItemTemplate().detail.contains("isnew")) {
                danhHieu.isNew = true;
                DanhHieuNew dhNew = getDanhHieuNewById(item.id);
                if (dhNew == null) {
                    danhHieu.isNew = false;
                } else {
                    danhHieu.idItem = item.id;
                }
            }
            Info.danhHieus.add(danhHieu);
            doiDanhHieu();
        } else if (item.getItemTemplate().type == 24) {
            removeItem(item);
            msgUseItemBag(item);
            for (int i = 0; i < DataCenter.gI().EffectTemplate.length; i++) {
                if (DataCenter.gI().EffectTemplate[i].name.equals(item.getItemTemplate().name)) {
                    Effect eff = new Effect((short) i, Effect.getValueEffectFormIdItem(item.id), System.currentTimeMillis(), 30 * (60 * 1000));
                    this.addEffect(eff);
                    return;
                }
            }
        } else if (item.getItemTemplate().type == 22 || item.getItemTemplate().type == 23) {
            removeItem(item);
            msgUseItemBag(item);
            int value = Effect.getValueEffectFormIdItem(item.id);
            if (item.getItemTemplate().type == 22 && delayHp < System.currentTimeMillis()) {
                delayHp = System.currentTimeMillis() + 3000;
                addHp(value * 6);
            } else if (item.getItemTemplate().type == 23 && delayMp < System.currentTimeMillis()) {
                delayMp = System.currentTimeMillis() + 3000;
                addMp(value * 6);
            }
        } else if (item.getItemTemplate().type == 29) {
            if (item.id == 159) {
                Effect effect = new Effect((short) 42, 50, System.currentTimeMillis(), 60 * (60 * 1000) * 5);
                addEffect(effect);
            } else if (item.id == 281) {
                Effect effect = new Effect((short) 42, 75, System.currentTimeMillis(), 60 * (60 * 1000) * 5);
                addEffect(effect);
            } else if (item.id == 347) {
                Effect effect = new Effect((short) 42, 100, System.currentTimeMillis(), 60 * (60 * 1000) * 5);
                addEffect(effect);
            }
            if (item.id == 150) {
                if (Bag.sknSo >= 3) {
                    return;
                }
                Bag.sknSo++;
                Point.diemKyNang += 1;
                msgUpdateDataChar();
            } else if (item.id == 151) {
                if (Bag.sknTrung >= 2) {
                    return;
                }
                Bag.sknTrung++;
                Point.diemKyNang += 2;
                msgUpdateDataChar();
            } else if (item.id == 152) {
                if (Bag.sknCao >= 1) {
                    return;
                }
                Bag.sknCao++;
                Point.diemKyNang += 3;
                msgUpdateDataChar();
            }
            removeItem(item);
            msgUseItemBag(item);
        } else if (item.getItemTemplate().type == 100) {
            UseItemType100(item);
        } else if (item.getItemTemplate().type == 28) {
            long count = Arrays.stream(Bag.arrItemExtend)
                    .filter(Objects::nonNull)
                    .count();

            if (count == 3) {
                service.alertMessage("Vui lòng tháo túi mở rộng trước khi đeo mới");
                return;
            }
            removeItem(item, true);
            for (int i = 0; i < Bag.arrItemExtend.length; i++) {
                if (Bag.arrItemExtend[i] == null) {
                    Bag.arrItemExtend[i] = item;
                    updateItemBag();
                    msgUseItemBag(item);
                    item.index = i;
                    return;
                }
            }
        } else if (item.getItemTemplate().type == 99) {
            useItemType99(item);
        }

    }

    private void useItemType99(Item item) {
        switch (item.id) {
            case 619:
                if (Info.countHu > 9) {
                    service.serverMessage("Đã sử dụng hết lượt của hôm nay");
                    return;
                }
                if (item.getValueHu() > 0) {
                    Info.countHu++;
                    removeItem(item);
                    msgUseItemBag(item);
                    int exp = item.getValueHu();
                    int level = this.level();
                    Point.exp += exp;
                    int levelNew = this.level();
                    if (level != levelNew) {
                        upLevel(level, levelNew);
                    }
                    msgAddExp();
                } else
                    service.serverMessage("Hũ không có chứa kinh nghiệm");
                break;
            default:
                service.serverMessage("Item chưa thể sử dụng");
                break;
        }
    }

    private void UseItemType100(Item item) {
        if (Event.getEvent() != null) {
            Event.getEvent().useItem(this, item);
        }
        switch (item.id) {
            case 616:
                removeItem(item);
                msgUseItemBag(item);
                if (theGiuTien == 0) {
                    theGiuTien = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30);
                } else
                    theGiuTien += TimeUnit.DAYS.toMillis(30);
                getService().serverMessage("Thẻ giữ tiền đã được kích hoạt, Hạn sử dụng tới: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(theGiuTien));
                break;
            case 266:
                if (Info.lvPk < 1) {
                    service.serverMessage("Bạn không có điểm pk");
                    return;
                }
                removeItem(item);
                msgUseItemBag(item);
                Info.lvPk = (byte) Math.max(0, Info.lvPk - 5);
                break;
            case 300:
                if (timeOffline < 5) {
                    return;
                }
                timeOffline -= 5;
                int expdefaut = 140000;
                if (level() > 50) {
                    expdefaut *= 3;
                }
                removeItem(item);
                msgUseItemBag(item);
                addExp(expdefaut * 5);
                break;
            case 707:
                removeItem(item);
                msgUseItemBag(item);
                addEffect(new Effect((short) 91, 100, System.currentTimeMillis(), (int) TimeUnit.DAYS.toMillis(7)));
                break;
            case 919:
                removeItem(item);
                msgUseItemBag(item);
                MapLangCo.gI().maps.get(0).addChar(this);
                break;
            case 164:
                removeItem(item);
                msgUseItemBag(item);
                int itemid = ItemDrop.ITEM_TUI_MAY_MAN.next();
                if (itemid == 163) {
                    addBacKhoa(Utlis.nextInt(5000, 30000));
                } else {
                    Item newitem = new Item(itemid);
                    newitem.isLock = true;
                    addItem(newitem);
                    msgAddItemBag(newitem);
                }
                break;
            case 174:
                if (Info.pointHokage.Quan >= 5000) {
                    this.user.session.sendMessage(HanderMessage.SendThongBao("Không thể sử dụng thêm", HanderMessage.RED_MID));
                    return;
                }
                Info.pointHokage.Quan += 5;
                removeItem(item);
                msgUseItemBag(item);
                break;
            case 175:
                if (Info.pointHokage.BaoTay >= 5000) {
                    this.user.session.sendMessage(HanderMessage.SendThongBao("Không thể sử dụng thêm", HanderMessage.RED_MID));
                    return;
                }
                Info.pointHokage.BaoTay += 5;
                removeItem(item);
                msgUseItemBag(item);
                break;
            case 179:
                if (Info.pointHokage.Dai >= 5000) {
                    this.user.session.sendMessage(HanderMessage.SendThongBao("Không thể sử dụng thêm", HanderMessage.RED_MID));
                    return;
                }
                Info.pointHokage.Dai += 5;
                removeItem(item);
                msgUseItemBag(item);
                break;
            case 216:
                if (Info.pointHokage.Giay >= 5000) {
                    this.user.session.sendMessage(HanderMessage.SendThongBao("Không thể sử dụng thêm", HanderMessage.RED_MID));
                    return;
                }
                Info.pointHokage.Giay += 5;
                removeItem(item);
                msgUseItemBag(item);
                break;
            case 217:
                if (Info.pointHokage.Tui >= 5000) {
                    this.user.session.sendMessage(HanderMessage.SendThongBao("Không thể sử dụng thêm", HanderMessage.RED_MID));
                    return;
                }
                Info.pointHokage.Tui += 5;
                removeItem(item);
                msgUseItemBag(item);
                break;
            case 218:
                if (Info.pointHokage.Vk >= 5000) {
                    this.user.session.sendMessage(HanderMessage.SendThongBao("Không thể sử dụng thêm", HanderMessage.RED_MID));
                    return;
                }
                Info.pointHokage.Vk += 5;
                removeItem(item);
                msgUseItemBag(item);
                break;
            case 248:
                if (Info.pointHokage.Ao >= 5000) {
                    this.user.session.sendMessage(HanderMessage.SendThongBao("Không thể sử dụng thêm", HanderMessage.RED_MID));
                    return;
                }
                Info.pointHokage.Ao += 5;
                removeItem(item);
                msgUseItemBag(item);
                break;
            case 278:
                if (Info.pointHokage.Day >= 5000) {
                    this.user.session.sendMessage(HanderMessage.SendThongBao("Không thể sử dụng thêm", HanderMessage.RED_MID));
                    return;
                }
                Info.pointHokage.Day += 5;
                removeItem(item);
                msgUseItemBag(item);
                break;
            case 302:
                if (Info.pointHokage.Moc >= 5000) {
                    this.user.session.sendMessage(HanderMessage.SendThongBao("Không thể sử dụng thêm", HanderMessage.RED_MID));
                    return;
                }
                Info.pointHokage.Moc += 5;
                removeItem(item);
                msgUseItemBag(item);
                break;
            case 315:
                if (Info.pointHokage.OngTieu >= 5000) {
                    this.user.session.sendMessage(HanderMessage.SendThongBao("Không thể sử dụng thêm", HanderMessage.RED_MID));
                    return;
                }
                Info.pointHokage.OngTieu += 5;
                removeItem(item);
                msgUseItemBag(item);
                break;
            case 294:
                if (Bag.arrItemBody[11] != null) {
                    if (Bag.arrItemBody[11].checkFullTuLuyen()) {
                        service.serverMessage("Độ tu luyện đã đầy");
                        return;
                    }
                    removeItem(item);
                    msgUseItemBag(item);
                    Bag.arrItemBody[11].updateTuLuyen(25);
                    msgUpdateItemBody();
                }
                break;
            case 265:
                removeItem(item);
                msgUseItemBag(item);
                this.addEffect(new Effect((short) 47, 200, System.currentTimeMillis(), 60 * 1000 * 25));
                break;
            case 285:
                removeItem(item);
                msgUseItemBag(item);
                this.addEffect(new Effect((short) 47, 300, System.currentTimeMillis(), 60 * 1000 * 25));
                break;
            case 704:
                if (Info.numct < 18) {
                    Info.numct = 18;
                    removeItem(item);
                    msgUseItemBag(item);
                    service.sendNumCt();
                    user.session.sendMessage(HanderMessage.resetScreen());
                } else {
                    service.alertMessage("Mỗi nhân vật chỉ có thể sử dụng một lần");
                }
                break;
            case 782: // bùa phân thân
                service.alertMessage("Sử dụng bùa phân thân bằng cách mở giao diện nhân vật - Sách kĩ năng chiến đấu (Yêu cầu SKNCĐ Level 18)");
                break;
            case 790:
                if (Info.numct == 17) {
                    service.alertMessage("Vui lòng sử dụng nhẫn thuật sơ cấp trước");
                    return;
                }
                if (Info.numct < 19) {
                    Info.numct = 19;
                    removeItem(item);
                    msgUseItemBag(item);
                    service.sendNumCt();
                    user.session.sendMessage(HanderMessage.resetScreen());
                } else {
                    service.alertMessage("Mỗi nhân vật chỉ có thể sử dụng một lần");
                }
                break;
            case 688:
                lockViThu.lock();
                try {
                    if (Bag.arrItemBody[10] == null || Bag.arrItemBody[10].id > 484 || Bag.arrItemBody[10].id < 476) {
                        service.serverMessage("Không có vĩ thú");
                        return;
                    }
                    if (listSkill.size() >= 6) {
                        service.serverMessage("Vui lòng xoá skill trước khi dùng thêm");
                        return;
                    }
                    removeItem(item);
                    msgUseItemBag(item);
                    SkillClan skill = (SkillClan) DataCenter.gI().vSkillClan.get(Utlis.nextInt(0, DataCenter.gI().vSkillClan.size() - 2));
                    SkillClan skilladd = new SkillClan();
                    skilladd.id = skill.id;
                    skilladd.name = skill.name;
                    skilladd.strOptions = skill.strOptions;
                    skilladd.levelNeed = skill.levelNeed;
                    listSkill.add(skilladd);
                    msgSkillViThu();
                } catch (Exception e) {

                } finally {
                    lockViThu.unlock();
                }
                break;
            case 860:
                lockViThu.lock();
                try {
                    if (listSkill.size() < 6) {
                        service.serverMessage("Cần full skill vĩ thú mới sử dụng được skill đặc biệt");
                        return;
                    }
                    if (listSkill.size() >= 7) {
                        service.serverMessage("Vui lòng xoá skill trước khi dùng thêm");
                        return;
                    }
                    removeItem(item);
                    msgUseItemBag(item);
                    SkillClan skilldb = (SkillClan) DataCenter.gI().vSkillClan.get(6);
                    SkillClan skilladd = new SkillClan();
                    skilladd.id = skilldb.id;
                    skilladd.name = skilldb.name;
                    skilladd.strOptions = skilldb.strOptions;
                    skilladd.levelNeed = skilldb.levelNeed;
                    listSkill.add(6, skilladd);
                    msgSkillViThu();
                } catch (Exception e) {

                } finally {
                    lockViThu.unlock();
                }
                break;
            case 310:
                break;
            case 312:
                if (item.amount >= 1000) {
                    if (item.amount > 1000) {
                        removeItemByAmount(item, 1000);
                        msgUseItemBag(item);
                    } else {
                        removeItem(item, true);
                        msgRemoveItemBag(item);
                    }
                    Item sach = new Item(150);
                    sach.isLock = true;
                    addItem(sach);
                    msgAddItemBag(sach);
                }
                break;
            case 313:
                if (item.amount >= 1000) {
                    if (item.amount > 1000) {
                        removeItemByAmount(item, 1000);
                        msgUseItemBag(item);
                    } else {
                        removeItem(item, true);
                        msgRemoveItemBag(item);
                    }
                    Item sach = new Item(153);
                    sach.isLock = true;
                    addItem(sach);
                    msgAddItemBag(sach);
                }
                break;
            case 754:
                if (item.amount >= 100000) {
                    if (Bag.bac < 1000000) {
                        service.warningMessage("Cần 1.000.000 bạc");
                        return;
                    }
                    removeItemByAmount(item, 100000);
                    addBac(-1000000);
                    Item sieucap = new Item(749);
                    sieucap.isLock = true;
                    sieucap.he = Info.idhe;
                    sieucap.addItemOption(new ItemOption(128, 0, 12000));
                    sieucap.addItemOption(new ItemOption(331, 50, 100));
                    sieucap.addItemOption(new ItemOption(0, 1800, 2000));
                    sieucap.addItemOption(new ItemOption(1, 1800, 2000));
                    if (sieucap.he == 1) {
                        sieucap.addItemOption(new ItemOption(109, 180, 200));
                        sieucap.addItemOption(new ItemOption(114, 380, 400));
                    } else if (sieucap.he == 2) {
                        sieucap.addItemOption(new ItemOption(110, 180, 200));
                        sieucap.addItemOption(new ItemOption(115, 380, 400));
                    } else if (sieucap.he == 3) {
                        sieucap.addItemOption(new ItemOption(111, 180, 200));
                        sieucap.addItemOption(new ItemOption(113, 380, 400));
                    } else if (sieucap.he == 4) {
                        sieucap.addItemOption(new ItemOption(112, 180, 200));
                        sieucap.addItemOption(new ItemOption(117, 380, 400));
                    } else if (sieucap.he == 5) {
                        sieucap.addItemOption(new ItemOption(108, 180, 200));
                        sieucap.addItemOption(new ItemOption(113, 380, 400));
                    }
                    sieucap.createItemOptions();
                    addItem(sieucap);
                    service.serverMessage("Đổi thành công bùa nổ siêu cấp");
                    service.resetScreen();
                } else {
                    service.warningMessage("Cần 100.000 mảnh");
                }
                break;
            case 498:
                if (!Manager.gI().useItem.containsKey(Info.name)) {
                    removeItem(item);
                    msgUseItemBag(item);
                    Info.countCamThuat += 1;
                    Manager.gI().useItem.put(Info.name, (int) item.id);
                    service.alertMessage("Tăng thêm 1 lượt cấm thuật");
                } else
                    service.alertMessage("Mỗi ngày chỉ dùng 1 lần");
                break;
            case 568:
                if (!Manager.gI().useItem_2.containsKey(Info.name)) {
                    removeItem(item);
                    msgUseItemBag(item);
                    Info.countCamThuat += 2;
                    Manager.gI().useItem_2.put(Info.name, (int) item.id);
                    service.alertMessage("Tăng thêm 2 lượt cấm thuật");
                } else
                    service.alertMessage("Mỗi ngày chỉ dùng 1 lần");
                break;
            case 295:
                if (level() > 59) {
                    return;
                }
                if (Info.idClass == 0) {
                    service.alertMessage("Vui lòng vào lớp");
                    return;
                }
                if (getCountNullItemBag() < 1) {
                    service.alertMessage("Không đủ slot ruong do");
                    return;
                }
                removeItem(item);
                msgUseItemBag(item);
                int[] itemTypes = new int[]{0, 2, 4, 6, 8};
                int type = itemTypes[Utlis.nextInt(0, itemTypes.length - 1)];
                Item trangbi = Item.getItemWithTypeAndLevel(type, level(), Info.gioiTinh, Info.idClass);
                Item.setOptionsTrangBiPhuKien(trangbi, level());
                Item.GetOptionHokage(trangbi);
                trangbi.createItemOptions();
                trangbi.strOptions += ";148,0";
                trangbi.a(8);
                addItem(trangbi);
                msgAddItemBag(trangbi);
                break;
            case 296:
                if (level() > 59) {
                    return;
                }
                if (Info.idClass == 0) {
                    service.alertMessage("Vui lòng vào lớp");
                    return;
                }
                if (getCountNullItemBag() < 1) {
                    service.alertMessage("Không đủ slot ruong do");
                    return;
                }
                removeItem(item);
                msgUseItemBag(item);
                int[] itemTypes1 = new int[]{3, 5, 7, 9};
                int type1 = itemTypes1[Utlis.nextInt(0, itemTypes1.length - 1)];
                Item phukien = Item.getItemWithTypeAndLevel(type1, level(), Info.gioiTinh, Info.idClass);
                Item.setOptionsTrangBiPhuKien(phukien, level());
                Item.GetOptionHokage(phukien);
                phukien.createItemOptions();
                phukien.strOptions += ";148,0";
                phukien.a(8);
                addItem(phukien);
                msgAddItemBag(phukien);
                break;
            case 297:
                if (level() > 59) {
                    return;
                }
                if (Info.idClass == 0) {
                    service.alertMessage("Vui lòng vào lớp");
                    return;
                }
                if (getCountNullItemBag() < 1) {
                    service.alertMessage("Không đủ slot ruong do");
                    return;
                }
                removeItem(item);
                msgUseItemBag(item);
                Item vk = Item.getItemWithTypeAndLevel(1, level(), Info.gioiTinh, Info.idClass);
                Item.setOptionsVuKhi(vk, level());
                Item.GetOptionHokage(vk);
                vk.createItemOptions();
                vk.strOptions += ";148,0";
                vk.a(8);
                addItem(vk);
                msgAddItemBag(vk);
                break;
            case 368:
                if (Bag.Banh > 9) {
                    return;
                }
                removeItem(item);
                msgUseItemBag(item);
                Bag.Banh++;
                Point.diemTiemNang += 5;
                msgUpdateDataChar();
                updateTiemNang();
                break;
            case 369:
                if (Bag.banhUBao > 2) {
                    return;
                }
                removeItem(item);
                msgUseItemBag(item);
                Bag.banhUBao++;
                Point.diemKyNang += 1;
                msgUpdateDataChar();
                updateTiemNang();
                break;
            case 404:
                removeItem(item);
                msgUseItemBag(item);
                addEffect(new Effect((short) 66, 1, System.currentTimeMillis(), 60 * 1000 * 60));
                break;
            case 617:
                if (buaUeTho) {
                    return;
                }
                removeItem(item);
                msgUseItemBag(item);
                addEffect(new Effect((short) 81, 1, System.currentTimeMillis(), 60 * 1000 * 5));
                break;
            case 763:
                if (Bag.arrItemBody[10] == null) {
                    return;
                }
                if (Bag.arrItemBody[10].level == 18) {
                    service.alertMessage("Vĩ thú đã full sức mạnh");
                    return;
                }
                if (Bag.arrItemBody[10] != null && Bag.arrItemBody[10].isSucManh()) {
                    removeItem(item);
                    msgUseItemBag(item);
                    Bag.arrItemBody[10].updateViThu(500);
                    msgUpdateItemBody();
                } else {
                    service.alertMessage("Vui lòng mở sức mạnh vĩ thú");
                }
                break;
            case 428:
                removeItem(item);
                msgUseItemBag(item);
                Item ngoc = new Item(Utlis.nextInt(406, 413));
                ngoc.isLock = true;
                addItem(ngoc);
                msgAddItemBag(ngoc);
                break;
            case 705: // kinh nghiệm vô hạn
                if (Info.KinhNghiemVoHan >= 2) {
                    service.serverMessage("Mỗi nhân vật chỉ được sử dụng tối đa 2 lần");
                    return;
                }
                if (this.level() > 59) {
                    service.serverMessage("Chỉ được sử dụng dưới level 59");
                    return;
                }
                removeItem(item);
                msgUseItemBag(item);
                int level = this.level();
                setExp(DataCenter.gI().GetExpFormLevel(level + 1));
                Info.KinhNghiemVoHan += 1;

                try {
                    this.user.session.sendMessage(Message.c((byte) -43));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                this.user.session.sendMessage(HanderMessage.SendThongBao("Sử dụng kinh nghiệm vô hạn thành công, bạn được tăng 1 cấp!", HanderMessage.YELLOW_MID));
                break;
            case 558: // dầu cóc
                if (Info.countDauCoc >= 10) {
                    service.serverMessage("Mỗi ngày chỉ sử dụng tối đa 10 dầu cóc");
                    return;
                }
                removeItem(item);
                msgUseItemBag(item);
                Info.countDauCoc += 1;
                addExp(35000000);
                removeItem(item);
                msgUseItemBag(item);

                break;
            case 643:
                removeItem(item);
                msgUseItemBag(item);
                this.addEffect(new Effect((short) 85, 100, System.currentTimeMillis(), 60 * 1000 * 60));
                break;


            case 559: // vật phẩm phiên bản test
                if (getCountNullItemBag() >= 13) {
                    removeItem(item);
                    msgUseItemBag(item);
                    int[] listItem = {174, 175, 179, 216, 217, 218, 248, 278, 302, 315};

                    for (int i = 0; i < listItem.length; i++) {
                        Item it = new Item(listItem[i], true);
                        it.amount = 9999;
                        addItem(it);
                        msgAddItemBag(it);
                    }
                    for (int i = 934; i <= 938; i++) {
                        Item itemthuoc1z = new Item(i, true);
                        addItem(itemthuoc1z);
                        msgAddItemBag(itemthuoc1z);
                    }
                    Point.hoatLuc += 5000000;
                    user.session.sendMessage(HanderMessage.UpdateHoatLuc(Point.hoatLuc));
                }else {
                    service.serverMessage("Hành trang không đủ 13 ô trống");
                }
                break;

            case 361:
                if (getCountNullItemBag() >= 3) {
                    removeItem(item);
                    msgUseItemBag(item);
                    for (int i = 171; i <= 173; i++) {
                        Item itemthuoc1 = new Item(i, true);
                        addItem(itemthuoc1);
                        msgAddItemBag(itemthuoc1);
                    }
                }
                break;
            case 362:
                if (getCountNullItemBag() >= 3) {
                    removeItem(item);
                    msgUseItemBag(item);
                    for (int i = 355; i <= 357; i++) {
                        Item itemthuoc2 = new Item(i, true);
                        addItem(itemthuoc2);
                        msgAddItemBag(itemthuoc2);
                    }
                }
                break;


            case 644:
                removeItem(item);
                msgUseItemBag(item);
                this.addEffect(new Effect((short) 86, 100, System.currentTimeMillis(), 60 * 1000 * 60));
                break;
            case 434:
                if (item.amount >= 1000) {
                    if (item.amount > 1000) {
                        removeItemByAmount(item, 1000);
                        msgUseItemBag(item);
                    } else {
                        removeItem(item, true);
                        msgRemoveItemBag(item);
                    }
                    Item sach = new Item(435);
                    sach.isLock = true;
                    addItem(sach);
                    msgAddItemBag(sach);
                } else {
                    service.serverMessage("Không đủ 1000 mảnh để ghép thành sách kỹ năng chiến đấu");
                }
                break;
            case 178:
                if (Info._mapID != 85 || Info.cy != 692) {
                    service.serverMessage("Ở đây làm gì có cá");
                    return;
                }
                if (Info._mapID == 85 && Info.cy == 692) {
                    if (this.isCatchItem) {
                        return;
                    }
                    if (getCountNullItemBag() > 0) {
                        isCatchItem = true;
                        ScheduledExecutorService executor = ScheduledExecutor.getInstance();
                        int time = Utlis.nextInt(3500, 7000);
                        user.session.sendMessage(HanderMessage.takingItem(time, Info.idEntity, "Đang thả câu", 500));
                        future = executor.schedule(() -> {
                            try {
                                if (!isClean && isCatchItem) {
                                    if (item.getAmount() > 1) {
                                        removeItemByAmount(item, 1);
                                        msgUseItemBag(item);
                                    } else {
                                        removeItem(item, true);
                                        msgRemoveItemBag(item);
                                    }
                                    int[] idItem = {161, 6, 277, 6, 7, 7, 8, 9, 161, 277, 428, 353, 563, 565, 567, 10};
                                    int[] percent = {10, 20, 10, 20, 15, 20, 3, 2, 2, 2, 2, 2, 2, 2, 1};
                                    int index = Utlis.randomWithRate(percent, 100);
                                    int id = idItem[index];

                                    Item item2 = new Item(id);
                                    addItem(item2);
                                    msgAddItemBag(item2);
                                    if (!Info.khoaExp) {
                                        addExp(50000);
                                    }

                                    if (percent[index] <= 10) {
                                        chatPublic("Haha! Được một "
                                                + item2.getItemTemplate().name + " rồi nè!");
                                    }
                                    service.sendMessage(HanderMessage.TestMess7(Info.idEntity));
                                    isCatchItem = false;
                                    // addEventPoint(1, Events.TOP_FISHING);
                                }
                            } catch (Exception e) {
                                Log.error(" loi cau ca roi ", e);
                            }
                        }, time, TimeUnit.MILLISECONDS);
                    }
                }
                break;
            case 171:
            case 172:
            case 173:
            case 355:
            case 356:
            case 357:
            case 358:
            case 359:
            case 360:
                short idEff = 0;
                int value = 0;

                if (item.id >= 171 && item.id <= 173) {
                    idEff = (short) ((item.id - 171) + 39);
                    value = (item.id == 171 ? 1500 : (item.id == 172 ? 100 : 350));
                } else if (item.id >= 355 && item.id <= 357) {
                    idEff = (short) ((item.id - 355) + 39);
                    value = (item.id == 355 ? 3000 : (item.id == 356 ? 200 : 700));
                } else if (item.id >= 358 && item.id <= 360) {
                    idEff = (short) ((item.id - 358) + 39);
                    value = (item.id == 358 ? 4500 : (item.id == 359 ? 300 : 1050));
                }
                removeItem(item);
                msgUseItemBag(item);
                this.addEffect(new Effect(idEff, value, System.currentTimeMillis(), 60 * 1000 * 5));
                msgUpdateHpFull();
                msgUpdateMpFull();
                break;
            case 917:
                if (clan == null) {
                    service.serverMessage("Bạn chưa có gia tộc");
                    return;
                }
                if (clan.getLevel() < 5) {
                    service.serverMessage("Cần gia tộc cấp 5");
                    return;
                }
                if (Info.countTBGT <= 0) {
                    service.serverMessage("Mỗi ngày chỉ dùng được 50 cái");
                    return;
                }
                Info.countTBGT--;
                removeItem(item);
                msgUseItemBag(item);
                addClanPoint(Utlis.nextInt(100, 100));
                break;
            case 918:
                if (clan == null) {
                    service.serverMessage("Bạn chưa có gia tộc");
                    return;
                }
                if (clan.getLevel() < 10) {
                    service.serverMessage("Cần gia tộc cấp 10");
                    return;
                }
                removeItem(item);
                msgUseItemBag(item);
                addClanPoint(Utlis.nextInt(200, 200));
                break;
            case 599:
                if (item.amount < 1000) {
                    service.warningMessage("Cần 1000 mảnh để ghép");
                    return;
                }
                if (item.amount > 1000) {
                    removeItemByAmount(item, 1000);
                    msgUseItemBag(item);
                } else {
                    removeItem(item, true);
                    msgRemoveItemBag(item);
                }
                Item svc = new Item(600);
                svc.isLock = true;
                addItem(svc);
                msgAddItemBag(svc);
                break;
            case 600:
                if (Skill.arraySkill.length > 7) {
                    return;
                }
                removeItem(item);
                msgUseItemBag(item);
                Skill[] arraySkill_2 = new Skill[Skill.arraySkill.length + 1];
                for (int i = 0; i < Skill.arraySkill.length; i++) {
                    arraySkill_2[i] = Skill.arraySkill[i];
                }
                arraySkill_2[arraySkill_2.length - 1] = DataSkill.skills_57[Info.idClass - 1].cloneSkill();
                Skill.arraySkill = arraySkill_2;
                msgUpdateSkill();
                service.serverMessage("Chúc mừng bạn đã đánh thức huyết kế giới hạn");
                service.resetScreen();
                break;
            case 308:
                if (!zone.isDungeoClan()) {
                    return;
                }
                if (isBiDuoc) {
                    return;
                }
                removeItem(item);
                msgUseItemBag(item);
                this.addEffect(new Effect((short) 49, 1, System.currentTimeMillis(), 60 * 1000 * 5));
                break;
            case 437:
                if (timeChangeName > System.currentTimeMillis()) {
                    Date date = Utlis.getDate(timeChangeName);
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                    // Chuyển đổi Date thành String với định dạng đã chọn
                    String formattedDate = formatter.format(date);
                    service.serverMessage("Bạn vừa mới đổi tên vui lòng đợi hết thời gian ,Thời gian chờ của bạn tới : " + formattedDate);
                    return;
                }
                service.submenu(DataCmd.CHANGE_NAME, "Mời bạn nhập vào tên muốn đổi");
                break;
            case 915:
                updateItemRenew();
                StringBuilder strBuilder = new StringBuilder();
                appendItems(strBuilder, tanto, "Tanto");
                appendItems(strBuilder, aoChoang, "Áo Choàng");
                appendItems(strBuilder, thoiTrang, "Thời trang");
                try {
                    Writer writer = new Writer();
                    writer.writeShort(item.index);
                    writer.writeUTF(strBuilder.toString());
                    this.service.openTabItem(writer);
                } catch (IOException e) {

                }
                break;


            case 931:
                if (Info._mapID != 85 || Info.cy != 692) {
                    service.serverMessage("Ở đây làm gì có cá,Muốn câu cá hãy đến làng đá");
                    return;
                }
                if (Info._mapID == 85 && Info.cy == 692) {
                    removeItem(item);
                    msgUseItemBag(item);
                    int[] idItem = {920, 921, 922, 923, 924, 925};
                    int[] percent = {25, 25, 20, 20, 5, 5};
                    int index = Utlis.randomWithRate(percent, 100);
                    int id = idItem[index];

                    Item item2 = new Item(id);
                    addItem(item2);
                    msgAddItemBag(item2);
                    if (!Info.khoaExp) {
                        addExp(50000);
                    }

                    if (percent[index] <= 10) {
                        chatPublic("Haha! Được một con "
                                + item2.getItemTemplate().name + " rồi nè!");
                    }
                    service.sendMessage(HanderMessage.TestMess7(Info.idEntity));
                    isCatchItem = false;
                    getEventPoint().addPoint(Summer.TOP_FISH, 1);
                    getEventPoint().addPoint(EventPoint.DIEM_TIEU_XAI, 1);
                }
                break;
        }
        if (item.id == 169) {
            removeItem(item);
            msgUseItemBag(item);
            if (Info.idClass == 1 || Info.idClass == 5) {
                Point.arrayTiemNang[0] = 10;
                Point.arrayTiemNang[1] = 0;
                Point.arrayTiemNang[2] = 5;
                Point.arrayTiemNang[3] = 5;
            } else {
                Point.arrayTiemNang[0] = 0;
                Point.arrayTiemNang[1] = 0;
                Point.arrayTiemNang[2] = 15;
                Point.arrayTiemNang[3] = 5;
            }
            int so = 10 * Bag.stnSo;
            int trung = 20 * Bag.stnTrung;
            int cao = 30 * Bag.stnCao;
            int banh = 5 * Bag.Banh;
            Point.diemTiemNang = this.level() * 10 + so + trung + cao + banh;
            msgUpdateDataChar();
            updateTiemNang();
            return;
        }
        if (item.id == 182) {

            if (Info.countUseBinhHoatLuc >= 20) {
                service.serverMessage("Mỗi ngày chỉ sử dụng tối đa 20 bình hoạt lực");
                return;
            }
            removeItem(item);
            msgUseItemBag(item);
            Point.hoatLuc += 50;
            Info.countUseBinhHoatLuc += 1;
            user.session.sendMessage(HanderMessage.UpdateHoatLuc(Point.hoatLuc));
        }
        if (item.id == 231) {
            removeItem(item);
            msgUseItemBag(item);
            Item so = new Item(176);
            so.amount = 100;
            addItem(so);
            msgAddItemBag(so);
        }
        if (item.id == 782) {
            if (Point.diempt < 1) {
                return;
            }
            removeItem(item);
            msgUseItemBag(item);
            addEffect(new Effect((short) 99, 2 * Point.diempt, System.currentTimeMillis(), 60 * 1000 * 30));
        }
        if (item.id == 779) {
            if (Point.diempt < 1) {
                return;
            }
            removeItem(item);
            msgUseItemBag(item);
            addEffect(new Effect((short) 99, 2 * Point.diempt, System.currentTimeMillis(), 60 * 1000 * 60));
        }
        if (item.id == 177) {
            if (Info.countRuong > 7) {
                service.alertMessage("Mỗi ngày chỉ mở được 8 rương");
                return;
            }
            Info.countRuong++;
            removeItem(item);
            msgUseItemBag(item);
            int random = Utlis.nextInt(100);
            int iditem = -1;
            if (random < 5) {
                iditem = 9;
            } else if (random < 15) {
                iditem = 8;
            } else {
                iditem = 7;
            }
            Item da = new Item(iditem);
            da.amount = 1;
            da.isLock = true;
            addItem(da);
            msgAddItemBag(da);
        }
        if (item.id == 170) {
            removeItem(item);
            msgUseItemBag(item);
            boolean isHuyetKe = false;
            if (Skill.arraySkill[Skill.arraySkill.length - 1].idTemplate == DataSkill.skills_57[Info.idClass - 1].idTemplate) {
                isHuyetKe = true;
            }
            Skill[][] _arraySkill = new Skill[][]{
                    DataSkill.skills_0.clone(),
                    DataSkill.skills_1.clone(),
                    DataSkill.skills_2.clone(),
                    DataSkill.skills_3.clone(),
                    DataSkill.skills_4.clone(),
                    DataSkill.skills_5.clone(),};
            Skill.arraySkill = _arraySkill[Info.idClass];
            if (isHuyetKe) {
                Skill[] arraySkill_2 = new Skill[Skill.arraySkill.length + 1];
                for (int i = 0; i < Skill.arraySkill.length; i++) {
                    arraySkill_2[i] = Skill.arraySkill[i];
                }
                arraySkill_2[arraySkill_2.length - 1] = DataSkill.skills_57[Info.idClass - 1].cloneSkill();
                Skill.arraySkill = arraySkill_2;
            }
            Skill.skillFight = Skill.arraySkill[0];
            int so = 1 * Bag.sknSo;
            int trung = 2 * Bag.sknTrung;
            int cao = 3 * Bag.sknCao;
            Point.diemKyNang = this.level() * 1 + so + trung + cao + Bag.banhUBao;
            msgUpdateDataChar();
            msgUpdateSkill();
            return;
        }

        if (item.id == 435) {
            if (Info.sachChienDau < 16) {
                removeItem(item);
                msgUseItemBag(item);
                Info.sachChienDau++;
                Bag.itemSach = new Item(435);
                Bag.itemSach.strOptions = "207,0,-1;208,0,-1";
                Bag.itemSach.a(Info.sachChienDau);
                msgUpdateSachChienDau();
            } else {
                service.alertMessage("Vui lòng sử dụng sách kỹ năng chiến đấu cao hơn");
            }
            return;
        }
        if (item.id == 719) {
            if (Info.sachChienDau == 16) {
                removeItem(item);
                msgUseItemBag(item);
                Info.sachChienDau = 17;
                Bag.itemSach = new Item(719);
                Bag.itemSach.strOptions = "207,0,-1;208,0,-1;311,150,-1";
                Bag.itemSach.a(Info.sachChienDau);
                msgUpdateSachChienDau();
            }
            return;
        }
        if (item.id == 778) {
            if (Info.sachChienDau == 17) {
                removeItem(item);
                msgUseItemBag(item);
                Info.sachChienDau = 18;
                Bag.itemSach = new Item(719);
                Bag.itemSach.strOptions = "207,0,-1;208,0,-1;311,300,-1";
                Point.maxpt = 5;
                Bag.itemSach.a(Info.sachChienDau);
                msgUpdateSachChienDau();
                msgDataBag();
            }
            return;
        }
        if (item.id == 277) {
            if (getCountNullItemBag() >= 3) {
                removeItem(item);
                msgUseItemBag(item);
                for (int i = 4; i <= 6; i++) {
                    Item itemDa = new Item(i, true);
                    addItem(itemDa);
                    msgAddItemBag(itemDa);
                }
            }
            return;
        }
        if (item.id == 267) {
            removeItem(item);
            msgUseItemBag(item);
            int level = this.level() / 5 * 5;
            if (level >= 60) {
                level = 60;
            }
            while (true) {
                for (int j = 0; j < DataCenter.gI().ItemTemplate.length; j++) {
                    if (DataCenter.gI().ItemTemplate[j].type == 24 && DataCenter.gI().ItemTemplate[j].levelNeed == level) {
                        for (int i = 0; i < DataCenter.gI().EffectTemplate.length; i++) {
                            if (DataCenter.gI().EffectTemplate[i].name.equals(DataCenter.gI().ItemTemplate[j].name)) {
                                this.addEffect(new Effect((short) i, Effect.getValueEffectFormIdItem(DataCenter.gI().ItemTemplate[j].id), System.currentTimeMillis(), (15 * (60 * (60 * 1000)))));
                                return;
                            }
                        }
                        return;
                    }
                }
                level -= 10;
                level = this.level() / 5 * 5;
            }
        }
        if (item.id == 913) {
            msgOpenTabConfig(item);
            return;
        }
        if (item.id == 914) {
//            msgOpenTabSaoCuongHoa(item);
            return;
        }
        if (item.id == 190) {
            int exp = item.getAmount();
            removeItem(item, true);
            addExp(exp);
            msgRemoveItemBag(item);
            return;
        }
        if (item.id == 933) {
            if (getCountNullItemBag() < 16) {
                warningBagFull();
                return;
            }
            int[] idItem = {353, 563, 565, 567, 428, 174, 175, 179, 216, 217, 218, 248, 278, 302, 315, 434};
            for (int i = 0; i < idItem.length; i++) {
                int id = idItem[i];
                Item item2 = new Item(id);
                item2.amount = 9999;
                addItem(item2);
                msgAddItemBag(item2);
            }
            removeItem(item);
            msgUpdateItemBag(item);
            user.session.sendMessage(HanderMessage.resetScreen());
            return;
        }

        if (item.id == 161) {
            Item tinhThach = new Item(160);
            tinhThach.amount = 100;
            tinhThach.isLock = item.isLock;
            addItem(tinhThach);
            msgAddItemBag(tinhThach);
            removeItem(item);
            msgUpdateItemBag(item);
            return;
        }
        if (item.id == 269) {
            int gioiTinh = Info.gioiTinh;
            int idClass = Info.idClass;
            if (idClass == 0) {
                service.alertMessage("Ban chua nhap hoc");
                return;
            }
            Item[] _269 = new Item[]{
                    Item.getItemWithTypeAndLevel(0, 50, gioiTinh, idClass),
                    Item.getItemWithTypeAndLevel(1, 50, gioiTinh, idClass),
                    Item.getItemWithTypeAndLevel(2, 50, gioiTinh, idClass),
                    Item.getItemWithTypeAndLevel(3, 50, gioiTinh, idClass),
                    Item.getItemWithTypeAndLevel(4, 50, gioiTinh, idClass),
                    Item.getItemWithTypeAndLevel(5, 50, gioiTinh, idClass),
                    Item.getItemWithTypeAndLevel(6, 50, gioiTinh, idClass),
                    Item.getItemWithTypeAndLevel(7, 50, gioiTinh, idClass),
                    Item.getItemWithTypeAndLevel(8, 50, gioiTinh, idClass),
                    Item.getItemWithTypeAndLevel(9, 50, gioiTinh, idClass),};
            Item[] itemRQ = _269;
            int he1 = Utlis.nextInt(1, 5, idClass);
            int he2 = Utlis.nextInt(1, 5, idClass, he1);
            int he3 = Utlis.nextInt(1, 5, idClass, he1, he2);
            if (getCountNullItemBag() >= itemRQ.length) {
                for (int i = 0; i < itemRQ.length; i++) {
                    if (itemRQ[i] == null) {
                        itemRQ[i] = Item.getItemWithTypeAndLevel(i, 50, gioiTinh, idClass);
                    }
                    int level = itemRQ[i].getItemTemplate().levelNeed / 10 * 10;
                    if (itemRQ[i].isItemTrangBi()) {
                        if (itemRQ[i].isVuKhi()) {
                            itemRQ[i].he = Info.idClass;
                            Item.setOptionsVuKhi(itemRQ[i], level);
                        } else {
                            if (itemRQ[i].isSet1()) {
                                itemRQ[i].he = (byte) he1;
                            } else if (itemRQ[i].isSet2()) {
                                itemRQ[i].he = (byte) he2;
                            } else if (itemRQ[i].isSet3()) {
                                itemRQ[i].he = (byte) he3;
                            }
                            Item.setOptionsTrangBiPhuKien(itemRQ[i], level);
                        }
                        itemRQ[i].createItemOptions();
                        itemRQ[i].a(16);
                        itemRQ[i].isLock = false;
                    }
                    addItem(itemRQ[i]);
                }
                removeItem(item);
                msgUseItemBag(item);
                user.session.sendMessage(HanderMessage.resetScreen());
            }
            return;
        }
        if (item.id == 551) {
            int gioiTinh = Info.gioiTinh;
            int idClass = Info.idClass;
            if (idClass == 0) {
                service.alertMessage("Vao lop roi hay dung");
                return;
            }
            Item[] _269 = new Item[]{
                    Item.getItemWithTypeAndLevel(0, 30, gioiTinh, idClass),
                    Item.getItemWithTypeAndLevel(1, 30, gioiTinh, idClass),
                    Item.getItemWithTypeAndLevel(2, 30, gioiTinh, idClass),
                    Item.getItemWithTypeAndLevel(3, 30, gioiTinh, idClass),
                    Item.getItemWithTypeAndLevel(4, 30, gioiTinh, idClass),
                    Item.getItemWithTypeAndLevel(5, 30, gioiTinh, idClass),
                    Item.getItemWithTypeAndLevel(6, 30, gioiTinh, idClass),
                    Item.getItemWithTypeAndLevel(7, 30, gioiTinh, idClass),
                    Item.getItemWithTypeAndLevel(8, 30, gioiTinh, idClass),
                    Item.getItemWithTypeAndLevel(9, 30, gioiTinh, idClass),};
            Item[] itemRQ = _269;
            int he1 = Utlis.nextInt(1, 5, idClass);
            int he2 = Utlis.nextInt(1, 5, idClass, he1);
            int he3 = Utlis.nextInt(1, 5, idClass, he1, he2);
            if (getCountNullItemBag() >= itemRQ.length) {
                item.isLock = true;
                for (int i = 0; i < itemRQ.length; i++) {
                    if (itemRQ[i] == null) {
                        itemRQ[i] = Item.getItemWithTypeAndLevel(i, 20, gioiTinh, idClass);
                    }
                    int level = itemRQ[i].getItemTemplate().levelNeed / 10 * 10;
                    if (itemRQ[i].isItemTrangBi()) {
                        if (itemRQ[i].isVuKhi()) {
                            itemRQ[i].he = Info.idClass;
                            Item.setOptionsVuKhi(itemRQ[i], level);
                            Item.GetOptionHokage(itemRQ[i]);
                        } else {
                            if (itemRQ[i].isSet1()) {
                                itemRQ[i].he = (byte) he1;
                            } else if (itemRQ[i].isSet2()) {
                                itemRQ[i].he = (byte) he2;
                            } else if (itemRQ[i].isSet3()) {
                                itemRQ[i].he = (byte) he3;
                            }
                            Item.setOptionsTrangBiPhuKien(itemRQ[i], level);
                        }
                        Item.GetOptionHokage(itemRQ[i]);
                        itemRQ[i].createItemOptions();
                        itemRQ[i].a(6);
                        itemRQ[i].strOptions += ";148,0";
                    }
                    addItem(itemRQ[i]);
                    msgAddItemBag(itemRQ[i]);
                }
                removeItem(item);
                msgUseItemBag(item);

            }
            return;
        }
        Item[] itemRQ = ItemsFormItem.getGift(item.id);
        if (itemRQ != null) {
            if (getCountNullItemBag() >= itemRQ.length) {
                item.isLock = true;
                for (int i = 0; i < itemRQ.length; i++) {
                    if (itemRQ[i].isTypeTrangBi()) {
                        if (itemRQ[i].isVuKhi()) {
                            itemRQ[i].he = Info.idClass;
                            Item.setOptionsVuKhi(itemRQ[i], 1);
                        }
                        itemRQ[i].createItemOptions();
                    }
                    addItem(itemRQ[i]);
                }
                removeItem(item);
                msgUseItemBag(item);

                try {
                    for (int i = 0; i < itemRQ.length; i++) {
                        msgAddItemBag(itemRQ[i]);
                    }
                } catch (Exception ex) {

                }
            }

            return;
        }
    }

    private void msgSkillViThu() {
        try {
            Message m = Message.c((byte) -29);
            writerSkillViThu(m.writer);
            user.session.sendMessage(m);
        } catch (IOException e) {
        }
    }

    public void CheTao(int type) {
        if (getCountNullItemBag() < 1) {
            service.alertMessage("Hành trang không đủ chỗ trống");
            return;
        }

        Item item = null;
        Item tinhThach = FindItemBagUnlock(160);
        int requiredHoatLuc = 0;
        int requiredTinhThachAmount = 0;
        int requiredExpCheTao = 0;
        switch (type) {
            case 0:
                requiredHoatLuc = 5;
                requiredTinhThachAmount = 1;
                requiredExpCheTao = 0;
                break;
            case 1:
                requiredHoatLuc = 50;
                requiredTinhThachAmount = 10;
                requiredExpCheTao = 100;
                break;
            case 2:
                requiredHoatLuc = 500;
                requiredTinhThachAmount = 100;
                requiredExpCheTao = 200;
                break;
            case 3:
                requiredHoatLuc = 30;
                requiredTinhThachAmount = 5;
                requiredExpCheTao = 400;
                break;
            case 4:
                requiredHoatLuc = 70;
                requiredTinhThachAmount = 10;
                requiredExpCheTao = 500;
                break;
            case 5:
                requiredHoatLuc = 35;
                requiredExpCheTao = 600;
                break;
            case 6:
                requiredHoatLuc = 35;
                requiredExpCheTao = 700;
                break;
            case 7:
                requiredHoatLuc = 35;
                requiredExpCheTao = 800;
                break;
            case 8:
                requiredHoatLuc = 50;
                requiredExpCheTao = 900;
                break;
        }


        if (Info.expCheTao < requiredExpCheTao) {
            user.session.sendMessage(HanderMessage.SendThongBao("Không đủ cấp độ yêu cầu", HanderMessage.RED_MID));
            return;
        }
        if (requiredHoatLuc > 0 && requiredTinhThachAmount > 0) {
            if (Point.hoatLuc < requiredHoatLuc || tinhThach == null || tinhThach.amount < requiredTinhThachAmount) {
                user.session.sendMessage(HanderMessage.SendThongBao("Không đủ vật phẩm yêu cầu", HanderMessage.RED_MID));
                return;
            }
            removeItemByAmount(tinhThach, requiredTinhThachAmount);
            Point.hoatLuc -= requiredHoatLuc;

            switch (type) {
                case 0:
                case 1:
                    item = new Item(176);
                    item.amount = (type == 0) ? 1 : 10;
                    break;
                case 2:
                    item = new Item(231);
                    item.amount = 1;
                    break;
                case 3:
                    item = new Item(361);
                    item.amount = 1;
                    break;
                case 4:
                    item = new Item(362);
                    item.amount = 1;
                    break;
            }
        } else if (type >= 5 && type <= 8) {
            if (Point.hoatLuc < requiredHoatLuc) {
                user.session.sendMessage(HanderMessage.SendThongBao("Không đủ hoạt lực yêu cầu", HanderMessage.RED_MID));
                return;
            }
            int idItem = type == 5 ? 562 : type == 6 ? 564 : 566;
            int idCraft = type == 5 ? 563 : type == 6 ? 565 : 567;
            Item craftingItem = null;

            if (type == 8) {
                idItem = 354;
                idCraft = 353;
                requiredTinhThachAmount = 3;
                craftingItem = FindItemBag(idItem);
            } else {
                requiredTinhThachAmount = 5;
                craftingItem = FindItemBag(idItem);

            }

            if (craftingItem == null || craftingItem.getAmount() < requiredTinhThachAmount) {
                user.session.sendMessage(HanderMessage.SendThongBao("Không đủ vật phẩm yêu cầu", HanderMessage.RED_MID));
                return;
            }

            if (craftingItem.amount > requiredTinhThachAmount) {
                removeItemByAmount(craftingItem, requiredTinhThachAmount);
                msgUseItemBag(craftingItem);
            } else {
                removeItem(craftingItem, true);
                msgRemoveItemBag(craftingItem);
            }


            Point.hoatLuc -= requiredHoatLuc;
            item = new Item(idCraft);
            item.amount = 1;
            item.isLock = true;
        }

        if (item != null) {
            user.session.sendMessage(HanderMessage.MsgLoadPhanTram(this, 500, "Đang chế tạo"));
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            addItem(item);
            msgAddItemBag(item);
            if (type == 0) {
                Info.expCheTao += 1;
            } else {
                Info.expCheTao += 10;
            }
            user.session.sendMessage(HanderMessage.xoaTab(this));
            user.session.sendMessage(HanderMessage.MsgCheTao(Info.expCheTao, Point.hoatLuc, (short) item.amount, item));


        }
    }


    public void RecivePhucLoi(int index) {
        benefits.lock();
        try {
            if (getCountNullItemBag() < 5) {
                service.alertMessage("Hành trang không đủ chỗ trống");
                return;
            }

            for (TemplatePL temp : phucLoi.listPl) {
                if (temp.Id == index) {
                    service.alertMessage("Nhận 1 lần thôi cụ");
                    return;
                }
            }

            // Xử lý nhận quà
            TemplatePL temp = getItemPl(index);
            phucLoi.listPl.add(temp);
            switch (temp.IDPhucLoi) {
                case 0:
                    break;
                case 1:
                    break;
                case 2:
                    break;
                case 10:
                    switch (temp.IdItem) {
                        case 417:
                            if (Info.rank < 1)
                                Info.rank = 1;
                            Item item1 = new Item(161);
                            item1.isLock = true;
                            addItem(item1);
                            msgAddItemBag(item1);
                            msgGetInfo();
                            service.sendChar();
                            service.updateRank();
                            return;
                        case 418:
                            if (Info.rank < 2)
                                Info.rank = 2;
                            if (Info.BuffEXP < 5)
                                Info.BuffEXP = 5;
                            Item da = new Item(277);
                            da.isLock = true;
                            addItem(da);
                            msgAddItemBag(da);
                            msgGetInfo();
                            service.sendChar();
                            service.updateRank();
                            return;
                        case 419:
                            if (Info.rank < 3)
                                Info.rank = 3;
                            if (Info.BuffEXP < 10)
                                Info.BuffEXP = 10;
                            Manager.gI().countRank++;
                            item1 = new Item(266);
                            item1.amount = 5;
                            item1.isLock = true;
                            addItem(item1);
                            msgAddItemBag(item1);
                            msgGetInfo();
                            service.sendChar();
                            service.updateRank();
                            return;
                        case 420:
                            if (Info.rank < 4)
                                Info.rank = 4;
                            if (Info.BuffEXP < 10)
                                Info.BuffEXP = 10;
                            item1 = new Item(347);
                            item1.amount = 5;
                            Manager.gI().countRank++;
                            item1.isLock = true;
                            addItem(item1);
                            msgAddItemBag(item1);
                            msgGetInfo();
                            service.sendChar();
                            service.updateRank();
                            return;
                        case 421:
                            if (Info.rank < 5)
                                Info.rank = 5;
                            if (Manager.gI().rankCaoNhat < 5) {
                                Manager.gI().rankCaoNhat = 5;
                            }
                            if (Info.BuffEXP < 20)
                                Info.BuffEXP = 20;
                            item1 = new Item(7);
                            item1.amount = 1;
                            item1.isLock = true;
                            addItem(item1);
                            msgAddItemBag(item1);
                            Manager.gI().countRank++;
                            msgGetInfo();
                            service.sendChar();
                            service.updateRank();
                            return;
                        case 422:
                            if (Info.rank < 6)
                                Info.rank = 6;
                            if (Manager.gI().rankCaoNhat < 6) {
                                Manager.gI().rankCaoNhat = 6;
                            }
                            if (Info.BuffEXP < 20)
                                Info.BuffEXP = 20;
                            da = new Item(277);
                            da.amount = 15;
                            da.isLock = true;
                            addItem(da);
                            msgAddItemBag(da);
                            msgGetInfo();
                            service.sendChar();
                            Manager.gI().countRank++;
                            service.updateRank();
                            return;
                        case 423:
                            if (Info.rank < 7)
                                Info.rank = 7;
                            if (Manager.gI().rankCaoNhat < 7) {
                                Manager.gI().rankCaoNhat = 7;
                            }
                            if (Info.BuffEXP < 30)
                                Info.BuffEXP = 30;
                            item1 = new Item(161);
                            item1.amount = 10;
                            item1.isLock = true;
                            addItem(item1);
                            msgAddItemBag(item1);
                            msgGetInfo();
                            Manager.gI().countRank++;
                            service.sendChar();
                            service.updateRank();
                            return;
                        case 424:
                            if (Manager.gI().rankCaoNhat < 8) {
                                Manager.gI().rankCaoNhat = 8;
                            }
                            if (Info.rank < 8)
                                Info.rank = 8;
                            if (Info.BuffEXP < 30)
                                Info.BuffEXP = 30;
                            Item skn = new Item(152);
                            skn.isLock = true;
                            addItem(skn);
                            msgAddItemBag(skn);
                            msgGetInfo();
                            Manager.gI().countRank++;
                            service.sendChar();
                            service.updateRank();
                            return;
                        case 425:
                            if (Info.rank < 9)
                                Info.rank = 9;
                            if (Info.BuffEXP < 40)
                                Info.BuffEXP = 40;
                            if (Manager.gI().rankCaoNhat < 9) {
                                Manager.gI().rankCaoNhat = 9;
                            }
                            Manager.gI().countRank++;
                            Item stn = new Item(155);
                            stn.isLock = true;
                            addItem(stn);
                            msgAddItemBag(stn);
                            msgGetInfo();
                            service.sendChar();
                            service.updateRank();
                            return;
                        case 426:
                            if (Info.rank < 10)
                                Info.rank = 10;
                            if (Info.BuffEXP < 40)
                                Info.BuffEXP = 40;
                            if (Manager.gI().rankCaoNhat < 10) {
                                Manager.gI().rankCaoNhat = 10;
                            }
                            DanhHieu lucdao = new DanhHieu();
                            lucdao.TextDanhHieu = "Lục Đạo";
                            lucdao.timeStart = 0;
                            lucdao.timeEnd = -1 * 1000;
                            Info.danhHieus.add(lucdao);
                            doiDanhHieu();
                            Item ct = new Item(463);
                            ct.isLock = true;
                            ct.strOptions = "0,500;1,500;2,150;3,500;4,150;209,99";
                            ct.he = Info.idhe;
                            addItem(ct);
                            msgAddItemBag(ct);
                            Item chakra = new Item(763);
                            chakra.isLock = true;
                            chakra.amount = 500;
                            addItem(chakra);
                            msgAddItemBag(chakra);
                            msgGetInfo();
                            service.sendChar();
                            service.updateRank();
                            Manager.gI().countRank++;
                            return;
                    }
                    break;
            }
            if (temp.IdItem == 163) {
                this.addBacKhoa(temp.Amount);
                return;
            }
            if (temp.IdItem == 191) {
                this.addBac(temp.Amount);
                return;
            }
            if (temp.IdItem == 192) {
                this.addVangKhoa(temp.Amount);
                return;
            }
            if (temp.IdItem == 193) {
                this.addVang(temp.Amount);
                return;
            }
            Item item = new Item(temp.IdItem);
            item.isLock = temp.isLock;
            item.amount = temp.Amount;
            item.strOptions = temp.strOption;
            addItem(item);
            msgAddItemBag(item);
        } catch (Exception e) {
            Log.error("loi phuc loi roi :", e);
        } finally {
            benefits.unlock();
        }
    }

    public short getExpBuff() {
        short expbuff = 0;
        expbuff += Info.BuffEXP;
        expbuff += expTemp;
        expbuff += options[267] + options[66];
        return expbuff;
    }

    public TemplatePL getItemPl(int id) {
        for (TemplatePL temp : PhucLoi.getInstance().itemPL) {
            if (temp.Id == id) {
                return temp;
            }
        }
        return null;
    }

    public boolean checkItemPl(TemplatePL templatePL) {
        if (templatePL.IDPhucLoi == 13 && !phucLoi.goiHaoHoa) {
            return false;
        }
        if (templatePL.IDPhucLoi == 14 && !phucLoi.goiChiTon) {
            return false;
        }
        for (TemplatePL temp : phucLoi.listPl) {
            if (temp.Id == templatePL.Id) {
                return false;
            }
        }
        if (!checkPLByType(templatePL)) {
            return false;
        }
        return true;
    }

    private boolean checkPLByType(TemplatePL item) {
        switch (item.IDPhucLoi) {
            case 0: // online ngay
                if (item.yeucau > phucLoi.timeOnline) {
                    return false;
                }
                break;
            case 1:// qua 7 ngay
                if (item.yeucau > phucLoi.soNgayOnline) {
                    return false;
                }
                break;
            case 2://qua cap
                if (item.yeucau > level()) {
                    return false;
                }
                break;

            case 3://tieu ngay
                if (item.yeucau > phucLoi.tieuNgay) {
                    return false;
                }
                break;
            case 4: // Tieu Tuan
                if (item.yeucau > phucLoi.tieuTuan) {
                    return false;
                }
                break;
            case 5://nap ngay
                if (item.yeucau > phucLoi.napNgay) {
                    return false;
                }
                break;
            case 6://naptuan
                if (item.yeucau > phucLoi.napTuan) {
                    return false;
                }
                break;
            case 7://nap lien tuc
                if (item.yeucau > phucLoi.napLienTuc) {
                    return false;
                }
                break;
            case 8://nap 3 mốc
                if (item.yeucau > phucLoi.nap3moc) {
                    return false;
                }
                break;
            case 9://nap don
                if (item.yeucau > phucLoi.napDon) {
                    return false;
                }
                break;

            case 10: // Nap rank
                if (item.yeucau > Bag.pointNAP) {
                    return false;
                }
                break;
            case 11: // rank chung
                if (item.yeucau > Manager.gI().rankCaoNhat) {
                    return false;
                }
                break;
            case 12: // rank tất cả
                if (item.yeucau > Manager.gI().countRank) {
                    return false;
                }
                break;

        }
        return true;
    }

    public void msgRemoveItemBag(Item item) {
//        Log.debug("msgRemoveItemBag");
        try {
            Writer writer = new Writer();
            writer.writeShort(item.index);
            service.removeItemBag(writer);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void msgUpdateItemBag(Item item) {
        Log.debug("msgUpdateItemBag");
        try {
            Writer writer = new Writer();
            item.write(writer);
            service.updateItemBag(writer);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void msgAddItemBag(Item item) {
        Log.debug("msgAddItemBag");
        try {
            Writer writer = new Writer();
            item.write(writer);
            service.addItemBag(writer);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void msgUseItemBag(Item item) {
        Log.debug("msgUseItemBag");
        try {
            Writer writer = new Writer();
            writer.writeShort(item.index);
            writer.writeBoolean(item.isLock);
            if (item.isTypeTrangBi()) {

            } else if (item.getItemTemplate().type == 28) {
                writer.writeShort(Bag.arrItemBag.length);
            } else {
                writer.writeInt(item.getAmount());
            }
            service.useItemBag(writer);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (item.getAmount() <= 0) {
            msgRemoveItemBag(item);
        } else {
            msgUpdateItemBag(item);
        }
    }

    public int getCountNullItemBag() {
        int c = 0;
        for (int i = 0; i < Bag.arrItemBag.length; i++) {
            if (Bag.arrItemBag[i] == null) {
                c++;
            }
        }
        return c;
    }

    public boolean removeItem(Item item) {
        if (item == null) {
            return false;
        }
        for (int i = 0; i < Bag.arrItemBag.length; i++) {
            if (Bag.arrItemBag[i] == item) {
                item.index = (short) i;
                if (item.getAmount() > 1) {
                    Bag.arrItemBag[i].setAmount(Bag.arrItemBag[i].getAmount() - 1);
                } else {
                    Bag.arrItemBag[i].setAmount(0);
                    Bag.arrItemBag[i] = null;
                }
                return true;
            }
        }

        return false;
    }

    public boolean removeItemByAmount(Item item, int SoLuong) {
        if (item == null) {
            return false;
        }
        for (int i = 0; i < Bag.arrItemBag.length; i++) {
            if (Bag.arrItemBag[i] == item) {
                item.index = (short) i;
                if (item.getAmount() > SoLuong) {
                    Bag.arrItemBag[i].setAmount(Bag.arrItemBag[i].getAmount() - SoLuong);
                } else {
                    Bag.arrItemBag[i].setAmount(0);
                    Bag.arrItemBag[i] = null;
                }
                return true;
            }
        }

        return false;
    }

    public boolean removeItem(Item item, boolean clean) {
        if (item == null) {
            return false;
        }
        for (int i = 0; i < Bag.arrItemBag.length; i++) {
            if (Bag.arrItemBag[i] == item) {
                item.index = (short) i;
                if (item.getAmount() > 1 && !clean) {
                    Bag.arrItemBag[i].setAmount(Bag.arrItemBag[i].getAmount() - 1);
                } else {
                    Bag.arrItemBag[i].setAmount(0);
                    Bag.arrItemBag[i] = null;
                }
                return true;
            }
        }

        return false;
    }

    public int getSlotItemExtend(Item item) {
        if (item != null) {
            switch (item.id) {
                case 185:
                    return 9;
                case 186:
                    return 18;
                case 187:
                    return 27;
                case 468:
                    return 36;
            }
        }
        return 0;
    }

    public void updateItemBag() {

        int numAdd = 0;
        for (int i = 0; i < Bag.arrItemExtend.length; i++) {
            if (Bag.arrItemExtend[i] != null) {
                numAdd = numAdd + getSlotItemExtend(Bag.arrItemExtend[i]);
            }
        }
        Item[] _arrItemBag = new Item[27 + numAdd];
        for (int i = 0; i < _arrItemBag.length && i < Bag.arrItemBag.length; i++) {
            _arrItemBag[i] = Bag.arrItemBag[i];
        }
        Bag.arrItemBag = _arrItemBag;
    }

    public void findHuPhach() {
        huphach.clear();
        for (int i = 0; i < Bag.arrItemBag.length; i++) {
            Item item = Bag.arrItemBag[i];
            if (item != null && item.id == 619) {
                huphach.add(item);
            }
        }
    }

    public void addExp(long exp) {
//        if (getExpBuff() > 0) {
//            exp += exp * getExpBuff() / 100;
//        }
        exp *= 10;
        if (exp < 0) {
            exp = 0;
        }
        if (Info.khoaExp) {
            return;
        }
        int level = this.level();


        Point.exp += exp;
        int levelNew = this.level();
        if (level != levelNew) {
            upLevel(level, levelNew);
        }
        msgAddExp();
    }

    public void setExp(long exp) {
        int level = this.level();
        Point.exp = exp;
        int levelNew = this.level();
        if (level != levelNew) {
            upLevel(level, levelNew);
        }
        msgAddExp();
    }

    public void msgAddExp() {
        try {
            Writer writer = new Writer();
            writer.writeLong(Point.exp);
            writer.writeInt(Info.idEntity);
            if (zone != null)
                zone.addExpToAllChar(writer);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void sortItem(byte type) {
        if (type == 0) {
            sortItemBag();
        } else if (type == 1) {
            boxSort();
        }
    }

    public void msgSortItem(byte type) {
        try {
            Writer writer = new Writer();
            writer.writeByte(type);
            service.sortItem(writer);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void itemExtendToBag(byte index) {
        Item item = Bag.arrItemExtend[index];
        if (item == null) {
            return;
        }
        int num = getSlotItemExtend(item);
        int num_new = Bag.arrItemBag.length - num;
        ArrayList<Item> listItemBag = new ArrayList<Item>();
        Utlis.getArrayListNotNull(Bag.arrItemBag, listItemBag);
        if (listItemBag.size() - 1 < num_new) {
            Bag.arrItemExtend[index] = null;
            updateItemBag();
            sortItemBag();
            addItem(item);
            msgItemExtendToBag(index);
        } else {

        }

    }

    public void msgItemExtendToBag(byte index) {
        try {
            Writer writer = new Writer();
            writer.writeByte(index);
            writer.writeShort(Bag.arrItemBag.length);
            service.itemExtendToBag(writer);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void vutItem(short index) {
        if (InfoGame.isDie) {
            return;
        }
        if (trade != null) {
            return;
        }
        if (index < 0 || index >= Bag.arrItemBag.length || Bag.arrItemBag[index] == null || Bag.arrItemBag[index].isLock) {
            return;
        }
        if (isSecurity && !isUnlockSecurity) {
            service.warningMessage("Vui lòng mở khoá bảo mật trước khi sử dụng tính năng này");
            return;
        }

        Item item = Bag.arrItemBag[index];
        if (item != null) {
            msgRemoveItemBag(Bag.arrItemBag[index]);
            this.Bag.arrItemBag[index] = null;
            if (user.actived) {
                short x = this.Info.cx;
                short y = this.Info.cy;
                ItemMap itemMap = new ItemMap((short) zone.id_ENTITY_ITEM_MAP++);
                itemMap.setY((short) y);
                itemMap.setX((short) x);
                itemMap.setOwnerID(this.id);
                itemMap.setItem(item);
                zone.addItemMap(itemMap);
                try {
                    Writer writer = new Writer();
                    writer.writeInt(this.id);
                    itemMap.write(writer, -1, -1, zone);
                    service.vutItem(writer);
                } catch (IOException e) {

                }
            }
            Manager.gI().saveDropItemPlayer("Đã vứt item " + item.getItemTemplate().name + "với số lượng : " + item.amount, Info.name);
        }

    }

    public void tachItem(short index, int amount) {
        if (index < 0 || index >= Bag.arrItemBag.length || Bag.arrItemBag[index] == null || Bag.arrItemBag[index].getAmount() <= amount || amount > Short.MAX_VALUE || amount < 1) {
            return;
        }
        Item itemMain = Bag.arrItemBag[index];
        Item item2 = Bag.arrItemBag[index].cloneItem();
        itemMain.setAmount(itemMain.getAmount() - amount);
        item2.setAmount(amount);
        if (item2.id == 619) {
            item2.strOptions = "283,0";
        }
        for (int i = 0; i < Bag.arrItemBag.length; i++) {
            if (Bag.arrItemBag[i] == null) {
                item2.index = (short) i;
                Bag.arrItemBag[i] = item2;
                msgTachItem(itemMain, item2);
                return;
            }
        }
    }

    public void SellItem(short index, boolean check) {

        if (isSecurity && !isUnlockSecurity) {
            service.warningMessage("Vui lòng mở khoá bảo mật trước khi sử dụng tính năng này");
            return;
        }


        Item itemBag = this.Bag.arrItemBag[index];
        if (itemBag != null) {
            //  History history = new History(this.id, History.BAN_VAT_PHAM);
            //  history.setPrice(0, 1000, 0);
            // history.setBefore(this.Bag.bac, this.Bag.bacKhoa, this.Bag.vang, this.Bag.vangKhoa);
            // history.themItem(this.Bag.arrItemBag[index]);
            // history.setTime(System.currentTimeMillis());
            addBacKhoa(1000);
            removeItem(itemBag, true);
            //  history.setAfter(this.Bag.bac, this.Bag.bacKhoa, this.Bag.vang, this.Bag.vangKhoa);
            //  History.insert(history);
            try {
                MsgSellItem(index);
            } catch (IOException e) {
//                throw new RuntimeException(e);
            }
        }
    }

    private void MsgSellItem(short index) throws IOException {
        Message msg = new Message((byte) 119);
        msg.writeInt(Bag.bacKhoa);
        msg.writeInt(Bag.bac);
        msg.writeShort(index);
        user.session.sendMessage(msg);
    }

    /*
     public static void l(Message var0) {
     try {
     short var1 = var0.reader.dis.readShort();
     int var2 = var0.reader.dis.readInt();
     Char.gI().arrItemBag[var1].setAmount(var2);
     Char.gI().arrItemBag[var1].index = var1;
     short var6 = var0.reader.dis.readShort();
     int var5 = var0.reader.dis.readInt();
     Char.gI().arrItemBag[var6] = Char.gI().arrItemBag[var1].a();
     Char.gI().arrItemBag[var6].setAmount(var5);
     Char.gI().arrItemBag[var6].index = var6;
     } catch (Exception var4) {
     }
     }

     */
    public void msgTachItem(Item itemMain, Item item2) {
        try {
            Writer writer = new Writer();
            writer.writeShort(itemMain.index);
            writer.writeInt(itemMain.getAmount());
            writer.writeShort(item2.index);
            writer.writeInt(item2.getAmount());
            service.tachItem(writer);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void msgSendArrItemBag() {
        try {
            Writer writer = new Writer();
            writer.writeInt(Bag.bac);
            writeItemBag(writer, Bag.arrItemBag);
            service.sendArrItemBag(writer);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void cancelClan() {
        if (this.clan != null) {
            if (this.clan.getNumberMember() <= 1) {
                Member member = clan.getMemberByName(Info.name);
                if (member != null) {
                    clan.memberDAO.delete(member);
                }
                Clan.getClanDAO().delete(clan);
                service.showGiaToc();
                this.clan = null;
            } else {
                service.alertMessage("Chỉ có thể hủy gia tộc khi chỉ còn 1 thành viên.");
            }
        }
    }


    public void chatPublic(String str) {

//        Log.debug(str); //B1 152 : 428

        // B2 1028 : 284
        // B3 176 : 137
        // BIBOG 908 : 137
//        Log.debug("CX " + Info.cx + " CY " + Info.cy);
        chatAdmin(str);
        chatInZone(str);
//        if (str.equals("vxmm")) {
//            LuckyDraw lucky = LuckyDrawManager.getInstance().find(0);
//            lucky.show(this);
//        }
        Message m;
        if (str.equals("xoabua")) {
            Effect effect = getEffect(66);
            if (effect != null) {
                removeEffect(effect);
                HanderEff.RemovePointEff(this, effect);
                msgRemoveEffect(effect);
                service.serverMessage("Xóa bùa thành công");
            }
        }
        m = new Message((byte) 3);
        try {
            m.writeInt(Info.idEntity);
            m.writeShort(33);
            user.session.sendMessage(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void chatInZone(String str) {
        Message m = new Message((byte) 21);
        try {
            m.writeUTF(Info.name);
            m.writeUTF(str);
            zone.SendMessageInZone(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void chatAdmin(String str) {
        if (user.isAdmin) {
            if (str.startsWith("map")) {
                String[] arr = Utlis.split(str, " ");
                try {
                    int idMap = Integer.parseInt(arr[1]);
                    if (!DataCenter.gI().MapTemplate[idMap].notBlock) {

                        if (Map.maps[idMap].addChar(this)) {
                            Info._mapID = (short) idMap;
                        }

                    }
                } catch (Exception x) {
                    x.printStackTrace();
                }
            } else if (str.contains("buano")) {
                ItemOption[] options = Bag.arrItemBody[13].getItemOption();
                Item buanew = new Item(870);
                buanew.isLock = true;
                String stropt = "";
                stropt += options[0].getId() + "," + "0" + "," + "2500" + ";";
                stropt += options[1].getId() + "," + "0" + "," + "2500" + ";";
                stropt += "0,1500;";
                stropt += "1,1500;";
                stropt += "3,3500;";
                stropt += "2,350;";
                stropt += "5,350;";
                stropt += "180,450;";
                stropt += "197," + Utlis.nextInt(1, 3) + ";";
                stropt += "122," + Utlis.nextInt(10, 20);
                buanew.strOptions = stropt;
                addItem(buanew);
            } else if (str.contains("xoado")) {
                for (int i = 0; i < Bag.arrItemBag.length; i++) {
                    Bag.arrItemBag[i] = null;
                }
            } else if (str.startsWith(
                    "dead")) {
                DeadForest deadForest = new DeadForest(6, level());
                addWorld(deadForest);
                deadForest.addMember(this);
                deadForest.join(this);
//            } else if (str.equals("lathinh")) {
//                SelectCard.getInstance().open(this);
            } else if (str.startsWith("zonesummer")) {
                SummerEvent summerEvent = new SummerEvent(level());
                summerEvent.addMember(this);
                summerEvent.join(this);
            } else if (str.equals("daihoi")) {
                DaiHoiVoThuat.DAIHOI = new DaiHoiVoThuat();
                DaiHoiVoThuat.DAIHOI.join(1, this);
            } else if (str.startsWith(
                    "clan")) {
                Territory territory = new Territory(clan.id);
                addWorld(territory);
                territory.joinZone(this, 46);
            } else if (str.startsWith("resetmap")) {
                String[] arr = str.split(" ");
                int id = Integer.parseInt(arr[1]);
                Map.maps[id].close();
                Map.maps[id].update();
            } else if (str.startsWith(
                    "getmob")) {
                service.serverMessage("moblive:: " + zone.getLivingMonsters().size());
            } else if (str.contains(
                    "spawm")) {
                try {
                    BossManager.gI().spawnBoss();
                } catch (Exception e) {

                }
            } else if (str.equals("loidai")) {
                Arena arena = new Arena();
                arena.join(1, this);
                arena.join(2, this);
                addWorld(arena);
            } else if (str.equals("nhaphoc")) {
                this.Info.idClass = this.getLopFormSelectChar(this.Info.idChar);
                this.service.sendChar();
                this.Skill = new InfoSkill(this.Info.idClass);
                this.msgUpdateDataChar();
                this.msgUpdateSkill();
            } else if (str.contains(
                    "bosssk")) {
                try {
                    BossManager.gI().createBossTest();
                } catch (Exception e) {

                }
            } else if (str.contains(
                    "vithu")) {
                try {
                    Bag.arrItemBody[10].updateViThu(180000);
                } catch (Exception e) {

                }
            } else if (str.contains(
                    "addmob")) {
                String[] arr = Utlis.split(str, " ");
                short id = Short.parseShort(arr[1]);
                Mob mob = Mob.mobTask((short) id, Info.name, Info.cy, Info.cx);
                zone.monsters.add(mob);
                service.sendMessage(HanderMessage.AddMob(mob));
            } else if (str.contains(
                    "goimob")) {
                try {
                    for (Mob mob : zone.monsters) {
                        zone.reSpawnMobToAllChar(mob);
                    }
                } catch (Exception e) {

                }
            } else if (str.contains(
                    "diacung")) {
                Dungeon dungeon = (Dungeon) findWorld(World.DUNGEON);
                if (dungeon == null) {
                    dungeon = new Dungeon(level(), 3600);
                    addWorld(dungeon);
                    Dungeon.addDungeon(dungeon);
                    dungeon.addMember(this);
                    dungeon.join(this);
                } else {
                    dungeon.join(this);
                }
            } else if (str.equals("langco")) {
                MapLangCo.gI().maps.get(0).addChar(this);
            } else if (str.startsWith("resetmap")) {
                String[] arr = Utlis.split(str, " ");
                int mapid = Integer.parseInt(arr[1]);
                Map.maps[mapid].resetThreadUpdate();
            } else if (str.startsWith(
                    "uplv")) {
                String[] arr = Utlis.split(str, " ");
                clan.addExp(Integer.parseInt(arr[1]));
            } else if (str.startsWith("additem")) {
                String[] arr = Utlis.split(str, " ");
                try {
                    int id = Integer.parseInt(arr[1]);
                    int amount = Integer.parseInt(arr[2]);
                    Item itADD = new Item(id);
                    if (itADD.getItemTemplate().isXepChong) {
                        itADD.amount = amount;
                    } else {
                        itADD.amount = 1;
                        for (int i = 1; i < amount; i++) {
                            Item itA = new Item(id);
                            addItem(itA);
                            msgAddItemBag(itA);
                        }
                    }
                    if (this.addItem(itADD)) {
                        this.msgAddItemBag(itADD);
                        Log.debug("addITEM sucsees");
                    }

                } catch (Exception x) {
                    x.printStackTrace();
                }
            } else if (str.startsWith("setlv")) {
                String[] arr = Utlis.split(str, " ");
                try {
                    int id = Integer.parseInt(arr[1]);
                    setExp(DataCenter.gI().GetExpFormLevel(id));
                } catch (Exception x) {
                    x.printStackTrace();
                }
            } else if (str.contains("skill")) {
                try {
                    Message m = Message.c((byte) -29);
                    m.writeByte(1);
                    m.writeByte(13);
                    m.writeByte(0);
                    user.session.sendMessage(m);
                } catch (Exception e) {

                }
            } else if (str.contains("taskday")) {
                try {
                    Message m = new Message((byte) -5);
                    DataTaskDay taskDay = DataCenter.gI().DataTaskDay[1];
                    m.writeByte(2);
                    m.writeByte(taskDay.id);
                    m.writeShort(0);
                    m.writeBoolean(false);
                    user.session.sendMessage(m);
                } catch (Exception e) {

                }
            } else if (str.contains("task2")) {
                try {
                    Message m = new Message((byte) -5);
                    m.writeByte(1);
                    m.writeByte(1);
                    m.writeByte(0);
                    m.writeShort(4);
                    m.writeShort(5);
                    m.writeShort(6);
                    m.writeShort(7);
                    m.writeUTF("mot cai gi do22232222");
                    user.session.sendMessage(m);
                } catch (Exception e) {

                }
            } else if (str.contains("nvhn")) {
                try {
                    Message m = new Message((byte) -5);
                    m.writeByte(0);
                    m.writeByte(12);
                    m.writeShort(1);// so luong da lam
                    m.writeShort(15);
                    m.writeShort(0);//id npc
                    m.writeShort(3); // id mob
                    m.writeShort(86); // vi tri map
                    m.writeShort(5);
                    m.writeShort(77);
                    m.writeShort(7);// so luong can lam
                    m.writeUTF("aaaaa");
                    m.writeUTF("abccccc");
                    m.writeUTF("mot hay la cai gi");
                    m.writeBoolean(false);
                    user.session.sendMessage(m);
                } catch (Exception e) {

                }
            } else if (str.contains("msg44")) {
                try {
                    Message m = Message.c((byte) -44);
                    m.writeUTF(Info.name);
                    captcha = CaptchaUtil.generateCaptchaText();
                    m.write(CaptchaUtil.CapchaToByte(captcha));
                    user.session.sendMessage(m);
                } catch (Exception x) {
                    x.printStackTrace();
                }
            } else if (str.contains("tvm")) {
                HanderClickEvent.thuvanmay(this, (byte) 74);
            } else if (str.startsWith("thread")) {
                try {
                    service.alertMessage("Đang có " + Thread.activeCount());
                } catch (Exception x) {
                    x.printStackTrace();
                }
            } else if (str.startsWith("session")) {
                try {
                    service.alertMessage("Đang có " + ServerManager.getUsers().size());
                } catch (Exception x) {
                    x.printStackTrace();
                }
            } else if (str.startsWith("player")) {
                try {
                    service.alertMessage("Đang có " + ServerManager.getNumberOnline() + " player online");
                } catch (Exception x) {
                    x.printStackTrace();
                }
            } else if (str.startsWith("clear")) {
                String[] arr = Utlis.split(str, " ");
                try {
                    Bag.arrItemBag = new Item[Bag.arrItemBag.length];
                    msgSendArrItemBag();
                } catch (Exception x) {
                    x.printStackTrace();
                }
            } else if (str.startsWith("shop")) {
                String[] arr = Utlis.split(str, " ");
                try {
                    byte id = Byte.parseByte(arr[1]);
                    ClickEvent.ShopTrangBi(this, id, Info.idhe);
                } catch (Exception x) {
                    x.printStackTrace();
                }
            } else if (str.startsWith("msg")) {
                String[] arr = Utlis.split(str, " ");
                try {
                    byte id = Byte.parseByte(arr[1]);
                    Message m = new Message((byte) 122);
                    m.writeByte(id);
                    if (id == 73) {
                        m.writeUTF("TEST CASE MOI NE");
                        m.writeShort(99);
                    }
                    if (id == 98) {
                        m.writeBoolean(false);// co hay khong ma bao ve
                        m.writeBoolean(false);// da mo khoa hay chua
                        m.writeInt(0);// thoi gian xoa ma bao ve
                    }
                    user.session.sendMessage(m);
                } catch (Exception x) {
                    x.printStackTrace();
                }
            } else if (str.startsWith("checkmap")) {
                String[] arr = Utlis.split(str, " ");
                try {
                    service.alertMessage("MapID: " + this.Info._mapID);
                } catch (Exception x) {
                    x.printStackTrace();
                }
            } else if (str.startsWith("soncap")) {
                SonCapMyo sonCapMyo = new SonCapMyo(level());
                addWorld(sonCapMyo);
                sonCapMyo.addMember(this);
                sonCapMyo.joinZone(this, 97);
            } else if (str.contains("eff")) {
                short idEff = Short.parseShort(str.split(" ")[1]);
                Message m2 = new Message((byte) 50);
                try {
                    m2.writeInt(Info.idEntity);
                    m2.writeShort(idEff);
                    m2.writeInt(1);
                    m2.writeLong(System.currentTimeMillis() + 5000); // set long show
                    m2.writeInt(15000); // set long down
                    user.session.sendMessage(m2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (str.contains(
                    "xx")) {
                try {
                    short idEff = Short.parseShort(str.split(" ")[1]);
                    Message m = new Message((byte) idEff);
                    m.writeUTF("Đẹp trai số 1");
                    // m.writeByte(idEff);
                    user.session.sendMessage(m);
                } catch (IOException ex) {
                    Logger.getLogger(Char.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else if (str.equals(
                    "phu")) {
                String[] size = {"Trường Konoha Gakuen", "Làng", "Khu rừng chết", "Đại chiến nhẫn giả lần 3", "Đại hội nhẫn giả"};
                StringBuilder str2 = new StringBuilder();
                for (int i = 0; i < size.length; i++) {
                    if (i > 0) {
                        str2.append(";");
                    }
                    str2.append(size[i]);
                }
                Log.debug(str2.toString());
                Message m = new Message((byte) -8);
                try {
                    m.writer.writeUTF("Di chuyển nhanh qua các làng");
                    m.writer.writeUTF(str2.toString());
                    user.session.sendMessage(m);
                } catch (IOException ex) {
                    Logger.getLogger(Char.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (str.equals(
                    "test")) {
                try {
                    Message m = new Message((byte) 122);
                    m.writer.writeByte(100);
                    // m.writeByte(idEff);
                    user.session.sendMessage(m);
                } catch (IOException ex) {
                    Logger.getLogger(Char.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (str.startsWith("banctg")) {
                try {
                    String[] arr = Utlis.split(str, " ");
                    String name = arr[1];
                    Char pl = ServerManager.findCharByName(name);
                    if (pl != null && pl.user != null) {
                        pl.Info.banCTG = true;
                    }
                } catch (Exception ex) {
                }
            } else if (str.startsWith("unban")) {
                try {
                    String[] arr = Utlis.split(str, " ");
                    String name = arr[1];
                    Char pl = ServerManager.findCharByName(name);
                    if (pl != null && pl.user != null) {
                        pl.Info.banCTG = false;
                    }
                } catch (Exception ex) {
                }
            } else if (str.equals("baotri")) {
                new Thread(() -> {
//                    List<Char> chars22 = ServerManager.getChars();
//                    for (Char _char2 : chars22) {
//                        if(_char2!= null && _char2.user!=null && _char2.service!=null)
//                        _char2.user.service.alertMessage("Chuẩn bị bảo trì sau 30s");
//                    }
                    service.alertMessage("Bắt đầu bảo trì sau 30s");
                    Main.maintance();
                    System.exit(1);
                }).start();
            }
        }
    }


    public void itemBagToBox(int index) {
        if (index < 0 || index >= Bag.arrItemBag.length || Bag.arrItemBag[index] == null) {
            return;
        }
        Item item = this.Bag.arrItemBag[index];
        int indexBoxNull = IndexBoxNull();
        if (indexBoxNull == -1) {
            service.alertMessage("Rương đã đầy");
            return;
        }
        item.index = indexBoxNull;
        this.Bag.arrItemBox[indexBoxNull] = item;
        this.Bag.arrItemBag[index] = null;
        user.session.sendMessage(HanderMessage.ItemBagToBox(index, indexBoxNull));
    }

    public void itemBoxToBag(byte index) {
        if (index < 0 || index >= Bag.arrItemBox.length || Bag.arrItemBox[index] == null || !this.checkAddItem(Bag.arrItemBox[index])) {
            return;
        }
        if (isSecurity && !isUnlockSecurity) {
            service.warningMessage("Vui lòng mở khoá bảo mật trước khi sử dụng tính năng này");
            return;
        }
        Item item = Bag.arrItemBox[index];
        this.addItem(item);
        Bag.arrItemBox[index] = null;
        user.session.sendMessage(HanderMessage.ItemBoxToBag(index, item.index));
    }

    public int IndexBoxNull() {
        for (int i = 0; i < this.Bag.arrItemBox.length; i++) {
            if (this.Bag.arrItemBox[i] == null) {
                return i;
            }
        }
        return -1;
    }

    public Item findItemBox(int id) {
        for (int i = 0; i < this.Bag.arrItemBox.length; i++) {
            Item item = this.Bag.arrItemBox[i];
            if (item != null && item.id == id) {
                return item;
            }
        }
        return null;
    }

    public int findIndexItemBox(int id) {
        for (int i = 0; i < this.Bag.arrItemBox.length; i++) {
            Item item = this.Bag.arrItemBox[i];
            if (item != null && item.id == id) {
                return i;
            }
        }
        return -1;
    }

    public void msgUpdateHp() {
        if (user == null || user.session == null) {
            return;
        }
        if (Point.hp > maxHP) {
            Point.hp = maxHP;
        }
        try {
            Writer writer = new Writer();
            writer.writeInt(Point.hp);
            if (Point.hp <= 0) {
                writer.writeShort(Info.cx);
                writer.writeShort(Info.cy);
                writer.writeUTF("");
                if (trade != null) {
                    trade.closeUITrade();
                }
            }
            service.updateHp_Me(writer);

        } catch (Exception ex) {
            Log.error("Loi hoi hp " + ex);
        }
        try {
            Writer writer = new Writer();
            writer.writeInt(Info.idEntity);
            writer.writeInt(Point.hp);
            if (Point.hp <= 0) {
                writer.writeShort(Info.cx);
                writer.writeShort(Info.cy);
                writer.writeUTF("");
                if (trade != null) {
                    trade.closeUITrade();
                }
            }
            if (zone != null) {
                zone.updateHp_Orther(this, writer);
            }

        } catch (Exception ex) {
            Log.error("Loi hoi hp " + ex);
        }

    }

    public void msgUpdateHpFull() {
        if (Point.hp > maxHP) {
            Point.hp = maxHP;
        }
        try {
            Writer writer = new Writer();
            writer.writeInt(maxHP);
            writer.writeInt(Point.hp);
            service.updateHpFull_Me(writer);
        } catch (Exception ex) {
            Log.error("Loi hoi hp " + ex);
        }
        if (zone != null) {
            try {
                Writer writer = new Writer();
                writer.writeInt(Info.idEntity);
                writer.writeInt(maxHP);
                writer.writeInt(Point.hp);
                zone.updateHpFull_Orther(this, writer);

            } catch (Exception ex) {
                Log.error("Loi hoi hp " + ex);
            }
        }
    }

    public void msgUpdateMp() {
        if (Point.mp > maxMP) {
            Point.mp = maxMP;
        }
        try {
            Writer writer = new Writer();
            writer.writeInt(Point.mp);
            service.updateMp_Me(writer);
        } catch (Exception ex) {
            Log.error("Loi hoi mp " + ex);
        }

        try {
            Writer writer = new Writer();
            writer.writeInt(Info.idEntity);
            writer.writeInt(Point.mp);
            if (zone != null) {
                zone.updateMp_Orther(this, writer);
            }

        } catch (Exception ex) {
            Log.error("Loi hoi mp " + ex);
        }

    }

    public void msgUpdateMpFull() {
        if (Point.mp > maxMP) {
            Point.mp = maxMP;
        }
        try {
            Writer writer = new Writer();
            writer.writeInt(maxMP);
            writer.writeInt(Point.mp);
            service.updateMpFull_Me(writer);
        } catch (Exception ex) {
            Log.error("Loi hoi hp " + ex);
        }
        if (zone != null) {
            try {
                Writer writer = new Writer();
                writer.writeInt(Info.idEntity);
                writer.writeInt(maxMP);
                writer.writeInt(Point.mp);
                zone.updateMpFull_Orther(this, writer);

            } catch (Exception ex) {
                Log.error("Loi hoi hp " + ex);
            }
        }
    }

    public void msgUpdateHpMpWhenAttack(boolean cm, String name) {
        try {
            Writer writer = new Writer();
            writer.writeInt(Info.idEntity);
            writer.writeInt(Point.mp);
            writer.writeInt(Point.hp);
            writer.writeBoolean(cm);
            writeDie(writer, name);
            if (zone != null)
                zone.updateHpMpWhenAttack(writer);
        } catch (Exception ex) {
            Log.error("Loi hoi hpmp sau khi danh " + ex);
        }
    }

    public void msgOpenTabConfig(Item item) {
        try {
            Writer writer = new Writer();
            writer.writeShort(item.index);

            String[][] array = new String[][]{
                    {""},
                    {}
            };
            writer.writeUTF("test1;test2,test_1;test3,test_1,test_2;test4,test_1,test_2,test_3");
            this.service.openTabItem(writer);
        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

    public void msgOpenTabSaoCuongHoa(Item item) {
        try {

            ArrayList<String> text = new ArrayList<String>();
            ArrayList<IActionItem> action = new ArrayList<IActionItem>();
            for (int i = 0; i < Bag.arrItemBody.length; i++) {
                if (Bag.arrItemBody[i] != null) {
                    if (i >= 0 && i <= 9) {
                        int index = i;

                        if (Bag.arrItemBody[i].u()) {
                            action.add(new IActionItem() {
                                @Override
                                public void action(Char pl) {
                                    Bag.arrItemBody[index].a(Bag.arrItemBody[index].level + 4);
                                }
                            });
                            text.add("Nâng cấp cho: " + (Bag.arrItemBody[i].getItemTemplate().name) + "(+" + Bag.arrItemBody[i].level + " lên +" + (Bag.arrItemBody[i].level + 4) + ")");
                        }
                    }
                }
            }
            String s = "";
            for (int i = 0; i < text.size(); i++) {
                s += text.get(i);
                if (i < text.size() - 1) {
                    s += ";";
                }
            }
            if (s.length() > 0) {
                item.arrayAction = action.toArray(new IActionItem[action.size()]);
                Writer writer = new Writer();
                writer.writeShort(item.index);
                writer.writeUTF(s);
                this.service.openTabItem(writer);
            }

        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

    public void upLevel(int levelOld, int levelNew) {
        int levelUp = levelNew - levelOld;
        if (levelUp <= 0) {
            return;
        }
        Point.diemTiemNang += (levelUp * 10);
        Point.diemKyNang += (levelUp * 1);
        msgUpdateDataChar();
    }

    public void msgDataBag() {
        try {
            Writer writer = new Writer();
            writer.writeInt(Bag.bac);
            this.writeItemBag(writer, Bag.arrItemBag);
            writer.writeByte(Point.diempt);
            writer.writeByte(Point.maxpt);
            writer.writeInt(Point.expsach);
            this.service.dataBag(writer);
        } catch (Exception ex) {
            Log.error("Loi hoi data bag " + ex);

        }
    }

    /*
     public void aU(Message var1) {
     try {
     Char var2 = Char.gI();
     gI().diemTiemNang = var1.reader.dis.readShort();
     gI().diemKyNang = var1.reader.dis.readShort();
     var2.I = var1.reader.dis.readByte();
     var2.J = var1.reader.dis.readByte();
     var2.K = var1.reader.dis.readByte();
     var2.L = var1.reader.dis.readBoolean();
     gI().pointNAP = var1.reader.dis.readInt();
     if (this.ak != null) {
     this.ak.d();
     }

     } catch (Exception var4) {
     Utlis.println(var4);
     }
     }
     */
    public void msgUpdateDataChar() {
        try {
            Writer writer = new Writer();
            writer.writeShort(Point.diemTiemNang);
            writer.writeShort(Point.diemKyNang);
            writer.writeByte(countFinishDay);// tuan hoan
            writer.writeByte(countLoopBoss);// thu phuc linh thu
            writer.writeByte(3);
            writer.writeBoolean(true);
            writer.writeInt(Bag.pointNAP);
            this.service.updateDataChar(writer);
        } catch (Exception ex) {
            Log.error("Loi hoi datachar " + ex);

        }
    }

    public void msgGetInfo() {
        try {
            updateAllChiSo();
            Writer writer = new Writer();
            writer.writeInt(Info.levelCheTao);
            writer.writeInt(Point.hoatLuc);
            writer.writeByte(Info.sachChienDau);
            writer.writeShort(Point.diemKyNang);
            writer.writeShort(Point.diemTiemNang);
            writer.writeBoolean(false);
            writer.writeByte(0);
            writer.writeShort(Point.arrayTiemNang[0]);
            writer.writeShort(chakra);
            writer.writeShort(Point.arrayTiemNang[2]);
            writer.writeShort(Point.arrayTiemNang[3]);
            writer.writeInt(damage);
            writer.writeInt(attackMonsters);

           /*
             0. Chính xác*/
            writer.writeShort(exactly);/*
             1. Bỏ qua né tránh*/

            writer.writeShort(ignoreMiss);/*
             2. Chí mạng*/

            writer.writeShort(critical >= 3000 ? 3000 : critical);/*
             3. Tấn công khi đánh chí mạng*/

            writer.writeShort(criticalAttack);/*
             4. Tăng tấn công lên hệ Lôi*/

            writer.writeShort(lightningResistance);/*
             5. Tăng tấn công lên hệ Thổ*/

            writer.writeShort(earthResistance);/*
             6. Tăng tấn công lên hệ Thủy*/

            writer.writeShort(waterAttackBoost);/*

             7. Tăng tấn công lên hệ Hỏa*/

            writer.writeShort(fireAttackBoost);/*
             8. Tăng tấn công lên hệ Phong*/

            writer.writeShort(windAttackBoost);/*
             9. Gây suy yếu*/

            writer.writeShort(weaken);/*
             10. Gây trúng độc*/

            writer.writeShort(poison);/*
             11. Gây làm chậm*/

            writer.writeShort(slow);/*
             12. Gây bỏng*/

            writer.writeShort(slow);/*
             13. Gây choáng*/

            writer.writeShort(stun);/*
             14. Bỏ qua kháng tính*/

            writer.writeShort(ignoreResistance);/*
             15. Kháng Lôi*/

            writer.writeShort(lightningResistance);/*
             16. Kháng Thổ*/

            writer.writeShort(earthResistance);/*
             17. Kháng Thủy*/

            writer.writeShort(waterResistance);/*
             18. Kháng Hỏa*/

            writer.writeShort(fireResistance);/*
             19. Kháng Phong*/

            writer.writeShort(windResistance);/*
             20. Giảm sát thương*/

            writer.writeShort(damageReduction);/*
             21. Tốc độ di chuyển*/

            writer.writeShort(movementSpeed);/*
             22. Né tránh*/

            writer.writeShort(miss);/*
             23. Phản đòn*/

            writer.writeShort(counterAttack);/*
             24. Phòng chí mạng*/

            writer.writeShort(criticalDefense);/*
             25. Tương khắc lên hệ */

            writer.writeShort(elementalCounter);/*
             26. Giảm tương khắc của hệ */

            writer.writeShort(elementalCounterReduce);/*
             27. Giảm gây suy yếu*/

            writer.writeShort(reduceWeaken);/*
             28. Giảm gây trúng độc*/

            writer.writeShort(reducePoison);/*
             29. Giảm gây làm chậm*/

            writer.writeShort(reduceSlow);/*
             30. Giảm gây bỏng*/

            writer.writeShort(reduceBurn);/*
             31. Giảm gây choáng*/

            writer.writeShort(reduceStun);/*
             32. Giảm trừ chí mạng*/

            writer.writeShort(reduceCriticalDamage);/*
             */

            this.service.getInfo(writer);
        } catch (Exception ex) {
            Log.error("Loi hoi getinfo " + ex);

        }
    }

    public void nangCapSkill(short id) {
        supportSkill.clear();
        Skill skill = this.getSkillWithIdTemplate(id);
        if (skill == null || skill.level >= skill.getSkillTemplate().levelMax || DataCenter.gI().getSkillWithIdAndLevel(id, skill.level + 1) != null && DataCenter.gI().getSkillWithIdAndLevel(id, skill.level + 1).levelNeed > this.level() || Point.diemKyNang <= 0) {
            return;
        }
        Point.diemKyNang--;
        for (int i = 0; i < Skill.arraySkill.length; i++) {
            if (Skill.arraySkill[i] == skill) {
                Skill.arraySkill[i] = DataCenter.gI().getSkillWithIdAndLevel(id, skill.level + 1);
                if (Skill.arraySkill[i].idTemplate == Skill.skillFight.idTemplate) {
                    Skill.skillFight = Skill.arraySkill[i];
                }
                break;
            }
        }
        msgGetInfo();
        msgUpdateDataChar();
        msgUpdateSkill();
        if (taskId == TaskName.NV_NHAN_GIA_HOC_VIEN && taskMain != null && taskMain.index == 11) {
            taskNext();
        }
    }

    public void msgUpdateSkill() {
        try {
            Writer writer = new Writer();
            this.writeSkill(writer);
            this.service.updateSkill(writer);
        } catch (Exception ex) {
            Log.error("Loi update skill " + ex);

        }
    }

    public void focusSkill(short id) {
        Skill skill = this.getSkillWithIdTemplate(id);
        if (skill == null || skill.level <= 0 || skill.levelNeed > this.level()) {
            return;
        }
        Skill.skillFight = skill;
        msgUpdateSkill();
    }

    public void updateTiemNang() {
//        Point.hpFull += (100 + (Point.arrayTiemNang[3] * 10));
//        Point.mpFull += (100 + (Point.arrayTiemNang[2] * 10));
//        if (Point.HpMpChara != Point.arrayTiemNang[1] / 3) {
//            Point.hpFull += Point.arrayTiemNang[1] / 3;
//            Point.mpFull += Point.arrayTiemNang[1] / 3;
//        }

        //update();
//        this.hpFull = (100 + (this.arrayTiemNang[3] * 10));
//        this.mpFull = (100 + (this.arrayTiemNang[2] * 10));
        msgUpdateHpFull();
        msgUpdateMpFull();
    }

    public void reSpawn() {
        if (!canRevive) {
            service.serverMessage("Bạn không thể hồi sinh do dính bùa uế thổ , vui lòng nhập captchat để giải hiệu ứng");
            service.resendCaptcha();
            return;
        }
        InfoGame.isDie = false;
        Point.hp = maxHP;
        Point.mp = maxMP;
        try {
            Writer writer = new Writer();
            writer.writeInt(Info.idEntity);
            writer.writeInt(Point.hp);
            writer.writeInt(Point.mp);
            if (zone != null)
                zone.reSpawn(writer);
            //this.user.session.serivce.setXYChar();
        } catch (Exception ex) {
            Log.error("Loi hoi sinh " + ex);
        }
        try {
            Writer writer = new Writer();
            writer.writeInt(Info.idEntity);
            Info.writeXY(writer);
            if (zone != null)
                zone.setXYChar(writer);
            //this.user.session.serivce.setXYChar();
        } catch (Exception ex) {
            Log.error("Loi hoi hoi sinh " + ex);
        }

    }

    public void reSpawnHS() {
        if (!canRevive) {
            service.serverMessage("Bạn không thể hồi sinh do dính bùa uế thổ , vui lòng nhập captchat để giải hiệu ứng");
            service.resendCaptcha();
            return;
        }
        if (zone.isLoiDai()) {
            service.serverMessage("Không thể hồi sinh tại khu vực này");
            return;
        }
        if (Bag.vangKhoa > 0) {
            addVangKhoa(-1);
        } else {
            this.user.session.sendMessage(HanderMessage.SendThongBao("Không đủ vàng khoá", HanderMessage.RED_MID));
        }
        InfoGame.isDie = false;
        Point.hp = maxHP;
        Point.mp = maxMP;
        try {
            Writer writer = new Writer();
            writer.writeInt(Info.idEntity);
            writer.writeInt(Point.hp);
            writer.writeInt(Point.mp);
            if (zone != null)
                zone.reSpawn(writer);
            //this.user.session.serivce.setXYChar();
        } catch (Exception ex) {
            Log.error("Loi hoi hoi sinh " + ex);
        }
//        try {
//            Writer writer = new Writer();
//            writer.writeInt(Info.idEntity);
//            Info.writeXY(writer);
//            InfoGame.ZoneGame.setXYChar(writer);
//            //this.user.session.serivce.setXYChar();
//        } catch (Exception ex) {
//
//        }
    }

    public void writeDie(Writer writer, String string) throws IOException {
        if (Point.hp <= 0) {
//            try {
//                int cy = zone.getXYBlockMap(cx, this.cy).cy;
//                this.setXY(cx, cy);
//            } catch (Exception ex) {
//
//            }
            if (trade != null) {
                trade.closeUITrade();
            }
            writer.writeShort(Info.cx);
            writer.writeShort(Info.cy);
            writer.writeUTF(string);
        }
    }

    public void addEffect(Effect effect) {
        lockEff.lock();
        try {
            for (int i = listEffect.size() - 1; i >= 0; i--) {
                if (listEffect.get(i).getEffectTemplate().type == effect.getEffectTemplate().type) {
                    if (listEffect.get(i).getEffectTemplate().id == effect.getEffectTemplate().id) {
                        if (listEffect.get(i).getEffectTemplate().id == 101 || listEffect.get(i).getEffectTemplate().id == 100) {
                            return;
                        }
                        if (listEffect.get(i).getEffectTemplate().id == 8
                                || listEffect.get(i).getEffectTemplate().id == 9
                                || listEffect.get(i).getEffectTemplate().id == 11
                                || listEffect.get(i).getEffectTemplate().id == 12
                                || listEffect.get(i).getEffectTemplate().id == 38) {
                            return;
                        }
                        long l = 0;
                        if (listEffect.get(i).value != effect.value) {
                            l = effect.maintain;
                        } else
                            l = ((long) effect.maintain) + ((long) listEffect.get(i).getMaintain());
                        if (l > Integer.MAX_VALUE) {
                            l = Integer.MAX_VALUE;
                        }
                        effect.maintain = (int) l;
                    }
                    listEffect.remove(i);
                    break;
                }
            }
            if (effect.getEffectTemplate().type == 6 || effect.getEffectTemplate().type == 7) {
                if (effect.maintain >= 3000) {
                    effect.maintain = 3000;
                }
            }
            HanderEff.getPointEff(this, effect);
            this.listEffect.add(effect);
            msgAddEffect(effect);
        } catch (Exception ex) {
            Log.error("Loi add eff " + ex);
        } finally {
            lockEff.unlock();
        }
    }


    public void msgAddEffect(Effect effect) {
        try {
            Writer writer = new Writer();
            writer.writeInt(Info.idEntity);
            effect.write(writer);
            if (zone != null)
                zone.addEffect(writer);
        } catch (Exception ex) {
            Log.error("Loi send eff " + ex);
        }

    }

    public void msgRemoveEffect(Effect effect) {
        try {
            Writer writer = new Writer();
            writer.writeInt(Info.idEntity);
            writer.writeShort(effect.id);
            if (zone != null)
                zone.removeEffect(writer);
        } catch (Exception ex) {
            Log.error("Loi remove eff " + ex);
        }

    }


    public void itemBodyToBag(byte index) {
        if (index < 0 || index >= Bag.arrItemBody.length || Bag.arrItemBody[index] == null || !this.checkAddItem(Bag.arrItemBody[index])) {
            return;
        }
        if (isSecurity && !isUnlockSecurity) {
            service.warningMessage("Vui lòng mở khoá bảo mật trước khi sử dụng tính năng này");
            return;
        }
        Item item = Bag.arrItemBody[index];
        this.addItem(item);
        Bag.arrItemBody[index] = null;
        msgItemBodyToBag(index, item.index);
        this.msgGetInfo();
        this.sortItem((byte) 0);
        this.updateTiemNang();
    }

    public void msgItemBodyToBag(byte indexBody, int indexBag) {
        try {
            Writer writer = new Writer();
            writer.writeByte(indexBody);
            writer.writeShort(indexBag);
            this.service.itemBodyToBag_Me(writer);
        } catch (Exception ex) {

        }
        msgUpdateItemBody_Orther();

    }

    public void msgUpdateItemBody_Me() {
        try {
            Writer writer = new Writer();
            this.writeItemBody(writer, Bag.arrItemBody);
            this.writeItemBody(writer, Bag.arrItemBody2);
            this.service.updateItemBody_Me(writer);
        } catch (Exception ex) {

        }
    }

    public void msgUpdateItemBody_Orther() {
        try {
            Writer writer = new Writer();
            writer.writeInt(Info.idEntity);
            this.writeItemBody(writer, Bag.arrItemBody);
            if (zone != null)
                zone.updateItemBody_Orther(this, writer);
        } catch (Exception ex) {
            Log.error("loi update body " + ex);
        }
    }

    private int getPhatHuyLucDanhCoBan() {
        int c = 0;
        int[] array = new int[]{34, 47, 122};
        for (int i = 0; i < Bag.arrItemBody.length; i++) {
            if (Bag.arrItemBody[i] != null) {
                c += Bag.arrItemBody[i].getChiSo(this, array);
            }
        }

        //
        return c;
    }

    public int getDameMob(Mob mob, int dame) {
        if (mob.he == 1) {
            dame += lightningAttackBoost;
        }
        if (mob.he == 2) {
            dame += earthAttackBoost;
        }
        if (mob.he == 3) {
            dame += waterAttackBoost;
        }
        if (mob.he == 4) {
            dame += fireAttackBoost;
        }
        if (mob.he == 5) {
            dame += windAttackBoost;
        }
        return dame;
    }

    public void msgUpdateItemBody() {
        updateAllChiSo();
        msgUpdateItemBody_Me();
        msgUpdateItemBody_Orther();
        msgUpdateHpFull();
        msgUpdateMpFull();
    }

    public void useItemBodyDuPhong(short index) {
        if (index < 0 || index >= Bag.arrItemBag.length || Bag.arrItemBag[index] == null || !Bag.arrItemBag[index].isTypeTrangBi()) {
            return;
        }
        if (isSecurity && !isUnlockSecurity) {
            service.warningMessage("Vui lòng mở khoá bảo mật trước khi sử dụng tính năng này");
            return;
        }
        Item item = Bag.arrItemBag[index];
        item.isLock = true;
        removeItem(item);
        msgRemoveItemBag(item);
        Item body = Bag.arrItemBody2[item.getItemTemplate().type];
        if (body != null) {
            addItem(body);
            msgGetInfo();
            msgSendArrItemBag();
        }
        item.index = item.getItemTemplate().type;
        Bag.arrItemBody2[item.index] = item;
        msgSendArrItemBag();
        msgItemBodyDuPhong(index);
        msgUpdateItemBody();
    }

    public void msgItemBodyDuPhong(int index) {
        try {
            Writer writer = new Writer();
            writer.writeShort(index);
            service.itemBodyDuPhong(writer);
        } catch (Exception ex) {
            Log.error("Loi item body2 " + ex);
        }
    }

    public void itemBodyDuPhongToBag(byte index) {
        if (index < 0 || index >= Bag.arrItemBody2.length || Bag.arrItemBody2[index] == null || !this.checkAddItem(Bag.arrItemBody2[index])) {
            return;
        }
        if (isSecurity && !isUnlockSecurity) {
            service.warningMessage("Vui lòng mở khoá bảo mật trước khi sử dụng tính năng này");
            return;
        }
        Item item = Bag.arrItemBody2[index];
        Bag.arrItemBody2[index] = null;
        addItem(item);
        msgItemBodyDuPhongToBag(index, item.index);
        msgAddItemBag(item);
        msgSendArrItemBag();
    }

    public void msgItemBodyDuPhongToBag(int indexBody, int indexBag) {
        try {
            Writer writer = new Writer();
            writer.writeByte(indexBody);
            writer.writeShort(indexBag);
            service.itemBodyDuPhongToBag(writer);
        } catch (Exception ex) {
            Log.error("Loi msg item body2 " + ex);
        }
    }

    public void ghepDa(Message msg) {
        try {
            Vector<Item> crystals = new Vector<>();
            boolean[] list = new boolean[Bag.arrItemBag.length];
            boolean isBac = msg.readBoolean();
            byte size = msg.readByte();
            while (msg.reader.Avali() > 0) {
                int indexItem = msg.readShort();
                if (indexItem < 0 || indexItem >= Bag.arrItemBag.length) {
                    continue;
                }
                if (!list[indexItem]) {
                    list[indexItem] = true;
                    if (Bag.arrItemBag[indexItem] != null && Bag.arrItemBag[indexItem].id <= 11) {
                        if (Bag.arrItemBag[indexItem].id == 11) {
                            service.alertMessage("Da dat toi gioi han");
                            return;
                        }
                        crystals.add(Bag.arrItemBag[indexItem]);
                    }
                }
            }
            int sizeReal = crystals.size();
            if (sizeReal < 2) {
                service.warningMessage("Cần 2 viên đá mới có thể ghép");
                return;
            }
            int percent = 0;
            int i = 0;
            for (Item item : crystals) {
                percent += DataCenter.gI().pointGhepDa[item.id];
            }
            if (percent > 0) {
                for (i = DataCenter.gI().pointGhepDa.length - 1; i >= 0; i--) {
                    if (percent > DataCenter.gI().pointGhepDa[i]) {
                        break;
                    }
                }
            }
            percent = (int) (percent * 100 / DataCenter.gI().pointGhepDa[i + 1]);
            if (i >= DataCenter.gI().pointGhepDa.length - 1) {
                i = DataCenter.gI().pointGhepDa.length - 2;
            }
            if (percent < 40 || isBac && Bag.bacKhoa < DataCenter.gI().bacKhoaGhepDa[i + 1] || !isBac && Bag.bac < DataCenter.gI().bacKhoaGhepDa[i + 1]) {
                return;
            }
            boolean isDaKhoa = false;
            boolean ghep = Utlis.randomBoolean(100, percent);
            if (!isDaKhoa) {
                isDaKhoa = !isBac;
            }
            if (isBac) {
                Bag.bacKhoa -= DataCenter.gI().bacKhoaGhepDa[i + 1];
            } else {
                Bag.bac -= DataCenter.gI().bacKhoaGhepDa[i + 1];
            }
            for (Item item : crystals) {
                removeItem(item, true);
            }
            Item daGhep = null;
            if (ghep) {
                daGhep = (new Item(i + 1, true));
            } else {
                daGhep = (new Item(i, true));
            }
            addItem(daGhep);
            msgGhepDa(crystals, daGhep);
        } catch (Exception ex) {
            Log.error("Loi ghep da " + ex);
        }
    }

    public void msgGhepDa(Vector<Item> da, Item daGhep) {
        try {
            Writer writer = new Writer();
            writer.writeInt(Bag.bac);
            writer.writeInt(Bag.bacKhoa);
            writer.writeByte(da.size());
            for (int i = 0; i < da.size(); i++) {
                writer.writeShort(((Item) da.get(i)).index);
            }
            daGhep.write(writer);
            service.ghepDa(writer);
        } catch (Exception ex) {
            Log.error("Loi msg ghep da " + ex);
        }
    }

    public Item[] checkBag(int var1) {
        switch (var1) {
            case 0:
                return Bag.arrItemBag;
            case 1:
                return Bag.arrItemBox;
            case 2:
                return Bag.arrItemBody;
            case 3:
                return Bag.arrItemBody2;
            case 4:
                return Bag.arrItemExtend;
            default:
                return null;
        }
    }

    public void cuongHoa(Message msg) {
        try {
            if (isSecurity && !isUnlockSecurity) {
                service.warningMessage("Vui lòng mở khoá bảo mật trước khi sử dụng tính năng này");
                return;
            }
            byte type_item = msg.readByte();
            short index_item = msg.readShort();
            short index_bua = msg.readShort();
            byte size = msg.readByte();
            Item itemch = checkBag(type_item)[index_item];
            if (itemch == null) {
                service.alertMessage("Item khong hop le");
                return;
            }
            Vector<Item> crystals = new Vector<>();
            int numberCrystal = 0;
            boolean[] list = new boolean[Bag.arrItemBag.length];
            while (msg.reader.Avali() > 0) {
                int indexItem = msg.readShort();
                if (indexItem < 0 || indexItem >= Bag.arrItemBag.length) {
                    continue;
                }
                if (!list[indexItem]) {
                    list[indexItem] = true;
                    if (Bag.arrItemBag[indexItem] != null && Bag.arrItemBag[indexItem].getItemTemplate().type == 21) {
                        crystals.add(Bag.arrItemBag[indexItem]);
                        numberCrystal++;
                    }
                }
            }
            int sizeReal = crystals.size();
            if (sizeReal > 16 || sizeReal < size) {
                service.alertMessage("Số lượng đá không chính xác");
                return;
            }
            if (numberCrystal == 0) {
                service.alertMessage("Hãy chọn đá nâng cấp");
                return;
            }
            if (!itemch.isItemTrangBi() || !itemch.u()) {
                service.alertMessage("Item khong dung");
                return;
            }
            long temp = 0;
            int percent = 0;
            int coin = 0;
            for (int i = 0; i < crystals.size(); i++) {
                Item itm = crystals.get(i);
                if (itm != null && (int) itm.getItemTemplate().type == 21) {
                    temp += DataCenter.gI().pointGhepDa[itm.id];
                }
            }
            if (itemch.isVuKhi() && itemch.level + 1 <= 19) {
                percent = (int) (temp * 100 / DataCenter.gI().pointUpgradeVuKhi[itemch.level + 1]);
                coin = DataCenter.gI().bacKhoaUpgradeVuKhi[itemch.level + 1];
            }
            if (itemch.isTrangBi() && itemch.level + 1 <= 19) {
                percent = (int) (temp * 100 / DataCenter.gI().pointUpgradeTrangBi[itemch.level + 1]);
                coin = DataCenter.gI().bacKhoaUpgradeTrangBi[itemch.level + 1];
            }
            if (itemch.isPhuKien() && itemch.level + 1 <= 19) {
                percent = (int) (temp * 100 / DataCenter.gI().pointUpgradePhuKien[itemch.level + 1]);
                coin = DataCenter.gI().bacKhoaUpgradePhuKien[itemch.level + 1];
            }
            Item bua = index_bua == -1 ? null : Bag.arrItemBag[index_bua];
            if (bua != null && bua.id != 162) {
                bua = null;
            }
            if (Bag.bacKhoa < coin) {
                int numBac = 0;
                if (Bag.bac + Bag.bacKhoa >= Integer.MAX_VALUE) {
                    numBac = Integer.MAX_VALUE;
                }
                if (numBac < coin) {
                    return;
                }
            }
            if (bua != null) {
                percent += 3;
            }

            if (Bag.bacKhoa >= coin) {
                Bag.bacKhoa -= coin;
            } else {
                Bag.bacKhoa = 0;
                Bag.bac = (Bag.bac - (coin - Bag.bacKhoa));
            }
            for (Item da : crystals) {
                removeItem(da, true);
            }
            if (bua != null) {
                removeItem(bua);
            }
//            if (percent < 100) {
//                percent = percent / 3;
//            }
            boolean CuongHoa = Utlis.randomBoolean(100, percent);
            if (CuongHoa) {
                itemch.a(itemch.level + 1);
                if (itemch.level > 16) {
                    Utlis.logText("log/cuonghoa.txt", Info.name + " đã đập đồ " + itemch.getItemTemplate().levelNeed + " lên cấp " + itemch.level);
                    Main.HeThongCTG("Bất ngờ chưa , nhẫn giả " + Info.name + " vừa đập thành công " + itemch.getItemTemplate().name + " cấp " + itemch.level, 2);
                }
                itemch.isLock = true;
                if (taskId == TaskName.NV_NHIEM_VU_DAU_TIEN && taskMain != null && taskMain.index == 10 && itemch.isVuKhi()) {
                    taskNext();
                }
            }
            msgCuongHoa(CuongHoa, false, crystals, itemch, bua, type_item);
            msgUpdateItemBody_Orther();
        } catch (Exception ex) {
            Log.error("Loi cuong hoa " + ex);
        }
    }

    public void msgCuongHoa(boolean CuongHoa, boolean b, Vector<Item> da, Item itemCuongHoa, Item bua, int type_item) {
        try {
            Writer writer = new Writer();
            writer.writeBoolean(CuongHoa);
            writer.writeBoolean(b);
            writer.writeInt(Bag.bac);
            writer.writeInt(Bag.bacKhoa);
            writer.writeByte(da.size());
            for (int i = 0; i < da.size(); i++) {
                writer.writeShort(((Item) da.get(i)).index);
            }
            if (bua == null) {
                writer.writeShort(-1);
            } else {
                writer.writeShort(bua.index);
            }
            itemCuongHoa.write(writer);
            writer.writeByte(type_item);
            service.cuongHoa(writer);
        } catch (Exception ex) {
            Log.error("Loi msg cuong hoa " + ex);
        }
    }

    public void msgUpdateStatusChar() {
        try {
            Writer writer = new Writer();
            writer.writeInt(Info.idEntity);
            writer.writeByte(Info.lvPk);
            writer.writeInt(GetTaiPhu());
            writer.writeShort(movementSpeed);
            writer.writeByte(InfoGame.statusGD);
            if (zone != null)
                zone.updateStatusChar(writer);
        } catch (Exception ex) {

        }
    }

    public void useItemWithTab(int indexItem, int index1, int index2) {
        if (indexItem < 0 || indexItem >= Bag.arrItemBag.length || Bag.arrItemBag[indexItem] == null) {
            return;
        }
        if (trade != null) {
            trade.closeUITrade();
            return;
        }
        if (isSecurity && !isUnlockSecurity) {
            service.warningMessage("Vui lòng mở khoá bảo mật trước khi sử dụng tính năng này");
            return;
        }
        switch (Bag.arrItemBag[indexItem].id) {
            case 915:
                switch (index1) {
                    case 0:
                        if (tanto == null || tanto.isEmpty()) {
                            return;
                        }
                        if (index2 < 0 || index2 >= tanto.size()) {
                            return;
                        }
                        Item giahan = FindItemBag(915);
                        if (giahan == null) {
                            return;
                        }
                        Item item = tanto.get(index2);
                        if (item == null) {
                            return;
                        }
                        if (item.renew >= 4) {
                            service.serverMessage("Item đã hết lượt gia hạn");
                            return;
                        }
                        if (item.expiry > -1) {
                            item.expiry += TimeUnit.DAYS.toMillis(3);
                            item.renew++;
                        }
                        removeItem(giahan);
                        msgUseItemBag(giahan);
                        service.resetScreen();
                        service.serverMessage("Bạn đã gia hạn item " + item.getItemTemplate().name + " thành công");
                        break;
                    case 1:
                        if (aoChoang == null || aoChoang.isEmpty()) {
                            return;
                        }
                        if (index2 < 0 || index2 >= aoChoang.size()) {
                            return;
                        }
                        giahan = FindItemBag(915);
                        if (giahan == null) {
                            return;
                        }
                        item = aoChoang.get(index2);
                        if (item == null) {
                            return;
                        }
                        if (item.renew >= 4) {
                            service.serverMessage("Item đã hết lượt gia hạn");
                            return;
                        }
                        if (item.expiry > -1) {
                            item.expiry += TimeUnit.DAYS.toMillis(3);
                            item.renew++;
                        }
                        removeItem(giahan);
                        msgUseItemBag(giahan);
                        service.resetScreen();
                        service.serverMessage("Bạn đã gia hạn item " + item.getItemTemplate().name + " thành công");
                        break;
                    case 2:
                        if (thoiTrang == null || thoiTrang.isEmpty()) {
                            return;
                        }
                        if (index2 < 0 || index2 >= thoiTrang.size()) {
                            return;
                        }
                        giahan = FindItemBag(915);
                        if (giahan == null) {
                            return;
                        }
                        item = thoiTrang.get(index2);
                        if (item == null) {
                            return;
                        }
                        if (item.renew >= 4) {
                            service.serverMessage("Item đã hết lượt gia hạn");
                            return;
                        }
                        if (item.expiry > -1) {
                            item.expiry += TimeUnit.DAYS.toMillis(3);
                            item.renew++;
                        }
                        removeItem(giahan);
                        msgUseItemBag(giahan);
                        service.resetScreen();
                        service.serverMessage("Bạn đã gia hạn item " + item.getItemTemplate().name + " thành công");
                        break;
                }
                break;
            case 914:
                break;
            case 435:
                switch (index1) {
                    case 0:
                        if (Bag.vang < 1500) {
                            user.session.sendMessage(HanderMessage.SendThongBao("Không đủ vàng", HanderMessage.RED_MID));
                            return;
                        }
                        if (!removeItems(435, 10)) {
                            user.session.sendMessage(HanderMessage.SendThongBao("Không đủ sách để đổi", HanderMessage.RED_MID));
                            return;
                        }
                        addVang(-1500);
                        Item sachcc = new Item(719);
                        Item sach435 = FindItemBag(435);
                        sachcc.isLock = true;
                        addItem(sachcc);
                        msgAddItemBag(sachcc);
                        msgUseItemBag(sach435);

                        break;
                    case 1:
                        if (Bag.vang < 3000) {
                            user.session.sendMessage(HanderMessage.SendThongBao("Không đủ vàng", HanderMessage.RED_MID));
                            return;
                        }
                        if (!removeItems(435, 60)) {
                            user.session.sendMessage(HanderMessage.SendThongBao("Không đủ sách để đổi", HanderMessage.RED_MID));
                            return;
                        }
                        addVang(-3000);
                        Item sachsc = new Item(778);
                        Item sach435z = FindItemBag(435);
                        sachsc.isLock = true;
                        addItem(sachsc);
                        msgAddItemBag(sachsc);
                        msgUseItemBag(sach435z);
                        break;
                }
                break;
        }
    }

    private void msgUpdateSachChienDau() {
        try {
            Writer writer = new Writer();
            writer.writeByte(Info.sachChienDau);
            this.service.updateSachChienDau(writer);
        } catch (Exception ex) {

        }
    }

    public void tachCuongHoa(Message msg) {
        try {
            if (isSecurity && !isUnlockSecurity) {
                service.warningMessage("Vui lòng mở khoá bảo mật trước khi sử dụng tính năng này");
                return;
            }
            byte type_item = msg.readByte();
            short index_item = msg.readShort();
            if (index_item < 0 || index_item >= this.Bag.arrItemBag.length) {
                return;
            }
            Item item = checkBag(type_item)[index_item];
            if (item == null) {
                return;
            }
            if (item != null && (item.isVuKhi() || item.isTrangBi() || item.isPhuKien())) {
                if (item.level > 0) {
                    int num = 0;
                    int coin = 0;
                    if (item.isVuKhi()) {
                        for (byte i = item.level; i > 0; i--) {
                            num += DataCenter.gI().pointUpgradeVuKhi[i];
                            coin += DataCenter.gI().bacKhoaUpgradeVuKhi[i];
                        }
                    } else if (item.isTrangBi()) {
                        for (byte i = item.level; i > 0; i--) {
                            num += DataCenter.gI().pointUpgradeTrangBi[i];
                            coin += DataCenter.gI().bacKhoaUpgradeTrangBi[i];
                        }
                    } else {
                        for (byte i = item.level; i > 0; i--) {
                            num += DataCenter.gI().pointUpgradePhuKien[i];
                            coin += DataCenter.gI().bacKhoaUpgradePhuKien[i];
                        }
                    }
                    num /= 3;
                    int num2 = 0;
                    List<Item> list = new ArrayList<>();
                    for (int n = DataCenter.gI().pointGhepDa.length - 1; n >= 0; n--) {
                        if (num >= DataCenter.gI().pointGhepDa[n]) {
                            Item item2 = new Item(n);
                            item2.isLock = true;
                            list.add(item2);
                            num -= DataCenter.gI().pointGhepDa[n];
                            n++;
                            num2++;
                        }
                    }
                    if (num2 > getCountNullItemBag()) {
                        user.session.sendMessage(HanderMessage.SendThongBao("Không đủ chỗ trống", HanderMessage.RED_MID));
                        return;
                    }
                    int i2 = 0;
                    int size = list.size();
                    for (int i = 0; i < this.Bag.arrItemBag.length; i++) {
                        if (i2 < size) {
                            addItem(list.get(i2));
                            i2++;
                        }
                    }
                    item.a(0);
                    msgTachCuongHoa(item, type_item);
                    addBacKhoa(coin / 3);
                    msgSendArrItemBag();
                    msgUpdateItemBody_Orther();
                }
            }
        } catch (Exception ex) {
            Log.error("erro tach cuong hoa" + ex);
        }
    }

    private void msgTachCuongHoa(Item item, int type_item) {
        try {
            Writer writer = new Writer();
            item.write(writer);
            writer.writeByte(type_item);
            this.service.tachCuongHoa(writer);
        } catch (Exception ex) {
        }

    }

    public void NhanQuaThu(TemplateThu thu) {
        thu.isSucess = true;
        if (thu.Bac > 0) {
            addBac(thu.Bac);
            thu.Bac = 0;
        }
        if (thu.BacKhoa > 0) {
            addBacKhoa(thu.BacKhoa);
            thu.BacKhoa = 0;
        }
        if (thu.Vang > 0) {
            addVang(thu.Vang);
            thu.Vang = 0;
        }
        if (thu.VangKhoa > 0) {
            addVangKhoa(thu.VangKhoa);
            thu.VangKhoa = 0;
        }
        if (thu.Exp > 0) {
            addExp(thu.Exp);
            thu.Exp = 0;
        }
//        if(thu.Item!=null)
//        {
//            if(this.getCountNullItemBag() !=0)
//            {
//                addItem(thu.Item);
//                thu.Item =null;
//            }else{
//                user.session.sendMessage(HanderMessage.SendThongBao(this, "Hành trang không đủ ô trống", HanderMessage.WHITE));
//            }
//        }

    }

    public synchronized void addBacKhoa(long bacKhoa) {
        long l = ((long) Bag.bacKhoa) + ((long) bacKhoa);
        if (l > Integer.MAX_VALUE) {
            l = Integer.MAX_VALUE;
        }
        Bag.bacKhoa = (int) l;
        try {
            Writer writer = new Writer();
            writer.writeInt(Bag.bacKhoa);
            writer.writeBoolean(true);
            this.service.updateBacKhoa(writer);
        } catch (Exception ex) {
        }
    }

    public synchronized void addBac(long bac) {
        if (trade != null) {
            trade.closeUITrade();
        }
        long l = ((long) Bag.bac) + ((long) bac);
        if (l > Integer.MAX_VALUE) {
            l = Integer.MAX_VALUE;
        }
        Bag.bac = (int) l;
        try {
            Writer writer = new Writer();
            writer.writeInt(Bag.bac);
            writer.writeBoolean(true);
            this.service.updateBac(writer);
        } catch (Exception ex) {
        }
    }

    public synchronized void addVang(long vang) {
        long l = ((long) Bag.vang) + ((long) vang);
        if (l > Integer.MAX_VALUE) {
            l = Integer.MAX_VALUE;
        }
        if (vang < 0) {
            phucLoi.tieuNgay += vang * -1;
            phucLoi.tieuTuan += vang * -1;
        }
        Bag.vang = (int) l;
        try {
            Writer writer = new Writer();
            writer.writeInt(Bag.vang);
            writer.writeBoolean(true);
            this.service.updateVang(writer);
        } catch (Exception ex) {
        }
    }


    public synchronized void addVangKhoa(long vangkhoa) {
        long l = ((long) Bag.vangKhoa) + ((long) vangkhoa);
        if (l > Integer.MAX_VALUE) {
            l = Integer.MAX_VALUE;
        }
        Bag.vangKhoa = (int) l;
        try {
            Writer writer = new Writer();
            writer.writeInt(Bag.vangKhoa);
            writer.writeBoolean(true);
            this.service.updateVangKhoa(writer);
        } catch (Exception ex) {
//            ex.printStackTrace();
        }
    }

    public void removeItemBug() {
        try {
            Item ct = Bag.arrItemBody[14];
            if (ct != null) {
                Bag.arrItemBody[14] = null;
            }
            Item ct2 = Bag.arrItemBody2[14];
            if (ct2 != null) {
                Bag.arrItemBody2[14] = null;
            }
            for (int i = Bag.arrItemBag.length - 1; i >= 0; i--) {
                Item item = Bag.arrItemBag[i];
                if (item != null && item.getItemTemplate().type == 14) {
                    removeItem(item, true);
                }
            }
            for (int i = Bag.arrItemBox.length - 1; i >= 0; i--) {
                Item item = Bag.arrItemBox[i];
                if (item != null && item.getItemTemplate().type == 14) {
                    Bag.arrItemBox[i] = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeItemBugLevl() {
        try {
            for (int i = Bag.arrItemBody.length - 1; i >= 0; i--) {
                Item item = Bag.arrItemBody[i];
                if (item != null && !item.u() && item.getItemTemplate().type != 14) {
                    Bag.arrItemBody[i] = null;
                }
            }
            for (int i = Bag.arrItemBody2.length - 1; i >= 0; i--) {
                Item item = Bag.arrItemBody2[i];
                if (item != null && !item.u() && item.getItemTemplate().type != 14) {
                    Bag.arrItemBody2[i] = null;
                }
            }
            for (int i = Bag.arrItemBag.length - 1; i >= 0; i--) {
                Item item = Bag.arrItemBag[i];
                if (item != null && item.isItemTrangBi() && !item.u() && item.getItemTemplate().type != 14) {
                    removeItem(item);
                    msgRemoveItemBag(item);
                }
            }
            for (int i = Bag.arrItemBox.length - 1; i >= 0; i--) {
                Item item = Bag.arrItemBox[i];
                if (item != null && item.isItemTrangBi() && !item.u() && item.getItemTemplate().type != 14) {
                    Bag.arrItemBox[i] = null;
                }
            }
            msgUpdateItemBody_Orther();
            msgSendArrItemBag();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void khamNgoc(Message msg) {
        try {
            if (isSecurity && !isUnlockSecurity) {
                service.warningMessage("Vui lòng mở khoá bảo mật trước khi sử dụng tính năng này");
                return;
            }
            byte typeTb = msg.readByte();
            short numTb = msg.readShort();
            byte sizeNgoc = msg.readByte();
            Item it = checkBag(typeTb)[numTb];
            if (it == null) {
                return;
            }
            Vector<Item> items = new Vector<>();
            boolean[] list = new boolean[Bag.arrItemBag.length];
            while (msg.reader.Avali() > 0) {
                int index = msg.readShort();
                if (index < 0 || index >= Bag.arrItemBag.length) {
                    continue;
                }
                if (!list[index]) {
                    list[index] = true;
                    Item item = Bag.arrItemBag[index];
                    if (item != null && item.getItemTemplate().type == 32)
                        items.add(item);
                }
            }
            if (items.isEmpty()) {
                return;
            }
            if (items.size() < sizeNgoc) {
                return;
            }
            int countKham = it.checkCountKham() + Info.countKham;
            int countDaKham = it.countDaKham();
            if (countDaKham >= countKham) {
                service.alertMessage("Đã tới giới hạn khảm");
                return;
            }
            boolean var1 = true;
            int var2 = -1;
            int y = 0;
            if (it != null) {
                for (int var3 = 0; var3 < items.size(); ++var3) {
                    if (items.get(var3) != null) {
                        if (var2 == -1) {
                            var2 = items.get(var3).id;
                        } else if (var2 != items.get(var3).id) {
                            var1 = false;
                            break;
                        }

                        y += items.get(var3).getAmount();
                    }
                }

                Vector var7 = new Vector();
                ItemOption var4;
                if ((var4 = it.a(var7, var2)) != null) {
                    if (var4.a(it)) {
                        var1 = false;
                    }
                } else {
                    var7 = new Vector();
                    ItemOption[] var9 = it.L();
                    if (var9 == null) {
                        return;
                    }
                    for (int var5 = 0; var5 < var9.length; ++var5) {
                        var7.add(var9[var5]);
                    }

                    if (it.V()) {
                        var7.insertElementAt(ItemOption.g(var2), var7.size() - 1);
                    } else {
                        var7.add(ItemOption.g(var2));
                    }

                    it.strOptions = Item.a(var7);
                }
                int[] x;
                if (y > 0) {
                    x = it.ab(y, var2);
                } else {
                    x = null;
                }
            }
            for (Item item : items) {
                if (item != null) {
                    removeItem(item, true);
                }
            }
            Message m = new Message((byte) -46);
            m.writeByte(1);
            m.writeShort(0);
            it.write(m.writer);
            m.writeByte(typeTb);
            user.session.sendMessage(m);

//            Log.debug("type " + typeTb + " num " + numTb + " TypeNogc " + sizeNgoc + " numNgoc " + numNgoc + " quanity " + u);
        } catch (IOException ex) {
            Logger.getLogger(HanderCombine.class.getName()).log(Level.SEVERE, null, ex);
            this.service.alertMessage("Co loi say ra vui long bao voi admin");
            return;
        }
    }

    public void tachKham(Message msg) {
        try {
            byte typeBag = msg.readByte();
            short index = msg.readShort();
            short idItem = msg.readShort();
            if (checkBag(typeBag)[index] == null) {
                service.alertMessage("Không có item");
                return;
            }
            Item itemTach = checkBag(typeBag)[index];
            if (itemTach != null) {
                if (Bag.vang < 600) {
                    service.serverMessage("Cần ít nhất 600 vàng trong hành trang");
                    return;
                }
                Item[] b = new Item[4 + Info.countKham];
                int i = 0;
                ItemOption[] var11;
                if ((var11 = itemTach.L()) != null) {
                    Vector var2 = new Vector();
                    Vector var3 = new Vector();
                    int var4 = 0;
                    ItemOption var5 = null;

                    int var6;
                    for (var6 = 0; var6 < var11.length; ++var6) {
                        if (var11[var6].a[0] == 298) {
                            var5 = var11[var6];
                        }

                        int var7;
                        if (idItem == -1) {
                            if ((var7 = var11[var6].h()) < 0) {
                                if (var11[var6].getItemOptionTemplate().type == 8) {
                                    ++var4;
                                }

                                var2.add(var11[var6]);
                            } else {
                                int var8 = var11[var6].a[3];
                                int var9 = 0;

                                for (int var10 = 0; var10 <= var8; ++var10) {
                                    var9 += DataCenter.gI().ngocKhamUpgrade[var10];
                                }

                                var3.add(new Item(var7, true, var9));
                            }
                        } else {
                            if ((var7 = var11[var6].h()) < 0 || idItem != 0 && idItem != var7) {
                                if (var11[var6].getItemOptionTemplate().type == 8) {
                                    ++var4;
                                }

                                var2.add(var11[var6]);
                            } else {
                                int var8 = var11[var6].a[3];
                                int var9 = 0;

                                for (int var10 = 0; var10 <= var8; ++var10) {
                                    var9 += DataCenter.gI().ngocKhamUpgrade[var10];
                                }

                                var3.add(new Item(var7, true, var9));
                            }
                        }
                    }

                    if (var5 != null) {
                        var5.c(var4);
                    }

                    itemTach.strOptions = Item.a(var2);

                    for (var6 = 0; var6 < var3.size(); ++var6) {
                        i += ((Item) var3.get(var6)).getAmount();
                        if (var6 >= b.length) {
                            break;
                        }

                        b[var6] = (Item) var3.elementAt(var6);
                    }
                    i = (int) (i * 1.3);
                    if (i > 600) {
                        i = 600;
                    }
                    addVang(-i);
                    for (Item item : b) {
                        if (item != null && item.amount > 0) {
                            addItem(item);
                            msgAddItemBag(item);
                        }
                    }
                }
                msgSendArrItemBag();
                msgUpdateItemBody_Orther();
                Message m = new Message((byte) -47);
                itemTach.write(m.writer);
                m.writeByte(typeBag);
                user.session.sendMessage(m);
            }
        } catch (IOException e) {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ghepCt(Message msg) {
        try {
            if (isSecurity && !isUnlockSecurity) {
                service.warningMessage("Vui lòng mở khoá bảo mật trước khi sử dụng tính năng này");
                return;
            }
            byte sizeGhep = msg.readByte();
            byte[] typeTemp = new byte[sizeGhep];
            short[] index = new short[sizeGhep];
            Item[] itemsToUpgrade = new Item[sizeGhep];

            // Đọc dữ liệu từ msg và kiểm tra trước khi tiến hành
            for (int i = 0; i < sizeGhep; i++) {
                typeTemp[i] = msg.readByte();
                index[i] = msg.readShort();
                itemsToUpgrade[i] = checkBag(typeTemp[i])[index[i]];
            }
            StringBuilder optionMergeBuilder = new StringBuilder();
            for (Item item : itemsToUpgrade) {
                if (item == null || item.getItemTemplate().type != 14) {
                    service.alertMessage("Loi cai trang");
                    return;
                }
                // Nếu đây không phải là item đầu tiên, thêm dấu chấm phẩy để ngăn cách
                if (optionMergeBuilder.length() > 0) {
                    optionMergeBuilder.append("!");
                }

                // Ghép nối thông tin item vào chuỗi
                optionMergeBuilder.append(item.id)
                        .append("-")
                        .append(item.strOptions);
            }
            String optionMerge = optionMergeBuilder.toString();
            Item itemCt = itemsToUpgrade[0];
            itemCt.strOptionsBackCaiTrang = optionMerge;
            for (int var9 = 0; var9 < itemsToUpgrade.length; ++var9) {
                if (itemsToUpgrade[var9] != null) {
                    ++itemCt.level;
                }
            }

            if (itemCt.level < 0) {
                itemCt.level = 0;
            }

            Vector var10 = new Vector();
            Vector var2 = new Vector();

            for (int var3 = 0; var3 < itemsToUpgrade.length; ++var3) {
                ItemOption[] var4;
                if (itemsToUpgrade[var3] != null && (var4 = itemsToUpgrade[var3].L()) != null) {
                    int var5;
                    if (!itemsToUpgrade[var3].equals(itemsToUpgrade[0])) {
                        for (var5 = 0; var5 < DataCenter.gI().ItemOptionTemplate.length; ++var5) {
                            if (DataCenter.gI().ItemOptionTemplate[var5].name.trim().toLowerCase().equals(itemsToUpgrade[var3].getItemTemplate().name.trim().toLowerCase())) {
                                var2.add(new ItemOption(DataCenter.gI().ItemOptionTemplate[var5].id + ",0,0"));
                                break;
                            }
                        }
                    }

                    for (var5 = 0; var5 < var4.length; ++var5) {
                        boolean var6 = true;

                        for (int var7 = 0; var7 < var10.size(); ++var7) {
                            if (((ItemOption) var10.get(var7)).a[0] == var4[var5].a[0]) {
                                ((ItemOption) var10.get(var7)).c(((ItemOption) var10.get(var7)).a[1] + var4[var5].a[1]);
                                var6 = false;
                                break;
                            }
                        }

                        if (var6) {
                            var10.add(var4[var5]);
                        }
                    }
                }
            }

            var10.addAll(var2);
            itemCt.strOptions = Item.a(var10);
            itemCt.level = (byte) var2.size();
            itemCt.isLock = true;
            for (int i = 0; i < sizeGhep; i++) {
                checkBag(typeTemp[i])[index[i]] = null;
            }
            addItem(itemCt);
            msgAddItemBag(itemCt);
            msgSendArrItemBag();
            user.session.sendMessage(HanderMessage.resetScreen());

        } catch (IOException e) {
        }
    }


    public void showct(Message msg) {
        try {
            byte typebag = msg.readByte();
            short index = msg.readShort();
            Item item = checkBag(typebag)[index];
            if (item == null) {
                return;
            }
        } catch (IOException e) {
        }

    }

    public void tachCt(Message msg) {
        try {
            if (isSecurity && !isUnlockSecurity) {
                service.warningMessage("Vui lòng mở khoá bảo mật trước khi sử dụng tính năng này");
                return;
            }
            byte type = msg.readByte();
            short index = msg.readShort();
            if (checkBag(type)[index] == null) {
                return;
            }
            Item item = checkBag(type)[index];
            Message m = new Message((byte) -51);
            m.writeByte(type);
            m.writeShort(index);
            String[] itemsData = item.strOptionsBackCaiTrang.split("!");
            List<Item> items = new ArrayList<>();

            for (String itemData : itemsData) {
                String[] parts = itemData.split("-");
                if (parts.length >= 2) {
                    short id = Short.parseShort(parts[0]);
                    String strOptions = parts[1];
                    Item itemSplit = new Item(id);
                    itemSplit.strOptions = strOptions;

                    items.add(itemSplit);
                }
            }
            if (items == null) {
                service.alertMessage("Co loi say ra trong qua trinh tach ct");
                return;
            }
            if (items.size() > getCountNullItemBag()) {
                service.alertMessage("Dọn hành trang trước khi tách để tránh mất cải trang nhé bạn");
                return;
            }
            for (Item it : items) {
                Item clone = it.cloneItem();
                clone.isLock = true;
                clone.write(m.writer);
                addItem(clone);
                msgAddItemBag(clone);
            }
            checkBag(type)[index] = null;
            user.session.sendMessage(m);
        } catch (IOException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doiTinhThach(Message msg) {
        try {
            byte size = msg.readByte();
            Vector<Item> items = new Vector<>();
            boolean[] list = new boolean[Bag.arrItemBag.length];
            while (msg.reader.Avali() > 0) {
                byte type = msg.readByte();
                int index = msg.readShort();
                if (type != 0) {
                    service.alertMessage("Vui lòng cấp trang bị vào hành trang trước khi đổi");
                    return;
                }
                if (index < 0 || index >= Bag.arrItemBag.length) {
                    continue;
                }
                if (!list[index]) {
                    list[index] = true;
                    Item item = Bag.arrItemBag[index];
                    if (item != null && item.isItemBody() && item.level == 0)
                        items.add(item);
                    if (item != null && item.isItemBody() && item.level > 0)
                        getService().warningMessage("Có trang bị đã nâng cấp không thể đổi tinh thạch");
                }
            }
            if (items.size() > 16 || items.size() < size || items.size() < 1) {
                return;
            }
            int numTinhThach = 0;
            int numNgocMyo = 0;
            int numNgocBayan = 0;
            int numNgocRineGan = 0;
            int numNgocSharigan = 0;
            for (Item item : items) {
                numTinhThach += item.getTinhThach();
                if (item.W() || item.X()) {
                    int var9 = item.getItemTemplate().levelNeed / 10 * 100 - 100;
                    if (item.getItemTemplate().levelNeed / 10 == 6) {
                        var9 = 600;
                    }

                    if (item.X()) {
                        var9 *= 2;
                    }

                    if (item.isVuKhi()) {
                        numNgocMyo += var9;
                    }

                    if (item.isSharigan()) {
                        numNgocSharigan += var9;
                    }

                    if (item.isBayakugan()) {
                        numNgocBayan += var9;
                    }

                    if (item.isRenegan()) {
                        numNgocRineGan += var9;
                    }
                }
            }
            if (numTinhThach > 0) {
                Item tt = new Item(160);
                tt.isLock = true;
                tt.amount = numTinhThach;
                addItem(tt);
                msgAddItemBag(tt);
            }
            if (numNgocMyo > 0) {
                Item tt = new Item(353);
                tt.isLock = true;
                tt.amount = numNgocMyo;
                addItem(tt);
                msgAddItemBag(tt);
            }
            if (numNgocSharigan > 0) {
                Item tt = new Item(565);
                tt.isLock = true;
                tt.amount = numNgocSharigan;
                addItem(tt);
                msgAddItemBag(tt);
            }
            if (numNgocBayan > 0) {
                Item tt = new Item(563);
                tt.isLock = true;
                tt.amount = numNgocBayan;
                addItem(tt);
                msgAddItemBag(tt);
            }
            if (numNgocRineGan > 0) {
                Item tt = new Item(567);
                tt.isLock = true;
                tt.amount = numNgocRineGan;
                addItem(tt);
                msgAddItemBag(tt);
            }
            for (Item item : items) {
                removeItem(item, true);
                msgRemoveItemBag(item);
            }
            user.session.sendMessage(HanderMessage.resetScreen());

        } catch (Exception e) {

        }
    }

    public void nangBiKip(Message msg) {
        try {
            int size = msg.readByte();
            if (size < 2) {
                service.alertMessage("Can du 2 bi kip");
                return;
            }
            byte type_1 = msg.readByte();
            short index_1 = msg.readShort();
            byte type_2 = msg.readByte();
            short index_2 = msg.readShort();
            if (type_1 == type_2 && index_1 == index_2) {
                service.alertMessage("Bug item ?");
                return;
            }
            Item item1 = checkBag(type_1)[index_1];
            Item item2 = checkBag(type_2)[index_2];
            if (item1 == null || item2 == null) {
                return;
            }
            Item bikip = item1.getItemTemplate().levelNeed > item2.getItemTemplate().levelNeed ? item1 : item2;
            if (item1.getItemTemplate().levelNeed - item2.getItemTemplate().levelNeed > 15 || item2.getItemTemplate().levelNeed - item1.getItemTemplate().levelNeed > 15) {
                service.alertMessage("Vui lòng nâng cấp theo hướng dẫn");
                return;
            }
            ItemOption[] options = bikip.getItemOption();
            Vector listops = new Vector<>();
            ItemOption tuluyen = null;
            ItemOption op143 = null;
            ItemOption op144 = null;
            ItemOption op145 = null;
            for (ItemOption ops : options) {
                if (ops.getId() == 128) {
                    tuluyen = ops;
                }
                if (ops.getId() == 143) {
                    op143 = ops;
                }
                if (ops.getId() == 144) {
                    op144 = ops;
                }
                if (ops.getId() == 145) {
                    op145 = ops;
                }
                listops.add(ops);
            }
            if (tuluyen == null) {
                return;
            }
            int maxTuLuyen = getMaxTuLuyen(bikip.getItemTemplate().levelNeed);
            if (tuluyen.f() >= maxTuLuyen) {
                service.alertMessage("Đã tới giới hạn");
                return;
            }
            ItemOption var9 = (ItemOption) listops.get(listops.size() - 1);
            if (bikip.getItemTemplate().levelNeed == 35) {
                if (var9.getItemOptionTemplate().type == 3) {
                    if (op144 == null)
                        listops.add(new ItemOption("144,50,50"));
                } else {
                    if (op143 == null)
                        listops.add(new ItemOption("143,15,15"));
                }

                if (var9.getItemOptionTemplate().type != 4) {
                    tuluyen.e(tuluyen.f() + 1000);
                    bikip.strOptions = Item.a(listops);
                } else {
                    tuluyen.e(tuluyen.f() + 1000);
                    bikip.strOptions = Item.a(listops);
                }
            } else if (bikip.getItemTemplate().levelNeed == 49) {
                if (var9.getItemOptionTemplate().type == 4) {
                    if (op145 == null)
                        listops.add(new ItemOption("145,10,10"));
                } else if (var9.getItemOptionTemplate().type == 3) {
                    if (op144 == null)
                        listops.add(new ItemOption("144,80,80"));
                } else {
                    if (op143 == null)
                        listops.add(new ItemOption("143,25,25"));
                }

                if (var9.getItemOptionTemplate().type != 5) {
                    tuluyen.e(tuluyen.f() + 1000);
                    bikip.strOptions = Item.a(listops);
                } else {
                    tuluyen.e(tuluyen.f() + 1000);
                    bikip.strOptions = Item.a(listops);
                }
            } else {
                tuluyen.e(tuluyen.f() + 1000);
                bikip.strOptions = Item.a(listops);
            }
            checkBag(type_1)[index_1] = null;
            checkBag(type_2)[index_2] = null;
            addItem(bikip);
            msgNangBiKip(type_1, index_1, type_2, index_2, bikip);
        } catch (Exception e) {
            Log.error("erro nang bi kip ", e);
        }
    }

    private void msgNangBiKip(byte type_1, short index_1, byte type_2, short index_2, Item bikip) throws IOException {
        Message m = new Message((byte) -96);
        m.writeByte(2);
        m.writeByte(type_1);
        m.writeShort(index_1);
        m.writeByte(type_2);
        m.writeShort(index_2);
        bikip.write(m.writer);
        m.writeByte(0);
        user.session.sendMessage(m);
    }

    public int getMaxTuLuyen(int levelNeed) {
        switch (levelNeed) {
            case 30:
                return 10000;
            case 45:
                return 14000;
            case 49:
                return 17000;
        }
        return 0;
    }

    public void nangCapSkillViThu(Message msg) {
        lockViThu.lock();
        try {
            byte index = msg.readByte();
            boolean isVang = msg.readBoolean();
            if (index < 0 || index > 6) {
                return;
            }
            SkillClan skillClan = listSkill.get(index);
            if (skillClan != null) {
                if (isVang) {
                    if (Bag.vang < 1000) {
                        user.session.sendMessage(HanderMessage.SendThongBao("Không có đủ 1000 vàng", HanderMessage.RED_MID));
                        return;
                    }
                    if (skillClan.levelNeed >= Info.levelMaxViThu) {
                        user.session.sendMessage(HanderMessage.SendThongBao("Đã max cấp", HanderMessage.RED_MID));
                        return;
                    }
                    addVang(-1000);
                    skillClan.a(skillClan.levelNeed + 1);
                    listSkill.set(index, skillClan);
                    msgSkillViThu();
                    msgUpdateItemBody();
                    msgUpdateItemBody_Orther();
                } else {
                    if (Bag.bacKhoa < 50000000) {
                        user.session.sendMessage(HanderMessage.SendThongBao("Không có đủ 50tr bạc khoá", HanderMessage.RED_MID));
                        return;
                    }
                    if (skillClan.levelNeed >= Info.levelMaxViThu) {
                        user.session.sendMessage(HanderMessage.SendThongBao("Đã max cấp", HanderMessage.RED_MID));
                        return;
                    }
                    addBacKhoa(-50000000);
                    skillClan.a(skillClan.levelNeed + 1);
                    listSkill.set(index, skillClan);
                    msgSkillViThu();
                    msgUpdateItemBody();
                    msgUpdateItemBody_Orther();
                }
            }
        } catch (Exception e) {

        } finally {
            lockViThu.unlock();
        }
    }

    public void xoaSkillViThu(Message msg) {
        lockViThu.lock();
        try {
            byte index = msg.readByte();
            boolean isVang = msg.readBoolean();
            if (index < 0 || index > 6) {
                return;
            }
            SkillClan skillClan = listSkill.get(index);
            if (skillClan != null) {
                if (isVang) {
                    if (Bag.vang < 500) {
                        user.session.sendMessage(HanderMessage.SendThongBao("Không có đủ 500 vàng", HanderMessage.RED_MID));
                        return;
                    }
                    addVang(-500);
                    listSkill.remove(index);
                    msgSkillViThu();
                    msgUpdateItemBody();
                    msgUpdateItemBody_Orther();
                } else {
                    if (Bag.bacKhoa < 30000000) {
                        user.session.sendMessage(HanderMessage.SendThongBao("Không có đủ 30tr bạc khoá", HanderMessage.RED_MID));
                        return;
                    }
                    addBacKhoa(-30000000);
                    listSkill.remove(index);
                    msgSkillViThu();
                    msgUpdateItemBody();
                    msgUpdateItemBody_Orther();
                }
            }
        } catch (Exception e) {

        } finally {
            lockViThu.unlock();
        }
    }

    public void cleanTrade() {
        this.trade = null;
        this.myTrade = null;
        this.partnerTrade = null;
    }

    public void tradeInvite(Message msg) {
        try {
            if (InfoGame.isDie) {
                return;
            }
            String name = msg.readUTF();
            Char _char = this.zone.getChars().stream().filter(s -> s.Info.name.equals(name)).findAny().orElse(null);
            if (_char != null) {
                if (!user.actived) {
                    service.alertMessage("Tài khoản của bạn chưa thể giao dịch. Vui lòng kích hoạt tài khoản!");
                    return;
                }
                if (isSecurity && !isUnlockSecurity) {
                    service.warningMessage("Vui lòng mở khoá bảo mật trước khi sử dụng tính năng này");
                    return;
                }
                if (!_char.user.actived) {
                    service.alertMessage("Đối phương chưa kích hoạt tài khoản");
                    return;
                }
                int distance = Utlis.getRange(_char.Info.cx, _char.Info.cy, this.Info.cx, this.Info.cy);
                if (distance > 100) {
                    service.alertMessage("Khoảng cách quá xa!");
                    return;
                }
                if (_char.trade != null) {
                    service.alertMessage("Người này đang giao dịch với người khác.");
                    return;
                }
                if (_char.InfoGame.isDie) {
                    service.alertMessage("Người chơi đang chết.");
                    return;
                }
                Invite.PlayerInvite p = _char.invite.findCharInvite(Invite.GIAO_DICH, this.id);
                if (p != null) {
                    service.alertMessage("Không thể mời giao dịch liên tục. Vui lòng thử lại sau 30s nữa.");
                    return;
                }
                _char.invite.addCharInvite(Invite.GIAO_DICH, this.id, 30);
                _char.service.tradeInvite(Info.name);
                Trade trade = new Trade();
                this.trade = trade;
                _char.trade = trade;
                this.myTrade = trade.traders[0] = new Trader(this);
                _char.myTrade = trade.traders[1] = new Trader(_char);
                this.partnerTrade = _char.myTrade;
                _char.partnerTrade = this.myTrade;
            }
        } catch (Exception e) {

        }
    }

    public void acceptInviteTrade(Message msg) {
        try {
            if (isSecurity && !isUnlockSecurity) {
                service.warningMessage("Vui lòng mở khoá bảo mật trước khi sử dụng tính năng này");
                return;
            }
            trade.openUITrade();
        } catch (Exception e) {

        }
    }

    public void tradeItemLock(Message ms) {
        try {
            int xu = ms.readInt();
            byte itemLength = ms.readByte();
            if (xu > 0 && xu <= this.Bag.bac) {
                this.myTrade.coinTradeOrder = xu;
            }
            String errFormat = "";
            boolean isMyMissTake = true;
            Char partner = partnerTrade.getChar();
            if (this.Bag.bac < xu) {
                errFormat = "%s không đủ Bạc để giao dịch";
            } else if (xu > 500000000) {
                errFormat = "%s đã giao dịch quá giới hạn 500.000.000 Bạc";
            } else if (itemLength > partner.getCountNullItemBag()) {
                isMyMissTake = false;
                errFormat = "%s không đủ chỗ trống trong hành trang";
            } else if (partner.Bag.bac + xu > Integer.MAX_VALUE) {
                isMyMissTake = false;
                errFormat = "%s đã đạt giới hạn chứa xu, vui lòng giao dịch ít hơn";
            }
            if (!errFormat.equals("")) {
                tradeClose();
                String partner_name_new = partner.Info.name;
                String name_new = Info.name;
                service.alertMessage(String.format(errFormat, isMyMissTake ? "Bạn" : partner_name_new));
                partner.service.alertMessage(String.format(errFormat, isMyMissTake ? name_new : "Bạn"));
                return;
            }
            ArrayList<Integer> list = new ArrayList<>();
            this.myTrade.itemTradeOrder = new Vector<>();
            for (int i = 0; i < itemLength; i++) {
                int index = ms.readShort();
                if (index < 0 || index >= this.Bag.arrItemBag.length) {
                    continue;
                }
                if (Bag.arrItemBag[index] != null) {
                    if (Utlis.checkExist(list, index)) {
                        continue;
                    }
                    this.myTrade.itemTradeOrder.add(Bag.arrItemBag[index]);
                    list.add(index);
                }
            }
            trade.tradeItemLock(myTrade);
            trade.viewItemInfo(partner);
            myTrade.isLock = true;
        } catch (Exception e) {

        }
    }

    private void tradeClose() {
    }

    public void tradeAccept() {
        if (trade == null || myTrade == null) {
            return;
        }
        if (!myTrade.accept && myTrade.isLock) {
            myTrade.accept = true;
            (myTrade == trade.traders[0] ? trade.traders[1] : trade.traders[0]).player.service.tradeAccept();
        }
        if (trade.traders[0].accept && trade.traders[1].accept) {
            try {
                trade.update();
            } catch (Exception ex) {
                Log.error("err: " + ex.getMessage(), ex);
            }
        }
    }

    public void acceptInviteClan() {
        try {
            if (clan != null) {
                return;
            }
            Char _char = zone.findCharInMapByName(nameInvite);
            if (_char != null && _char.clan != null) {
                Invite.PlayerInvite c = this.invite.findCharInvite(Invite.GIA_TOC, _char.id);
                if (c == null) {
                    service.alertMessage("Đã hết thời gian chấp nhận vào gia tộc.");
                    return;
                }
                if (!user.actived) {
                    service.serverMessage("Bạn chưa kích hoạt tài khoản");
                    return;
                }
                if (this.timeOutClan > System.currentTimeMillis()) {
                    service.serverMessage("Ban vừa mới rời gia tộc vẫn đang trong thời gian chờ");
                    return;
                }
                Clan clan = _char.clan;
                List<Member> members = clan.memberDAO.getAll();
                if (members.size() < clan.getMemberMax()) {
                    this.clan = clan;
                    Member mem = Member.builder()
                            .classId(this.Info.idClass)
                            .level(level())
                            .type(Clan.TYPE_NORMAl)
                            .name(this.Info.name)
                            .idChar(this.Info.idChar)
                            .pointClan(0)
                            .pointClanWeek(0)
                            .build();
                    mem.setChar(this);
                    mem.setOnline(true);
                    clan.memberDAO.save(mem);
                    service.showGiaToc();
                    clan.writeLog(Info.name, " đã vào gia tộc ", 0);
                    _char.user.session.sendMessage(HanderMessage.SendThongBao("Người chơi " + Info.name + "vừa gia nhập gia tộc", HanderMessage.WHITE));
                    user.session.sendMessage(HanderMessage.SendThongBao("Bạn đã gia nhập vào gia tộc " + this.clan.getName(), HanderMessage.WHITE));
                    zone.SendMessageInZone(HanderMessage.sendGiaToc(this));
                } else {
                    service.alertMessage("Gia tộc đã đủ thành viên.");
                }
            } else {
                service.alertMessage("Người mời đã rời khỏi khu vực.");
            }
        } catch (Exception e) {
            Log.error("Loi vao gia toc", e);
        } finally {
            nameInvite = "";
        }
    }

    public void acceptClan() {
        try {
            if (!inviteName.isEmpty()) {
                Char pl = ServerManager.findCharByName(inviteName);
                if (pl != null) {
                    if (pl.clan != null) {
                        return;
                    }
                    if (clan == null) {
                        return;
                    }
                    if (pl.timeOutClan > System.currentTimeMillis()) {
                        service.serverMessage("Đối phương vừa mới rời gia tộc vẫn đang trong thời gian chờ");
                        return;
                    }
                    if (!pl.user.actived) {
                        service.serverMessage("Đối phương chưa kích hoạt tài khoản");
                        return;
                    }
                    Clan clan = this.clan;
                    List<Member> members = clan.memberDAO.getAll();
                    if (members.size() < clan.getMemberMax()) {
                        pl.clan = clan;
                        Member mem = Member.builder()
                                .classId(pl.Info.idClass)
                                .level(level())
                                .type(Clan.TYPE_NORMAl)
                                .name(pl.Info.name)
                                .idChar(pl.Info.idChar)
                                .pointClan(0)
                                .pointClanWeek(0)
                                .build();
                        mem.setChar(pl);
                        mem.setOnline(true);
                        clan.memberDAO.save(mem);
                        pl.service.showGiaToc();
                        clan.writeLog(pl.Info.name, " đã vào gia tộc ", 0);
                        this.user.session.sendMessage(HanderMessage.SendThongBao("Người chơi " + Info.name + "vừa gia nhập gia tộc", HanderMessage.WHITE));
                        pl.user.session.sendMessage(HanderMessage.SendThongBao("Bạn đã gia nhập vào gia tộc " + this.clan.getName(), HanderMessage.WHITE));
                        pl.zone.SendMessageInZone(HanderMessage.sendGiaToc(pl));
                    } else {
                        pl.service.alertMessage("Gia tộc đã đủ thành viên.");
                    }
                }
            }
        } catch (Exception e) {
            Log.error("error chap nhan vao clan ", e);
        } finally {
            inviteName = "";
        }
    }

    public void changeClanType(Message ms) {
        try {
            String name = ms.readUTF();
            byte type = ms.readByte();
            if (this.clan != null) {
                if (isSecurity && !isUnlockSecurity) {
                    service.warningMessage("Vui lòng mở khoá bảo mật trước khi sử dụng tính năng này");
                    return;
                }
                int cType = clan.getMemberByName(Info.name).getType();
                if (cType == Clan.TYPE_TOCTRUONG) {
                    Member mem = this.clan.getMemberByName(name);
                    if (mem != null) {
                        if (type == Clan.TYPE_TOCPHO || type == Clan.TYPE_TRUONGLAO) {
                            if (type == Clan.TYPE_TOCPHO && this.clan.getNumberSameType(type) >= 1) {
                                service.serverMessage("Chức phó gia tộc đã đủ chỗ.");
                                return;
                            }
                            if (type == Clan.TYPE_TRUONGLAO && this.clan.getNumberSameType(type) >= 5) {
                                service.serverMessage("Chức trưởng lão gia tộc đã đủ chỗ.");
                                return;
                            }

                            mem.setType(type);
                            Connection conn = Connect.getConnection();
                            PreparedStatement stmt = conn
                                    .prepareStatement("UPDATE `clan_member` SET `type` = ? WHERE `id` = ? LIMIT 1;");
                            try {
                                stmt.setInt(1, mem.getType());
                                stmt.setInt(2, mem.getId());
                                stmt.executeUpdate();
                            } finally {
                                stmt.close();
                            }
                            Char _char = ServerManager.findCharByName(name);
                            if (_char != null) {
                                _char.service.showGiaToc();
                            }
                            service.showInfoGiaToc();
                            clan.getClanService().serverMessage(name + " được bổ nhiệm làm "
                                    + ((type == Clan.TYPE_TOCPHO) ? "tộc phó" : "trưởng lão"));

                        } else {
                            if (type == 5) {
                                if (clan.getCoin() < 1000000) {
                                    service.serverMessage("Ngân sách không đủ 1 triệu bạc");
                                    return;
                                }
                                clan.addCoin(-1000000);
                                clan.writeLog(Info.name, "Đã nhường chức tộc trưởng cho " + mem.getName() + " ngân quỹ trừ", 1000000);
                                Connection conn = Connect.getConnection();
                                Member old = this.clan.getMemberByName(Info.name);
                                old.setType(Clan.TYPE_NORMAl);
                                PreparedStatement stmt1 = conn
                                        .prepareStatement("UPDATE `clan_member` SET `type` = ? WHERE `id` = ? LIMIT 1;");
                                try {
                                    stmt1.setInt(1, old.getType());
                                    stmt1.setInt(2, old.getId());
                                    stmt1.executeUpdate();
                                } finally {
                                    stmt1.close();
                                }
                                clan.setMainName(mem.getName());
                                service.showGiaToc();
                                mem.setType(Clan.TYPE_TOCTRUONG);
                                PreparedStatement stmt = conn
                                        .prepareStatement("UPDATE `clan_member` SET `type` = ? WHERE `id` = ? LIMIT 1;");
                                try {
                                    stmt.setInt(1, mem.getType());
                                    stmt.setInt(2, mem.getId());
                                    stmt.executeUpdate();
                                } finally {
                                    stmt.close();
                                }
                                Char _char = ServerManager.findCharByName(name);
                                if (_char != null) {
                                    _char.service.showGiaToc();
                                }
                                service.resetScreen();
                                service.serverMessage("Nhường chức tộc trưởng thành công");
                                return;
                            }
                            Connection conn = Connect.getConnection();
                            mem.setType(Clan.TYPE_NORMAl);
                            PreparedStatement stmt = conn
                                    .prepareStatement("UPDATE `clan_member` SET `type` = ? WHERE `id` = ? LIMIT 1;");
                            try {
                                stmt.setInt(1, mem.getType());
                                stmt.setInt(2, mem.getId());
                                stmt.executeUpdate();
                            } finally {
                                stmt.close();
                            }
                            Char _char = ServerManager.findCharByName(name);
                            if (_char != null) {
                                _char.service.showGiaToc();
                            }
                            service.showInfoGiaToc();
                            clan.getClanService().serverMessage(name + " đã bị bãi nhiệm");
                        }
                    } else {
                        service.serverMessage("Thành viên không tồn tại.");
                    }
                } else {
                    service.serverMessage("Bạn không phải tộc trưởng.");
                }
            } else {
                service.serverMessage("Bạn không có gia tộc.");
            }
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void moveOutClan(Message ms) {
        try {
            String name = ms.readUTF();
            if (name.equals(Info.name)) {
                service.alertMessage("Bạn không thể tự trục xuất chính mình.");
                return;
            }
            if (this.clan != null) {
                if (isSecurity && !isUnlockSecurity) {
                    service.warningMessage("Vui lòng mở khoá bảo mật trước khi sử dụng tính năng này");
                    return;
                }
                int cType = this.clan.getMemberByName(Info.name).getType();
                if (cType == Clan.TYPE_TOCTRUONG || cType == Clan.TYPE_TOCPHO) {
                    Member mem = this.clan.getMemberByName(name);
                    if (mem == null) {
                        service.serverMessage("Thành viên này không tồn tại.");
                        return;
                    }
                    int memType = mem.getType();
                    if (memType >= cType) {
                        service.serverMessage("Bạn không có quyền trục xuất người này.");
                        return;
                    }
                    if (this.clan.countKick <= 0) {
                        service.serverMessage("Clan đã hết lượt kick vào ngày hôm nay.");
                        return;
                    }

                    int coin = 10000;
                    switch (memType) {

                        case Clan.TYPE_TOCPHO:
                            coin = 100000;
                            break;

                        case Clan.TYPE_TRUONGLAO:
                            coin = 50000;
                            break;

                        case Clan.TYPE_UUTU:
                            coin = 20000;
                            break;
                    }
                    if (this.clan.getCoin() < coin) {
                        service.serverMessage("Ngân quỹ không đủ.");
                        return;
                    }
                    clan.memberDAO.delete(mem);
                    clan.writeLog(Info.name, "đã kích người chơi " + name + " ngân quỹ trừ ", coin);
                    this.clan.addCoin(-coin);
                    this.clan.countKick--;
                    Char _char = ServerManager.findCharByName(name);
                    if (_char != null) {
                        _char.clan = null;
                        _char.service.showGiaToc();
                        _char.msgUpdateHpFull();
                        _char.msgUpdateMpFull();
                        _char.timeOutClan = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(24);
                    }
                    service.showInfoGiaToc();
                    this.clan.getClanService().serverMessage(Info.name + " đã trục xuất " + name + ", ngân quỹ trừ "
                            + coin + " Bạc");
                } else {
                }
            } else {
                service.serverMessage("Bạn không có gia tộc");
            }
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void outClan() {
        if (this.clan != null) {
            Member mem = this.clan.getMemberByName(Info.name);
            if (mem != null) {
                if (isSecurity && !isUnlockSecurity) {
                    service.warningMessage("Vui lòng mở khoá bảo mật trước khi sử dụng tính năng này");
                    return;
                }
                int cType = mem.getType();
                if (cType != Clan.TYPE_TOCTRUONG) {
                    try {
                        int coin = 10000;
                        switch (cType) {
                            case Clan.TYPE_TOCPHO:
                                coin = 100000;
                                break;

                            case Clan.TYPE_TRUONGLAO:
                                coin = 50000;
                                break;

                            case Clan.TYPE_UUTU:
                                coin = 20000;
                                break;
                        }
                        if (this.Bag.bac < coin) {
                            service.serverMessage("Bạn không đủ Bac.");
                            return;
                        }
                        this.timeOutClan = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(24);
                        addBac(-coin);
                        this.clan.memberDAO.delete(mem);
                        this.clan.getClanService().serverMessage(Info.name + " đã rời gia tộc");
                        this.clan = null;
                        service.resetScreen();
                        service.showGiaToc();
                        msgUpdateHpFull();
                        msgUpdateMpFull();
                        service.serverMessage("Bạn đã rời gia tộc thành công.");
                    } catch (Exception ex) {
                        Log.error("out clan err: " + ex.getMessage(), ex);
                    }
                } else {
                    service.serverMessage("Bạn là tộc trưởng nên không thể rời.");
                }
            }
        } else {
            service.serverMessage("Bạn không trong gia tộc.");
        }
    }

    public void moSkill(Message msg) {
        try {
            if (this.clan != null) {
                if (isSecurity && !isUnlockSecurity) {
                    service.warningMessage("Vui lòng mở khoá bảo mật trước khi sử dụng tính năng này");
                    return;
                }
                int cType = this.clan.getMemberByName(Info.name).getType();
                if (cType == Clan.TYPE_TOCTRUONG || cType == Clan.TYPE_TOCPHO) {
                    int id = msg.readByte();
                    SkillClan skillClan = DataCenter.gI().getSkillById(id);
                    if (skillClan != null) {
                        if (skillClan.levelNeed > clan.getLevel()) {
                            service.serverMessage("Gia tộc của bạn chưa đủ yều cầu để khai mở kỹ năng này");
                            return;
                        }
                        if (skillClan.moneyBuy > clan.getCoin()) {
                            service.serverMessage("Gia tộc của bạn không đủ bạc để mở khoá kỹ năng");
                            return;
                        }
                        this.clan.addCoin(-skillClan.moneyBuy);
                        this.clan.skillClans.add(skillClan);
                        this.service.showInfoGiaToc();
                        clan.writeLog(Info.name, "đã mở skill " + skillClan.name + " và ngân quỹ trừ ", skillClan.moneyBuy);
                        this.clan.getClanService().serverMessage("Gia tộc đã mở khoá kỹ năng " + skillClan.name);
                    }

                }
            }
        } catch (Exception e) {

        }
    }

    public void inputCoinClan(Message ms) {
        try {
            if (trade != null) {
                return;
            }
            if (clan == null) {
                return;
            }
            int coin = ms.readInt();
            if (coin < 1000 || coin > 100000000) {
                service.alertMessage("Vui lòng nhập trong khoảng từ 1.000 Bạc đến 100.000.000 Bạc.");
                return;
            }
            if (coin > this.Bag.bac) {
                service.alertMessage("Bạn không đủ Bac.");
                return;
            }

            if (this.clan.main_name != this.Info.name && !this.user.actived) {
                service.serverMessage("Bạn phải kích hoạt để dùng tính năng này");
                return;
            }
            if (isSecurity && !isUnlockSecurity) {
                service.warningMessage("Vui lòng mở khoá bảo mật trước khi sử dụng tính năng này");
                return;
            }

            clan.writeLog(Info.name, "đã góp vào gia tộc ", coin);
            clan.addCoin(coin);
            addBac(-coin);
            service.showInfoGiaToc();
            clan.getClanService()
                    .serverMessage(Info.name + " đóng góp vào ngân quỹ " + coin + " Bac");
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void openFindParty() {
        try {
            HashMap<String, Group> groups = new HashMap<>();
            List<Char> chars = zone.getChars();
            for (Char _char : chars) {
                if (_char != null && _char.group != null) {
                    groups.put(_char.group.memberGroups.get(0).name, _char.group);
                }
            }
            getService().openFindParty(groups);
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void addParty(Message msg) {
        try {
            if (InfoGame.isDie) {
                return;
            }
            String name = msg.readUTF();
            if (this.Info.name.equals(name)) {
                return;
            }
            Char _char = ServerManager.findCharByName(name);
            if (_char != null) {
                if (this.group == null) {
                    createGroup();
                } else {
                    MemberGroup party = this.group.memberGroups.get(0);
                    if (party.charId != this.id) {
                        service.serverMessage("Bạn không phải trưởng nhóm");
                        return;
                    }
                }
                if (_char.group != null) {
                    service.serverMessage("Người này đã gia nhập nhóm khác");
                    return;
                }
                Invite.PlayerInvite p = _char.invite.findCharInvite(Invite.NHOM, this.id);
                Invite.PlayerInvite c = this.invite.findCharInvite(Invite.XIN_VAO_NHOM, _char.id);
                if (c != null) {
                    acceptPleaseParty(name);
                    return;
                }
                if (p != null) {
                    service.alertMessage("Không thể mời vào nhóm liên tục. Vui lòng thử lại sau 30s nữa.");
                    return;
                }
                _char.invite.addCharInvite(Invite.NHOM, this.id, 30);
                _char.getService().partyInvite(Info.name);
            } else {
                service.serverMessage("Người này không tồn tại hoặc không online.");
            }
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void addPartyAccept(Message msg) {
        try {
            if (this.group == null) {
                String name = msg.readUTF();
                Char _char = ServerManager.findCharByName(name);
                if (_char != null) {
                    if (_char.group == null) {
                        service.serverMessage("Nhóm này không còn tồn tại");
                        return;
                    }
                    MemberGroup p = _char.group.memberGroups.get(0);
                    if (p.charId != _char.id) {
                        service.serverMessage("Người này không phải nhóm trưởng");
                        return;
                    }
                    Invite.PlayerInvite c = this.invite.findCharInvite(Invite.NHOM, _char.id);
                    if (c == null) {
                        pleaseInputParty(name);
                        return;
                    }
                    MemberGroup party = new MemberGroup();
                    party.charId = this.id;
                    party.classID = this.Info.idClass;
                    party.name = this.Info.name;
                    party.setChar(this);
                    _char.group.add(party);
                    this.group = _char.group;
                    this.group.getGroupService().playerInParty();
                } else {
                    service.serverMessage("Hiện tại người này không online.");
                }
            } else {
                service.serverMessage("Bạn đã gia nhập nhóm khác");
            }
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void acceptPleaseParty(String name) {
        if (this.group == null) {
            return;
        }
        if (this.group.memberGroups.size() < 6) {
            if (this.group.memberGroups.get(0).charId == this.id) {
                try {
                    Char _char = ServerManager.findCharByName(name);
                    if (_char != null) {
                        if (_char.group == null) {
                            Invite.PlayerInvite c = this.invite.findCharInvite(Invite.XIN_VAO_NHOM, _char.id);
                            if (c == null) {
                                service.alertMessage("Đã hết thời gian chấp nhận yêu cầu tham gia nhóm.");
                                return;
                            }
                            _char.group = this.group;
                            MemberGroup p = new MemberGroup();
                            p.charId = _char.id;
                            p.classID = _char.Info.idClass;
                            p.name = _char.Info.name;
                            p.setChar(_char);
                            _char.group.add(p);
                        } else {
                            service.alertMessage("Người này đã gia nhập nhóm khác");
                        }
                    } else {
                        service.alertMessage("Người này không online");
                    }
                } catch (Exception ex) {
                    Log.error("err: " + ex.getMessage(), ex);
                }
            }
        } else {
            service.alertMessage("Nhóm đã đủ thành viên");
        }
    }

    public void pleaseInputParty(String name) {
        try {
            if (this.group != null) {
                return;
            }
            if (this.Info.name.equals(name)) {
                return;
            }
            Char _char = ServerManager.findCharByName(name);
            if (_char != null) {
                if (_char.group != null) {
                    if (_char.group.isLock) {
                        service.serverMessage("Nhóm này đã khóa, không thể xin gia nhập");
                        return;
                    }
                    if (_char.group.memberGroups.size() == 6) {
                        service.serverMessage("Nhóm đã đủ thành viên");
                        return;
                    }
                    if (_char.group.memberGroups.get(0).charId != _char.id) {
                        return;
                    }
                    Invite.PlayerInvite p = _char.invite.findCharInvite(Invite.XIN_VAO_NHOM, this.id);
                    if (p != null) {
                        service.serverMessage("Không thể xin vào nhóm liên tục. Vui lòng thử lại sau 30s nữa.");
                        return;
                    }
                    _char.invite.addCharInvite(Invite.XIN_VAO_NHOM, this.id, 30);
                    _char.getService().pleaseInputParty(Info.name);
                } else {
                    service.serverMessage("Nhóm này không tồn tại");
                }
            } else {
                service.serverMessage("Người này không online hoặc không tồn tại");
            }
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void outParty() {
        if (this.group != null) {
            synchronized (group) {
                if (this.group.memberGroups.size() > 1) {
                    int index = this.group.getIndexById(this.id);
                    if (index != -1) {
                        this.group.removeParty(index);
                    }
                }
                this.group = null;
                getService().outParty();
            }
        }

    }

    public void moveMember(Message ms) {
        try {
            if (this.group != null) {
                if (this.group.memberGroups.get(0).charId == this.id) {
                    if (isSecurity && !isUnlockSecurity) {
                        service.warningMessage("Vui lòng mở khoá bảo mật trước khi sử dụng tính năng này");
                        return;
                    }
                    String name = ms.readUTF();
                    if (Info.name.equals(name)) {
                        service.serverMessage("Không thể kick bản thân");
                    }
                    List<MemberGroup> partys = this.group.getMemberGroup();
                    Char _char = null;
                    for (MemberGroup p : partys) {
                        if (p.name.equals(name)) {
                            _char = p.getChar();
                        }
                    }
                    if (_char != null) {
                        _char.outParty();
                        _char.service.serverMessage("Bạn đã bị đuổi khỏi nhóm");
                    }
                    // _char.sendMessage(new Message(83));
                    // this.group.removeParty(index);
                } else {
                    service.serverMessage("Bạn không phải trưởng nhóm");
                }
            }

        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void changeTeamLeader(Message ms) {
        try {
            if (InfoGame.isDie) {
                return;
            }
            if (this.group != null) {
                if (this.group.memberGroups.get(0).charId == this.id) {
                    String name = ms.readUTF();
                    if (Info.name.equals(name)) {
                        service.serverMessage("Không thể nhường cho người này");
                    }
                    int index = this.group.getIndexByName(name);
                    if (index > 0)
                        this.group.changeLeader(index);
                } else {
                    service.serverMessage("Bạn không phải trưởng nhóm");
                }
            }

        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void chatPrivate(Message ms) {
        try {
            String to = ms.readUTF();
            if (to == null || to.length() > 300) {
                return;
            }

            String[] parts = to.split(":"); // Tách chuỗi dựa vào ký tự ':'
            if (parts.length < 2) {
                getService().serverMessage("Nội dung không đúng");
                return;
            }

            // Kiểm tra xem chuỗi có đủ dài để thực hiện substring(1) hay không
            if (parts[0].length() < 2) {
                getService().serverMessage("Tên người nhận không hợp lệ");
                return;
            }

            String name = parts[0].substring(1); // Loại bỏ ký tự '/' đầu tiên và lấy phần còn lại
            Char _char = ServerManager.findCharByName(name);
            if (_char == null || this.Info.name.equals(name)) {
                getService().serverMessage("Đối phương đã offline");
                return;
            }

            _char.getService().chatPrivate(Info.name, parts[1], name);
            getService().chatPrivate(Info.name, parts[1], name);
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }


    public void addFriend(Message ms) {
        try {
            String name = ms.readUTF();
            Char _char = ServerManager.findCharByName(name);

            if (_char == null) {
                service.serverMessage("Người này không online hoặc không tồn tại!");
                return;
            }
            if (_char == this) {
                return;
            }
            Friend friend = friends.get(name);
            if (friend != null) {
                service.serverMessage(name + " đã có trong danh sách bạn bè.");
                return;
            }
            Friend me = _char.friends.get(Info.name);
            if (me != null) {
                me.type = 1;
                friends.put(_char.Info.name, new Friend(_char.Info.name, (byte) 1, true));
                getService().addFriend(name, 1, true);
                _char.getService().removeFriend(Info.name);
                getService().removeFriend(name);
                _char.getService().addFriend(Info.name, 1, true);
                return;
            } else {
                friends.put(_char.Info.name, new Friend(_char.Info.name, (byte) 0, false));
            }
            getService().addFriend(name, 0, false);
            _char.getService().inviteFriend(Info.name, 2);
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void removeFriend(Message ms) {
        try {
            String name = ms.readUTF();
            Char _char = ServerManager.findCharByName(name);
            if (this.friends.get(name) != null) {
                if (_char != null) {
                    this.friends.remove(name);
                    getService().removeFriend(name);
                    if (_char.friends.get(Info.name) != null) {
                        _char.friends.remove(Info.name);
                    }
                    _char.getService().removeFriend(Info.name);
                }
            } else {
                getService().removeFriend(name);
                if (_char != null) {
                    _char.getService().removeFriend(Info.name);
                    if (_char.friends.get(Info.name) != null) {
                        _char.friends.remove(Info.name);
                    }
                }
            }
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }

    }

    public int getIndexItemByIdInBag(int itemId) {
        for (Item item : this.Bag.arrItemBag) {
            if (item != null && item.id == itemId) {
                return item.index;
            }
        }
        return -1;
    }

    public EventPoint getEventPoint() {
        return eventPoint;
    }

    public void warningBagFull() {
        service.alertMessage("Hành trang không đủ chỗ trống");
    }

    public void orderTaskDay() {
        int idEntity = zone.findIdentityNpc(102);
        for (TaskOrder task : this.taskOrders) {
            if (task.taskId == TaskOrder.TASK_DAY) {
                getService().npcChat(idEntity, "Nhiệm vụ lần trước ta giao, con vẫn chưa hoàn thành.");
                return;
            }
        }
        if (this.level() < 20) {
            getService().npcChat(idEntity, "Con hãy có gắng luyện tập đạt cấp 20 rồi quay lại đây gặp ta.");
            return;
        }
        if (countFinishDay <= 0) {
            getService().npcChat(idEntity,
                    "Hôm nay con đã làm hết nhiệm vụ ta giao. Hãy quay lại vào ngày hôm sau..");
            return;
        }
        TaskOrder task = TaskFactory.getInstance().createTaskOrder(TaskOrder.TASK_DAY, this);
        if (task != null) {
            this.countFinishDay--;
            getService().sendTaskOrder(task);
            taskOrders.add(task);
            getService().npcChat(idEntity, "Đây là lần nhận nhiệm vụ thứ " + (10 - countFinishDay)
                    + " trong ngày hôm nay. Mỗi ngày được nhận tối đa 10 lần con nhé.");
        }
    }

    public void cancelTaskDay() {
        int idEntity = zone.findIdentityNpc(102);
        for (TaskOrder task : this.taskOrders) {
            if (task.taskId == TaskOrder.TASK_DAY) {
                getService().resetTaskOrder(0);
                taskOrders.remove(task);
                getService().npcChat(idEntity,
                        "Ta đã hủy nhiệm vụ của con. Lần sau cố gắng hoàn thành tốt nhiệm vụ con nhé.");
                return;
            }
        }
        getService().npcChat(idEntity, "Hiện tại con không có nhiệm vụ để hủy.");
    }

    public void finishTaskDay() {
        int idEntity = zone.findIdentityNpc(102);
        for (TaskOrder task : this.taskOrders) {
            if (task.taskId == TaskOrder.TASK_DAY) {
                if (task.isComplete()) {
                    getService().npcChat(idEntity, "Tốt lắm! Ta có phần thưởng dành cho con.");
                    this.Info.chuyenCan += 5;
                    this.Info.chuyenCanTuan += 5;
                    addBacKhoa(500000);
                    if (this.clan != null) {
                        this.addClanPoint(10);
                    }
                    addExp(5000000);
                    this.user.session.sendMessage(HanderMessage.SendThongBao("Bạn nhận được 5.000.000 Exp, 5 điểm chuyên cần, 10 điểm cống hiến gia tộc", HanderMessage.YELLOW_MID));
                    getService().resetTaskOrder(0);
                    taskOrders.remove(task);
                    msgUpdateDataChar();
                    if (taskId == 22 && taskMain != null && taskMain.index == 1 && countFinishDay == 0) {
                        updateTaskCount(1);
                    }
                } else {
                    getService().npcChat(idEntity, "Con hãy hoàn thành nhiệm vụ rồi quay lại đây.");
                }
                return;
            }
        }
        getService().npcChat(idEntity,
                "Hãy làm việc cho ta bằng cách làm nhiệm vụ, sau khi hoàn thành con sẽ nhận được phần thưởng từ ta.");
    }

    public void orderTaskBoss() {
        int idEntity = zone.findIdentityNpc(98);
        for (TaskOrder task : this.taskOrders) {
            if (task.taskId == TaskOrder.TASK_BOSS) {
                getService().npcChat(idEntity, "Nhiệm vụ lần trước ta giao, con vẫn chưa hoàn thành.");
                return;
            }
        }
        if (this.level() < 20) {
            getService().npcChat(idEntity, "Con hãy có gắng luyện tập đạt cấp 20 rồi quay lại đây gặp ta.");
            return;
        }
        if (countLoopBoss <= 0) {
            getService().npcChat(idEntity,
                    "Nhiệm vụ truy bắt manh thu của ngày hôm nay đã hết con hãy quay lại vào ngày mai.");
            return;
        }
        TaskOrder task = TaskFactory.getInstance().createTaskOrder(TaskOrder.TASK_BOSS, this);
        if (task != null) {
            this.countLoopBoss--;
            getService().sendTaskBoss(task);
            taskOrders.add(task);
            getService().npcChat(idEntity, "Con hay sớm hoàn thành nhiệm vụ để nhận thưởng từ ta.");
        }
    }

    public void cancelTaskBoss() {
        int idEntity = zone.findIdentityNpc(98);
        for (TaskOrder task : this.taskOrders) {
            if (task.taskId == TaskOrder.TASK_BOSS) {
                getService().resetTaskOrder(task.taskId);
                taskOrders.remove(task);
                getService().npcChat(idEntity,
                        "Ta đã hủy nhiệm vụ của con. Lần sau cố gắng hoàn thành tốt nhiệm vụ con nhé.");
                return;
            }
        }
        getService().npcChat(idEntity, "Hiện tại con không có nhiệm vụ để hủy.");
    }

    public void finishTaskBoss() {
        int idEntity = zone.findIdentityNpc(98);
        for (TaskOrder task : this.taskOrders) {
            if (task.taskId == TaskOrder.TASK_BOSS) {
                if (task.isComplete()) {
                    getService().npcChat(idEntity, "Tốt lắm! Ta có phần thưởng dành cho con.");
                    this.Info.chuyenCan += 10;
                    this.Info.chuyenCanTuan += 10;
                    addBacKhoa(1000000);
                    if (this.clan != null) {
                        this.addClanPoint(5);
                    }
                    getService().resetTaskOrder(task.taskId);
                    msgUpdateDataChar();
                    taskOrders.remove(task);
                    if (taskId == 22 && taskMain != null && taskMain.index == 0) {
                        taskNext();
                    }
                } else {
                    getService().npcChat(idEntity, "Con hãy hoàn thành nhiệm vụ rồi quay lại đây.");
                }
                return;
            }
        }
        getService().npcChat(idEntity,
                "Hãy làm việc cho ta bằng cách làm nhiệm vụ, sau khi hoàn thành con sẽ nhận được phần thưởng từ ta.");
    }

    public void moveTo(String name) {
        if (name.equals(this.Info.name)) {
            service.serverMessage("Không thể dịch chuyển đến chính mình.");
            return;
        }
        if (zone.isWorld()) {
            service.serverMessage("Không thể sử dụng tính năng này.");
            return;
        }
        if (zone.isLoiDai()) {
            service.serverMessage("Không thể sử dụng tính năng này.");
            return;
        }
        long now = System.currentTimeMillis();
        int second = (int) ((now - lastTimeTeleport) / 1000);
        if (second < 20) {
            service.serverMessage("Chỉ có thể sử dụng sau " + (20 - second) + " giây");
            return;
        }
        Char _char = ServerManager.findCharByName(name);
        if (_char != null) {
            if (_char.zone.isWorld()) {
                service.serverMessage("Nơi này quá nguy hiểm. Bạn không thể di chuyển tới!");
                return;
            }
            if (_char.zone.isLoiDai()) {
                service.serverMessage("Không thể sử dụng tính năng này.");
                return;
            }
            try {
                if (this.zone != _char.zone) {
                    _char.zone.addChar(this);
                }
                setXY(_char.Info.cx, _char.Info.cy);
                this.service.setXYChar();
                lastTimeTeleport = now;
            } catch (Exception ex) {
                Log.error("err: " + ex.getMessage(), ex);
            }
        } else {
            service.serverMessage("Hiện tại người này không online.");
        }
    }

    public void setXY(short x, short y) {
        Info.cx = x;
        Info.cy = y;
    }

    public void security(Message msg) {
        try {
            byte type = msg.readByte();
            String text = msg.readUTF();
            switch (type) {
                case 0://quen ma
                    if (!isSecurity) {
                        service.serverMessage("Bạn chưa có mã bảo vệ không thể quên ma");
                        return;
                    }
                    timeRemoveSecurity = (int) ((System.currentTimeMillis() + TimeUnit.DAYS.toMillis(3)) / 1000);
                    service.sendSecurity();
                    break;
                case 1://dat ma
                    if (isSecurity || isUnlockSecurity) {
                        service.serverMessage("Bạn đang có mã bảo vệ không thể tạo mới");
                        return;
                    }
                    if (text.isEmpty()) {
                        service.serverMessage("Vui lòng nhập vào ký tự hoặc số");
                        return;
                    }
                    passwordSecurity = text;
                    isSecurity = true;
                    service.serverMessage("Đã đặt mã bảo vệ thành công");
                    service.resetScreen();
                    break;
                case 2:// mo khoa
                    if (!isSecurity) {
                        service.serverMessage("Bạn chưa có mã bảo vệ không thể mở khoá");
                        return;
                    }
                    if (!passwordSecurity.equalsIgnoreCase(text)) {
                        service.serverMessage("Mã bảo vệ không chính xác");
                        return;
                    } else {
                        service.serverMessage("Đã mở khoá mã bảo vệ thành công");
                        isUnlockSecurity = true;
                        timeRemoveSecurity = 0;
                        service.resetScreen();
                    }
                    break;
                case 3:// xoa ma
                    if (!isSecurity) {
                        service.serverMessage("Bạn chưa có mã bảo vệ không thể xoá");
                        return;
                    }
                    if (!isUnlockSecurity) {
                        service.serverMessage("Vui lòng mở khoá trước khi xoá mã bảo vệ");
                        return;
                    }
                    isUnlockSecurity = false;
                    isSecurity = false;
                    passwordSecurity = "";
                    service.serverMessage("Đã xoá mã bảo vệ thành công");
                    service.resetScreen();
                    break;
            }
        } catch (IOException e) {
        }
    }

    public void inviteTyVo(Message msg) {
        try {
            if (InfoGame.isDie) {
                return;
            }
            if (idCharPk != -1) {
                return;
            }
            String name = msg.readUTF();
            Char pl = ServerManager.findCharByName(name);
            if (pl != null) {
                if (pl.idCharPk != -1) {
                    return;
                }
                pl.getService().sendTyVo(Info.name);
                service.serverMessage("Đã gửi lời mời tỷ võ tới " + name);
            }
        } catch (IOException e) {

        }
    }

    public void acceptTyVo(Message msg) {
        try {
            if (InfoGame.isDie) {
                return;
            }
            String name = msg.readUTF();
            Char pl = ServerManager.findCharByName(name);
            if (pl != null) {
                pl.InfoGame.TypePk = 1;
                InfoGame.TypePk = 1;
                service.startTyVo(pl.id);
                pl.service.startTyVo(id);
                pl.idCharPk = id;
                idCharPk = pl.id;
                isTyVo = true;
                pl.isTyVo = true;
            }
        } catch (IOException e) {

        }
    }

    public void cancelTyvo(Message msg) {
        try {
            if (InfoGame.isDie) {
                return;
            }
            String name = msg.readUTF();
            Char pl = ServerManager.findCharByName(name);
            if (pl != null) {
                pl.getService().serverMessage("Đối phương đã từ chối lời mời tỷ võ của bạn");
            }
        } catch (IOException e) {

        }
    }

    public void subMenu(Message msg) {
        try {
            String text = msg.readUTF();
            short type = msg.readShort();
            int var = msg.readInt();
            if (InfoGame.isDie) {
                return;
            }
            if (text.isEmpty()) {
                service.warningMessage("Ký tự nhập vào không hợp lệ");
                return;
            }
            switch (type) {
                case DataCmd.CHANGE_NAME:
                    if (clan != null) {
                        service.alertMessage("Vui lòng thoát gia tộc trước khi đổi");
                        return;
                    }
                    if (timeChangeName > System.currentTimeMillis()) {
                        service.serverMessage("Bạn vừa mới đổi tên vui lòng đợi hết thời gian , Thời gian chờ của bạn tới " + TimeUnit.MILLISECONDS.toHours(timeChangeName));
                        return;
                    }
                    if (text.matches(".*\\s+.*")) {
                        service.sendMessage(HanderMessage.SendThongBao("Tên nhân vật không được chứa ký tự đặc biệt va khoảng trắng", HanderMessage.RED_MID));
                        break;
                    }
                    if (!Utlis.CheckString(text)) {
                        service.sendMessage(HanderMessage.SendThongBao("Tên nhân vật không được chứa ký tự đặc biệt", HanderMessage.RED_MID));
                        break;
                    }
                    if (CharDB.getCharByName(text) != null) {
                        service.sendMessage(HanderMessage.SendThongBao("Đã có tên nhân vật này", HanderMessage.RED_MID));
                        break;
                    }
                    if (text.length() < 5) {
                        service.sendMessage(HanderMessage.SendThongBao("Tên nhân vật phải có tối thiểu 5 ký tự", HanderMessage.RED_MID));
                        break;
                    }
                    try {

                        PreparedStatement stmt = Connect.getConnection()
                                .prepareStatement("SELECT * FROM `player` WHERE `Name` = ? LIMIT 1;", ResultSet.TYPE_SCROLL_SENSITIVE,
                                        ResultSet.CONCUR_READ_ONLY);
                        stmt.setString(1, text);
                        ResultSet data = stmt.executeQuery();
                        if (data.first()) {
                            service.alertMessage("Tên đã tồn tại, vui lòng chọn một tên khác.");
                            return;
                        }

                        PreparedStatement stmt2 = Connect.getConnection()
                                .prepareStatement("UPDATE `player` SET `Name` = ? WHERE `IdChar` = ? LIMIT 1;");
                        try {

                            stmt2.setString(1, text);
                            stmt2.setInt(2, this.id);
                            stmt2.executeUpdate();
                        } finally {
                            stmt2.close();
                        }
                        Info.name = text;
                        // Item item = ItemFactory.getInstance().newItem(ItemName.THE_DOI_TEN);
                        Item thedoiten = FindItemBag(437);
                        removeItem(thedoiten);
                        msgRemoveItemBag(thedoiten);
                        timeChangeName = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(15);
                        service.resetScreen();
                        service.alertMessage(String.format("Thay đổi thành công, tên mới của bạn là: %s.", text));
                        service.alertMessage("Tự Động Thoát Sau 1s.");
                        Utlis.writing("LogChangeName.txt", "Player " + Info.name + " đổi thành tên: " + text + "\n");
                        int TimeSeconds = 3;
                        while (TimeSeconds > 0) {
                            TimeSeconds--;
                            try {
                                Thread.sleep(500L);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(Char.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        if (!isClean) {
                            user.session.clean();
                        }
                        service.alertMessage("Vui lòng thoát game để hoàn thành đổi tên.");
                    } catch (Exception e) {
                        e.printStackTrace();
                        service.alertMessage(String.format("Thay đổi thất bại.", text));
                    }
                    break;
            }
        } catch (IOException e) {

        }

    }

    public void betArena(int coin) {
        Arena arena = (Arena) findWorld(World.ARENA);
        if (arena == null) {
            return;
        }
        if (coin < 1000) {
            getService().serverMessage("Tối thiểu phải là 1.000 Bạc.");
            return;
        }
        if (coin > 1000000) {
            getService().serverMessage("Tối đa là 1.000.000 Bạc.");
            return;
        }
        if (coin > this.Bag.bac) {
            service.alertMessage("Bạn không có đủ bạc");
            return;
        }
        int team = arena.getTeam(this);
        arena.setMoney(team, coin);
    }

    public void inviteArena(String name) {
        if (name.equals(this.Info.name)) {
            getService().serverMessage("Không thể thách đấu bản thân.");
            return;
        }
        if (!user.actived) {
            service.serverMessage("Bạn chưa kích hoạt tài khoản");
            return;
        }
        Char _char = zone.findCharName(name);
        if (_char != null) {
            try {
                if (this.group != null) {
                    getService().alertMessage("Out nhóm trước khi thách đấu");
                    return;
                }
                if (_char.group != null) {
                    getService().alertMessage("Đối phương đang có nhóm không thể thách đấu");
                    return;
                }
                if (!_char.user.actived) {
                    service.serverMessage("Đối thủ chưa kích hoạt tài khoản");
                    return;
                }
                Invite.PlayerInvite p = _char.invite.findCharInvite(Invite.PK, Info.name);
                if (p != null) {
                    getService().alertMessage("Không thể mời thách đấu liên tục. Vui lòng thử lại sau 30s nữa.");
                    return;
                }
                _char.getService().inviteLoiDai(Info.name);
                _char.invite.addCharInvite(Invite.PK, Info.name, 30);
                getService().serverMessage("Ta đã gởi lời mời thách đấu đến " + name);
            } catch (Exception e) {
                Log.error("err: " + e.getMessage(), e);
            }
        } else {
            getService().alertMessage("Hiện tại " + name + " không có mặt ở đây.");
        }
    }

    public void acceptInviteLoiDai(Message ms) {
        try {
            if (!user.actived) {
                service.serverMessage("Bạn chưa kích hoạt tài khoản");
                return;
            }
            String name = ms.readUTF();
            if (invite.findCharInvite(Invite.PK, name) != null) {
                Char _char = zone.findCharName(name);
                if (_char != null) {
                    Arena arena = new Arena();
                    this.addWorld(arena);
                    arena.join(1, this);
                    _char.addWorld(arena);
                    arena.join(2, _char);
                    arena.isTwoTeamsEtered = true;
                } else {
                    service.alertMessage("Hiện tại người này không có mặt ở đây.");
                }
            } else {
                service.alertMessage("Đã hết thời gian để chấp nhận yêu cầu.");
            }
        } catch (Exception e) {
            Log.error("err: " + e.getMessage(), e);
        }
    }

    public void menu_MSG5(byte option) {
        switch (typeMenu) {
            case TYPEMENU.RENEW_ITEM:
                break;
            case TYPEMENU.HOAN_THANH_NHIEM_VU:
                getService().sendMessage(new Message((byte) 5));
                getService().sendSTRtask();
                break;
            case TYPEMENU.LAM_NHIEM_VU:
                if (taskId == TaskName.NV_LAM_NGUOI_TOT_BUNG) {
                    if (taskMain.index == 2) {
                        updateTakingStep(2);
                    }
                }
                if (taskId == TaskName.NV_TRO_GIUP_LANG_SUONG_MU) {
                    if (taskMain.index == 1) {
                        updateTakingStep(taskMain.index);
                    }
                }
                if (taskId == TaskName.NV_BAT_KE_NGHE_LEN) {
                    if (taskMain.index == 3) {
                        updateTakingStep(taskMain.index);
                    }
                }
                if (taskId == TaskName.NV_KE_DICH_LO_DIEN) {
                    if (taskMain.index == 0 || taskMain.index == 2) {
                        updateTakingStep(taskMain.index);
                    } else if (taskMain.index == 1) {
                        updateTaskCount(1);
//                        if (createMob) {
//                            service.serverMessage("Bạn đã triệu hội quái vật rồi không thể gọi tiếp , nếu sảy ra lỗi có thể out game vào lại");
//                            return;
//                        }
//                        createMob = true;
//                        getService().sendMessage(new Message((byte) 5));
//                        getService().sendTaskStep(1);
//                        Mob mob = Mob.mobTask((short) 222, "", Info.cy, Info.cx);
//                        mob.hp = mob.hpGoc = mob.hpFull = level() * 20;
//                        zone.monsters.add(mob);
//                        service.sendMessage(HanderMessage.AddMob(mob));
                    }
                }
                if (taskId == TaskName.NV_TRAN_CHIEN_SONG_CON) {
                    if (taskMain.index == 0 || taskMain.index == 2) {
                        updateTakingStep(taskMain.index);
                    } else if (taskMain.index == 1) {
                        updateTaskCount(1);
//                        if (createMob) {
//                            service.serverMessage("Bạn đã triệu hội quái vật rồi không thể gọi tiếp , nếu sảy ra lỗi có thể out game vào lại");
//                            return;
//                        }
//                        createMob = true;
//                        getService().sendMessage(new Message((byte) 5));
//                        getService().sendTaskStep(1);
//                        Mob mob = Mob.mobTask((short) 224, "", Info.cy, Info.cx);
//                        mob.hp = mob.hpGoc = mob.hpFull = level() * 20;
//                        zone.monsters.add(mob);
//                        service.sendMessage(HanderMessage.AddMob(mob));
                    }
                }
                if (taskId == TaskName.NV_NOI_BUON_CUA_IDATE) {
                    if (taskMain.index == 0 || taskMain.index == 2) {
                        updateTakingStep(taskMain.index);
                    } else if (taskMain.index == 1) {
                        updateTaskCount(1);
//                        if (createMob) {
//                            service.serverMessage("Bạn đã triệu hội quái vật rồi không thể gọi tiếp , nếu sảy ra lỗi có thể out game vào lại");
//                            return;
//                        }
//                        createMob = true;
//                        getService().sendMessage(new Message((byte) 5));
//                        getService().sendTaskStep(1);
//                        Mob mob = Mob.mobTask((short) 226, "", Info.cy, Info.cx);
//                        mob.hp = mob.hpGoc = mob.hpFull = level() * 20;
//                        zone.monsters.add(mob);
//                        service.sendMessage(HanderMessage.AddMob(mob));
                    }
                }
                if (taskId == TaskName.NV_KE_THU_LANG_CAT) {
                    if (taskMain.index == 0 || taskMain.index == 2) {
                        updateTakingStep(taskMain.index);
                    } else if (taskMain.index == 1) {
                        updateTaskCount(1);
//                        if (createMob) {
//                            service.serverMessage("Bạn đã triệu hội quái vật rồi không thể gọi tiếp , nếu sảy ra lỗi có thể out game vào lại");
//                            return;
//                        }
//                        createMob = true;
//                        getService().sendMessage(new Message((byte) 5));
//                        getService().sendTaskStep(1);
//                        Mob mob = Mob.mobTask((short) 234, "", Info.cy, Info.cx);
//                        mob.hp = mob.hpGoc = mob.hpFull = level() * 20;
//                        zone.monsters.add(mob);
//                        service.sendMessage(HanderMessage.AddMob(mob));
                    }
                }
                if (taskId == TaskName.NV_GIAO_LUU_VO_CONG_VOI_GAARA) {
                    if (taskMain.index == 1) {
                        updateTakingStep(taskMain.index);
                    } else if (taskMain.index == 0) {
                        updateTaskCount(1);
//                        if (createMob) {
//                            service.serverMessage("Bạn đã triệu hội quái vật rồi không thể gọi tiếp , nếu sảy ra lỗi có thể out game vào lại");
//                            return;
//                        }
//                        createMob = true;
//                        getService().sendMessage(new Message((byte) 5));
//                        getService().sendTaskStep(1);
//                        Mob mob = Mob.mobTask((short) 227, "", Info.cy, Info.cx);
//                        mob.hp = mob.hpGoc = mob.hpFull = level() * 20;
//                        zone.monsters.add(mob);
//                        service.sendMessage(HanderMessage.AddMob(mob));
                    } else if (taskMain.index == 2) {
                        updateTaskCount(1);
//                        if (createMob) {
//                            service.serverMessage("Bạn đã triệu hội quái vật rồi không thể gọi tiếp , nếu sảy ra lỗi có thể out game vào lại");
//                            return;
//                        }
//                        createMob = true;
//                        getService().sendMessage(new Message((byte) 5));
//                        getService().sendTaskStep(1);
//                        Mob mob = Mob.mobTask((short) 236, "", Info.cy, Info.cx);
//                        mob.hp = mob.hpGoc = mob.hpFull = level() * 20;
//                        zone.monsters.add(mob);
//                        service.sendMessage(HanderMessage.AddMob(mob));
                    }
                }
                if (taskId == TaskName.NV_HOAN_TRA_BAO_VAT) {
                    if (taskMain.index == 1) {
                        updateTakingStep(taskMain.index);
                        Item thu = new Item(400);
                        thu.isLock = true;
                        addItem(thu);
                        msgAddItemBag(thu);
                    } else if (taskMain.index == 2) {
                        updateTakingStep(taskMain.index);
                        Item thu = FindItemBag(400);
                        if (thu != null) {
                            removeItem(thu, true);
                            msgRemoveItemBag(thu);
                        }
                    } else if (taskMain.index == 3) {
                        updateTakingStep(taskMain.index);
                    } else if (taskMain.index == 4) {
                        updateTakingStep(taskMain.index);
                        Item thu = FindItemBag(236);
                        if (thu != null) {
                            removeItem(thu, true);
                            msgRemoveItemBag(thu);
                        }
                    } else if (taskMain.index == 5) {
                        updateTakingStep(taskMain.index);
                        Item thu = new Item(398);
                        thu.isLock = true;
                        addItem(thu);
                        msgAddItemBag(thu);
                    } else if (taskMain.index == 6) {
                        updateTakingStep(taskMain.index);
                        Item thu = FindItemBag(398);
                        if (thu != null) {
                            removeItem(thu, true);
                            msgRemoveItemBag(thu);
                        }
                    } else if (taskMain.index == 7) {
                        updateTakingStep(taskMain.index);
                        Item thu = new Item(399);
                        thu.isLock = true;
                        addItem(thu);
                        msgAddItemBag(thu);
                    } else if (taskMain.index == 8) {
                        updateTakingStep(taskMain.index);
                        Item thu = FindItemBag(399);
                        if (thu != null) {
                            removeItem(thu, true);
                            msgRemoveItemBag(thu);
                        }
                    }
                }
                if (taskId == TaskName.NV_CAU_CUU_VIEN_BINH) {
                    getService().sendMessage(new Message((byte) 5));
                    Item itemRemove = FindItemBag(386 + taskMain.index);
                    if (itemRemove != null) {
                        removeItem(itemRemove, true);
                        msgRemoveItemBag(itemRemove);
                    }
                    updateTakingStep(taskMain.index);
                    if (taskMain.index < 5) {
                        int itemId = 386 + taskMain.index;
                        Item item = new Item(itemId);
                        item.isLock = true;
                        addItem(item);
                        msgAddItemBag(item);
                    }
                }
                if (taskId == TaskName.NV_TIM_UDON) {
                    if (taskMain.index == 0) {
                        updateTakingStep(0);
                    } else if (taskMain.index == 1) {
                        getService().sendMessage(new Message((byte) 5));
                        getService().sendTaskStep(1);
                        updateTaskCount(1);
//                        String[] segments = taskMain.vStep.get(taskMain.index).STR_ITEM.split("\\{");
//                        if (isDatTre) {
//                            getService().serverMessage("Bạn đang dắt trẻ rồi , không thẻ dắt thêm nếu lỗi hãy thoát game vào lại");
//                            return;
//                        }
//                        isDatTre = true;
//                        Mob mob = Mob.mobTask((short) 221, Info.name, Info.cy, Info.cx);
//                        zone.monsters.add(mob);
//                        getService().sendMessage(HanderMessage.AddMob(mob));
//                        int i = 0;
//                        long timestart = System.currentTimeMillis();
//                        while (!Main.BaoTri) {
//                            if (timestart + 3000 * i < System.currentTimeMillis()) {
//                                String segment = segments[i].trim().replace("}", "");
//                                if (!segment.isEmpty()) {
//                                    String[] xy = segment.split(",");
//                                    short x = Short.parseShort(xy[0]);
//                                    short y = Short.parseShort(xy[1]);
//                                    getService().sendMessage(HanderMessage.MoveMob(mob.idEntity, x, y));
//                                    getService().mobChat(mob.idEntity, "Về nhà thôi!!!!");
//                                }
//                                i += 1;
//                                if (i == segments.length) {
//                                    updateTaskCount(1);
//                                    getService().sendMessage(HanderMessage.RemoveMob(mob.idEntity));
//                                    isDatTre = false;
//                                    if (zone != null)
//                                        zone.removeMob(mob.idEntity);
//                                    return;
//                                }
//
//                            }
//
//                        }

                    }
                }
                if (taskId == TaskName.NV_GIAI_CUU_INARI) {
                    if (taskMain.index == 1) {
                        updateTakingStep(taskMain.index);
                    } else if (taskMain.index == 2) {
                        getService().sendMessage(new Message((byte) 5));
                        getService().sendTaskStep(1);
                        updateTaskCount(1);
//                        String[] segments = taskMain.vStep.get(taskMain.index).STR_ITEM.split("\\{");
//                        if (isDatTre) {
//                            getService().serverMessage("Bạn đang dắt trẻ rồi , không thẻ dắt thêm nếu lỗi hãy thoát game vào lại");
//                            return;
//                        }
//                        isDatTre = true;
//                        Mob mob = Mob.mobTask((short) 223, Info.name, Info.cy, Info.cx);
//                        zone.monsters.add(mob);
//                        getService().sendMessage(HanderMessage.AddMob(mob));
//                        int i = 0;
//                        long timestart = System.currentTimeMillis();
//                        while (!Main.BaoTri) {
//                            if (timestart + 3000 * i < System.currentTimeMillis()) {
//                                String segment = segments[i].trim().replace("}", "");
//                                if (!segment.isEmpty()) {
//                                    String[] xy = segment.split(",");
//                                    short x = Short.parseShort(xy[0]);
//                                    short y = Short.parseShort(xy[1]);
//                                    getService().sendMessage(HanderMessage.MoveMob(mob.idEntity, x, y));
//                                    getService().mobChat(mob.idEntity, "Về nhà thôi!!!!");
//                                }
//                                i += 1;
//                                if (i == segments.length) {
//                                    updateTaskCount(1);
//                                    getService().sendMessage(HanderMessage.RemoveMob(mob.idEntity));
//                                    isDatTre = false;
//                                    if (zone != null)
//                                        zone.removeMob(mob.idEntity);
//                                    return;
//                                }
//
//                            }
//
//                        }

                    }
                }
                if (taskId == TaskName.NV_LE_HOI_TODOROKI_TAISHA) {
                    if (taskMain.index == 0 || taskMain.index == 2) {
                        updateTakingStep(taskMain.index);
                    } else if (taskMain.index == 1) {
                        getService().sendMessage(new Message((byte) 5));
                        getService().sendTaskStep(1);
                        updateTaskCount(1);
//                        String[] segments = taskMain.vStep.get(taskMain.index).STR_ITEM.split("\\{");
//                        if (isDatTre) {
//                            getService().serverMessage("Bạn đang dắt trẻ rồi , không thẻ dắt thêm nếu lỗi hãy thoát game vào lại");
//                            return;
//                        }
//                        isDatTre = true;
//                        Mob mob = Mob.mobTask((short) 225, Info.name, Info.cy, Info.cx);
//                        zone.monsters.add(mob);
//                        getService().sendMessage(HanderMessage.AddMob(mob));
//                        int i = 0;
//                        long timestart = System.currentTimeMillis();
//                        while (!Main.BaoTri) {
//                            if (timestart + 3000 * i < System.currentTimeMillis()) {
//                                String segment = segments[i].trim().replace("}", "");
//                                if (!segment.isEmpty()) {
//                                    String[] xy = segment.split(",");
//                                    short x = Short.parseShort(xy[0]);
//                                    short y = Short.parseShort(xy[1]);
//                                    getService().sendMessage(HanderMessage.MoveMob(mob.idEntity, x, y));
//                                    getService().mobChat(mob.idEntity, "Về nhà thôi!!!!");
//                                    setXY(x, y);
//                                    getService().setXYChar();
//                                }
//
//                                i += 1;
//                                if (i == segments.length) {
//                                    updateTaskCount(1);
//                                    getService().sendMessage(HanderMessage.RemoveMob(mob.idEntity));
//                                    if (zone != null)
//                                        zone.removeMob(mob.idEntity);
//                                    isDatTre = false;
//                                    return;
//                                }
//
//                            }
//
//                        }

                    }
                }
                if (taskId == TaskName.NV_GIUP_DO_GAARA) {
                    if (taskMain.index == 0) {
                        updateTakingStep(taskMain.index);
                    } else if (taskMain.index == 1) {
                        getService().sendMessage(new Message((byte) 5));
                        getService().sendTaskStep(1);
                        updateTaskCount(1);
//                        String[] segments = taskMain.vStep.get(taskMain.index).STR_ITEM.split("\\{");
//                        if (isDatTre) {
//                            getService().serverMessage("Bạn đang dắt trẻ rồi , không thẻ dắt thêm nếu lỗi hãy thoát game vào lại");
//                            return;
//                        }
//                        isDatTre = true;
//                        Mob mob = Mob.mobTask((short) 228, Info.name, Info.cy, Info.cx);
//                        zone.monsters.add(mob);
//                        getService().sendMessage(HanderMessage.AddMob(mob));
//                        int i = 0;
//                        long timestart = System.currentTimeMillis();
//                        while (!Main.BaoTri) {
//                            if (timestart + 3000 * i < System.currentTimeMillis()) {
//                                String segment = segments[i].trim().replace("}", "");
//                                if (!segment.isEmpty()) {
//                                    String[] xy = segment.split(",");
//                                    short x = Short.parseShort(xy[0]);
//                                    short y = Short.parseShort(xy[1]);
//                                    getService().sendMessage(HanderMessage.MoveMob(mob.idEntity, x, y));
//                                    getService().mobChat(mob.idEntity, "Về nhà thôi!!!!");
//                                    setXY(x, y);
//                                    getService().setXYChar();
//                                }
//
//                                i += 1;
//                                if (i == segments.length) {
//                                    updateTaskCount(1);
//                                    getService().sendMessage(HanderMessage.RemoveMob(mob.idEntity));
//                                    if (zone != null)
//                                        zone.removeMob(mob.idEntity);
//                                    isDatTre = false;
//                                    return;
//                                }
//
//                            }
//
//                        }

                    }
                }
                if (taskId == TaskName.NV_NHAN_GIA_HOC_VIEN
                        || taskId == TaskName.NV_NHIEM_VU_DAU_TIEN
                        || taskId == TaskName.NV_CHUA_LANH_VET_THUONG
                        || taskId == TaskName.NV_XOA_BO_CAM_THUAT) {
                    if (taskMain.index == 8 && taskId == TaskName.NV_NHAN_GIA_HOC_VIEN) {
                        if (Bag.arrItemBody[1] != null) {
                            getService().sendMessage(HanderMessage.SendThongBao("Vui lòng cất vũ khí trước khi nhập học", HanderMessage.WHITE));
                            return;
                        }
                        getService().sendTaskStep(8);
                        taskNext();
                        Info.idClass = getLopFormSelectChar(Info.idChar);
                        service.sendChar();
                        Skill = new InfoSkill(Info.idClass);
                        msgUpdateDataChar();
                        msgUpdateSkill();
                        Item vk = new Item(IdVK1x(Info.idClass));
                        vk.he = Info.idhe;
                        Item.setOptionsVuKhiToBag(vk, Info.idClass);
                        vk.isLock = true;
                        addItem(vk);
                        msgAddItemBag(vk);
                    } else
                        updateTakingStep(taskMain.index);
                    if (taskId == TaskName.NV_CHUA_LANH_VET_THUONG) {
                        Item binhNuoc = new Item(205);
                        binhNuoc.amount = 5;
                        binhNuoc.isLock = true;
                        addItem(binhNuoc);
                        msgAddItemBag(binhNuoc);
                    }

                }
                break;
            case TYPEMENU.KI_THI_CHUNNIN:
                if (taskMain.index == 0 || taskMain.index == 2) {
                    updateTakingStep(taskMain.index);
                } else if (taskMain.index == 1) {
                    int id = zone.findIdentityNpc(96);
                    switch (taskMain.count) {
                        case 0:
                            if (option == 1) {
                                updateTaskCount(1);
                                anserChunnin();
                                service.npcChat(id, "Trả lời chính xác");
                            } else {
                                service.npcChat(id, "Trả lời sai");
                            }
                            return;
                        case 1:
                            if (option == 0) {
                                updateTaskCount(1);
                                anserChunnin();
                                service.npcChat(id, "Trả lời chính xác");
                            } else {
                                service.npcChat(id, "Trả lời sai");
                            }
                            return;
                        case 2:
                            if (option == 2) {
                                updateTaskCount(1);
                                anserChunnin();
                                service.npcChat(id, "Trả lời chính xác");
                            } else {
                                service.npcChat(id, "Trả lời sai");
                            }
                            return;
                        case 3:
                            if (option == 3) {
                                updateTaskCount(1);
                                anserChunnin();
                                service.npcChat(id, "Trả lời chính xác");
                            } else {
                                service.npcChat(id, "Trả lời sai");
                            }
                            return;
                        case 4:
                            if (option == 1) {
                                updateTaskCount(1);
                                service.npcChat(id, "Trả lời chính xác");
                            } else {
                                service.npcChat(id, "Trả lời sai");
                            }
                            break;
                    }
                }

                break;

        }
        getService().sendMessage(new Message((byte) 5));
    }

    private void updateTakingStep(int idStep) {
        getService().sendMessage(new Message((byte) 5));
        getService().sendTaskStep(idStep);
        taskNext();
    }

    public int IdVK1x(int Idclass) {
        for (ItemTemplate item : DataCenter.gI().ItemTemplate) {
            if (item.idClass == Idclass && item.levelNeed == 10 && item.type == 1) {
                return item.id;
            }

        }
        return 0;
    }

    public void doibikip(Message msg) {
        try {
            byte size = msg.readByte();
            Vector<Item> items = new Vector<>();
            int count = 0;
            boolean[] list = new boolean[Bag.arrItemBag.length];
            while (msg.reader.Avali() > 0) {
                int index = msg.readShort();
                if (index < 0 || index >= Bag.arrItemBag.length) {
                    continue;
                }
                if (!list[index]) {
                    list[index] = true;
                    Item item = Bag.arrItemBag[index];
                    if (item != null && item.id == 310) {
                        count += item.amount;
                        items.add(item);
                    }
                }
            }
            if (items.isEmpty()) {
                return;
            }
            if (count < 1000) {
                service.serverMessage("Không đủ 1000 mảnh bí kíp");
                return;
            }
            if (Bag.bac < 500000) {
                service.serverMessage("Không đủ 500k bạc");
                return;
            }
            for (Item item : items) {
                removeItem(item, true);
                msgRemoveItemBag(item);
            }
            addBac(-500000);
            int iditem = 0;
            switch (Info.idClass) {
                case 1:
                    iditem = 140;
                    break;
                case 2:
                    iditem = 137;
                    break;
                case 3:
                    iditem = 143;
                    break;
                case 4:
                    iditem = 146;
                    break;
                case 5:
                    iditem = 149;
                    break;
            }
            Item caocap = new Item(iditem);
            caocap.isLock = true;
            caocap.he = Info.idhe;
            caocap.addItemOption(new ItemOption(128, 0, 12000));
            caocap.addItemOption(new ItemOption(0, 500, 600));
            caocap.addItemOption(new ItemOption(1, 500, 600));
            if (caocap.he == 1) {
                caocap.addItemOption(new ItemOption(109, 80, 100));
                caocap.addItemOption(new ItemOption(114, 200, 220));
            } else if (caocap.he == 2) {
                caocap.addItemOption(new ItemOption(110, 80, 100));
                caocap.addItemOption(new ItemOption(115, 200, 220));
            } else if (caocap.he == 3) {
                caocap.addItemOption(new ItemOption(111, 80, 100));
                caocap.addItemOption(new ItemOption(113, 200, 220));
            } else if (caocap.he == 4) {
                caocap.addItemOption(new ItemOption(112, 80, 100));
                caocap.addItemOption(new ItemOption(117, 200, 220));
            } else if (caocap.he == 5) {
                caocap.addItemOption(new ItemOption(108, 80, 100));
                caocap.addItemOption(new ItemOption(113, 200, 220));
            }
            caocap.createItemOptions();
            addItem(caocap);
            msgAddItemBag(caocap);
            if (count - 1000 > 0) {
                Item manh = new Item(310);
                manh.amount = count - 1000;
                manh.isLock = true;
                addItem(manh);
                msgAddItemBag(manh);
            }
            service.serverMessage("Chúc mừng bạn đổi thành công bí kíp cao cấp");
            service.resetScreen();
        } catch (IOException e) {

        }
    }

    public void phatLuong(Message m) {
        try {
            String name = m.readUTF();
            int money = m.readInt();
            if (clan == null) {
                return;
            }
            if (money < 0) {
                return;
            }
            if (isSecurity && !isUnlockSecurity) {
                service.warningMessage("Vui lòng mở khoá bảo mật trước khi sử dụng tính năng này");
                return;
            }
            int cType = this.clan.getMemberByName(Info.name).getType();
            if (cType == Clan.TYPE_TOCTRUONG) {
                Member mem = this.clan.getMemberByName(name);
                if (mem == null) {
                    service.serverMessage("Thành viên không tồn tại");
                    return;
                }
                int _money = money + (money / 100);
                if (_money > clan.getCoin()) {
                    service.serverMessage("Gia tộc không đủ bạc để phát lương");
                    return;
                }
                Char plNhan = ServerManager.findCharByName(name);
                if (plNhan == null) {
                    service.serverMessage("Không tìm thấy người chơi hoặc người chơi đã offline");
                    return;
                }
                clan.addCoin(-money);
                TemplateThu thu = new TemplateThu();
                int id = plNhan.letters.size() + 1;
                if (plNhan.letters.size() > 0) {
                    id = plNhan.letters.get(plNhan.letters.size() - 1).id + 1;
                }
                thu.id = (short) id;
                thu.Bac = money;
                thu.BacKhoa = 0;
                thu.Vang = 0;
                thu.VangKhoa = 0;
                thu.Exp = 0;
                thu.Title = "Gia Tộc " + clan.getName() + " phát lương ";
                thu.NameNguoiGui = this.Info.name;
                thu.NoiDungThu = "";
                thu.TimeEnd = System.currentTimeMillis() + 864000000;
                plNhan.letters.add(thu);
                plNhan.getService().reloadLetter();
                this.getService().sendMessage(HanderMessage.resetScreen());
                clan.writeLog(Info.name, "Đã phát lương cho " + name + " và ngân quỹ trừ", _money);
                this.service.serverMessage("Phát lương thành công");
                this.getService().showInfoGiaToc();
            }
        } catch (IOException e) {

        }

    }

    public void acceptTask() {
        if (taskMain == null) {
            TaskTemplate template = Task.getTaskTemplate(taskId);
            if (template == null) {
                return;
            }
            taskMain = TaskFactory.getInstance().createTask(taskId, (byte) 0, (short) 0);
            getService().sendTaskInfo();
            if (taskId == TaskName.NV_CANH_BAO_DAN_LANG) {
                for (int i = 0; i < 3; i++) {
                    Item item = new Item(380);
                    item.amount = 1;
                    item.isLock = true;
                    addItem(item);
                    msgAddItemBag(item);
                }
            } else if (taskId == TaskName.NV_BAT_KE_NGHE_LEN) {
                for (int i = 0; i < 3; i++) {
                    Item item = new Item(383);
                    item.amount = 1;
                    item.isLock = true;
                    addItem(item);
                    msgAddItemBag(item);
                }
            } else if (taskId == TaskName.NV_CAU_CUU_VIEN_BINH) {
                Item item = new Item(386);
                item.amount = 1;
                item.isLock = true;
                addItem(item);
                msgAddItemBag(item);
            } else if (taskId == TaskName.NV_TRUY_TIM_BI_KIP || taskId == TaskName.NV_BAO_VAT_LANG_LA) {
                Item item = new Item(209);
                item.amount = 1;
                item.isLock = true;
                addItem(item);
                msgAddItemBag(item);
            } else if (taskId == TaskName.NV_XOA_BO_CAM_THUAT) {
                Item item = new Item(394);
                item.amount = 1;
                item.isLock = true;
                addItem(item);
                msgAddItemBag(item);
            } else if (taskId == TaskName.NV_HOAN_TRA_BAO_VAT) {
                Item item = new Item(235);
                item.amount = 60;
                item.isLock = true;
                addItem(item);
                msgAddItemBag(item);
            }
            return;
        }
        taskNext();
    }

    public boolean menuTask() {
        if (taskMain == null) {
            takingTask();
            return true;
        } else if (taskMain.index > taskMain.vStep.size() - 1) {
            getService().sendTextNPC("", "Hoàn thành");
            typeMenu = TYPEMENU.HOAN_THANH_NHIEM_VU;
            return true;
        } else {
            switch (taskId) {
                case TaskName.NV_NHAN_GIA_HOC_VIEN:
                    if (taskMain.index < 8) {
                        getService().sendTextNPC("", "Nói chuyện");
                        typeMenu = TYPEMENU.LAM_NHIEM_VU;
                        return true;
                    } else if (taskMain.index == 8) {
                        getService().sendTextNPC("", "Nhập học");
                        typeMenu = TYPEMENU.LAM_NHIEM_VU;
                        return true;
                    }
                    break;
                case TaskName.NV_LAM_NGUOI_TOT_BUNG:
                    if (taskMain.index == 2) {
                        getService().sendTextNPC("", "Nói chuyện");
                        typeMenu = TYPEMENU.LAM_NHIEM_VU;
                        return true;
                    }
                    break;
                case TaskName.NV_TRO_GIUP_LANG_SUONG_MU:
                    if (taskMain.index == 1) {
                        getService().sendTextNPC("", "Nói chuyện");
                        typeMenu = TYPEMENU.LAM_NHIEM_VU;
                        return true;
                    }
                    break;
                case TaskName.NV_TIM_UDON:
                    if (taskMain.index == 0) {
                        getService().sendTextNPC("", "Nói chuyện");
                        typeMenu = TYPEMENU.LAM_NHIEM_VU;
                        return true;
                    } else if (taskMain.index == 1) {
                        getService().sendTextNPC("", "Về nhà thôi");
                        typeMenu = TYPEMENU.LAM_NHIEM_VU;
                        return true;
                    }
                    break;
                case TaskName.NV_GIAI_CUU_INARI:
                    if (taskMain.index == 1) {
                        getService().sendTextNPC("", "Nói chuyện");
                        typeMenu = TYPEMENU.LAM_NHIEM_VU;
                        return true;
                    } else if (taskMain.index == 2) {
                        getService().sendTextNPC("", "Về nhà thôi");
                        typeMenu = TYPEMENU.LAM_NHIEM_VU;
                        return true;
                    }
                    break;
                case TaskName.NV_BAT_KE_NGHE_LEN:
                    if (taskMain.index == 3) {
                        getService().sendTextNPC("", "Nói chuyện");
                        typeMenu = TYPEMENU.LAM_NHIEM_VU;
                        return true;
                    }
                    break;
                case TaskName.NV_KE_DICH_LO_DIEN:
                    if (taskMain.index == 0 || taskMain.index == 2) {
                        getService().sendTextNPC("", "Nói chuyện");
                        typeMenu = TYPEMENU.LAM_NHIEM_VU;
                        return true;
                    } else if (taskMain.index == 1) {
                        getService().sendTextNPC("", "Thách đấu");
                        typeMenu = TYPEMENU.LAM_NHIEM_VU;
                        return true;
                    }
                    break;
                case TaskName.NV_CAU_CUU_VIEN_BINH:
                    if (taskMain.index >= 0 && taskMain.index <= 4) {
                        getService().sendTextNPC("", "Giao thư");
                    } else
                        getService().sendTextNPC("", "Nói chuyện");
                    typeMenu = TYPEMENU.LAM_NHIEM_VU;
                    return true;
                case TaskName.NV_CHUA_LANH_VET_THUONG:
                    getService().sendTextNPC("", "Nói chuyện");
                    typeMenu = TYPEMENU.LAM_NHIEM_VU;
                    return true;
                case TaskName.NV_XOA_BO_CAM_THUAT:
                    getService().sendTextNPC("", "Nói chuyện");
                    typeMenu = TYPEMENU.LAM_NHIEM_VU;
                    return true;
                case TaskName.NV_TRAN_CHIEN_SONG_CON:
                    if (taskMain.index == 0 || taskMain.index == 2) {
                        getService().sendTextNPC("", "Nói chuyện");
                    } else if (taskMain.index == 1) {
                        getService().sendTextNPC("", "Ra tay đi");
                    }
                    typeMenu = TYPEMENU.LAM_NHIEM_VU;
                    return true;
                case TaskName.NV_KI_THI_CHUNIN:
                    if (taskMain.index == 0 || taskMain.index == 2) {
                        getService().sendTextNPC("", "Nói chuyện");
                        typeMenu = TYPEMENU.KI_THI_CHUNNIN;
                    } else if (taskMain.index == 1) {
                        return false;
                    }
                    return true;
                case TaskName.NV_LE_HOI_TODOROKI_TAISHA:
                    if (taskMain.index == 0 || taskMain.index == 2) {
                        getService().sendTextNPC("", "Nói chuyện");
                    } else if (taskMain.index == 1) {
                        getService().sendTextNPC("", "Lên đường thôi");
                    }
                    typeMenu = TYPEMENU.LAM_NHIEM_VU;
                    return true;
                case TaskName.NV_NOI_BUON_CUA_IDATE:
                    if (taskMain.index == 0 || taskMain.index == 2) {
                        getService().sendTextNPC("", "Nói chuyện");
                    } else if (taskMain.index == 1) {
                        getService().sendTextNPC("", "Ra tay đi");
                    }
                    typeMenu = TYPEMENU.LAM_NHIEM_VU;
                    return true;
                case TaskName.NV_HOAN_TRA_BAO_VAT:
                    if (taskMain.index == 1 || taskMain.index == 3 || taskMain.index >= 5) {
                        getService().sendTextNPC("", "Nói chuyện");
                    } else if (taskMain.index == 2) {
                        getService().sendTextNPC("", "Giao thư");
                    } else if (taskMain.index == 4) {
                        getService().sendTextNPC("", "Giao cá");
                    }
                    typeMenu = TYPEMENU.LAM_NHIEM_VU;
                    return true;
                case TaskName.NV_GIUP_DO_GAARA:
                    if (taskMain.index == 0) {
                        getService().sendTextNPC("", "Nói chuyện");
                    } else if (taskMain.index == 1) {
                        getService().sendTextNPC("", "Lên đường thôi");
                    }
                    typeMenu = TYPEMENU.LAM_NHIEM_VU;
                    return true;
                case TaskName.NV_KE_THU_LANG_CAT:
                    if (taskMain.index == 0 || taskMain.index == 2) {
                        getService().sendTextNPC("", "Nói chuyện");
                    } else if (taskMain.index == 1) {
                        getService().sendTextNPC("", "Ra tay di");
                    }
                    typeMenu = TYPEMENU.LAM_NHIEM_VU;
                    return true;
                case TaskName.NV_GIAO_LUU_VO_CONG_VOI_GAARA:
                    if (taskMain.index == 0 || taskMain.index == 2) {
                        getService().sendTextNPC("", "Ra tay di");
                    } else if (taskMain.index == 1) {
                        getService().sendTextNPC("", "Nói chuyện");
                    }
                    typeMenu = TYPEMENU.LAM_NHIEM_VU;
                    return true;
                default:
                    if (taskId == TaskName.NV_NHAN_GIA_HOC_VIEN
                            || taskId == TaskName.NV_NHIEM_VU_DAU_TIEN) {
                        if (taskId == TaskName.NV_NHIEM_VU_DAU_TIEN && taskMain != null && taskMain.index == 10) {
                            return false;
                        }
                        getService().sendTextNPC("", "Nói chuyện");
                        typeMenu = TYPEMENU.LAM_NHIEM_VU;
                        return true;
                    }
            }
        }
        return false;
    }

    public void anserChunnin() {
        if (taskMain != null && taskMain.index == 1 && taskId == 20) {
            typeMenu = TYPEMENU.KI_THI_CHUNNIN;
            switch (taskMain.count) {
                case 0:
                    getService().sendTextNPC("Trong game Làng Nhẫn Giả, tỉ lệ chí mạng gây thêm sát thương là bao nhiêu?", "3.2;1.8;1.5;2.0");
                    break;
                case 1:
                    getService().sendTextNPC("Để vào được khu luyện tập thì cần gặp Npc nào?", "Shizune;Anko Mitarashi;Mei Terumi;Chiyo");
                    break;
                case 2:
                    getService().sendTextNPC("Muốn tăng kinh nghiệm khi đánh quái cần có vật phẩm gì?", "Thỏi vàng;Xích linh chi;Nhân sâm;Túi thức ăn");
                    break;
                case 3:
                    getService().sendTextNPC("Khi kích vũ khí khác hệ thì cần có vật phẩm gì?", "Thú nuôi;Áo choàng;Bí kíp;Tanto");
                    break;
                case 4:
                    getService().sendTextNPC("Vật phẩm cải trang có công dụng gì?", "Thay đổi diện mạo;Tăng chỉ số và thay đổi diện mạo;Làm người khác không nhìn thấy;Có được tuyệt kỹ của người cải trang");
                    break;
            }
        }
    }

    public void spinTreasure(byte action) {
        SpinTreasure.getInstance().spinning(this, action);
    }

    public void rewardTreasure(byte action, boolean isRuong) {
        SpinTreasure.getInstance().spinReward(this, action, isRuong);
    }

    public void sellMarket(Message msg) {
        try {
            if (!user.actived) {
                getService().serverMessage("Vui lòng kích hoạt tài khoản để sử dụng tính năng này");
                return;
            }

            if (isSecurity && !isUnlockSecurity) {
                service.warningMessage("Vui lòng mở khoá bảo mật trước khi sử dụng tính năng này");
                return;
            }

//            if(true){
//                service.serverMessage("Tính năng đang được nâng cấp");
//                return;
//            }
            short index = msg.readShort();
            byte typeTime = msg.readByte();
            int price = msg.readInt();
            int time = typeTime == 0 ? 8 : typeTime == 1 ? 16 : typeTime == 2 ? 24 : typeTime == 3 ? 48 : 72;
            if (index < 0 || index >= Bag.arrItemBag.length) {
                return;
            }
            Item item = Bag.arrItemBag[index];
            if (item == null || item.amount < 1 || item.isLock) {
                service.serverMessage("Không tìm thấy vật phẩm");
                return;
            }
            if (price < 0) {
                service.serverMessage("Giá bạc phải lớn hơn 0 bạc");
            }
            if (price >= 100000000) {
                service.serverMessage("Giá bạc tối đa là 99.000.000 bạc");
                return;
            }
            int fee = 50000;
            if (Bag.bac < fee) {
                service.serverMessage(String.format("Phí bán vật phẩm là %,d bạc.", fee));
                return;
            }
            MarketManager.gI().themItem(this, item, price, (int) TimeUnit.HOURS.toMillis(time));
            addBac(-fee);
            Bag.arrItemBag[index] = null;
            getService().sellMarket(fee, index);
            MarketManager.gI().showListSell(this);
            MarketManager.gI().show(this, (byte) 0, (byte) 0, (short) 1);
        } catch (Exception e) {

        }
    }

    public TemplateThu findLetter(int id) {
        synchronized (letters) {
            for (TemplateThu thu : letters) {
                if (thu != null && thu.id == id) {
                    return thu;
                }
            }
        }
        return null;
    }

    public void removeLetter(Message msg) {
        try {
            List<TemplateThu> removes = new ArrayList<>();
            int size = msg.readShort();
            for (int i = 0; i < size; i++) {
                int id = msg.readShort();
                TemplateThu letter = findLetter(id);
                if (letter != null) {
                    removes.add(letter);
                }
            }
            letters.removeAll(removes);
            getService().reloadLetter();
        } catch (IOException e) {

        }
    }

    public String getName() {
        return Info.name;
    }

    public boolean checkBuyWekfare(int id) {
        switch (id) {
            case 13:
                if (phucLoi.theThang > System.currentTimeMillis())
                    return true;
                break;
            case 14:
                if (phucLoi.theVinhVien > System.currentTimeMillis())
                    return true;
                break;
        }
        return false;
    }

    public void subscribeBenefitPackage(int idBenefit) {
        switch (idBenefit) {
            case 13:
                if (Bag.vang < 100) {
                    getService().warningMessage("Bạn không đủ 100 vàng");
                    return;
                }
                addVang(-100);
                addVangKhoa(100);
                phucLoi.theThang = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30);
                getService().serverMessage("Mua thành công thẻ tháng");
                ClickEvent.PhucLoi(this);
                break;
            case 14:
                if (Bag.vang < 300) {
                    getService().warningMessage("Bạn không đủ 300 vàng");
                    return;
                }
                addVang(-300);
                addVangKhoa(300);
                phucLoi.theVinhVien = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(365);
                getService().serverMessage("Mua thành công thẻ vĩnh viễn");
                ClickEvent.PhucLoi(this);
                break;
        }
    }

    public String getTextWelfare(int id, String description) {
        switch (id) {
            case 0:
                return description = String.format(description, phucLoi.timeOnline / 60000);
            case 1:
                return description = String.format(description, phucLoi.soNgayOnline);
            case 2:
                return description = String.format(description, level());
            case 3:
                return description = String.format(description, phucLoi.tieuNgay);
            case 4:
                return description = String.format(description, phucLoi.tieuTuan);
            case 5:
                return description = String.format(description, phucLoi.napNgay);
            case 6:
                return description = String.format(description, phucLoi.napTuan);
            case 7:
                return description = String.format(description, phucLoi.napLienTuc);
            case 8:
                return description = String.format(description, phucLoi.nap3moc);
            case 9:
                return description = String.format(description, phucLoi.napDon);
            case 10:
                return description = String.format(description, Bag.pointNAP);
            case 11:
                return description = String.format(description, Manager.gI().rankCaoNhat);
            case 12:
                return description = String.format(description, Manager.gI().countRank);

        }
        return description;
    }

    public boolean checkItemShopRank(int id) {
        return shoprank.contains(id);
    }

    public void moneyToBox(Message msg) {
        try {
            if (theGiuTien < System.currentTimeMillis()) {
                getService().warningMessage("Thẻ giữ tiền của bạn đã hết hạn");
                return;
            }
            byte type = msg.readByte();
            int money = msg.readInt();
            // 0: bạc, 1: bac khoa,2: vàng, 3: vàng khoa
            if (money < 0) {
                getService().warningMessage("Số nhập vào không hợp lệ");
                return;
            }
            switch (type) {
                case 0:
                    if (Bag.bac < money) {
                        getService().warningMessage("Bạn không đủ bạc");
                        return;
                    }
                    long num = (long) Bag.bacBox + (long) money;
                    if (num >= Integer.MAX_VALUE) {
                        getService().warningMessage("Đã tới giới hạn lưu trữ bạc trong hòm đồ");
                        return;
                    }
                    addBac(-money);
                    addBacBox(money);
                    getService().serverMessage("Chuyển bạc vào hòm đồ thành công");
                    break;
                case 1:
                    if (Bag.bacKhoa < money) {
                        getService().warningMessage("Bạn không đủ bạc khoá");
                        return;
                    }
                    long num1 = (long) Bag.bacKhoaBox + (long) money;
                    if (num1 >= Integer.MAX_VALUE) {
                        getService().warningMessage("Đã tới giới hạn lưu trữ bạc khoá trong hòm đồ");
                        return;
                    }
                    addBacKhoa(-money);
                    addBacKhoaBox(money);
                    getService().serverMessage("Chuyển bạc khoá vào hòm đồ thành công");
                    break;
                case 2:
                    if (Bag.vang < money) {
                        getService().warningMessage("Bạn không đủ vàng");
                        return;
                    }
                    long num2 = (long) Bag.vangBox + (long) money;
                    if (num2 >= Integer.MAX_VALUE) {
                        getService().warningMessage("Đã tới giới hạn lưu trữ vàng trong hòm đồ");
                        return;
                    }
                    addVang(-money);
                    addVangBox(money);
                    getService().serverMessage("Chuyển vàng vào hòm đồ thành công");
                    break;
                case 3:
                    if (Bag.vangKhoa < money) {
                        getService().warningMessage("Bạn không đủ vàng khoá");
                        return;
                    }
                    long num3 = (long) Bag.vangKhoaBox + (long) money;
                    if (num3 >= Integer.MAX_VALUE) {
                        getService().warningMessage("Đã tới giới hạn lưu trữ vàng khoá trong hòm đồ");
                        return;
                    }
                    addVangKhoa(-money);
                    addVangKhoaBox(money);
                    getService().serverMessage("Chuyển vàng khoá vào hòm đồ thành công");
                    break;

            }
        } catch (Exception e) {
            Log.error("Error money to box " + e);
        }
    }

    public synchronized void addBacKhoaBox(int money) {
        long num = (long) Bag.bacKhoaBox + (long) money;
        if (num >= Integer.MAX_VALUE) {
            Bag.bacKhoaBox = Integer.MAX_VALUE;
        } else {
            Bag.bacKhoaBox += money;
        }
        ClickEvent.OpenBox(this);
    }

    public synchronized void addVangKhoaBox(int money) {
        long num = (long) Bag.vangKhoaBox + (long) money;

        if (num >= Integer.MAX_VALUE) {
            Bag.vangKhoaBox = Integer.MAX_VALUE;
        } else {
            Bag.vangKhoaBox += money;
        }
        ClickEvent.OpenBox(this);
    }

    public synchronized void addBacBox(int money) {
        long num = (long) Bag.bacBox + (long) money;

        if (num >= Integer.MAX_VALUE) {
            Bag.bacBox = Integer.MAX_VALUE;
        } else {
            Bag.bacBox += money;
        }
        ClickEvent.OpenBox(this);
    }

    public synchronized void addVangBox(int money) {
        long num = (long) Bag.vangBox + (long) money;
        if (num >= Integer.MAX_VALUE) {
            Bag.vangBox = Integer.MAX_VALUE;
        } else {
            Bag.vangBox += money;
        }
        ClickEvent.OpenBox(this);
    }

    public void moneyToBag(Message msg) {
        try {
            if (theGiuTien < System.currentTimeMillis()) {
                getService().warningMessage("Thẻ giữ tiền của bạn đã hết hạn");
                return;
            }
            byte type = msg.readByte();
            int money = msg.readInt();
            // 0: bạc, 1: bac khoa,2: vàng, 3: vàng khoa
            if (money < 0) {
                getService().warningMessage("Số nhập vào không hợp lệ");
                return;
            }
            switch (type) {
                case 0:
                    if (Bag.bacBox < money) {
                        getService().warningMessage("Bạn không đủ bạc trong hòm đồ");
                        return;
                    }
                    long num = (long) Bag.bacBox + (long) money;
                    if (num >= Integer.MAX_VALUE) {
                        getService().warningMessage("Đã tới giới hạn lưu trữ bạc trong túi");
                        return;
                    }
                    addBac(money);
                    addBacBox(-money);
                    getService().serverMessage("Chuyển bạc vào túi thành công");
                    break;
                case 1:
                    if (Bag.bacKhoaBox < money) {
                        getService().warningMessage("Bạn không đủ bạc khoá trong hòm đồ");
                        return;
                    }
                    long num1 = (long) Bag.bacKhoaBox + (long) money;
                    if (num1 >= Integer.MAX_VALUE) {
                        getService().warningMessage("Đã tới giới hạn lưu trữ bạc khoá trong túi");
                        return;
                    }
                    addBacKhoa(money);
                    addBacKhoaBox(-money);
                    getService().serverMessage("Chuyển bạc khoá vào túi thành công");
                    break;
                case 2:
                    if (Bag.vangBox < money) {
                        getService().warningMessage("Bạn không đủ vàng trong hòm đồ");
                        return;
                    }
                    long num2 = (long) Bag.vangBox + (long) money;
                    if (num2 >= Integer.MAX_VALUE) {
                        getService().warningMessage("Đã tới giới hạn lưu trữ vàng trong túi");
                        return;
                    }
                    addVang(money);
                    addVangBox(-money);
                    getService().serverMessage("Chuyển vàng vào túi thành công");
                    break;
                case 3:
                    if (Bag.vangKhoaBox < money) {
                        getService().warningMessage("Bạn không đủ vàng khoá trong hòm đồ");
                        return;
                    }
                    long num3 = (long) Bag.vangKhoaBox + (long) money;
                    if (num3 >= Integer.MAX_VALUE) {
                        getService().warningMessage("Đã tới giới hạn lưu trữ vàng khoá trong túi");
                        return;
                    }
                    addVangKhoa(money);
                    addVangKhoaBox(-money);
                    getService().serverMessage("Chuyển vàng khoá vào túi thành công");
                    break;
            }
        } catch (Exception e) {
            Log.error("Error money to bag " + e);
        }
    }


    /*
     public static void ah(Message var0) {
     try {
     LangLa_cx var1;
     if ((var1 = gI().H()) instanceof LangLa_kl) {
     LangLa_kl var7;
     LangLa_kl var10000 = var7 = (LangLa_kl) var1;
     Message var8 = var0;
     LangLa_kl var6 = var10000;

     try {
     var6.y.levelCheTao = var8.reader.dis.readInt();
     var6.y.hoatLuc = var8.reader.dis.readInt();
     var6.y.sachChienDau = var8.reader.dis.readByte();
     var6.v = var8.reader.dis.readShort();
     var6.i = var6.u = var8.reader.dis.readShort();
     LangLa_ce var10 = var6.B;
     boolean var3 = var8.reader.dis.readBoolean();
     var10.a = var3;
     var6.A.a(var8.reader.dis.readByte());
     var6.e = var8.reader.dis.readShort();
     var6.f = var8.reader.dis.readShort();
     var6.h = var8.reader.dis.readShort();
     var6.g = var8.reader.dis.readShort();
     var6.w[0] = var8.reader.dis.readInt();
     var6.w[1] = var8.reader.dis.readInt();
     var6.w[2] = var8.reader.dis.readShort();
     var6.w[3] = var8.reader.dis.readShort();
     var6.w[4] = var8.reader.dis.readShort();
     var6.w[5] = var8.reader.dis.readShort();
     var6.w[6] = var8.reader.dis.readShort();
     var6.w[7] = var8.reader.dis.readShort();
     var6.w[8] = var8.reader.dis.readShort();
     var6.w[9] = var8.reader.dis.readShort();
     var6.w[10] = var8.reader.dis.readShort();
     var6.w[11] = var8.reader.dis.readShort();
     var6.w[12] = var8.reader.dis.readShort();
     var6.w[13] = var8.reader.dis.readShort();
     var6.w[14] = var8.reader.dis.readShort();
     var6.w[15] = var8.reader.dis.readShort();
     var6.w[16] = var8.reader.dis.readShort();
     var6.x[0] = var8.reader.dis.readShort();
     var6.x[1] = var8.reader.dis.readShort();
     var6.x[2] = var8.reader.dis.readShort();
     var6.x[3] = var8.reader.dis.readShort();
     var6.x[4] = var8.reader.dis.readShort();
     var6.x[5] = var8.reader.dis.readShort();
     var6.x[6] = var8.reader.dis.readShort();
     var6.y.speedMove = (short) (var6.x[6] / 100);
     var6.x[7] = var8.reader.dis.readShort();
     var6.x[8] = var8.reader.dis.readShort();
     var6.x[9] = var8.reader.dis.readShort();
     var6.x[10] = var8.reader.dis.readShort();
     var6.x[11] = var8.reader.dis.readShort();
     var6.x[12] = var8.reader.dis.readShort();
     var6.x[13] = var8.reader.dis.readShort();
     var6.x[14] = var8.reader.dis.readShort();
     var6.x[15] = var8.reader.dis.readShort();
     var6.x[16] = var8.reader.dis.readShort();
     var6.x[17] = var8.reader.dis.readShort();
     var6.c[0].a("" + var6.e);
     var6.c[1].a("" + var6.f);
     var6.c[2].a("" + var6.h);
     var6.c[3].a("" + var6.g);
     mTextBox var11 = var6.c[0];
     int var9 = var6.e;
     var11.n = var9;
     var11 = var6.c[1];
     var9 = var6.f;
     var11.n = var9;
     var11 = var6.c[2];
     var9 = var6.h;
     var11.n = var9;
     var11 = var6.c[3];
     var9 = var6.g;
     var11.n = var9;
     var6.d = true;
     var6.b(var6.a);
     return;
     } catch (Exception var4) {
     Utlis.println(var4);
     }
     }

     } catch (Exception var5) {
     Utlis.println(var5);
     }
     }
     */
    public static class ItemsFormItem {

        public static Item[] _268 = new Item[]{
                new Item(185, true),
                new Item(185, true),
                new Item(185, true),
                new Item(169, true, 10),
                new Item(170, true, 10),
                new Item(267, true, 10),
                new Item(12, true, 1000),
                new Item(17, true, 1000),
                new Item(28, true),
                new Item(914, true, 100),
                new Item(269, true)
        };

        public static Item[] getGift(int id) {
            switch (id) {
                case 268:
                    return Utlis.cloneArray(_268);
//                case 269:
//                    return Utlis.cloneArray(_269);
            }
            return null;
        }
    }

    public void sortItemBag() {
        if (trade != null) {
            return;
        }
        try {
            Item[] var0 = this.Bag.arrItemBag;
            Vector var1 = new Vector();

            for (int var2 = 0; var2 < var0.length; ++var2) {
                if (var0[var2] != null) {
                    var1.add(var0[var2]);
                }

                var0[var2] = null;
            }

            for (int var4 = 0; var4 < var1.size(); ++var4) {
                Item var7;
                if ((var7 = (Item) var1.get(var4)).getItemTemplate().isXepChong) {
                    for (int var5 = var1.size() - 1; var5 > var4; --var5) {
                        Item var3;
                        if ((var3 = (Item) var1.get(var5)).id == var7.id && var3.isLock == var7.isLock && var3.expiry == var7.expiry) {
                            var7.setAmount(var7.getAmount() + var3.getAmount());
                            var1.remove(var5);
                        }
                    }
                }
            }

            for (short var8 = 0; var8 < var1.size(); var0[var8].index = var8++) {
                var0[var8] = (Item) var1.get(var8);
            }
            service.bagSort();
        } catch (Exception var6) {
        }
//        try {
//            Vector items = new Vector();
//            for (int i = 0; i < this.Bag.arrItemBag.length; i++) {
//                Item item = this.Bag.arrItemBag[i];
//                if (item != null && item.getItemTemplate().isXepChong && !item.hasExpire()) {
//                    items.addElement(item);
//                }
//            }
//            for (int i = 0; i < items.size(); i++) {
//                Item itemi = (Item) items.elementAt(i);
//                if (itemi != null) {
//                    for (int j = i + 1; j < items.size(); j++) {
//                        Item itemj = (Item) items.elementAt(j);
//                        if (itemj != null && itemi.getItemTemplate().equals(itemj.getItemTemplate()) && itemi.isLock == itemj.isLock) {
//                            itemi.add(itemj.getAmount());
//                            this.Bag.arrItemBag[itemj.index] = null;
//                            items.setElementAt(null, j);
//                        }
//                    }
//                }
//            }
//            for (int i = 0; i < this.Bag.arrItemBag.length; i++) {
//                if (this.Bag.arrItemBag[i] != null) {
//                    for (int j = 0; j <= i; j++) {
//                        if (this.Bag.arrItemBag[j] == null) {
//                            this.Bag.arrItemBag[j] = this.Bag.arrItemBag[i];
//                            this.Bag.arrItemBag[j].index = j;
//                            this.Bag.arrItemBag[i] = null;
//                            break;
//                        }
//                    }
//                }
//            }
//            service.bagSort();
//        } catch (Exception e) {
//            Log.error("err: " + e.getMessage(), e);
//        }
    }

    public void boxSort() {
        try {
            Item[] var0 = this.Bag.arrItemBox;
            Vector var1 = new Vector();

            for (int var2 = 0; var2 < var0.length; ++var2) {
                if (var0[var2] != null) {
                    var1.add(var0[var2]);
                }

                var0[var2] = null;
            }

            for (int var4 = 0; var4 < var1.size(); ++var4) {
                Item var7;
                if ((var7 = (Item) var1.get(var4)).getItemTemplate().isXepChong) {
                    for (int var5 = var1.size() - 1; var5 > var4; --var5) {
                        Item var3;
                        if ((var3 = (Item) var1.get(var5)).id == var7.id && var3.isLock == var7.isLock && var3.expiry == var7.expiry) {
                            var7.setAmount(var7.getAmount() + var3.getAmount());
                            var1.remove(var5);
                        }
                    }
                }
            }

            for (short var8 = 0; var8 < var1.size(); var0[var8].index = var8++) {
                var0[var8] = (Item) var1.get(var8);
            }

        } catch (Exception var6) {
        }
//        try {
//            Vector items = new Vector();
//            for (int i = 0; i < this.Bag.arrItemBox.length; i++) {
//                Item item = this.Bag.arrItemBox[i];
//                if (item != null && item.getItemTemplate().isXepChong && !item.hasExpire()) {
//                    items.addElement(item);
//                }
//            }
//            for (int i = 0; i < items.size(); i++) {
//                Item itemi = (Item) items.elementAt(i);
//                if (itemi != null) {
//                    for (int j = i + 1; j < items.size(); j++) {
//                        Item itemj = (Item) items.elementAt(j);
//                        if (itemj != null && itemi.getItemTemplate().equals(itemj.getItemTemplate()) && itemi.isLock == itemj.isLock) {
//                            itemi.add(itemj.getAmount());
//                            this.Bag.arrItemBox[itemj.index] = null;
//                            items.setElementAt(null, j);
//                        }
//                    }
//                }
//            }
//            for (int i = 0; i < this.Bag.arrItemBox.length; i++) {
//                if (this.Bag.arrItemBox[i] != null) {
//                    for (int j = 0; j <= i; j++) {
//                        if (this.Bag.arrItemBox[j] == null) {
//                            this.Bag.arrItemBox[j] = this.Bag.arrItemBox[i];
//                            this.Bag.arrItemBox[j].index = j;
//                            this.Bag.arrItemBox[i] = null;
//                            break;
//                        }
//                    }
//                }
//            }
        service.boxSort();
//        } catch (Exception e) {
//            Log.error("err: " + e.getMessage(), e);
//        }
    }

    public void removeItemsWithIDPhucLoi(int idPhucLoi) {
        // Sử dụng removeIf để xoá các phần tử thỏa mãn điều kiện
        for (int i = phucLoi.listPl.size() - 1; i >= 0; i--) {
            TemplatePL pl = phucLoi.listPl.get(i);
            if (pl.IDPhucLoi == idPhucLoi) {
                phucLoi.listPl.remove(i);
            }
        }
    }

    public void MoRongBox() {
        if (Bag.vang < 90) {
            user.session.sendMessage(HanderMessage.SendThongBao("Không đủ vàng", HanderMessage.RED_MID));
            return;
        }
        if (Bag.arrItemBox.length > 60) {
            user.session.sendMessage(HanderMessage.SendThongBao("Đã đạt giới hạn", HanderMessage.YELLOW_MID));
            return;
        }
        Item[] items = new Item[Bag.arrItemBox.length + 9];
        for (int i = 0; i < Bag.arrItemBox.length; i++) {
            items[i] = Bag.arrItemBox[i];
        }
        Bag.arrItemBox = items;
        Info.countBox += 9;
        addVang(-90);
        user.session.sendMessage(HanderMessage.resetScreen());
        service.alertMessage("Mở rộng rương thành công");
    }

    public void guiThu(String name, String chuDe, String noiDung, int Bac, short indexItem) {
        try {
            if (!user.actived) {
                service.alertMessage("Bạn chưa thể gửi thư. Vui lòng kích hoạt tài khoản!");
                return;
            }

            if (isSecurity && !isUnlockSecurity) {
                service.warningMessage("Vui lòng mở khoá bảo mật trước khi sử dụng tính năng này");
                return;
            }

            if (Bac < 0) {
                return;
            }

            Char plNhan = ServerManager.findCharByName(name);
            boolean isOffLine = false;

            if (plNhan == null) {
                plNhan = CharDB.getCharByName(name);
                isOffLine = true;
            }

            if (plNhan == null) {
                this.service.alertMessage("Không tìm thấy người chơi");
                return;
            } else {
                if (plNhan == this || this.Info.name.equals(name) || this.id == plNhan.id) {
                    this.service.alertMessage("Gửi thư cho chính mình làm gì vậy bạn");
                    return;
                }

                TemplateThu thu = new TemplateThu();
                int id = plNhan.letters.size() + 1;
                if (plNhan.letters.size() > 0) {
                    id = plNhan.letters.get(plNhan.letters.size() - 1).id + 1;
                }
                thu.id = (short) (id);
                thu.Bac = Bac;
                thu.BacKhoa = 0;
                thu.Vang = 0;
                thu.VangKhoa = 0;
                thu.Exp = 0;
                thu.Title = chuDe;
                thu.NameNguoiGui = this.Info.name;
                thu.NoiDungThu = noiDung;
                thu.TimeEnd = System.currentTimeMillis() + 864000000;

                int totalCost = 10 + (Bac / 100);

                if (Bag.bac < Bac + totalCost) {
                    this.service.alertMessage("Không đủ bạc");
                    return;
                }

                addBac(-totalCost);
                if (Bac > 0) {
                    addBac(-Bac);
                }

                if (indexItem > -1) {
                    Item item = Bag.arrItemBag[indexItem];
                    if (item != null) {
                        if (item.level > 1) {
                            this.service.alertMessage("Không thể gửi item đã cường hoá");
                            return;
                        }
                        if (item.isLock) {
                            this.service.alertMessage("Item bị khoá không thể gửi thư");
                            return;
                        }

                        Item itemthu = new Item(item.id);
                        itemthu.amount = item.amount;
                        itemthu.strOptions = item.strOptions;
                        thu.Item = itemthu;
                        removeItem(item, true);
                        msgRemoveItemBag(item);
                    } else {
                        this.service.alertMessage("Không có item để gửi");
                        return;
                    }
                }
                if (thu.Bac < 0) {
                    //   getService().warningMessage("Cố tình làm điều này sẽ khiến bạn bị ban");
                    return;
                }
                if (Bag.bac < 0) {
                    //  getService().warningMessage("Cố tình làm điều này sẽ khiến bạn bị ban");
                    return;
                }
                plNhan.letters.add(thu);

                if (isOffLine) {
                    CharDB.updateDBThu(plNhan, name);
                } else {
                    plNhan.getService().reloadLetter();
                }

                this.user.session.sendMessage(HanderMessage.resetScreen());
                this.service.alertMessage("Gửi thư thành công");
            }
        } catch (Exception e) {
            service.alertMessage("Có lỗi xảy ra, vui lòng thử lại sau");
            Log.error("Lỗi gửi thư: ", e);
        }
    }

    public void ChangeCoin(int select) {
        try {
            if (user.coin <= 0) {
                service.alertMessage("Không có coin");
                return;
            }
            int coinPlayer = user.coin;
            int coin = 0;
            int vang = 0;
            switch (select) {
                case 0:
                    coin = 10000;
                    vang = 300;
                    break;
                case 1:
                    coin = 50000;
                    vang = 1500;
                    break;
                case 2:
                    coin = 100000;
                    vang = 3200;
                    break;
                case 3:
                    coin = 500000;
                    vang = 16000;
                    break;
                case 4:
                    coin = 1000000;
                    vang = 33000;
                    break;
                case 5:
                    coin = 5000000;
                    vang = 180000;
                    break;
            }
            if (user.coin < coin) {
                service.alertMessage("Không có đủ coin để đổi");
                return;
            }
            CharDB.logExchange(this.Info.name, coinPlayer, coinPlayer - coin);
            addVang(vang);

            if (Bag.pointNAP == 0) { //quà nạp đầu
                TemplateThu thu1 = new TemplateThu();

                int id = this.letters.size() + 1;
                if (this.letters.size() > 0) {
                    id = this.letters.get(this.letters.size() - 1).id + 1;
                }
                thu1.id = (short) id;

                thu1.Title = "Quà Nạp Lần Đầu";
                thu1.NameNguoiGui = "Hệ thống";
                thu1.NoiDungThu = "Làng Lá - Đại Chiến Konoha gửi tặng bạn quà nạp lần đầu, chúc bạn chơi game vui vẻ!";
                Item itemthu1 = new Item(558);
                itemthu1.amount = 9999;
                itemthu1.strOptions = "";
                thu1.Item = itemthu1;
                this.letters.add(thu1);

                TemplateThu thu2 = new TemplateThu();

                int id1 = this.letters.size() + 1;
                if (this.letters.size() > 0) {
                    id1 = this.letters.get(this.letters.size() - 1).id + 1;
                }
                thu2.id = (short) id1;
                thu2.Title = "Quà Nạp Lần Đầu";
                thu2.NameNguoiGui = "Hệ thống";
                thu2.NoiDungThu = "Làng Lá - Đại Chiến Konoha gửi tặng bạn quà nạp lần đầu, chúc bạn chơi game vui vẻ!";
                Item itemthu2 = new Item(529);
                itemthu2.strOptions = "0,150;1,150;3,150;209,60";
                thu2.Item = itemthu2;
                this.letters.add(thu2);

                TemplateThu thu3 = new TemplateThu();
                int id2 = this.letters.size() + 1;
                if (this.letters.size() > 0) {
                    id2 = this.letters.get(this.letters.size() - 1).id + 1;
                }
                thu3.id = (short) id2;
                thu3.Title = "Quà Nạp Lần Đầu";
                thu3.NameNguoiGui = "Hệ thống";
                thu3.NoiDungThu = "Làng Lá - Đại Chiến Konoha gửi tặng bạn quà nạp lần đầu, chúc bạn chơi game vui vẻ!";
                Item itemthu3 = new Item(705);
                itemthu3.amount = 9999;
                itemthu3.strOptions = "";
                thu3.Item = itemthu3;
                this.letters.add(thu3);

                TemplateThu thu4 = new TemplateThu();
                int id3 = this.letters.size() + 1;
                if (this.letters.size() > 0) {
                    id3 = this.letters.get(this.letters.size() - 1).id + 1;
                }
                thu3.id = (short) id3;

                thu4.Title = "Quà Nạp Lần Đầu";
                thu4.NameNguoiGui = "Hệ thống";
                thu4.NoiDungThu = "Làng Lá - Đại Chiến Konoha gửi tặng bạn quà nạp lần đầu, chúc bạn chơi game vui vẻ!";
                Item itemthu4 = new Item(938);
                itemthu4.strOptions = "";
                thu4.Item = itemthu4;
                this.letters.add(thu4);
                this.getService().reloadLetter();
            }
            Bag.pointNAP += vang;
            Bag.pointNapNew += vang;
            user.coin -= coin;
            phucLoi.napNgay += vang;
            phucLoi.napTuan += vang;
            if (phucLoi.napDon < vang)
                phucLoi.napDon = vang;

            if (phucLoi.nap3moc < vang)
                phucLoi.nap3moc = vang;
            if (newDay) {
                newDay = false;
                phucLoi.napLienTuc++;
            }
            phucLoi.listnap.add(vang);
            service.sendChar();





            user.session.sendMessage(HanderMessage.SendThongBao("Đổi thành công " + coin, HanderMessage.WHITE));
            CharDB.updateDBcoin(coin, user.ID_USER);
        } catch (Exception e) {
            service.alertMessage("Co loi say ra vui long thu lai sau");
            Log.error("Loi doi coin " + e);
            return;
        }
    }

    public byte getMaxLevelItem(byte level) {
        if (level < 20) {
            return 4;
        } else if (level < 30) {
            return 8;
        } else if (level < 40) {
            return 12;
        } else if (level < 50) {
            return 14;
        } else {
            return 16;
        }
    }

    public void updateEveryHalfSecond() {
        try {
            if (!InfoGame.isDie && !isClean) {
                int hp = options[136] + options[143] + options[200] + options[256] + options[26];
                int mp = options[27] + options[137] + options[257];
                if (hp > 0) {
                    addHp(hp);
                    msgUpdateHp();
                }
                if (mp > 0) {
                    addMp(mp);
                    msgUpdateMp();
                }
                try {
                    if (Info.idClass == 3 && level() >= 50 && (Point.hp < maxHP / 5)) {
                        if (getEffect(34) == null && getEffect(61) == null) {
                            com.sg188.data.Skill skill = findSkillWithId(17);
                            if (skill == null || skill.level < 1) {
                                return;
                            }
                            ItemOption[] options = skill.getItemOption();
                            addEffect(new Effect((short) 34, options[0].getvalue(), System.currentTimeMillis(), 3000));
                            addEffect(new Effect((short) 61, options[1].getvalue(), System.currentTimeMillis(), 3000));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            Log.error("Loi update hoi hp mp item body " + e.getMessage());
        }
    }

    public Skill findSkillWithId(int id) {
        for (Skill skill : this.Skill.arraySkill) {
            if (skill.idTemplate == id) {
                return skill;
            }
        }
        return null;
    }

    public void updateEqip_1(Message msg) {
        try {
            byte typeBag = msg.readByte();
            short index_item = msg.readShort();
            byte sizeItem = msg.readByte();
            Vector<Item> items = new Vector<>();
            boolean[] list = new boolean[Bag.arrItemBag.length];
            while (msg.reader.Avali() > 0) {
                int index = msg.readShort();
                if (index < 0 || index >= Bag.arrItemBag.length) {
                    continue;
                }
                if (!list[index]) {
                    list[index] = true;
                    Item item = Bag.arrItemBag[index];
                    if (item != null)
                        items.add(item);
                }
            }
            if (items.isEmpty()) {
                return;
            }
            if (items.size() < sizeItem) {
                return;
            }
            Item itemEqip = this.checkBag(typeBag)[index_item];
            if (itemEqip == null) {
                service.alertMessage("Ôi bạn ơi đừng như thế");
                return;
            }
            if (!itemEqip.isItemTrangBi()) {
                service.alertMessage("Khong phai trang bi");
                return;
            }
            if (!itemEqip.isHokage()) {
                service.alertMessage("Không phải trang bị hokage");
                return;
            }
            short countNgoc = 0;
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i) != null) {
                    countNgoc += items.get(i).amount;
                }
            }
            short soNgocCan = (short) (itemEqip.getItemTemplate().levelNeed / 10 * 100);
            int bacKhoa = 25000000;
            if (itemEqip.getItemTemplate().levelNeed / 10 == 5) {
                bacKhoa = 30000000;
            } else if (itemEqip.getItemTemplate().levelNeed / 10 == 6) {
                bacKhoa = 40000000;
                soNgocCan = 700;
            }
            if (countNgoc < soNgocCan) {
                service.alertMessage("Không đủ ngọc");
                return;
            }
            if (bacKhoa > Bag.bacKhoa) {
                service.alertMessage("Không đủ bạc khóa");
                return;
            }
            addBacKhoa(-bacKhoa);
            this.checkBag(typeBag)[index_item] = null;
            for (Item item : items) {
                removeItem(item, true);
            }
            countNgoc -= soNgocCan;
            if (itemEqip.isVuKhi()) {
                itemEqip.updateOption();
            } else if (itemEqip.isBayakugan()) {
                itemEqip.updateOptionByakugan();
            } else if (itemEqip.isRenegan()) {
                itemEqip.updateOptionRinegan();
            } else if (itemEqip.isSharigan()) {
                itemEqip.updateOptionSharigan();
            }
            Item ngocTraLai = new Item(items.get(0).id);
            ngocTraLai.amount = countNgoc;
            ngocTraLai.isLock = true;
            addItem(ngocTraLai);
            msgAddItemBag(ngocTraLai);
            itemEqip.isLock = true;
            addItem(itemEqip);
            msgAddItemBag(itemEqip);
            msgSendArrItemBag();
            msgSortItem((byte) 0);
            msgUpdateEqip(itemEqip, sizeItem, items);
        } catch (IOException e) {
            service.alertMessage("Có lỗi sảy ra vui lòng báo lại admin");
            Log.error("Loi nang cap item eqip", e);
        }
    }

    private void msgUpdateEqip(Item itemEqip, byte sizeItem, List<Item> ngoc) throws IOException {
        Message m = new Message((byte) -35);
        itemEqip.write(m.writer);
        m.writeByte(2);
        for (int i = 0; i < 2; i++) {
            m.writeShort(0);
        }
        user.session.sendMessage(m);
    }

    public void updateBuaNo(Message msg) {
        try {
            byte opindex = msg.readByte();
            byte typebag = msg.readByte();
            short index_buano = msg.readShort();
            byte sizeItem = msg.readByte();
            Vector<Item> items = new Vector<>();
            boolean[] list = new boolean[Bag.arrItemBag.length];
            while (msg.reader.Avali() > 0) {
                int index = msg.readShort();
                if (index < 0 || index >= Bag.arrItemBag.length) {
                    continue;
                }
                if (!list[index]) {
                    list[index] = true;
                    Item item = Bag.arrItemBag[index];
                    if (item != null && item.id == 160)
                        items.add(item);
                }
            }
            if (items.isEmpty()) {
                return;
            }
            if (items.size() > 6) {
                return;
            }
            Item item = checkBag(typebag)[index_buano];
            if (item != null) {
                ItemOption[] options = item.getItemOption();
                if (options.length < 2) {
                    return;
                }
                if (opindex >= options.length) {
                    return;
                }
                int num = 0;
                int valueold = options[opindex].getvalue();
                int valuenew = 0;
                int valueadd = options[opindex].f() - valueold;
                for (Item it : items) {
                    num += it.amount;
                }
                if (num / 10 > valueadd) {
                    num = num - (valueadd * 10);
                } else {
                    valueadd = num / 10;
                    num = 0;
                }
                valuenew = valueadd + valueold;
                for (Item item1 : items) {
                    removeItem(item1, true);
                }
                if (num > 0) {
                    Item tinhthach = new Item(160);
                    tinhthach.isLock = true;
                    tinhthach.amount = num;
                    addItem(tinhthach);
                }
                options[opindex].c(valuenew);
                String stropt = "";
                for (ItemOption op : options) {
                    stropt += op.g() + ";";
                }
                item.strOptions = stropt.substring(0, stropt.length() - 1);
                checkBag(typebag)[index_buano] = item;
                msgUpdateBuaNo(item, typebag, items);
            }
        } catch (Exception e) {
        }
    }

    public void msgUpdateBuaNo(Item buano, byte type, Vector<Item> tt) {
        Message m = new Message((byte) 106);
        try {
            buano.write(m.writer);
            m.writeByte(type);
            m.writeByte(tt.size());
            for (Item item : tt) {
                m.writeShort(item.index);
            }
            user.session.sendMessage(m);
        } catch (Exception e) {

        }
    }

    public void nangBuaNo(Message msg) {
        try {
            byte type = msg.readByte();
            short index = msg.readShort();
            Item buano = checkBag(type)[index];
            if (buano == null) {
                return;
            }
            ItemOption[] options = buano.getItemOption();
            if (options.length >= 2) {
                boolean full = false;
                if (options[0].getvalue() == options[0].f() && options[1].getvalue() == options[1].f()) {
                    full = true;
                } else {
                    service.alertMessage("Vui lòng luyện max cấp trước khi đổi bùa mới");
                }
                if (full) {
                    if (Bag.vang < 800) {
                        service.alertMessage("Không đủ vàng");
                        return;
                    }
                    if (Bag.bac < 1000000) {
                        service.alertMessage("Không đủ bạc");
                        return;
                    }
                    Item buanew = new Item(602);
                    buanew.isLock = true;
                    String stropt = "";
                    stropt += options[0].getId() + "," + "0" + "," + "1000" + ";";
                    stropt += options[1].getId() + "," + "0" + "," + "1000" + ";";
                    stropt += "2,100;";
                    stropt += "5,50;";
                    stropt += "197," + Utlis.nextInt(1, 3) + ";";
                    stropt += "122," + Utlis.nextInt(2, 8);
                    buanew.strOptions = stropt;
                    addItem(buanew);
                    checkBag(type)[index] = null;
                    msgAddItemBag(buanew);
                    msgUpdateItemBody_Orther();
                    addVang(-800);
                    addBac(-1000000);
                    user.session.sendMessage(HanderMessage.resetScreen());
                }
            }
        } catch (Exception e) {

        }
    }

    public void nangcapBuaSieuCap() {
        try {

            Item buano = Bag.arrItemBody[13];
            if (buano == null) {
                service.alertMessage("Không tìm thấy bùa nổ cao cấp, vui lòng đeo trang bị lên người");
                return;
            }
            if (buano.id != 602) {
                return;
            }
            ItemOption[] options = buano.getItemOption();
            if (options.length >= 2) {
                boolean full = false;
                if (options[0].getvalue() == options[0].f() && options[1].getvalue() == options[1].f()) {
                    full = true;
                } else {
                    service.alertMessage("Vui lòng luyện max cấp trước khi đổi bùa mới");
                }
                if (full) {
                    if (Bag.vang < 3000) {
                        service.alertMessage("Không đủ vàng");
                        return;
                    }
                    Item buanew = new Item(811);
                    buanew.isLock = true;
                    String stropt = "";
                    stropt += options[0].getId() + "," + "0" + "," + "1500" + ";";
                    stropt += options[1].getId() + "," + "0" + "," + "1500" + ";";
                    stropt += "2,150;";
                    stropt += "5,150;";
                    stropt += "180,150;";
                    stropt += "197," + Utlis.nextInt(1, 3) + ";";
                    stropt += "122," + Utlis.nextInt(10, 20);
                    buanew.strOptions = stropt;
                    addItem(buanew);
                    Bag.arrItemBody[13] = null;
                    msgAddItemBag(buanew);
                    msgUpdateItemBody_Orther();
                    addVang(-3000);
                    user.session.sendMessage(HanderMessage.resetScreen());
                }
            }
        } catch (Exception e) {

        }
    }

    public void nangcapBuaHienNhan() {
        try {
            Item buano = Bag.arrItemBody[13];
            if (buano == null) {
                service.alertMessage("Không tìm thấy bùa nổ siêu cấp, vui lòng đeo trang bị lên người");
                return;
            }
            if (buano.id != 811) {
                getService().serverMessage("Bùa nổ không phù hợp,Bùa nổ siêu cấp mới có thể lên bùa nổ hiền nhân");
                return;
            }
            ItemOption[] options = buano.getItemOption();
            if (options.length >= 2) {
                boolean full = false;
                if (options[0].getvalue() == options[0].f() && options[1].getvalue() == options[1].f()) {
                    full = true;
                } else {
                    service.alertMessage("Vui lòng luyện max cấp trước khi đổi bùa mới");
                }
                if (full) {
                    if (Bag.vang < 9000) {
                        service.alertMessage("Không đủ vàng");
                        return;
                    }
                    Item buanew = new Item(870);
                    buanew.isLock = true;
                    String stropt = "";
                    stropt += options[0].getId() + "," + "0" + "," + "2500" + ";";
                    stropt += options[1].getId() + "," + "0" + "," + "2500" + ";";
                    stropt += "0,1500;";
                    stropt += "1,1500;";
                    stropt += "3,3500;";
                    stropt += "2,350;";
                    stropt += "5,350;";
                    stropt += "180,450;";
                    stropt += "197," + Utlis.nextInt(5, 9) + ";";
                    stropt += "122," + Utlis.nextInt(20, 30);
                    buanew.strOptions = stropt;
                    addItem(buanew);
                    Bag.arrItemBody[13] = null;
                    msgAddItemBag(buanew);
                    msgUpdateItemBody_Orther();
                    addVang(-9000);
                    user.session.sendMessage(HanderMessage.resetScreen());
                }
            }
        } catch (Exception e) {

        }
    }

    public int idNgocUpgradate(Item item) {
        if (item.isRenegan()) {
            return 567;
        } else if (item.isSharigan()) {
            return 565;
        } else if (item.isBayakugan()) {
            return 563;
        } else {
            return 353;
        }
    }

    public void updateLucDao(Message msg) {
        try {
            byte typeBag = msg.readByte();
            short index_item = msg.readShort();
            byte sizeItem = msg.readByte();
            Vector<Item> items = new Vector<>();
            boolean[] list = new boolean[Bag.arrItemBag.length];
            while (msg.reader.Avali() > 0) {
                int index = msg.readShort();
                if (index < 0 || index >= Bag.arrItemBag.length) {
                    continue;
                }
                if (!list[index]) {
                    list[index] = true;
                    Item item = Bag.arrItemBag[index];
                    if (item != null)
                        items.add(item);
                }
            }
            if (items.isEmpty()) {
                return;
            }
            if (items.size() < sizeItem) {
                return;
            }
            Item itemEqip = this.checkBag(typeBag)[index_item];
            if (itemEqip == null) {
                service.alertMessage("Ôi bạn ơi đừng như thế");
                return;
            }
            if (!itemEqip.isItemTrangBi()) {
                service.alertMessage("Khong phai trang bi");
                return;
            }
            if (!itemEqip.W()) {
                service.alertMessage("Không đủ điều kiện , cần nâng cấp lần 1 trước");
                return;
            }
            short countNgoc = 0;
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i) != null) {
                    if (items.get(i).id != idNgocUpgradate(itemEqip)) {
                        service.alertMessage("Ngọc không phù hợp");
                        return;
                    }
                    countNgoc += items.get(i).amount;
                }
            }
            short soNgocCan = (short) (itemEqip.getItemTemplate().levelNeed / 10 * 100 + 150);
            int bacKhoa = itemEqip.getItemTemplate().levelNeed / 10 * 10000000 + 15000000;
            ;
            if (countNgoc < soNgocCan) {
                service.alertMessage("Không đủ ngọc");
                return;
            }
            if (bacKhoa > Bag.bacKhoa) {
                service.alertMessage("Không đủ bạc khóa");
                return;
            }
            addBacKhoa(-bacKhoa);
            this.checkBag(typeBag)[index_item] = null;
            for (Item item : items) {
                removeItem(item, true);
            }
            countNgoc -= soNgocCan;
            if (itemEqip.isVuKhi()) {
                itemEqip.updateOption_2();
            } else if (itemEqip.isBayakugan()) {
                itemEqip.updateOptionByakugan_2();
            } else if (itemEqip.isRenegan()) {
                itemEqip.updateOptionRinegan_2();
            } else if (itemEqip.isSharigan()) {
                itemEqip.updateOptionSharigan_2();
            }
            Item ngocTraLai = new Item(items.get(0).id);
            ngocTraLai.amount = countNgoc;
            ngocTraLai.isLock = true;
            addItem(ngocTraLai);
            msgAddItemBag(ngocTraLai);
            itemEqip.isLock = true;
            addItem(itemEqip);
            msgAddItemBag(itemEqip);
            msgSendArrItemBag();
            msgSortItem((byte) 0);
            msgUpdateEqip(itemEqip, sizeItem, items);
        } catch (Exception e) {

        }
    }

    public Effect getEffect(int id) {
        lockEff.lock();
        try {
            for (Effect effect : listEffect) {
                if (effect != null && effect.id == id) {
                    return effect;
                }
            }
        } catch (Exception e) {
            Log.error("erro get effect");
        } finally {
            lockEff.unlock();
        }
        return null;
    }

    public void removeEffect(Effect effect) {
        lockEff.lock();
        try {
            listEffect.remove(effect);
        } catch (Exception e) {
            Log.error("erro remove effect");
        } finally {
            lockEff.unlock();
        }
    }

    public void addWorld(World world) {
        synchronized (worlds) {
            if (worlds.stream().noneMatch(w -> w.getType() == world.getType())) {
                worlds.add(world);
                Log.debug("add worldType: " + world.getType());
            }
        }
    }

    public void removeWorld(byte type) {
        synchronized (worlds) {
            worlds.removeIf((t) -> t.getType() == type);
            Log.debug("remove worldType: " + type);
        }
    }

    public World findWorld(byte type) {
        synchronized (worlds) {
            for (World world : worlds) {
                if (world.getType() == type) {
                    return world;
                }
            }
            return null;
        }
    }

    public void removeMemberFromWorld(Zone pre, Zone now) {
        if (worlds != null) {
            synchronized (worlds) {
                worlds.forEach((t) -> {
                    if (t != null && t.leaveWorld(pre, now)) {
                        t.removeMember(this);
                    }
                });
            }
        }
    }

    public void addMemberForWorld(Zone pre, Zone now) {
        if (worlds != null) {
            synchronized (worlds) {
                worlds.forEach((t) -> {
                    if (t != null && t.enterWorld(pre, now)) {
                        t.addMember(this);
                    }
                });
            }
        }
    }

    public void createClan(String name) {
        if (this.clan == null) {
            try {
                Item lenh = FindItemBag(301);
                if (lenh == null) {
                    user.session.sendMessage(HanderMessage.SendThongBao("Không có gia tộc lệnh", HanderMessage.WHITE));
                    return;
                }
                if (name.equals("")) {
                    user.session.sendMessage(HanderMessage.SendThongBao("Tên không hợp lệ.", HanderMessage.WHITE));
                    return;
                }
                if (name.length() > 13) {
                    user.session.sendMessage(HanderMessage.SendThongBao("Con hãy chọn cái tên ngắn hơn.", HanderMessage.WHITE));
                    return;
                }
                Pattern p = Pattern.compile("^[a-zA-Z0-9]+$"); // Cho phép ký tự chữ cái (viết thường hoặc viết hoa) và số
                Matcher m1 = p.matcher(name);
                if (!m1.find()) {
                    user.session.sendMessage(HanderMessage.SendThongBao("Tên tài khoản có kí tự lạ.", HanderMessage.WHITE));
                    return;
                }
                ClanDAO clanDAO = Clan.getClanDAO();
                if (clanDAO.checkExist(name)) {
                    user.session.sendMessage(HanderMessage.SendThongBao("Tên gia tộc đã tồn tại.", HanderMessage.WHITE));
                    return;
                }
                removeItem(lenh, true);
                Clan clan = new Clan();
                clan.setName(name);
                clan.setMainName(this.Info.name);
                clan.setAlert("");
                clan.setLevel((byte) 1);
                clan.setCoin(0);
                clan.setExp(0);
                clan.setCountClan(20);
                clan.setRegDate(new Date());
                clan.setOpenDun((byte) 1);
                clan.writeLog(clan.getMainName(), "đã tạo gia tộc ", clan.getCoin());
                this.clan = clan;
                Clan.getClanDAO().save(clan);
                Member mem = Member.builder()
                        .classId(this.Info.idClass)
                        .level(level())
                        .type(Clan.TYPE_TOCTRUONG)
                        .idChar(this.Info.idChar)
                        .name(this.Info.name)
                        .pointClan(0)
                        .pointClanWeek(0)
                        .build();
                mem.setChar(this);
                mem.setOnline(true);
                clan.memberDAO.save(mem);
                service.resetScreen();
                service.createGiaToc();
                service.showGiaToc();
                zone.SendMessageInZone(HanderMessage.sendGiaToc(this));
            } catch (Exception e) {
            }
        }
    }

    public void clanInvite(Message ms) {
        if (this.clan != null) {
            int typeClan = this.clan.getMemberByName(this.Info.name).getType();
            if (typeClan == Clan.TYPE_TOCTRUONG || typeClan == Clan.TYPE_TOCPHO || typeClan == Clan.TYPE_TRUONGLAO) {
                try {
                    String name = ms.readUTF();
                    Char _char = zone.findCharInMapByName(name);
                    if (_char != null) {
                        if (_char == this) {
                            return;
                        }
                        if (_char.clan != null) {
                            return;
                        }
                        Invite.PlayerInvite p = _char.invite.findCharInvite(Invite.GIA_TOC, this.id);
                        if (p != null) {
                            service.alertMessage("Không thể mời vào vào liên tục. Vui lòng thử lại sau 30s nữa.");
                            return;
                        }
                        _char.invite.addCharInvite(Invite.GIA_TOC, this.id, 30);
                        String type = "";
                        if (typeClan == 5) {
                            type = "Tộc trưởng";
                        } else if (typeClan == 4) {
                            type = "Tộc phó";
                        } else if (typeClan == 3) {
                            type = "Trưởng lão";
                        }
                        _char.nameInvite = Info.name;
                        _char.getService().clanInvite(Info.name, type, clan.getName());
                    }
                } catch (Exception ex) {
                    Log.error("err: " + ex.getMessage(), ex);
                }
            } else {
                service.alertMessage("Bạn không có quyền này.");
            }
        } else {
            service.alertMessage("Bạn không có gia tộc.");
        }
    }

    public void createGroup() {
        try {
            if (this.group != null) {
                service.alertMessage("Không thể tạo nhóm");
                return;
            }
            int maxGroup = 6;
            int numberGroup = zone.getNumberGroup();
            if (numberGroup >= maxGroup) {
                service.serverMessage("Số nhóm trong khu vực đã đạt tối đa.");
                return;
            }
            Group group = new Group();
            MemberGroup party = new MemberGroup();
            party.charId = this.id;
            party.classID = this.Info.idClass;
            party.name = Info.name;
            party.setChar(this);
            group.add(party);
            this.group = group;
            this.group.getGroupService().playerInParty();
        } catch (Exception e) {
            Log.error(" loi create group");
        }
    }

    public Friend[] getFriends() {
        if (this.friends == null) {
            return new Friend[0];
        }
        return this.friends.values().toArray(new Friend[this.friends.size()]);
    }

    public Friend[] getEnemies() {
        if (this.enemies == null) {
            return new Friend[0];
        }
        return this.enemies.values().toArray(new Friend[this.enemies.size()]);
    }

    public void addClanPoint(int point) {
        if (this.clan != null) {
            Member mem = this.clan.getMemberByName(Info.name);
            if (mem != null) {
                mem.addPointClanWeek(point);
                mem.addPointClan(point);
                this.clan.addExp(point);
                service.serverMessage("Bạn nhận được " + point + " điểm cống hiến gia tộc.");
            }
        }
    }

    public void loadEventPoint() {
        try {
            Event event = Event.getEvent();
            if (event != null) {
                EventPoint eventPoint = event.findEventPointByPlayerID(this.id);
                if (eventPoint == null) {
                    eventPoint = event.createEventPoint();
                    eventPoint.setPlayerID(this.id);
                    eventPoint.setPlayerName(this.Info.name);

                    Gson g = new Gson();
                    PreparedStatement ps = Connect.getConnection()
                            .prepareStatement("INSERT INTO `event_points`(`event_id`, `player_id`, `point`) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, event.getId());
                    ps.setInt(2, this.id);
                    ps.setString(3, g.toJson(eventPoint.getPoints()));
                    ps.executeUpdate();
                    ResultSet generatedKeys = ps.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        eventPoint.setId(generatedKeys.getInt(1));
                    }
                    ps.close();
                    event.addEventPoint(eventPoint);
                }
                setEventPoint(eventPoint);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Char.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateEventPoint() {
        try {
            EventPoint eventPoint = getEventPoint();
            if (eventPoint != null) {
                Gson g = new Gson();
                PreparedStatement ps = Connect.getConnection()
                        .prepareStatement("UPDATE `event_points` SET `point` = ? WHERE `id` = ? LIMIT 1;");
                ps.setString(1, g.toJson(eventPoint.getPoints()));
                ps.setInt(2, eventPoint.getId());
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Char.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void requestMatchInfo(Message ms) {
        try {
            short id = ms.readShort();
            Arena arena = Arena.getArenaByID(id);
            if (arena != null) {
                if (arena.isOpened) {
                    addWorld(arena);
                    arena.join(3, this);
                    if (Utlis.nextInt(2) == 0) {
                        setXY((short) 130, (short) 566);
                    } else {
                        setXY((short) 635, (short) 566);
                    }
                    service.setXYChar();
                } else {
                    service.alertMessage("Trận đấu chưa bắt đầu.");
                }
            } else {
                service.alertMessage("Không tìm thấy trận đấu này.");
            }
        } catch (Exception e) {
            Log.error("err: " + e.getMessage(), e);
        }
    }

    public void updateItemRenew() {
        tanto.clear();
        aoChoang.clear();
        thoiTrang.clear();
        for (Item item : Bag.arrItemBag) {
            if (item != null) {
                if (item.getItemTemplate().type == 15 && item.expiry > -1) {
                    tanto.add(item);
                } else if (item.getItemTemplate().type == 12 && item.expiry > -1) {
                    aoChoang.add(item);
                } else if (item.getItemTemplate().type == 16 && item.expiry > -1) {
                    thoiTrang.add(item);
                }
            }
        }
        for (Item item : Bag.arrItemBody) {
            if (item != null) {
                if (item.getItemTemplate().type == 15 && item.expiry > -1) {
                    tanto.add(item);
                } else if (item.getItemTemplate().type == 12 && item.expiry > -1) {
                    aoChoang.add(item);
                } else if (item.getItemTemplate().type == 16 && item.expiry > -1) {
                    thoiTrang.add(item);
                }
            }
        }
        for (Item item : Bag.arrItemBody2) {
            if (item != null) {
                if (item.getItemTemplate().type == 15 && item.expiry > -1) {
                    tanto.add(item);
                } else if (item.getItemTemplate().type == 12 && item.expiry > -1) {
                    aoChoang.add(item);
                } else if (item.getItemTemplate().type == 16 && item.expiry > -1) {
                    thoiTrang.add(item);
                }
            }
        }
    }

    private void appendItems(StringBuilder strBuilder, List<Item> items, String prefix) {
        strBuilder.append(prefix);
        if (!items.isEmpty()) { // Kiểm tra xem danh sách có trống không
            for (int i = 0; i < items.size(); i++) {
                Item itm = items.get(i);
                strBuilder.append(",");
                strBuilder.append(itm.getItemTemplate().name);
            }
            // Thêm dấu chấm phẩy sau mỗi loại mục
        }
        strBuilder.append(";");
    }

    public void attackCharacter(Message ms) {
        try {
            if (InfoGame.isDie) {
                return;
            }
            short idSkil = ms.readShort();
            int id = ms.readInt();
            Skill skill = this.getSkillWithIdTemplate(idSkil);
            if (System.currentTimeMillis() - skill.time < skill.coolDown)
                return;
            if (isDontAttack())
                return;
            Char charFocus = zone.findCharInMap(id);
            ArrayList<Char> chars = new ArrayList<>();
            if (charFocus != null && charFocus != this && charFocus.user != null && charFocus.Point.hp > 0 && isMeCanAttackOtherPlayer(charFocus)) {
                if (Utlis.getRange(charFocus.Info.cx, this.Info.cx) <= skill.rangeNgang && Utlis.getRange(charFocus.Info.cy, this.Info.cy) <= skill.rangeDoc) {
                    chars.add(charFocus);
                    if (skill.idTemplate == 11) {
                        setXY(charFocus.Info.cx, charFocus.Info.cy);
                        service.setXYChar();
                        Effect effect = new Effect((short) 36, 0, System.currentTimeMillis(), getParamSkill(11, 103));
                        charFocus.addEffect(effect);
                    }
                }
            }
            if ((skill.idTemplate == 2 || skill.idTemplate == 8 || skill.idTemplate == 14 || skill.idTemplate == 20 || skill.idTemplate == 26)) {
                List<Char> charList = zone.getChars();
                int maxTarget = skill.maxTarget;
                if (skill.level > 17)
                    maxTarget = 3;

                int i = 1;
                try {
                    for (Char p : charList) {
                        if (i == maxTarget)
                            return;
                        if (p != null && p.user != null && p != this && p != charFocus && !p.InfoGame.isDie) {
                            if (Utlis.getRange(p.Info.cx, this.Info.cx) <= skill.rangeNgang && Utlis.getRange(p.Info.cy, this.Info.cy) <= skill.rangeDoc && isMeCanAttackOtherPlayer(p)) {
                                chars.add(p);
                                i++;
                            }
                        }
                    }
                } catch (Exception e) {

                }
            }
            if (chars.isEmpty()) {
                return;
            }
            int manaUse = skill.mpUse;
            if (this.Point.mp < manaUse) {
                service.serverMessage("Không đủ mp để sử dụng chiêu");
                return;
            }
            addMp(-manaUse);
            attackCharacter(chars, skill);
        } catch (Exception e) {
        }
    }

    private boolean isMeCanAttackOtherPlayer(Char cAtt) {
        if (zone.isLoiDai()) {
            if ((InfoGame.TypePk == 2 && cAtt.InfoGame.TypePk == 2))
                return true;
        }
        if (!inLangCo && !zone.isKRC() && buaBaoHo)
            return false;
        return ((InfoGame.TypePk == 3 || cAtt.InfoGame.TypePk == 3) && !isTeam(cAtt))
                || (zone.isKRC() && !isTeam(cAtt) && !isClan(cAtt))
                || (InfoGame.TypePk == 2 && cAtt.InfoGame.TypePk == 2 && !isTeam(cAtt) && !isClan(cAtt))
                || (idCharPk == cAtt.id && cAtt.idCharPk == id && isTyVo && cAtt.isTyVo)
                || (idCuuSat == cAtt.id && cAtt.idCuuSat == id && (isCuuSat || cAtt.isCuuSat));
    }

    public boolean isTeam(Char c) {
        if (group != null) {
            synchronized (group.memberGroups) {
                for (MemberGroup p : group.memberGroups) {
                    if (c.id == p.charId) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isClan(Char c) {
        if (clan != null) {
            Member m = clan.getMemberByName(c.Info.name);
            if (m != null) {
                return true;
            }
        }
        return false;
    }

    private boolean isDontAttack() {
        return false;
    }

    public void attackCharacter(ArrayList<Char> chars, Skill skill) {
        int damage = this.damage;
        int option92 = 0;
        int skillOptions[] = new int[DataCenter.gI().ItemOptionTemplate.length];
        try {
            for (com.sg188.data.Skill skill1 : Skill.arraySkill) {
                if (skill1 != null && skill1.level > 0 && skill1.getSkillTemplate().type != 5) {
                    for (ItemOption op : skill1.getItemOption()) {
                        if (op != null && op.getId() == 92) {
                            option92 = op.getvalue();
                            break;
                        }
                    }
                }
            }
            for (ItemOption op : skill.getItemOption()) {
                if (op != null) {
                    skillOptions[op.getId()] = op.getvalue();
                }
            }
        } catch (Exception e) {
        }
        int skillTemplateId = skill.idTemplate;
        if (skillTemplateId == 33) {
            for (Char _char : chars) {
                if (_char != null && !_char.isClean) {
                    _char.addEffect(new Effect((short) 78, 0, System.currentTimeMillis(), getParamSkill(33, 268)));
                }
            }
        } else if (skillTemplateId == 4) {
            int param = getParamSkill(4, 181) + getParamSkill(1, 269);
            for (Char _char : chars) {
                if (_char != null && !_char.isClean) {
                    _char.addEffect(new Effect((short) 55, param, System.currentTimeMillis(), 10000));
                    _char.addEffect(new Effect((short) 56, getParamSkill(4, 182), System.currentTimeMillis(), 10000));
                    if (getChiSoFormSkill(270) != 0) {
                        _char.addEffect(new Effect((short) 76, getChiSoFormSkill(270), System.currentTimeMillis(), 10000));
                    }
                }
            }
        } else if ((skillTemplateId == 2 || skillTemplateId == 8 || skillTemplateId == 14 || skillTemplateId == 20 || skillTemplateId == 26)) {
            damage += damage * option92 / 100;
        }
        if (isFatal > 0) {
            Effect eff1 = getEffect(53);
            Effect eff2 = getEffect(54);
            if (eff1 != null && eff2 != null) {
                try {
                    removeEffect(eff1);
                    HanderEff.RemovePointEff(this, eff1);
                    msgRemoveEffect(eff1);
                    removeEffect(eff2);
                    HanderEff.RemovePointEff(this, eff2);
                    msgRemoveEffect(eff2);
                } catch (Exception e) {

                }
            }
            isFatal = 0;
            Char _char = chars.get(0);
            if (_char != null && !_char.isClean)
                chars.get(0).addEffect(new Effect((short) 11, 0, System.currentTimeMillis(), 2000));
        }
        int exactly = this.exactly + skillOptions[65];
        damage += skillOptions[78];
        for (Char pl : chars) {
            pl.lock.lock();
            try {
                if (pl != null && !pl.isClean && !pl.InfoGame.isDie) {
                    zone.SendMessageInZone(HanderMessage.SendAttackChar(Info.idEntity, Point.mp, (short) skill.index, pl.Info.idEntity));
                    int khang = 0;
                    switch (pl.Info.idhe) {
                        case 0:
                            break;
                        case 1:
                            damage += lightningAttackBoost;
                            damage += skillOptions[75];
                            break;
                        case 2:
                            damage += earthAttackBoost;
                            damage += skillOptions[73];
                            break;
                        case 3:
                            damage += waterAttackBoost;
                            damage += skillOptions[74];
                            break;
                        case 4:
                            damage += fireAttackBoost;
                            damage += skillOptions[77];
                            break;
                        case 5:
                            damage += waterAttackBoost;
                            damage += skillOptions[76];
                            break;
                    }
                    switch (Info.idhe) {
                        case 1:
                            khang = pl.lightningResistance;
                            break;
                        case 2:
                            khang = pl.earthResistance;
                            break;
                        case 3:
                            khang = pl.waterResistance;
                            break;
                        case 4:
                            khang = pl.fireResistance;
                            break;
                        case 5:
                            khang = pl.windResistance;
                            break;
                    }
                    khang -= options[269];
                    short bong = (short) (burn + skillOptions[71] - pl.reduceBurn);
                    short suyyeu = (short) (weaken + skillOptions[68] - pl.reduceWeaken);
                    short doc = (short) (poison + skillOptions[69] - pl.reducePoison);
                    short lamcham = (short) (slow + skillOptions[70] - pl.reduceSlow);
                    short choang = (short) (stun + skillOptions[72] - pl.reduceStun);
                    boolean isBong = Utlis.nextInt(2000) < bong;
                    boolean issuyyeu = Utlis.nextInt(2000) < suyyeu;
                    boolean isdoc = Utlis.nextInt(2000) < doc;
                    boolean islamcham = Utlis.nextInt(2000) < lamcham;
                    boolean ischoang = Utlis.nextInt(2000) < choang;
                    if (isdoc) {
                        pl.addEffect(new Effect((short) 9, 0, System.currentTimeMillis(), 2000));
                    }
                    if (isBong) {
                        pl.addEffect(new Effect((short) 11, 0, System.currentTimeMillis(), 2000));
                    }
                    if (issuyyeu) {
                        pl.addEffect(new Effect((short) 8, 0, System.currentTimeMillis(), 2000));
                    }
                    if (ischoang) {
                        pl.addEffect(new Effect((short) 12, 0, System.currentTimeMillis(), 1000));
                    }
                    if (islamcham) {
                        pl.addEffect(new Effect((short) 38, 0, System.currentTimeMillis(), 2000));
                    }
                    if (getEffect(8) != null) {
                        damage /= 2;
                    }
                    if (pl.getEffect(11) != null) {
                        damage += damage;
                    }
                    if (pl.getEffect(8) != null) {
                        khang /= 2;
                    }
                    int crit = critical >= 3000 ? 3000 : critical;
                    boolean chi_mang = Utlis.isCriticalHit(crit - pl.reduceCriticalDamage);
                    if (chi_mang) {
                        int num = criticalAttack;
                        num -= pl.criticalDefense * 2;
                        damage = damage + (damage * num / 100);
                    }
                    if (Utlis.nextInt(0, 1000) < ignoreResistance)
                        khang = 0;
                    double percentDameReduction = calculateDamageReduction(khang);
                    damage -= damage * percentDameReduction / 100;
                    damage = Math.max(damage - pl.damageReduction, 0);
                    int miss = pl.miss;
                    int exactlyAttack = exactly - miss;
                    boolean isMiss = exactlyAttack < 0 ? Utlis.randomBoolean(100, 90) : Utlis.randomBoolean(100, 1);
                    if (isMiss) {
                        damage = 0;
                    }
                    try {
                        Effect effect = pl.getEffect(34);
                        if (effect != null) {
                            pl.addHp(effect.value);
                        }
                    } catch (Exception e) {

                    }
                    pl.addHp(-damage);
                    zone.SendMessageInZone(HanderMessage.SendHpAttack(pl.Info.idEntity, pl.Point.hp, pl.Point.mp, false, pl.Info.cx, pl.Info.cy, Info.name));
                    if (pl.Point.hp <= 0) {
                        if (buaUeTho && !inLangCo && !pl.inLangCo && !zone.isLoiDai()) {
                            pl.canRevive = false;
                            pl.service.sendCaptCha(Info.name);
                            Effect bua = getEffect(81);
                            if (bua != null) {
                                removeEffect(bua);
                                HanderEff.RemovePointEff(this, bua);
                                msgRemoveEffect(bua);
                            }
                        }
                        if (pl.idCharPk == id) {
                            service.resuiltTyVo(pl.id, (byte) 2);
                            pl.service.resuiltTyVo(id, (byte) 3);
                            idCharPk = -1;
                            pl.idCharPk = -1;
                            InfoGame.TypePk = 0;
                            pl.InfoGame.TypePk = 0;
                        }
                        if (zone.isTranhDoatLanhTho()) {
                            pointTranhDoat += 5;
                            getService().sendPointMap(pointTranhDoat);
                        }
                        if ((pl.isCuuSat || isCuuSat) && pl.idCuuSat != -1 && idCuuSat != -1 && pl.idCuuSat == id) {
                            service.endCuuSat(id, true);
                            pl.service.endCuuSat(id, true);
                            service.endCuuSat(pl.id, true);
                            pl.service.endCuuSat(pl.id, true);
                            if (pl.isCuuSat) {
                                pl.Info.lvPk++;
                            }
                            if (isCuuSat) {
                                if (zone.isLangCo) {
                                    getService().serverMessage("Bạn đang có điểm PK không thể vào khu vực này");
                                    Map.maps[Info.mapReSpawm].addChar(this);
                                }
                                Info.lvPk++;
                            }
                            idCuuSat = -1;
                            isCuuSat = false;
                            pl.idCuuSat = -1;
                            pl.isCuuSat = false;
                        }
                        if (zone.isLangCo) {
                            if (InfoGame.TypePk == 3 || InfoGame.TypePk == 2) {
                                Info.lvPk++;
                                getService().serverMessage("Bạn đang có điểm PK không thể vào khu vực này");
                                Map.maps[Info.mapReSpawm].addChar(this);
                            }
                        }

                    }
                }
            } catch (Exception e) {

            } finally {
                pl.lock.unlock();
            }
        }

    }

    public double calculateDamageReduction(int resistance) {
        double damageReduction;

        // Hằng số để điều chỉnh đường cong lôgarit
        double logBase = 1.5;
        double scaleFactor = 80.0 / Math.log(5000.0 / logBase);

        // Tính phần trăm giảm sát thương dựa trên hàm lôgarit
        damageReduction = scaleFactor * Math.log(resistance / logBase);

        // Giới hạn phần trăm giảm sát thương từ 0% đến 80%
        damageReduction = Math.min(damageReduction, 80.0);
        damageReduction = Math.max(damageReduction, 0.0);

        return damageReduction;
    }

}
