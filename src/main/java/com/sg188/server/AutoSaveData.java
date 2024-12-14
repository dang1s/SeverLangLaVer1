package com.sg188.server;

import EventClick.ClickTop;
import SqlConnection.CharDB;
import com.sg188.clan.Clan;
import com.sg188.lib.Log;
import com.sg188.real.Char;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class  AutoSaveData implements Runnable {
    @Override
    public void run() {
        while (!Main.BaoTri) {
            try {
                Thread.sleep(60*1000*5);
                for(Char pl: ServerManager.getChars()){
                    if(pl != null && pl.user != null){
                        pl.flush();
                    }
                }

                Log.info("Lưu data tự động");
                ClickTop.cTop = CharDB.getTop((byte) 0);
                ClickTop.cTaiPhu = CharDB.getTopTaiPhu();
                ClickTop.cCuaCai = CharDB.getTopCuaCai();
                ClickTop.cNhiDong = CharDB.getTopNhiDong((byte) 0);
                ClickTop.cChuyenCan = CharDB.getTopChuyenCan();
                ClickTop.cNapTuan = CharDB.getTopNapTuan();
                ClickTop.cNhiDongTaiPhu = CharDB.getTopNhiDongTaiPhu();

                Main.sendRandomMessage();

//                try {
//                    List<Clan> clans = Clan.getClanDAO().getAll();
//                    synchronized (clans) {
//                        for (Clan clan : clans) {
//                            Clan.getClanDAO().update(clan);
//                        }
//                    }
//                    Log.debug("Hoan tat luu data clan");
//                }catch (Exception e){
//                   Log.error("error save data clan "+e);
//                }
            } catch (InterruptedException ex) {
                Logger.getLogger(AutoSaveData.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
}
