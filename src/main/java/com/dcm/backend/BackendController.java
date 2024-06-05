package com.dcm.backend;

import com.dcm.backend.beans.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class BackendController {

    @Autowired
    FileService fs;


    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (fs.uploadFile(file)) {
            return ResponseEntity.ok("File successfully uploaded");
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

}
