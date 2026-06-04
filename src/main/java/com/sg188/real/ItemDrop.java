package com.sg188.real;

import com.sg188.lib.RandomCollection;

public class ItemDrop {
    public static final RandomCollection<Integer> ITEM_MAP = new RandomCollection<>();
    public static final RandomCollection<Integer> ITEM_TUI_MAY_MAN = new RandomCollection<>();
    public static final RandomCollection<Integer> ITEM_TUI_MAY_MAN3 = new RandomCollection<>();
    public static final RandomCollection<Integer> ITEM_LANG_CO = new RandomCollection<>();
    public static final RandomCollection<Integer> ITEM_HANG_VI_THU = new RandomCollection<>();
    public static void Init(){
        ITEM_MAP.add(0.2,164);
        ITEM_MAP.add(2,4);
        ITEM_MAP.add(3, 3);
        ITEM_MAP.add(3, 17);
        ITEM_MAP.add(1, 5);
        ITEM_MAP.add(1, 5);
        ITEM_MAP.add(170, -1);

        ITEM_TUI_MAY_MAN.add(10,2);
        ITEM_TUI_MAY_MAN.add(8,3);
        ITEM_TUI_MAY_MAN.add(5,4);
        ITEM_TUI_MAY_MAN.add(1,163);
        ITEM_TUI_MAY_MAN.add(10,160);


//        ITEM_TUI_MAY_MAN3.add(0.01,966);
//        ITEM_TUI_MAY_MAN3.add(5,1005);
//        ITEM_TUI_MAY_MAN3.add(1,823);
//        ITEM_TUI_MAY_MAN3.add(10,163);
//        ITEM_TUI_MAY_MAN3.add(5,11);
//        ITEM_TUI_MAY_MAN3.add(2,353);
//        ITEM_TUI_MAY_MAN3.add(2,567);
//        ITEM_TUI_MAY_MAN3.add(2,565);
//        ITEM_TUI_MAY_MAN3.add(2,563);







        ITEM_LANG_CO.add(1,434); // langco
        ITEM_LANG_CO.add(0.5,562);
        ITEM_LANG_CO.add(0.5,564);
        ITEM_LANG_CO.add(0.5,566);
        ITEM_LANG_CO.add(0.05,563);
        ITEM_LANG_CO.add(0.05,565);
        ITEM_LANG_CO.add(0.05,567);
        ITEM_LANG_CO.add(0.05,294);
        ITEM_LANG_CO.add(5,310);
        ITEM_LANG_CO.add(5,754);

//        ITEM_LANG_CO.add(0.5,1051);
//        ITEM_LANG_CO.add(0.5,1052);
        ITEM_LANG_CO.add(0.5,179);
//        ITEM_LANG_CO.add(0.5,216);
//        ITEM_LANG_CO.add(0.5,217);
//        ITEM_LANG_CO.add(0.5,218);
//        ITEM_LANG_CO.add(0.5,248);
//        ITEM_LANG_CO.add(0.5,278);
//        ITEM_LANG_CO.add(0.5,302);
//        ITEM_LANG_CO.add(0.5,315);
//        ITEM_LANG_CO.add(100,-1);

        ITEM_HANG_VI_THU.add(1,434); // hangvithu
        ITEM_HANG_VI_THU.add(0.5,562);
        ITEM_HANG_VI_THU.add(0.5,564);
        ITEM_HANG_VI_THU.add(0.5,566);
        ITEM_HANG_VI_THU.add(0.05,563);
        ITEM_HANG_VI_THU.add(0.05,565);
        ITEM_HANG_VI_THU.add(0.05,567);
        ITEM_HANG_VI_THU.add(0.05,294);
        ITEM_HANG_VI_THU.add(5,310);
        ITEM_HANG_VI_THU.add(5,754);
        ITEM_HANG_VI_THU.add(0.5,179);
    }

}
