package com.ruby_dev.movieapi.controllers;

import com.ruby_dev.movieapi.services.FileService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/api/v1/file")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService){
        this.fileService = fileService;
    }

    @Value("${project.poster_path}")
    private String path;
    
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFileController(@RequestPart MultipartFile file) throws IOException {
        String uploadedFile = fileService.uploadFile(path, file);
        return new ResponseEntity<>("File Uploaded: " + uploadedFile, HttpStatus.CREATED);
    }

    @GetMapping("/{fileName}")
    public void getFileController(@PathVariable String fileName, HttpServletResponse response) throws IOException {
       InputStream resourceFile = fileService.getResourceFile(path, fileName);
       response.setContentType(MediaType.IMAGE_PNG_VALUE);
       StreamUtils.copy(resourceFile, response.getOutputStream());
    }
}
