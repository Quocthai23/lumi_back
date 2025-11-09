// com.lumiere.app.web.rest.MediaResource
package com.lumiere.app.web.rest;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.nio.file.*;

@RestController
@RequestMapping("/api/media")
public class MediaResource {
    private final Path mediaRoot;

    public MediaResource(Path mediaRoot) {
        this.mediaRoot = mediaRoot;
    }

    @GetMapping("/{name}")
    public void get(@PathVariable String name, HttpServletResponse resp) {
        // Chỉ cho phép tên file kiểu UUID.ext: chặn path traversal
        if (!name.matches("^[a-fA-F0-9\\-]{8,}\\.\\w{1,10}$")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Path file = mediaRoot.resolve("attachments").resolve(name).normalize();
        if (!file.startsWith(mediaRoot)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        if (!Files.exists(file)) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try (InputStream in = Files.newInputStream(file, StandardOpenOption.READ)) {
            String ct = Files.probeContentType(file);
            resp.setHeader(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000, immutable");
            resp.setHeader(HttpHeaders.ETAG, String.valueOf(Files.getLastModifiedTime(file).toMillis()));
            resp.setContentType(ct != null ? ct : MediaType.APPLICATION_OCTET_STREAM_VALUE);
            resp.setContentLengthLong(Files.size(file));
            FileCopyUtils.copy(in, resp.getOutputStream());
            resp.flushBuffer();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
