package com.oneself.service.impl;

import com.oneself.exception.OneselfException;
import com.oneself.model.enums.RedisKeyPrefixEnum;
import com.oneself.model.vo.CaptchaVO;
import com.oneself.service.CaptchaService;
import com.oneself.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author liuhuan
 * date 2025/1/24
 * packageName com.oneself.service.impl
 * className CaptchaServiceImpl
 * description 验证码服务实现
 * version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CaptchaServiceImpl implements CaptchaService {

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 验证码字符集（排除容易混淆的字符：0, O, 1, I, l）
     */
    private static final String CAPTCHA_CHARS = "23456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz";
    private static final int CAPTCHA_LENGTH = 4;
    private static final int IMAGE_WIDTH = 120;
    private static final int IMAGE_HEIGHT = 40;
    private static final int CAPTCHA_EXPIRE_MINUTES = 5; // 验证码5分钟过期

    @Override
    public CaptchaVO generateCaptcha() {
        // 1. 生成验证码ID
        String captchaId = JwtUtils.getUUID();

        // 2. 生成验证码文本
        String captchaCode = generateCaptchaCode();

        // 3. 生成验证码图片
        BufferedImage image = generateCaptchaImage(captchaCode);

        // 4. 将图片转换为Base64
        String base64Image = imageToBase64(image);

        // 5. 存储验证码到Redis（不区分大小写，统一转小写存储）
        String redisKey = buildCaptchaKey(captchaId);
        redisTemplate.opsForValue().set(redisKey, captchaCode.toLowerCase(), CAPTCHA_EXPIRE_MINUTES, TimeUnit.MINUTES);

        log.debug("生成验证码，captchaId={}, code={}", captchaId, captchaCode);

        return CaptchaVO.builder()
                .captchaId(captchaId)
                .captchaImage("data:image/png;base64," + base64Image)
                .build();
    }

    @Override
    public boolean validateCaptcha(String captchaId, String captchaCode) {
        if (StringUtils.isBlank(captchaId) || StringUtils.isBlank(captchaCode)) {
            return false;
        }

        String redisKey = buildCaptchaKey(captchaId);
        String storedCode = redisTemplate.opsForValue().get(redisKey);

        if (StringUtils.isBlank(storedCode)) {
            log.warn("验证码已过期或不存在，captchaId={}", captchaId);
            return false;
        }

        // 验证码不区分大小写
        boolean isValid = storedCode.equalsIgnoreCase(captchaCode.trim());

        if (isValid) {
            // 验证成功后删除验证码（一次性使用）
            redisTemplate.delete(redisKey);
            log.debug("验证码验证成功，captchaId={}", captchaId);
        } else {
            log.warn("验证码验证失败，captchaId={}, input={}, stored={}", captchaId, captchaCode, storedCode);
        }

        return isValid;
    }

    /**
     * 生成验证码文本
     */
    private String generateCaptchaCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            int index = random.nextInt(CAPTCHA_CHARS.length());
            code.append(CAPTCHA_CHARS.charAt(index));
        }
        return code.toString();
    }

    /**
     * 生成验证码图片
     */
    private BufferedImage generateCaptchaImage(String code) {
        BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 设置抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 设置背景色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);

        // 绘制干扰线
        Random random = new Random();
        g.setColor(getRandomColor(200, 250));
        for (int i = 0; i < 5; i++) {
            int x1 = random.nextInt(IMAGE_WIDTH);
            int y1 = random.nextInt(IMAGE_HEIGHT);
            int x2 = random.nextInt(IMAGE_WIDTH);
            int y2 = random.nextInt(IMAGE_HEIGHT);
            g.drawLine(x1, y1, x2, y2);
        }

        // 绘制验证码字符
        int charWidth = IMAGE_WIDTH / (CAPTCHA_LENGTH + 1);
        g.setFont(new Font("Arial", Font.BOLD, 28));
        // 计算字符垂直位置：字体大小为28，图片高度40，基线位置应该在30左右（约75%高度）比较居中
        int baseY = IMAGE_HEIGHT * 3 / 4; // 30
        for (int i = 0; i < code.length(); i++) {
            g.setColor(getRandomColor(20, 130));
            int x = charWidth * (i + 1) - 10;
            // 在基线位置上下随机偏移，范围约 27-33
            int y = baseY + random.nextInt(6) - 3;
            // 添加旋转效果
            double angle = (random.nextDouble() - 0.5) * 0.3; // -15度到15度
            g.rotate(angle, x, y);
            g.drawString(String.valueOf(code.charAt(i)), x, y);
            g.rotate(-angle, x, y);
        }

        // 绘制干扰点
        for (int i = 0; i < 50; i++) {
            int x = random.nextInt(IMAGE_WIDTH);
            int y = random.nextInt(IMAGE_HEIGHT);
            g.setColor(getRandomColor(150, 200));
            g.fillOval(x, y, 2, 2);
        }

        g.dispose();
        return image;
    }

    /**
     * 获取随机颜色
     */
    private Color getRandomColor(int min, int max) {
        Random random = new Random();
        int r = min + random.nextInt(max - min);
        int g = min + random.nextInt(max - min);
        int b = min + random.nextInt(max - min);
        return new Color(r, g, b);
    }

    /**
     * 将图片转换为Base64
     */
    private String imageToBase64(BufferedImage image) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            log.error("验证码图片转换Base64失败", e);
            throw new OneselfException("生成验证码图片失败");
        }
    }

    /**
     * 构建验证码Redis Key
     */
    private String buildCaptchaKey(String captchaId) {
        return RedisKeyPrefixEnum.CAPTCHA.getPrefix() + captchaId;
    }
}

