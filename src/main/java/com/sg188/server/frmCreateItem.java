/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg188.server;

import com.sg188.data.DataCenter;
import com.sg188.data.ItemOption;
import com.sg188.lib.mArrays;
import com.sg188.real.Item;
import com.sg188.real.Item.LangLa_gp;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import test.AutoCompletion;

/**
 *
 * @author admin
 */
/*
id=0, name=Hp: +#, type=0, level=0, strOption=20;20;20;20;20;20;20;25;25;25;25;30;30;40;40;100;200;200;400;0
id=1, name=Mp: +#, type=0, level=0, strOption=20;20;20;20;20;20;20;25;25;25;25;30;30;40;40;100;200;200;400;0
id=2, name=Tấn công: +#, type=0, level=0, strOption=50;50;50;50;50;50;50;70;70;70;70;90;90;110;110;150;200;200;400;0
id=3, name=Sát thương quái: +#, type=0, level=0, strOption=20;20;20;20;20;20;20;25;25;25;25;30;30;40;40;100;150;150;300;0
id=4, name=Bỏ qua né tránh: +#, type=0, level=0, strOption=20;20;20;20;20;20;20;25;25;25;25;30;30;40;40;100;150;150;300;0
id=5, name=Chí mạng: +#, type=0, level=0, strOption=10;10;10;10;10;10;10;15;15;15;15;20;20;25;25;40;60;60;120;0
id=6, name=Sát thương chuyển thành hồi phục Hp: +#%, type=0, level=0, strOption=1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;2;0
id=7, name=Kháng lôi: +#, type=2, level=0, strOption=5;5;5;5;5;5;5;10;10;10;10;15;15;20;20;30;40;40;80;0
id=8, name=Kháng thổ: +#, type=2, level=0, strOption=5;5;5;5;5;5;5;10;10;10;10;15;15;20;20;30;40;40;80;0
id=9, name=Kháng thủy: +#, type=2, level=0, strOption=5;5;5;5;5;5;5;10;10;10;10;15;15;20;20;30;40;40;80;0
id=10, name=Kháng hỏa: +#, type=2, level=0, strOption=5;5;5;5;5;5;5;10;10;10;10;15;15;20;20;30;40;40;80;0
id=11, name=Kháng phong: +#, type=2, level=0, strOption=5;5;5;5;5;5;5;10;10;10;10;15;15;20;20;30;40;40;80;0
id=12, name=Kháng tất cả: +#, type=2, level=0, strOption=5;5;5;5;5;5;5;10;10;10;10;15;15;20;20;30;40;40;80;0
id=13, name=Giảm trừ sát thương: +#, type=2, level=0, strOption=5;5;5;5;5;5;5;10;10;10;10;15;15;20;20;30;40;40;80;0
id=14, name=Né tránh: +#, type=2, level=0, strOption=10;10;10;10;10;10;10;15;15;15;15;20;20;25;25;40;60;60;120;0
id=15, name=Chí mạng: +#, type=2, level=0, strOption=10;10;10;10;10;10;10;15;15;15;15;20;20;25;25;40;60;60;120;0
id=16, name=Phản đòn: +#, type=2, level=0, strOption=1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;2;0
id=17, name=(+4) Tốc độ di chuyển: +#, type=3, level=10, strOption=
id=18, name=Hp tối đa: +#, type=2, level=0, strOption=20;20;20;20;20;20;20;25;25;25;25;30;30;40;40;100;200;200;400;0
id=19, name=Mp tối đa: +#, type=2, level=0, strOption=20;20;20;20;20;20;20;25;25;25;25;30;30;40;40;100;200;200;400;0
id=20, name=Chính xác: +#, type=2, level=0, strOption=20;20;20;20;20;20;20;25;25;25;25;30;30;40;40;100;150;150;300;0
id=21, name=Tăng tấn công lên Hệ Lôi: +#, type=2, level=0, strOption=10;10;10;10;10;10;10;15;15;15;15;20;20;25;25;40;60;60;120;0
id=22, name=Tăng tấn công lên Hệ Thổ: +#, type=2, level=0, strOption=10;10;10;10;10;10;10;15;15;15;15;20;20;25;25;40;60;60;120;0
id=23, name=Tăng tấn công lên hệ Thủy: +#, type=2, level=0, strOption=10;10;10;10;10;10;10;15;15;15;15;20;20;25;25;40;60;60;120;0
id=24, name=Tăng tấn công lên hệ Hỏa: +#, type=2, level=0, strOption=10;10;10;10;10;10;10;15;15;15;15;20;20;25;25;40;60;60;120;0
id=25, name=Tăng tấn công lên Hệ Phong: +#, type=2, level=0, strOption=10;10;10;10;10;10;10;15;15;15;15;20;20;25;25;40;60;60;120;0
id=26, name=(+4) Mỗi nữa giây phục hồi Hp: +#, type=3, level=10, strOption=
id=27, name=(+4) Mỗi nữa giây phục hồi Mp: +#, type=3, level=10, strOption=
id=28, name=(+8) Chí mạng: +#, type=4, level=20, strOption=
id=29, name=(+8) Hp tối đa: +#, type=4, level=20, strOption=
id=30, name=(+8) Mp tối đa: +#, type=4, level=20, strOption=
id=31, name=(+12) Tấn công cơ bản của vũ khí: +#, type=5, level=30, strOption=
id=32, name=(+12) Tỉ lệ Hp tối đa: +#%, type=5, level=30, strOption=
id=33, name=(+12) Tỉ lệ Mp tối đa: +#%, type=5, level=30, strOption=
id=34, name=(+14) Phát huy lực tấn công cơ bản: +#%, type=6, level=40, strOption=
id=35, name=(+14) Kháng lôi: +#, type=6, level=40, strOption=
id=36, name=(+14) Kháng thổ: +#, type=6, level=40, strOption=
id=37, name=(+14) Kháng thủy: +#, type=6, level=40, strOption=
id=38, name=(+14) Kháng hỏa: +#, type=6, level=40, strOption=
id=39, name=(+14) Kháng phong: +#, type=6, level=40, strOption=
id=40, name=(+14) Kháng tất cả: +#, type=6, level=40, strOption=
id=41, name=(+14) Tấn công khi có chí mạng: +#%, type=6, level=40, strOption=
id=42, name=(+16) Giảm tấn công khi bị chí mạng: -#%, type=7, level=50, strOption=
id=43, name=(+16) Giảm tấn công khi bị chí mạng: -#%, type=7, level=50, strOption=
id=44, name=(+16) Giảm tấn công khi bị chí mạng: -#%, type=7, level=50, strOption=
id=45, name=(+16) Giảm tấn công khi bị chí mạng: -#%, type=7, level=50, strOption=
id=46, name=(+16) Giảm tấn công khi bị chí mạng: -#%, type=7, level=50, strOption=
id=47, name=(+16) Phát huy lực tấn công cơ bản: +#%, type=7, level=50, strOption=
id=48, name=(+4) Gây suy yếu: +#, type=3, level=10, strOption=
id=49, name=(+4) Gây trúng độc: +#, type=3, level=10, strOption=
id=50, name=(+4) Gây làm chậm: +#, type=3, level=10, strOption=
id=51, name=(+4) Gây bỏng: +#, type=3, level=10, strOption=
id=52, name=(+4) Gây choáng: +#, type=3, level=10, strOption=
id=53, name=Tương khắc lên hệ Lôi: #/@, type=1, level=0, strOption=
id=54, name=Tương khắc lên hệ Thổ: #/@, type=1, level=0, strOption=
id=55, name=Tương khắc lên hệ Thủy: #/@, type=1, level=0, strOption=
id=56, name=Tương khắc lên hệ Hỏa: #/@, type=1, level=0, strOption=
id=57, name=Tương khắc lên hệ Phong: #/@, type=1, level=0, strOption=
id=58, name=Giảm tương khắc của hệ Lôi: #/@, type=1, level=0, strOption=
id=59, name=Giảm tương khắc của hệ Thổ: #/@, type=1, level=0, strOption=
id=60, name=Giảm tương khắc của hệ Thủy: #/@, type=1, level=0, strOption=
id=61, name=Giảm tương khắc của hệ Hỏa: #/@, type=1, level=0, strOption=
id=62, name=Giảm tương khắc của hệ Phong: #/@, type=1, level=0, strOption=
id=63, name=Chí mạng: +#, type=50, level=0, strOption=10;10;10;10;10;10;10;10;10;10;20;20
id=64, name=Né tránh: +#, type=50, level=0, strOption=10;10;10;10;10;10;10;10;10;10;20;20
id=65, name=Chính xác: +#, type=50, level=0, strOption=10;10;10;10;10;10;10;10;10;10;20;20
id=66, name=Tăng kinh nghiệm đánh quái: +#%, type=50, level=0, strOption=3;3;3;3;3;3;3;3;3;3;5;5
id=67, name=Phản đòn: +#%, type=50, level=0, strOption=1;1;1;1;1;1;1;1;1;1;2;2
id=68, name=Gây suy yếu: +#, type=51, level=0, strOption=1;1;1;1;1;1;1;1;1;1;1;1
id=69, name=Gây trúng độc: +#, type=51, level=0, strOption=1;1;1;1;1;1;1;1;1;1;1;1
id=70, name=Gây làm chậm: +#, type=51, level=0, strOption=1;1;1;1;1;1;1;1;1;1;1;1
id=71, name=Gây bỏng:+#, type=51, level=0, strOption=1;1;1;1;1;1;1;1;1;1;1;1
id=72, name=Gây choáng: +#, type=51, level=0, strOption=1;1;1;1;1;1;1;1;1;1;1;1
id=73, name=Lôi độn: +#, type=50, level=0, strOption=50;50;50;50;50;50;50;70;70;70;100;100
id=74, name=Thổ độn: +#, type=50, level=0, strOption=50;50;50;50;50;50;50;70;70;70;100;100
id=75, name=Phong độn: +#, type=50, level=0, strOption=50;50;50;50;50;50;50;70;70;70;100;100
id=76, name=Hỏa độn: +#, type=50, level=0, strOption=50;50;50;50;50;50;50;70;70;70;100;100
id=77, name=Thủy độn: +#, type=50, level=0, strOption=50;50;50;50;50;50;50;70;70;70;100;100
id=78, name=Tấn công: +#, type=50, level=0, strOption=50;50;50;50;50;50;50;70;70;70;100;100
id=79, name=Hp tối đa: +#%, type=50, level=0, strOption=5;5;5;5;5;5;5;5;5;5;10;10
id=80, name=Mp tối đa: +#%, type=50, level=0, strOption=5;5;5;5;5;5;5;5;5;5;10;10
id=81, name=Kháng tất cả: +#, type=50, level=0, strOption=5;5;5;5;5;5;5;5;5;5;10;10
id=82, name=Kháng lôi: +#, type=50, level=0, strOption=10;10;10;10;10;10;10;10;10;10;15;15
id=83, name=Kháng thổ:, type=50, level=0, strOption=10;10;10;10;10;10;10;10;10;10;15;15
id=84, name=Kháng thủy: +#, type=50, level=0, strOption=10;10;10;10;10;10;10;10;10;10;15;15
id=85, name=Kháng hỏa: +#, type=50, level=0, strOption=10;10;10;10;10;10;10;10;10;10;15;15
id=86, name=Kháng phong: +#, type=50, level=0, strOption=10;10;10;10;10;10;10;10;10;10;15;15
id=87, name=Di chuyển nhanh đến mục tiêu, type=50, level=0, strOption=
id=88, name=Làm đối phương không thể di chuyển: duy trì +# giây, type=54, level=0, strOption=500;700;900;1300;1600;1900;2200;2500;2700;3000;3500;4000
id=89, name=Sát thương quái: +#, type=50, level=0, strOption=20;20;20;20;20;20;20;25;25;25;30;30
id=90, name=Dùng Mp hút: +#% sát thương (Duy trì 90 giây), type=50, level=0, strOption=5;5;5;5;5;5;5;5;5;5;10;10
id=91, name=Tốc độ di chuyển: +#, type=50, level=0, strOption=10;10;10;10;10;10;10;10;10;10;25;25
id=92, name=Cộng thêm: +#% sức tấn công của chiêu @, type=52, level=40, strOption=5;5;5;5;5;5;5;5;5;5;5;5
id=93, name=Cộng thêm: +#% sức tấn công của chiêu @, type=52, level=70, strOption=3;3;3;3;3;3;3;3;3;3;3;3
id=94, name=Tăng chí mạng: +# (duy trì 10 giây), type=50, level=0, strOption=40;40;40;40;40;40;40;40;40;40;40;40
id=95, name=Tăng tấn công khi đánh chí mạng: +#%, type=50, level=0, strOption=5;5;5;5;5;5;5;5;5;5;10;10
id=96, name=Mỗi nữa giây phục hồi Hp cho mình và đồng đội: +# (duy trì 5 giây), type=50, level=0, strOption=20;20;20;20;20;20;20;20;20;20;25;25
id=97, name=Tăng kháng tất cả cho mình và đồng đội: +# (duy trì 30 giây), type=50, level=0, strOption=5;5;5;5;5;5;5;5;5;5;10;10
id=98, name=Cứ mỗi lần sát thương mục tiêu sẽ chuyển thành Mp cho bản thân: +#%, type=50, level=0, strOption=1;1;1;1;1;1;2;2;2;2;3;3
id=99, name=Giải trừ 5 hiệu ứng cơ bản (suy yếu, trúng độc, làm chậm, bỏng, choáng): duy trì +# giây, type=54, level=0, strOption=500;1000;1500;2000;2500;3000;3500;4000;4500;5000;6000;7000
id=100, name=Kháng tất cả các loại sát thương: duy trì +# giây, type=54, level=0, strOption=5;5;5;5;5;5;5;5;5;5;10;10
id=101, name=Mỗi lần thọ thương sẽ tự động tăng thêm Hp: +# (duy trì 3 giây), type=50, level=0, strOption=5;5;5;5;5;5;10;10;10;10;15;15
id=102, name=Tăng +#% tấn công cơ bản (duy trì 5 giây), type=50, level=0, strOption=5;5;5;5;5;5;5;10;10;10;20;20
id=103, name=Làm tê liệt đối phương: duy trì # giây, type=54, level=0, strOption=500;600;700;800;900;1000;1100;1200;1400;1600;1800;2000
id=104, name=Hỗ trợ đồng đội tăng kinh nghiệm khi đánh quái: +#%, type=50, level=0, strOption=1;1;1;1;1;1;1;2;2;2;5;5
id=105, name=Địa điểm kho báu, type=0, level=0, strOption=
id=106, name=Hp: +#, type=1, level=0, strOption=20;20;20;20;20;20;20;25;25;25;25;30;30;40;40;100;200;200;400;0
id=107, name=Mp: +#, type=1, level=0, strOption=20;20;20;20;20;20;20;25;25;25;25;30;30;40;40;100;200;200;400;0
id=108, name=Kháng lôi: +#, type=1, level=0, strOption=5;5;5;5;5;5;5;10;10;10;10;15;15;20;20;30;40;40;80;0
id=109, name=Kháng thổ: +#, type=1, level=0, strOption=5;5;5;5;5;5;5;10;10;10;10;15;15;20;20;30;40;40;80;0
id=110, name=Kháng thủy: +#, type=1, level=0, strOption=5;5;5;5;5;5;5;10;10;10;10;15;15;20;20;30;40;40;80;0
id=111, name=Kháng hỏa: +#, type=1, level=0, strOption=5;5;5;5;5;5;5;10;10;10;10;15;15;20;20;30;40;40;80;0
id=112, name=Kháng phong: +#, type=1, level=0, strOption=5;5;5;5;5;5;5;10;10;10;10;15;15;20;20;30;40;40;80;0
id=113, name=Tăng tấn công lên hệ Lôi: +#, type=1, level=0, strOption=10;10;10;10;10;10;10;15;15;15;15;20;20;25;25;40;60;60;120;0
id=114, name=Tăng tấn công lên hệ Thổ: +#, type=1, level=0, strOption=10;10;10;10;10;10;10;15;15;15;15;20;20;25;25;40;60;60;120;0
id=115, name=Tăng tấn công lên hệ Thủy: +#, type=1, level=0, strOption=10;10;10;10;10;10;10;15;15;15;15;20;20;25;25;40;60;60;120;0
id=116, name=Tăng tấn công lên hệ Hỏa: +#, type=1, level=0, strOption=10;10;10;10;10;10;10;15;15;15;15;20;20;25;25;40;60;60;120;0
id=117, name=Tăng tấn công lên hệ Phong: +#, type=1, level=0, strOption=10;10;10;10;10;10;10;15;15;15;15;20;20;25;25;40;60;60;120;0
id=118, name=Tốc độ di chuyển: +#, type=1, level=0, strOption=
id=119, name=Tỉ lệ Hp tối đa: +#%, type=1, level=0, strOption=
id=120, name=Tỉ lệ Mp tối đa: +#%, type=1, level=0, strOption=
id=121, name=Kháng tất cả: +#, type=1, level=0, strOption=5;5;5;5;5;5;5;10;10;10;10;15;15;20;20;30;40;40;80;0
id=122, name=Phát huy lực tấn công cơ bản: +#%, type=1, level=0, strOption=
id=123, name=Gây suy yếu: +#, type=1, level=0, strOption=
id=124, name=Gây trúng độc: +#, type=1, level=0, strOption=
id=125, name=Gây làm chậm: +#, type=1, level=0, strOption=
id=126, name=Gây bỏng: +#, type=1, level=0, strOption=
id=127, name=Gây choáng: +#, type=1, level=0, strOption=
id=128, name=Độ tu luyện: #/@, type=0, level=0, strOption=
id=129, name=Cộng thêm: +#% sức tấn công của chiêu @, type=52, level=90, strOption=5;5;5;5;5;5;5;5;5;5;5;5
id=130, name=Cộng thêm: +#% sức tấn công của chiêu @, type=52, level=110, strOption=3;3;3;3;3;3;3;3;3;3;3;3
id=131, name=Kích vũ khí hệ Lôi, type=1, level=0, strOption=
id=132, name=Kích vũ khí hệ Thổ, type=1, level=0, strOption=
id=133, name=Kích vũ khí hệ Thủy, type=1, level=0, strOption=
id=134, name=Kích vũ khí hệ Hỏa, type=1, level=0, strOption=
id=135, name=Kích vũ khí hệ Phong, type=1, level=0, strOption=
id=136, name=Mỗi nữa giây phục hồi Hp: +#, type=0, level=0, strOption=2;2;2;2;2;2;2;2;2;2
id=137, name=Mỗi nữa giây phục hồi Mp: +#, type=0, level=0, strOption=
id=138, name=Tương khắc lên hệ Lôi: +#, type=50, level=0, strOption=
id=139, name=Tương khắc lên hệ Thổ: +#, type=50, level=0, strOption=
id=140, name=Tương khắc lên hệ Thủy: +#, type=50, level=0, strOption=
id=141, name=Tương khắc lên hệ Hỏa: +#, type=50, level=0, strOption=
id=142, name=Tương khắc lên hệ Phong: +#, type=50, level=0, strOption=
id=143, name=Mỗi nữa giây phục hồi Hp: +#, type=3, level=0, strOption=
id=144, name=Chí mạng: +#, type=4, level=0, strOption=
id=145, name=Bỏ qua kháng tính: +#%, type=5, level=0, strOption=
id=146, name=Tấn công: +#, type=5, level=0, strOption=
id=147, name=Bỏ qua né tránh: +#, type=50, level=0, strOption=
id=148, name=Trang bị Hokage, type=9, level=0, strOption=
id=149, name=Bỏ qua kháng tính: +#%, type=0, level=0, strOption=
id=150, name=Tốc độ di chuyển: +#, type=0, level=0, strOption=
id=151, name=Né tránh: +#, type=0, level=0, strOption=
id=152, name=Kháng tất cả: +#, type=0, level=0, strOption=
id=153, name=Tăng tấn công lên hệ Lôi: +#, type=0, level=0, strOption=
id=154, name=Tăng tấn công lên hệ Thổ: +#, type=0, level=0, strOption=
id=155, name=Tăng tấn công lên hệ Thủy: +#, type=0, level=0, strOption=
id=156, name=Tăng tấn công lên hệ Hỏa: +#, type=0, level=0, strOption=
id=157, name=Tăng tấn công lên hệ Phong: +#, type=0, level=0, strOption=
id=158, name=Sát thương chuyển thành hồi phục Hp: +#%, type=0, level=0, strOption=
id=159, name=Trang bị Sharingan, type=9, level=0, strOption=
id=160, name=Bỏ qua né tránh: +#, type=0, level=0, strOption=10;10;10;10;10;10;10;10;10;10;10;14;18;22;26;30;40;40;80;0
id=161, name=Né tránh: +#, type=0, level=0, strOption=10;10;10;10;10;10;10;10;10;10;10;15;15;20;20;30;40;40;80;0
id=162, name=Phản đòn: +#%, type=10, level=0, strOption=1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;2;0
id=163, name=Trang bị Byakugan, type=9, level=0, strOption=
id=164, name=Trang bị Rinnegan, type=9, level=0, strOption=
id=165, name=Trang bị Hiền Nhân, type=9, level=0, strOption=
id=166, name=Chí mạng: +#, type=0, level=0, strOption=10;10;10;10;10;10;10;10;10;10;10;10;10;10;10;10;10;10;20;0
id=167, name=Chính xác: +#, type=0, level=0, strOption=10;10;10;10;10;10;10;10;10;10;10;15;15;20;20;30;40;40;80;0
id=168, name=Gây suy yếu: +#, type=0, level=0, strOption=1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;2;0
id=169, name=Gây trúng độc +#, type=0, level=0, strOption=1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;2;0
id=170, name=Gây làm chậm: +#, type=0, level=0, strOption=1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;2;0
id=171, name=Gây bỏng: +#, type=0, level=0, strOption=1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;2;0
id=172, name=Gây choáng: +#, type=0, level=0, strOption=1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;2;0
id=173, name=Giảm trừ sát thương: +#, type=0, level=0, strOption=10;10;10;10;10;10;10;10;10;10;10;10;10;10;10;10;10;10;20;0
id=174, name=Giảm tấn công khi bị chí mạng: -#%, type=0, level=0, strOption=1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;2;0
id=175, name=Hp:+#, type=50, level=0, strOption=
id=176, name=Tăng tốc độ di chuyển: +# (duy trì 15 giây), type=50, level=0, strOption=
id=177, name=Tăng né tránh: +# (duy trì 15 giây), type=50, level=0, strOption=
id=178, name=Ẩn thân: duy trì +# giây, khi tấn công đối phương hiệu ứng sẽ tự biến mất, type=54, level=0, strOption=
id=179, name=Gây bỏng: +#, type=50, level=0, strOption=
id=180, name=Chính xác: +#, type=50, level=0, strOption=
id=181, name=Giảm kháng tất cả: -# (duy trì 10 giây), type=50, level=0, strOption=
id=182, name=Triệt tiêu né tránh và giảm trừ sát thương: -# (duy trì 10 giây), type=50, level=0, strOption=
id=183, name=Triệu hồi chim yêu quái: duy trì # giây, type=54, level=0, strOption=
id=184, name=Sức tấn công: +#, type=50, level=0, strOption=
id=185, name=Gây suy yếu: +#, type=50, level=0, strOption=
id=186, name=Gây trúng độc: +#, type=50, level=0, strOption=
id=187, name=Gây làm chậm: +#, type=50, level=0, strOption=
id=188, name=Gây làm chậm: +#, type=50, level=0, strOption=
id=189, name=Gây bỏng:+#, type=50, level=0, strOption=
id=190, name=Triệu hồi chim khổng lồ: duy trì # giây, type=54, level=0, strOption=
id=191, name=Mỗi 2 giây phục hồi +# Hp, type=50, level=0, strOption=
id=192, name=Hp: +#, type=50, level=0, strOption=
id=193, name=Tấn công: +# (duy trì 3 giây), type=50, level=0, strOption=
id=194, name=Triệu hồi dơi hút máu: duy trì # giây, type=54, level=0, strOption=
id=195, name=Hút Hp: +#, type=50, level=0, strOption=
id=196, name=Làm tiêu hao Mp: -#, type=50, level=0, strOption=
id=197, name=Bỏ qua kháng tính: +#%, type=50, level=0, strOption=
id=198, name=Trạng thái hiền nhân: duy trì # giây, type=54, level=0, strOption=
id=199, name=Tấn công: +#, type=8, level=0, strOption=10;10;12;12;14;14;14;16;16;16;18;18;18;20;20;20;22;22;24
id=200, name=Mỗi nữa giây phục hồi Hp: +#, type=8, level=0, strOption=1;1;1;1;1;1;1;1;1;1;2;2;2;3;3;3;4;4;5
id=201, name=Kháng tất cả: +#, type=8, level=0, strOption=5;5;6;6;7;7;7;8;8;8;9;9;9;10;10;10;11;11;12
id=202, name=Hp: +#, type=8, level=0, strOption=10;10;15;15;20;20;20;25;25;25;30;30;30;35;35;35;40;40;45
id=203, name=Chí mạng: +#, type=8, level=0, strOption=5;5;6;6;7;7;7;8;8;8;9;9;9;10;10;10;11;11;12
id=204, name=Né tránh: +#, type=8, level=0, strOption=5;5;6;6;7;7;7;8;8;8;9;9;9;10;10;10;11;11;12
id=205, name=Chính xác: +#, type=8, level=0, strOption=5;5;6;6;7;7;7;8;8;8;9;9;9;10;10;10;11;11;12
id=206, name=Giảm trừ sát thương: +#, type=8, level=0, strOption=5;5;6;6;7;7;7;8;8;8;9;9;9;10;10;10;11;11;12
id=207, name=Tấn công quái: +#, type=0, level=0, strOption=150;150;150;150;150;150;150;150;150;150;150;150;150;150;150;150;150;150;300;0
id=208, name=Tấn công: +#, type=0, level=0, strOption=100;100;100;100;100;100;100;100;100;100;100;100;100;100;100;100;100;100;200;0
id=209, name=Tăng Chakra: +#, type=0, level=0, strOption=
id=210, name=Cải trang Akimichi Choji, type=14, level=10, strOption=
id=211, name=Cải trang Rock Lee, type=14, level=12, strOption=
id=212, name=Cải trang Shino, type=14, level=14, strOption=
id=213, name=Cải trang Temari, type=14, level=16, strOption=
id=214, name=Cải trang Kankuro, type=14, level=18, strOption=
id=215, name=Cải trang Gaara, type=14, level=20, strOption=
id=216, name=Cải trang Hidan, type=14, level=22, strOption=
id=217, name=Cải trang Kakuzu, type=14, level=24, strOption=
id=218, name=Cải trang Deidara, type=14, level=26, strOption=
id=219, name=Cải trang Sasori, type=14, level=28, strOption=
id=220, name=Cải trang Zetsu, type=14, level=30, strOption=
id=221, name=Cải trang Hoshigaki Kisame, type=14, level=32, strOption=
id=222, name=Cải trang Uchiha Itachi, type=14, level=34, strOption=
id=223, name=Cải trang Konan, type=14, level=36, strOption=
id=224, name=Cải trang Pain, type=14, level=38, strOption=
id=225, name=Cải trang Uchiha Obito, type=14, level=40, strOption=
id=226, name=Cải trang Hokage, type=14, level=50, strOption=
id=227, name=Thời gian luyện tập: # giờ, type=0, level=0, strOption=
id=228, name=Cải trang Lục Đạo, type=14, level=55, strOption=
id=229, name=Cải trang Kakashi, type=14, level=31, strOption=
id=230, name=Cải trang Minato, type=14, level=45, strOption=
id=231, name=Cải trang Hashirama, type=14, level=48, strOption=
id=232, name=Cải trang Anbu, type=14, level=13, strOption=
id=233, name=Cải trang Anbu Cội Rễ, type=14, level=15, strOption=
id=234, name=Cải trang Haku, type=14, level=17, strOption=
id=235, name=Cải trang Zabuza, type=14, level=19, strOption=
id=236, name=Cải trang Sai, type=14, level=21, strOption=
id=237, name=Cải trang Iruka, type=14, level=23, strOption=
id=238, name=Cải trang Kurenai, type=14, level=25, strOption=
id=239, name=Cải trang Asuma, type=14, level=27, strOption=
id=240, name=Cải trang Gai, type=14, level=29, strOption=
id=241, name=Cải trang Karin, type=14, level=33, strOption=
id=242, name=Cải trang Suigetsu, type=14, level=35, strOption=
id=243, name=Cải trang Yamato, type=14, level=39, strOption=
id=244, name=Cải trang Kabuto, type=14, level=41, strOption=
id=245, name=Cải trang Tsunade, type=14, level=42, strOption=
id=246, name=Cải trang Orochimaru, type=14, level=43, strOption=
id=247, name=Cải trang Jiraiya, type=14, level=44, strOption=
id=248, name=Cải trang Kakashi Lục Đạo, type=14, level=46, strOption=
id=249, name=Cải trang Tobirama, type=14, level=47, strOption=
id=250, name=Cải trang Madara, type=14, level=49, strOption=
id=251, name=Cải trang Juugo, type=14, level=37, strOption=
id=252, name=(+17) Sát thương chuyển thành hồi phục Hp: +#%, type=10, level=60, strOption=
id=253, name=(+17) Tăng thêm Hp: +#, type=10, level=60, strOption=
id=254, name=Tăng tấn công cơ bản: +#%, type=0, level=0, strOption=1;1;1;1;1;1;1;1;1;1;2;2;2;3;3;4;4;4;8;0
id=255, name=Tăng Chakra: +#, type=0, level=0, strOption=1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;2;0
id=256, name=Mỗi nữa giây phục hồi Hp: +#, type=0, level=0, strOption=1;1;1;1;1;1;1;1;1;1;2;2;2;3;3;4;4;4;8;0
id=257, name=Mỗi nữa giây phục hồi Mp: +#, type=0, level=0, strOption=1;1;1;1;1;1;1;1;1;1;2;2;2;3;3;4;4;4;8;0
id=258, name=Kháng tất cả: +#, type=0, level=0, strOption=5;5;5;5;5;5;5;5;5;5;10;10;10;15;15;20;20;20;40;0
id=259, name=(+17) Gây suy yếu: +#, type=10, level=60, strOption=
id=260, name=(+17) Gây trúng độc: +#, type=10, level=60, strOption=
id=261, name=(+17) Gây làm chậm: +#, type=10, level=60, strOption=
id=262, name=(+17) Gây bỏng: +#, type=10, level=60, strOption=
id=263, name=(+17) Gây choáng: +#, type=10, level=60, strOption=
id=264, name=Linh hồn: #/10, type=0, level=0, strOption=
id=265, name=Cải trang Phù Thủy, type=14, level=20, strOption=
id=266, name=Cải trang Bí Ngô, type=14, level=20, strOption=
id=267, name=Tăng kinh nghiệm đánh quái: +#%, type=0, level=0, strOption=
id=268, name=Khóa chặt đối phương: duy trì +# giây, type=54, level=0, strOption=
id=269, name=Giảm kháng tất cả: -#, type=50, level=0, strOption=
id=270, name=Giảm Chakra: -#, type=50, level=0, strOption=
id=271, name=Ảnh thủ phược chi thuật, type=50, level=0, strOption=
id=272, name=Giữ chặt không cho đối phương di chuyển: duy trì +# giây, type=54, level=0, strOption=
id=273, name=Tăng Chakra: +# (duy trì 10 giây), type=50, level=0, strOption=
id=274, name=Tỉ lệ gây choáng nữa giây: +#%, type=50, level=0, strOption=
id=275, name=Mỗi 1 giây phục hồi tỉ lệ Hp: +#% (duy trì 10 giây), type=50, level=0, strOption=
id=276, name=Dùng Mp hút: +#% sát thương (duy trì 20 giây), type=50, level=0, strOption=
id=277, name=Tăng thời gian gây tê liệt khi sử dụng chiêu dịch chuyển chi thuật: +# giây, type=54, level=0, strOption=
id=278, name=Mỗi lần xuất chiêu sẽ có xác xuất tăng thêm 1 nhát đánh: duy trì +# giây, type=54, level=0, strOption=
id=279, name=Tăng thêm tấn công theo tỉ lệ Hp của đối phương: +#% (không có tác dụng với quái), type=50, level=0, strOption=
id=280, name=Chính xác:+#, type=50, level=0, strOption=
id=281, name=Cải trang Văn Lang, type=14, level=11, strOption=
id=282, name=Cải trang Seimei, type=14, level=20, strOption=
id=283, name=Kinh nghiệm: #/200.000.000, type=0, level=0, strOption=
id=284, name=Cải trang Obito Lục Đạo, type=14, level=10, strOption=
id=285, name=Có xác xuất tăng thêm tấn công khi đánh chí mạng: +#%, type=50, level=0, strOption=
id=286, name=(+18) Có xác xuất hút chakra: +# (Duy trì 3 giây), type=11, level=0, strOption=
id=287, name=Cứ 30 giây sẽ né 1 đòn tấn công, type=0, level=0, strOption=
id=288, name=Cải trang Bạch Zetsu, type=14, level=30, strOption=
id=289, name=Giảm gây suy yếu: -#, type=0, level=0, strOption=
id=290, name=Giảm gây trúng độc: -#, type=0, level=0, strOption=
id=291, name=Giảm gây làm chậm: -#, type=0, level=0, strOption=
id=292, name=Giảm gây bỏng: -#, type=0, level=0, strOption=
id=293, name=Giảm gây choáng: -#, type=0, level=0, strOption=
id=294, name=Cải trang Anbu Gấu, type=14, level=10, strOption=
id=295, name=Cải trang Anbu Cọp, type=14, level=10, strOption=
id=296, name=Cải trang Anbu Chim, type=14, level=10, strOption=
id=297, name=Cải trang Anbu Gà, type=14, level=10, strOption=
id=298, name=Cải trang Anbu Dê, type=14, level=10, strOption=
id=299, name=Cải trang Dracula, type=14, level=10, strOption=
id=300, name=Cải trang Noel, type=14, level=10, strOption=
id=301, name=Cải trang Killer B, type=14, level=0, strOption=
id=302, name=Cải trang cương thi Itachi, type=14, level=0, strOption=
id=303, name=Cải trang thủ lĩnh Văn Lang, type=14, level=0, strOption=
id=304, name=(+18) Chính xác: +#, type=11, level=0, strOption=
id=305, name=Sức mạnh: #/@, type=0, level=0, strOption=
id=306, name=Tấn công khi đánh chí mạng: +#%, type=0, level=0, strOption=
id=307, name=Tăng tương khắc: +#, type=0, level=0, strOption=
id=308, name=Cải trang Madara Lục Đạo, type=14, level=49, strOption=
id=309, name=(+16) Tấn công khi có chí mạng: +#%, type=7, level=0, strOption=
id=310, name=(+18) Tăng tương khắc: +#, type=11, level=0, strOption=
id=311, name=Giảm tương khắc: +#, type=0, level=0, strOption=
id=312, name=Biến người khác thành quái vật: +#, type=50, level=0, strOption=
id=313, name=Cải trang Frankenstein, type=14, level=10, strOption=
id=314, name=Làm suy giảm chính xác của đối phương: -# (duy trì 15 giây), type=50, level=0, strOption=
id=315, name=Ngăn chặn thi triển nhẫn thuật: duy trì # giây , type=54, level=0, strOption=
id=316, name=Hút đối phương lại gần, type=50, level=0, strOption=
id=317, name=Ngăn chặn không cho đối phương sử dụng dược phẩm: duy trì # giây, type=54, level=0, strOption=
id=318, name=Làm suy giảm né tránh của đổi phương: -# (duy trì 15 giây), type=50, level=0, strOption=
id=319, name=Cải trang Người Sói, type=14, level=0, strOption=
id=320, name=Cải trang Tết, type=14, level=0, strOption=
id=321, name=Cải trang Tử Môn Gai, type=14, level=0, strOption=
id=322, name=Cứ 1 phút sẽ tạo ra hiệu ứng xuyên giáp #% , type=0, level=0, strOption=
id=323, name=(+18) Giảm tương khắc: +#, type=11, level=0, strOption=
id=324, name=(+18) Né tránh: +#, type=11, level=0, strOption=
id=325, name=Giảm suy yếu: -#, type=6, level=0, strOption=
id=326, name=Giảm trúng độc: -#, type=6, level=0, strOption=
id=327, name=Giảm làm chậm: -#, type=6, level=0, strOption=
id=328, name=Giảm bỏng: -#, type=6, level=0, strOption=
id=329, name=Giảm choáng: -#, type=6, level=0, strOption=
id=330, name=Giảm tương khắc: -#, type=7, level=0, strOption=
id=331, name=Giảm tương khắc: -#, type=0, level=0, strOption=5;5;5;5;5;5;5;5;5;5;5;5;5;5;5;10;10;10;20;0
id=332, name=Giảm 5 loại hiệu ứng: -#, type=0, level=0, strOption=
id=333, name=Cải trang Shisui, type=14, level=0, strOption=
id=334, name=Cải trang Mỵ Nương, type=14, level=0, strOption=
id=335, name=Cải trang Thủ Lĩnh Anbu, type=14, level=0, strOption=
id=336, name=Kỹ năng phân thân: cấp #/@, type=0, level=0, strOption=
id=337, name=Kinh nghiệm: #/, type=0, level=0, strOption=
id=338, name=Cải trang Thần Cọp, type=14, level=0, strOption=
id=339, name=Cải trang Nagato, type=14, level=0, strOption=
id=340, name=Có thể ghép với thời trang khác loại, type=0, level=0, strOption=
id=341, name=Tích lũy kinh nghiệm phân phân: +#, type=0, level=0, strOption=
id=342, name=Cải trang Sơn Tinh, type=14, level=0, strOption=
id=343, name=Cải trang Thủy Tinh, type=14, level=0, strOption=
id=344, name=Giảm trừ chí mạng: +#, type=8, level=0, strOption=6;6;7;7;8;8;8;9;9;9;10;10;10;11;11;11;12;12;13
id=345, name=Giảm tương khắc: +#, type=8, level=0, strOption=4;4;5;5;6;6;6;7;7;7;8;8;8;9;9;9;10;10;11
id=346, name=Giảm trừ chí mạng: +#, type=0, level=0, strOption=
id=347, name=Cứ 3 phút sẽ kích hoạt toàn chân thể, type=0, level=0, strOption=
id=348, name=(+2) Giảm trừ chí mạng: +#, type=15, level=0, strOption=
id=349, name=(+2) Hiệu ứng ngẫu nhiên: +#, type=15, level=0, strOption=
id=350, name=(+19) Tấn công lên hệ Lôi: #, type=16, level=0, strOption=
id=351, name=(+19) Tấn công lên hệ Thổ: #, type=16, level=0, strOption=
id=352, name=(+19) Tấn công lên hệ Thủy: #, type=16, level=0, strOption=
id=353, name=(+19) Tấn công lên hệ Hỏa: #, type=16, level=0, strOption=
id=354, name=(+19) Tấn công lên hệ Phong: #, type=16, level=0, strOption=
id=355, name=(+19) Giảm gây suy yếu: -#, type=16, level=0, strOption=
id=356, name=(+19) Giảm gây trúng độc: -#, type=16, level=0, strOption=
id=357, name=(+19) Giảm gây làm chậm: -#, type=16, level=0, strOption=
id=358, name=(+19) Giảm gây bỏng: -#, type=16, level=0, strOption=
id=359, name=(+19) Giảm gây choáng: -#, type=16, level=0, strOption=
id=360, name=(+19) Bỏ qua kháng tính: +#%, type=16, level=0, strOption=
id=361, name=Trang bị lục đạo (+#% tấn công cơ bản), type=9, level=0, strOption=
id=362, name=(+18) Chí mạng: +#, type=11, level=0, strOption=
id=363, name=Cải trang Thỏ Ngọc, type=14, level=0, strOption=
id=364, name=Cải trang Ma Tốc Độ, type=14, level=0, strOption=
id=365, name=Cải trang Mèo Hiệp Sĩ, type=14, level=0, strOption=
id=366, name=Cải trang Thánh Gióng, type=14, level=0, strOption=
id=367, name=Cải trang Hanzo, type=14, level=0, strOption=
id=368, name=Cải trang Danzo, type=14, level=0, strOption=
id=369, name=Susano Itachi: duy trì 30 giây, type=50, level=0, strOption=
id=370, name=Hút 8% Mp đối phương và chuyển #% sát thương thành hồi phục Hp, type=11, level=0, strOption=
id=371, name=Phản đòn: +#%, type=0, level=0, strOption=
id=372, name=Tăng tương khắc: +#, type=0, level=0, strOption=5;5;5;5;5;5;5;5;5;5;5;5;5;5;5;10;10;10;20;0
id=373, name=Phản đòn: +#%, type=0, level=0, strOption=
WriteFile: ItemOptionTemplate.txt
Load ArrDataGame2 Done: 27362
27362
93231
WriteFile: data\arr_data_game2.bin
BUILD SUCCESSFUL (total time: 6 seconds)

 */
