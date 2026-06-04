/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg188.server;

import com.sg188.lib.Log;
import com.sg188.lib.Utlis;
import com.tgame.model.Caption;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author admin
 */
public class Test {

    public static class test2 {

        int CXtuhY;

        public final String toString() {
            byte[] arrayOfByte = new byte[29];
            this.CXtuhY = -1305212728;
            arrayOfByte[0] = (byte) (this.CXtuhY >>> 16);
            this.CXtuhY = 898807971;
            arrayOfByte[1] = (byte) (this.CXtuhY >>> 24);
            this.CXtuhY = -959138304;
            arrayOfByte[2] = (byte) (this.CXtuhY >>> 21);
            this.CXtuhY = -898419794;
            arrayOfByte[3] = (byte) (this.CXtuhY >>> 12);
            this.CXtuhY = -1818080043;
            arrayOfByte[4] = (byte) (this.CXtuhY >>> 2);
            this.CXtuhY = -796224309;
            arrayOfByte[5] = (byte) (this.CXtuhY >>> 1);
            this.CXtuhY = -1520256420;
            arrayOfByte[6] = (byte) (this.CXtuhY >>> 16);
            this.CXtuhY = 221399040;
            arrayOfByte[7] = (byte) (this.CXtuhY >>> 16);
            this.CXtuhY = 1851098842;
            arrayOfByte[8] = (byte) (this.CXtuhY >>> 5);
            this.CXtuhY = -1795175219;
            arrayOfByte[9] = (byte) (this.CXtuhY >>> 1);
            this.CXtuhY = -1140442420;
            arrayOfByte[10] = (byte) (this.CXtuhY >>> 12);
            this.CXtuhY = -432117991;
            arrayOfByte[11] = (byte) (this.CXtuhY >>> 20);
            this.CXtuhY = -157727993;
            arrayOfByte[12] = (byte) (this.CXtuhY >>> 14);
            this.CXtuhY = -1060459380;
            arrayOfByte[13] = (byte) (this.CXtuhY >>> 17);
            this.CXtuhY = -1688338161;
            arrayOfByte[14] = (byte) (this.CXtuhY >>> 23);
            this.CXtuhY = 1716535914;
            arrayOfByte[15] = (byte) (this.CXtuhY >>> 20);
            this.CXtuhY = 1254569074;
            arrayOfByte[16] = (byte) (this.CXtuhY >>> 12);
            this.CXtuhY = 1873765131;
            arrayOfByte[17] = (byte) (this.CXtuhY >>> 8);
            this.CXtuhY = -660925417;
            arrayOfByte[18] = (byte) (this.CXtuhY >>> 15);
            this.CXtuhY = 1099308469;
            arrayOfByte[19] = (byte) (this.CXtuhY >>> 12);
            this.CXtuhY = -959348554;
            arrayOfByte[20] = (byte) (this.CXtuhY >>> 21);
            this.CXtuhY = 86347823;
            arrayOfByte[21] = (byte) (this.CXtuhY >>> 10);
            this.CXtuhY = -233133430;
            arrayOfByte[22] = (byte) (this.CXtuhY >>> 15);
            this.CXtuhY = -1771399788;
            arrayOfByte[23] = (byte) (this.CXtuhY >>> 20);
            this.CXtuhY = -400331195;
            arrayOfByte[24] = (byte) (this.CXtuhY >>> 12);
            this.CXtuhY = -711559576;
            arrayOfByte[25] = (byte) (this.CXtuhY >>> 18);
            this.CXtuhY = 936406340;
            arrayOfByte[26] = (byte) (this.CXtuhY >>> 24);
            this.CXtuhY = -1609693712;
            arrayOfByte[27] = (byte) (this.CXtuhY >>> 14);
            this.CXtuhY = 61244676;
            arrayOfByte[28] = (byte) (this.CXtuhY >>> 13);
            return new String(arrayOfByte);
        }
    }

    public static int count = -2;

