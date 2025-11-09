package com.lumiere.app.service.impl;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.firebase.cloud.StorageClient;
import com.lumiere.app.service.AttachmentService;
import com.lumiere.app.service.ChunkUploadService;
import com.lumiere.app.service.dto.AttachmentDTO;
import com.lumiere.app.utils.Const;
import com.lumiere.app.utils.PublicUrlBuilder;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class ChunkUploadServiceImpl implements ChunkUploadService {

    private final Path tempPath;
    private final Path mediaRoot;
    private final AttachmentService attachmentService;
    private final PublicUrlBuilder urlBuilder;

    public ChunkUploadServiceImpl(Path tempPath, Path mediaRoot, AttachmentService attachmentService, PublicUrlBuilder urlBuilder) {
        this.tempPath = tempPath;
        this.mediaRoot = mediaRoot;
        this.attachmentService = attachmentService;
        this.urlBuilder = urlBuilder;
    }

    @Override
    public void saveChunk(String uploadId, int chunkIndex, MultipartFile file) throws IOException {
        Path dir = tempPath.resolve(uploadId);
        Files.createDirectories(dir);

        Path chunkPath = dir.resolve(String.valueOf(chunkIndex));
        Files.copy(file.getInputStream(), chunkPath, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public AttachmentDTO mergeChunks(String uploadId, int totalChunks, String originalFileName) throws IOException {
        // 0) Validate
        if (totalChunks <= 0) throw new IllegalArgumentException("totalChunks invalid");
        if (uploadId == null || uploadId.isBlank()) throw new IllegalArgumentException("uploadId missing");

        // 1) Đường dẫn tạm & xác thực chunk
        Path uploadDir = tempPath.resolve(uploadId).normalize(); // <-- luôn dưới uploads/tmp/<uploadId>
        if (!Files.isDirectory(uploadDir)) {
            throw new IOException("Upload temp dir not found: " + uploadDir);
        }
        for (int i = 0; i < totalChunks; i++) {
            Path chunk = uploadDir.resolve(String.valueOf(i)).normalize();
            if (!chunk.startsWith(uploadDir) || !Files.exists(chunk)) {
                throw new IOException("Missing chunk index: " + i);
            }
        }

        // 2) Tên file an toàn + đuôi
        String ext = guessExtension(originalFileName);         // .jpg/.png/... hoặc ".bin"
        String safeName = UUID.randomUUID() + ext;             // tên mới, tránh path traversal
        Path finalDir  = mediaRoot.resolve("attachments");     // <-- KHÔNG đặt dưới tmp
        Files.createDirectories(finalDir);

        Path finalFile = finalDir.resolve(safeName).normalize();
        if (!finalFile.startsWith(finalDir)) {
            throw new IOException("Invalid final path");
        }

        // 3) Gộp qua file .part rồi move ATOMIC
        Path staging = finalDir.resolve(safeName + ".part").normalize();
        try (OutputStream os = Files.newOutputStream(
            staging, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
            for (int i = 0; i < totalChunks; i++) {
                Files.copy(uploadDir.resolve(String.valueOf(i)), os);
            }
        } catch (IOException e) {
            Files.deleteIfExists(staging);
            throw e;
        }
        Files.move(staging, finalFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);

        // 4) Metadata
        String contentType = Optional.ofNullable(Files.probeContentType(finalFile))
            .orElseGet(() -> mimeFromExt(ext));
        long size = Files.size(finalFile);

        // 5) Tạo DTO + URL tuyệt đối (domain)
        AttachmentDTO dto = new AttachmentDTO();
        dto.setName(safeName);
        dto.setUrl(urlBuilder.media(safeName));  // ví dụ: https://your-domain.com/api/media/<safeName>
        dto.setContentType(contentType);
        dto.setSize(size);
        dto.setUploadedAt(Instant.now());

        // 6) Dọn rác tạm
        try { FileSystemUtils.deleteRecursively(uploadDir); } catch (Exception ignore) {}

        // 7) Lưu DB
        return attachmentService.save(dto);
    }

    // Helpers
    private static String guessExtension(String original) {
        if (original == null) return ".bin";
        String name = original.trim();
        int dot = name.lastIndexOf('.');
        if (dot > -1 && dot < name.length() - 1) {
            String e = name.substring(dot + 1).toLowerCase().replaceAll("[^a-z0-9]", "");
            return switch (e) {
                case "jpeg" -> ".jpg";
                case "jpg", "png", "webp", "gif", "bmp", "pdf" -> "." + e;
                default -> "." + (e.isBlank() ? "bin" : e);
            };
        }
        return ".bin";
    }

    private static String mimeFromExt(String ext) {
        return switch (ext.toLowerCase()) {
            case ".jpg", ".jpeg" -> "image/jpeg";
            case ".png" -> "image/png";
            case ".webp" -> "image/webp";
            case ".gif" -> "image/gif";
            case ".bmp" -> "image/bmp";
            case ".pdf" -> "application/pdf";
            default -> "application/octet-stream";
        };
    }

}
