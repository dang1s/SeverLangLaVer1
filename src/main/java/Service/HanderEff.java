/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Service;

import com.sg188.data.DataCenter;
import com.sg188.lib.Log;
import com.sg188.real.Char;
import com.sg188.real.Effect;
import com.sg188.real.Mob;

/**
 *
 * @author ADMIN
 */
public class HanderEff {
    // SKILL [][]{{51, 52}, {53, 54}, {55, 56, 76}, {57, 58}, {59, 60}, {34, 61}, {62, 63}, {64, 65}, {69, 70}, {68, 70}, {72, 73}, {31, 73}, {74, 77}, {100, 104, 105}};
    public static void getPointEff(Char _myChar, Effect Eff) {
        switch (DataCenter.gI().EffectTemplate[Eff.id].type) {
            case 0: // Mỗi nữa giây phục hồi # Hp và Mp ( 0 -> 9 ; 43 ->45; 50)
                break;
            case 1: // Giảm 50% lực đánh và 50% tất cả loại kháng bản thân (7)
                break;
            case 2: // Mỗi 0.35 giây sẽ bị rút độc từ từ làm giảm # Hp (9)
                break;
            case 3: // Làm giảm tốc độ di chuyển(10)
                break;
            case 4: // Mỗi khi bị tấn công sẽ bị thọ thương nhiều hơn(11)
                break;
            case 5: // Không thể di chuyển, không thể tấn công và không thể sử dụng dược phẩm(12)
                break;
            case 6: // Mỗi nữa giây phục hồi # Hp(13 -> 19 ; 89)
                break;
            case 7: // Mỗi nữa giây phục hồi # Mp(20 ->26)
                break;
            case 8: // Kháng tất cả các loại sát thương(27)
                break;
            case 9: // Dùng Mp hút #% sát thương(28)
                break;
            case 10: // Cộng thêm #% tấn công cơ bản cho bản thân(29)
                break;
            case 11: // Mỗi nữa giây phục hồi # Hp(30)
                break;
            case 12: // Giải trừ 5 hiệu ứng cơ bản (suy yếu, trúng độc, làm chậm, bỏng, choáng)(31)
                break;
            case 13: // Cộng thêm # kháng tất cả cho bản thân(32)
                break;
            case 14: // Cộng thêm # chí mạng cho bản thân(33)
                break;
            case 15: // Mỗi nữa giây tự động tăng cường Hp: +#(34)
                break;
            case 16: // Thần trí bất minh, không thể điều khiển được bản thân(35)(Niết bàn tâm kinh)
                break;
            case 17: // Khi bị dính chiêu sẽ không thể di chuyển, không thể tấn công nhưng có thể sử dụng dược phẩm(36)(Dịch chuyển chi thuật)
                break;
            case 18: // Tăng thêm #% kinh nghiệm nhận được khi đánh quái (37)
                break;
            case 19: // Không thể di chuyển nhưng vẫn có thể sử dụng dược phẩm (38)(Hiệu ứng đóng băng)
                break;
            case 20: // Tăng # Hp, Mp (39)
                _myChar.boostHPMP= (short) Eff.value;
                break;
            case 21: // Tăng # kháng tất cả (40)
                _myChar.boostResistAll= (short) Eff.value;
                break;
            case 22: // Tăng # lực tấn công cơ bản(41)
                _myChar.boostDame= (short) Eff.value;
                break;
            case 23: // Tăng #% kinh nghiệm khi đánh quái (42)
                _myChar.expTemp = (short) Eff.value;
                break;
            case 24: // Không bị bất cứ ai tấn công, cũng như không thể tấn công người khác (46)(Trạng thái bảo hộ)
                break;
            case 25: // Tăng #% kinh nghiệm nhận được từ lửa trại và độ tu luyện bí kíp(47)
                _myChar.buffRuou = (short) Eff.value;
                break;
            case 41: // Tăng 300% tấn công cơ bản(48)
                break;
            case 42: // Làm cho tinh thần bất định, đầu óc choáng váng, có thể né tránh những phản đòn của đối phương(49)
                _myChar.isBiDuoc = true;
                break;
            case 43: // Tăng # tốc độ di chuyển(51)
                break;
            case 44: // Tăng # né tránh(52)
                break;
            case 45: // Che giấu khỏi tầm nhìn của đối phương\nGây bỏng: +#(53)
                break;
            case 46: // Chính xác: +# (54)
                break;
            case 47: // Giảm kháng tất cả: -#(55)
                _myChar.reducedResist= (short) Eff.value;
                break;
            case 48: // Triệt tiêu né tránh và giảm trừ sát thương: -#(56)
                _myChar.reducedNeftDame= (short) Eff.value;
                break;
            case 49: // Tấn công: +#(57)
                _myChar.mobBird = (short) Eff.value;
                break;
            case 50: // Gây làm chậm: +#(58)
                break;
            case 51: // Mỗi 2 giây phục hồi # Hp(59)
                break;
            case 52: // Tăng # Hp(60)
                _myChar.buffHP = Eff.value;
                _myChar.msgUpdateHpFull();
                break;
            case 53: // Tấn công: +#(61)(Thủy lao thuật)
                _myChar.thuyLaoThuat = (short) Eff.value;
                break;
            case 54: // Tăng tấn công khi đánh chí mạng: +#(62)(Dơi hút máu)
                break;
            case 55: // Hút Hp: +#(63)(Dơi hút máu)
                break;
            case 56: // Bỏ qua kháng tính: +#%(64)(Trạng thái hiền nhân)
                _myChar.isHienNhan = (short) Eff.value;
                break;
            case 57: // Làm tiêu hao Mp: -# (mỗi 3 giây sẽ tự động thi triển 1 lần và không có tác dụng đối với quái)(65)
                break;
            case 58:  // Không bị bất cứ ai tấn công, cũng như không thể tấn công người khác(66)(Bùa bảo hộ)
                _myChar.buaBaoHo = true;
                break;
            case 59: // Tăng thêm 100% kinh nghiệm khi sử dụng xích linh chi. Hiệu quả # ủy thác(67)
                break;
            case 60: // Dùng Mp hút: +#% sát thương(68)
                _myChar.mpHutDame = (short) Eff.value;
                break;
            case 61: // Mỗi 1 giây phục hồi tỉ lệ Hp: +#%(69)
                break;
            case 62: // Tăng thời gian gây tê liệt khi sử dụng chiêu dịch chuyển chi thuật: +# giây(70)(Bạch hào chi thuật)
                break;
            case 63: // Bị giữ chặt bởi bóng(71) (Ánh thủ phược chi thuật)
                break;
            case 64: // Có xác xuất tăng thêm 1 nhát đánh khi xuất chiêu(72)
                break;
            case 65: // Chính xác: +#(73)
                break;
            case 66: // Tăng Chakra: +#(74)
                _myChar.boostChakra= (short) Eff.value;
                break;
            case 67: // Không thể di chuyển, không thể tấn công và không thể sử dụng dược phẩm(75)(Hiệu ứng choáng)
                break;
            case 68: // Giảm Chakra: -#(76)
                _myChar.reducedChakra= (short) Eff.value;
                break;
            case 69: // Tỉ lệ gây choáng nữa giây: +#%(77)
                break;
            case 70: // Khóa chặt tay không cho sử dụng nhẫn thuật(78)(Dây dích)
                break;
            case 71: // Tốc độ di chuyển chậm, không thể sử dụng phù dịch chuyển. Luôn ở trạng thái chiến đấu khi khi ở các map luyện công(79)
                break;
            case 72: // Tốc độ di chuyển chậm, không thể sử dụng phù dịch chuyển. Luôn ở trạng thái chiến đấu khi khi ở các map luyện công(80)
                break;
            case 73: // Yểm bùa ngăn không cho đối phương tự động hồi sinh hoặc về thành nếu không trực tiếp nhập mã captcha(81)
                _myChar.buaUeTho=true;
                break;
            case 74: // Tăng # tầm nhìn khoảng cách đánh(82)
                break;
            case 75: // Hút chakra của đối phương: +#(83)
                break;
            case 76: // Bị hút lượng chakra: -#(84)
                break;
            case 77: // Tăng #% kinh nghiệm nhận được khi đánh quái(85)
                break;
            case 78: // Tăng #% bạc khóa nhận được khi đánh quái(86)
                break;
            case 79: // Tăng chakra: +#(87)
                break;
            case 80: // Bị thần thụ hút chakra: -#, nhẫn thuật sẽ tự kết thúc sau 21h trong ngày.(88)
                break;
            case 81: // Gây sát thương trực tiếp theo tỉ lệ Hp của đối phương: +#% (không có tác dụng với quái)(90)
                break;
            case 82: // Tăng 2 sức mạnh luyện tập của phân thân và vĩ thú(91)
                _myChar.tuLuyenChau = true;
                break;
            case 83: // Không thể trò chuyện, không thể tự hồi sinh tại chỗ(92)(Ma thuật)
                break;
            case 84: // Suy giảm né tránh: +#(93)
                break;
            case 85: // Không thể thi triển nhẫn thuật(94)(Biệt thiên thần)
                break;
            case 86: // Không thể sử dụng dược phẩm(95)(Thanh sắt chara)
                break;
            case 87: // Suy giảm chính xác: +#(96)
                break;
            case 88: // Tăng # kháng tất cả và 20% né tránh(97)
                break;
            case 89: // Tăng # tấn công và 20% tấn công cơ bản(98)
                break;
            case 90: // Chỉ số dựa trên chủ thể: #%\nLưu ý: chỉ sử dụng trong ngày, qua ngày mới sẽ bị mất(99)
                _myChar.cloneLive = true;
                break;
            case 91: // Tăng 1000 Hp và # tấn công(100)
                _myChar.buffSpeed+=100;
                _myChar.buffHP_2 = Eff.value;
                _myChar.buffDame = Eff.value;
                _myChar.msgUpdateHpFull();
                break;
            case 92: // Tăng # chính xác và tấn công(101)
                _myChar.buffCx= Eff.value;
                _myChar.buffDame = Eff.value;
                break;
            case 93: // Tăng 80% phản đòn và 300 kháng tất cả(102)
                break;
            case 94: // Tăng #% kinh nghiệm(103)
                break;
            case 95: // Tăng #%  tấn công khi đánh chí mạng(104)
                break;
            case 96: // Tăng 100 tốc độ di chuyển và +# gây bỏng(105)
                break;
            case 97:
                break;
            case 98: // Tăng  # chakra,  tốc độ di chuyển, tỉ lệ %Hp(107)
                _myChar.isSusanoItatchi = (short) Eff.value;
                break;
        }
        _myChar.updateAllChiSo();
    }

