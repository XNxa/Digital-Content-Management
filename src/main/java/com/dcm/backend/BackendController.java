package com.dcm.backend;

import com.dcm.backend.beans.File;
import com.dcm.backend.beans.Keyword;
import com.dcm.backend.beans.Status;
import com.dcm.backend.beans.Version;
import com.dcm.backend.repositories.FileRepository;
import com.dcm.backend.repositories.KeywordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class BackendController {

    @Autowired
    FileService fs;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    KeywordRepository keywordRepository;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("description") String desc,
            @RequestParam("version") Version version,
            @RequestParam("status") Status status,
            @RequestParam("keywords") Collection<String> keywords
    ) {
        if (fs.uploadFile(file)) {
            Collection<Keyword> keywordCollection = new LinkedList<>();
            for (String key : keywords) {
                Optional<Keyword> k = keywordRepository.findById(key);
                if (k.isPresent()) {
                    keywordCollection.add(k.get());
                } else {
                    Keyword saved = keywordRepository.save(new Keyword(key));
                    keywordCollection.add(saved);
                }
            }
            File f = new File(file.getOriginalFilename(), desc, version, status, LocalDateTime.now().toString(), file.getContentType(), file.getSize(), keywordCollection);
            fileRepository.save(f);
            return ResponseEntity.ok("File successfully uploaded");
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/files")
    public ResponseEntity<Iterable<File>> getFiles() {
        return ResponseEntity.ok(fileRepository.findAll());
    }
}

