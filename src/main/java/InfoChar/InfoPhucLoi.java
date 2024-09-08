package InfoChar;


import com.sg188.PhucLoi.TemplatePL;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InfoPhucLoi {
    public int timeOnline;
    public int soNgayOnline;
    public int tieuNgay;
    public int tieuTuan;
    public boolean goiChiTon;
    public boolean goiHaoHoa;
    public int napNgay;
    public int napDon;
    public int nap3moc;
    public int napTuan;
    public int napLienTuc;
    public long theThang =-1 ;
    public long theVinhVien=-1;
    public List<TemplatePL>listPl = new ArrayList<>();
    public List<Integer>listnap= new ArrayList<>();
    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        obj.put("timeOnline", this.timeOnline);
        obj.put("songayonline", this.soNgayOnline);
        obj.put("tieungay", this.tieuNgay);
        obj.put("tieutuan", this.tieuTuan);
        obj.put("goichiton", this.goiChiTon);
        obj.put("goihaohoa", this.goiHaoHoa);
        obj.put("napngay", this.napNgay);
        obj.put("naptuan", this.napTuan);
        obj.put("naplientuc", this.napLienTuc);
        obj.put("thethang", this.theThang);
        obj.put("thevinhvien", this.theVinhVien);
        obj.put("napdon", this.napDon);
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(listnap);
        obj.put("listnap",jsonArray);
        return obj;
    }
}
