package com.sg188.data;

import com.sg188.lib.Log;
import com.sg188.lib.Utlis;
import com.sg188.lib.Binary;
import com.sg188.lib.mArrays;
import com.sg188.server.Main;
import com.sg188.server.lib.Message;
import com.sg188.server.lib.Reader;
import com.sg188.server.lib.Writer;

import java.io.*;

import com.tgame.model.Caption;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Hashtable;

import org.json.JSONArray;
public class DataCenter {
    
    static {
        DataCenter.bg = null;
        DataCenter.bh = new Object();
        DataCenter.be = new ArrayList<>();
        DataCenter.gI().readArrDataGame(true);
    }
    public String ipServer;
    public int portServer;
    public String c;
    public int d;
    public int e;
    public boolean f;
    public boolean INPUT_CAPTCHA;
    public int h;
    public int i;
    public int j;
    public int k;
    public int l;
    public int m;
    public int n;
    public int widthScreen;
    public int heightScreen;
    public int q;
    public int r;
    public int zoomLevelScreen;
    public int typeArr;
    public int zoomLevel;
    public String v;
    public int typeOS;
    public int VER1;
    public int VER2;
    private boolean bf;
    public float z;
    public int A;
    public int B;
    public int C;
    public int D;
    public LangLa_gv[] E;
    public LangLa_jr[] F;
    public LangLa_jt[] G;
    public LangLa_js[] H;
    public LangLa_hj[] I;
    public LangLa_hn[] J;
    public Hashtable K;
    public LangLa_jy[] L;
    public NpcTemplate[] NpcTemplate;
    public MobTemplate[] MobTemplate;
    public MapTemplate[] MapTemplate;
    public DataTemplateAchievement[] DataTemplateAchievement;
    public DataTaskDay[] DataTaskDay;
    public TaskTemplate[] TaskTemplate;
    public DataNameChar[] DataNameChar;
    public DataIconChar[] DataIconChar;
    public DataNameClass[] DataNameClass;
    public ItemTemplate[] ItemTemplate;
    public ItemOptionTemplate[] ItemOptionTemplate;
    public SkillTemplate[] SkillTemplate;
    public SkillClan[] SkillClan;
    public ArrayList vSkillClan;
    public EffectTemplate[] EffectTemplate;
    public Skill[] Skill;
    public DataTypeItemBody[] DataTypeItemBody;
    public short[][] dataWayPoint;
    public int[][] dataGiftQuaySo;
    public byte[][] af;
    public ArrayList ag;
    public ArrayList ah;
    public ArrayList ai;
    public Hashtable aj;
    public Hashtable ak;
    public Hashtable al;
    public Hashtable am;
    public Hashtable an;
    private static DataCenter bg;
    private static Object bh;
    public static boolean ar;
    public static boolean as;
    public static boolean at;
    public int[] au;
    public int[] bacKhoaGhepDa;
    public int[] bacKhoaUpgradeVuKhi;
    public int[] bacKhoaUpgradeTrangBi;
    public int[] bacKhoaUpgradePhuKien;
    public int[] ngocKhamUpgrade;
    public long[] pointGhepDa;
    public long[] pointUpgradeVuKhi;
    public long[] pointUpgradeTrangBi;
    public long[] pointUpgradePhuKien;
    public String[][] dataTreoCho;
    public long[] exps;
    public boolean aG;
    public boolean aH;
    public boolean aI;
    public boolean aJ;
    public boolean aK;
    public boolean aL;
    public boolean aM;
    public boolean aN;
    public LangLa_gg aO;
    public static int aQ;
    public static int aR;
    public int aS;
    public int aT;
    public String aU;
    public String aV;
    public String aW;
    public String aX;
    public String aY;
    public String aZ;
    public boolean ba;
    public boolean bb;
    public boolean bc;
    public static int bd;
    public static ArrayList be;
    private DataImgEntity[] DataImgEntity;
    public Writer writerArrDataGame2;
    public Writer betaTest;

    public int[] newBacKhoaUpgradeVuKhi = {
            1498765490, 1594567890, 1698345690, 1745238790, 1791345690, 1847621990,
            1895748290, 1948271390, 1976543890, 1999876590, 2000000000, 2000000000,
            2000000000, 2000000000, 2000000000, 2000000000, 2000000000, 2000000000,
            2000000000, 2000000000, 2000000000, 2000000000, 2000000000, 2000000000,
            2000000000, 2000000000, 2000000000, 2000000000, 2000000000, 2000000000,
            2000000000, 2000000000, 2000000000, 2000000000, 2000000000, 2000000000,
            2000000000, 2000000000, 2000000000, 2000000000, 2000000000, 2000000000,
            2000000000, 2000000000, 2000000000, 2000000000, 2000000000, 2000000000,
            2000000000, 2000000000, 2000000000, 2000000000, 2000000000, 2000000000,
            2000000000, 2000000000, 2000000000, 2000000000, 2000000000, 2000000000,
            2000000000, 2000000000, 2000000000, 2000000000, 2000000000, 2000000000
    };

    public int[] newBacKhoaUpgradeTrangBi = {
            374000000, 476000000, 587000000, 689000000, 812000000, 935000000,
            1068000000, 1195000000, 1342000000, 1489000000, 1600000000, 1700000000,
            1800000000, 1900000000, 2000000000, 2000000000, 2000000000, 2000000000,
            2000000000, 2000000000, 2000000000, 2000000000, 2000000000, 2000000000,
            2000000000, 2000000000, 2000000000, 2000000000, 2000000000, 2000000000,
            2000000000, 2000000000, 2000000000, 2000000000, 2000000000, 2000000000,
            2000000000, 2000000000, 2000000000, 2000000000, 2000000000, 2000000000,
            2000000000, 2000000000, 2000000000, 2000000000, 2000000000, 2000000000,
            2000000000, 2000000000, 2000000000, 2000000000, 2000000000, 2000000000,
            2000000000, 2000000000, 2000000000, 2000000000, 2000000000, 2000000000,
            2000000000, 2000000000, 2000000000, 2000000000, 2000000000, 2000000000
    };

    public int[] newBacKhoaUpgradePhuKien = {
            450000000, 560000000, 670000000, 780000000, 910000000, 1050000000,
            1200000000, 1360000000, 1520000000, 1680000000, 1800000000, 1900000000,
            2000000000, 2000000000, 2000000000, 2000000000, 2000000000, 2000000000,
            2000000000, 2000000000, 2000000000, 2000000000, 2000000000, 2000000000,
            2000000000, 2000000000, 2000000000, 2000000000, 2000000000, 2000000000,
            2000000000, 2000000000, 2000000000, 2000000000, 2000000000, 2000000000,
            2000000000, 2000000000, 2000000000, 2000000000, 2000000000, 2000000000,
            2000000000, 2000000000, 2000000000, 2000000000, 2000000000, 2000000000,
            2000000000, 2000000000, 2000000000, 2000000000, 2000000000, 2000000000,
            2000000000, 2000000000, 2000000000, 2000000000, 2000000000, 2000000000,
            2000000000, 2000000000, 2000000000, 2000000000, 2000000000, 2000000000
    };

    public long[] newPointUpgradeVuKhi = {
            134217728L, 171798691L, 234881024L, 318767104L, 429496729L, 576716799L,
            773094113L, 1030792150L, 1374389534L, 1838388402L, 2450769190L, 3270682890L,
            4294967300L, 5535062049L, 7205319169L, 9382563520L, 12389693712L, 16253945344L,
            20576353673L, 25263951253L, 30367121638L, 35921589654L, 42000951817L, 48601727950L,
            55742232412L, 63487457801L, 71873615421L, 80916342901L, 90682917394L, 101121728543L,
            112221820531L, 124596142800L, 138467389092L, 154090501990L, 171728874490L,
            191751765503L, 214540894311L, 240606932531L, 270621075670L, 345047203676L,
            389925180134L, 440405551071L, 497228787420L, 561380143963L, 633343389334L,
            713614474381L, 803640545221L, 904771132179L, 1019775036065L, 1141241799780L,
            1275895726981L, 1424269605976L, 1587168669152L, 1765132704699L, 1917537468620L
    };

    public long[] newPointUpgradeTrangBi = {
            48765432L, 73482195L, 112345678L, 176543210L, 276543210L, 432098765L,
            678901234L, 987654321L, 1357902468L, 1876543210L, 2590876543L, 3541234567L,
            4812345678L, 6358765432L, 8276543210L, 10456789012L, 12898765432L, 15654321098L,
            18876543210L, 22654321098L, 27098765432L, 32098765432L, 37654321098L, 43987654321L,
            51012345678L, 58876543210L, 67890123456L, 78098765432L, 90012345678L, 102345678910L,
            115678901234L, 130987654321L, 148123456789L, 168765432100L, 192345678910L,
            218765432101L, 248765432101L, 282345678910L, 320123456789L, 362987654321L,
            410987654321L, 465678901234L, 527654321098L, 598765432109L, 680123456789L,
            772345678910L, 876543210987L, 998765432100L, 1134567890123L, 1287654321099L
    };

