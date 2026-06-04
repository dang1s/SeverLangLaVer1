/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Template;

import com.sg188.real.Item;
import org.json.simple.JSONObject;

/**
 *
 * @author ADMIN
 */
public class TemplateThu {
    public short id;
    public boolean isSucess;
    public String NameNguoiGui;
    public String Title;
    public String NoiDungThu;
    public int Bac;
    public int BacKhoa;
    public int Vang;
    public int VangKhoa;
    public long Exp;
    public long TimeEnd;
    public Item Item; // chỉ 1 :D
    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        obj.put("id", this.id);
        obj.put("isSucess", this.isSucess);
        obj.put("nguoigui", this.NameNguoiGui);
        obj.put("title", this.Title);
        obj.put("noidung", this.NoiDungThu);
        obj.put("bac", this.Bac);
        obj.put("backhoa", this.BacKhoa);
        obj.put("vang", this.Vang);
        obj.put("vangkhoa", this.VangKhoa);
        obj.put("exp", this.Exp);
        obj.put("timeend", this.TimeEnd);
        if(Item!=null)
        obj.put("item",Item.toJSONObject());
        return obj;
    }
}
