package EventClick;

import com.sg188.data.DataCenter;

public class InfoTop {
    public String name;
    public long exp;
    public long taiPhu;

    public int chuyencan;
    public byte idHe;
    public short level;
    public int pointNap;
    public int pointDaiHoi;
    public int pointNapTuan;
    public String clanName="";

    public long getExp() {
        return exp;
    }

    public long getTaiPhu() {
        return taiPhu;
    }

    public long getChuyenCan() {
        return chuyencan;
    }
    public long getPointNap() {
        return pointNap;
    }

    public long getPointNapTuan() {
        return pointNapTuan;
    }

    public int getPointDaiHoi() {
        return pointDaiHoi;
    }
    public short getLevvel() {
        long var1 = this.exp;

        short var3;
        for (var3 = 0; var3 < DataCenter.gI().exps.length && var1 >= DataCenter.gI().exps[var3]; ++var3) {
            var1 -= DataCenter.gI().exps[var3];
        }

        return var3;
    }
}
