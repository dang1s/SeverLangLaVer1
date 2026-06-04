/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Service;

/**
 *
 * @author ADMIN
 */
public  class HanderService {
    
    private static HanderService I;
    public static HanderService gI()
    {
        return I == null ? new HanderService() : I;
    }
    public static int getItemCanGiao(int idTask,int idStep){
        switch (idTask){
            case 1:
                if(idStep ==1)
                return 378;
            case 2:
                if(idStep ==1)
                    return 194;
            case 3:
                if(idStep ==4)
                    return 379;
            case 11:
                if(idStep ==5)
                    return 382;
            case 15:
                if(idStep ==3)
                    return 195;
            case 16:
                if(idStep ==1)
                    return 392;
            case 17:
                if(idStep ==1)
                    return 399;
        }
        return -1;
    }
    
}
