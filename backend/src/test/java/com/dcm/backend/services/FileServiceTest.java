package com.dcm.backend.services;

import com.dcm.backend.config.MinioConfig;
import com.dcm.backend.config.MinioProperties;
import com.dcm.backend.dto.FileFilterDTO;
import com.dcm.backend.dto.FileHeaderDTO;
import com.dcm.backend.entities.FileHeader;
import com.dcm.backend.entities.Keyword;
import com.dcm.backend.enumeration.Status;
import com.dcm.backend.exceptions.FileNotFoundException;
import com.dcm.backend.exceptions.NoThumbnailException;
import com.dcm.backend.repositories.FileRepository;
import com.dcm.backend.services.impl.FileServiceImpl;
import com.dcm.backend.services.impl.KeywordServiceImpl;
import io.minio.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
public class FileServiceTest {

    @Mock
    private MinioProperties mp;

    @Mock
    private MinioConfig mc;

    @Mock
    private FileRepository fileRepository;

    @Mock
    private ThumbnailService thumbnailService;

    @Mock
    private KeywordServiceImpl keywordService;

    @Mock
    private MinioClient mockMinioClient;

    @InjectMocks
    private FileServiceImpl fileService;

    @BeforeEach
    void setUp() {
        when(mp.getBucketName()).thenReturn("test");
        when(mc.minioClient()).thenReturn(mockMinioClient);
    }

    private void testUploadTemplate(String filename, String type) throws Exception {
        boolean isMedia = false;
        boolean isImage = false;
        if (type.contains("image")) {
            isMedia = true;
            isImage = true;
        } else if (type.contains("video")) {
            isMedia = true;
        }

        FileHeaderDTO metadata = FileHeaderDTO.builder()
                .filename(filename)
                .description("Test file")
                .keywords(List.of("test", "file"))
                .type(type)
                .size(18L)
                .version("1")
                .status(Status.PLANIFIE)
                .build();

        InputStream is = new ByteArrayInputStream("This is a test file".getBytes());

        when(keywordService.getOrAddKeyword(anyString())).thenReturn(new Keyword("test"));

        if (isMedia) {
            when(thumbnailService.generateImageThumbnail(any(InputStream.class), anyInt(),
                    anyInt())).thenReturn(new BufferedImage(1, 1, 1));
            when(thumbnailService.generateVideoThumbnail(any(InputStream.class), anyInt(),
                    anyInt())).thenReturn(new BufferedImage(1, 1, 1));
            when(thumbnailService.getInputStreamFromBufferedImage(
                    any(BufferedImage.class), anyString())).thenReturn(
                    new ByteArrayInputStream("test".getBytes()));
            when(thumbnailService.isImage(anyString())).thenReturn(isImage);
            when(thumbnailService.isVideo(anyString())).thenReturn(!isImage);
        }

        fileService.upload(is, metadata);

        verify(fileRepository, times(1)).save(argThat(argument -> argument.getFilename()
                .equals(metadata.getFilename()) && argument.getDescription()
                .equals(metadata.getDescription()) && argument.getVersion()
                .equals(metadata.getVersion()) && argument.getStatus()
                .equals(metadata.getStatus()) && argument.getType()
                .equals(metadata.getType()) && argument.getSize()
                .equals(metadata.getSize()) && argument.getDate()
                .equals(LocalDate.now().toString()) && argument.getKeywords()
                .size() == (metadata.getKeywords().size())));

        verify(mockMinioClient, times(isMedia ? 2 : 1)).putObject(
                any(PutObjectArgs.class));
    }

    @Test
    void testUpload() throws Exception {
        testUploadTemplate("testfile.txt", "text/plain");
    }

    @Test
    void testUploadImg() throws Exception {
        testUploadTemplate("testfile.png", "image/png");
    }

    @Test
    void testUploadVideo() throws Exception {
        testUploadTemplate("testfile.mp4", "video/mp4");
    }


    @Test
    void testCount() {
        when(fileRepository.count()).thenReturn(10L);
        long count = fileService.count();
        assertEquals(10L, count);
    }

    @Test
    void testGetPage() {
        FileFilterDTO filter = FileFilterDTO.builder()
                .page(0)
                .size(10)
                .filename("")
                .status(List.of())
                .keywords(List.of())
                .build();

        List<FileHeader> files = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            files.add(new FileHeader("testfile" + i + ".txt", "Test file", "1",
                    Status.PUBLIE, LocalDate.now().toString(), "text/plain", 123L,
                    List.of(new Keyword("test"), new Keyword("file"))));
        }

        Page<FileHeader> page = new PageImpl<>(files);
        //noinspection unchecked
        when(fileRepository.findAll(any(Specification.class),
                any(Pageable.class))).thenReturn(page);

