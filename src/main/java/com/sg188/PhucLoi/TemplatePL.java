package com.sg188.PhucLoi;

import org.json.simple.JSONObject;

public class TemplatePL {
    public int Id;
    public int IdItem;
    public int IDPhucLoi;
    public boolean isLock;
    public String name;
    public String strOption;
    public int Amount;
    public int yeucau;
    public TemplatePL(){

    }
    public TemplatePL(int id, int idItem, int IDPhucLoi, boolean isLock, String name, String strOption,int Amount,int yeucau) {
        Id = id;
        IdItem = idItem;
        this.IDPhucLoi = IDPhucLoi;
        this.isLock = isLock;
        this.name = name;
        this.strOption = strOption;
        this.Amount = Amount;
        this.yeucau = yeucau;
    }
    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        obj.put("id", this.Id);
        obj.put("iditem", this.IdItem);
        obj.put("idphucloi", this.IDPhucLoi);
        obj.put("islock", this.isLock);
        obj.put("name", this.name);
        obj.put("stroption",this.strOption);
        obj.put("amount",this.Amount);
        return obj;
    }

}
