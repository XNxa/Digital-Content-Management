package com.dcm.backend.services;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class ThumbnailServiceTest {

    @Autowired
    private ThumbnailService thumbnailService;

    @Test
    public void testGenerateImageThumbnailJpg() throws Exception {
        testGenerateImageThumbnail("jpg");
    }

    @Test
    public void testGenerateImageThumbnailPng() throws Exception {
        testGenerateImageThumbnail("png");
    }

    @Test
    public void testGenerateImageThumbnailGif() throws Exception {
        testGenerateImageThumbnail("gif");
    }

    @Test
    public void testGenerateImageThumbnailBmp() throws Exception {
        testGenerateImageThumbnail("bmp");
    }

    @Test
    public void testGenerateVideoThumbnailMp4() throws IOException {
        testGenerateVideoThumbnail("mp4");
    }

    @Test
    public void testGenerateVideoThumbnailAvi() throws IOException {
        testGenerateVideoThumbnail("avi");
    }

    @Test
    public void testIsImage() {
        boolean isImage = thumbnailService.isImage("image/jpeg");
        assertTrue(isImage);

        boolean isNotImage = thumbnailService.isImage("video/mp4");
        assertFalse(isNotImage);
    }

    @Test
    public void testIsVideo() {
        boolean isVideo = thumbnailService.isVideo("video/mp4");
        assertTrue(isVideo);

        boolean isNotVideo = thumbnailService.isVideo("image/jpeg");
        assertFalse(isNotVideo);
    }

    @Test
    public void testGetInputStreamFromBufferedImage() throws IOException {
        BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);

        InputStream inputStream =
                thumbnailService.getInputStreamFromBufferedImage(image, "jpg");

        assertNotNull(inputStream);
        BufferedImage resultImage = ImageIO.read(inputStream);
        assertNotNull(resultImage);
        assertEquals(200, resultImage.getWidth());
        assertEquals(200, resultImage.getHeight());
    }

    private void testGenerateVideoThumbnail(String format) throws IOException {
        InputStream videoInputStream =
                getClass().getResourceAsStream("/testvideo." + format);

        BufferedImage thumbnail =
                thumbnailService.generateVideoThumbnail(videoInputStream, 100, 100);

        assertNotNull(thumbnail);
        assertEquals(100, thumbnail.getWidth());
        assertEquals(100, thumbnail.getHeight());
    }

    private void testGenerateImageThumbnail(@NotNull String format) throws IOException {
        BufferedImage originalImage =
                new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(originalImage, format, os);
        InputStream imageInputStream = new ByteArrayInputStream(os.toByteArray());

        BufferedImage thumbnail =
                thumbnailService.generateImageThumbnail(imageInputStream, 100, 100);

        assertNotNull(thumbnail);
        assertEquals(100, thumbnail.getWidth());
        assertEquals(100, thumbnail.getHeight());
    }
}
