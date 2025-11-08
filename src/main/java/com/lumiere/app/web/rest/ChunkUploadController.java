package com.lumiere.app.web.rest;

import com.lumiere.app.service.ChunkUploadService;
import com.lumiere.app.service.dto.AttachmentDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/chunk-upload")
public class ChunkUploadController {

    private final ChunkUploadService service;

    public ChunkUploadController(ChunkUploadService service) {
        this.service = service;
    }

    @PostMapping("/chunk")
    public ResponseEntity<String> uploadChunk(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Upload-Id") String uploadId,
            @RequestHeader("Chunk-Index") int chunkIndex) throws IOException {
        service.saveChunk(uploadId, chunkIndex, file);
        return ResponseEntity.ok("Uploaded chunk " + chunkIndex);
    }

    @PostMapping("/complete")
    public ResponseEntity<?> completeUpload(
            @RequestParam("uploadId") String uploadId,
            @RequestParam("totalChunks") int totalChunks,
            @RequestParam("fileName") String fileName) throws IOException {

        AttachmentDTO finalPath = service.mergeChunks(uploadId, totalChunks, fileName);
        return ResponseEntity.ok().body(finalPath);
    }
}
