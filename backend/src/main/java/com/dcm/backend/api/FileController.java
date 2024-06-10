package com.dcm.backend.api;

import com.dcm.backend.dto.FileHeaderDTO;
import com.dcm.backend.entities.FileHeader;
import com.dcm.backend.entities.Keyword;
import com.dcm.backend.services.FileService;
import io.minio.errors.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class FileController {

    @Autowired
    private FileService fs;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/upload")
    public void uploadFile(@RequestPart("file") MultipartFile file,
                           @RequestPart("metadata") FileHeaderDTO metadata)
            throws IOException, ServerException, InsufficientDataException,
            ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {

        metadata.setFilename(file.getOriginalFilename());
        metadata.setSize(file.getSize());
        metadata.setType(file.getContentType());

        fs.upload(file.getInputStream(), metadata);
    }

    @GetMapping("/count")
    public long getNumberOfPages() {
        return fs.count();
    }

    @GetMapping("/files")
    public List<FileHeaderDTO> getFiles(@RequestParam("page") int page, @RequestParam("size") int size) {
        Page<FileHeader> p = fs.getPage(page, size);
        p.getTotalPages();

        return fs.getPage(page, size)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private FileHeaderDTO convertToDto(FileHeader fileHeader) {
        FileHeaderDTO fileHeaderDTO = modelMapper.map(fileHeader, FileHeaderDTO.class);
        fileHeaderDTO.setKeywords(
                fileHeader.getKeywords().stream().map(Keyword::getName).toList());
        return fileHeaderDTO;
    }
}

