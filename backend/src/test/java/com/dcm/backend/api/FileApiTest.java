package com.dcm.backend.api;

import com.dcm.backend.config.TestSecurityConfig;
import com.dcm.backend.dto.FileFilterDTO;
import com.dcm.backend.dto.FileHeaderDTO;
import com.dcm.backend.dto.FilenameDTO;
import com.dcm.backend.enumeration.Status;
import com.dcm.backend.exceptions.FileNotFoundException;
import com.dcm.backend.exceptions.NoThumbnailException;
import com.dcm.backend.repositories.FileRepository;
import com.dcm.backend.services.FileService;
import com.dcm.backend.services.KeywordService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@WithUserDetails("user1")
public class FileApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    @MockBean
    private KeywordService keywordService;

    @MockBean
    private FileRepository fileRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testUploadFile() throws Exception {
        doNothing().when(fileService).upload(any(), any(FileHeaderDTO.class));

        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain",
                "test content".getBytes());

        FileHeaderDTO metadataDTO = FileHeaderDTO.builder()
                .filename("test.txt")
                .folder("web/docs")
                .description("test description")
                .version("1")
                .status(Status.valueOf("PLANIFIE"))
                .type("text/plain")
                .size(10L)
                .keywords(List.of("keyword1", "keyword2"))
                .build();

        String jsonmetadata = objectMapper.writeValueAsString(metadataDTO);

        MockMultipartFile metadata =
                new MockMultipartFile("metadata", "", "application/json",
                        jsonmetadata.getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/file/upload")
                .file(file)
                .file(metadata)).andExpect(status().isOk());
    }

    @Test
    public void testGetNumberOfElements() throws Exception {
        when(fileService.count(any())).thenReturn(10L);

        FileFilterDTO filter = FileFilterDTO.builder()
                .page(0)
                .size(0)
                .filename("")
                .folder("web/docs")
                .status(List.of())
                .keywords(List.of())
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/file/count")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().isOk())
                .andExpect(content().string("10"));
    }

    @Test
    public void testGetFiles() throws Exception {
        FileFilterDTO filter = FileFilterDTO.builder()
                .page(0)
                .size(10)
                .filename("")
                .folder("web/docs")
                .status(List.of())
                .keywords(List.of())
                .build();
        String jsonFilter = objectMapper.writeValueAsString(filter);

        FileHeaderDTO fileHeader = FileHeaderDTO.builder()
                .folder("web/docs")
                .filename("test")
                .description("test description")
                .version("1")
                .status(Status.valueOf("PLANIFIE"))
                .type("text/plain")
                .size(10L)
                .keywords(List.of("keyword1", "keyword2"))
                .build();

        List<FileHeaderDTO> page = List.of(fileHeader);

        when(fileService.list(any(FileFilterDTO.class))).thenReturn(page);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/file/files")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonFilter))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].filename").value("test"));
    }

    @Test
    public void testGetFileData() throws Exception {
        InputStreamResource resource = new InputStreamResource(
                new ByteArrayInputStream("test content".getBytes()));
        when(fileService.getFile(any(FilenameDTO.class))).thenReturn(resource);
        when(fileService.getFileType(any(FilenameDTO.class))).thenReturn(
                MediaType.TEXT_PLAIN);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/file/filedata")
                        .param("filename", "test.txt")
                        .param("folder", "web/docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
                .andExpect(content().string("test content"));
    }

    @Test
    public void testGetFileDataAbsent() throws Exception {
        when(fileService.getFile(any(FilenameDTO.class))).thenThrow(
                FileNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/file/filedata")
                .param("filename", "test.txt")
                .param("folder", "web/docs")).andExpect(status().isNotFound());
    }

    @Test
    public void testGetThumbnail() throws Exception {
        InputStreamResource resource = new InputStreamResource(
                new ByteArrayInputStream("thumbnail content".getBytes()));
        when(fileService.getThumbnail(any(FilenameDTO.class))).thenReturn(resource);
        when(fileService.getFileType(any(FilenameDTO.class))).thenReturn(
                MediaType.IMAGE_JPEG);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/file/thumbnail")
                        .param("filename", "test.txt")
                        .param("folder", "web/docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(content().string("thumbnail content"));
    }

    @Test
    public void testGetThumbnailAbsent() throws Exception {
        when(fileService.getThumbnail(any(FilenameDTO.class))).thenThrow(
                FileNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/file/thumbnail")
                .param("filename", "test.txt")
                .param("folder", "web/docs")).andExpect(status().isNotFound());
    }

    @Test
    public void testGetThumbnailAbsent2() throws Exception {
        when(fileService.getThumbnail(any(FilenameDTO.class))).thenThrow(
                NoThumbnailException.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/file/thumbnail")
                .param("filename", "test.txt")
                .param("folder", "web/docs")).andExpect(status().isNotFound());
    }

    @Test
    public void testGetKeywords() throws Exception {
        List<String> keywords = List.of("keyword1", "keyword2");
        when(keywordService.getAll()).thenReturn(keywords);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/file/keywords"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]", is("keyword1")))
                .andExpect(jsonPath("$[1]", is("keyword2")));
    }

    @Test
    public void testDeleteFile() throws Exception {
        doNothing().when(fileService).delete(any());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/file/delete")
                .param("filename", "test.txt")
                .param("folder", "web/docs")).andExpect(status().isOk());
    }

    @Test
    public void testDeleteFileAbsent() throws Exception {
        doThrow(FileNotFoundException.class).when(fileService).delete(any());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/file/delete")
                .param("filename", "test.txt")
                .param("folder", "web/docs")).andExpect(status().isNotFound());
    }

    @Test
    public void testGetLink() throws Exception {
        when(fileService.getLink(any(FilenameDTO.class))).thenReturn(
                "http://example.com/test.txt");

        mockMvc.perform(MockMvcRequestBuilders.get("/api/file/link")
                        .param("filename", "web/test.txt")
                        .param("folder", "web/docs"))
                .andExpect(status().isOk())
                .andExpect(content().string("http://example.com/test.txt"));
    }

    @Test
    public void testGetLinkAbsent() throws Exception {
        when(fileService.getLink(any(FilenameDTO.class))).thenThrow(
                FileNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/file/link")
                .param("filename", "test.txt")
                .param("folder", "web/docs")).andExpect(status().isNotFound());
    }

    @Test
    public void testDuplicateFile() throws Exception {
        doNothing().when(fileService).duplicate(any(FilenameDTO.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/file/duplicate")
                .param("filename", "test.txt")
                .param("folder", "web/docs")).andExpect(status().isOk());
    }

    @Test
    public void duplicateFileAbsent() throws Exception {
        doThrow(FileNotFoundException.class).when(fileService)
                .duplicate(any(FilenameDTO.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/file/duplicate")
                .param("filename", "test.txt")
                .param("folder", "web/docs")).andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateFile() throws Exception {
        doNothing().when(fileService)
                .update(any(FilenameDTO.class), any(FileHeaderDTO.class));

        FileHeaderDTO metadataDTO = FileHeaderDTO.builder()
                .folder("web/docs")
                .filename("test.txt")
                .description("updated description")
                .version("1")
                .status(Status.valueOf("PLANIFIE"))
                .type("text/plain")
                .size(10L)
                .keywords(List.of("keyword1", "keyword2"))
                .build();

        String jsonMetadata = objectMapper.writeValueAsString(metadataDTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/file/update")
                .param("filename", "test.txt")
                .param("folder", "web/docs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMetadata)).andExpect(status().isOk());
    }

    @Test
    public void testUpdateFileAbsent() throws Exception {
        doThrow(FileNotFoundException.class).when(fileService)
                .update(any(FilenameDTO.class), any(FileHeaderDTO.class));

        FileHeaderDTO metadataDTO = FileHeaderDTO.builder()
                .folder("web/docs")
                .filename("web/test.txt")
                .description("updated description")
                .version("1")
                .status(Status.valueOf("PLANIFIE"))
                .type("text/plain")
                .size(10L)
                .keywords(List.of("keyword1", "keyword2"))
                .build();

        String jsonMetadata = objectMapper.writeValueAsString(metadataDTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/file/update")
                .param("filename", "web/test.txt")
                .param("folder", "web/docs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMetadata)).andExpect(status().isNotFound());
    }
}