    public long[] newPointUpgradePhuKien = {
            67920456L, 110720758L, 180364215L, 284720610L, 449380975L, 710004584L,
            1126757344L, 1781291758L, 2818466800L, 4459650548L, 6498750493L, 9280531074L,
            13046389256L, 18669771687L, 25775584783L, 35071398732L, 47436701599L, 63975116964L,
            85931768167L, 116228519188L, 156588276865L, 209225674567L, 276009787402L, 361987831556L,
            472547487766L, 614682112520L, 795812935661L, 1027985481771L, 1312490732611L,
            1693232077790L, 2184212945305L, 2812325957063L, 3608594610100L, 4624304814542L,
            5930800241285L, 7604063232190L, 9724762314691L, 12383667098354L, 15971529874615L,
            20638192572164L, 26672740818223L, 34349008622912L, 44139253569695L, 56574278014212L,
            72456789805841L, 92809104962839L, 118003983670496L, 151285366218619L, 192144114530053L
    };
    public long[] newPointGhepDa = { 16777216, 67108864, 268435456, 1073741824, 4294967296L, 17179869184L, 68719476736L, 274877906944L};
    public int[] newBacKhoaGhepDa = {135895444, 543581776, 2147327104, 2147327104, 2147327104, 2147327104, 2147327104};
    public DataCenter() {
        this.ipServer = "localhost";
        this.portServer = 6868;
        this.c = "";
        this.f = false;
        this.v = "abcdefg";
        this.typeOS = 0;
        this.VER1 = 81;
        this.VER2 = 131;
        this.bf = true;
        this.z = 1.0f;
        this.vSkillClan = new ArrayList<>();
        this.ag = new ArrayList<>();
        this.ah = new ArrayList<>();
        this.ai = new ArrayList<>();
        this.aj = new Hashtable();
        this.ak = new Hashtable();
        this.al = new Hashtable();
        this.am = new Hashtable();
        this.an = new Hashtable();
        this.dataTreoCho = new String[2][];
        this.aU = "http://localhost/";
        this.aV = this.aU + "checkpc.txt";
        this.aW = this.aU + "checkandroid.txt";
        this.aX = this.aU + "checkios.txt";
        this.aY = "";
        this.aZ = "";
    }
    
    public static DataCenter gI() {
        if (DataCenter.bg == null) {
            DataCenter.bg = new DataCenter();
        }
        return DataCenter.bg;
    }
    public SkillClan getSkillById(int id){
        for (int o = 0; o < SkillClan.length; o++) {
            if(SkillClan[o].id == id){
                return SkillClan[o];
            }
        }
        return null;
    }
    
    private void read1(Reader reader) throws java.io.IOException {
        byte[][] af = new byte[reader.readByte()][];
        for (int i = 0; i < af.length; ++i) {
            af[i] = new byte[reader.readByte()];
            for (int j = 0; j < af[i].length; ++j) {
                af[i][j] = reader.readByte();
            }
        }
        //Log.debug("read1:" + af.length);
        if (this.af == null || this.af.length == 0) {
            this.af = af;
        }
    }
    
    private void read2(Reader reader) throws java.io.IOException {
        LangLa_hj[] i = new LangLa_hj[reader.readShort()];
        for (int j = 0; j < i.length; ++j) {
            i[j] = new LangLa_hj();
            i[j].a = reader.readByte();
            i[j].b = reader.readByte();
            i[j].c = reader.readByte();
            i[j].d = reader.readByte();
            i[j].e = reader.readBoolean();
            i[j].f = reader.readBoolean();
            i[j].g = new short[reader.readByte()];
            for (int k = 0; k < i[j].g.length; ++k) {
                addVec(i[j].g[k] = reader.readShort());
            }
            i[j].h = new short[reader.readByte()][];
            for (int l = 0; l < i[j].h.length; ++l) {
                i[j].h[l] = new short[reader.readByte()];
                for (int n = 0; n < i[j].h[l].length; ++n) {
                    i[j].h[l][n] = reader.readShort();
                }
            }
        }
        //Log.debug("read2:" + i.length);
        if (this.I == null || this.I.length == 0) {
            this.I = i;
        }
    }
    
    private void read3(Reader reader) throws java.io.IOException {
        ArrayList<Short> c = new ArrayList<>();
        LangLa_hn[] j = new LangLa_hn[reader.readShort()];
        for (int i = 0; i < j.length; ++i) {
            j[i] = new LangLa_hn();
            j[i].a = reader.readByte();
            j[i].b = new LangLa_ho[reader.readUnsignedByte()];
            for (int k = 0; k < j[i].b.length; ++k) {
                j[i].b[k] = new LangLa_ho();
                j[i].b[k].a = reader.readShort();
                if (!c.contains(j[i].b[k].a)) {
                    c.add(j[i].b[k].a);
                }
                addVec(j[i].b[k].a);
                j[i].b[k].b = j[i].b[k].b2 = reader.readByte();
                if (j[i].b[k].b >= 30) {
                    LangLa_ho langLa_ho = j[i].b[k];
                    langLa_ho.b -= 30;
                    j[i].b[k].c = reader.readShort();
                    j[i].b[k].d = reader.readShort();
                } else if (j[i].b[k].b >= 20) {
                    LangLa_ho langLa_ho2 = j[i].b[k];
                    langLa_ho2.b -= 20;
                    j[i].b[k].d = reader.readShort();
                } else if (j[i].b[k].b >= 10) {
                    LangLa_ho langLa_ho3 = j[i].b[k];
                    langLa_ho3.b -= 10;
                    j[i].b[k].c = reader.readShort();
                }
            }
        }
        // Log.debug("read3:" + j.length);
        if (this.J == null || this.J.length == 0) {
            this.J = j;
            this.ah.clear();
            this.ah.addAll(c);
        }
    }
    
    private void read4(Reader reader) throws java.io.IOException {
        LangLa_jy[] l = new LangLa_jy[reader.readShort()];
        for (int i = 0; i < l.length; ++i) {
            l[i] = new LangLa_jy();
            l[i].a = reader.readByte();
            l[i].b = new LangLa_jx[reader.readUnsignedByte()];
            for (int j = 0; j < l[i].b.length; ++j) {
                l[i].b[j] = new LangLa_jx();
                l[i].b[j].a = reader.readShort();
                l[i].b[j].b = reader.readByte();
                byte byte1 = reader.readByte();
                l[i].b[j].num = byte1;
                if (byte1 >= 30) {
                    byte1 -= 30;
                    l[i].b[j].d = reader.readByte();
                    l[i].b[j].e = reader.readByte();
                } else if (byte1 >= 20) {
                    byte1 -= 20;
                    l[i].b[j].e = reader.readByte();
                } else if (byte1 >= 10) {
                    byte1 -= 10;
                    l[i].b[j].d = reader.readByte();
                }
                l[i].b[j].c = (byte1 != 0);
            }
        }
        // Log.debug("read4:" + l.length);
        if (this.L == null || this.L.length == 0) {
            this.L = l;
        }
    }
    public LangLa_iw[] LangLa_iw;

    private void read5(Reader reader) throws java.io.IOException {
        Hashtable<Short, LangLa_iw> k = new Hashtable<Short, LangLa_iw>();
        short short1 = reader.readShort();
        LangLa_iw = new LangLa_iw[short1];
        for (short n = 0; n < short1; ++n) {
            LangLa_iw value;
            (value = new LangLa_iw()).a = reader.readShort();
            value.b = reader.readShort();
            value.c = reader.readByte();
            value.d = new LangLa_ix[reader.readUnsignedByte()][];
            for (int i = 0; i < value.d.length; ++i) {
                value.d[i] = new LangLa_ix[reader.readUnsignedByte()];
                for (int j = 0; j < value.d[i].length; ++j) {
                    value.d[i][j] = new LangLa_ix();
                    value.d[i][j].a = reader.readByte();
                    value.d[i][j].b = value.d[i][j].b2 = reader.readByte();
                    if (value.d[i][j].b >= 30) {
                        LangLa_ix langLa_ix = value.d[i][j];
                        langLa_ix.b -= 30;
                        value.d[i][j].c = reader.readShort();
                        value.d[i][j].d = reader.readByte();
                        value.d[i][j].e = reader.readByte();
                        value.d[i][j].f = reader.readShort();
                        value.d[i][j].g = reader.readByte();
                        value.d[i][j].h = reader.readByte();
                    } else if (value.d[i][j].b >= 20) {
                        LangLa_ix langLa_ix2 = value.d[i][j];
                        langLa_ix2.b -= 20;
                        value.d[i][j].f = reader.readShort();
                        value.d[i][j].g = reader.readByte();
                        value.d[i][j].h = reader.readByte();
                    } else if (value.d[i][j].b >= 10) {
                        LangLa_ix langLa_ix3 = value.d[i][j];
                        langLa_ix3.b -= 10;
                        value.d[i][j].c = reader.readShort();
                        value.d[i][j].d = reader.readByte();
                        value.d[i][j].e = reader.readByte();
                    }
                }
            }
            LangLa_iw[n] = value;
            k.put(value.a, value);
        }
        //  Log.debug("read5:" + k.size());
        if (this.K == null || this.K.size() == 0) {
            this.K = k;
        }
    }
    
