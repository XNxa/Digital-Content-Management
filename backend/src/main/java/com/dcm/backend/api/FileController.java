package com.dcm.backend.api;

import com.dcm.backend.dto.FileHeaderDTO;
import com.dcm.backend.entities.FileHeader;
import com.dcm.backend.entities.Keyword;
import com.dcm.backend.services.FileService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            throws Exception {

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
    public List<FileHeaderDTO> getFiles(@RequestParam("page") int page,
                                        @RequestParam("size") int size) {
        Page<FileHeader> p = fs.getPage(page, size);
        p.getTotalPages();

        return fs.getPage(page, size)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/filedata")
    public ResponseEntity<Resource> getFileData(@RequestParam("filename") String filename) throws
            Exception {
        InputStreamResource resource = fs.getFile(filename);
        return ResponseEntity.ok().contentType(fs.getFileType(filename)).body(resource);
    }

    private FileHeaderDTO convertToDto(FileHeader fileHeader) {
        FileHeaderDTO fileHeaderDTO = modelMapper.map(fileHeader, FileHeaderDTO.class);
        fileHeaderDTO.setKeywords(
                fileHeader.getKeywords().stream().map(Keyword::getName).toList());
        return fileHeaderDTO;
    }
}

