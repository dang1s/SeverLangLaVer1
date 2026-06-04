/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfoChar;

import Data.DataSkill;
import com.sg188.data.Skill;

/**
 *
 * @author ADMIN
 */
public class InfoSkill {

    public Skill[] arraySkill;
    public Skill skillFight;
    public InfoSkill()
    {
        
    }
    public InfoSkill(byte idClass) {

        Skill[][] _arraySkill = new Skill[][]{
            DataSkill.skills_0.clone(),
            DataSkill.skills_1.clone(),
            DataSkill.skills_2.clone(),
            DataSkill.skills_3.clone(),
            DataSkill.skills_4.clone(),
            DataSkill.skills_5.clone(),};

        arraySkill = _arraySkill[idClass];

        skillFight = arraySkill[0];
    }
}
