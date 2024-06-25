package com.dcm.backend.services;

import com.dcm.backend.config.MinioConfig;
import com.dcm.backend.config.MinioProperties;
import com.dcm.backend.dto.FileHeaderDTO;
import com.dcm.backend.entities.FileHeader;
import com.dcm.backend.entities.Keyword;
import com.dcm.backend.enumeration.Status;
import com.dcm.backend.exceptions.FileNotFoundException;
import com.dcm.backend.repositories.FileRepository;
import com.dcm.backend.repositories.KeywordRepository;
import com.dcm.backend.services.impl.FileServiceImpl;
import com.dcm.backend.services.impl.KeywordServiceImpl;
import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
public class FileServiceTest {

    @Mock
    private MinioProperties mp;
    @Mock
    private MinioConfig mc;
    @Mock
    private KeywordRepository keywordRepository;
    @Mock
    private FileRepository fileRepository;
    @Mock
    private ThumbnailService thumbnailService;

    @InjectMocks
    private FileServiceImpl fileService;
    private KeywordServiceImpl keywordService;

    @BeforeEach
    void setUp() throws Exception {
        when(mp.getBucketName()).thenReturn("test");
    }

    @Test
    void testUpload() throws Exception {
        FileHeaderDTO metadata = new FileHeaderDTO();
        metadata.setFilename("testfile.txt");
        metadata.setDescription("Test file");
        metadata.setKeywords(List.of("test", "file"));
        metadata.setType("text/plain");
        metadata.setSize(18L);
        metadata.setVersion(String.valueOf(1));
        metadata.setStatus(Status.PLANIFIE);

        InputStream is = new ByteArrayInputStream("This is a test file".getBytes());

        MinioClient mockMinioClient = mock(MinioClient.class);
        when(mc.minioClient()).thenReturn(mockMinioClient);

        when(keywordRepository.findById(anyString())).thenReturn(Optional.empty());
        when(keywordRepository.save(any(Keyword.class))).thenAnswer(
                i -> i.getArguments()[0]);
        when(thumbnailService.isImage(anyString())).thenReturn(false);
        when(thumbnailService.isVideo(anyString())).thenReturn(false);

        fileService.upload(is, metadata);

        verify(fileRepository, times(1)).save(any(FileHeader.class));
    }

    @Test
    void testCount() {
        when(fileRepository.count()).thenReturn(10L);

        long count = fileService.count();

        assertEquals(10L, count);
    }

    @Test
    void testGetPage() {
        FileHeader fileHeader =
                new FileHeader("testfile.txt", "Test file", "1", Status.PLANIFIE,
                        LocalDate.now().toString(), "text/plain", 123L,
                        new LinkedList<>());

        Page<FileHeader> page = new PageImpl<>(Collections.singletonList(fileHeader));

        when(fileRepository.findAll(any(Specification.class),
                any(Pageable.class))).thenReturn(page);

        //TODO: Fix this test
//        Page<FileHeader> result =
//                fileService.getPage(0, 10, Optional.empty(), Optional.empty(),
//                        Optional.empty());
//
//        assertNotNull(result);
//        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testGetFileType() throws FileNotFoundException {
        FileHeader fileHeader =
                new FileHeader("testfile.txt", "Test file", "1", Status.PUBLIE,
                        LocalDate.now().toString(), "text/plain", 123L,
                        new LinkedList<>());

        when(fileRepository.findByFilename(anyString())).thenReturn(
                Optional.of(fileHeader));

        MediaType type = fileService.getFileType("testfile.txt");

        assertNotNull(type);
        assertEquals("text/plain", type.toString());
    }

    @Test
    void testGetKeywords() {
        Keyword keyword = new Keyword("test");
        when(keywordRepository.findAll()).thenReturn(Collections.singletonList(keyword));

        List<String> keywords = keywordService.getKeywords();

        assertNotNull(keywords);
        assertEquals(1, keywords.size());
        assertEquals("test", keywords.get(0));
    }

}
