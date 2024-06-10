package com.dcm.backend.services;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public interface ThumbnailService {

    public static int WIDTH = 200;
    public static int HEIGHT = 300;

    /**
     * Generate a thumbnail of the given image with the given width and height.
     *
     * @param image  The image to generate a thumbnail of.
     * @param width  The width of the thumbnail.
     * @param height The height of the thumbnail.
     * @return The thumbnail image.
     */
    public BufferedImage generateImageThumbnail(InputStream image, int width,
                                                int height) throws IOException;

    /**
     * Generate a thumbnail of the given video with the given width and height.
     *
     * @param video  The video to generate a thumbnail of.
     * @param width  The width of the thumbnail.
     * @param height The height of the thumbnail.
     * @return The thumbnail image.
     */
    public BufferedImage generateVideoThumbnail(InputStream video, int width,
                                                int height) throws
            IOException;


    /**
     * Check if the given format is an image.
     *
     * @param format
     * @return true if the format is an image, false otherwise.
     */
    public boolean isImage(String format);

    /**
     * Check if the given format is a video.
     *
     * @param format
     * @return true if the format is a video, false otherwise.
     */
    public boolean isVideo(String format);

    /**
     * Get an InputStream from a BufferedImage.
     *
     * @param image  The BufferedImage to get an InputStream from.
     * @param format The format of the image.
     * @return The InputStream of the BufferedImage.
     */
    public InputStream getInputStreamFromBufferedImage(BufferedImage image,
                                                       String format) throws IOException;
}
