package com.sg188.data;

import com.sg188.lib.Log;
import com.sg188.lib.Utlis;
import com.sg188.real.Item;
import org.json.simple.JSONObject;

public class SkillClan {
   public int id;
   public String name;
   public String detail;
   public int levelNeed;
   public String strOptions;
   public int idIcon;
   public int moneyBuy;

   public SkillClan a() {
      SkillClan var1;
      (var1 = new SkillClan()).id = this.id;
      var1.name = this.name;
      var1.detail = this.detail;
      var1.levelNeed = this.levelNeed;
      var1.strOptions = this.strOptions;
      var1.idIcon = this.idIcon;
      var1.moneyBuy = this.moneyBuy;
      return var1;
   }

   public JSONObject toJSONObject() {
      JSONObject obj = new JSONObject();
      obj.put("id",this.id);
      obj.put("name", this.name);
      obj.put("levelNeed", this.levelNeed);
      obj.put("strOptions", this.strOptions);
      return obj;
   }
      public ItemOption[] getOption() {
      if (this.strOptions != null && this.strOptions.length() > 0) {
         String[] var1;
         ItemOption[] var2 = new ItemOption[(var1 = Utlis.split(this.strOptions, ";")).length];

         for(int var3 = 0; var3 < var1.length; ++var3) {
            var2[var3] = new ItemOption(var1[var3]);
         }

         return var2;
      } else {
         return null;
      }
   }

   public void a(int var1) {
      this.levelNeed = var1;
      for (int i = 0; i < DataCenter.gI().vSkillClan.size(); i++) {
         SkillClan skillClan = (SkillClan) DataCenter.gI().vSkillClan.get(i);
         if(skillClan.id == id){
            this.strOptions = skillClan.strOptions;
            break;
         }
      }
      if (this.strOptions != null && this.strOptions.length() > 0) {
         String[] var2;
         ItemOption[] var3 = new ItemOption[(var2 = Utlis.split(this.strOptions, ";")).length];

         for(int var4 = 0; var4 < var2.length; ++var4) {
            var3[var4] = new ItemOption(var2[var4]);
            var3[var4].d(var3[var4].a[1] * var1);
         }

         this.strOptions = Item.a(var3);
      }
   }

}
