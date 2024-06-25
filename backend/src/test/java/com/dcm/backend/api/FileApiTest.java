package com.dcm.backend.api;

import com.dcm.backend.dto.FileHeaderDTO;
import com.dcm.backend.enumeration.Status;
import com.dcm.backend.util.FileTestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.MinioClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FileApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FileTestUtils utils;

    @Autowired
    private MinioClient mc;


    @BeforeEach
    public void setup() throws Exception {
        utils.clearAll();
    }

    @AfterEach
    public void cleanup() throws Exception {
        utils.clearAll();
    }


    @Test
    public void contextLoads() {
    }


    @Test
    public void testUploadPng() throws Exception {
        this.upload("test1.png", "src/test/resources/test1.png",
                "image/png", "Un",
                "Deux");
        assertTrue(utils.presentInBucket("test1.png"));
        assertTrue(utils.presentInBucket("thumbnail_test1.png"));
        assertTrue(utils.hasTags("test1.png", "Un", "Deux"));
    }

    @Test
    public void testUploadTxt() throws Exception {
        this.upload("test.txt", "src/test/resources/test.txt", MediaType.TEXT_PLAIN_VALUE,
                "Un", "Deux");
        assertTrue(utils.presentInBucket("test.txt"));
        assertTrue(utils.hasTags("test.txt", "Un", "Deux"));
    }

    @Test
    public void testCount() throws Exception {
        utils.addFile("test1", "src/test/resources/test1.png", "image/png");

        mockMvc.perform(get("/api/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        utils.addFile("test2", "src/test/resources/test2.png", "image/png");

        mockMvc.perform(get("/api/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    @Test
    public void testGetPage() throws Exception {
        for (int i = 0; i < 10; i++) {
            utils.addFile("test" + i + ".png", "src/test/resources/test1.png", "image" +
                    "/png");
        }

        List<FileHeaderDTO> expectedFiles = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            FileHeaderDTO file = new FileHeaderDTO();
            file.setFilename("test" + i + ".png");
            file.setThumbnailName("thumbnail_test" + i + ".png");
            file.setDescription("");
            file.setKeywords(List.of());
            file.setStatus(Status.PUBLIE);
            file.setVersion("VF");
            file.setDate(LocalDate.now().toString());
            file.setType("image/png");
            file.setSize(Files.size(Path.of("src/test/resources/test1.png")));
            expectedFiles.add(file);
        }

        ObjectMapper mapper = new ObjectMapper();
        String expectedJson = mapper.writeValueAsString(expectedFiles);

        mockMvc.perform(get("/api/files").param("page", "0").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void testGetFile() throws Exception {
        utils.addFile("test1", "src/test/resources/test1.png", "image/png");

        mockMvc.perform(get("/api/filedata").param("filename", "test1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG));
    }

    /**
     * Generic test method for uploading files
     */
    private void upload(String fileName, String path, String type, String... keywords) throws
            Exception {

        // Get file from file system
        MockMultipartFile file = new MockMultipartFile("file", fileName, type,
                Files.readAllBytes(Path.of(path)));

        FileHeaderDTO fileHeader = new FileHeaderDTO();
        fileHeader.setDescription("Description...");
        fileHeader.setKeywords(List.of(keywords));
        fileHeader.setStatus(Status.PUBLIE);
        fileHeader.setVersion("VF");
        fileHeader.setType(type);

        MockMultipartFile metadata = new MockMultipartFile("metadata", "metadata",
                MediaType.APPLICATION_JSON_VALUE,
                new ObjectMapper().writeValueAsBytes(fileHeader));

        mockMvc.perform(multipart("/api/upload").file(file).file(metadata))
                .andExpect(status().isOk());
    }

}
