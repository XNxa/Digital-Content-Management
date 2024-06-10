package com.dcm.backend.services;

import com.dcm.backend.util.FileTestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class FileServiceTest {

    @Autowired
    private FileTestUtils utils;

    @BeforeEach
    public void setup() throws Exception {
        utils.clearAll();
    }

    @AfterEach
    public void cleanup() throws Exception {
        utils.clearAll();
    }

    @Test
    public void testUpload() throws Exception {

    }

    @Test
    public void testCount() throws Exception {

    }

    @Test
    public void testGetPage() throws Exception {

    }

    @Test
    public void testGetFile() throws Exception {

    }

}