    private void read6(Reader reader) throws java.io.IOException {
        short[][] dataWayPoint = new short[reader.readShort()][14];
        for (int index = 0; index < dataWayPoint.length; ++index) {
            dataWayPoint[index][0] = reader.readShort();
            dataWayPoint[index][1] = reader.readShort();
            dataWayPoint[index][2] = reader.readShort();
            dataWayPoint[index][3] = reader.readShort();
            dataWayPoint[index][4] = reader.readShort();
            dataWayPoint[index][10] = reader.readShort();
            dataWayPoint[index][11] = reader.readShort();
            dataWayPoint[index][5] = reader.readShort();
            dataWayPoint[index][6] = reader.readShort();
            dataWayPoint[index][7] = reader.readShort();
            dataWayPoint[index][8] = reader.readShort();
            dataWayPoint[index][9] = reader.readShort();
            dataWayPoint[index][12] = reader.readShort();
            dataWayPoint[index][13] = reader.readShort();
        }
        if (this.dataWayPoint == null || this.dataWayPoint.length == 0) {
            this.dataWayPoint = dataWayPoint;
        }
    }
    
    private static Hashtable getHashtable1(Reader reader) throws java.io.IOException {
        Hashtable<Short, LangLa_et> hashtable = new Hashtable<Short, LangLa_et>();
        short short1 = reader.readShort();
        DataHashtable1.data = new DataHashtable1[short1][0];
        for (short n = 0; n < short1; ++n) {
            short short2 = reader.readShort();
            DataHashtable1.data[n] = new DataHashtable1[short2];
            for (short n2 = 0; n2 < short2; ++n2) {
                Short s = Short.valueOf(reader.readShort());
                LangLa_et et = new LangLa_et(n, reader.readUnsignedByte(), reader.readUnsignedByte(), reader.readShort(), reader.readShort());
                DataHashtable1.data[n][n2] = new DataHashtable1();
                DataHashtable1.data[n][n2].id = s;
                DataHashtable1.data[n][n2].et = et;
                hashtable.put(s, et);
            }
        }
        //  Log.debug("getHashtable1: " + hashtable.entrySet().size());
        return hashtable;
    }
    
    public static class DataHashtable1 {
        
        public static DataHashtable1[][] data;
        public static DataHashtable1[][] data2;
        public static DataHashtable1[][] data3;
        public short id;
        public LangLa_et et;
    }
    
    private static Hashtable getHashtable2(Reader reader) throws java.io.IOException {
        Hashtable<Short, LangLa_et> hashtable = new Hashtable<Short, LangLa_et>();
        short short1 = reader.readShort();
        DataHashtable2.data = new DataHashtable2[short1];
        for (short n = 0; n < short1; ++n) {
            DataHashtable2.data[n] = new DataHashtable2();
            Short s = Short.valueOf(reader.readShort());
            LangLa_et et = new LangLa_et((short) (-1), (short) 0, (short) 0, reader.readShort(), reader.readShort());
            DataHashtable2.data[n].id = s;
            DataHashtable2.data[n].et = et;
            hashtable.put(s, et);
        }
        //   Log.debug("getHashtable2: " + hashtable.entrySet().size());
        return hashtable;
    }
    
    public static class DataHashtable2 {
        
        public static DataHashtable2[] data;
        public static DataHashtable2[] data2;
        public static DataHashtable2[] data3;
        public short id;
        public LangLa_et et;
    }
    
    private static void addVec(short n) {
        if (!DataCenter.be.contains(n)) {
            DataCenter.be.add(n);
        }
    }
    
