package EventClick;

import com.sg188.data.DataCenter;

public class InfoTop {
    public String name;
    public long exp;
    public long taiPhu;
     public int luyenTap;
    public long chuyencan;
    public byte idHe;
    public short level;
    public int pointNap;
    public int pointDaiHoi;
    public int pointNapTuan;
    public int cuongHoa;
    public int cuaCai;
    public int cuaCaiTuan;
    public int chuyenCanTuan;
    public int cuongHoaTuan;
    public int congHienTuan;
    public int loiDai;
    public String clanName="";
    public int pointChienTich;

    public long getExp() {
        return exp;
    }

    public long getTaiPhu() {
        return taiPhu;
    }
    public int getLuyenTap() {
        return luyenTap;
    }
    public long getChuyenCan() {
        return chuyencan;
    }
    public long getPointNap() {
        return pointNap;
    }
    public int getcuongHoa() {
        return cuongHoa;
    }

    public int cuaCai() {
        return cuaCai;
    }

    public int cuaCaiTuan() {
        return cuaCaiTuan;
    }

    public int cuongHoaTuan() {
        return cuongHoaTuan;
    }
    public long chuyenCanTuan() {
        return chuyenCanTuan;
    }
    public int congHienTuan() {
        return congHienTuan;
    }
    public int getLoiDai() {
        return loiDai;
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

    public long getChienTich(){
        return pointChienTich;
    }
}
