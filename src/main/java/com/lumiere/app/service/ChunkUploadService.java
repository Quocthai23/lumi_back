package com.lumiere.app.service;


import com.lumiere.app.service.dto.AttachmentDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ChunkUploadService {
    void saveChunk(String uploadId, int chunkIndex, MultipartFile file) throws IOException;

    AttachmentDTO mergeChunks(String uploadId, int totalChunks, String fileName) throws IOException;
}
