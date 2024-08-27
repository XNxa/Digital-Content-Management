package com.dcm.backend.api;

import com.dcm.backend.services.FileService;
import com.dcm.backend.utils.Couple;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(LinkController.URL_PATH)
public class LinkController {

    public static final String URL_PATH = "/public/api/file";

    @Autowired
    FileService fs;

    @SneakyThrows
    @GetMapping("/{uuid}")
    public ResponseEntity<Resource> accessLink(@PathVariable String uuid) {
        Couple<InputStreamResource, MediaType> file =
                fs.accessLink(uuid);
        return ResponseEntity.ok().contentType(file.second()).body(file.first());
    }

}