public class frmCreateItem extends javax.swing.JFrame {

    /**
     * Creates new form frmCreateItem
     */
    public Item item = null;

    public frmCreateItem() {
        try {
            initComponents();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        jComboBox1.removeAllItems();
        for (int i = 0; i < DataCenter.gI().ItemTemplate.length; i++) {
            jComboBox1.addItem("(" + i + ") " + DataCenter.gI().ItemTemplate[i].name);
        }
        jComboBox2.removeAllItems();
        for (int i = 0; i < DataCenter.gI().ItemOptionTemplate.length; i++) {
            jComboBox2.addItem("(" + i + ") " + DataCenter.gI().ItemOptionTemplate[i].name);
        }
        jComboBox1.setEditable(true);
        jComboBox2.setEditable(true);
        JTextField tf1 = (JTextField) jComboBox1.getEditor().getEditorComponent();

        tf1.addKeyListener(
                new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    boolean b = false;
                    for (int i = 0; i < jComboBox1.getItemCount(); i++) {
                        if (jComboBox1.getItemAt(i).toLowerCase().contains(tf1.getText().toLowerCase())) {
                            jComboBox1.setSelectedIndex(i);
                            b = true;
                            break;
                        }
                    }
                    if (!b) {
                        infoBox("Không tìm thấy Vật Phẩm này.");
                    }
                }
            }
        });
        JTextField tf2 = (JTextField) jComboBox2.getEditor().getEditorComponent();

        tf2.addKeyListener(
                new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    boolean b = false;
                    for (int i = 0; i < jComboBox2.getItemCount(); i++) {
                        if (jComboBox2.getItemAt(i).toLowerCase().contains(tf2.getText().toLowerCase())) {
                            jComboBox2.setSelectedIndex(i);
                            b = true;
                            break;
                        }
                    }
                    if (!b) {
                        infoBox("Không tìm thấy Option này.");
                    }
                }
            }
        });

    }

    public static void infoBox(String infoMessage) {
        JOptionPane.showMessageDialog(null, infoMessage, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jComboBox2 = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel1.setName(""); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
        );

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel2.setText("jLabel2");
        jLabel2.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
        );

        jButton1.setText("Thêm Options");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel3.setText("Value 1: ");

        jLabel4.setText("Value 2: ");

        jLabel5.setText("Value 3:");

        jLabel6.setText("StrOptions: ");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jButton2.setText("Load Options");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jComboBox2, javax.swing.GroupLayout.Alignment.TRAILING, 0, 632, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel3)
                                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
                                            .addComponent(jTextField2))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(126, 126, 126)
                                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(8, 8, 8)
                                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel3)
                                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel5)
                                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed

        int index = this.jComboBox1.getSelectedIndex();
        if (index >= 0 && index < DataCenter.gI().ItemTemplate.length) {
            try {
                this.jLabel1.setIcon(new ImageIcon(ImageIO.read(new File("DataImage\\IconClient\\" + DataCenter.gI().ItemTemplate[index].idIcon + ".png.png"))));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Item itemOld = item;
            item = new Item(index);
            if (itemOld != null) {
                item.strOptions = itemOld.strOptions;
            }
            setItem();

        }

    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String str1 = this.jTextField1.getText();
        String str2 = this.jTextField2.getText();
        String str3 = this.jTextField3.getText();
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");
        int num1 = -1;
        int num2 = -1;
        int num3 = -1;
        try {
            num1 = Integer.parseInt(str1);
        } catch (Exception êx) {

        }
        try {
            num2 = Integer.parseInt(str2);
        } catch (Exception êx) {

        }
        try {
            num3 = Integer.parseInt(str3);
        } catch (Exception êx) {

        }

        ItemOption itemoption = null;
        if (num1 > -1 && num2 > -1 && num3 > -1) {
            itemoption = new ItemOption(new int[]{jComboBox2.getSelectedIndex(), num1, num2, num3});
        } else if (num1 > -1 && num2 > -1) {
            itemoption = new ItemOption(new int[]{jComboBox2.getSelectedIndex(), num1, num2});
        } else if (num1 > -1) {
            itemoption = new ItemOption(new int[]{jComboBox2.getSelectedIndex(), num1});
        }

        if (itemoption == null) {
            return;
        }
        item.removeItemOption(itemoption);
        item.addItemOption(itemoption);
        setItem();

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        item.strOptions = this.jTextArea1.getText();
        setItem();
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        //DataCenter.gI().readArrDataGame(true);
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmCreateItem().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    // End of variables declaration//GEN-END:variables

    private void setItem() {
        Vector vec = Item.getTextVec(item);
        String s = "";
        for (int i = 0; i < vec.size(); i++) {
            s += ((LangLa_gp) vec.get(i)).a;
            s += "<br>";
        }
        //   Log.debug(item.strOptions);
        this.jLabel2.setText("<html>" + s + "</html>");
        this.jTextArea1.setText(item.strOptions);
    }
}
