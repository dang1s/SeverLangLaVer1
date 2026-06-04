/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfoChar;

import com.sg188.lib.Utlis;
import com.sg188.real.Char;
import org.json.simple.JSONObject;

/**
 *
 * @author ADMIN
 */
public class InfoPoint {

    public int hp = 100;
    public int mp = 100;
    public int hpFull = 0;
    public int mpFull = 0;
    public long exp;
    public int[] arrayTiemNang = new int[4];
    public int diemTiemNang;
    public int diemKyNang;
    public int hoatLuc;
    public byte diempt;
    public byte maxpt;
    public int expsach;
    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        obj.put("sm", this.arrayTiemNang[0]);
        obj.put("chakra", this.arrayTiemNang[1]);
        obj.put("hp", this.arrayTiemNang[2]);
        obj.put("mp", this.arrayTiemNang[3]);
        obj.put("pointTn", this.diemTiemNang);
        obj.put("pointKn", this.diemKyNang);
        obj.put("hoatluc", this.hoatLuc);
        obj.put("exp", this.exp);
        obj.put("diempt", this.diempt);
        obj.put("maxpt", this.maxpt);
        obj.put("expsach", this.expsach);
        return obj;
    }

    /**
     * class 1 Lôi :Lôi khắc thổ, Phong khắc lôi class 2 Thổ : Thổ khắc thủy,
     * Lôi khắc thổ class 3 Thủy: Thủy khắc hỏa,Thổ khắc thủy class 4 Hỏa: Hỏa
     * khắc phong, Thủy khắc hỏa class 5 Phong : Phong khắc lôi, Hỏa khắc phong
     */



    public static int getKhangByClass(Char _char, int idClass) {
        switch (idClass) {
            case 1: // Lôi
                return _char.lightningResistance;
            case 2: // Thổ
                return _char.earthResistance;
            case 3: // Thủy
                return _char.waterResistance;
            case 4: // Hỏa
                return _char.fireResistance;
            case 5: // Phong
                return _char.windResistance;
            default:
                return 0;
        }
    }

    public static int calculateKhang(double khang) {
         if (khang > 0 && khang < 200) {
             return 50; // Giảm 20%
         } else if (khang >= 200 && khang < 500) {
             return 70; // Giảm 10%
        } else if (khang >= 500 && khang < 1000) {
            return 85; // Giảm 20%
        } else if (khang >= 1000 && khang < 1500) {
            return 90; // Giảm 25%
        } else if (khang >= 1500 && khang < 2000) {
            return 95; // Giảm 35%
        } else if (khang >= 2000 && khang < 2500) {
            return 96; // Giảm 40%
        } else if (khang >= 2500 && khang < 3000) {
            return 97; // Giảm 45%
        } else if (khang >= 3000 && khang < 3500) {
            return 98; // Giảm 60%
        } else if (khang >= 3500 && khang < 4000) {
            return 99; // Giảm 70%
        } else if (khang >= 4000 && khang < 5000) {
            return 99; // Giảm 85%
        }
        return 80; // Không giảm nếu không nằm trong khoảng nào
    }
}
