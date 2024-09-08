package com.sg188.lib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class ProfanityFilter {
    private List<String> profanityList;

    public ProfanityFilter() {
        this.profanityList = new ArrayList<>();
        // Thêm các từ cấm vào danh sách từ cấm
        addBadWord("lồn");
        addBadWord("buồi");
        addBadWord("địt");
        addBadWord("súc vật");
        addBadWord("lon");
        addBadWord("buoi");
        addBadWord("dit");
        addBadWord("suc vat");
        addBadWord("mẹ mày");
        addBadWord("me may");
        addBadWord("đm");
        addBadWord("dm");
        addBadWord(".com");
        addBadWord(".tk");
        addBadWord(".ga");
        addBadWord(".cf");
        addBadWord(".net");
        addBadWord(".xyz");
        addBadWord(".mobi");
        addBadWord(".ml");
        addBadWord(".onine");
        addBadWord("như cc");
        addBadWord("nhu cc");
        addBadWord("game rác");
        addBadWord("game rac");
        addBadWord("parky");
        addBadWord("nam kỳ");
        addBadWord("namky");
        addBadWord("nam kì");
        addBadWord("nam ki");
        addBadWord("parki");
    }

    // Thêm từ cấm vào danh sách từ cấm
    public void addBadWord(String word) {
        profanityList.add(word.toLowerCase()); // Chuyển đổi từ sang chữ thường
    }

    // Kiểm tra xem văn bản có chứa từ cấm không
    public boolean containsProfanity(String text) {
        for (String word : profanityList) {
            // Kiểm tra xem từ cấm có xuất hiện trong văn bản không
            if (Pattern.compile("\\b" + Pattern.quote(word) + "\\b", Pattern.CASE_INSENSITIVE).matcher(text).find()) {
                return true;
            }
        }
        return false;
    }

    // Cắt từ cấm từ văn bản
    public String censorProfanity(String text) {
        for (String word : profanityList) {
            // Sử dụng biểu thức chính quy để tìm và thay thế từ cấm bằng ký tự *
            text = text.replaceAll("\\b" + Pattern.quote(word) + "\\b", repeat("*", word.length()));
        }
        return text;
    }

    // Phương thức hỗ trợ để tạo chuỗi ký tự lặp lại
    private String repeat(String str, int times) {
        return new String(new char[times]).replace("\0", str);
    }

}

