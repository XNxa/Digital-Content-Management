package com.dcm.backend.services;

import com.dcm.backend.config.ApplicationProperties;
import com.dcm.backend.dto.FileFilterDTO;
import com.dcm.backend.dto.FileHeaderDTO;
import com.dcm.backend.dto.FilenameDTO;
import com.dcm.backend.entities.FileHeader;
import com.dcm.backend.entities.Keyword;
import com.dcm.backend.enumeration.Status;
import com.dcm.backend.exceptions.FileNotFoundException;
import com.dcm.backend.exceptions.NoThumbnailException;
import com.dcm.backend.repositories.FileRepository;
import com.dcm.backend.services.impl.FileServiceImpl;
import com.dcm.backend.services.impl.KeywordServiceImpl;
import com.dcm.backend.utils.mappers.FileHeaderMapper;
import io.minio.GetObjectResponse;
import jakarta.persistence.EntityManager;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestComponent
@ExtendWith(MockitoExtension.class)
public class FileServiceTest {

    @Mock
    ApplicationProperties ap;

    @Mock
    private MinioService ms;

    @Mock
    private FileRepository fileRepository;

    @Mock
    EntityManager em;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private ThumbnailService thumbnailService;

    @Mock
    private KeywordServiceImpl keywordService;

    @Spy
    private FileHeaderMapper mockFileHeaderMapper =
            Mappers.getMapper(FileHeaderMapper.class);

    @InjectMocks
    private FileServiceImpl fileService;

    @SneakyThrows
    private void setupThumbnailServiceMocks(boolean isImage) {
        when(thumbnailService.generateImageThumbnail(any(InputStream.class), anyInt(),
                anyInt())).thenReturn(new BufferedImage(1, 1, 1));
        when(thumbnailService.generateVideoThumbnail(any(InputStream.class), anyInt(),
                anyInt())).thenReturn(new BufferedImage(1, 1, 1));
        when(thumbnailService.getInputStreamFromBufferedImage(any(BufferedImage.class),
                anyString())).thenReturn(new ByteArrayInputStream("test".getBytes()));
        when(thumbnailService.isImage(anyString())).thenReturn(isImage);
        when(thumbnailService.isVideo(anyString())).thenReturn(!isImage);
    }

    private FileHeaderDTO createFileHeaderDTO(String filename, String type) {
        return FileHeaderDTO.builder()
                .filename(filename)
                .folder("web/images")
                .description("Test file")
                .keywords(List.of("test", "file"))
                .type(type)
                .size(18L)
                .version("1")
                .date(LocalDate.now())
                .status(Status.PLANIFIE)
                .build();
    }

    private FileHeader createFileHeader(String filename, String type, String thumbnailName) {
        return FileHeader.builder()
                .folder("web/images")
                .filename(filename)
                .thumbnailName(thumbnailName)
                .description("Test file")
                .version("1")
                .status(Status.PUBLIE)
                .date(LocalDate.now())
                .type(type)
                .size(123L)
                .keywords(List.of(new Keyword("test"), new Keyword("file")))
                .build();
    }

    private void testUploadTemplate(String filename, String type) throws Exception {
        boolean isImage = type.contains("image");
        boolean isMedia = isImage || type.contains("video");

        FileHeaderDTO metadata = createFileHeaderDTO(filename, type);
        InputStream is = new ByteArrayInputStream("This is a test file".getBytes());

        when(keywordService.getOrAddKeyword(anyString())).thenReturn(new Keyword("test"));

        if (isMedia) {
            setupThumbnailServiceMocks(isImage);
        }

        fileService.upload(is, metadata);

        verify(fileRepository, times(1)).save(argThat(argument -> argument.getFilename()
                .equals(metadata.getFilename()) && argument.getDescription()
                .equals(metadata.getDescription()) && argument.getVersion()
                .equals(metadata.getVersion()) && argument.getStatus()
                .equals(metadata.getStatus()) && argument.getType()
                .equals(metadata.getType()) && argument.getSize()
                .equals(metadata.getSize()) && argument.getDate()
                .equals(LocalDate.now()) && argument.getKeywords()
                .size() == metadata.getKeywords().size()));
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
        when(fileRepository.count(any(Specification.class))).thenReturn(10L);
        long count = fileService.count(
                new FileFilterDTO(0, 0, "web/images", "", List.of(), List.of(), "",
                        List.of(), LocalDate.now(), LocalDate.now()));
        assertEquals(10L, count);
    }

    @Test
    void testGetFiles() {
        FileFilterDTO filter = FileFilterDTO.builder()
                .page(0)
                .size(10)
                .filename("")
                .status(List.of())
                .keywords(List.of())
                .build();

        List<FileHeader> files = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            files.add(createFileHeader("testfile" + i + ".txt", "text/plain", null));
        }

        Page<FileHeader> page = new PageImpl<>(files);
        when(fileRepository.findAll(any(Specification.class),
                any(Pageable.class))).thenReturn(page);

