package com.sg188.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EffectTemplate {
   public byte id;
   public String name;
   public String detail;
   public short type;
   public short idIcon;
   public short idMob;

    @Override
    public String toString() {
        return "ID : "+id +" ; TYPE : "+type+ " ; NAME : "+name + " ; INFO : "+detail;
    }
    @JsonCreator
    public EffectTemplate(@JsonProperty("id") int id,
                          @JsonProperty("name") String name,
                          @JsonProperty("detail") String detail,
                          @JsonProperty("type") int type,
                          @JsonProperty("idIcon") int idIcon,
                          @JsonProperty("idMob") int idMob) {
        this.id = (byte) id;
        this.name = name;
        this.detail = detail;
        this.type = (short) type;
        this.idIcon = (short) idIcon;
        this.idMob = (short) idMob;
    }


    public EffectTemplate(int var1) {
      this.id = (byte)var1;
   }

}
