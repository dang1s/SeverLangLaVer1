package com.sg188.real;

import InfoChar.InfoChar;
import InfoChar.InfoEff;
import InfoChar.InfoGame;
import InfoChar.InfoGiftCode;
import InfoChar.InfoInventory;
import InfoChar.InfoPoint;
import InfoChar.InfoSkill;
import InfoChar.InfoPhucLoi;
import com.sg188.clan.Clan;
import com.sg188.data.DataCenter;
import com.sg188.data.ItemOption;
import com.sg188.data.Skill;
import com.sg188.data.SkillClan;
import com.sg188.lib.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Body {
    public InfoChar Info;
    public InfoSkill Skill;
    public InfoInventory Bag;
    public InfoPoint Point;
    public InfoGame InfoGame;


    public InfoEff Effs;
    public InfoGiftCode GiftCode;
    public InfoPhucLoi phucLoi;
    public Vector<SkillClan> listSkill = new Vector(7);
    public Lock lockViThu = new ReentrantLock();
    public Clan clan;
    public List<Skill> supportSkill = new ArrayList<>();
    public int maxHP;
    public int maxMP;
    public int damage, damage2, basicAttack;
    public int attackMonsters;        // Tấn công quái
    public int exactly;               // Chính xác (formerly exactly)
    public int ignoreMiss;           // Bỏ qua né tránh
    public int critical;              // Chí mạng
    public int criticalAttack;        // Tấn công khi đánh chí mạng
    public int lightningAttackBoost;  // Tăng tấn công lên hệ Lôi
    public int earthAttackBoost;      // Tăng tấn công lên hệ Thổ
    public int waterAttackBoost;      // Tăng tấn công lên hệ Thủy
    public int fireAttackBoost;       // Tăng tấn công lên hệ Hỏa
    public int windAttackBoost;       // Tăng tấn công lên hệ Phong
    public int weaken;                // Gây suy yếu
    public int poison;                // Gây trúng độc
    public int slow;                  // Gây làm chậm
    public int burn;                  // Gây bỏng
    public int stun;                  // Gây choáng
    public int ignoreResistance;      // Bỏ qua kháng tính

    // Defense attributes
    public int lightningResistance;   // Kháng Lôi
    public int earthResistance;       // Kháng Thổ
    public int waterResistance;       // Kháng Thủy
    public int fireResistance;        // Kháng Hỏa
    public int windResistance;        // Kháng Phong
    public int damageReduction;       // Giảm sát thương
    public int movementSpeed;         // Tốc độ di chuyển

    public int miss;                  // Né tránh (formerly dodge)
    public int counterAttack;         // Phản đòn
    public int criticalDefense;       // Phòng chí mạng
    public int elementalCounter;      // Tương khắc lên hệ
    public int elementalCounterReduce;// Giảm tương khắc của hệ
    public short reduceWeaken;          // Giảm gây suy yếu
    public short reducePoison;          // Giảm gây trúng độc
    public short reduceSlow;            // Giảm gây làm chậm
    public short reduceBurn;            // Giảm gây bỏng
    public short reduceStun;            // Giảm gây choáng
    public int reduceCriticalDamage;  // Giảm trừ chí mạng
    public short chakra;
    public short allResistance;
    public int[] options;
    public boolean[] haveOptions;
    public int mpHutDame;
    public short boostHPMP;
    public short boostResistAll;
    public short boostDame;
    public short reducedResist;
    public short reducedNeftDame;
    public int buffSpeed;
    public int buffDame;
    public int buffCx;
    public short isSusanoItatchi;
    public int isHienNhan;
    public int boostChakra;
    public int reducedChakra;
    public int buffHP;
    public int buffHP_2;
    public short thuyLaoThuat;

    public int getChiSoFormSkill(int... array) {
        int c = 0;
        for (int i = 0; i < Skill.arraySkill.length; i++) {
            if (Skill.arraySkill[i] != null && Skill.arraySkill[i].getSkillTemplate().type >= 5) {
                c += Skill.arraySkill[i].getChiSo((Char) this, array);
            }
        }

        return c;
    }

    public int getParamSkill(int id, int option) {
        int c = 0;
        for (int i = 0; i < Skill.arraySkill.length; i++) {
            if (Skill.arraySkill[i] != null && Skill.arraySkill[i].idTemplate == id) {
                c += Skill.arraySkill[i].getChiSo((Char) this, option);
            }
        }

        return c;
    }


    public void updateAllChiSo() {
        int length = DataCenter.gI().ItemOptionTemplate.length;
        options = new int[length];
        haveOptions = new boolean[length];
        for (Item itemBody : this.Bag.arrItemBody) {
            if (itemBody != null && itemBody.strOptions.length() > 0 && !itemBody.strOptions.isEmpty()) {
                for (ItemOption option : itemBody.getItemOption()) {
                    int optionID = option.getId();
                    if (option.getItemOptionTemplate().type >= 3 && option.getItemOptionTemplate().type <= 7) {
                        if ((option.getItemOptionTemplate().type == 3 && itemBody.level >= 4) ||
                                (option.getItemOptionTemplate().type == 4 && itemBody.level >= 8) ||
                                (option.getItemOptionTemplate().type == 5 && itemBody.level >= 12) ||
                                (option.getItemOptionTemplate().type == 6 && itemBody.level >= 14) ||
                                (option.getItemOptionTemplate().type == 7 && itemBody.level >= 16)
                        ) {
                            options[optionID] += option.getvalue();
                            haveOptions[optionID] = true;
                        }
                    } else if (option.getItemOptionTemplate().type == 10 && itemBody.level > 17) {
                        options[optionID] += option.getvalue();
                        haveOptions[optionID] = true;
                    } else if (option.getItemOptionTemplate().type == 15 && itemBody.level > 18) {
                        options[optionID] += option.getvalue();
                        haveOptions[optionID] = true;
                    } else if (option.getItemOptionTemplate().type == 16 && itemBody.level > 19) {
                        options[optionID] += option.getvalue();
                        haveOptions[optionID] = true;
                    } else {
                        options[optionID] += option.getvalue();
                        haveOptions[optionID] = true;
                    }
                }
            }
        }
        for (Item itemBody : this.Bag.arrItemBody2) {
            if (itemBody != null && (itemBody.id >= 908 && itemBody.id <= 911) && itemBody.strOptions.length() > 0) {
                for (ItemOption option : itemBody.getItemOption()) {
                    int optionID = option.getId();
                    options[optionID] += option.getvalue();
                    haveOptions[optionID] = true;
                }
            }
        }
        if (Bag.itemSach != null) {
            for (ItemOption option : Bag.itemSach.getItemOption()) {
                int optionID = option.getId();
                options[optionID] += option.getvalue();
                haveOptions[optionID] = true;
            }
        }
        try {
            for (com.sg188.data.Skill skill : Skill.arraySkill) {
                if (skill.getSkillTemplate().type == 5) {
                    if (skill != null) {
                        for (ItemOption option : skill.getItemOption()) {
                            int optionID = option.getId();
                            options[optionID] += option.getvalue();
                            haveOptions[optionID] = true;
                        }
                    }
                }
            }
        } catch (Exception e) {

        }
        if (this.listSkill.size() > 0 && Bag.arrItemBody[10] != null) {
            synchronized (listSkill) {
                for (SkillClan skillClan : listSkill) {
                    if (skillClan != null) {
                        ItemOption[] itemOptions = skillClan.getOption();
                        if (itemOptions.length > 0) {
                            for (ItemOption option : itemOptions) {
                                int optionID = option.getId();
                                options[optionID] += option.getvalue();
                                haveOptions[optionID] = true;
                            }
                        }
                    }
                }
            }
        }
        if(clan !=null){
            if(clan.skillClans.size() > 0){
                for (SkillClan skillClan: clan.skillClans) {
                    ItemOption[] itemOptions = skillClan.getOption();
                    if (itemOptions.length > 0) {
                        for (ItemOption option : itemOptions) {
                            int optionID = option.getId();
                            options[optionID] += option.getvalue();
                            haveOptions[optionID] = true;
                        }
                    }
                }
            }
        }
        int basicHp = 0;
        int basicMp = 0;
        int potentialDame = 0;
        chakra = (short) (Point.arrayTiemNang[1]+options[209]+options[255]+boostChakra+isSusanoItatchi-reducedChakra);
        exactly = options[20]+options[65]+options[167]+options[180]+options[205]+options[280]+options[304]+buffCx;
        miss = options[14]+options[64]+options[151]+options[161]+options[204]+options[324]+chakra;
        critical = options[5]+options[15]+options[28]+options[63]+options[144]+options[166]+options[203]+options[362]+chakra;
        potentialDame = (int) (Point.arrayTiemNang[0]*1.8+chakra*0.8+Point.arrayTiemNang[2]*1.4);
        int amplifyBasicAttack = options[34]+options[47]+options[122];
        damage = potentialDame+options[2]+options[31]+(potentialDame*amplifyBasicAttack/100)+options[78]+options[146]+options[184]+options[199]+options[208];
        if(critical > 3000){
            damage+= (critical-3000)*2;
        }
        damage+= damage*(options[361]+options[254])/100;
        damage+=boostDame;
        damage+=buffDame;
        damage+=thuyLaoThuat;
        damage-=reducedNeftDame;
        attackMonsters = options[3]+options[89]+options[208];
        damage2 = damage- damage/100;
        basicHp = chakra*2+Point.arrayTiemNang[3]*9;
        basicMp = chakra*2+Point.arrayTiemNang[2]*8;
        int basicHPMAX = options[18]+options[29];
        basicHPMAX+= basicHPMAX*(options[32]+options[119])/100;
        maxHP = basicHp + options[0]+options[106]+options[192]+options[202]+options[253]+basicHPMAX+options[175];
        maxHP+= maxHP *(options[79])/100;
        maxHP+=boostHPMP;
        maxHP+=buffHP;
        maxHP+=buffHP_2;
        maxHP+=maxHP*isSusanoItatchi/100;
        int basicMPMAX = options[19]+options[30];
        basicMPMAX = basicMPMAX*(options[33]+options[120])/100;
        maxMP = basicMp + basicMPMAX+options[1]+options[107];
        maxMP += maxMP *(options[80])/100;
        maxMP+=boostHPMP;
        damageReduction = options[13]+options[173]+options[206];
        counterAttack = options[16];
        counterAttack+= counterAttack*(options[67]+options[162]+options[371]+options[373]);
        criticalAttack = options[41]+options[95]+options[306]+options[309];
        reduceCriticalDamage = options[344]+options[346]+options[348];
        criticalDefense = options[42]+options[43]+options[44]+options[45]+options[46]+options[174];
        ignoreMiss = options[4]+options[147]+options[160];
        allResistance = (short) (options[12]+options[40]+options[81]+options[121]+options[152]+options[201]+options[258]+boostResistAll-reducedResist);
        lightningResistance = allResistance+options[7]+options[35]+options[82]+options[108];
        earthResistance = allResistance+options[8]+options[36]+options[83]+options[109];
        waterResistance = allResistance+options[9]+options[37]+options[84]+options[110];
        fireResistance= allResistance+options[10]+options[38]+options[85]+options[111];
        windResistance= allResistance+options[11]+options[39]+options[86]+options[112];
        lightningAttackBoost = options[21]+options[113]+options[153];
        earthAttackBoost = options[22]+options[114]+options[154];
        waterAttackBoost = options[23]+options[115]+options[155];
        fireAttackBoost = options[24]+options[116]+options[156];
        windAttackBoost = options[25]+options[117]+options[157];
        weaken = options[48]+options[68]+options[123]+options[168]+options[185]+options[259];
        poison = options[49]+options[69]+options[124]+options[169]+options[186]+options[260];
        slow = options[50]+options[70]+options[125]+options[170]+options[187]+options[261];
        burn = options[51]+options[71]+options[126]+options[171]+options[189]+options[262];
        stun= options[52]+options[72]+options[127]+options[172]+options[263];
        reduceWeaken = (short) (options[289]+options[325]+options[355]);
        reducePoison= (short) (options[290]+options[326]+options[356]);
        reduceSlow= (short) (options[291]+options[327]+options[357]);
        reduceBurn= (short) (options[292]+options[328]+options[358]);
        reduceStun= (short) (options[293]+options[329]+options[359]);
        ignoreResistance = options[145]+options[149]+options[197]+options[360];
        elementalCounter = options[307]+options[310]+options[372];
        elementalCounterReduce = options[311]+options[323]+options[330]+options[331]+options[345];
        movementSpeed = options[91]+options[118]+options[150]+options[17]+500+buffSpeed+isSusanoItatchi;

        //vô cực
        chakra = (short) ((double) options[380]/100 * chakra + chakra);
        //thiên đạo
        maxHP = (int) ((double) options[379]/100 * maxHP + maxHP);

        switch (Info.idhe){
            case 1:
                elementalCounter+=options[54];
                elementalCounterReduce+=options[62];
                break;
            case 2:
                elementalCounter+=options[55];
                elementalCounterReduce+=options[58];
                break;
            case 3:
                elementalCounter+=options[56];
                elementalCounterReduce+=options[59];
                break;
            case 4:
                elementalCounter+=options[57];
                elementalCounterReduce+=options[60];
                break;
            case 5:
                elementalCounter+=options[53];
                elementalCounterReduce+=options[61];
                break;
        }
    }

}