        Page<FileHeader> result = fileService.getPage(filter);

        assertNotNull(result);
        assertEquals(10, result.getContent().size());
    }

    @Test
    @SneakyThrows
    void testDelete() {
        FileHeader fileHeader =
                new FileHeader("testfile.txt", "Test file", "1", Status.PUBLIE,
                        LocalDate.now().toString(), "text/plain", 123L,
                        new LinkedList<>());

        when(fileRepository.findByFilename("testfile.txt")).thenReturn(
                Optional.of(fileHeader));

        fileService.delete(new String[]{"testfile.txt"});

        verify(fileRepository, times(1)).delete(fileHeader);
        verify(mockMinioClient, times(1)).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    @SneakyThrows
    void testDeleteImage() {
        FileHeader fileHeader = FileHeader.builder()
                .filename("testfile.png")
                .thumbnailName("thumbnail_testfile.png")
                .description("Test file")
                .version("1")
                .status(Status.PUBLIE)
                .date(LocalDate.now().toString())
                .type("image/png")
                .size(123L)
                .keywords(new LinkedList<>())
                .build();

        when(fileRepository.findByFilename(anyString())).thenReturn(
                Optional.of(fileHeader));

        fileService.delete(new String[]{"testfile.txt"});

        verify(fileRepository, times(1)).delete(fileHeader);
        verify(mockMinioClient, times(2)).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    @SneakyThrows
    void testDeleteNotFound() {
        String nonExistentFilename = "testfilee.txt";

        when(fileRepository.findByFilename(nonExistentFilename)).thenReturn(
                Optional.empty());

        assertThrows(FileNotFoundException.class, () -> fileService.delete(new String[]{nonExistentFilename}));

        verify(fileRepository, never()).delete(any(FileHeader.class));
        verify(mockMinioClient, never()).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    void testGetFileExists() throws Exception {
        String filename = "existingFile.txt";
        FileHeader existingFileHeader =
                new FileHeader(filename, "Test file", "1", Status.PUBLIE,
                        LocalDate.now().toString(), "text/plain", 123L,
                        new LinkedList<>());

        InputStream fakeStream = new ByteArrayInputStream("file content".getBytes());

        GetObjectArgs getObjectArgs =
                GetObjectArgs.builder().bucket("test").object(filename).build();

        GetObjectResponse getObjectResponse =
                new GetObjectResponse(null, "test", null, filename, fakeStream);

        when(fileRepository.findByFilename(filename)).thenReturn(
                Optional.of(existingFileHeader));
        when(mockMinioClient.getObject(getObjectArgs)).thenReturn(getObjectResponse);

        InputStreamResource result = fileService.getFile(filename);

        assertNotNull(result);
        verify(mc.minioClient(), times(1)).getObject(getObjectArgs);
    }

    @Test
    @SneakyThrows
    void testGetFileNotFound() {
        String filename = "nonExistentFile.txt";
        when(fileRepository.findByFilename(filename)).thenReturn(Optional.empty());

        assertThrows(FileNotFoundException.class, () -> fileService.getFile(filename));

        verify(mockMinioClient, never()).getObject(any(GetObjectArgs.class));
    }

    @Test
    @SneakyThrows
    void testGetThumbnailExists() {
        String filename = "existingFile.jpg";
        String thumbnailName = "thumbnail_existingFile.jpg";
        FileHeader existingFileHeader = FileHeader.builder()
                .filename(filename)
                .thumbnailName(thumbnailName)
                .description("Test file")
                .version("1")
                .status(Status.PUBLIE)
                .date(LocalDate.now().toString())
                .type("image/jpeg")
                .size(123L)
                .keywords(new LinkedList<>())
                .build();

        InputStream fakeStream = new ByteArrayInputStream("thumbnail content".getBytes());

        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket("test")
                .object(thumbnailName)
                .build();

        GetObjectResponse getObjectResponse = new GetObjectResponse(null, "test",
                null, thumbnailName, fakeStream);

        when(fileRepository.findByFilename(filename)).thenReturn(
                Optional.of(existingFileHeader));
        when(mockMinioClient.getObject(getObjectArgs)).thenReturn(getObjectResponse);

        InputStreamResource result = fileService.getThumbnail(filename);

        assertNotNull(result);
        verify(mockMinioClient, times(1)).getObject(getObjectArgs);
    }

    @Test
    @SneakyThrows
    void testGetThumbnailFileNotFound() {
        String filename = "nonExistentFile.txt";
        when(fileRepository.findByFilename(filename)).thenReturn(Optional.empty());

        Exception exception = assertThrows(FileNotFoundException.class, () -> fileService.getThumbnail(filename));

        assertEquals("getThumbnail : " + filename + " not found", exception.getMessage());
        verify(mockMinioClient, never()).getObject(any(GetObjectArgs.class));
    }

    @Test
    @SneakyThrows
    void testGetThumbnailNoThumbnail() {
        // Setup
        String filename = "fileWithoutThumbnail.txt";
        FileHeader fileWithoutThumbnail = new FileHeader(filename, "Test file", "1",
                Status.PUBLIE, LocalDate.now().toString(),
                "text/plain", 123L, new LinkedList<>());

        when(fileRepository.findByFilename(filename)).thenReturn(
                Optional.of(fileWithoutThumbnail));

        assertThrows(NoThumbnailException.class, () -> fileService.getThumbnail(filename));

        verify(mockMinioClient, never()).getObject(any(GetObjectArgs.class));
    }

    @Test
    void testGetFileTypeExists() throws FileNotFoundException {
        String filename = "existingFile.txt";
        FileHeader existingFileHeader = new FileHeader(filename, "Test file", "1",
                Status.PUBLIE, LocalDate.now().toString(),
                "text/plain", 123L, new LinkedList<>());

        when(fileRepository.findByFilename(filename)).thenReturn(
                Optional.of(existingFileHeader));

        MediaType result = fileService.getFileType(filename);

        assertNotNull(result);
        assertEquals(MediaType.TEXT_PLAIN, result);
        verify(fileRepository, times(1)).findByFilename(filename);
    }

    @Test
    void testGetFileTypeNotFound() {
        String filename = "nonExistentFile.txt";
        when(fileRepository.findByFilename(filename)).thenReturn(Optional.empty());

        assertThrows(FileNotFoundException.class, () -> fileService.getFileType(filename));
    }

    @Test
    @SneakyThrows
    void testGetLinkSuccess() {
        String filename = "testfile.txt";
        String expectedUrl = "https://example.com/testfile.txt";

        when(mockMinioClient.getPresignedObjectUrl(
                any(GetPresignedObjectUrlArgs.class))).thenReturn(expectedUrl);
        when(fileRepository.findByFilename(filename)).thenReturn(Optional.of(
                new FileHeader(filename, "Test file", "1", Status.PUBLIE,
                        LocalDate.now().toString(), "text/plain", 123L,
                        new LinkedList<>())));

        String result = fileService.getLink(filename);

        assertEquals(expectedUrl, result);
        verify(mockMinioClient, times(1)).getPresignedObjectUrl(
                any(GetPresignedObjectUrlArgs.class));
    }

    @Test
    @SneakyThrows
    void testGetLinkFail() {
        String filename = "nonExistentFile.txt";
        when(fileRepository.findByFilename(filename)).thenReturn(Optional.empty());

        assertThrows(FileNotFoundException.class, () -> fileService.getLink(filename));
        verify(mockMinioClient, never()).getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class));
    }

    @Test
    void testDuplicateFileSuccess() throws Exception {
        String filename = "originalFile.txt";
        FileHeader originalFileHeader = new FileHeader(filename, "Original file", "1",
                Status.PUBLIE, LocalDate.now().toString(),
                "text/plain", 123L,
                List.of(new Keyword("keyword1")));

        when(fileRepository.findByFilename(filename)).thenReturn(Optional.of(originalFileHeader));

        fileService.duplicate(filename);

        ArgumentCaptor<FileHeader> fileHeaderCaptor = ArgumentCaptor.forClass(FileHeader.class);
        verify(fileRepository).save(fileHeaderCaptor.capture());

        FileHeader capturedFileHeader = fileHeaderCaptor.getValue();
        assertEquals("copy_originalFile.txt", capturedFileHeader.getFilename());
        assertNull(capturedFileHeader.getThumbnailName());
        assertNotNull(capturedFileHeader.getDate());

        verify(mc.minioClient(), times(1)).copyObject(any(CopyObjectArgs.class));
    }

    @Test
    void testDuplicateFileSuccessImage() throws Exception {
        String filename = "originalFile.jpg";
        FileHeader originalFileHeader = new FileHeader(filename, "Original file", "1",
                Status.PUBLIE, LocalDate.now().toString(),
                "image/jpeg", 123L,
                List.of(new Keyword("keyword1")));
        originalFileHeader.setThumbnailName("thumbnail_originalFile.jpg");

        when(fileRepository.findByFilename(filename)).thenReturn(Optional.of(originalFileHeader));

        fileService.duplicate(filename);

        ArgumentCaptor<FileHeader> fileHeaderCaptor = ArgumentCaptor.forClass(FileHeader.class);
        verify(fileRepository).save(fileHeaderCaptor.capture());

        FileHeader capturedFileHeader = fileHeaderCaptor.getValue();
        assertEquals("copy_originalFile.jpg", capturedFileHeader.getFilename());
        assertEquals("thumbnail_copy_originalFile.jpg",
                capturedFileHeader.getThumbnailName());
        assertNotNull(capturedFileHeader.getDate());

        verify(mc.minioClient(), times(2)).copyObject(any(CopyObjectArgs.class));
    }

    @Test
    @SneakyThrows
    void testDuplicateFileNotFound() {
        String filename = "nonexistentFile.txt";
        when(fileRepository.findByFilename(filename)).thenReturn(Optional.empty());

        assertThrows(FileNotFoundException.class, () -> fileService.duplicate(filename));

        verify(mockMinioClient, never()).copyObject(any(CopyObjectArgs.class));
        verify(fileRepository, never()).save(any(FileHeader.class));
    }

    @Test
    void testDuplicateThrowsMinioException() throws Exception {
        String filename = "originalFile.txt";
        FileHeader originalFileHeader = new FileHeader(filename, "Original file", "1",
                Status.PUBLIE, LocalDate.now().toString(),
                "text/plain", 123L,
                List.of(new Keyword("keyword1")));
        originalFileHeader.setThumbnailName("thumbnail_originalFile.jpg");

        when(fileRepository.findByFilename(filename)).thenReturn(Optional.of(originalFileHeader));
        when(mockMinioClient.copyObject(any(CopyObjectArgs.class))).thenThrow(
                IOException.class);

        assertThrows(IOException.class, () -> fileService.duplicate(filename));

        verify(fileRepository, never()).save(any(FileHeader.class));
    }

    @Test
    void testUpdateFileSuccess() throws Exception {
        String filename = "existingFile.txt";
        FileHeader existingFileHeader = new FileHeader(filename, "Original description", "1",
                Status.PUBLIE, LocalDate.now().toString(),
                "text/plain", 123L,
                List.of(new Keyword("keyword1")));
        FileHeaderDTO metadata = FileHeaderDTO.builder()
                .description("Updated description")
                .version("2")
                .status(Status.PLANIFIE)
                .keywords(List.of("keyword2"))
                .build();

        when(fileRepository.findByFilename(filename)).thenReturn(Optional.of(existingFileHeader));
        when(keywordService.getOrAddKeyword("keyword2")).thenReturn(new Keyword("keyword2"));


        ArgumentCaptor<FileHeader> fileHeaderCaptor = ArgumentCaptor.forClass(FileHeader.class);
        when(fileRepository.save(fileHeaderCaptor.capture())).thenReturn(any(FileHeader.class));

        fileService.update(filename, metadata);

        assertEquals("Updated description", fileHeaderCaptor.getValue().getDescription());
        assertEquals("2", fileHeaderCaptor.getValue().getVersion());
        assertEquals(Status.PLANIFIE, fileHeaderCaptor.getValue().getStatus());
        assertEquals(1, fileHeaderCaptor.getValue().getKeywords().size());
        assertEquals("keyword2", fileHeaderCaptor.getValue().getKeywords().iterator().next().getName());

        verify(mockMinioClient, times(1)).setObjectTags(any(SetObjectTagsArgs.class));
        verify(fileRepository, times(1)).save(any(FileHeader.class));
    }

    @Test
    @SneakyThrows
    void testUpdateFileNotFound() {
        String filename = "nonexistentFile.txt";
        FileHeaderDTO metadata = FileHeaderDTO.builder()
                .description("Updated description")
                .version("2")
                .status(Status.PLANIFIE)
                .keywords(List.of("keyword2"))
                .build();

        when(fileRepository.findByFilename(filename)).thenReturn(Optional.empty());

        assertThrows(FileNotFoundException.class, () -> fileService.update(filename, metadata));

        verify(mockMinioClient, never()).setObjectTags(any(SetObjectTagsArgs.class));
        verify(fileRepository, never()).save(any(FileHeader.class));
    }

    @Test
    void testUpdateThrowsMinioException() throws Exception {
        String filename = "existingFile.txt";
        FileHeader existingFileHeader = new FileHeader(filename, "Original description", "1",
                Status.PUBLIE, LocalDate.now().toString(),
                "text/plain", 123L,
                List.of(new Keyword("keyword1")));
        FileHeaderDTO metadata = FileHeaderDTO.builder()
                .description("Updated description")
                .version("2")
                .status(Status.PLANIFIE)
                .keywords(List.of("keyword2"))
                .build();

        when(fileRepository.findByFilename(filename)).thenReturn(Optional.of(existingFileHeader));
        doThrow(new IOException()).when(mockMinioClient).setObjectTags(any(SetObjectTagsArgs.class));

        assertThrows(IOException.class, () -> fileService.update(filename, metadata));

        verify(fileRepository, never()).save(existingFileHeader);
    }

}