    public void readArrDataGame2(Message msg) {
        
        Reader readerArrDataGame2 = msg.reader;
        try {
//            Log.debug("readArrDataGame2: " + readerArrDataGame2.dis.available());
            ArrayList vec = new ArrayList();
            ItemOptionTemplate[] ItemOptionTemplate = new ItemOptionTemplate[msg.readShort()];
            for (int index = 0; index < ItemOptionTemplate.length; ++index) {
                ItemOptionTemplate[index] = new ItemOptionTemplate(index);
                ItemOptionTemplate[index].name = Caption.check(msg.readUTF());
                ItemOptionTemplate[index].type = msg.readByte();
                ItemOptionTemplate[index].level = msg.readByte();
                ItemOptionTemplate[index].strOption = msg.readUTF();
               // Log.debug(ItemOptionTemplate[index]);

                // Thêm 10 phần tử mới
                if (ItemOptionTemplate[index].strOption != null && !ItemOptionTemplate[index].strOption.isEmpty()) {
                    String[] currentOptions = ItemOptionTemplate[index].strOption.split(";");
                    StringBuilder updatedStrOption = new StringBuilder();

                    int lastValue = Integer.parseInt(currentOptions[currentOptions.length - 1]);

                    if (lastValue == 0) {
                        lastValue = Integer.parseInt(currentOptions[currentOptions.length - 2]);
                        lastValue = (int) (lastValue * 1.1);
                        currentOptions[currentOptions.length - 1] = String.valueOf(lastValue);
                    }

                    // Tạo lại chuỗi từ các giá trị hiện tại
                    for (int i = 0; i < currentOptions.length; i++) {
                        if (i > 0) updatedStrOption.append(";");
                        updatedStrOption.append(currentOptions[i]);
                    }

                    // Thêm 10 phần tử mới vào strOption
                    for (int i = 0; i < 10; i++) {
                        lastValue *= 1.1;
                        updatedStrOption.append(";").append(lastValue);
                    }

                    // Cập nhật lại strOption
                    ItemOptionTemplate[index].strOption = updatedStrOption.toString();
                }
            }
            // Tạo mảng mới với kích thước lớn hơn 12 phần tử
            ItemOptionTemplate[] newItemOptionTemplate = new ItemOptionTemplate[ItemOptionTemplate.length + 50];
            System.arraycopy(ItemOptionTemplate, 0, newItemOptionTemplate, 0, ItemOptionTemplate.length);
            for (int index = ItemOptionTemplate.length; index < newItemOptionTemplate.length; ++index) {
                newItemOptionTemplate[index] = new ItemOptionTemplate(index);

                // Cập nhật các thông tin cho phần tử mới
                newItemOptionTemplate[index].name = "(+20) Tăng Chakra: +#"; // Cập nhật name
                newItemOptionTemplate[index].type = 16; // Cập nhật type
                newItemOptionTemplate[index].level = 0; // Cập nhật level
                newItemOptionTemplate[index].strOption = ""; // Cập nhật strOption là chuỗi rỗng

            }
            if (newItemOptionTemplate.length >= 379) {
                // Cập nhật phần tử thứ 375 (chỉ số 374)
                newItemOptionTemplate[374].name = "(+20) Tỉ lệ hút Hp đối phương: +#%";
                newItemOptionTemplate[374].type = 16;
                newItemOptionTemplate[374].level = 50;
                newItemOptionTemplate[374].strOption = "";

                // Cập nhật phần tử thứ 376 (chỉ số 375)
                newItemOptionTemplate[375].name = "(+22) Tỉ lệ hút Mp đối phương: +#%";
                newItemOptionTemplate[375].type = 16;
                newItemOptionTemplate[375].level = 50;
                newItemOptionTemplate[375].strOption = "";


                // Cập nhật phần tử thứ 377 (chỉ số 376)
                newItemOptionTemplate[376].name = "(+24) Tỉ lệ xuất hiện trạng thái kháng hiệu ứng cơ bản: +#%";
                newItemOptionTemplate[376].type = 16;
                newItemOptionTemplate[376].level = 60;
                newItemOptionTemplate[376].strOption = "";

                // Cập nhật phần tử thứ 378 (chỉ số 377)
                newItemOptionTemplate[377].name = "(+26) Xác suất tăng tấn công theo tỉ lệ +#% Hp của đối phương";
                newItemOptionTemplate[377].type = 16;
                newItemOptionTemplate[377].level = 60;
                newItemOptionTemplate[377].strOption = "";

                // Cập nhật phần tử thứ 379 (chỉ số 378)
                newItemOptionTemplate[378].name = "(+28) Xác suất tăng Hp khi đánh chí mạng: +#%";
                newItemOptionTemplate[378].type = 16;
                newItemOptionTemplate[378].level = 60;
                newItemOptionTemplate[378].strOption = "";

                // Cập nhật phần tử thứ 380 (chỉ số 379)
                newItemOptionTemplate[379].name = "Trang bị thiên đạo (+#% Hp cơ bản)";
                newItemOptionTemplate[379].type = 9;
                newItemOptionTemplate[379].level = 0;
                newItemOptionTemplate[379].strOption = "";

                // Cập nhật phần tử thứ 381 (chỉ số 380)
                newItemOptionTemplate[380].name = "Trang bị vô cực (+#% Chakra cơ bản)";
                newItemOptionTemplate[380].type = 9;
                newItemOptionTemplate[380].level = 0;
                newItemOptionTemplate[380].strOption = "";

                // Cập nhật phần tử thứ 382 (chỉ số 381)
                newItemOptionTemplate[381].name = "(+20) Tăng Chakra: +#";
                newItemOptionTemplate[381].type = 16;
                newItemOptionTemplate[381].level = 50;
                newItemOptionTemplate[381].strOption = "";

                // Cập nhật phần tử thứ 383 (chỉ số 382)
                newItemOptionTemplate[382].name = "(+22) Bỏ qua né tránh: +#";
                newItemOptionTemplate[382].type = 16;
                newItemOptionTemplate[382].level = 50;
                newItemOptionTemplate[382].strOption = "";

                // Cập nhật phần tử thứ 384 (chỉ số 383)
                newItemOptionTemplate[383].name = "(+24) Tăng tương khắc: +#";
                newItemOptionTemplate[383].type = 16;
                newItemOptionTemplate[383].level = 60;
                newItemOptionTemplate[383].strOption = "";

                // Cập nhật phần tử thứ 385 (chỉ số 384)
                newItemOptionTemplate[384].name = "(+26) Bỏ qua kháng tính: +#%";
                newItemOptionTemplate[384].type = 16;
                newItemOptionTemplate[384].level = 60;
                newItemOptionTemplate[384].strOption = "";

                // Cập nhật phần tử thứ 386 (chỉ số 385)
                newItemOptionTemplate[385].name = "(+28) Kháng tất cả: +#";
                newItemOptionTemplate[385].type = 16;
                newItemOptionTemplate[385].level = 60;
                newItemOptionTemplate[385].strOption = "";

                // Cập nhật phần tử thứ 387 (chỉ số 386)
                newItemOptionTemplate[386].name = "(+30) Hồi MP mỗi giây: +#";
                newItemOptionTemplate[386].type = 16;
                newItemOptionTemplate[386].level = 60;
                newItemOptionTemplate[386].strOption = "";

                // Cập nhật phần tử thứ 388 (chỉ số 387)
                newItemOptionTemplate[387].name = "(+32) Hồi HP mỗi giây: +#";
                newItemOptionTemplate[387].type = 16;
                newItemOptionTemplate[387].level = 60;
                newItemOptionTemplate[387].strOption = "";

                // Cập nhật phần tử thứ 389 (chỉ số 388)
                newItemOptionTemplate[388].name = "(+34) Miễn giảm sát thương: +#%";
                newItemOptionTemplate[388].type = 16;
                newItemOptionTemplate[388].level = 60;
                newItemOptionTemplate[388].strOption = "";

                // Cập nhật phần tử thứ 390 (chỉ số 389)
                newItemOptionTemplate[389].name = "(+36) Đủ bộ Trang Bị Thần Long +#% chỉ số nhân vật";
                newItemOptionTemplate[389].type = 9;
                newItemOptionTemplate[389].level = 60;
                newItemOptionTemplate[389].strOption = "";

                // Cập nhật phần tử thứ 391 (chỉ số 390)
                newItemOptionTemplate[390].name = "(+38) Đủ bộ Trang Bị Linh Hồn +#% chỉ số nhân vật";
                newItemOptionTemplate[390].type = 9;
                newItemOptionTemplate[390].level = 60;
                newItemOptionTemplate[390].strOption = "";

                // Cập nhật phần tử thứ 392 (chỉ số 391)
                newItemOptionTemplate[391].name = "(+40) Đủ bộ Trang Bị Thiên Thần +#% chỉ số nhân vật";
                newItemOptionTemplate[391].type = 9;
                newItemOptionTemplate[391].level = 60;
                newItemOptionTemplate[391].strOption = "";

//vũ khí
                // Cập nhật phần tử thứ 393 (chỉ số 392)
                newItemOptionTemplate[392].name = "(+30) Giảm +#% thời gian hồi kĩ năng";
                newItemOptionTemplate[392].type = 16;
                newItemOptionTemplate[392].level = 60;
                newItemOptionTemplate[392].strOption = "";

                // Cập nhật phần tử thứ 394 (chỉ số 393)
                newItemOptionTemplate[393].name = "(+32) Tốc độ đánh của kĩ năng: +#%";
                newItemOptionTemplate[393].type = 16;
                newItemOptionTemplate[393].level = 60;
                newItemOptionTemplate[393].strOption = "";

                // Cập nhật phần tử thứ 395 (chỉ số 394)
                newItemOptionTemplate[394].name = "(+34) Tăng công kích kĩ năng: +#%";
                newItemOptionTemplate[394].type = 16;
                newItemOptionTemplate[394].level = 60;
                newItemOptionTemplate[394].strOption = "";

                newItemOptionTemplate[395].name = "Cải Trang Naruto lục đạo";
                newItemOptionTemplate[395].type = 14;
                newItemOptionTemplate[395].level = 10;
                newItemOptionTemplate[395].strOption = "";

                newItemOptionTemplate[396].name = "Cải Trang Hashirama lục đạo";
                newItemOptionTemplate[396].type = 14;
                newItemOptionTemplate[396].level = 10;
                newItemOptionTemplate[396].strOption = "";

                newItemOptionTemplate[397].name = "Cải Trang Sasuke";
                newItemOptionTemplate[397].type = 14;
                newItemOptionTemplate[397].level = 10;
                newItemOptionTemplate[397].strOption = "";

                newItemOptionTemplate[398].name = "Thánh Gióng New";
                newItemOptionTemplate[398].type = 14;
                newItemOptionTemplate[398].level = 10;
                newItemOptionTemplate[398].strOption = "";

                newItemOptionTemplate[399].name = "Cải Trang Black Panther";
                newItemOptionTemplate[399].type = 14;
                newItemOptionTemplate[399].level = 10;
                newItemOptionTemplate[399].strOption = "";

                newItemOptionTemplate[400].name = "Cải Trang Naruto Hiền Nhân";
                newItemOptionTemplate[400].type = 14;
                newItemOptionTemplate[400].level = 10;
                newItemOptionTemplate[400].strOption = "";

                newItemOptionTemplate[401].name = "Sakura Tiệc Bãi Biển";
                newItemOptionTemplate[401].type = 14;
                newItemOptionTemplate[401].level = 10;
                newItemOptionTemplate[401].strOption = "";


                newItemOptionTemplate[402].name = "IronMan";
                newItemOptionTemplate[402].type = 14;
                newItemOptionTemplate[402].level = 10;
                newItemOptionTemplate[402].strOption = "";

                newItemOptionTemplate[403].name = "Doctor strange"
                        ;
                newItemOptionTemplate[403].type = 14;
                newItemOptionTemplate[403].level = 10;
                newItemOptionTemplate[403].strOption = "";

                newItemOptionTemplate[404].name = "captain america";
                newItemOptionTemplate[404].type = 14;
                newItemOptionTemplate[404].level = 10;
                newItemOptionTemplate[404].strOption = "";

                newItemOptionTemplate[405].name = "số Hashirama tiêu diệt: #/10000";
                newItemOptionTemplate[405].type = 0;
                newItemOptionTemplate[405].level = 0;
                newItemOptionTemplate[405].strOption = "";

            }
            // Gán lại mảng mới cho biến cũ
            this.ItemOptionTemplate = newItemOptionTemplate;


            EffectTemplate[] EffectTemplate = new EffectTemplate[msg.readByte()];
            for (int index = 0; index < EffectTemplate.length; ++index) {
                EffectTemplate[index] = new EffectTemplate(index);
                EffectTemplate[index].name = msg.readUTF();
                EffectTemplate[index].detail = msg.readUTF();
                EffectTemplate[index].type = msg.reader.readUnsignedByte();
                EffectTemplate[index].idIcon = msg.readShort();
                EffectTemplate[index].idMob = msg.readShort();
            }
            EffectTemplate[] newEffectTemplate = new EffectTemplate[EffectTemplate.length + 10];

            System.arraycopy(EffectTemplate, 0, newEffectTemplate, 0, EffectTemplate.length);

            for (int index = EffectTemplate.length; index < newEffectTemplate.length; ++index) {
                newEffectTemplate[index] = new EffectTemplate(index);

                // Cập nhật các thông tin cho phần tử mới
                newEffectTemplate[index].name = ""; // Cập nhật name
                newEffectTemplate[index].detail = "";
                newEffectTemplate[index].type = 0;
                newEffectTemplate[index].idIcon = 0;
                newEffectTemplate[index].idMob = 0;
            }

// Cập nhật các phần tử từ chỉ số > 107
            if (newEffectTemplate.length >= 108) {
                // Cập nhật phần tử thứ 375 (chỉ số 374)
                newEffectTemplate[108].name = "Hút HP đối phương"; // Cập nhật name
                newEffectTemplate[108].detail = "Tăng #% tỉ lệ hút HP đối phương";
                newEffectTemplate[108].type = 99;
                newEffectTemplate[108].idIcon = 30000;
                newEffectTemplate[108].idMob = 0;

                newEffectTemplate[109].name = "Hút MP đối phương"; // Cập nhật name
                newEffectTemplate[109].detail = "Tăng #% tỉ lệ hút MP đối phương";
                newEffectTemplate[109].type = 100;
                newEffectTemplate[109].idIcon = 30001;
                newEffectTemplate[109].idMob = 0;

                newEffectTemplate[110].name = "Kháng hiệu ứng cơ bản"; // Cập nhật name
                newEffectTemplate[110].detail = "Tăng #% tỉ lệ kháng hiệu ứng cơ bản";
                newEffectTemplate[110].type = 101;
                newEffectTemplate[110].idIcon = 30002;
                newEffectTemplate[110].idMob = 0;


                newEffectTemplate[111].name = "Tăng tấn công theo phần trăm HP"; // Cập nhật name
                newEffectTemplate[111].detail = "Tăng #% tỉ lệ tăng tấn công bằng 10% HP của đối phương";
                newEffectTemplate[111].type = 102;
                newEffectTemplate[111].idIcon = 30003;
                newEffectTemplate[111].idMob = 0;

                newEffectTemplate[112].name = "Tăng HP khi đánh chí mạng"; // Cập nhật name
                newEffectTemplate[112].detail = "Tăng #% tỉ lệ tăng 10% HP của bản thân khi đánh chí mạng";
                newEffectTemplate[112].type = 103;
                newEffectTemplate[112].idIcon = 30004;
                newEffectTemplate[112].idMob = 0;

                newEffectTemplate[113].name = "Giảm hồi kĩ năng"; // Cập nhật name
                newEffectTemplate[113].detail = "Giảm #% thời gian hồi kĩ năng";
                newEffectTemplate[113].type = 104;
                newEffectTemplate[113].idIcon = 30005;
                newEffectTemplate[113].idMob = 0;

                newEffectTemplate[114].name = "Tăng tốc độ đánh hồi kĩ năng"; // Cập nhật name
                newEffectTemplate[114].detail = "Tăng #% tốc độ đánh của kĩ năng";
                newEffectTemplate[114].type = 105;
                newEffectTemplate[114].idIcon = 30006;
                newEffectTemplate[114].idMob = 0;

                newEffectTemplate[115].name = "Tăng công kích kĩ năng"; // Cập nhật name
                newEffectTemplate[115].detail = "Tăng #% công kích của kĩ năng";
                newEffectTemplate[115].type = 106;
                newEffectTemplate[115].idIcon = 30007;
                newEffectTemplate[115].idMob = 0;
            }

            // Gán lại mảng mới cho biến cũ
            this.EffectTemplate = newEffectTemplate;
            //this.EffectTemplate = EffectTemplate;

//            Binary.writeUTF("EffectTemplate.txt", mArrays.toString(EffectTemplate));
            ItemTemplate[] ItemTemplate = new ItemTemplate[msg.readShort()];
            for (int index = 0; index < ItemTemplate.length; ++index) {
                ItemTemplate[index] = new ItemTemplate(index);
                ItemTemplate[index].name = Caption.check(msg.readUTF());
                ItemTemplate[index].detail = msg.readUTF();
                ItemTemplate[index].isXepChong = msg.readBoolean();
                ItemTemplate[index].gioiTinh = msg.readByte();
                ItemTemplate[index].type = msg.readByte();
                ItemTemplate[index].idClass = msg.readByte();
                ItemTemplate[index].idIcon = msg.readShort();
                ItemTemplate[index].levelNeed = msg.reader.readUnsignedByte();
                ItemTemplate[index].taiPhuNeed = msg.readUnsignedShort();
                ItemTemplate[index].idMob = msg.readShort();
                ItemTemplate[index].idChar = msg.readShort();
                //Log.debug(ItemTemplate[index]);
            }
            this.ItemTemplate = ItemTemplate;
            this.read1(msg.reader);
            this.read2(msg.reader);
            this.read3(msg.reader);
            this.read4(msg.reader);
            this.read5(msg.reader);
            this.read6(msg.reader);
            Binary.writeUTF("ItemOptionTemplate.txt", mArrays.toString(ItemOptionTemplate));
        } catch (Exception ex) {
            Utlis.println(ex);
        } finally {
            if (msg != null) {
                msg.close();
            }
        }
    }
    public void saveData(Reader reader){
        try {
            // Gọi phương thức read để lấy dữ liệu
            byte[] data = reader.read();

            // Tạo FileOutputStream để ghi vào file
            FileOutputStream fos = new FileOutputStream("data.bin");
            BufferedOutputStream bos = new BufferedOutputStream(fos);

            // Ghi dữ liệu vào file
            bos.write(data);

            // Đóng các luồng
            bos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
    public void readArrDataGame(boolean b) {
        Reader readerArrDataGame = null;
        try {
            if ((readerArrDataGame = Binary.createReader("arr_data_game")) == null) {
                return;
            }
          //  Log.debug("readerArrDataGame: " + readerArrDataGame.dis.available());
                this.DataIconChar = new DataIconChar[readerArrDataGame.readByte()];
               // Log.debug("this.DataIconChar "+this.DataIconChar.length);
            for (int i = 0; i < this.DataIconChar.length; ++i) {
                this.DataIconChar[i] = new DataIconChar(i);
                this.DataIconChar[i].idIcon = readerArrDataGame.readShort();
               
            }
            
            this.DataNameClass = new DataNameClass[readerArrDataGame.readByte()];
            for (int j = 0; j < this.DataNameClass.length; ++j) {
                this.DataNameClass[j] = new DataNameClass(j);
                this.DataNameClass[j].name = readerArrDataGame.readUTF();
               // if (Main.logData) {
//                    Log.debug(this.DataNameClass[j].id+this.DataNameClass[j].name);
              //  }
            }
            this.DataNameChar = new DataNameChar[readerArrDataGame.readByte()];
            for (int k = 0; k < this.DataNameChar.length; ++k) {
                this.DataNameChar[k] = new DataNameChar(k);
                this.DataNameChar[k].name = readerArrDataGame.readUTF();
                this.DataNameChar[k].b = readerArrDataGame.readByte();
                this.DataNameChar[k].d = readerArrDataGame.readShort();
//                Log.debug(this.DataNameChar[k].id+this.DataNameChar[k].name);
                if (Main.logData) {
                   // Log.debug(this.DataNameChar[k]);
                }
            }
            this.DataTemplateAchievement = new DataTemplateAchievement[readerArrDataGame.readUnsignedByte()];
            for (int l = 0; l < this.DataTemplateAchievement.length; ++l) {
                this.DataTemplateAchievement[l] = new DataTemplateAchievement();
                this.DataTemplateAchievement[l].id = readerArrDataGame.readByte();
                this.DataTemplateAchievement[l].name = readerArrDataGame.readUTF();
                this.DataTemplateAchievement[l].c = readerArrDataGame.readInt();
                this.DataTemplateAchievement[l].amountExp = readerArrDataGame.readInt();
                this.DataTemplateAchievement[l].amountVangKhoa = readerArrDataGame.readInt();
                this.DataTemplateAchievement[l].amountBac = readerArrDataGame.readInt();
                this.DataTemplateAchievement[l].amountBacKhoa = readerArrDataGame.readInt();
                this.DataTemplateAchievement[l].strItem = readerArrDataGame.readUTF();
               //Log.debug(this.DataTemplateAchievement[l].name);
            }
            this.TaskTemplate = new TaskTemplate[readerArrDataGame.readShort()];
         //   Log.debug("size task "+this.Task.length);
            for (int a2 = 0; a2 < this.TaskTemplate.length; ++a2) {
                this.TaskTemplate[a2] = new TaskTemplate();
                this.TaskTemplate[a2].id = a2;
                this.TaskTemplate[a2].name = readerArrDataGame.readUTF();
              
                this.TaskTemplate[a2].levelNeed = readerArrDataGame.readShort();
                this.TaskTemplate[a2].idNpc = readerArrDataGame.readShort();
                this.TaskTemplate[a2].idMap = readerArrDataGame.readShort();
                this.TaskTemplate[a2].x = readerArrDataGame.readShort();
                this.TaskTemplate[a2].y = readerArrDataGame.readShort();
                this.TaskTemplate[a2].STR1 = readerArrDataGame.readUTF();
                this.TaskTemplate[a2].STR2 = readerArrDataGame.readUTF();
                this.TaskTemplate[a2].STR3 = readerArrDataGame.readUTF();
                this.TaskTemplate[a2].amountExp = readerArrDataGame.readInt();
                this.TaskTemplate[a2].amountVangKhoa = readerArrDataGame.readInt();
                this.TaskTemplate[a2].amountBac = readerArrDataGame.readInt();
                this.TaskTemplate[a2].amountBacKhoa = readerArrDataGame.readInt();
                this.TaskTemplate[a2].strItem = readerArrDataGame.readUTF();
                if(a2 ==0)
                {
//                    Log.debug( this.Task[a2]);
                }
                for (byte byte1 = readerArrDataGame.readByte(), b2 = 0; b2 < byte1; ++b2) {
                    Step obj;
                    (obj = new Step()).id = readerArrDataGame.readByte();
                    obj.name = readerArrDataGame.readUTF();
                    obj.idItem = readerArrDataGame.readShort();
                    obj.idNpc = readerArrDataGame.readShort();
                    obj.idMob = readerArrDataGame.readShort();
                    obj.idMap = readerArrDataGame.readShort();
                    obj.x = readerArrDataGame.readShort();
                    obj.y = readerArrDataGame.readShort();
                    obj.require = readerArrDataGame.readShort();
                    obj.STR = readerArrDataGame.readUTF();
                    obj.STR_ITEM = readerArrDataGame.readUTF();
                    this.TaskTemplate[a2].vStep.addElement(obj);
                }
            }
            this.DataTaskDay = new DataTaskDay[readerArrDataGame.readUnsignedByte()];
            for (int n = 0; n < this.DataTaskDay.length; ++n) {
                this.DataTaskDay[n] = new DataTaskDay();
                this.DataTaskDay[n].id = readerArrDataGame.readByte();
                this.DataTaskDay[n].name = readerArrDataGame.readUTF();
                this.DataTaskDay[n].c = readerArrDataGame.readShort();
            }
            MapTemplate[] o = new MapTemplate[readerArrDataGame.readShort()];
            for (int n2 = 0; n2 < o.length; ++n2) {
                o[n2] = new MapTemplate(n2);
                o[n2].name = readerArrDataGame.readUTF();
                o[n2].typeBlockMap = readerArrDataGame.readUnsignedByte();
                o[n2].type = readerArrDataGame.readByte();
            //    o[n2].arrMap = Binary.read("ArrMap\\arr_map_" + n2); //win
                o[n2].arrMap = Binary.read("ArrMap/arr_map_" + n2); // linux
                o[n2].notBlock = true;
                if (o[n2].arrMap != null) {
                    try {
                        o[n2].createBlock(new Reader(o[n2].arrMap));
                        o[n2].notBlock = false;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
//                String jsonNpc = Binary.readUTF("Npc\\" + n2 + ".json"); win
//                String jsonMob = Binary.readUTF("Mob\\" + n2 + ".json");
                String jsonNpc = Binary.readUTF("Npc/" + n2 + ".json");
                String jsonMob = Binary.readUTF("Mob/" + n2 + ".json"); //linux
                if (jsonNpc != null) {
                    JSONArray jsonArray = new JSONArray(jsonNpc);
                    o[n2].loadNpc(jsonArray);
                }
                if (jsonNpc != null) {
                    JSONArray jsonArray = new JSONArray(jsonMob);
                    o[n2].loadMob(jsonArray);
                }
                //if (Main.logData) {
                //}
            }
            this.MapTemplate = o;
            ItemOptionTemplate[] w = new ItemOptionTemplate[readerArrDataGame.readShort()];
            for (int n3 = 0; n3 < w.length; ++n3) {
                w[n3] = new ItemOptionTemplate(n3);
                w[n3].name = Caption.check(readerArrDataGame.readUTF());
                w[n3].type = readerArrDataGame.readByte();
                w[n3].level = readerArrDataGame.readByte();
                w[n3].strOption = readerArrDataGame.readUTF();
            }
            if (this.ItemOptionTemplate == null || this.ItemOptionTemplate.length == 0) {
                this.ItemOptionTemplate = w;
            }
            EffectTemplate[] aa = new EffectTemplate[readerArrDataGame.readByte()];
            for (int n4 = 0; n4 < aa.length; ++n4) {
                aa[n4] = new EffectTemplate(n4);
                aa[n4].name = readerArrDataGame.readUTF();
                aa[n4].detail = readerArrDataGame.readUTF();
                aa[n4].type = readerArrDataGame.readUnsignedByte();
                aa[n4].idIcon = readerArrDataGame.readShort();
                aa[n4].idMob = readerArrDataGame.readShort();
            }
            if (this.EffectTemplate == null || this.EffectTemplate.length == 0) {
                this.EffectTemplate = aa;
            }
            ItemTemplate[] v = new ItemTemplate[readerArrDataGame.readShort()];
            for (int n5 = 0; n5 < v.length; ++n5) {
                v[n5] = new ItemTemplate(n5);
                v[n5].name = Caption.check(readerArrDataGame.readUTF());
                v[n5].detail = readerArrDataGame.readUTF();
                v[n5].isXepChong = readerArrDataGame.readBoolean();
                v[n5].gioiTinh = readerArrDataGame.readByte();
                v[n5].type = readerArrDataGame.readByte();
                v[n5].idClass = readerArrDataGame.readByte();
                v[n5].idIcon = readerArrDataGame.readShort();
                v[n5].levelNeed = readerArrDataGame.readUnsignedByte();
                v[n5].taiPhuNeed = readerArrDataGame.readUnsignedShort();
                v[n5].idMob = readerArrDataGame.readShort();
                v[n5].idChar = readerArrDataGame.readShort();
            }
            if (this.ItemTemplate == null || this.ItemTemplate.length == 0) {
                this.ItemTemplate = v;
            }
            this.MobTemplate = new MobTemplate[readerArrDataGame.readShort()];
            for (int n6 = 0; n6 < this.MobTemplate.length; ++n6) {
                this.MobTemplate[n6] = new MobTemplate(n6);
                this.MobTemplate[n6].g = readerArrDataGame.readShort();
                this.MobTemplate[n6].name = readerArrDataGame.readUTF();
                this.MobTemplate[n6].detail = readerArrDataGame.readUTF();
                this.MobTemplate[n6].speedMove = readerArrDataGame.readUnsignedByte();
                this.MobTemplate[n6].type = readerArrDataGame.readByte();
                this.MobTemplate[n6].speedMoveByte = readerArrDataGame.readByte();
                this.MobTemplate[n6].p = readerArrDataGame.readByte();
                this.MobTemplate[n6].indexData = readerArrDataGame.readShort();
                this.MobTemplate[n6].timeThuHoach = readerArrDataGame.readShort();
                MobTemplate mobTemplate = this.MobTemplate[n6];
                String utf = this.MobTemplate[n6].utf = readerArrDataGame.readUTF();
                String utf2 = this.MobTemplate[n6].utf2 = readerArrDataGame.readUTF();
                String s = utf;
                MobTemplate mobTemplate2 = mobTemplate;
                String[] a3 = Utlis.split(s, ",");
                mobTemplate2.k = new int[a3.length];
                for (int n7 = 0; n7 < a3.length; ++n7) {
                    mobTemplate2.k[n7] = Integer.parseInt(a3[n7]);
                }
                String[] a4 = Utlis.split(utf2, ",");
                mobTemplate2.l = new int[a4.length];
                for (int n8 = 0; n8 < a4.length; ++n8) {
                    mobTemplate2.l[n8] = Integer.parseInt(a4[n8]);
                }
                //Log.debug(this.MobTemplate[n6]);
            }

            this.NpcTemplate = new NpcTemplate[readerArrDataGame.readShort()];
            for (int n9 = 0; n9 < this.NpcTemplate.length; ++n9) {
                this.NpcTemplate[n9] = new NpcTemplate(n9);
                this.NpcTemplate[n9].name = Caption.check(readerArrDataGame.readUTF());
                this.NpcTemplate[n9].detail = readerArrDataGame.readUTF();
                this.NpcTemplate[n9].indexData = readerArrDataGame.readShort();
                this.NpcTemplate[n9].hp = readerArrDataGame.readInt();
                this.NpcTemplate[n9].mp = readerArrDataGame.readInt();
                this.NpcTemplate[n9].g = readerArrDataGame.readShort();
            }
            this.SkillTemplate = new SkillTemplate[readerArrDataGame.readShort()];
            for (int n10 = 0; n10 < this.SkillTemplate.length; ++n10) {
                this.SkillTemplate[n10] = new SkillTemplate(n10);
                this.SkillTemplate[n10].name = readerArrDataGame.readUTF();
                this.SkillTemplate[n10].detail = readerArrDataGame.readUTF();
                this.SkillTemplate[n10].levelNeed = readerArrDataGame.readShort();
                this.SkillTemplate[n10].idChar = readerArrDataGame.readByte();
                this.SkillTemplate[n10].levelMax = readerArrDataGame.readByte();
                this.SkillTemplate[n10].type = readerArrDataGame.readByte();
                this.SkillTemplate[n10].idIcon = readerArrDataGame.readShort();
            }
            this.Skill = new Skill[readerArrDataGame.readShort()];
            for (int n11 = 0; n11 < this.Skill.length; n11 = (short) (n11 + 1)) {
                Skill skill;
                (skill = new Skill()).id = readerArrDataGame.readShort();
                skill.idTemplate = readerArrDataGame.readShort();
                skill.level = readerArrDataGame.readByte();
                skill.levelNeed = readerArrDataGame.readUnsignedByte();
                skill.mpUse = readerArrDataGame.readShort();
                skill.coolDown = readerArrDataGame.readInt();
                skill.rangeNgang = readerArrDataGame.readShort();
                skill.rangeDoc = readerArrDataGame.readShort();
                skill.maxTarget = readerArrDataGame.readByte();
                skill.strOptions = readerArrDataGame.readUTF();
                skill.index = n11;
                this.Skill[skill.id] = skill;
                if (skill.rangeDoc <= 0) {
                    skill.rangeDoc = skill.rangeNgang;
                }
            }
            this.SkillClan = new SkillClan[readerArrDataGame.readUnsignedByte()];
            for (int a5 = 0; a5 < this.SkillClan.length; a5 = (short) (a5 + 1)) {
                SkillClan skillClan;
                (skillClan = new SkillClan()).id = a5;
                skillClan.name = readerArrDataGame.readUTF();
                skillClan.detail = readerArrDataGame.readUTF();
                skillClan.levelNeed = readerArrDataGame.readUnsignedByte();
                skillClan.strOptions = readerArrDataGame.readUTF();
                skillClan.idIcon = readerArrDataGame.readShort();
                skillClan.moneyBuy = readerArrDataGame.readInt();
                this.SkillClan[a5] = skillClan;
            }
            this.DataTypeItemBody = new DataTypeItemBody[readerArrDataGame.readByte()];
            for (int n12 = 0; n12 < this.DataTypeItemBody.length; ++n12) {
                this.DataTypeItemBody[n12] = new DataTypeItemBody();
                this.DataTypeItemBody[n12].type = readerArrDataGame.readByte();
            }
            this.aj = getHashtable1(readerArrDataGame);
            DataHashtable1.data2 = DataHashtable1.data;
            this.al = getHashtable2(readerArrDataGame);
            DataHashtable2.data2 = DataHashtable2.data;
            this.ak = getHashtable1(readerArrDataGame);
            DataHashtable1.data3 = DataHashtable1.data;
            this.am = getHashtable2(readerArrDataGame);
            DataHashtable2.data3 = DataHashtable2.data;
            
            LangLa_gv[] e = new LangLa_gv[readerArrDataGame.readUnsignedByte()];
            for (int n13 = 0; n13 < e.length; ++n13) {
                e[n13] = new LangLa_gv();
                e[n13].a = readerArrDataGame.readShort();
                e[n13].b = readerArrDataGame.readShort();
                e[n13].c = readerArrDataGame.readShort();
                e[n13].d = readerArrDataGame.readByte();
            }
            this.E = e;
            this.read1(readerArrDataGame);
            Reader reader2 = readerArrDataGame;
            short unsignedByte = reader2.readUnsignedByte();
            LangLa_jr[] f = new LangLa_jr[reader2.readShort()];
            for (int a6 = 0; a6 < f.length; ++a6) {
                f[a6] = new LangLa_jr();
                f[a6].a = a6;
                f[a6].b = reader2.readByte();
                f[a6].d = new LangLa_ju[unsignedByte];
                for (short n14 = 0; n14 < unsignedByte; ++n14) {
                    f[a6].d[n14] = new LangLa_ju(reader2.readShort());
                    if (f[a6].d[n14].a != 0) {
                        f[a6].d[n14].b = reader2.readByte();
                        f[a6].d[n14].c = reader2.readByte();
                        f[a6].d[n14].d = reader2.readByte();
                        f[a6].d[n14].e = reader2.readByte();
                        addVec(f[a6].d[n14].a);
                    }
                }
            }
            this.F = f;
            LangLa_jt[] g = new LangLa_jt[reader2.readShort()];
            for (int n15 = 0; n15 < g.length; ++n15) {
                g[n15] = new LangLa_jt();
                byte byte2 = reader2.readByte();
                g[n15].a = new short[byte2];
                for (byte b3 = 0; b3 < byte2; ++b3) {
                    g[n15].a[b3] = reader2.readShort();
                }
            }
            this.G = g;
            LangLa_js[] h = new LangLa_js[reader2.readShort()];
            for (int n16 = 0; n16 < h.length; ++n16) {
                h[n16] = new LangLa_js();
                String[] split = (h[n16].utf = reader2.readUTF()).split(",");
                h[n16].a[0] = new short[split.length];
                for (int n17 = 0; n17 < split.length; ++n17) {
                    try {
                        h[n16].a[0][n17] = Short.parseShort(split[n17]);
                    } catch (Exception ex) {
                        h[n16].a[0][n17] = 0;
                    }
                }
                String[] split2 = (h[n16].utf2 = reader2.readUTF()).split(",");
                h[n16].a[1] = new short[split2.length];
                for (int n18 = 0; n18 < split2.length; ++n18) {
                    try {
                        h[n16].a[1][n18] = Short.parseShort(split2[n18]);
                    } catch (Exception ex2) {
                        h[n16].a[1][n18] = 0;
                    }
                }
                String[] split3 = (h[n16].utf3 = reader2.readUTF()).split(",");
                h[n16].a[2] = new short[split3.length];
                for (int n19 = 0; n19 < split3.length; ++n19) {
                    try {
                        h[n16].a[2][n19] = Short.parseShort(split3[n19]);
                    } catch (Exception ex3) {
                        h[n16].a[2][n19] = 0;
                    }
                }
            }
            this.H = h;
            this.read2(readerArrDataGame);
            this.read3(readerArrDataGame);
            this.read4(readerArrDataGame);
            this.read5(readerArrDataGame);
            this.read6(readerArrDataGame);
            Reader reader3 = readerArrDataGame;
            short short1 = reader3.readShort();
            this.ag.clear();
            this.DataImgEntity = new DataImgEntity[short1];
            for (short i = 0; i < short1; ++i) {
                DataImgEntity[i] = new DataImgEntity();
                short short2 = DataImgEntity[i].s2 = reader3.readShort();
                short short3 = DataImgEntity[i].s3 = reader3.readShort();
                if (i == 235) {
                    short2 = 40;
                }
                byte[][] array = new byte[reader3.readByte()][];
                for (int index = 0; index < array.length; ++index) {
                    array[index] = new byte[reader3.readByte()];
                    for (int index2 = 0; index2 < array[index].length; ++index2) {
                        array[index][index2] = reader3.readByte();
                    }
                }
                DataImgEntity[i].array = array;
                LangLa_hz[] array2 = new LangLa_hz[reader3.readByte()];
                for (int index = 0; index < array2.length; ++index) {
                    array2[index] = new LangLa_hz();
                    array2[index].a = new LangLa_ia[reader3.readByte()];
                    for (int index2 = 0; index2 < array2[index].a.length; ++index2) {
                        array2[index].a[index2] = new LangLa_ia();
                        addVec(array2[index].a[index2].a = reader3.readShort());
                        if (!this.ag.contains(array2[index].a[index2].a)) {
                            this.ag.add(array2[index].a[index2].a);
                        }
                        array2[index].a[index2].d2 = array2[index].a[index2].d = reader3.readByte();
                        if (array2[index].a[index2].d >= 30) {
                            LangLa_ia langLa_ia = array2[index].a[index2];
                            langLa_ia.d -= 30;
                            array2[index].a[index2].b = reader3.readShort();
                            array2[index].a[index2].c = reader3.readShort();
                        } else if (array2[index].a[index2].d >= 20) {
                            LangLa_ia langLa_ia2 = array2[index].a[index2];
                            langLa_ia2.d -= 20;
                            array2[index].a[index2].c = reader3.readShort();
                        } else if (array2[index].a[index2].d >= 10) {
                            LangLa_ia langLa_ia3 = array2[index].a[index2];
                            langLa_ia3.d -= 10;
                            array2[index].a[index2].b = reader3.readShort();
                        }
                    }
                }
                DataImgEntity[i].array2 = array2;
                for (int index = 0; index < this.NpcTemplate.length; ++index) {
                    if (this.NpcTemplate[index].indexData == i) {
                        this.NpcTemplate[index].width = short2;
                        this.NpcTemplate[index].height = short3;
                        this.NpcTemplate[index].b = array2;
                        this.NpcTemplate[index].a = array;
                    }
                }
                for (int index = 0; index < this.MobTemplate.length; ++index) {
                    if (this.MobTemplate[index].indexData == i) {
                        this.MobTemplate[index].width = short2;
                        this.MobTemplate[index].height = short3;
                        switch (this.MobTemplate[index].indexData) {
                            case 47: {
                                MobTemplate mobTemplate3 = this.MobTemplate[index];
                                mobTemplate3.height -= 5;
                                break;
                            }
                            case 48: {
                                MobTemplate mobTemplate4 = this.MobTemplate[index];
                                mobTemplate4.height -= 50;
                                break;
                            }
                            case 49:
                            case 151:
                            case 193: {
                                MobTemplate mobTemplate5 = this.MobTemplate[index];
                                mobTemplate5.height -= 22;
                                break;
                            }
                            case 52:
                            case 227: {
                                MobTemplate mobTemplate6 = this.MobTemplate[index];
                                mobTemplate6.height -= 7;
                                break;
                            }
                            case 28:
                            case 53: {
                                MobTemplate mobTemplate7 = this.MobTemplate[index];
                                mobTemplate7.height -= 9;
                                break;
                            }
                            case 54:
                            case 59:
                            case 177: {
                                MobTemplate mobTemplate8 = this.MobTemplate[index];
                                mobTemplate8.height -= 65;
                                break;
                            }
                            case 55:
                            case 190: {
                                MobTemplate mobTemplate9 = this.MobTemplate[index];
                                mobTemplate9.height -= 20;
                                break;
                            }
                            case 56: {
                                MobTemplate mobTemplate10 = this.MobTemplate[index];
                                mobTemplate10.height += 3;
                                break;
                            }
                            case 57: {
                                MobTemplate mobTemplate11 = this.MobTemplate[index];
                                mobTemplate11.height += 2;
                                break;
                            }
                            case 60: {
                                MobTemplate mobTemplate12 = this.MobTemplate[index];
                                mobTemplate12.height += 5;
                                break;
                            }
                            case 63: {
                                MobTemplate mobTemplate13 = this.MobTemplate[index];
                                mobTemplate13.height -= 34;
                                break;
                            }
                            case 26: {
                                MobTemplate mobTemplate14 = this.MobTemplate[index];
                                mobTemplate14.height -= 20;
                                break;
                            }
                            case 9:
                            case 123: {
                                MobTemplate mobTemplate15 = this.MobTemplate[index];
                                mobTemplate15.height -= 4;
                                break;
                            }
                            case 157:
                            case 169:
                            case 170: {
                                MobTemplate mobTemplate16 = this.MobTemplate[index];
                                mobTemplate16.height -= 8;
                                break;
                            }
                            case 154:
                            case 155:
                            case 156:
                            case 158:
                            case 195:
                            case 220:
                            case 224:
                            case 226: {
                                MobTemplate mobTemplate17 = this.MobTemplate[index];
                                mobTemplate17.height -= 2;
                                break;
                            }
                            case 58:
                            case 124: {
                                MobTemplate mobTemplate18 = this.MobTemplate[index];
                                mobTemplate18.height -= 10;
                                break;
                            }
                            case 178:
                            case 192:
                            case 197:
                            case 198: {
                                MobTemplate mobTemplate19 = this.MobTemplate[index];
                                mobTemplate19.height -= 5;
                                break;
                            }
                            case 191:
                            case 196:
                            case 212:
                            case 225: {
                                MobTemplate mobTemplate20 = this.MobTemplate[index];
                                mobTemplate20.height -= 15;
                                break;
                            }
                            case 217: {
                                MobTemplate mobTemplate21 = this.MobTemplate[index];
                                mobTemplate21.height += 13;
                                break;
                            }
                        }
                        this.MobTemplate[index].b = array2;
                        this.MobTemplate[index].a = array;
                    }
                }
            }
            this.ai.clear();
            this.ai.add("iconClient.zip");
            this.ai.add("iconChar.zip");
            Reader reader4;
            ArrayServer[] arrayServer = new ArrayServer[(reader4 = readerArrDataGame).readByte()];
           // Log.debug("arrayServer: " + arrayServer.length);
            for (int index = 0; index < arrayServer.length; ++index) {
                arrayServer[index] = new ArrayServer();
                arrayServer[index].nameServers = reader4.readUTF();
                arrayServer[index].servers = new Server[reader4.readByte()];
                for (int index2 = 0; index2 < arrayServer[index].servers.length; ++index2) {
                    arrayServer[index].servers[index2] = new Server();
                    arrayServer[index].servers[index2].id = reader4.readShort();
                    arrayServer[index].servers[index2].name = reader4.readUTF();
                    arrayServer[index].servers[index2].ip = reader4.readUTF();
                    arrayServer[index].servers[index2].port = reader4.readShort();
                    arrayServer[index].servers[index2].portCheck = reader4.readShort();
                }
            }
            Reader reader5;
            int[] au = new int[(reader5 = readerArrDataGame).readInt()];
            for (int index = 0; index < au.length; ++index) {
                au[index] = reader5.readShort();
            }
            this.au = au;
            //  Log.debug("au: " + au.length);
            this.bacKhoaGhepDa = readArrayInt(readerArrDataGame);
            this.bacKhoaUpgradeVuKhi = readArrayInt(readerArrDataGame);
            this.bacKhoaUpgradeTrangBi = readArrayInt(readerArrDataGame);
            this.bacKhoaUpgradePhuKien = readArrayInt(readerArrDataGame);
            this.pointGhepDa = readArrayLong(readerArrDataGame);
            this.pointUpgradeVuKhi = readArrayLong(readerArrDataGame);
            this.pointUpgradeTrangBi = readArrayLong(readerArrDataGame);
            this.pointUpgradePhuKien = readArrayLong(readerArrDataGame);
            this.ngocKhamUpgrade = readArrayInt(readerArrDataGame);
            this.dataTreoCho[0] = readArrayString(readerArrDataGame);
            this.dataTreoCho[1] = readArrayString(readerArrDataGame);
            this.exps = readArrayLong(readerArrDataGame);

            bacKhoaUpgradeVuKhi = Utlis.addNewValues(bacKhoaUpgradeVuKhi, newBacKhoaUpgradeVuKhi);
            bacKhoaUpgradeTrangBi = Utlis.addNewValues(bacKhoaUpgradeTrangBi, newBacKhoaUpgradeTrangBi);
            bacKhoaUpgradePhuKien = Utlis.addNewValues(bacKhoaUpgradePhuKien, newBacKhoaUpgradePhuKien);
            pointUpgradeVuKhi = Utlis.addNewValues(pointUpgradeVuKhi, newPointUpgradeVuKhi);
            pointUpgradeTrangBi = Utlis.addNewValues(pointUpgradeTrangBi, newPointUpgradeTrangBi);
            pointUpgradePhuKien = Utlis.addNewValues(pointUpgradePhuKien, newPointUpgradePhuKien);
            pointGhepDa = Utlis.addNewValues(pointGhepDa, newPointGhepDa);
            bacKhoaGhepDa = Utlis.addNewValues(bacKhoaGhepDa, newBacKhoaGhepDa);

            //System.out.println("bacKhoaGhepDa: " + Arrays.toString(pointGhepDa));

            this.dataGiftQuaySo = new int[readerArrDataGame.readByte()][];
            for (int index = 0; index < this.dataGiftQuaySo.length; ++index) {
                this.dataGiftQuaySo[index] = readArrayInt(readerArrDataGame);
            }
            this.vSkillClan.clear();
            try {
                for (byte size = readerArrDataGame.readByte(), index = 0; index < size; ++index) {
                    SkillClan skillClan;
                    (skillClan = new SkillClan()).id = readerArrDataGame.readByte();
                    skillClan.name = readerArrDataGame.readUTF();
                    skillClan.detail = readerArrDataGame.readUTF();
                    skillClan.levelNeed = readerArrDataGame.readUnsignedByte();
                    skillClan.strOptions = readerArrDataGame.readUTF();
                    skillClan.idIcon = readerArrDataGame.readShort();
                    this.vSkillClan.add(skillClan);
                }
            } catch (Exception ex4) {
            }
            this.typeArr = readerArrDataGame.readByte();
            byte[] data = Binary.read("arr_data_game2");
            Log.debug("size data "+data.length);
            writerArrDataGame2 = new Writer();            
            for (int i = 71440; i < data.length; i++) {
                writerArrDataGame2.writeByte(data[i]);

            }
            this.readArrDataGame2(new Message((byte) 0, data)); // ở đây đọc ct
//            for (int n11 = 0; n11 < this.Skill.length; n11 = (short) (n11 + 1)) {
//                
//                Skill[n11].getItemOption();
//            }
        } catch (Exception ex5) {
            ex5.printStackTrace();
            byte[] c;
            if ((c = Binary.c(gI().aU + "arr_data_game.bin")) != null) {
                Binary.write("arr_data_game", c);
                this.readArrDataGame(b);
            }
        } finally {
            if (readerArrDataGame != null) {
                readerArrDataGame.close();
            }
            this.ba = true;
        }
    }
    
    public static class DataImgEntity {
        
        private short s2;
        private short s3;
        private byte[][] array;
        private LangLa_hz[] array2;
        
    }
    
    private static int[] readArrayInt(Reader reader) throws java.io.IOException {
        int[] array = new int[reader.readInt()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = reader.readInt();
        }
        return array;
    }
    
    private static long[] readArrayLong(Reader reader) throws java.io.IOException {
        long[] array = new long[reader.readInt()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = reader.readLong();
        }
        return array;
    }
    
    private static String[] readArrayString(Reader reader) throws java.io.IOException {
        String[] array = new String[reader.readInt()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = reader.readUTF();
        }
        return array;
    }
    
    public static ArrayList h() {
        ArrayList<short[]> vector = new ArrayList<>();
        for (int i = 0; i < gI().dataWayPoint.length; ++i) {
            vector.add(gI().dataWayPoint[i]);
        }
        return vector;
    }
    
    public Skill getSkillWithIdAndLevel(int id, int level) {
        for (int i = 0; i < this.Skill.length; i++) {
            if (this.Skill[i].idTemplate == id && this.Skill[i].level == level) {
                return this.Skill[i].cloneSkill();
            }
            
        }
        return null;
    }
    
    public static int GetLevelFormExp(long var1) {      
        int var3;
        for (var3 = 0; var3 < DataCenter.gI().exps.length && var1 >= DataCenter.gI().exps[var3]; ++var3) {
            var1 -= DataCenter.gI().exps[var3];
        }
        
        return var3;
    }
    
    public static long GetExpFormLevel(int lv) {
        
        long l = 0;
        int var3;
        for (var3 = 0; var3 < DataCenter.gI().exps.length && var3 < lv; ++var3) {
            l += DataCenter.gI().exps[var3];
        }
        
        return l;
    }
}
