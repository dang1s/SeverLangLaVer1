package com.sg188.PhucLoi;

import java.util.ArrayList;
import java.util.List;

public class Welfare {
    private int id;
    private String welfareType;
    private int welfareId;
    private String welfareName;
    private boolean isPackage;
    private String description;
    public List<TemplatePL>item=new ArrayList<>();

    // Constructors, getters, and setters
    public Welfare(int id, String welfareType, int welfareId, String welfareName, boolean isPackage, String description) {
        this.id = id;
        this.welfareType = welfareType;
        this.welfareId = welfareId;
        this.welfareName = welfareName;
        this.isPackage = isPackage;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWelfareType() {
        return welfareType;
    }

    public void setWelfareType(String welfareType) {
        this.welfareType = welfareType;
    }

    public int getWelfareId() {
        return welfareId;
    }

    public void setWelfareId(int welfareId) {
        this.welfareId = welfareId;
    }

    public String getWelfareName() {
        return welfareName;
    }

    public void setWelfareName(String welfareName) {
        this.welfareName = welfareName;
    }

    public boolean isPackage() {
        return isPackage;
    }

    public void setPackage(boolean isPackage) {
        this.isPackage = isPackage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

