package com.sg188.real;

import com.sg188.lib.RandomCollection;

public class ItemDrop {
    public static final RandomCollection<Integer> ITEM_MAP = new RandomCollection<>();
    public static final RandomCollection<Integer> ITEM_TUI_MAY_MAN = new RandomCollection<>();
    public static final RandomCollection<Integer> ITEM_LANG_CO = new RandomCollection<>();
    public static void Init(){
        ITEM_MAP.add(5,164);
        ITEM_MAP.add(70,-1);

        ITEM_TUI_MAY_MAN.add(10,2);
        ITEM_TUI_MAY_MAN.add(8,3);
        ITEM_TUI_MAY_MAN.add(5,4);
        ITEM_TUI_MAY_MAN.add(1,163);

        ITEM_LANG_CO.add(10,434);
        ITEM_LANG_CO.add(0.5,562);
        ITEM_LANG_CO.add(0.5,564);
        ITEM_LANG_CO.add(0.5,566);
        ITEM_LANG_CO.add(0.05,563);
        ITEM_LANG_CO.add(0.05,565);
        ITEM_LANG_CO.add(0.05,567);
        ITEM_LANG_CO.add(0.05,294);
        ITEM_LANG_CO.add(5,310);
        ITEM_LANG_CO.add(5,754);

        ITEM_LANG_CO.add(0.5,174);
        ITEM_LANG_CO.add(0.5,175);
        ITEM_LANG_CO.add(0.5,179);
        ITEM_LANG_CO.add(0.5,216);
        ITEM_LANG_CO.add(0.5,217);
        ITEM_LANG_CO.add(0.5,218);
        ITEM_LANG_CO.add(0.5,248);
        ITEM_LANG_CO.add(0.5,278);
        ITEM_LANG_CO.add(0.5,302);
        ITEM_LANG_CO.add(0.5,315);
        ITEM_LANG_CO.add(150,-1);
    }

}
