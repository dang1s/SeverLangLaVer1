package InfoChar;

import org.json.simple.JSONObject;

public class PointHokage {
    public short Dai;
    public short Ao;
    public short BaoTay;
    public short Quan;
    public short Giay;
    public short Vk;
    public short Day;
    public short Moc;
    public short OngTieu;
    public short Tui;
    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        obj.put("dai", this.Dai);
        obj.put("ao", this.Ao);
        obj.put("baotay", this.BaoTay);
        obj.put("quan", this.Quan);
        obj.put("giay", this.Giay);
        obj.put("vk", this.Vk);
        obj.put("day", this.Day);
        obj.put("moc", this.Moc);
        obj.put("ongtieu", this.OngTieu);
        obj.put("tui", this.Tui);
        return obj;
    }

}
