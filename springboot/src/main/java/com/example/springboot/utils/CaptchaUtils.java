package com.example.springboot.utils;

import java.util.Random;

public class CaptchaUtils {

    // 生成4位随机数字验证码
    public static Captcha generateCaptcha() {
        Random random = new Random();

        // 生成4位随机数字 (1000-9999)
        int code = random.nextInt(9000) + 1000;

        return new Captcha(String.valueOf(code), code);
    }

    // 验证码实体类
    public static class Captcha {
        private String code;
        private int result;

        public Captcha(String code, int result) {
            this.code = code;
            this.result = result;
        }

        public String getCode() {
            return code;
        }

        public int getResult() {
            return result;
        }
    }
}
