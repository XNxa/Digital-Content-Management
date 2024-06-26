package com.dcm.backend.api;

import com.dcm.backend.dto.FileFilterDTO;
import com.dcm.backend.dto.FileHeaderDTO;
import com.dcm.backend.entities.FileHeader;
import com.dcm.backend.entities.Keyword;
import com.dcm.backend.enumeration.Status;
import com.dcm.backend.services.FileService;
import com.dcm.backend.services.KeywordService;
import jakarta.validation.Valid;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class FileController {

    @Autowired
    private FileService fs;

    @Autowired
    private KeywordService ks;

    @Autowired
    private ModelMapper modelMapper;

    @SneakyThrows
    @PostMapping("/upload")
    public void uploadFile(@RequestPart("file") MultipartFile file,
                           @RequestPart("metadata") @Valid FileHeaderDTO metadata) {
        fs.upload(file.getInputStream(), metadata);
    }

    @GetMapping("/count")
    public long getNumberOfElements() {
        return fs.count();
    }

    @PostMapping("/files")
    public List<FileHeaderDTO> getFiles(@RequestBody @Valid FileFilterDTO filter) {
        Page<FileHeader> p = fs.getPage(filter);
        p.getTotalPages();

        return p.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @SneakyThrows
    @GetMapping("/filedata")
    public ResponseEntity<Resource> getFileData(@RequestParam("filename") String filename) {
        InputStreamResource resource = fs.getFile(filename);
        return ResponseEntity.ok().contentType(fs.getFileType(filename)).body(resource);
    }

    @SneakyThrows
    @GetMapping("/thumbnail")
    public ResponseEntity<Resource> getThumbnail(@RequestParam("filename") String filename) {
        InputStreamResource resource = fs.getThumbnail(filename);
        return ResponseEntity.ok().contentType(fs.getFileType(filename)).body(resource);
    }

    @GetMapping("/keywords")
    public List<String> getKeywords() {
        return ks.getKeywords();
    }

    @SneakyThrows
    @DeleteMapping("/delete")
    public void deleteFile(@RequestParam("filename") String[] filenames) {
        fs.delete(filenames);
    }

    @SneakyThrows
    @GetMapping("/link")
    public String getLink(@RequestParam("filename") String filename) {
        return fs.getLink(filename);
    }

    @SneakyThrows
    @PostMapping("/duplicate")
    public void duplicateFile(@RequestParam("filename") String filename) {
        fs.duplicate(filename);
    }

    @SneakyThrows
    @PutMapping("/update")
    public void updateFile(@RequestParam("filename") String filename,
                           @RequestBody @Valid FileHeaderDTO metadata) {
        fs.update(filename, metadata);
    }

    private FileHeaderDTO convertToDto(FileHeader fileHeader) {
        FileHeaderDTO fileHeaderDTO = modelMapper.map(fileHeader, FileHeaderDTO.class);
        fileHeaderDTO.setKeywords(
                fileHeader.getKeywords().stream().map(Keyword::getName).toList());
        return fileHeaderDTO;
    }
}

