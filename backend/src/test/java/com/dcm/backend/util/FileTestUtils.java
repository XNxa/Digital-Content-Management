package com.dcm.backend.util;

import com.dcm.backend.entities.FileHeader;
import com.dcm.backend.enumeration.Status;
import com.dcm.backend.repositories.FileRepository;
import io.minio.*;
import io.minio.messages.Item;
import io.minio.messages.Tags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;

@Component
public class FileTestUtils {

    private static final String TEST_BUCKET = "test";

    @Autowired
    private MinioClient mc;

    @Autowired
    private FileRepository fileRepository;


    /**
     * Clear all files from database and bucket
     */
    public void clearAll() throws Exception {
        clearBucket();
        fileRepository.deleteAll();
    }


    /**
     * Add a file to the bucket
     *
     * @param name filename in the bucket
     * @param path path to the file (with extension)
     */
    public void addFile(String name, String path) throws Exception {
        java.io.File file = new java.io.File(path);
        assert file.exists();

        mc.uploadObject(UploadObjectArgs.builder()
                .bucket(TEST_BUCKET)
                .object(name)
                .filename(path)
                .build());

        FileHeader f = new FileHeader();
        f.setFilename(name);
        f.setDescription("");
        f.setDate(LocalDate.now().toString());
        f.setKeywords(new ArrayList<>());
        f.setSize(file.length());
        f.setStatus(Status.PUBLIE);
        f.setVersion("VF");
        f.setType(Files.probeContentType(file.toPath()));

        fileRepository.save(f);
    }

    /**
     * Check if a file is present in the bucket
     *
     * @param name filename
     * @return true if the file is present
     */
    public boolean presentInBucket(String name) {
        try {
            mc.statObject(
                    StatObjectArgs.builder().bucket(TEST_BUCKET).object(name).build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public boolean hasTags(String name, String... tags) throws Exception {
        Tags t = mc.getObjectTags(
                GetObjectTagsArgs.builder().bucket(TEST_BUCKET).object(name).build());

        if (tags.length != t.get().size()) {
            return false;
        } else {
            for (String tag : tags) {
                if (!t.get().containsKey(tag)) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Clear the bucket
     */
    private void clearBucket() throws Exception {
        // Clear the bucket before each test
        Iterable<Result<Item>> results =
                mc.listObjects(ListObjectsArgs.builder().bucket(TEST_BUCKET).build());
        for (Result<Item> result : results) {
            mc.removeObject(RemoveObjectArgs.builder().bucket(TEST_BUCKET)
                    .object(result.get().objectName()).build());
        }
    }
}
