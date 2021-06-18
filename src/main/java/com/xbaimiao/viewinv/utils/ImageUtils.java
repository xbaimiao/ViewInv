package com.xbaimiao.viewinv.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class ImageUtils {

    public static BufferedImage downloadImage(String link) throws IOException {
        URL url = new URL(link);
        URLConnection connection = url.openConnection();
        connection.setUseCaches(false);
        connection.setDefaultUseCaches(false);
        connection.addRequestProperty("User-Agent", "Mozilla/5.0");
        connection.addRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
        connection.addRequestProperty("Pragma", "no-cache");
        InputStream in = connection.getInputStream();
        BufferedImage image = ImageIO.read(in);
        in.close();
        return image;
    }

    public static BufferedImage multiply(BufferedImage image, double value) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int colorValue = image.getRGB(x, y);
                Color color = new Color(colorValue, true);

                if (color.getAlpha() != 0) {
                    int red = (int) (color.getRed() * value);
                    int green = (int) (color.getGreen() * value);
                    int blue = (int) (color.getBlue() * value);
                    color = new Color(red < 0 ? 0 : (Math.min(red, 255)), green < 0 ? 0 : (Math.min(green, 255)), blue < 0 ? 0 : (Math.min(blue, 255)), color.getAlpha());
                    image.setRGB(x, y, color.getRGB());
                }
            }
        }
        return image;
    }

    public static BufferedImage multiply(BufferedImage image, BufferedImage imageOnTop) {
        for (int y = 0; y < image.getHeight() && y < imageOnTop.getHeight(); y++) {
            for (int x = 0; x < image.getWidth() && x < imageOnTop.getWidth(); x++) {
                int value = image.getRGB(x, y);
                Color color = new Color(value, true);

                int multiplyValue = imageOnTop.getRGB(x, y);
                Color multiplyColor = new Color(multiplyValue, true);

                int red = (int) Math.round((double) color.getRed() / 255 * (double) multiplyColor.getRed());
                int green = (int) Math.round((double) color.getGreen() / 255 * (double) multiplyColor.getGreen());
                int blue = (int) Math.round((double) color.getBlue() / 255 * (double) multiplyColor.getBlue());
                color = new Color(red, green, blue, color.getAlpha());
                image.setRGB(x, y, color.getRGB());
            }
        }

        return image;
    }

    public static byte[] imageToBytes(BufferedImage bufferedImage) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", out);
        } catch (IOException ignored) {
        }
        return out.toByteArray();
    }

    public static BufferedImage copyImage(BufferedImage source) {
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = b.createGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }
}
