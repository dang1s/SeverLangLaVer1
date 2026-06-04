package com.sg188.lib;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.util.Random;

public class CaptchaUtil {

    private static final String UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER_CASE = UPPER_CASE.toLowerCase();
    private static final String DIGITS = "0123456789";
    private static final String ALL_CHARS = UPPER_CASE + LOWER_CASE + DIGITS;
    private static final SecureRandom random = new SecureRandom();
    private static final byte length = 4;


    /**
     * Tạo chuỗi ký tự ngẫu nhiên cho CAPTCHA.
     *
     * @param length Độ dài của chuỗi CAPTCHA.
     * @return Chuỗi CAPTCHA.
     */
    public static String generateCaptchaText() {
        if (length <= 0) throw new IllegalArgumentException("Độ dài phải lớn hơn 0");

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALL_CHARS.charAt(random.nextInt(ALL_CHARS.length())));
        }
        return sb.toString();
    }
    public static BufferedImage createCaptchaImage(String captchaText) {
        int width = 80;
        int height = 50;
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();

        // Thiết lập nền và font
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));

        // Vẽ chuỗi CAPTCHA
        g2d.drawString(captchaText, 10, 30);

        // Thêm nhiễu
        Random rand = new Random();
        for (int i = 0; i < 150; i++) {
            int x = rand.nextInt(width);
            int y = rand.nextInt(height);
            g2d.drawLine(x, y, x, y);
        }

        g2d.dispose();

        return bufferedImage;
    }
    public static byte[] CapchaToByte(String captchaText){
        try {
            BufferedImage captchaImage = CaptchaUtil.createCaptchaImage(captchaText);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(captchaImage, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            return imageBytes;
        }catch (Exception e){
            return null;
        }
    }
}

