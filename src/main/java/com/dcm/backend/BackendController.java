package com.dcm.backend;

import com.dcm.backend.beans.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BackendController {

    @Autowired
    BackendRepository facade;

    @GetMapping("/files")
    public Iterable<File> getAll() {
        return facade.findAll();
    }

    @GetMapping("/new")
    public void add(@RequestParam("name") String name) {
        File f = new File();
        f.setFilename(name);
        facade.save(f);
    }

}
