package com.dcm.backend.api;

import com.dcm.backend.dto.FileHeaderDTO;
import com.dcm.backend.dto.FileUploadDTO;
import com.dcm.backend.entities.File;
import com.dcm.backend.entities.Keyword;
import com.dcm.backend.services.FileService;
import io.minio.errors.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class BackendController {

    @Autowired
    private FileService fs;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/upload")
    public void uploadFile(@ModelAttribute FileUploadDTO file)
            throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        fs.uploadFile(file);
    }

    @GetMapping("/files")
    public List<FileHeaderDTO> getFiles() {
        return fs.getAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

    }

    private FileHeaderDTO convertToDto(File file) {
        FileHeaderDTO fileHeaderDTO = modelMapper.map(file, FileHeaderDTO.class);
        fileHeaderDTO.setKeywords(file.getKeywords().stream().map(Keyword::getName).toList());
        return fileHeaderDTO;
    }
}

