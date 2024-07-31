package com.dcm.backend.api;

import com.dcm.backend.annotations.LogEvent;
import com.dcm.backend.annotations.Loggable;
import com.dcm.backend.dto.FileFilterDTO;
import com.dcm.backend.dto.FileHeaderDTO;
import com.dcm.backend.dto.FilenameDTO;
import com.dcm.backend.services.FileService;
import com.dcm.backend.services.KeywordService;
import com.dcm.backend.utils.mappers.FileHeaderMapper;
import jakarta.validation.Valid;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/file")
@CrossOrigin
public class FileController extends Loggable {

    @Autowired
    private FileService fs;

    @Autowired
    private KeywordService ks;

    @Autowired
    private FileHeaderMapper fileHeaderMapper;

    @LogEvent
    @SneakyThrows
    @PostMapping("/upload")
    @PreAuthorize("hasRole(@util.buildPermission(#metadata, 'import'))")
    public void uploadFile(@RequestPart("file") MultipartFile file, @RequestPart("metadata") @Valid FileHeaderDTO metadata) {
        fs.upload(file.getInputStream(), metadata);
    }

    @LogEvent
    @PostMapping("/count")
    public long getNumberOfElements(@RequestBody @Valid FileFilterDTO filter) {
        return fs.count(filter);
    }

    @LogEvent
    @PostMapping("/files")
    public List<FileHeaderDTO> getFiles(@RequestBody @Valid FileFilterDTO filter) {
        return fs.getFiles(filter);
    }

    @LogEvent
    @GetMapping("file/{id}")
    public FileHeaderDTO getFile(@PathVariable int id) {
        return fs.getFileHeader(id);
    }

    @LogEvent
    @SneakyThrows
    @GetMapping("/filedata")
    public ResponseEntity<Resource> getFileData(@ModelAttribute @Valid FilenameDTO file) {
        InputStreamResource resource = fs.getFile(file);
        return ResponseEntity.ok().contentType(fs.getFileType(file)).body(resource);
    }

    @LogEvent
    @SneakyThrows
    @GetMapping("/thumbnail")
    public ResponseEntity<Resource> getThumbnail(@ModelAttribute @Valid FilenameDTO file) {
        InputStreamResource resource = fs.getThumbnail(file);
        return ResponseEntity.ok().contentType(fs.getFileType(file)).body(resource);
    }

    @LogEvent
    @GetMapping("/keywords")
    public List<String> getKeywords() {
        return ks.getKeywords();
    }

    @LogEvent
    @SneakyThrows
    @DeleteMapping("/delete")
    @PreAuthorize("hasRole(@util.buildPermission(#file, 'delete'))")
    public void deleteFile(@ModelAttribute @Valid FilenameDTO file) {
        fs.delete(file);
    }

    @LogEvent
    @SneakyThrows
    @GetMapping("/link")
    @PreAuthorize("hasRole(@util.buildPermission(#file,'share')) or hasRole(@util.buildPermission(#file, 'copy_link'))")
    public String getLink(@ModelAttribute @Valid FilenameDTO file) {
        return fs.getLink(file);
    }

    @LogEvent
    @SneakyThrows
    @PostMapping("/duplicate")
    @PreAuthorize("hasRole(@util.buildPermission(#file, 'duplicate'))")
    public void duplicateFile(@ModelAttribute @Valid FilenameDTO file) {
        fs.duplicate(file);
    }

    @LogEvent
    @SneakyThrows
    @PutMapping("/update")
    @PreAuthorize("hasRole(@util.buildPermission(#file, 'modify'))")
    public void updateFile(@ModelAttribute @Valid FilenameDTO file, @RequestBody @Valid FileHeaderDTO metadata) {
        fs.update(file, metadata);
    }

    @LogEvent
    @GetMapping("/types")
    public Collection<String> getTypes(@ModelAttribute @Valid FilenameDTO file) {
        return fs.getTypes(file.getFolder());
    }

    @LogEvent
    @GetMapping("/new-stats")
    public Collection<Long> getNewStats() {
        return fs.getNewStats(LocalDate.now().minusDays(7));
    }

    @LogEvent
    @GetMapping("/status-stats")
    public Collection<Long> getStatusStats() {
        return fs.getStatusStats();
    }

    @LogEvent
    @GetMapping("/search")
    public List<FileHeaderDTO> search(@RequestParam String query) {
        return fs.search(query);
    }
}

