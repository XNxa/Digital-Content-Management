package com.dcm.backend.services;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public interface ThumbnailService {

    int WIDTH = 200;
    int HEIGHT = 300;

    /**
     * Generate a thumbnail of the given image with the given width and height.
     *
     * @param image  The image to generate a thumbnail of.
     * @param width  The width of the thumbnail.
     * @param height The height of the thumbnail.
     * @return The thumbnail image.
     * @throws IOException If the image cannot be read.
     * @throws IllegalArgumentException If the InputStream is null.
     */
    BufferedImage generateImageThumbnail(InputStream image, int width,
                                         int height) throws IOException;

    /**
     * Generate a thumbnail of the given video with the given width and height.
     *
     * @param video  The video to generate a thumbnail of.
     * @param width  The width of the thumbnail.
     * @param height The height of the thumbnail.
     * @return The thumbnail image.
     */
    BufferedImage generateVideoThumbnail(InputStream video, int width,
                                         int height) throws
            IOException;


    /**
     * Check if the given format is an image.
     *
     * @return true if the format is an image, false otherwise.
     */
    boolean isImage(String format);

    /**
     * Check if the given format is a video.
     *
     * @return true if the format is a video, false otherwise.
     */
    boolean isVideo(String format);

    /**
     * Get an InputStream from a BufferedImage.
     *
     * @param image  The BufferedImage to get an InputStream from.
     * @param format The format of the image.
     * @return The InputStream of the BufferedImage.
     */
    InputStream getInputStreamFromBufferedImage(BufferedImage image,
                                                String format) throws IOException;
}
