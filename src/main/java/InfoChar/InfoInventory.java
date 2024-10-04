/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfoChar;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sg188.real.Item;
import com.sg188.task.Task;
import org.json.simple.JSONObject;

/**
 * @author ADMIN
 */
public class InfoInventory {

    public Item[] arrItemBag;
    public Item[] arrItemBox;
    public Item[] arrItemBody;
    public Item[] arrItemBody2;
    public Item[] arrItemExtend;
    public Item[] arrItemSkillViThu;
    public Item itemSach;
    public int bac;
    public int bacKhoa;
    public int vangKhoa;
    public int vang;
    public int bacBox;
    public int bacKhoaBox;
    public int vangKhoaBox;
    public int vangBox;
    public int taiPhu;
    public int statusGD = 0;
    public int pointNAP;
    public int pointNapNew;
    public byte stnSo;
    public byte stnTrung;
    public byte stnCao;
    public byte sknSo;
    public byte sknTrung;
    public byte sknCao;
    public byte Banh;
    public byte banhUBao;


    public InfoInventory() {
        arrItemBag = new Item[27];
        Item vk = new Item(28);
        vk.isLock = true;
        Item.setOptionsVuKhiToBag(vk, 1);
        arrItemBag[0] = vk;
        arrItemBody = new Item[17];
        arrItemBody2 = new Item[17];
        arrItemExtend = new Item[3];
        arrItemSkillViThu = new Item[6];
        arrItemBox = new Item[27];
        vang = 0;//open sửa lại
        vangKhoa = 1000;
        bac = 1000;
        bacKhoa = 1000;
        vangBox = 0;
        vangKhoaBox = 0;
        bacBox = 0;
        bacKhoaBox = 0;
    }

    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        obj.put("backhoa", this.bacKhoa);
        obj.put("bacbox", this.bacBox);
        obj.put("backhoabox", this.bacKhoaBox);
        obj.put("vangkhoa", this.vangKhoa);
        obj.put("vangkhoabox", this.vangKhoaBox);
        obj.put("vangbox", this.vangBox);
        obj.put("pointnap", this.pointNAP);
        obj.put("pointnapnew", this.pointNapNew);
        obj.put("stnso", this.stnSo);
        obj.put("stntrung", this.stnTrung);
        obj.put("stncao", this.stnCao);
        obj.put("sknso", this.sknSo);
        obj.put("skntrung", this.sknTrung);
        obj.put("skncao", this.sknCao);
        obj.put("banh", this.Banh);
        obj.put("taiphu", this.taiPhu);
        obj.put("banhUbao", this.banhUBao);
        if (itemSach != null)
            obj.put("sach", itemSach.toJSONObject());
        return obj;
    }

}
