/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfoChar;

import com.sg188.real.Char;
import com.sg188.real.Item;

/**
 *
 * @author ADMIN
 */
public class InfoGame {


    /**
     * ********** PK ***************
     */
    public byte statusGD = 0;
    public boolean isDie = false;
    public byte TypePk;



    /* ODER MENU
     
     */
    public boolean isOderMenu;
    /*GIA TOC
    

     */
    public boolean isScheduledForRespawn = false;



    public void clenOder() {
        isOderMenu = false;
    }

}
