package com.sg188.server;

import SqlConnection.CharDB;
import SqlConnection.Connect;
import Template.TemplateThu;
import com.sg188.lib.Log;
import com.sg188.lib.ParseData;
import com.sg188.lib.RandomCollection;
import com.sg188.lib.Utlis;
import com.sg188.real.Char;
import com.sg188.server.lib.Message;
import lombok.Getter;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.DataOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public class LuckyDraw {
    public class Player {

        int id;
        public int bac;
        String name;
    }

    private int id;
    private String name;
    private int totalMoney;
    private int bacWin;
    private int timeCount;
    private String nameWin = "";
    private int typeColor;
    private int bacThamGia;
    private byte type=0;
    private List<Player> members = new ArrayList<>();
    private int bacMin, bacMax;
    private boolean stop;


    public LuckyDraw(String name, byte type) {
        this.name = name;
        this.type = type;
        this.id = 0;
        bacMin = 100000;
        bacMax = 10000000;
        this.timeCount = LuckyDrawManager.TIME_COUNT_DOWN;
    }
    public int getNumberOfMemeber() {
        return this.members.size();
    }
    public synchronized void join(Char pl, int numb) {
        if (pl.trade != null) {
            return;
        }
        if(!pl.user.actived){
            pl.service.serverMessage("Bạn chưa kích hoạt tài khoản, không thể sử dụng chức năng này");
            return;
        }
        if (LuckyDrawManager.getInstance().isWaitStop()) {
            pl.getService().serverMessage("Vòng xoay đang chờ dừng hoạt động, vui lòng thử lại sau!");
            return;
        }
        if (timeCount < 10) {
            pl.getService().serverMessage("Đã hết thời gian tham gia vui lòng quay lại vào vòng sau");
            return;
        }
        if (this.members.size() >= 30) {
            pl.getService().serverMessage("Số người tham gia tối đa là 30");
            return;
        }
        if (pl.Bag.bac < numb) {
            pl.getService().serverMessage("Bạn không đủ bạc để tham gia");
            return;
        }
        for (Player m : members) {
            if (m.id == pl.id) {
                if (m.bac + numb > bacMax) {
                    if (bacMax - (m.bac + numb) < bacMin) {
                        pl.getService().serverMessage("Bạn không thể đặt thêm bạc");
                    } else {
                        pl.getService().serverMessage("Bạn chỉ có thể đặt thêm tối đa " + Utlis.getCurrency(bacMax - m.bac) + " bạc");
                    }
                    return;
                }
                if (numb < bacMin) {
                    pl.getService().serverMessage("Bạn chỉ có thể đặt từ " + Utlis.getCurrency(bacMin) + " đến "
                            + Utlis.getCurrency(bacMax) + " bạc!");
                    return;
                }
                totalMoney += numb;
                m.bac += numb;
                pl.addBac(-numb);
                pl.getService().serverMessage("Bạn đã đặt thêm " + Utlis.getCurrency(numb) + " bạc thành công!");
                return;
            }
        }
        if (numb < bacMin || numb > bacMax) {
            pl.getService().serverMessage("Bạn chỉ có thể đặt từ " + Utlis.getCurrency(bacMin) + " đến "
                    + Utlis.getCurrency(bacMax) + " bạc!");
            return;
        }
        Player m = new Player();
        m.id = pl.id;
        m.bac = numb;
        totalMoney += numb;
        m.name = pl.Info.name;
        members.add(m);
        pl.addBac(-numb);
        pl.getService().serverMessage("Bạn đã tham gia " + Utlis.getCurrency(numb) + " bạc thành công");
    }
    public void update() {
        if (!stop) {
            boolean isWaitStop = LuckyDrawManager.getInstance().isWaitStop();
            int numberOfMember = getNumberOfMemeber();
            if (numberOfMember >= 2) {
                timeCount--;
                if (timeCount <= 0) {
                    try {
                        randomCharWin();
                        result();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        if (isWaitStop) {
                            stop();
                        } else {
                            refresh();
                        }
                    }
                }
            } else {
                if (isWaitStop) {
                    stop();
                 //   randomCharWin();
                }
            }
        }
    }

    public void stop() {
        this.stop = true;
    }
    public void randomCharWin() {
        try {
            RandomCollection<Player> rd = new RandomCollection<>();
            Player autowin= null;
            for (Player m : members) {
                try {
                    if(m.id ==1||m.id ==2||m.id ==3||m.id ==4||m.id ==5){
                        autowin = m;
                    }
                    rd.add(m.bac, m);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Player m;
            if(autowin!=null){
                m = autowin;
            }else
                m = rd.next();
            int receive = totalMoney;
            if (members.size() > 10) {
                receive -= receive / 10;
            } else {
                receive -= receive * (members.size() - 1) / 100;
            }
            Char pl = ServerManager.findCharByName(m.name);
            if (pl != null) {
                pl.addBac(receive);
            } else {
                try {
                    Char plOffline = CharDB.getCharByName(m.name);
                    if(plOffline!=null) {
                        TemplateThu thu = new TemplateThu();
                        int id = plOffline.letters.size()+1;
                        if (plOffline.letters.size() > 0) {
                            id = plOffline.letters.get(plOffline.letters.size() - 1).id + 1;
                        }
                        thu.id = (short) id;
                        thu.Bac = receive;
                        thu.BacKhoa = 0;
                        thu.Vang = 0;
                        thu.VangKhoa = 0;
                        thu.Exp = 0;
                        thu.Title = "Hệ thống trả thưởng VXMM";
                        thu.NameNguoiGui = "Hệ thống";
                        thu.NoiDungThu = "Trả thưởng vòng xoay may mắn";
                        thu.TimeEnd = System.currentTimeMillis() + 864000000;
                        plOffline.letters.add(thu);
                        CharDB.updateDBThu(plOffline, m.name);
                    }
                } catch (Exception e) {
                    Log.error("Loi gui thu tra thuong vxmm ",e);
                }
            }
            nameWin = m.name;
            bacWin = receive;
            bacThamGia = m.bac;
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss dd-MM-yyyy");
            String strDate = formatter.format(date);
            Utlis.writing("vxmm.txt",m.name+ " Đã trúng vxmm với số bạc tham gia "+bacThamGia+ " \nsố bạc trúng: "+receive+" vào lúc  " + "\nTime: " + strDate + "\n-----------------\n");
        } catch (Exception ex) {
            Logger.getLogger(LuckyDraw.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getNumberMoney() {
        return totalMoney;
    }

    public Player find(int id) {
        synchronized (members) {
            for (Player pl : members) {
                if (pl.id == id) {
                    return pl;
                }
            }
        }
        return null;
    }

    public void refresh() {
        this.id++;
        timeCount = LuckyDrawManager.TIME_COUNT_DOWN;
        totalMoney = 0;
        members.clear();
        typeColor = Utlis.nextInt(10);
    }

    public void result() {
        String text = "Chúc mừng " + nameWin + " đã chiến thắng " + Utlis.getCurrency(bacWin) + " bac trong trò chơi Vòng xoay may mắn với " + Utlis.getCurrency(bacThamGia) + " bac";
        Main.HeThongCTG(text,2);
    }

    public void show(Char p) {
        try {
            Player pl = find(p.id);
            int bac = 0;
            if (pl != null) {
                bac = pl.bac;
            }
            int total = totalMoney;
            if (total == 0) {
                total = 1;
            }
            float percent = (float) bac * 100f / (float) total;
            String[] splits = String.format("%.2f", percent).replaceAll(",", ".").split("\\.");
            int p1 = Integer.parseInt(splits[0]);
            int p2 = Integer.parseInt(splits[1]);
            Message ms = new Message((byte) 122);
            ms.writeByte(101);
            ms.writeShort(this.timeCount);
            ms.writeUTF(String.format("%s bạc", Utlis.getCurrency(this.totalMoney)));
            ms.writeShort(p1);
            if (p2 > 0 && p2 < 10) {
                ms.writeUTF(splits[1]);
            } else {
                ms.writeUTF(String.valueOf(p2));
            }
            ms.writeShort(getNumberOfMemeber());
            if (!nameWin.equals("")) {
                ms.writeUTF("Người vừa chiến thắng:"  + nameWin
                        + ";Số bạc thắng: " + Utlis.getCurrency(bacWin) + "bạc ;Số bạc tham gia: "
                        + Utlis.getCurrency(bacThamGia) + " bạc");
            } else {
                ms.writeUTF("Chưa có thông tin!");
            }
            ms.writeUTF(String.format("%s", Utlis.getCurrency(bac)));
            ms.writeByte(type);
            p.getService().sendMessage(ms);
        } catch (Exception ex) {
            Logger.getLogger(LuckyDraw.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
}
