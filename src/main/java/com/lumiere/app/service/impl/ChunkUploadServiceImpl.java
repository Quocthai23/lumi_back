package com.lumiere.app.service.impl;

import com.google.cloud.storage.Storage;
import com.google.firebase.cloud.StorageClient;
import com.lumiere.app.service.AttachmentService;
import com.lumiere.app.service.ChunkUploadService;
import com.lumiere.app.service.dto.AttachmentDTO;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.Instant;

@Service
public class ChunkUploadServiceImpl implements ChunkUploadService {

    private final Path tempPath;
    private final Path finalPath;
    private final AttachmentService attachmentService;
    private final Storage storage;

    public ChunkUploadServiceImpl(Path tempPath, Path finalPath, AttachmentService attachmentService) {
        this.tempPath = tempPath;
        this.finalPath = finalPath;
        this.attachmentService = attachmentService;
        if(StorageClient.getInstance() != null){
            this.storage = StorageClient.getInstance().bucket().getStorage();
        }else{
            this.storage = null;
        }
    }

    @Override
    public void saveChunk(String uploadId, int chunkIndex, MultipartFile file) throws IOException {
        Path dir = tempPath.resolve(uploadId);
        Files.createDirectories(dir);

        Path chunkPath = dir.resolve(String.valueOf(chunkIndex));
        Files.copy(file.getInputStream(), chunkPath, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public AttachmentDTO mergeChunks(String uploadId, int totalChunks, String fileName) throws IOException {
        Path dir = tempPath.resolve(uploadId);
        Path finalFile = finalPath.resolve(fileName);

        try (OutputStream os = Files.newOutputStream(finalFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (int i = 0; i < totalChunks; i++) {
                Path chunkPath = dir.resolve(String.valueOf(i));
                Files.copy(chunkPath, os);
            }
        }


        String contentType = Files.probeContentType(finalFile);
        long size = Files.size(finalFile);

        AttachmentDTO attachmentDTO = new AttachmentDTO();
        attachmentDTO.setName(fileName);

        try (InputStream is = Files.newInputStream(finalFile)) {
            MultipartFile multipartFile = new MockMultipartFile(
                fileName, fileName, contentType, is
            );
            attachmentService.uploadAttachment(multipartFile);
        }

        attachmentDTO.setContentType(contentType);
        attachmentDTO.setSize(size);
        attachmentDTO.setUploadedAt(Instant.now());
        FileSystemUtils.deleteRecursively(dir);

        AttachmentDTO saved = attachmentService.save(attachmentDTO);

        FileSystemUtils.deleteRecursively(dir);

        return saved;
    }


//    private void getImageUrl(AttachmentDTO image, Cache cache) {
//        String cacheKey = "image_url_" + image.getName();
//        String cachedUrl = cache != null ? cache.get(cacheKey).toString() : null;
//
//        if (cachedUrl != null) {
//            image.setUrl(cachedUrl);
//        } else {
//            try {
//                BlobId blobId = BlobId.of(Const.BUCKET_NAME, image.getName());
//                BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
//                    .setContentType(".jpg")
//                    .build();
//                URL url =  storage.signUrl(blobInfo, 30, TimeUnit.MINUTES);
//                String urlString = url.toString();
//                image.setUrl(urlString);
//
//                if (cache != null) {
//                    cache.put(cacheKey, urlString);
//                }
//            } catch (Exception ignore) {
//
//            }
//        }
//    }

}