    public static void RemovePointEff(Char _myChar, Effect Eff) {
        switch (DataCenter.gI().EffectTemplate[Eff.id].type) {
            case 0: // Mỗi nữa giây phục hồi # Hp và Mp ( 0 -> 9 ; 43 ->45; 50)
                break;
            case 1: // Giảm 50% lực đánh và 50% tất cả loại kháng bản thân (7)
                break;
            case 2: // Mỗi 0.35 giây sẽ bị rút độc từ từ làm giảm # Hp (9)
                break;
            case 3: // Làm giảm tốc độ di chuyển(10)
                break;
            case 4: // Mỗi khi bị tấn công sẽ bị thọ thương nhiều hơn(11)
                break;
            case 5: // Không thể di chuyển, không thể tấn công và không thể sử dụng dược phẩm(12)
                break;
            case 6: // Mỗi nữa giây phục hồi # Hp(13 -> 19 ; 89)
                break;
            case 7: // Mỗi nữa giây phục hồi # Mp(20 ->26)
                break;
            case 8: // Kháng tất cả các loại sát thương(27)
                break;
            case 9: // Dùng Mp hút #% sát thương(28)
                break;
            case 10: // Cộng thêm #% tấn công cơ bản cho bản thân(29)
                break;
            case 11: // Mỗi nữa giây phục hồi # Hp(30)
                break;
            case 12: // Giải trừ 5 hiệu ứng cơ bản (suy yếu, trúng độc, làm chậm, bỏng, choáng)(31)
                break;
            case 13: // Cộng thêm # kháng tất cả cho bản thân(32)
                break;
            case 14: // Cộng thêm # chí mạng cho bản thân(33)
                break;
            case 15: // Mỗi nữa giây tự động tăng cường Hp: +#(34)
                break;
            case 16: // Thần trí bất minh, không thể điều khiển được bản thân(35)
                break;
            case 17: // Khi bị dính chiêu sẽ không thể di chuyển, không thể tấn công nhưng có thể sử dụng dược phẩm(36)
                break;
            case 18: // Tăng thêm #% kinh nghiệm nhận được khi đánh quái (37)
                break;
            case 19: // Không thể di chuyển nhưng vẫn có thể sử dụng dược phẩm (38)
                break;
            case 20: // Tăng # Hp, Mp (39)
                _myChar.boostHPMP= 0;
                break;
            case 21: // Tăng # kháng tất cả (40)
                _myChar.boostResistAll= 0;
                break;
            case 22: // Tăng # lực tấn công cơ bản(41)
                _myChar.boostDame= 0;
                break;
            case 23: // Tăng #% kinh nghiệm khi đánh quái (42)
                _myChar.expTemp -= Eff.value;
                break;
            case 24: // Không bị bất cứ ai tấn công, cũng như không thể tấn công người khác (46)
                break;
            case 25: // Tăng #% kinh nghiệm nhận được từ lửa trại và độ tu luyện bí kíp(47)
                _myChar.buffRuou =0;
                break;
            case 41: // Tăng 300% tấn công cơ bản(48)
                break;
            case 42: // Làm cho tinh thần bất định, đầu óc choáng váng, có thể né tránh những phản đòn của đối phương(49)
                _myChar.isBiDuoc = false;
                break;
            case 43: // Tăng # tốc độ di chuyển(51)
                break;
            case 44: // Tăng # né tránh(52)
                break;
            case 45: // Che giấu khỏi tầm nhìn của đối phương\nGây bỏng: +#(53)
                _myChar.isFatal = 0;
                break;
            case 46: // Chính xác: +# (54)
                break;
            case 47: // Giảm kháng tất cả: -#(55)
                _myChar.reducedResist=0;
                break;
            case 48: // Triệt tiêu né tránh và giảm trừ sát thương: -#(56)
                _myChar.reducedNeftDame=0;
                break;
            case 49: // Tấn công: +#(57)
                _myChar.mobBird = 0;
                break;
            case 50: // Gây làm chậm: +#(58)
                break;
            case 51: // Mỗi 2 giây phục hồi # Hp(59)
                break;
            case 52: // Tăng # Hp(60)
                _myChar.buffHP = 0;
                _myChar.msgUpdateHpFull();
                break;
            case 53: // Tấn công: +#(61)
                _myChar.thuyLaoThuat = 0;
                break;
            case 54: // Tăng tấn công khi đánh chí mạng: +#(62)(Dơi hút máu)
                break;
            case 55: // Hút Hp: +#(63)(Dơi hút máu)
                break;
            case 56: // Bỏ qua kháng tính: +#%(64)
                _myChar.isHienNhan = 0;
                break;
            case 57: // Làm tiêu hao Mp: -# (mỗi 3 giây sẽ tự động thi triển 1 lần và không có tác dụng đối với quái)(65)
                break;
            case 58:  // Không bị bất cứ ai tấn công, cũng như không thể tấn công người khác(66)
                _myChar.buaBaoHo = false;
                break;
            case 59: // Tăng thêm 100% kinh nghiệm khi sử dụng xích linh chi. Hiệu quả # ủy thác(67)
                break;
            case 60: // Dùng Mp hút: +#% sát thương(68)
                _myChar.mpHutDame = 0;
                break;
            case 61: // Mỗi 1 giây phục hồi tỉ lệ Hp: +#%(69)
                break;
            case 62: // Tăng thời gian gây tê liệt khi sử dụng chiêu dịch chuyển chi thuật: +# giây(70)
                break;
            case 63: // Bị giữ chặt bởi bóng(71)
                break;
            case 64: // Có xác xuất tăng thêm 1 nhát đánh khi xuất chiêu(72)
                break;
            case 65: // Chính xác: +#(73)
                break;
            case 66: // Tăng Chakra: +#(74)
                _myChar.boostChakra=0;
                break;
            case 67: // Không thể di chuyển, không thể tấn công và không thể sử dụng dược phẩm(75)
                break;
            case 68: // Giảm Chakra: -#(76)
                _myChar.reducedChakra =0;
                break;
            case 69: // Tỉ lệ gây choáng nữa giây: +#%(77)
                break;
            case 70: // Khóa chặt tay không cho sử dụng nhẫn thuật(78)
                break;
            case 71: // Tốc độ di chuyển chậm, không thể sử dụng phù dịch chuyển. Luôn ở trạng thái chiến đấu khi khi ở các map luyện công(79)
                break;
            case 72: // Tốc độ di chuyển chậm, không thể sử dụng phù dịch chuyển. Luôn ở trạng thái chiến đấu khi khi ở các map luyện công(80)
                break;
            case 73: // Yểm bùa ngăn không cho đối phương tự động hồi sinh hoặc về thành nếu không trực tiếp nhập mã captcha(81)
                _myChar.buaUeTho=false;
                break;
            case 74: // Tăng # tầm nhìn khoảng cách đánh(82)
                break;
            case 75: // Hút chakra của đối phương: +#(83)
                break;
            case 76: // Bị hút lượng chakra: -#(84)
                break;
            case 77: // Tăng #% kinh nghiệm nhận được khi đánh quái(85)
                break;
            case 78: // Tăng #% bạc khóa nhận được khi đánh quái(86)
                break;
            case 79: // Tăng chakra: +#(87)
                break;
            case 80: // Bị thần thụ hút chakra: -#, nhẫn thuật sẽ tự kết thúc sau 21h trong ngày.(88)
                break;
            case 81: // Gây sát thương trực tiếp theo tỉ lệ Hp của đối phương: +#% (không có tác dụng với quái)(90)
                break;
            case 82: // Tăng 2 sức mạnh luyện tập của phân thân và vĩ thú(91)
                _myChar.tuLuyenChau=false;
                break;
            case 83: // Không thể trò chuyện, không thể tự hồi sinh tại chỗ(92)
                break;
            case 84: // Suy giảm né tránh: +#(93)
                break;
            case 85: // Không thể thi triển nhẫn thuật(94)
                break;
            case 86: // Không thể sử dụng dược phẩm(95)
                break;
            case 87: // Suy giảm chính xác: +#(96)
                break;
            case 88: // Tăng # kháng tất cả và 20% né tránh(97)
                break;
            case 89: // Tăng # tấn công và 20% tấn công cơ bản(98)
                break;
            case 90: // Chỉ số dựa trên chủ thể: #%\nLưu ý: chỉ sử dụng trong ngày, qua ngày mới sẽ bị mất(99)
                _myChar.cloneLive = false;
                break;
            case 91: // Tăng 1000 Hp và # tấn công(100)
                _myChar.buffSpeed =0;
                _myChar.buffDame= 0;
                _myChar.buffHP_2 = 0;
                _myChar.msgUpdateHpFull();
                break;
            case 92: // Tăng # chính xác và tấn công(101)
                _myChar.buffCx= 0;
                _myChar.buffDame= 0;
                break;
            case 93: // Tăng 80% phản đòn và 300 kháng tất cả(102)
                break;
            case 94: // Tăng #% kinh nghiệm(103)
                break;
            case 95: // Tăng #%  tấn công khi đánh chí mạng(104)
                break;
            case 96: // Tăng 100 tốc độ di chuyển và +# gây bỏng(105)
                break;
            case 97:
                break;
            case 98: // Tăng  # chakra,  tốc độ di chuyển, tỉ lệ %Hp(107)
                _myChar.isSusanoItatchi = 0;
                break;
        }
        _myChar.updateAllChiSo();
    }

    public static void RemoveEffMob(Mob mob, Effect eff) {
        switch (eff.id) {
            case 8:
                mob.isSuyYeu = false;
                break;
            case 11:
                mob.isBong = false;
                break;
            case 36:
                mob.IsTeLiet = false;
                break;
            case 94:
                mob.IsBietThienThan = true;
                break;

        }
    }
}
