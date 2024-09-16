package com.sg188.real;

import InfoChar.*;
import SqlConnection.CharDB;
import SqlConnection.Connect;
import Template.TemplateThu;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sg188.PhucLoi.TemplatePL;
import com.sg188.clan.Clan;
import com.sg188.clan.Member;
import com.sg188.data.SkillClan;
import com.sg188.data.TaskTemplate;
import com.sg188.lib.Log;
import com.sg188.lib.ParseData;
import com.sg188.lib.Utlis;
import com.sg188.server.ServerManager;
import com.sg188.server.Service;
import com.sg188.server.Session;
import com.sg188.task.Task;
import com.sg188.task.TaskFactory;
import com.sg188.task.TaskOrder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class User {
    public Session session;
    public Service service;
    public String username;
    public String password;
    public String[] subNameChar;
    public Char[] mCharShowLogin;
    public byte numberChar;
    public int ID_USER;
    public boolean isAdmin;
    public boolean isEntered;
    public boolean isCleaned;
    public boolean isDuplicate;
    public int coin;
    public boolean isLoadFinish;
    public boolean actived;
    public Char mChar;
    public Vector<Char> chars;
    private int banner;
    public int topSm;

    public User(Session client, String username, String password) {
        this.session = client;
        this.service = client.getService();
        this.username = username;
        this.password = password;
    }

    public void cleanUp() {
        this.isCleaned = true;
        this.mChar = null;
        this.session = null;
        this.service = null;
        Log.debug("clean user " + this.username);
    }

    public HashMap<String, Object> getUserMap() {
        try {
            ArrayList<HashMap<String, Object>> list;
            try (Connection conn = Connect.getConnection()) {
                PreparedStatement stmt = conn.prepareStatement("SELECT * from users where username = ?");
                stmt.setString(1, this.username);
                ResultSet data = stmt.executeQuery();
                try {
                    list = CharDB.convertResultSetToList(data);
                } finally {
                    data.close();
                    stmt.close();
                }
                if (list.isEmpty()) {
                    return null;
                }
                HashMap<String, Object> map = list.get(0);
                if (map != null) {
                    String passwordHash = (String) map.get("password");
                    if (!passwordHash.equals(password)) {
                        return null;
                    }
                }
                return map;
            }
        } catch (SQLException e) {
            Log.error("getUserMap() err", e);
        }

        return null;
    }

    public void initCharacterList() {
        long timeLoad = System.currentTimeMillis();
        try (Connection conn = Connect.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * from player where idchar = ? LIMIT 1;")) {
            stmt.setInt(1, this.ID_USER);
            ResultSet data = stmt.executeQuery();
            try {
                this.chars = new Vector<>();
                ObjectMapper s = new ObjectMapper();
                while (data.next()) {
                    Char _char = new Char();
                    JSONArray jArr = (JSONArray) JSONValue.parse(data.getString("Info"));
                    int len = jArr.size();
                    _char.id = data.getInt("IdChar");
                    _char.Info = new InfoChar();
                    _char.Info.idEntity = _char.id;
                    _char.Info.name = data.getString("Name");
                    for (int i = 0; i < len; i++) {
                        JSONObject obj = (JSONObject) jArr.get(i);
                        _char.Info.idChar = Byte.parseByte(obj.get("idchar").toString());
                        _char.Info.sachChienDau = Integer.parseInt(obj.get("sachchiendau").toString());
                        _char.Info.gioiTinh = Byte.parseByte(obj.get("gioitinh").toString());
                        _char.Info.idClass = Byte.parseByte(obj.get("idclass").toString());
                        _char.Info.idhe = Byte.parseByte(obj.get("idhe").toString());
                        _char.Info.selectDanhHieu = Byte.parseByte(obj.get("selectdanhhieu").toString());
                        _char.Info.rank = Byte.parseByte(obj.get("rank").toString());
                        _char.Info.selectCaiTrang = Byte.parseByte(obj.get("selectcaitrang").toString());
                        _char.Info.IdGiaToc = Short.parseShort(obj.get("idclan").toString());
                        _char.Info.RoleGiaToc = Byte.parseByte(obj.get("roleclan").toString());
                        _char.Info.BuffEXP = Byte.parseByte(obj.get("buffexp").toString());
                        _char.Info.chuyenCan = Integer.parseInt(obj.get("chuyencan").toString());
                        _char.Info.chuyenCanTuan = Short.parseShort(obj.get("chuyencantuan").toString());
                        _char.Info.cuaCai = Integer.parseInt(obj.get("cuacai").toString());
                        _char.Info.cuaCaiTuan = Integer.parseInt(obj.get("cuacaituan").toString());
                        _char.Info.cuongHoa = Integer.parseInt(obj.get("cuonghoa").toString());
                        _char.Info.cuaCaiTuan = Integer.parseInt(obj.get("cuonghoatuan").toString());
                        _char.Info.loiDai = Integer.parseInt(obj.get("loidai").toString());
                        _char.Info.luyenTap = Integer.parseInt(obj.get("luyentap").toString());
                        _char.Info.countRuong = Byte.parseByte(obj.get("countruong").toString());
                        _char.Info.expCheTao = Integer.parseInt(obj.get("expchetao").toString());
                        _char.Info.levelCheTao = Byte.parseByte(obj.get("levelchetao").toString());
                        _char.Info.banCTG = Boolean.parseBoolean(obj.get("banctg").toString());
                        _char.Info.typeVQMM = Byte.parseByte(obj.get("typevqmm").toString());
                        _char.Info.timeLogin = Long.parseLong(obj.get("timelogin").toString());
                        _char.Info._mapID = Byte.parseByte(obj.get("map").toString());
                        _char.Info.mapReSpawm = Byte.parseByte(obj.get("maphs").toString());
                        _char.Info.countKham = Byte.parseByte(obj.get("countkham").toString());
                        _char.Info.lvPk = Byte.parseByte(obj.get("lvpk").toString());
                        _char.Info.TimeStartHD = Long.parseLong(obj.get("timestart").toString());
                        if (obj.containsKey("cx") && obj.containsKey("cy")) {
                            _char.Info.cx = Short.parseShort(obj.get("cx").toString());
                            _char.Info.cy = Short.parseShort(obj.get("cy").toString());
                        }

                        if (obj.containsKey("pointnapnew")) {
                            _char.Bag.pointNapNew = Integer.parseInt(obj.get("pointnapnew").toString());
                        }


                        if (obj.containsKey("khoaexp")) {
                            _char.Info.khoaExp = Boolean.parseBoolean(obj.get("khoaexp").toString());
                        }
                        if (obj.containsKey("numct")) {
                            _char.Info.numct = Byte.parseByte(obj.get("numct").toString());
                        } else {
                            _char.Info.numct = 17;
                        }
                        if (obj.containsKey("lvmax")) {
                            _char.Info.levelMaxViThu = Byte.parseByte((obj.get("lvmax").toString()));
                        } else
                            _char.Info.levelMaxViThu = 4;
                        if (obj.containsKey("camthuat")) {
                            _char.Info.countCamThuat = Byte.parseByte((obj.get("camthuat").toString()));
                        } else
                            _char.Info.countCamThuat = 1;
                        if (obj.containsKey("idcamThuat")) {
                            _char.idCamThuat = Integer.parseInt(obj.get("idcamThuat").toString());
                        } else {
                            _char.idCamThuat = -1;
                        }

                        if (obj.containsKey("countUseBinhHoatLuc")) {
                            _char.Info.countUseBinhHoatLuc = Byte.parseByte((obj.get("countUseBinhHoatLuc").toString()));
                        } else
                            _char.Info.countUseBinhHoatLuc = 0;
                        if (obj.containsKey("countDauCoc")) {
                            _char.Info.countDauCoc = Byte.parseByte((obj.get("countDauCoc").toString()));
                        } else
                            _char.Info.countDauCoc = 0;
                        if (obj.containsKey("KinhNghiemVoHan")) {
                            _char.Info.KinhNghiemVoHan = Byte.parseByte((obj.get("KinhNghiemVoHan").toString()));
                        } else
                            _char.Info.KinhNghiemVoHan = 0;

                        if (obj.containsKey("idKlT")) {
                            _char.idKhuLuyenTap = Integer.parseInt(obj.get("idKlT").toString());
                        } else
                            _char.idKhuLuyenTap = -1;
                        if (obj.containsKey("idDiaCung")) {
                            _char.idDiaCung = Integer.parseInt(obj.get("idDiaCung").toString());
                        } else {
                            _char.idDiaCung = -1;
                        }
                        if (obj.containsKey("idDungeonEvent")) {
                            _char.idDungeonEvent = Integer.parseInt(obj.get("idDungeonEvent").toString());
                        } else {
                            _char.idDungeonEvent = -1;
                        }
                        if (obj.containsKey("taskseal")) {
                            JSONObject taskseal = (JSONObject) JSONValue.parse(obj.get("taskseal").toString());
                            _char.taskSeal = Boolean.parseBoolean(taskseal.get("isTask").toString());
                            _char.typeSeal = taskseal.get("type").toString();
                            _char.stepSeal = Byte.parseByte(taskseal.get("step").toString());
                        } else {
                            _char.taskSeal = false;
                            _char.typeSeal = "";
                            _char.stepSeal = 0;
                        }
                        if (obj.containsKey("security")) {
                            JSONObject security = (JSONObject) JSONValue.parse(obj.get("security").toString());
                            _char.isSecurity = Boolean.parseBoolean(security.get("isSecurity").toString());
                            _char.timeRemoveSecurity = Integer.parseInt(security.get("timeRemoveSecurity").toString());
                            _char.passwordSecurity = security.get("passwordSecurity").toString();
                        }
                        if (obj.containsKey("nvth")) {
                            _char.countFinishDay = Byte.parseByte(obj.get("nvth").toString());
                        } else
                            _char.countFinishDay = 10;
                        if (obj.containsKey("countLoopBoss")) {
                            _char.countLoopBoss = Byte.parseByte(obj.get("countLoopBoss").toString());
                        } else
                            _char.countLoopBoss = 3;
                        if (obj.containsKey("counttbgt")) {
                            _char.Info.countTBGT = Byte.parseByte(obj.get("counttbgt").toString());
                        } else
                            _char.Info.countTBGT = 50;
                        if (obj.containsKey("isUpdateOption")) {
                            _char.isUpdateOption = Boolean.parseBoolean(obj.get("isUpdateOption").toString());
                        }
                        if (obj.containsKey("timeChangeName")) {
                            _char.timeChangeName = Long.parseLong(obj.get("timeChangeName").toString());
                        }
                        if (obj.containsKey("timeOutClan")) {
                            _char.timeOutClan = Long.parseLong(obj.get("timeOutClan").toString());
                        }
                        if (obj.containsKey("countHu")) {
                            _char.Info.countHu = Byte.parseByte(obj.get("countHu").toString());
                        }
                        if (obj.containsKey("timeOffline")) {
                            _char.timeOffline = Short.parseShort(obj.get("timeOffline").toString());
                        }
                        if (obj.containsKey("inLangCo")) {
                            _char.inLangCo = Boolean.parseBoolean(obj.get("inLangCo").toString());
                        }
                        if (obj.containsKey("taskId")) {
                            _char.taskId = Short.parseShort(obj.get("taskId").toString());
                        }else {
                            _char.taskId = 0;
                        }
                        if(obj.containsKey("treasure")){
                            JSONObject _treasure = (JSONObject) JSONValue.parse(obj.get("treasure").toString());
                            int id = Integer.parseInt(_treasure.get("id").toString());
                            int quantity = Integer.parseInt(_treasure.get("quantity").toString());
                            int index = Integer.parseInt(_treasure.get("index").toString());
                            _char.treasure = Treasure.builder().id(id).index(index).quantity(quantity).rate(0).build();
                        }
                        if(obj.containsKey("shoprank")){
                            JsonArray jsonElements = JsonParser.parseString(obj.get("shoprank").toString()).getAsJsonArray();
                            for (int j = 0; j < jsonElements.size(); j++) {
                                _char.shoprank.add(jsonElements.get(j).getAsInt());
                            }
                        }
                        if(obj.containsKey("theGiuTien")){
                            _char.theGiuTien = Long.parseLong(obj.get("theGiuTien").toString());
                        }else {
                            _char.theGiuTien = 0;
                        }
                        if (obj.containsKey("bagAdd")) {
                            _char.bagAdd = Short.parseShort(obj.get("bagAdd").toString());
                        }

                    }
                    jArr.clear();
                    jArr = (JSONArray) JSONValue.parse(data.getString("inventory"));
                    len = jArr.size();
                    _char.Bag = new InfoInventory();
                    if (jArr.size() > 0) {
                        for (int i = 0; i < 1; i++) {
                            JSONObject obj = (JSONObject) jArr.get(i);
                            if(obj.containsKey("bac")){
                                _char.Bag.bac = Integer.parseInt(obj.get("bac").toString());
                            }else {
                                _char.Bag.bac = data.getInt("bac");
                            }
                            if(obj.containsKey("vang")){
                                _char.Bag.vang = Integer.parseInt(obj.get("vang").toString());
                            }else {
                                _char.Bag.vang = data.getInt("vang");
                            }
                            _char.Bag.bacKhoa = Integer.parseInt(obj.get("backhoa").toString());
                            _char.Bag.bacBox = Integer.parseInt(obj.get("bacbox").toString());
                            _char.Bag.bacKhoaBox = Integer.parseInt(obj.get("backhoabox").toString());
                            _char.Bag.vangKhoa = Integer.parseInt(obj.get("vangkhoa").toString());
                            _char.Bag.vangKhoaBox = Integer.parseInt(obj.get("vangkhoabox").toString());
                            _char.Bag.vangBox = Integer.parseInt(obj.get("vangbox").toString());
                            _char.Bag.pointNAP = Integer.parseInt(obj.get("pointnap").toString());
                            _char.Bag.stnSo = Byte.parseByte(obj.get("stnso").toString());
                            _char.Bag.stnTrung = Byte.parseByte(obj.get("stntrung").toString());
                            _char.Bag.stnCao = Byte.parseByte(obj.get("stncao").toString());
                            _char.Bag.sknSo = Byte.parseByte(obj.get("sknso").toString());
                            _char.Bag.sknTrung = Byte.parseByte(obj.get("skntrung").toString());
                            _char.Bag.sknCao = Byte.parseByte(obj.get("skncao").toString());
                            _char.Bag.Banh = Byte.parseByte(obj.get("banh").toString());
                            if (obj.containsKey("banhUbao")) {
                                _char.Bag.banhUBao = Byte.parseByte(obj.get("banhUbao").toString());
                            }
                            if (obj.containsKey("pointnapnew")) {
                                _char.Bag.pointNapNew = Integer.parseInt(obj.get("pointnapnew").toString());
                            }
                            if (obj.containsKey("sach")) {
                                _char.Bag.itemSach = new Item((JSONObject) obj.get("sach"));
                            }
                        }
                    }
                    jArr.clear();
                    jArr = (JSONArray) JSONValue.parse(data.getString("Point"));
                    len = jArr.size();
                    _char.Point = new InfoPoint();
                    _char.Point.arrayTiemNang = new int[4];
                    for (int i = 0; i < len; i++) {
                        JSONObject obj = (JSONObject) jArr.get(i);
                        _char.Point.arrayTiemNang[0] = Integer.parseInt(obj.get("sm").toString());
                        _char.Point.arrayTiemNang[1] = Integer.parseInt(obj.get("chakra").toString());
                        _char.Point.arrayTiemNang[2] = Integer.parseInt(obj.get("hp").toString());
                        _char.Point.arrayTiemNang[3] = Integer.parseInt(obj.get("mp").toString());
                        _char.Point.diemTiemNang = Integer.parseInt(obj.get("pointTn").toString());
                        _char.Point.diemKyNang = Integer.parseInt(obj.get("pointKn").toString());
                        _char.Point.hoatLuc = Integer.parseInt(obj.get("hoatluc").toString());
                        _char.Point.exp = Long.parseLong(obj.get("exp").toString());
                        if (obj.containsKey("diempt")) {
                            _char.Point.diempt = Byte.parseByte(obj.get("diempt").toString());
                        }
                        if (obj.containsKey("maxpt")) {
                            _char.Point.maxpt = Byte.parseByte(obj.get("maxpt").toString());
                        }
                        if (obj.containsKey("expsach")) {
                            _char.Point.expsach = Integer.parseInt(obj.get("expsach").toString());
                        }

                    }
                    jArr.clear();
                    jArr = (JSONArray) JSONValue.parse(data.getString("PhucLoi"));
                    len = jArr.size();
                    _char.phucLoi = new InfoPhucLoi();
                    if (jArr != null) {
                        for (int i = 0; i < len; i++) {
                            JSONObject obj = (JSONObject) jArr.get(i);
                            if (i == 0) {
                                _char.phucLoi.timeOnline = Integer.parseInt(obj.get("timeOnline").toString());
                                _char.phucLoi.soNgayOnline = Integer.parseInt(obj.get("songayonline").toString());
                                _char.phucLoi.tieuNgay = Integer.parseInt(obj.get("tieungay").toString());
                                _char.phucLoi.tieuTuan = Integer.parseInt(obj.get("tieutuan").toString());
                                _char.phucLoi.goiChiTon = Boolean.parseBoolean(obj.get("goichiton").toString());
                                _char.phucLoi.goiHaoHoa = Boolean.parseBoolean(obj.get("goihaohoa").toString());
                                _char.phucLoi.napNgay = Integer.parseInt(obj.get("napngay").toString());
                                _char.phucLoi.napTuan = Integer.parseInt(obj.get("naptuan").toString());
                                _char.phucLoi.napLienTuc = Integer.parseInt(obj.get("naplientuc").toString());
                                _char.phucLoi.theThang = Long.parseLong(obj.get("thethang").toString());
                                _char.phucLoi.theVinhVien = Long.parseLong(obj.get("thevinhvien").toString());
                                if (obj.containsKey("napdon")) {
                                    _char.phucLoi.napDon = Integer.parseInt(obj.get("napdon").toString());
                                } else {
                                    _char.phucLoi.napDon = 0;
                                }
                                if (obj.containsKey("nap3moc")) {
                                    _char.phucLoi.nap3moc = Integer.parseInt(obj.get("nap3moc").toString());
                                } else {
                                    _char.phucLoi.nap3moc = 0;
                                }
                                if(obj.containsKey("listnap")){
                                    JSONArray jsonArrayRead = (JSONArray) obj.get("listnap");
                                    for (Object o : jsonArrayRead) {
                                        _char.phucLoi.listnap.add(Integer.parseInt(o.toString()));
                                    }
                                }
                            } else {
                                TemplatePL templatePL = new TemplatePL();
                                templatePL.Id = Short.parseShort(obj.get("id").toString());
                                templatePL.IdItem = Short.parseShort(obj.get("iditem").toString());
                                templatePL.IDPhucLoi = Short.parseShort(obj.get("idphucloi").toString());
                                templatePL.isLock = Boolean.parseBoolean(obj.get("islock").toString());
                                templatePL.name = obj.get("name").toString();
                                templatePL.strOption = obj.get("stroption").toString();
                                templatePL.Amount = Integer.parseInt(obj.get("amount").toString());
                                _char.phucLoi.listPl.add(templatePL);
                            }
                        }
                    }
                    jArr.clear();
                    jArr = (JSONArray) JSONValue.parse(data.getString("Thu"));
                    len = jArr.size();
                    if (jArr != null) {
                        for (int i = 0; i < len; i++) {
                            JSONObject obj = (JSONObject) jArr.get(i);
                            TemplateThu templateThu = new TemplateThu();
                            templateThu.id = Short.parseShort(obj.get("id").toString());
                            templateThu.isSucess = Boolean.parseBoolean(obj.get("isSucess").toString());
                            templateThu.NameNguoiGui = obj.get("nguoigui").toString();
                            templateThu.Title = obj.get("title").toString();
                            templateThu.NoiDungThu = obj.get("noidung").toString();
                            templateThu.Bac = Integer.parseInt(obj.get("bac").toString());
                            templateThu.BacKhoa = Integer.parseInt(obj.get("backhoa").toString());
                            templateThu.Vang = Integer.parseInt(obj.get("vang").toString());
                            templateThu.VangKhoa = Integer.parseInt(obj.get("vangkhoa").toString());
                            templateThu.Exp = Long.parseLong(obj.get("exp").toString());
                            templateThu.TimeEnd = Long.parseLong(obj.get("timeend").toString());
                            if (obj.containsKey("item")) {
                                Item it = new Item((JSONObject) obj.get("item"));
                                if (it != null) {
                                    templateThu.Item = it;
                                }
                            }
                            _char.letters.add(templateThu);
                        }
                    }
                    jArr.clear();
                    jArr = (JSONArray) JSONValue.parse(data.getString("bagext"));
                    len = jArr.size();
                    _char.Bag.arrItemExtend = new Item[3];
                    if (jArr != null) {
                        for (int i = 0; i < len; i++) {
                            Item it = new Item((JSONObject) jArr.get(i));
                            if (it == null) {
                                continue;
                            }
                            _char.Bag.arrItemExtend[it.index] = it;
                        }
                    }
                    jArr = (JSONArray) JSONValue.parse(data.getString("Bag"));
                    len = jArr.size();
                    if (jArr != null) {
                        int numAdd = 0;
                        for (int i = 0; i < _char.Bag.arrItemExtend.length; i++) {
                            if (_char.Bag.arrItemExtend[i] != null) {
                                numAdd = numAdd + _char.getSlotItemExtend(_char.Bag.arrItemExtend[i]);
                            }
                        }
                        _char.Bag.arrItemBag = new Item[27 + numAdd+_char.bagAdd];
                        if (jArr != null) {
                            for (int i = 0; i < len; i++) {
                                Item it = new Item((JSONObject) jArr.get(i));
                                if (it == null) {
                                    continue;
                                }
                                if (it.amount > 500000) { //check item lớn hơn 500k
                                    it.amount = 500000;
                                }
                                _char.addItem(it);
                            }
                        }
                    }
                    jArr.clear();
                    jArr = (JSONArray) JSONValue.parse(data.getString("box"));
                    len = jArr.size();
                    if (jArr != null) {
                        int numAdd = 0;
                        _char.Bag.arrItemBox = new Item[72 + _char.Info.countBox];
                        if (jArr != null) {
                            for (int i = 0; i < len; i++) {
                                Item it = new Item((JSONObject) jArr.get(i));
                                if (it == null) {
                                    continue;
                                }
                                int indexBoxNull = _char.IndexBoxNull();
                                if (indexBoxNull == -1) {
                                    break;
                                }
                                if (it.amount > 50000) {
                                    it.amount = 50000;
                                }
                                _char.Bag.arrItemBox[indexBoxNull] = it;
                            }
                        }
                    }
                    jArr.clear();
                    jArr = (JSONArray) JSONValue.parse(data.getString("body"));
                    len = jArr.size();
                    if (jArr != null) {
                        if (jArr != null) {
                            for (int i = 0; i < len; i++) {
                                Item it = new Item((JSONObject) jArr.get(i));
                                if (it == null) {
                                    continue;
                                }
                                _char.Bag.arrItemBody[it.getItemTemplate().type] = it;
                            }
                        }
                    }
                    jArr.clear();
                    jArr = (JSONArray) JSONValue.parse(data.getString("body2"));
                    len = jArr.size();
                    if (jArr != null) {
                        if (jArr != null) {
                            for (int i = 0; i < len; i++) {
                                Item it = new Item((JSONObject) jArr.get(i));
                                if (it == null) {
                                    continue;
                                }
                                _char.Bag.arrItemBody2[it.getItemTemplate().type] = it;
                            }
                        }
                    }
                    jArr.clear();
                    jArr = (JSONArray) JSONValue.parse(data.getString("skillvithu"));
                    len = jArr.size();
                    if (jArr != null) {
                        if (jArr != null) {
                            for (int i = 0; i < len; i++) {
                                Item it = new Item((JSONObject) jArr.get(i));
                                if (it == null) {
                                    continue;
                                }
                                _char.Bag.arrItemSkillViThu[it.getItemTemplate().type] = it;
                            }
                        }
                    }
                    jArr.clear();
                    jArr = (JSONArray) JSONValue.parse(data.getString("Code"));
                    len = jArr.size();
                    _char.GiftCode = new InfoGiftCode();
                    if (jArr != null) {
                        for (int i = 0; i < len; i++) {
                            _char.GiftCode.GiftDaNhan.add(jArr.get(i).toString());
                        }
                    }
                    jArr.clear();
                    jArr = (JSONArray) JSONValue.parse(data.getString("Effect"));
                    len = jArr.size();
                    if (jArr != null) {
                        for (int i = 0; i < len; i++) {
                            JSONObject obj = (JSONObject) jArr.get(i);
                            Effect eff = new Effect();
                            eff.id = Short.parseShort(obj.get("id").toString());
                            eff.value = Integer.parseInt(obj.get("value").toString());
                            eff.timeStart = Long.parseLong(obj.get("timeStart").toString());
                            eff.maintain = Integer.parseInt(obj.get("maintain").toString());
                            _char.listEffect.add(eff);
                        }
                    }
                    jArr.clear();
                    jArr = (JSONArray) JSONValue.parse(data.getString("danhhieu"));
                    len = jArr.size();
                    if (jArr != null) {
                        for (int i = 0; i < len; i++) {
                            JSONObject obj = (JSONObject) jArr.get(i);
                            DanhHieu danhHieu = new DanhHieu();
                            danhHieu.TextDanhHieu = obj.get("text").toString();
                            danhHieu.timeStart = Long.parseLong(obj.get("timeStart").toString());
                            danhHieu.timeEnd = Long.parseLong(obj.get("timeEnd").toString());
                            if (obj.containsKey("isNew")) {
                                danhHieu.isNew = Boolean.parseBoolean(obj.get("isNew").toString());
                                danhHieu.idItem = Integer.parseInt(obj.get("idItem").toString());
                            }
                            _char.Info.danhHieus.add(danhHieu);
                        }
                    }
                    jArr.clear();
                    jArr = (JSONArray) JSONValue.parse(data.getString("hokage"));
                    len = jArr.size();
                    if (jArr != null) {
                        for (int i = 0; i < len; i++) {
                            JSONObject obj = (JSONObject) jArr.get(i);
                            PointHokage hokage = new PointHokage();
                            hokage.Dai = Short.parseShort(obj.get("dai").toString());
                            hokage.Ao = Short.parseShort(obj.get("ao").toString());
                            hokage.BaoTay = Short.parseShort(obj.get("baotay").toString());
                            hokage.Quan = Short.parseShort(obj.get("quan").toString());
                            hokage.Giay = Short.parseShort(obj.get("giay").toString());
                            hokage.Vk = Short.parseShort(obj.get("vk").toString());
                            hokage.Day = Short.parseShort(obj.get("day").toString());
                            hokage.Moc = Short.parseShort(obj.get("moc").toString());
                            hokage.OngTieu = Short.parseShort(obj.get("ongtieu").toString());
                            hokage.Tui = Short.parseShort(obj.get("tui").toString());
                            _char.Info.pointHokage = hokage;
                        }
                    }
                    jArr.clear();
                    jArr = (JSONArray) JSONValue.parse(data.getString("listskill"));
                    len = jArr.size();
                    if (jArr != null) {
                        for (int i = 0; i < len; i++) {
                            JSONObject obj = (JSONObject) jArr.get(i);
                            SkillClan skillClan = new SkillClan();
                            skillClan.id = Integer.parseInt(obj.get("id").toString());
                            skillClan.name = obj.get("name").toString();
                            skillClan.levelNeed = Integer.parseInt(obj.get("levelNeed").toString());
                            skillClan.strOptions = obj.get("strOptions").toString();
                            _char.listSkill.add(skillClan);
                        }
                    }
                    jArr.clear();
                    JSONArray friends = (JSONArray) JSONValue.parse(data.getString("friends"));
                    int size = friends.size();
                    _char.friends = new HashMap<>();
                    for (int i = 0; i < size; i++) {
                        Friend fr = new Friend((JSONObject) friends.get(i));
                        _char.friends.put(fr.name, fr);
                    }
                    JSONArray enemies = (JSONArray) JSONValue.parse(data.getString("enemies"));
                    size = enemies.size();
                    _char.enemies = new HashMap<>();
                    for (int i = 0; i < size; i++) {
                        Friend fr = new Friend((JSONObject) enemies.get(i));
                        _char.enemies.put(fr.name, fr);
                    }
                    int clan = data.getInt("clan");
                    if (clan != -1) {
                        Optional<Clan> g = Clan.getClanDAO().get(clan);
                        if (g != null && g.isPresent()) {
                            _char.clan = g.get();
                            Member mem = _char.clan.getMemberByName(_char.Info.name);
                            if (mem == null) {
                                _char.clan = null;
                            }
                        }
                    }
                    _char.taskOrders = new ArrayList<>();
                    jArr = (JSONArray) JSONValue.parse(data.getString("task"));
                    if (jArr != null) {
                        len = jArr.size();
                        for (int i = 0; i < len; i++) {
                            JSONObject obj = (JSONObject) jArr.get(i);
                            byte taskId = Byte.parseByte(obj.get("taskId").toString());
                            int count = Integer.parseInt(obj.get("count").toString());
                            int maxCount = Integer.parseInt(obj.get("maxCount").toString());
                            int killId = Integer.parseInt(obj.get("killId").toString());
                            int mapId = Integer.parseInt(obj.get("mapId").toString());
                            _char.taskOrders.add(new TaskOrder(_char, taskId, count, maxCount, killId, mapId));
                        }
                    }
                    String tt = data.getString("taskMain");
                    if (tt != null && !tt.equals("")) {
                        ParseData pd = new ParseData((JSONObject) JSONValue.parse(tt));
                        short taskID = pd.getShort("id");
                        byte taskIndex = pd.getByte("index");
                        short taskCount = pd.getShort("count");
                        TaskTemplate task = Task.getTaskTemplate(taskID);
                        if (task != null) {
                            _char.taskMain = TaskFactory.getInstance().createTask(taskID, taskIndex, taskCount);
                        }
                    }
                    _char.Skill = s.readValue(data.getString("skill"), InfoSkill.class);
                    _char.loadEventPoint();
                    this.chars.add(_char);
                }
            } finally {
                data.close();
                stmt.close();
            }
        } catch (Exception ex) {
            Log.error("Loi login cua player " + this.username, ex);
        }
    }

    public void login() {
        try {
            Pattern p = Pattern.compile("^[a-zA-Z0-9]+$");
            Matcher m1 = p.matcher(username);
            if (!m1.find()) {
                service.alertMessage("Tên tài khoản có kí tự lạ.");
                return;
            }
            HashMap<String, Object> map = getUserMap();
            if (map == null) {
                service.alertMessage("Thông tin tài khoản hoặc mật khẩu không chính xác.");
                return;
            }
//            if (ServerManager.timeWaitLogin.containsKey(username)) {
//                if (System.currentTimeMillis() < (Long) ServerManager.timeWaitLogin.get(username)) {
//                    service.alertMessage("Bạn chỉ có thể đăng nhập lại vào tài khoản sau " + ((Long) ServerManager.timeWaitLogin.get(username) - System.currentTimeMillis()) / 1000L + "s nữa");
//                    return;
//                }
//                ServerManager.timeWaitLogin.remove(username);
//            }
            this.ID_USER = (int) map.get("id");
            this.numberChar = (byte) ((int) map.get("QuantityChar"));
            int isAdmin = (int) map.get("isAdmin");
            this.isAdmin = isAdmin == 1 ? true : false;
            String dbArr = (String) map.get("ArrSubName");
            this.coin = (int) map.get("coin");
            this.banner = (int) map.get("lock");
            this.topSm = (int) map.get("topsm");
            int active = (int) map.get("activated");
            if (active == 0) {
                actived = true;
            } else {
                actived = false;
            }
            if (banner == 1) {
                service.alertMessage("Tài khoản của bạn đã bị khóa");
                return;
            }
//            if(active == 1){
//                service.alertMessage("Máy chủ Open chính thức vào 14h00 ngày 15/06");
//                return;
//            }
//            if(active == 0){
//                service.alertMessage("Máy chủ Open chính thức vào 14h00 ngày 15/06");
//                return;
//            }
            if (dbArr.equals("[]")) {
                this.subNameChar = null;
            } else {
                this.subNameChar = new ObjectMapper().readValue(dbArr, String[].class);
            }
            User u = ServerManager.findUserByUsername(this.username);
            if (u != null && !u.isCleaned) {
                service.serverMessage("Tài khoản đã có người đăng nhập.");
                if (u.session != null && u.session.getService() != null) {
                    u.session.getService().alertMessage("Có người đăng nhập vào tài khoản của bạn.");
                }
                try {
                    if (!u.isCleaned) {
                        u.session.clean();
                    }
                } catch (Exception e) {
                } finally {
                    ServerManager.removeUser(u);
                }
                return;
            }
            this.isLoadFinish = true;
        } catch (Exception ex) {
            Log.error("login err", ex);
            session.disconnect();
        }
    }
    public void createCharDB(Char c, int numberC, int _userid) {
        try (Connection conn = Connect.getConnection();) {
            PreparedStatement ps = conn.prepareStatement("Insert Into player set idchar = ? ,indexchar = ?,name = ?, info = ? ,inventory = ?,bag = ?,body = ?,body2 = ?,box = ?,bagext = ?,skillvithu = ?, skill = ? , point = ? ,thu = ?,code = ?,effect =?,phucLoi =?,hokage =?,danhhieu = ?,listskill = ?,`enemies` = ?,`friends` = ?,`task` = ?");
            ps.setInt(1, _userid);
            ps.setInt(2, numberC + 1);
            ps.setString(3, c.Info.name);
            c.Info.idEntity = _userid;
            c.Info.cx = 259;
            c.Info.cy=257;
            ObjectMapper json = new ObjectMapper();
            JSONArray info = new JSONArray();
            info.add(c.Info.toJSONObject());
            JSONArray inventory = new JSONArray();
            inventory.add(c.Bag.toJSONObject());
            JSONArray point = new JSONArray();
            point.add(c.Point.toJSONObject());
            JSONArray bags = new JSONArray();
            for (int i = 0; i < c.Bag.arrItemBag.length; i++) {
                try {
                    if (c.Bag.arrItemBag[i] != null) {
                        bags.add(c.Bag.arrItemBag[i].toJSONObject());

                    }
                } catch (Exception e) {
                }
            }
            String jinfo = info.toJSONString();
            String jbag = bags.toJSONString();
            String jinven = inventory.toJSONString();
            String jpoin = point.toJSONString();
            String skill = json.writeValueAsString(c.Skill);
            ps.setString(4, jinfo);
            ps.setString(5, jinven);
            ps.setString(6, jbag);
            ps.setString(7, "[]");
            ps.setString(8, "[]");
            ps.setString(9, "[]");
            ps.setString(10, "[]");
            ps.setString(11, "[]");
            ps.setString(12, skill);
            ps.setString(13, jpoin);
            ps.setString(14, "[]");
            ps.setString(15, "[]");
            ps.setString(16, "[]");
            ps.setString(17, "[]");
            ps.setString(18, "[]");
            ps.setString(19, "[]");
            ps.setString(20, "[]");
            ps.setString(21, "[]");
            ps.setString(22, "[]");
            ps.setString(23, "[]");

            ps.executeUpdate();
            ps.close();

            /**
             *
             * *********** INSERT TO ARR SUB NAME USERS************
             */
            if (c.user.subNameChar == null) {
                c.user.subNameChar = new String[3];
            }
            c.user.subNameChar[numberC] = c.Info.name;
            ps = conn.prepareStatement("UPDATE USERS SET quantitychar = ? , arrsubname = ? where username = ?");
            ps.setInt(1, numberC + 1);
            ps.setString(2, json.writeValueAsString(c.user.subNameChar));
            ps.setString(3, c.user.username);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
