package com.sg188.Shop;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sg188.data.ItemOption;
import com.sg188.data.ItemTemplate;
import com.sg188.lib.Utlis;
import com.sg188.real.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemShop {
    public int id;
    public int itemID;
    public byte he;
    public int Bac;
    public int TypeShop;
    public int TinhThach;
    public int BacKhoa;
    public int Vang;
    public int VangKhoa;
    public boolean isLock;
    public long expire;
    public String strOption;
    public int yeuCau;
    public int amount;
    public int giaCu;
    public int conLai;
    public ItemTemplate template;
    public List<Item>items=new ArrayList<>();
    public ItemShop(int id, int itemID, byte he ,byte typeShop,int TinhThach, int Bac, int BacKhoa, int Vang, int VangKhoa, boolean isLock, long expire,String strOption,int yeucau,int amount,int giaCu, int conLai) {
        this.id = id;
        this.itemID = itemID;
        this.he = he;
        this.TypeShop =typeShop;
        this.TinhThach = TinhThach;
        this.Bac = Bac;
        this.BacKhoa = BacKhoa;
        this.Vang = Vang;
        this.VangKhoa = VangKhoa;
        this.isLock = isLock;
        this.expire = expire;
        this.strOption = strOption;
        Item item = new Item(itemID);
        this.template = item.getItemTemplate();
        this.yeuCau = yeucau;
        this.amount = amount;
        this.giaCu = giaCu;
        this.conLai = conLai;
    }
    public ItemShop(int id, int itemID, byte he ,byte typeShop,int TinhThach, int Bac, int BacKhoa, int Vang, int VangKhoa, boolean isLock, long expire,String strOption,int yeucau,int amount) {
        this.id = id;
        this.itemID = itemID;
        this.he = he;
        this.TypeShop =typeShop;
        this.TinhThach = TinhThach;
        this.Bac = Bac;
        this.BacKhoa = BacKhoa;
        this.Vang = Vang;
        this.VangKhoa = VangKhoa;
        this.isLock = isLock;
        this.expire = expire;
        this.strOption = strOption;
        Item item = new Item(itemID);
        this.template = item.getItemTemplate();
        this.yeuCau = yeucau;
        this.amount = amount;
    }
}
