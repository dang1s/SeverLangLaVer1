/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg188.real;

import Manager.Manager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sg188.data.DataCenter;
import com.sg188.data.ItemOption;
import com.sg188.data.ItemOptionTemplate;
import com.sg188.data.ItemTemplate;
import com.sg188.lib.Log;
import com.sg188.lib.ParseData;
import com.sg188.lib.Utlis;
import com.sg188.server.handler.IActionItem;
import com.sg188.server.lib.Message;
import com.sg188.server.lib.Writer;
import com.tgame.model.Caption;
import com.tgame.model.LangLa_hg;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * @author admin
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class Item implements Cloneable {

    public short id = -1;
    public boolean isLock;
    public long expiry = -1L;
    public byte he = -1;
    public byte level;
    public int index;
    public String strOptions = "";
    public int typeShop;
    public String strOptionsBackCaiTrang="";
    public int amount = 1;
    private long createdAt = System.currentTimeMillis();
    public byte renew;


    static Item getItemWithTypeAndLevel(int type, int level) {
        for (int i = 0; i < DataCenter.gI().ItemTemplate.length; i++) {
            if (DataCenter.gI().ItemTemplate[i].type == type && DataCenter.gI().ItemTemplate[i].levelNeed / 10 * 10 == level / 10 * 10) {
                return new Item(i, true);
            }

        }
        return null;
    }

    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        obj.put("id", this.id);
        obj.put("expire", this.expiry);
        obj.put("he", this.he);
        obj.put("created_at", this.createdAt);
        obj.put("isLock", this.isLock);
        obj.put("level", this.level);
        obj.put("index", this.index);
        obj.put("strOptions", this.strOptions);
        obj.put("amount", this.amount);
        obj.put("renew", this.renew);
        obj.put("strOptionsBackCaiTrang", this.strOptionsBackCaiTrang);
        return obj;
    }

    public void load(JSONObject obj) {
        ParseData parse = new ParseData(obj);
        this.id = parse.getShort("id");
        this.expiry = parse.getLong("expire");
        try {
            this.index = parse.getInt("index");
        } catch (Exception e) {
        }
        this.isLock = parse.getBoolean("isLock");
        if(this.id==178||this.id==500||this.id==931){
            this.isLock=true;
        }
        this.createdAt = parse.getLong("created_at");
        this.he = parse.getByte("he");
        this.strOptions = parse.getString("strOptions");
        this.strOptions = removeDuplicatesById(this.strOptions);
        this.level = parse.getByte("level");
        this.amount = parse.getInt("amount");
        if(parse.containsKey("renew")){
            this.renew = parse.getByte("renew");
        }
        if(parse.containsKey("strOptionsBackCaiTrang"))
        this.strOptionsBackCaiTrang = parse.getString("strOptionsBackCaiTrang");
    }

    public static String removeDuplicatesById(String input) {
        StringBuilder result = new StringBuilder();
        HashSet<String> seenIds = new HashSet<>(); // Dùng để lưu trữ các ID đã thấy

        String[] pairs = input.split(";");
        for (String pair : pairs) {
            String id = pair.split(",")[0]; // Tách và lấy ID từ mỗi cặp
            if (seenIds.add(id)) { // Nếu ID chưa được thêm vào, thêm cặp đó vào kết quả
                result.append(pair).append(";");
            }
        }

        // Loại bỏ dấu chấm phẩy cuối cùng nếu có
        if (result.length() > 0 && result.charAt(result.length() - 1) == ';') {
            result.deleteCharAt(result.length() - 1);
        }

        return result.toString();
    }

    @JsonIgnore
    public int countDaKham() {
        int count = 0;
        if (!strOptions.isEmpty()) {
            String[] Listoptions = strOptions.split(";");
            for (String op : Listoptions) {
                String[] option = op.split(",");
                if (option.length > 3) {
                    short id = Short.parseShort(option[0]);
                    if (id == 200) {
                        count++;
                    }
                    if (id == 201) {
                        count++;
                    }
                    if (id == 202) {
                        count++;
                    }
                    if (id == 203) {
                        count++;
                    }
                    if (id == 204) {
                        count++;
                    }
                    if (id == 205) {
                        count++;
                    }
                    if (id == 206) {
                        count++;
                    }
                    if (id == 199) {
                        count++;
                    }
                    if (id == 345) {
                        count++;
                    }
                    if (id == 344) {
                        count++;
                    }
                }

            }
        }
        return count;
    }
    @JsonIgnore
    public int checkCountKham() {
        if (this.getItemTemplate().levelNeed >= 50) {
            return 4;
        } else if (this.getItemTemplate().levelNeed >= 40) {
            return 3;
        } else {
            return this.getItemTemplate().levelNeed >= 30 ? 2 : 1;
        }
    }


    public static void setOptionsVuKhi(Item item, int level) {
        item.addItemOption(new ItemOption(2, (50 * level / 10), 50 + (50 * level / 10)));
        item.addItemOption(new ItemOption(3, level * 2, (level * 2) + 10));
        item.addItemOption(new ItemOption(20, level * 2, (level * 2) + 10));
        if (level < 60) {
            if (item.he == 5) {
                item.addItemOption(new ItemOption(21, level, level + 10));
            } else {
                item.addItemOption(new ItemOption(item.he + 21, level, level + 10));
            }
        } else {
            if (item.he == 5) {
                item.addItemOption(new ItemOption(21, (level) * 2, (level + 10) * 2));
            } else {
                item.addItemOption(new ItemOption(item.he + 21, (level + 10) * 2));
            }
        }
        if (level >= 10) {
            item.addItemOption(new ItemOption((item.he - 1) + 48, 5 * (level / 10)));
        }
        if (level >= 20) {
            item.addItemOption(new ItemOption(28, (level / 10) * 30));
        }
        if (level >= 30) {
            item.addItemOption(new ItemOption(31, (100 + ((level / 10) * 50))));
        }
        if (level >= 40) {
            if (level >= 60) {
                item.addItemOption(new ItemOption(41, 120));
            } else if (level >= 50) {
                item.addItemOption(new ItemOption(41, 110));
            } else if (level >= 40) {
                item.addItemOption(new ItemOption(41, 95));
            }
        }
        if (level >= 50) {
            if (level >= 60) {
                item.addItemOption(new ItemOption(47, 220));
            } else if (level >= 50) {
                item.addItemOption(new ItemOption(47, 200));
            }
        }
    }

    public static void setOptionsVuKhiToBag(Item item, int level) {
        item.addItemOption(new ItemOption(2, Utlis.NextInt(50 * level / 10, (50 + (50 * level / 10)))));
        item.addItemOption(new ItemOption(3, Utlis.NextInt(level * 2, (level * 2) + 10)));
        item.addItemOption(new ItemOption(20, Utlis.NextInt(level * 2, (level * 2) + 10)));
        if (level < 60) {
            if (item.he == 5) {
                item.addItemOption(new ItemOption(21, Utlis.NextInt(level, level + 10)));
            } else {
                item.addItemOption(new ItemOption(item.he + 21, Utlis.NextInt(level, level + 10)));
            }
        } else {
            if (item.he == 5) {
                item.addItemOption(new ItemOption(21, Utlis.NextInt((level) * 2, (level + 10) * 2)));
            } else {
                item.addItemOption(new ItemOption(item.he + 21, (level + 10) * 2));
            }
        }
        if (level >= 10) {
            item.addItemOption(new ItemOption((item.he - 1) + 48, 5 * (level / 10)));
        }
        if (level >= 20) {
            item.addItemOption(new ItemOption(28, (level / 10) * 30));
        }
        if (level >= 30) {
            item.addItemOption(new ItemOption(31, (100 + ((level / 10) * 50))));
        }
        if (level >= 40) {
            if (level >= 60) {
                item.addItemOption(new ItemOption(41, 120));
            } else if (level >= 50) {
                item.addItemOption(new ItemOption(41, 110));
            } else if (level >= 40) {
                item.addItemOption(new ItemOption(41, 95));
            }
        }
        if (level >= 50) {
            if (level >= 60) {
                item.addItemOption(new ItemOption(47, 220));
            } else if (level >= 50) {
                item.addItemOption(new ItemOption(47, 200));
            }
        }
    }

    @JsonIgnore
    public boolean isHokage() {
        if (!strOptions.isEmpty()) {
            String[] Listoptions = strOptions.split(";");
            for (String op : Listoptions) {
                String[] option = op.split(",");
                short id = Short.parseShort(option[0]);
                if (id == 148) {
                    return true;
                }

            }
        }
        return false;
    }
    @JsonIgnore
    public boolean isItemUpdate() {
        if (!strOptions.isEmpty()) {
            String[] Listoptions = strOptions.split(";");
            for (String op : Listoptions) {
                String[] option = op.split(",");
                short id = Short.parseShort(option[0]);
                if (id == 163 || id == 159 || id == 164 || id == 165) {
                    return true;
                }

            }
        }
        return false;
    }

    public static int CheckItemMSG8ByMap(int mapid) {
        switch (mapid) {
            case 75:
                return 194;
            case 60:
                return 195;

        }
        return -1;
    }

    public static void setOptionsTrangBiPhuKien(Item item, int level) {
        item.addItemOption(new ItemOption(0, 20 * (level / 10), (20 * (level / 10)) + 10));
        item.addItemOption(new ItemOption(1, 20 * (level / 10), (20 * (level / 10)) + 10));
        int num1 = 5 * ((level / 10));
        int num2 = 5 * ((level / 10) + 1);

        int num3 = 20 + (20 * ((level / 10) - 1));
        int num4 = 30 + (20 * ((level / 10) - 1));
        if (item.he == 5) {
            item.addItemOption(new ItemOption(7, num1, num2));
        } else {
            item.addItemOption(new ItemOption(item.he + 7, num1, num2));
        }

        if (item.getItemTemplate().type == 9) {
            item.addItemOption(new ItemOption(12, num1, num2));

        } else if (item.getItemTemplate().type == 7 || item.getItemTemplate().type == 5) {
            item.addItemOption(new ItemOption(14, num1, num2));

        } else if (item.getItemTemplate().type == 3) {
            item.addItemOption(new ItemOption(15, num1, num2));

        } else if (item.getItemTemplate().type == 0 || item.getItemTemplate().type == 2 || item.getItemTemplate().type == 4 || item.getItemTemplate().type == 6) {
            item.addItemOption(new ItemOption(13, num1, num2));

        } else if (item.getItemTemplate().type == 8) {
            item.addItemOption(new ItemOption(18, num3, num4));
        }

        if (item.getItemTemplate().type == 8) {
            item.addItemOption(new ItemOption(17, 40 + (20 * (level / 10))));
        } else if (item.getItemTemplate().type == 0 || item.getItemTemplate().type == 2 || item.getItemTemplate().type == 4 || item.getItemTemplate().type == 6) {
            item.addItemOption(new ItemOption(26, 2 * (level / 10)));
        } else {
            item.addItemOption(new ItemOption(27, 2 * (level / 10)));
        }
        if (level >= 20) {
            num1 = 50 + (((level / 10) - 1) * 50);
            if (item.getItemTemplate().type == 0 || item.getItemTemplate().type == 2 || item.getItemTemplate().type == 4 || item.getItemTemplate().type == 6 || item.getItemTemplate().type == 8) {
                item.addItemOption(new ItemOption(29, num1));
            } else {
                item.addItemOption(new ItemOption(30, num1));

            }
        }
        if (level >= 30) {
            num1 = 5 * (level / 10);
            if (item.getItemTemplate().type == 0 || item.getItemTemplate().type == 2 || item.getItemTemplate().type == 4 || item.getItemTemplate().type == 6 || item.getItemTemplate().type == 8) {
                item.addItemOption(new ItemOption(32, num1));
            } else {
                item.addItemOption(new ItemOption(33, num1));
            }
        }
        if (level >= 40) {
            num1 = 100 + (25 * ((level / 10) - 4));
            if (item.he == 5) {
                item.addItemOption(new ItemOption(35, num1));
            } else {
                item.addItemOption(new ItemOption(item.he + 35, num1));
            }
        }
        if (level >= 50) {
            num1 = 5 * (level / 10);
            item.addItemOption(new ItemOption(42, num1));
        }
    }

    public static void GetOptionHokage(Item item) {
        String[] options = item.strOptions.split(";");
        for (int i = 0; i < options.length; i++) {
            String[] option = options[i].split(",");
            int id = Integer.parseInt(option[0]);
            int param1 = Integer.parseInt(option[1]);
            if (option.length > 2) {
                int param2 = Integer.parseInt(option[2]);
                param2 += param2 * 6 / 100;
                option[2] = String.valueOf(param2);
            } else {
                param1 += param1 * 6 / 100;
                option[1] = String.valueOf(param1);
            }
            options[i] = String.join(",", option);
        }
        item.strOptions = String.join(";", options);
    }
    public static void getOptionBijuu(Item item,int percent) {
        String[] options = item.strOptions.split(";");
        for (int i = 0; i < options.length; i++) {
            String[] option = options[i].split(",");
            int id = Integer.parseInt(option[0]);
            int param1 = Integer.parseInt(option[1]);
            if (option.length > 2) {
                int param2 = Integer.parseInt(option[2]);
                param2 += param2 * percent / 100;
                option[2] = String.valueOf(param2);
            } else {
                param1 += param1 * percent / 100;
                option[1] = String.valueOf(param1);
            }
            options[i] = String.join(",", option);
        }
        item.strOptions = String.join(";", options);
    }

    public static void getOptionAoChoang(Item item) {
        int level = item.getItemTemplate().levelNeed;
        item.addItemOption(new ItemOption(136, 15 + level, 15 + level + 5));
        item.addItemOption(new ItemOption(137, 15 + level, 15 + level + 5));
        if (level >= 45) {
            item.addItemOption(new ItemOption(149, (int) ((int) level * 0.3 - 11.4), (int) ((int) level * 0.3 - 10.4)));
        }
        if (level >= 48) {
            item.addItemOption(new ItemOption(174, (int) ((int) level * 0.3 - 8.5), (int) ((int) level * 0.3 - 7.5)));
        }
        if (level >= 49) {
            item.addItemOption(new ItemOption(174, (int) ((int) level * 0.3 - 8.5), (int) ((int) level * 0.3 - 7.5)));
            item.addItemOption(new ItemOption(311, 140, 150));
            item.addItemOption(new ItemOption(346, 200, 210));
        }
        item.addItemOption(new ItemOption(0, 20 * level + 600, 20 * level + 600 + 100));
        item.addItemOption(new ItemOption(119, 30+level*3, 30+level*3+15));
        item.addItemOption(new ItemOption(120, 30+level*3, 30+level*3+15));
        item.addItemOption(new ItemOption(121, 50+level*6, 50+level*6+20));
        item.addItemOption(new ItemOption(122, 20+level*2, 20+level*2+10));
        item.addItemOption(new ItemOption(123, (int) (level * 0.4), (int) (level * 0.4) + 2));
        item.addItemOption(new ItemOption(124, (int) (level * 0.4), (int) (level * 0.4) + 2));
        item.addItemOption(new ItemOption(125, (int) (level * 0.4), (int) (level * 0.4) + 2));
        item.addItemOption(new ItemOption(126, (int) (level * 0.4), (int) (level * 0.4) + 2));
        item.addItemOption(new ItemOption(127, (int) (level * 0.4), (int) (level * 0.4) + 2));
    }

    public static void getOptionCaiTrang(Item item) {
        int level = item.getItemTemplate().levelNeed;
        if (level >= 10) {
            item.addItemOption(new ItemOption(209, 10 + (level - 10) / 2 * 5));
        } else if (level >= 12) {
            item.addItemOption(new ItemOption(209, 10 + (level - 10) / 2 * 5));
        } else if (level >= 14) {
            item.addItemOption(new ItemOption(209, 10 + (level - 10) / 2 * 5));
        } else if (level >= 16) {
            item.addItemOption(new ItemOption(209, 10 + (level - 10) / 2 * 5));
        } else if (level >= 18) {
            item.addItemOption(new ItemOption(209, 10 + (level - 10) / 2 * 5));
        } else if (level >= 20) {
            item.addItemOption(new ItemOption(209, 10 + (level - 10) / 2 * 5));
        } else if (level >= 22) {
            item.addItemOption(new ItemOption(209, 10 + (level - 10) / 2 * 5));
        } else if (level >= 24) {
            item.addItemOption(new ItemOption(209, 10 + (level - 10) / 2 * 5));
        } else if (level >= 26) {
            item.addItemOption(new ItemOption(209, 10 + (level - 10) / 2 * 5));
        } else if (level >= 28) {
            item.addItemOption(new ItemOption(209, 10 + (level - 10) / 2 * 5));
        } else if (level >= 30) {
            item.addItemOption(new ItemOption(209, 10 + (level - 10) / 2 * 5));
        } else if (level >= 32) {
            item.addItemOption(new ItemOption(209, 10 + (level - 10) / 2 * 5));
        } else if (level >= 34) {
            item.addItemOption(new ItemOption(209, 10 + (level - 10) / 2 * 5));
        } else if (level >= 36) {
            item.addItemOption(new ItemOption(209, 10 + (level - 10) / 2 * 5));
        } else if (level >= 38) {
            item.addItemOption(new ItemOption(209, 10 + (level - 10) / 2 * 5));
        } else if (level >= 40) {
            item.addItemOption(new ItemOption(209, 10 + (level - 10) / 2 * 5));
        }


    }

    public static void getOptionThuNuoi(Item item) {
        int level = item.getItemTemplate().levelNeed;

        if (level == 15) {
            item.addItemOption(new ItemOption(0, 200, 300));
            item.addItemOption(new ItemOption(2, 40, 60));
            item.addItemOption(new ItemOption(5, 20, 30));
            item.addItemOption(new ItemOption(136, 10, 15));
            item.addItemOption(new ItemOption(137, 10, 15));


        } else if (level == 20) {
            item.addItemOption(new ItemOption(0, 300, 400));
            item.addItemOption(new ItemOption(2, 60, 80));
            item.addItemOption(new ItemOption(5, 40, 50));
            item.addItemOption(new ItemOption(136, 20, 25));
            item.addItemOption(new ItemOption(137, 20, 25));
            item.addItemOption(new ItemOption(149, 1, 2));


        } else if (level == 25) {
            item.addItemOption(new ItemOption(0, 400, 500));
            item.addItemOption(new ItemOption(2, 80, 100));
            item.addItemOption(new ItemOption(5, 60, 70));
            item.addItemOption(new ItemOption(136, 30, 40));
            item.addItemOption(new ItemOption(137, 30, 40));
            item.addItemOption(new ItemOption(149, 2, 3));
            item.addItemOption(new ItemOption(151, 40, 50));

        }


    }

    public static void getOptionTanTo(Item item) {
        int level = item.getItemTemplate().levelNeed;

        if (level == 25) {
            item.level = 4;

            item.addItemOption(new ItemOption(151, 70, 80));
            item.addItemOption(new ItemOption(152, 70, 80));
            item.addItemOption(new ItemOption(114, 200, 220));
            item.addItemOption(new ItemOption(109, 30, 40));
            item.addItemOption(new ItemOption(158, 3));

        } else if (level == 30) {
            item.level = 8;

            item.addItemOption(new ItemOption(151, 90, 100));
            item.addItemOption(new ItemOption(152, 90, 100));
            item.addItemOption(new ItemOption(114, 250, 300));
            item.addItemOption(new ItemOption(109, 60, 80));
            item.addItemOption(new ItemOption(158, 3));
            item.addItemOption(new ItemOption(2, 50, 100));
            item.addItemOption(new ItemOption(167, 60, 80));
            item.addItemOption(new ItemOption(123, 6, 8));

        } else if (level == 35) {
            item.level = 12;
            item.addItemOption(new ItemOption(151, 120, 140));
            item.addItemOption(new ItemOption(152, 120, 140));
            item.addItemOption(new ItemOption(114, 300, 350));
            item.addItemOption(new ItemOption(109, 80, 100));
            item.addItemOption(new ItemOption(158, 3));
            item.addItemOption(new ItemOption(2, 100, 150));
            item.addItemOption(new ItemOption(167, 80, 100));
            item.addItemOption(new ItemOption(123, 8, 10));
        }
        item.addItemOption(new ItemOption(148, 1));


    }

    public ItemOption[] L() {
        if (this.strOptions != null && this.strOptions.length() > 0&&!this.strOptions.isEmpty()) {
            String[] var1;
            ItemOption[] var2 = new ItemOption[(var1 = Utlis.split(this.strOptions, ";")).length];
            for (int var3 = 0; var3 < var1.length; ++var3) {
                try {
                    var2[var3] = new ItemOption(var1[var3]);
                } catch (Exception e) {
                    e.printStackTrace();
                    return  null;
                }
            }

            return var2;
        } else {
            return null;
        }
    }

    public static String a(Vector var0) {
        ItemOption[] var1 = new ItemOption[var0.size()];

        for (int var2 = 0; var2 < var1.length; ++var2) {
            var1[var2] = (ItemOption) var0.get(var2);
        }

        return a(var1);
    }

    public static String a(ItemOption[] var0) {
        String var1 = "";
        if (var0 != null) {
            for (int var2 = 0; var2 < var0.length; ++var2) {
                var1 = var1 + var0[var2].g();
                if (var2 < var0.length - 1) {
                    var1 = var1 + ";";
                }
            }
        }

        return var1;
    }

    public void updateOption() {
        byte level = this.level;
        this.a(0);
        ItemOption[] var2 = this.L();
        Vector var3 = new Vector();
        String[] var4 = new String[]{"", "168,10,-1", "169,10,-1", "170,10,-1", "171,10,-1", "172,10,-1"};
        String[] var5 = new String[]{"", "259,80,-1", "260,80,-1", "261,80,-1", "262,80,-1", "263,80,-1"};
        boolean var6 = false;
        boolean var7 = false;
        for (int var8 = 0; var8 < var2.length; ++var8) {
            if (var2[var8].a[0] != 148) {
                if (var2[var8].getItemOptionTemplate().type == 2 && !var6) {
                    var3.add(new ItemOption(var4[this.he]));
                    var3.add(new ItemOption("254,10,-1"));
                    var6 = true;
                }

                var3.add(var2[var8]);
                if (!var7) {
                    if (this.getItemTemplate().levelNeed / 10 == 4 && var2[var8].getItemOptionTemplate().type == 6) {
                        var3.add(new ItemOption("47,150,-1"));
                        var7 = true;
                    }

                    if (var2[var8].getItemOptionTemplate().type == 7) {
                        if (this.getItemTemplate().levelNeed / 10 >= 5) {
                            var3.add(new ItemOption("252,5,-1"));
                            var3.add(new ItemOption(var5[this.he]));
                        }

                        if (this.getItemTemplate().levelNeed / 10 >= 6) {
                            var3.add(new ItemOption("286,300,-1"));
                        }

                        var7 = true;
                    }
                }
            }
        }

        var3.add(new ItemOption("165,0,-1"));
        this.strOptions = Item.a(var3);
        this.a(level);
    }

    public void updateOption_2() {
        byte level = this.level;
        this.a(0);
        ItemOption[] var2 = this.L();
        Vector var3 = new Vector();
        String[] var4 = new String[]{"", "168,10,-1", "169,10,-1", "170,10,-1", "171,10,-1", "172,10,-1"};
        String[] var5 = new String[]{"", "259,80,-1", "260,80,-1", "261,80,-1", "262,80,-1", "263,80,-1"};
        boolean var6 = false;
        boolean var7 = false;
        for (int var8 = 0; var8 < var2.length; ++var8) {
            if (var2[var8].a[0] != 165) {
                var3.add(var2[var8]);
                if (!var7) {
                    if (var2[var8].getItemOptionTemplate().type == 7) {
                        if (this.getItemTemplate().levelNeed / 10 == 4) {
                            var3.add(new ItemOption("252,5,-1"));
                            var3.add(new ItemOption(var5[this.he]));
                        }
                    }
                    if (this.getItemTemplate().levelNeed / 10 == 5 && var2[var8].getItemOptionTemplate().type == 10) {
                        var3.add(new ItemOption("286,300,-1"));
                    }
                    if (this.getItemTemplate().levelNeed / 10 == 6 && var2[var8].getItemOptionTemplate().type == 11) {
                        var3.add(new ItemOption("350,300,-1"));
                        var3.add(new ItemOption("360,5,-1"));
                    }
                }
            }
        }
        int param = 0;
        if(this.getItemTemplate().levelNeed < 50){
            param = 6;
        } else if (this.getItemTemplate().levelNeed < 60) {
            param = 7;
        } else if (this.getItemTemplate().levelNeed < 70) {
            param = 8;
        }
        var3.add(new ItemOption("361," + param + ",-1"));
        this.strOptions = Item.a(var3);
        this.a(level);
    }

    public void updateOptionByakugan() {
        byte level = this.level;
        this.a(0);
        ItemOption[] var2 = this.L();
        Vector var3 = new Vector();
        String[] var4 = null;
        if (this.getItemTemplate().type == 6) {
            var4 = new String[]{"166,60,-1", "173,20,-1", "253,2000,-1", "163,0,-1"};
        } else if (this.getItemTemplate().type == 5) {
            var4 = new String[]{"174,2,-1", "173,20,-1", "253,2000,-1", "163,0,-1"};
        } else if (this.getItemTemplate().type == 9) {
            var4 = new String[]{"255,2,-1", "173,20,-1", "253,2000,-1", "163,0,-1"};
        }

        boolean var5 = false;
        boolean var6 = false;

        for (int var7 = 0; var7 < var2.length; ++var7) {
            if (var2[var7].a[0] != 148) {
                if (var2[var7].getItemOptionTemplate().type == 2 && !var5) {
                    var3.add(new ItemOption(var4[0]));
                    var3.add(new ItemOption(var4[1]));
                    var5 = true;
                }

                var3.add(var2[var7]);
                if (!var6) {
                    if (this.getItemTemplate().levelNeed / 10 == 4 && var2[var7].getItemOptionTemplate().type == 6) {
                        var3.add(new ItemOption("42,20,-1"));
                        var6 = true;
                    }

                    if (var2[var7].getItemOptionTemplate().type == 7) {
                        if (this.getItemTemplate().levelNeed / 10 >= 5) {
                            var3.add(new ItemOption(var4[2]));
                        }

                        if (this.getItemTemplate().levelNeed / 10 >= 6) {
                            this.b(var3);
                        }

                        var6 = true;
                    }
                }
            }
        }

        var3.add(new ItemOption(var4[3]));
        this.strOptions = Item.a(var3);
        this.a(level);
    }

    public void updateOptionByakugan_2() {
        byte level = this.level;
        this.a(0);
        ItemOption[] var2 = this.L();
        Vector var3 = new Vector();
        String[] var4 = null;
        if (this.getItemTemplate().type == 6) {
            var4 = new String[]{"166,60,-1", "173,20,-1", "253,2000,-1", "163,0,-1"};
        } else if (this.getItemTemplate().type == 5) {
            var4 = new String[]{"174,2,-1", "173,20,-1", "253,2000,-1", "163,0,-1"};
        } else if (this.getItemTemplate().type == 9) {
            var4 = new String[]{"255,2,-1", "173,20,-1", "253,2000,-1", "163,0,-1"};
        }

        boolean var5 = false;
        boolean var6 = false;

        for (int var7 = 0; var7 < var2.length; ++var7) {
            if (var2[var7].a[0] != 163) {
                var3.add(var2[var7]);
                if (!var6) {
                    if (this.getItemTemplate().levelNeed / 10 == 5 && var2[var7].getItemOptionTemplate().type == 10) {
                        this.b(var3);
                    }
                    if (this.getItemTemplate().levelNeed / 10 == 6 && var2[var7].getItemOptionTemplate().type == 11) {
                        int randomNumber = 355 + Utlis.nextInt(5);
                        var3.add(new ItemOption(""+randomNumber+",40,-1"));
                    }
                    if (var2[var7].getItemOptionTemplate().type == 7) {
                        if (this.getItemTemplate().levelNeed / 10 == 4) {
                            var3.add(new ItemOption(var4[2]));
                        }
                    }
                }
            }
        }

        int param = 0;
        if(this.getItemTemplate().levelNeed < 50){
            param = 6;
        } else if (this.getItemTemplate().levelNeed < 60) {
            param = 7;
        } else if (this.getItemTemplate().levelNeed < 70) {
            param = 8;
        }
        var3.add(new ItemOption("361," + param + ",-1"));
        this.strOptions = Item.a(var3);
        this.a(level);
    }

    public void updateOptionRinegan() {
        byte level = this.level;
        this.a(0);
        ItemOption[] var2 = this.L();
        Vector var3 = new Vector();
        String[] var4 = null;
        if (this.getItemTemplate().type == 0) {
            var4 = new String[]{"256,4,-1", "173,20,-1", "253,2000,-1", "164,0,-1"};
        } else if (this.getItemTemplate().type == 4) {
            var4 = new String[]{"257,4,-1", "173,20,-1", "253,2000,-1", "164,0,-1"};
        } else if (this.getItemTemplate().type == 3) {
            var4 = new String[]{"258,25,-1", "173,20,-1", "253,2000,-1", "164,0,-1"};
        }

        boolean var5 = false;
        boolean var6 = false;

        for (int var7 = 0; var7 < var2.length; ++var7) {
            if (var2[var7].a[0] != 148) {
                if (var2[var7].getItemOptionTemplate().type == 2 && !var5) {
                    var3.add(new ItemOption(var4[0]));
                    var3.add(new ItemOption(var4[1]));
                    var5 = true;
                }

                var3.add(var2[var7]);
                if (!var6) {
                    if (this.getItemTemplate().levelNeed / 10 == 4 && var2[var7].getItemOptionTemplate().type == 6) {
                        var3.add(new ItemOption("42,20,-1"));
                        var6 = true;
                    }

                    if (var2[var7].getItemOptionTemplate().type == 7) {
                        if (this.getItemTemplate().levelNeed / 10 >= 5) {
                            var3.add(new ItemOption(var4[2]));
                        }

                        if (this.getItemTemplate().levelNeed / 10 >= 6) {
                            this.b(var3);
                        }

                        var6 = true;
                    }
                }
            }
        }

        var3.add(new ItemOption(var4[3]));
        this.strOptions = Item.a(var3);
        this.a(level);
    }
    public void updateOptionRinegan_2() {
        byte level = this.level;
        this.a(0);
        ItemOption[] var2 = this.L();
        Vector var3 = new Vector();
        String[] var4 = null;
        if (this.getItemTemplate().type == 0) {
            var4 = new String[]{"256,4,-1", "173,20,-1", "253,2000,-1", "164,0,-1"};
        } else if (this.getItemTemplate().type == 4) {
            var4 = new String[]{"257,4,-1", "173,20,-1", "253,2000,-1", "164,0,-1"};
        } else if (this.getItemTemplate().type == 3) {
            var4 = new String[]{"258,25,-1", "173,20,-1", "253,2000,-1", "164,0,-1"};
        }

        boolean var5 = false;
        boolean var6 = false;

        for (int var7 = 0; var7 < var2.length; ++var7) {
            if (var2[var7].a[0] != 164) {
                var3.add(var2[var7]);
                if (!var6) {
                    if (this.getItemTemplate().levelNeed / 10 == 5 && var2[var7].getItemOptionTemplate().type == 10) {
                        this.b(var3);
                    }
                    if (this.getItemTemplate().levelNeed / 10 == 6 && var2[var7].getItemOptionTemplate().type == 11) {
                        int randomNumber = 355 + Utlis.nextInt(5);
                        var3.add(new ItemOption(""+randomNumber+",40,-1"));
                    }
                    if (var2[var7].getItemOptionTemplate().type == 7) {
                        if (this.getItemTemplate().levelNeed / 10 == 4) {
                            var3.add(new ItemOption(var4[2]));
                        }
                    }
                }
            }
        }

        int param = 0;
        if(this.getItemTemplate().levelNeed < 50){
            param = 6;
        } else if (this.getItemTemplate().levelNeed < 60) {
            param = 7;
        } else if (this.getItemTemplate().levelNeed < 70) {
            param = 8;
        }
        var3.add(new ItemOption("361," + param + ",-1"));
        this.strOptions = Item.a(var3);
        this.a(level);
    }

    public void updateTuLuyen(int i) {
        if (this.getItemTemplate().type != 11) {
            return;
        }
        ItemOption[] options = this.getItemOption();
        Vector listops = new Vector<>();
        ItemOption tuluyen = null;
        for (ItemOption ops : options) {
            if (ops.getId() == 128) {
                tuluyen = ops;
            }
            listops.add(ops);
        }
        if (tuluyen == null) {
            return;
        }
        int value = tuluyen.getvalue();
        if (value == tuluyen.f()) {
            return;
        }
        int valueadd = value + i;
        if (valueadd >= tuluyen.f()) {
            valueadd = tuluyen.f();
        }
        tuluyen.c(valueadd);
        this.strOptions = Item.a(listops);
        if (valueadd >= 1000)
            this.a(valueadd / 1000);
    }

    public void updateViThu(int i) {
        if (this.getItemTemplate().type != 10) {
            return;
        }
        ItemOption[] options = this.getItemOption();
        Vector listops = new Vector<>();
        ItemOption tuluyen = null;
        for (ItemOption ops : options) {
            if (ops.getId() == 305) {
                tuluyen = ops;
            }
            listops.add(ops);
        }
        if (tuluyen == null) {
            return;
        }
        int value = tuluyen.getvalue();
        if (value == tuluyen.f()) {
            return;
        }
        int valueadd = value + i;
        if (valueadd >= tuluyen.f()) {
            valueadd = tuluyen.f();
        }
        tuluyen.c(valueadd);
        this.strOptions = Item.a(listops);
        if (valueadd >= 10000)
            this.a(valueadd / 10000);
    }

    public void updateOptionSharigan() {
        byte level = this.level;
        this.a(0);
        ItemOption[] var2 = this.L();
        Vector var3 = new Vector();
        String[] var4 = null;
        if (this.getItemTemplate().type == 8) {
            var4 = new String[]{"161,85,-1", "173,20,-1", "253,2000,-1", "159,0,-1"};
        } else if (this.getItemTemplate().type == 2) {
            var4 = new String[]{"162,1,-1", "173,20,-1", "253,2000,-1", "159,0,-1"};
        } else if (this.getItemTemplate().type == 7) {
            var4 = new String[]{"167,70,-1", "173,20,-1", "253,2000,-1", "159,0,-1"};
        }

        boolean var5 = false;
        boolean var6 = false;

        for (int var7 = 0; var7 < var2.length; ++var7) {
            if (var2[var7].a[0] != 148) {
                if (var2[var7].getItemOptionTemplate().type == 2 && !var5) {
                    var3.add(new ItemOption(var4[0]));
                    var3.add(new ItemOption(var4[1]));
                    var5 = true;
                }

                var3.add(var2[var7]);
                if (!var6) {
                    if (this.getItemTemplate().levelNeed / 10 == 4 && var2[var7].getItemOptionTemplate().type == 6) {
                        var3.add(new ItemOption("42,20,-1"));
                        var6 = true;
                    }

                    if (var2[var7].getItemOptionTemplate().type == 7) {
                        if (this.getItemTemplate().levelNeed / 10 >= 5) {
                            var3.add(new ItemOption(var4[2]));
                        }

                        if (this.getItemTemplate().levelNeed / 10 >= 6) {
                            this.b(var3);
                        }

                        var6 = true;
                    }
                }
            }
        }

        var3.add(new ItemOption(var4[3]));
        this.strOptions = Item.a(var3);
        this.a(level);
    }
    public void updateOptionSharigan_2() {
        byte level = this.level;
        this.a(0);
        ItemOption[] var2 = this.L();
        Vector var3 = new Vector();
        String[] var4 = null;
        if (this.getItemTemplate().type == 8) {
            var4 = new String[]{"161,85,-1", "173,20,-1", "253,2000,-1", "159,0,-1"};
        } else if (this.getItemTemplate().type == 2) {
            var4 = new String[]{"162,1,-1", "173,20,-1", "253,2000,-1", "159,0,-1"};
        } else if (this.getItemTemplate().type == 7) {
            var4 = new String[]{"167,70,-1", "173,20,-1", "253,2000,-1", "159,0,-1"};
        }

        boolean var5 = false;
        boolean var6 = false;

        for (int var7 = 0; var7 < var2.length; ++var7) {
            if (var2[var7].a[0] != 159) {
                var3.add(var2[var7]);
                if (!var6) {
                    if (this.getItemTemplate().levelNeed / 10 == 5 && var2[var7].getItemOptionTemplate().type == 10) {
                        this.b(var3);
                    }
                    if (this.getItemTemplate().levelNeed / 10 == 6 && var2[var7].getItemOptionTemplate().type == 11) {
                        int randomNumber = 355 + Utlis.nextInt(5);
                        var3.add(new ItemOption(""+randomNumber+",40,-1"));
                    }
                    if (var2[var7].getItemOptionTemplate().type == 7) {
                        if (this.getItemTemplate().levelNeed / 10 == 4) {
                            var3.add(new ItemOption(var4[2]));
                        }
                    }
                }
            }
        }

        int param = 0;
        if(this.getItemTemplate().levelNeed < 50){
            param = 6;
        } else if (this.getItemTemplate().levelNeed < 60) {
            param = 7;
        } else if (this.getItemTemplate().levelNeed < 70) {
            param = 8;
        }
        var3.add(new ItemOption("361," + param + ",-1"));
        this.strOptions = Item.a(var3);
        this.a(level);
    }


    public void b(Vector var1) {
        switch (this.getItemTemplate().type) {
            case 6:
                var1.add(new ItemOption("323,200,-1"));
                return;
            case 7:
                var1.add(new ItemOption("324,250,-1"));
            default:
                var1.add(new ItemOption("362,"+Utlis.nextInt(50,200)+",-1"));
                return;
            case 8:
                var1.add(new ItemOption("304,300,-1"));
                return;
            case 9:
                var1.add(new ItemOption("310,250,-1"));
        }
    }

    public static void getOptionBiKip(Item item) {
        int level = item.getItemTemplate().levelNeed;

        if (level >= 25) {
            item.addItemOption(new ItemOption(128, 0, 8000));
            item.addItemOption(new ItemOption(0, 350, 400));
            item.addItemOption(new ItemOption(1, 350, 400));
            if (item.he == 1) {
                item.addItemOption(new ItemOption(109, 60, 70));
                item.addItemOption(new ItemOption(114, 120, 140));
            } else if (item.he == 2) {
                item.addItemOption(new ItemOption(110, 60, 70));
                item.addItemOption(new ItemOption(115, 120, 140));
            } else if (item.he == 3) {
                item.addItemOption(new ItemOption(111, 60, 70));
                item.addItemOption(new ItemOption(113, 120, 140));
            } else if (item.he == 4) {
                item.addItemOption(new ItemOption(112, 60, 70));
                item.addItemOption(new ItemOption(117, 120, 140));
            } else if (item.he == 5) {
                item.addItemOption(new ItemOption(108, 60, 70));
                item.addItemOption(new ItemOption(113, 120, 140));
            }
        } else if (level >= 15) {
            item.addItemOption(new ItemOption(128, 0, 4000));
            item.addItemOption(new ItemOption(0, 150, 200));
            item.addItemOption(new ItemOption(1, 150, 200));
            if (item.he == 1) {
                item.addItemOption(new ItemOption(109, 20, 30));
                item.addItemOption(new ItemOption(114, 60, 80));
            } else if (item.he == 2) {
                item.addItemOption(new ItemOption(110, 20, 30));
                item.addItemOption(new ItemOption(115, 60, 80));
            } else if (item.he == 3) {
                item.addItemOption(new ItemOption(111, 20, 30));
                item.addItemOption(new ItemOption(116, 60, 80));
            } else if (item.he == 4) {
                item.addItemOption(new ItemOption(112, 20, 30));
                item.addItemOption(new ItemOption(117, 60, 80));
            } else if (item.he == 5) {
                item.addItemOption(new ItemOption(108, 20, 30));
                item.addItemOption(new ItemOption(113, 60, 80));
            }

        }

    }

    public boolean checkFullTuLuyen() {
        try {
            ItemOption[] var1 = this.L();
            ItemOption var2;
            if (this.getItemTemplate().type == 11 && (var2 = var1[0]).a[1] == var1[0].f()) {
                return true;
            }
        } catch (Exception var3) {
        }

        return false;
    }

    public boolean u() {
        if (this.getItemTemplate().type >= 0 && this.getItemTemplate().type <= 9) {
            if (this.X()) {
                if (this.getItemTemplate().levelNeed >= 60 && this.level < 19 || this.getItemTemplate().levelNeed >= 50 && this.level < 18 || this.getItemTemplate().levelNeed >= 40 && this.level < 17) {
                    return true;
                }
            } else if (this.W()) {
                if (this.getItemTemplate().levelNeed >= 60 && this.level < 18 || this.getItemTemplate().levelNeed >= 50 && this.level < 17 || this.getItemTemplate().levelNeed >= 40 && this.level < 16) {
                    return true;
                }
            } else if (this.getItemTemplate().levelNeed >= 50 && this.level < 16 || this.getItemTemplate().levelNeed >= 40 && this.level < 14 || this.getItemTemplate().levelNeed >= 30 && this.level < 12 || this.getItemTemplate().levelNeed >= 20 && this.level < 8 || this.level < 4) {
                return true;
            }
        }

        return false;
    }

    static Item getItemWithTypeAndLevel(int type, int level, int gioiTinh, int idClass) {
        for (int i = 0; i < DataCenter.gI().ItemTemplate.length; i++) {
            if (DataCenter.gI().ItemTemplate[i].type == type && DataCenter.gI().ItemTemplate[i].levelNeed / 10 * 10 == level / 10 * 10) {
                if ((DataCenter.gI().ItemTemplate[i].gioiTinh == 2 || DataCenter.gI().ItemTemplate[i].gioiTinh == gioiTinh) && (DataCenter.gI().ItemTemplate[i].idClass == 0 || DataCenter.gI().ItemTemplate[i].idClass == idClass)) {
                    return new Item(i, true);
                } else {

                    Log.debug("null: " + type + "," + level + "," + gioiTinh + "," + idClass);
                    Log.debug("null: " + DataCenter.gI().ItemTemplate[i].gioiTinh + "," + DataCenter.gI().ItemTemplate[i].idClass);
                }
            }
        }
        return null;
    }

    @JsonIgnore
    public String strOptionsMain = "";
    @JsonIgnore
    public IActionItem[] arrayAction;

    @JsonIgnore
    public boolean isVuKhi() {
        return this.getItemTemplate().type == 1;
    }
    @JsonIgnore
    public boolean isBayakugan() {
        return this.getItemTemplate().type == 6 || this.getItemTemplate().type == 5 || this.getItemTemplate().type == 9;
    }
    @JsonIgnore
    public boolean isRenegan() {
        return this.getItemTemplate().type == 0 || this.getItemTemplate().type == 4 || this.getItemTemplate().type == 3;
    }
    @JsonIgnore
    public boolean isSharigan() {
        return this.getItemTemplate().type == 8 || this.getItemTemplate().type == 2 || this.getItemTemplate().type == 7;
    }

    @JsonIgnore
    public boolean isTrangBi() {
        return this.getItemTemplate().type == 0 || this.getItemTemplate().type == 2 || this.getItemTemplate().type == 4 || this.getItemTemplate().type == 6 || this.getItemTemplate().type == 8;
    }

    @JsonIgnore
    public boolean isPhuKien() {
        return this.getItemTemplate().type == 3 || this.getItemTemplate().type == 5 || this.getItemTemplate().type == 7 || this.getItemTemplate().type == 9;
    }

    public Object clone() {
        return this.cloneItem();
    }

    public Item cloneItem() {
        try {
            return (Item) super.clone();
        } catch (Exception var2) {
            Utlis.println(var2);
            return null;
        }
    }

    public Item() {

    }

    public Item(JSONObject obj) {
        load(obj);
    }

    public Item(int id) {
        this.id = (short) id;
        if (this.isTypeTrangBi()) {
            he = (byte) Utlis.nextInt(1, 5);
        }
    }

    public Item(boolean isSet) {
        if (this.isTypeTrangBi()) {
            he = (byte) Utlis.nextInt(1, 5);
        }
    }

    public Item(int id, boolean b) {
        this.id = (short) id;
        this.isLock = b;
        if (this.isTypeTrangBi()) {
            he = (byte) Utlis.nextInt(1, 5);
        }
    }

    public Item(int id, boolean b, int amount) {
        this.id = (short) id;
        this.isLock = b;
        this.amount = amount;
        if (this.isTypeTrangBi()) {
            he = (byte) Utlis.nextInt(1, 5);
        }
    }

    public Item(int id, boolean b, int amount, int level) {
        this.id = (short) id;
        this.isLock = b;
        this.amount = amount;
        this.level = (byte) level;
        if (this.isTypeTrangBi()) {
            he = (byte) Utlis.nextInt(1, 5);
        }
    }

    public Item(int id, boolean b, int amount, int level, String strOptions) {
        this.id = (short) id;
        this.isLock = b;
        this.amount = amount;
        this.level = (byte) level;
        if (this.isTypeTrangBi()) {
            he = (byte) Utlis.nextInt(1, 5);
        }
        this.strOptions = strOptions;
        this.strOptionsMain = strOptions;
    }

    @JsonIgnore
    public ItemTemplate getItemTemplate() {
        return Manager.gI().itemTemplates.get(id);
    }

    @JsonIgnore
    public boolean isTypeTrangBi() {
        for (int var1 = 0; var1 < DataCenter.gI().DataTypeItemBody.length; ++var1) {
            if (DataCenter.gI().DataTypeItemBody[var1].type == this.getItemTemplate().type) {
                return true;
            }
        }

        return false;
    }

    public boolean W() {
        ItemOption[] var1;
        if ((var1 = this.getItemOption()) != null) {
            for (int var2 = 0; var2 < var1.length; ++var2) {
                if (var1[var2].j()) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean X() {
        ItemOption[] var1;
        if ((var1 = this.getItemOption()) != null) {
            for (int var2 = 0; var2 < var1.length; ++var2) {
                if (var1[var2].k()) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isItemTrangBi() {
        return this.isVuKhi() || this.isTrangBi() || this.isPhuKien();
    }

    public boolean isItemBody() {
        return this.getItemTemplate().type >= 0 && this.getItemTemplate().type <= 16;
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {
//        if (amount <= 0) {
//            amount = 1;
//        }

        this.amount = amount;
    }

    public void read(Message var1) throws IOException {
        this.id = var1.readShort();
        if (this.id >= 0) {
            this.isLock = var1.readBoolean();
            this.expiry = var1.readLong();
            if (this.isTypeTrangBi()) {
                this.he = var1.readByte();
                this.level = var1.readByte();
                this.strOptions = var1.reader.readUTF();
            } else {
                this.setAmount(var1.readInt());
            }

            if (this.getItemTemplate().type == 99) {
                this.strOptions = var1.reader.readUTF();
            }

            this.index = var1.readShort();
        }
    }

    public int getTinhThach() {
        if (this.id >= 125 && this.id <= 133 || this.id == 535) {
            short var1 = 0;
            switch (this.id) {
                case 125:
                    var1 = 120;
                    break;
                case 126:
                    var1 = 240;
                    break;
                case 127:
                    var1 = 360;
                    break;
                case 128:
                    var1 = 480;
                    break;
                case 129:
                    var1 = 600;
                    break;
                case 130:
                    var1 = 720;
                    break;
                case 131:
                    var1 = 800;
                    break;
                case 132:
                    var1 = 1000;
                    break;
                case 133:
                    var1 = 1200;
                    break;
                case 535:
                    var1 = 1600;
            }

            long var2;
            int var4;
            if ((var2 = (this.expiry - Utlis.time()) / 86400000L) > 0L && (var4 = (int) (30L - var2) * var1 / 30) > 0 && var4 < var1) {
                return var1 - var4;
            }
        }

        if (this.getItemTemplate().type == 10 && this.expiry == -1L) {
            try {
                return Integer.parseInt(this.getItemTemplate().detail);
            } catch (Exception var7) {
                return 0;
            }
        } else {
            int var10 = 0;
            int var8 = 0;
            int var3 = 0;
            ItemOption[] var9;
            if ((var9 = this.L()) != null) {
                float var5 = 1.0F;

                for (int var6 = 0; var6 < var9.length; ++var6) {
                    if (var9[var6].getItemOptionTemplate().type <= 0) {
                        ++var10;
                    } else if (var9[var6].getItemOptionTemplate().type == 2) {
                        ++var8;
                    } else if (var9[var6].getItemOptionTemplate().type > 2 && var9[var6].getItemOptionTemplate().type <= 7) {
                        ++var3;
                    }

                    if (var9[var6].getItemOptionTemplate().id == 148) {
                        var5 = 1.2F;
                    } else if (var9[var6].getItemOptionTemplate().type == 9) {
                        var5 = 1.4F;
                    }
                }

                if (var10 >= 2 && var8 >= 2) {
                    byte var11 = 2;
                    if (this.getItemTemplate().levelNeed >= 50) {
                        var11 = 5;
                    } else if (this.getItemTemplate().levelNeed >= 40) {
                        var11 = 4;
                    } else if (this.getItemTemplate().levelNeed >= 30) {
                        var11 = 3;
                    } else if (this.getItemTemplate().levelNeed >= 20) {
                        var11 = 2;
                    } else if (this.getItemTemplate().levelNeed >= 10) {
                        var11 = 1;
                    }

                    if (var3 >= var11) {
                        if (this.getItemTemplate().levelNeed < 20) {
                            return (int) (1.0F * var5);
                        }

                        if (this.getItemTemplate().levelNeed < 30) {
                            return (int) (3.0F * var5);
                        }

                        return (int) ((float) (var10 + var8 + var3) * var5);
                    }
                }
            }

            return 0;
        }
    }


    public void write(Writer var1) throws IOException {
        var1.writeShort(id);
        if (id >= 0) {
            var1.writeBoolean(isLock);
            var1.writeLong(expiry);
            if (this.isTypeTrangBi()) {
                var1.writeByte(this.he);
                var1.writeByte(this.level);
                var1.writeUTF(strOptions);
            } else {
                var1.writeInt(this.getAmount());
            }
            if (this.getItemTemplate().type == 99) {
                var1.writeUTF(strOptions);
            }
            var1.writeShort(index);
        }
    }

    @JsonIgnore
    public ItemOption[] getItemOption() {
        if (strOptions != null && strOptions.length() > 0&&!strOptions.isEmpty()) {
            String[] var1;
            ItemOption[] itemOption = new ItemOption[(var1 = Utlis.split(strOptions, ";")).length];
            for (int var3 = 0; var3 < var1.length; ++var3) {
                try {
                    itemOption[var3] = new ItemOption(var1[var3]);
                } catch (Exception ex) {
                }
            }
            return itemOption;
        } else {
            return null;
        }
    }

    public static ItemOption[] getItemOptionFormStrOptions(String strOptions) {
        if (strOptions != null && strOptions.length() > 0) {
            String[] var1;
            ItemOption[] itemOption = new ItemOption[(var1 = Utlis.split(strOptions, ";")).length];

            for (int var3 = 0; var3 < var1.length; ++var3) {
                try {
                    itemOption[var3] = new ItemOption(var1[var3]);
                } catch (Exception ex) {

                }
            }

            return itemOption;
        } else {
            return new ItemOption[0];
        }
    }

    public void addItemOption(ItemOption itemOption) {
        if (itemOption == null) {
            return;
        }
        ItemOption[] dataOld = getItemOptionFormStrOptions(strOptions);
        ItemOption[] data = new ItemOption[dataOld.length + 1];
        for (int i = 0; i < dataOld.length; i++) {
            data[i] = dataOld[i];
        }
        data[data.length - 1] = itemOption;
        strOptions = getStrOptionsFormItemOption(data);
    }

    public static String getStrOptionsFormItemOption(ItemOption[] itemOption) {
        String var1 = "";
        if (itemOption != null) {
            for (int var2 = 0; var2 < itemOption.length; ++var2) {
                if (itemOption[var2] != null) {
                    var1 = var1 + itemOption[var2].g();
                    if (var2 < itemOption.length - 1) {
                        var1 = var1 + ";";
                    }
                }

            }
        }

        return var1;
    }

    public boolean checkDulicateOp() {
        int id = -1;
        if (!strOptions.isEmpty()) {
            String[] op = strOptions.split(";");
            for (String var : op) {
                String[] option = var.split(",");
                int idoption = Integer.parseInt(option[0]);
                if (idoption == id) {
                    return true;
                }
                id = idoption;
            }

        }
        return false;
    }


    public void removeItemOption(ItemOption itemoption) {
        if (itemoption == null) {
            return;
        }
        ItemOption[] items = getItemOption();
        for (int i = 0; i < items.length; i++) {
            try {
                if (items[i].a[0] == itemoption.a[0]) {
                    items[i] = null;
                }
            } catch (Exception x) {
            }
        }
        String var1 = "";
        if (items != null) {
            for (int var2 = 0; var2 < items.length; ++var2) {
                if (items[var2] != null) {
                    var1 = var1 + items[var2].g();
                    if (var2 < items.length - 1) {
                        var1 = var1 + ";";
                    }
                }
            }
        }
        strOptions = var1;

    }

    public void createItemOptions() {
        if (strOptionsMain == "") {
            strOptionsMain = this.getStrOptionsFormItemOption(this.getItemOption());
        }
        //  Log.debug(strOptionsMain);
        ItemOption[] array = getItemOptionFormStrOptions(this.strOptionsMain);
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] != null) {
                    try {
                        if (array[i].canCreateItemOption() && array[i].getId() != 128) {
                            int[] arrayChiSo = new int[2];
                            arrayChiSo[0] = array[i].a[0];
                            arrayChiSo[1] = Utlis.nextInt(array[i].a[1], array[i].f());
                            array[i].a = arrayChiSo;
                        }
                    } catch (Exception ex) {

                    }
                }
            }
        }
//        if (this.level > 0) {
//            for (int index = 0; index < this.level; index++) {
//                if (array != null) {
//                    for (int i = 0; i < array.length; i++) {
//                        if (array[i] != null) {
//                            try {
//                                array[i].a[1] += getChiSoMaxFormStrOptionMain(array[i].a[0]);
//                            } catch (Exception ex) {
//
//                            }
//                        }
//                    }
//                }
//            }
//        }
        this.strOptions = getStrOptionsFormItemOption(array);
//        int lv = level;
//        this.level = 0;
//        this.a(lv);
    }

    public boolean isSucManh() {
        ItemOption[] var1;
        if ((var1 = this.getItemOption()) != null) {
            for (int var2 = 0; var2 < var1.length; ++var2) {
                if (var1[var2].sucmanh()) {
                    return true;
                }
            }
        }

        return false;
    }
    public int checkSucManh() {
        ItemOption[] var1;
        if ((var1 = this.getItemOption()) != null) {
            for (int var2 = 0; var2 < var1.length; ++var2) {
                if (var1[var2].sucmanh()) {
                    return var1[var2].getvalue();
                }
            }
        }

        return 0;
    }

    public void a(int var1) {
        ItemOption[] var2;
        if ((var2 = this.getItemOption()) != null) {
            int var3;
            int var4;
            int[] var5;
            if (var1 >= this.level) {
                for (var3 = this.level + 1; var3 <= var1; ++var3) {
                    for (var4 = 0; var4 < var2.length; ++var4) {
                        if (var2[var4].getItemOptionTemplate().type != 8) {
                            var5 = var2[var4].getItemOptionTemplate().a();
                            if (var3 <= var5.length) {
                                var2[var4].d(var5[var3 - 1]);
                            }
                        }
                    }
                }
            } else {
                for (var3 = this.level; var3 > var1; --var3) {
                    for (var4 = 0; var4 < var2.length; ++var4) {
                        if (var2[var4].getItemOptionTemplate().type != 8) {
                            var5 = var2[var4].getItemOptionTemplate().a();
                            if (var3 <= var5.length) {
                                var2[var4].d(-var5[var3 - 1]);
                            }
                        }
                    }
                }
            }
        }

        this.level = (byte) var1;
        this.strOptions = getStrOptionsFormItemOption(var2);
    }

    public int getChiSoMaxFormStrOptionMain(int id) {
        ItemOption[] array = getItemOptionFormStrOptions(this.strOptionsMain);
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] != null) {
                    try {
                        if (array[i].canCreateItemOption() && array[i].a[0] == id) {
                            return array[i].f();
                        }
                    } catch (Exception ex) {

                    }
                }
            }
        }
        return 0;
    }

    public int getChiSo(Char pl, int... arrChiSo) {
        int c = 0;
        ItemOption[] array = getItemOption();
        if (arrChiSo == null) {
            for (int i = 0; array != null && i < array.length; i++) {
                if (array[i] != null) {
                    try {
                        if (this.checkItemCanAddItem(pl, array[i])) {
                            c += array[i].a[1];
                        }
                    } catch (Exception ex) {

                    }
                }
            }
        } else {
            for (int j = 0; arrChiSo != null && j < arrChiSo.length; j++) {
                if (array != null) {
                    for (int i = 0; i < array.length; i++) {
                        if (array[i] != null) {
                            try {
                                if (array[i].a[0] == arrChiSo[j]) {
                                    if (this.checkItemCanAddItem(pl, array[i])) {
                                        c += array[i].a[1];
                                    }
                                }
                            } catch (Exception ex) {

                            }
                        }
                    }
                }
            }
        }
        return c;
    }

    @JsonIgnore
    public int getChiSoDame() {
        int dame = 0;
        ItemOption[] array = getItemOption();
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] != null) {
                    try {
                        if (array[i].a[0] == 2) {
                            dame += array[i].a[1];
                        }
                    } catch (Exception ex) {

                    }
                }
            }
        }
        return dame;
    }

    public int getChiSoDameMob() {
        int dame = 0;
        ItemOption[] array = getItemOption();
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] != null) {
                    try {
                        if (array[i].a[0] == 3) {
                            dame += array[i].a[1];
                        }
                    } catch (Exception ex) {

                    }
                }
            }
        }
        return dame;
    }

    @JsonIgnore
    public boolean isDaCuongHoa() {
        return this.getItemTemplate().type == 21;
    }

    @JsonIgnore
    public boolean isSet2() {
        return this.getItemTemplate().type == 2 || this.getItemTemplate().type == 8 || this.getItemTemplate().type == 7;
    }

    @JsonIgnore
    public boolean isSet3() {
        return this.getItemTemplate().type == 6 || this.getItemTemplate().type == 5 || this.getItemTemplate().type == 9;
    }

    @JsonIgnore
    public boolean isSet1() {
        return this.getItemTemplate().type == 0 || this.getItemTemplate().type == 4 || this.getItemTemplate().type == 3;
    }
    @JsonIgnore
    public boolean checkKichAn(Char pl) {
        if (isSet1()) {
            if (pl.Bag.arrItemBody[0] != null && pl.Bag.arrItemBody[4] != null && pl.Bag.arrItemBody[3] != null) {
                int he1 = pl.Bag.arrItemBody[0].he;
                int he2 = pl.Bag.arrItemBody[3].he;
                int he3 = pl.Bag.arrItemBody[4].he;
                return he1 == he2 && he2 == he3;
            }
        }
        if (isSet2()) {
            if (pl.Bag.arrItemBody[2] != null && pl.Bag.arrItemBody[8] != null && pl.Bag.arrItemBody[7] != null) {
                int he1 = pl.Bag.arrItemBody[2].he;
                int he2 = pl.Bag.arrItemBody[8].he;
                int he3 = pl.Bag.arrItemBody[7].he;
                return he1 == he2 && he2 == he3;
            }
        }
        if (isSet3()) {
            if (pl.Bag.arrItemBody[6] != null && pl.Bag.arrItemBody[5] != null && pl.Bag.arrItemBody[9] != null) {
                int he1 = pl.Bag.arrItemBody[6].he;
                int he2 = pl.Bag.arrItemBody[5].he;
                int he3 = pl.Bag.arrItemBody[9].he;
                return he1 == he2 && he2 == he3;
            }
        }
        if (this.getItemTemplate().type == 1) {
            if (pl.Bag.arrItemBody[1] != null) {
                return pl.Info.idhe == pl.Bag.arrItemBody[1].he || pl.Bag.arrItemBody[15] != null && pl.Bag.arrItemBody[15].he == pl.Bag.arrItemBody[1].he;

            }
        }
        return false;
    }
    @JsonIgnore
    public boolean checkItemCanAddItem(Char pl, ItemOptionTemplate itemOption) {
        switch (itemOption.type) {
            case 3:
                return this.level >= 4;
            case 4:
                return this.level >= 8;
            case 5:
                return this.level >= 12;
            case 6:
                return this.level >= 14;
            case 7:
                return this.level >= 16;
            case 10:
                return this.level >= 17;
            case 11:
                return this.level >= 18;
            case 19:
                return this.level >= 16;
            case 2:
                return checkKichAn(pl);
        }
        return true;
    }
    @JsonIgnore
    public boolean checkItemCanAddItem(Char pl, ItemOption itemOption) {
        switch (itemOption.getItemOptionTemplate().type) {
            case 3:
                return this.level >= 4;
            case 4:
                return this.level >= 8;
            case 5:
                return this.level >= 12;
            case 6:
                return this.level >= 14;
            case 7:
                return this.level >= 16;
            case 10:
                return this.level >= 17;
            case 11:
                return this.level >= 18;
            case 19:
                return this.level >= 16;
            case 2:
                return checkKichAn(pl);
        }
        return true;
    }
    @JsonIgnore
    public boolean V() {
        ItemOption[] var1;
        if ((var1 = this.L()) != null) {
            for (int var2 = 0; var2 < var1.length; ++var2) {
                if (var1[var2].a[0] == 148 || var1[var2].j()) {
                    return true;
                }
            }
        }

        return false;
    }
    @JsonIgnore
    public ItemOption a(Vector var1, int var2) {
        var2 = ItemOption.f(var2);
        ItemOption[] var3 = this.L();
        ItemOption var4 = null;
        if (var3 != null) {
            for (int var5 = 0; var5 < var3.length; ++var5) {
                if (var3[var5].getId() == -1) {
                    return null;
                }
                if (var3[var5].getItemOptionTemplate().type == 8) {
                    var1.add(var3[var5]);
                    if (var3[var5].a[0] == var2) {
                        var4 = var3[var5];
                    }
                }
            }
        }

        return var4;
    }
    @JsonIgnore
    public int[] ab(int var1, int var2) {
        ItemOption[] var3;
        if ((var3 = this.L()) == null) {
            return null;
        } else {
            int var4 = ItemOption.f(var2);
            int[] var5 = null;
            int var6 = this.Y();

            for (int var7 = 0; var7 < var3.length; ++var7) {
                if (var3[var7].getId() == -1) {
                    return null;
                }
                if (var3[var7].getItemOptionTemplate().type == 8 && var3[var7].a[0] == var4) {
                    int var8 = var3[var7].i();
                    (var5 = new int[4])[0] = var2;
                    var5[1] = var8;
                    var5[2] = var8;
                    int[] var9 = var3[var7].getItemOptionTemplate().a();

                    for (int var10 = var8 + 1; var10 <= var9.length; ++var10) {
                        if (var1 >= DataCenter.gI().ngocKhamUpgrade[var10] && var10 <= var6) {
                            var5[1] = var8;
                            var5[2] = var10;
                            var5[3] += DataCenter.gI().ngocKhamUpgrade[var10];
                            var1 -= DataCenter.gI().ngocKhamUpgrade[var10];
                            var3[var7].d(var9[var10 - 1]);
                            var3[var7].a[3] = var10;
                        }
                    }
                }
            }

            this.strOptions = a(var3);
            return var5;
        }
    }
    @JsonIgnore
    public int Y() {
        ItemOption[] var1;
        if ((var1 = this.L()) != null) {
            for (int var2 = 0; var2 < var1.length; ++var2) {
                if (var1[var2].k()) {
                    return 18;
                }

                if (var1[var2].j()) {
                    return 17;
                }
            }
        }

        return 16;
    }
    @JsonIgnore
    public void createOptionTT(){
        List<String> indices = new ArrayList<>();
        indices.add("0,200,500");
        indices.add("1,200,500");
        indices.add("2,50,200");
        indices.add("3,70,250");
        indices.add("5,20,100");
        indices.add("108,100,200");
        indices.add("109,100,200");
        indices.add("110,100,200");
        indices.add("111,100,200");
        indices.add("112,100,200");
        indices.add("121,50,70");
        indices.add("209,20,50");
        indices.add("122,20,50");
        indices.add("306,20,80");

        // Trộn danh sách.
        Collections.shuffle(indices);

        // Tạo một đối tượng Random để tạo số lượng chỉ số ngẫu nhiên cần lấy.
        Random random = new Random();

        // Random số lượng chỉ số cần lấy từ 3 đến 6.
        int numberOfIndicesToGet = random.nextInt(4) + 3; // +3 vì phạm vi là từ 3 đến 6.

        // Tạo một danh sách mới để lưu trữ các chỉ số đã chọn.
        List<String> selectedIndices = new ArrayList<>();

        // Lấy số lượng chỉ số đã xác định từ danh sách đã trộn.
        for (int i = 0; i < numberOfIndicesToGet; i++) {
            selectedIndices.add(indices.get(i));
        }
        String result = String.join(";", selectedIndices);
        this.strOptions = result;
    }
    public void createOptionTanTo() {
        this.level = 16;
        this.strOptions = "122,10,30;151,50,200;152,50,200;117,100,300;110,50,150;158,1,3;2,50,200;167,50,150;126,3,8";
    }

    public void createOptionCaiTrang() {
        this.strOptions = "68,25;70,25;0,300;2,75;4,75;5,25";
    }

    @JsonIgnore
    public boolean has() {
        return has(1);
    }
    @JsonIgnore
    public boolean has(int amount) {
        return this.amount >= amount;
    }
    @JsonIgnore
    public boolean hasExpire() {
        return !isForever();
    }
    @JsonIgnore
    public boolean isForever() {
        return this.expiry == -1;
    }
    @JsonIgnore
    public void add(int amount) {
        this.amount += amount;
    }

    public void reduce(int amount) {
        this.amount -= amount;
    }
    @JsonIgnore
    public boolean addExp(int exp) {
        if(exp< 0 )
            return false;
        ItemOption[] options = this.getItemOption();
        Vector listops = new Vector<>();
        ItemOption tuluyen = null;
        for (ItemOption ops : options) {
            if (ops.getId() == 283) {
                tuluyen = ops;
            }
            listops.add(ops);
        }
        if (tuluyen == null) {
            return false;
        }
        int value = tuluyen.getvalue();
        if (value == 200000000) {
            return false;
        }
        int valueadd = value + exp;
        if (valueadd >= 200000000) {
            valueadd = 200000000;
        }
        tuluyen.c(valueadd);
        this.strOptions = Item.a(listops);
        return true;
    }
    @JsonIgnore
    public int getValueHu(){
        ItemOption[] options = this.getItemOption();
        for (ItemOption ops : options) {
            if (ops.getId() == 283) {
                return ops.getvalue();
            }
        }
        return 0;
    }


    public static class LangLa_gp {

        public String a;
        public int b;
        public int c;

        @Override
        public String toString() {
            return a;
        }

        public LangLa_gp(String var1, int var2, int var3) {
            this.a = var1;
            this.b = var2;
            this.c = var3;
        }
    }

    private static String a(int var0, int var1) {
        try {
            switch (var0) {
                case 0:
                    return Caption.rq[3] + " (" + Caption.ro[var1 - 1] + "), " + Caption.rq[4] + " (" + Caption.ro[var1 - 1] + ")";
                case 1:
                    return Caption.aq + " (" + Caption.ro[var1 - 1] + ") " + Caption.aw + " (" + Caption.ro[var1 - 1] + ")";
                case 2:
                    return Caption.rq[7] + " (" + Caption.ro[var1 - 1] + "), " + Caption.rq[8] + " (" + Caption.ro[var1 - 1] + ")";
                case 3:
                    return Caption.rq[0] + " (" + Caption.ro[var1 - 1] + "), " + Caption.rq[4] + " (" + Caption.ro[var1 - 1] + ")";
                case 4:
                    return Caption.rq[0] + " (" + Caption.ro[var1 - 1] + "), " + Caption.rq[3] + " (" + Caption.ro[var1 - 1] + ")";
                case 5:
                    return Caption.rq[6] + " (" + Caption.ro[var1 - 1] + "), " + Caption.rq[9] + " (" + Caption.ro[var1 - 1] + ")";
                case 6:
                    return Caption.rq[5] + " (" + Caption.ro[var1 - 1] + "), " + Caption.rq[9] + " (" + Caption.ro[var1 - 1] + ")";
                case 7:
                    return Caption.rq[2] + " (" + Caption.ro[var1 - 1] + "), " + Caption.rq[8] + " (" + Caption.ro[var1 - 1] + ")";
                case 8:
                    return Caption.rq[2] + " (" + Caption.ro[var1 - 1] + "), " + Caption.rq[7] + " (" + Caption.ro[var1 - 1] + ")";
                case 9:
                    return Caption.rq[6] + " (" + Caption.ro[var1 - 1] + "), " + Caption.rq[5] + " (" + Caption.ro[var1 - 1] + ")";
            }
        } catch (Exception var2) {
            Utlis.println(var2);
        }

        return "";
    }

    public static Vector getTextVec(Item var5) {
        Vector h = new Vector();
        ItemOption[] var45 = getItemOptionFormStrOptions(var5.strOptions);
        int var1 = 1;
        if (var45 != null) {
            int var3 = 0;

            boolean var21 = false;
            boolean var6 = false;

            for (int var7 = 0; var7 < var45.length; ++var7) {
                if ((var45[var7].getItemOptionTemplate().type < 3 || var45[var7].getItemOptionTemplate().type > 7) && var45[var7].getItemOptionTemplate().type != 10 && var45[var7].getItemOptionTemplate().type != 11 && var45[var7].getItemOptionTemplate().type != 15 && var45[var7].getItemOptionTemplate().type != 16) {
                    if (var45[var7].getItemOptionTemplate().type == 2) {
                        if (!var6) {
                            var6 = true;
                            h.addElement(new LangLa_gp(Caption.fM + a(var5.getItemTemplate().type, var5.he), -1, -11184811));
                        }
                        if (var3 > 0) {
                            h.addElement(new LangLa_gp(var45[var7].b(), -7812062, -16777216));
                            --var3;
                        } else {
                            h.addElement(new LangLa_gp(var45[var7].b(), -7631732, -16777216));
                        }
                    } else if (var45[var7].getItemOptionTemplate().type == 8) {
                        h.addElement(new LangLa_gp("(" + DataCenter.gI().ItemTemplate[var45[var7].h()].name + " " + Caption.dn + " " + var45[var7].i() + ") " + var45[var7].b(), -7340813, -16777216));
                    } else if (var45[var7].getItemOptionTemplate().type == 14) {
                        h.addElement(new LangLa_gp(var1 + ". " + var45[var7].b(), -16742145, -16777216));
                        ++var1;
                    } else if (var45[var7].getItemOptionTemplate().id >= 53 && var45[var7].getItemOptionTemplate().id <= 62) {
                        h.addElement(new LangLa_gp(var45[var7].c(), -10831436, -16777216));
                    } else if (var45[var7].getItemOptionTemplate().id != 128 && var45[var7].getItemOptionTemplate().id != 305) {
                        if (var45[var7].getItemOptionTemplate().id == 336) {
                            h.addElement(new LangLa_gp(var45[var7].c(), -623877, -16777216));
                        } else if (var45[var7].getItemOptionTemplate().id == 337) {
                            h.addElement(new LangLa_gp(var45[var7].b() + Utlis.numberFormat((var45[var7 - 1].getvalue() + 1) * 5000000), -2560, -16777216));
                        } else if (var45[var7].getId() == 105) {
                            Vector var10000 = h;
                            StringBuilder var10003 = (new StringBuilder()).append(Caption.fN).append(LangLa_hg.b());
                            int[] var10005 = var45[var7].a;
                            var10000.addElement(new LangLa_gp(var10003.append(DataCenter.gI().MapTemplate[var10005[1]].name).toString(), -1, -16777216));
                            h.addElement(new LangLa_gp(Caption.fO, -1, -16777216));
                        } else if (var45[var7].getId() == 148) {
                            h.addElement(new LangLa_gp(var45[var7].b(), -3407617, -16777216));
                        } else if (var45[var7].getId() == 159) {
                            h.addElement(new LangLa_gp(var45[var7].b(), -196483, -16777216));
                        } else if (var45[var7].getId() == 163) {
                            h.addElement(new LangLa_gp(var45[var7].b(), -48128, -16777216));
                        } else if (var45[var7].getId() != 164 && var45[var7].getItemOptionTemplate().id != 340) {
                            if (var45[var7].getId() == 165) {
                                h.addElement(new LangLa_gp(var45[var7].b(), -16712186, -16777216));
                            } else if (var45[var7].getId() == 361) {
                                h.addElement(new LangLa_gp(var45[var7].b(), -13176412, -16777216));
                            } else {
                                h.addElement(new LangLa_gp(var45[var7].b(), -10831436, -16777216));
                            }
                        } else {
                            h.addElement(new LangLa_gp(var45[var7].b(), -4588032, -16777216));
                        }
                    } else {
                        h.addElement(new LangLa_gp(var45[var7].c(), -2560, -16777216));
                    }
                } else {
                    if (!var21) {
                        var21 = true;
                        if (var5.getItemTemplate().type == 11) {
                            h.addElement(new LangLa_gp(Caption.fK, -1, -11184811));
                        } else {
                            h.addElement(new LangLa_gp(Caption.fL, -1, -11184811));
                        }
                    }

                    if ((var45[var7].getItemOptionTemplate().type != 15 || var5.level < 2) && (var45[var7].getItemOptionTemplate().type != 3 || var5.level < 4) && (var45[var7].getItemOptionTemplate().type != 4 || var5.level < 8) && (var45[var7].getItemOptionTemplate().type != 5 || var5.level < 12) && (var45[var7].getItemOptionTemplate().type != 6 || var5.level < 14) && (var45[var7].getItemOptionTemplate().type != 7 || var5.level < 16) && (var45[var7].getItemOptionTemplate().type != 10 || var5.level < 17) && (var45[var7].getItemOptionTemplate().type != 11 || var5.level < 18) && (var45[var7].getItemOptionTemplate().type != 16 || var5.level < 19)) {
                        h.addElement(new LangLa_gp(var45[var7].b(), -7631732, -16777216));
                    } else {
                        h.addElement(new LangLa_gp(var45[var7].b(), -2560, -16777216));
                    }
                }
            }
        }
        return h;
    }

}
