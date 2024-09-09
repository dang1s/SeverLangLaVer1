/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfoChar;

import com.sg188.real.DanhHieu;
import com.sg188.real.Entity;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class InfoChar extends Entity{

    public String username = "";
    public byte idChar;
    public byte lvPk;
    public byte typePK;
    public short speedMove; 
    public int sachChienDau;    
    public byte gioiTinh = 1;
    public byte idClass = 1;
    public byte idhe = 1;
    public byte selectDanhHieu = -1;
    public byte rank;
 
    public String name = "";
    public int selectCaiTrang;
    
    public int IdGiaToc =-1;
    public byte RoleGiaToc;
    public short BuffEXP = 0;
    public short chuyenCan;
    public short chuyenCanTuan;
    public long cuaCai;
    public int cuaCaiTuan;
    public int cuongHoa;
    public int cuongHoaTuan;
    public int loiDai;
    public long luyenTap;
    public byte countRuong;
    public int expCheTao;
    public int levelCheTao;
    public PointHokage pointHokage = new PointHokage();
    public List<DanhHieu>danhHieus = new ArrayList<>();
    public boolean banCTG;
    public byte countBox;
    public boolean khoaExp= false;




    public byte typeVQMM;
    public long timeLogin;
    /************* REGION MAP **************/
    public short _mapID;
    public byte countKham;
    public short mapReSpawm = 75;
    public long TimeStartHD;
    public byte numct = 17;
    public byte levelMaxViThu=4;
    public byte countCamThuat = 1;
    public byte countUseBinhHoatLuc = 0;
    public byte countTBGT;
    public byte countHu;


    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        obj.put("idchar", this.idChar);
        obj.put("lvpk", this.lvPk);
        obj.put("sachchiendau", this.sachChienDau);
        obj.put("gioitinh", this.gioiTinh);
        obj.put("idclass", this.idClass);
        obj.put("idhe", this.idhe);
        obj.put("selectdanhhieu", this.selectDanhHieu);
        obj.put("rank", this.rank);
        obj.put("selectcaitrang", this.selectCaiTrang);
        obj.put("idclan", this.IdGiaToc);
        obj.put("roleclan", this.RoleGiaToc);
        obj.put("buffexp", this.BuffEXP);
        obj.put("chuyencan", this.chuyenCan);
        obj.put("chuyencantuan", this.chuyenCanTuan);
        obj.put("cuacai", this.cuaCai);
        obj.put("cuacaituan", this.cuaCaiTuan);
        obj.put("cuonghoa", this.cuongHoa);
        obj.put("cuonghoatuan", this.cuongHoaTuan);
        obj.put("loidai", this.loiDai);
        obj.put("luyentap", this.luyenTap);
        obj.put("countruong", this.countRuong);
        obj.put("expchetao", this.expCheTao);
        obj.put("levelchetao", this.levelCheTao);
        obj.put("banctg", this.banCTG);
        obj.put("typevqmm", this.typeVQMM);
        obj.put("timelogin", this.timeLogin);
        obj.put("map", this._mapID);
        obj.put("cx", this.cx);
        obj.put("cy", this.cy);
        obj.put("maphs", this.mapReSpawm);
        obj.put("countkham", this.countKham);
        obj.put("timestart", this.TimeStartHD);
        obj.put("countbox", this.countBox);
        obj.put("khoaexp", this.khoaExp);
        obj.put("numct", this.numct);
        obj.put("lvmax", this.levelMaxViThu);
        obj.put("camthuat", this.countCamThuat);
        obj.put("counttbgt", this.countTBGT);
        obj.put("countHu", this.countHu);
        obj.put("countUseBinhHoatLuc", this.countUseBinhHoatLuc);
        return obj;
    }

    public InfoChar()
    {
        _mapID = 75;
    }
}
