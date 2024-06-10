package com.dcm.backend.integration;

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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
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
    public void testUpload() throws Exception {
        String name = "testfile";

        // Build a mock MultipartFile
        MockMultipartFile file =
                new MockMultipartFile("file", name, MediaType.TEXT_PLAIN_VALUE,
                        "some text".getBytes());
        Collection<String> k = List.of(new String[]{"Kw1", "Kw2"});

        // Build a metadata object
        FileHeaderDTO f = new FileHeaderDTO();
        f.setDescription("Description...");
        f.setKeywords(k);
        f.setStatus(Status.PUBLIE);
        f.setVersion("VF");

        ObjectMapper mapper = new ObjectMapper();
        MockMultipartFile metadata = new MockMultipartFile("metadata", "metadata",
                MediaType.APPLICATION_JSON_VALUE, mapper.writeValueAsBytes(f));

        mockMvc.perform(multipart("/api/upload")
                        .file(file)
                        .file(metadata))
                .andExpect(status().isOk());

        assertTrue(utils.presentInBucket(name));
    }

    @Test
    public void testCount() throws Exception {
        utils.addFile("test1", "src/test/resources/test1.png");

        mockMvc.perform(get("/api/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        utils.addFile("test2", "src/test/resources/test2.png");

        mockMvc.perform(get("/api/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    @Test
    public void testGetPage() throws Exception {
        for (int i = 0; i < 10; i++) {
            utils.addFile("test" + i, "src/test/resources/test1.png");
        }

        List<FileHeaderDTO> expectedFiles = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            expectedFiles.add(new FileHeaderDTO("test" + i, "", "VF", Status.PUBLIE,
                    LocalDate.now().toString(), "png", 23998L, new ArrayList<>()));
        }

        ObjectMapper mapper = new ObjectMapper();
        String expectedJson = mapper.writeValueAsString(expectedFiles);

        mockMvc.perform(get("/api/files").param("page", "0").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void testGetFile() throws Exception {
        throw new UnsupportedOperationException("TODO : Not implemented yet");
    }

}
