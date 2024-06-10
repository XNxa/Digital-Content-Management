package com.dcm.backend.services.impl;

import com.dcm.backend.services.ThumbnailService;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class ThumbnailServiceImpl implements ThumbnailService {


    @Override
    public BufferedImage generateImageThumbnail(InputStream image, int width,
                                                int height) throws IOException {
        BufferedImage img = ImageIO.read(image);
        return generateImageThumbnail(img, width, height);
    }

    @Override
    public BufferedImage generateVideoThumbnail(InputStream video, int width,
                                                int height) throws
            IOException {

        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(video);
        int frame = 0;

        frameGrabber.start();

        BufferedImage bufferedImage;
        try (Java2DFrameConverter converter = new Java2DFrameConverter()) {
            bufferedImage =
                    generateImageThumbnail(converter.convert(frameGrabber.grabImage()),
                            width,
                            height);
        }

        frameGrabber.close();

        return bufferedImage;
    }

    @Override
    public boolean isImage(String format) {
        if (format.contains("image")) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isVideo(String format) {
        if (format.contains("video")) {
            return true;
        }
        return false;
    }

    @Override
    public InputStream getInputStreamFromBufferedImage(BufferedImage image, String format) throws
            IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        ImageIO.write(image, format, os);

        return new ByteArrayInputStream(os.toByteArray());
    }

    private BufferedImage generateImageThumbnail(BufferedImage image, int width,
                                                 int height) throws IOException {
        BufferedImage img = image;

        BufferedImage res =
                Scalr.resize(img, Scalr.Method.SPEED, Scalr.Mode.AUTOMATIC,
                        width, height);

        img.flush();
        return res;
    }

}