        List<FileHeaderDTO> result = fileService.list(filter);

        assertNotNull(result);
        assertEquals(10, result.size());
    }

    @Test
    void testDelete() throws Exception {
        FileHeader fileHeader = createFileHeader("testfile.txt", "text/plain", null);
        when(fileRepository.findByFolderAndFilename(anyString(), anyString())).thenReturn(
                Optional.of(fileHeader));

        fileService.delete(new FilenameDTO("web/images", "testfile.txt"));

        verify(fileRepository, times(1)).delete(fileHeader);
    }

    @Test
    void testDeleteImage() throws Exception {
        FileHeader fileHeader =
                createFileHeader("testfile.png", "image/png", "thumbnail/testfile.png");
        when(fileRepository.findByFolderAndFilename(anyString(), anyString())).thenReturn(
                Optional.of(fileHeader));

        fileService.delete(new FilenameDTO("web/images", "testfile.png"));

        verify(fileRepository, times(1)).delete(fileHeader);
    }

    @Test
    void testDeleteNotFound() {
        String nonExistentFilename = "testfilee.txt";
        when(fileRepository.findByFolderAndFilename(anyString(), anyString())).thenReturn(
                Optional.empty());

        assertThrows(FileNotFoundException.class, () -> fileService.delete(
                new FilenameDTO("web/images", nonExistentFilename)));
        verify(fileRepository, never()).delete(any(FileHeader.class));
    }

    @Test
    void testGetFileExists() throws Exception {
        String filename = "existingFile.txt";
        FileHeader existingFileHeader = createFileHeader(filename, "text/plain", null);

        when(fileRepository.findByFolderAndFilename(anyString(),
                eq(filename))).thenReturn(Optional.of(existingFileHeader));
        InputStream fakeStream = new ByteArrayInputStream("file content".getBytes());
        GetObjectResponse getObjectResponse =
                new GetObjectResponse(null, "test", null, filename, fakeStream);
        when(ms.getObject("web/images/" + filename)).thenReturn(getObjectResponse);

        InputStreamResource result =
                fileService.getFile(new FilenameDTO("web/images", filename));

        assertNotNull(result);
    }

    @Test
    void testGetFileNotFound() {
        String filename = "nonExistentFile.txt";
        when(fileRepository.findByFolderAndFilename(anyString(),
                eq(filename))).thenReturn(Optional.empty());

        assertThrows(FileNotFoundException.class,
                () -> fileService.getFile(new FilenameDTO("web/images", filename)));
    }

    @Test
    void testGetThumbnailExists() throws Exception {
        String filename = "existingFile.jpg";
        String thumbnailName = "thumbnail/existingFile.jpg";
        FileHeader existingFileHeader =
                createFileHeader(filename, "image/jpeg", thumbnailName);

        InputStream fakeStream = new ByteArrayInputStream("thumbnail content".getBytes());
        when(fileRepository.findByFolderAndFilename(anyString(),
                eq(filename))).thenReturn(Optional.of(existingFileHeader));
        GetObjectResponse getObjectResponse =
                new GetObjectResponse(null, "test", null, filename, fakeStream);
        when(ms.getObject(thumbnailName)).thenReturn(getObjectResponse);

        InputStreamResource result =
                fileService.getThumbnail(new FilenameDTO("web/images", filename));

        assertNotNull(result);
    }

    @Test
    void testGetThumbnailFileNotFound() {
        String filename = "nonExistentFile.txt";
        when(fileRepository.findByFolderAndFilename(anyString(),
                eq(filename))).thenReturn(Optional.empty());

        assertThrows(FileNotFoundException.class,
                () -> fileService.getThumbnail(new FilenameDTO("web/images", filename)));
    }

    @Test
    void testGetThumbnailNoThumbnail() {
        String filename = "fileWithoutThumbnail.txt";
        FileHeader fileWithoutThumbnail = createFileHeader(filename, "text/plain", null);
        when(fileRepository.findByFolderAndFilename(anyString(),
                eq(filename))).thenReturn(Optional.of(fileWithoutThumbnail));

        assertThrows(NoThumbnailException.class,
                () -> fileService.getThumbnail(new FilenameDTO("web/images", filename)));
    }

    @Test
    @SneakyThrows
    void testGetFileTypeExists() {
        String filename = "existingFile.txt";
        FileHeader existingFileHeader = createFileHeader(filename, "text/plain", null);
        when(fileRepository.findByFolderAndFilename(anyString(),
                eq(filename))).thenReturn(Optional.of(existingFileHeader));

        MediaType result =
                fileService.getFileType(new FilenameDTO("web/images", filename));

        assertNotNull(result);
        assertEquals(MediaType.TEXT_PLAIN, result);
    }

    @Test
    void testGetFileTypeNotFound() {
        String filename = "nonExistentFile.txt";
        when(fileRepository.findByFolderAndFilename(anyString(),
                eq(filename))).thenReturn(Optional.empty());

        assertThrows(FileNotFoundException.class,
                () -> fileService.getFileType(new FilenameDTO("web/images", filename)));
    }

    @Test
    void testGetLinkSuccess() throws Exception {
        String filename = "testfile.txt";
        String expectedUrl = "https://example.com/testfile.txt";

        when(ms.getObjectUrl(anyString(), anyInt(), any())).thenReturn(expectedUrl);
        when(fileRepository.findByFolderAndFilename(any(), eq(filename))).thenReturn(
                Optional.of(createFileHeader(filename, "text/plain", null)));

        String result = fileService.getLink(new FilenameDTO("web/images", filename));

        assertEquals(expectedUrl, result);
    }

    @Test
    void testGetLinkFail() {
        String filename = "nonExistentFile.txt";
        when(fileRepository.findByFolderAndFilename(anyString(),
                eq(filename))).thenReturn(Optional.empty());

        assertThrows(FileNotFoundException.class,
                () -> fileService.getLink(new FilenameDTO("web/images", filename)));
    }

    @Test
    void testDuplicateFileSuccess() throws Exception {
        String filename = "originalFile.txt";
        FileHeader originalFileHeader = createFileHeader(filename, "text/plain", null);
        when(fileRepository.findByFolderAndFilename(anyString(),
                eq(filename))).thenReturn(Optional.of(originalFileHeader));

        fileService.duplicate(new FilenameDTO("web/images", filename));

        ArgumentCaptor<FileHeader> fileHeaderCaptor =
                ArgumentCaptor.forClass(FileHeader.class);
        verify(fileRepository).save(fileHeaderCaptor.capture());

        FileHeader capturedFileHeader = fileHeaderCaptor.getValue();
        assertEquals("originalFile_copy.txt", capturedFileHeader.getFilename());
        assertNull(capturedFileHeader.getThumbnailName());
        assertNotNull(capturedFileHeader.getDate());
    }

    @Test
    void testDuplicateFileSuccessImage() throws Exception {
        String filename = "originalFile.jpg";
        FileHeader originalFileHeader =
                createFileHeader(filename, "image/jpeg", "thumbnail/originalFile.jpg");
        when(fileRepository.findByFolderAndFilename(anyString(),
                eq(filename))).thenReturn(Optional.of(originalFileHeader));

        fileService.duplicate(new FilenameDTO("web/images", filename));

        ArgumentCaptor<FileHeader> fileHeaderCaptor =
                ArgumentCaptor.forClass(FileHeader.class);
        verify(fileRepository).save(fileHeaderCaptor.capture());

        FileHeader capturedFileHeader = fileHeaderCaptor.getValue();
        assertEquals("originalFile_copy.jpg", capturedFileHeader.getFilename());
        assertEquals("thumbnail/web/images/originalFile_copy.jpg",
                capturedFileHeader.getThumbnailName());
        assertNotNull(capturedFileHeader.getDate());
    }

    @Test
    void testDuplicateFileNotFound() {
        String filename = "nonexistentFile.txt";
        when(fileRepository.findByFolderAndFilename(anyString(),
                eq(filename))).thenReturn(Optional.empty());

        assertThrows(FileNotFoundException.class,
                () -> fileService.duplicate(new FilenameDTO("web/images", filename)));
        verify(fileRepository, never()).save(any(FileHeader.class));
    }

    @Test
    void testUpdateFileSuccess() throws Exception {
        String filename = "existingFile.txt";
        FileHeader existingFileHeader = createFileHeader(filename, "text/plain", null);
        FileHeaderDTO metadata = FileHeaderDTO.builder()
                .description("Updated description")
                .version("2")
                .status(Status.PLANIFIE)
                .keywords(List.of("keyword2"))
                .build();

        when(fileRepository.findByFolderAndFilename(anyString(),
                eq(filename))).thenReturn(Optional.of(existingFileHeader));
        when(keywordService.getOrAddKeyword("keyword2")).thenReturn(
                new Keyword("keyword2"));

        fileService.update(new FilenameDTO("web/images", filename), metadata);

        assertEquals("Updated description", existingFileHeader.getDescription());
        assertEquals("2", existingFileHeader.getVersion());
        assertEquals(Status.PLANIFIE, existingFileHeader.getStatus());
        assertEquals(1, existingFileHeader.getKeywords().size());
        assertEquals("keyword2",
                existingFileHeader.getKeywords().iterator().next().getName());
    }

    @Test
    void testUpdateFileNotFound() {
        String filename = "nonexistentFile.txt";
        FileHeaderDTO metadata = FileHeaderDTO.builder()
                .description("Updated description")
                .version("2")
                .status(Status.PLANIFIE)
                .keywords(List.of("keyword2"))
                .build();

        when(fileRepository.findByFolderAndFilename(anyString(),
                eq(filename))).thenReturn(Optional.empty());

        assertThrows(FileNotFoundException.class,
                () -> fileService.update(new FilenameDTO("web/images", filename),
                        metadata));
        verify(fileRepository, never()).save(any(FileHeader.class));
    }
}
