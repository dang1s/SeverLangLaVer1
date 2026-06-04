package com.sg188.real;

import Template.TemplateThu;
import market.MarketManager;

import java.util.ArrayList;
import java.util.List;

public class Letter {
    private static final Letter instance = new Letter();

    public static Letter gI() {
        return instance;
    }


    public void reciveAll(Char p){
        List<TemplateThu> recives = new ArrayList<>();
        synchronized (p.letters){
            for (TemplateThu thu: p.letters){
                recives.add(thu);
            }
        }
        for (TemplateThu thu:recives){
            recive(p,thu.id);
        }
    }



    public void recive(Char p,int id){
        TemplateThu letter = p.findLetter(id);
        if(letter==null){
            p.getService().serverMessage("Không tìm thấy thư này");
            return;
        }
        if(letter.isSucess){
            return;
        }
        if(letter.Item != null){
            if(p.getCountNullItemBag()==0) {
                p.warningBagFull();
                return;
            }
            p.addItem(letter.Item);
            p.msgAddItemBag(letter.Item);
            letter.Item=null;
        }
        p.NhanQuaThu(letter);
        p.getService().reloadLetter();
    }





}