    public static String getTextTimeFormSeconds(int var0) {
        int var1 = 0;
        if (var0 > 60) {
            var1 = var0 / 60;
            var0 %= 60;
        }

        int var2 = 0;
        if (var1 > 60) {
            var2 = var1 / 60;
            var1 %= 60;
        }

        int var3 = 0;
        if (var2 > 24) {
            var3 = var2 / 24;
            var2 %= 24;
        }

        String var4 = "";
        if (var3 > 0) {
            var4 = var4 + var3;
            var4 = var4 + " " + Caption.jZ;
            var4 = var4 + var2 + " " + Caption.ka;
        } else if (var2 > 0) {
            var4 = var4 + var2;
            var4 = var4 + " " + Caption.kc;
            var4 = var4 + var1 + " " + Caption.kb;
        } else {
            if (var1 > 9) {
                var4 = var4 + var1;
            } else {
                var4 = var4 + "0" + var1;
            }

            var4 = var4 + ":";
            if (var0 > 9) {
                var4 = var4 + var0;
            } else {
                var4 = var4 + "0" + var0;
            }
        }

        return var4;
    }


//    public static void main(String[] a) {
//
//        String query = "SELECT * FROM player";
//
//        try (
//                // Kết nối đến cơ sở dữ liệu
//                Connection connection = Connect.getConnection();
//
//                // Tạo một PreparedStatement
//                PreparedStatement preparedStatement = connection.prepareStatement(query);
//
//                // Thực thi câu lệnh SQL và lấy kết quả
//                ResultSet resultSet = preparedStatement.executeQuery()) {
//
//            // Tạo một danh sách để lưu trữ các đối tượng Player
//            List<Char> players = new ArrayList<>();
//
//            // Xử lý kết quả
//            while (resultSet.next()) {
//               Char pl = new Char();
//                ObjectMapper s = new ObjectMapper();
//                pl.Info = s.readValue(resultSet.getString("info"), InfoChar.class);
//                pl.Bag = s.readValue(resultSet.getString("bag"), InfoInventory.class);
//                pl.Point = s.readValue(resultSet.getString("point"), InfoPoint.class);
//                pl.Skill = s.readValue(resultSet.getString("skill"), InfoSkill.class);
//                pl.TuongKhac = s.readValue(resultSet.getString("tuongkhac"), InfoTuongKhac.class);
//            }
//
//            // In thông tin của các đối tượng Player
//            int i=0;
//            for (Char player : players) {
//                player.removeItemBug();
//                i++;
//            }
//            for(Char player: players){
//                CharDB.updateDB(player);
//            }
//            System.out.println("Clear "+i);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } catch (JsonMappingException e) {
//        } catch (JsonProcessingException e) {
//        }
//    }


    public static String toString(Object[] a) {
        if (a == null) {
            return "null";
        }

        int iMax = a.length - 1;
        if (iMax == -1) {
            return "[]";
        }

        StringBuilder b = new StringBuilder();

        for (int i = 0; ; i++) {
            b.append(count + ". " + String.valueOf(a[i]));
            count++;
            if (i == iMax) {
                return b.toString();
            }
            b.append("\n");
        }
    }

    public static byte[] icon(int id) {
        HttpURLConnection var6 = null;
        BufferedInputStream var7 = null;
        ByteArrayOutputStream var1 = null;
        int[] var3 = new int[1];
        byte[] var2 = null;
        byte[] var8 = null;
        try {
            // for (int i = 0; i < 8528; i++) {
            (var6 = (HttpURLConnection) (new URL("http://localhost/img/zoom2/iconchar/" + id + ".png")).openConnection()).setConnectTimeout(Utlis.timeOut);
            // }
            var7 = new BufferedInputStream(var6.getInputStream());
            var1 = new ByteArrayOutputStream();
            byte[] var9 = new byte[524288];

            int var10;
            while ((var10 = var7.read(var9)) != -1) {
                var1.write(var9, 0, var10);
                var3[0] += var10;
                if (var3[0] > 60000000) {
                    var1.flush();
                    var2 = var1.toByteArray();
                    var1.close();
                    var1 = new ByteArrayOutputStream();
                }
            }

            var1.flush();
            if (var2 == null) {
                var8 = var1.toByteArray();
            } else {
                var9 = var1.toByteArray();
                var8 = new byte[var2.length + var9.length];
                System.arraycopy(var2, 0, var8, 0, var2.length);
                System.arraycopy(var9, 0, var8, var2.length, var9.length);
            }
            var6.disconnect();
            var1.close();
            var7.close();
            Log.debug("byte data " + var8.length);
        } catch (MalformedURLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return var8;
    }

}
