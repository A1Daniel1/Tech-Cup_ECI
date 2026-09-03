package edu.escuelaing.techcup.shared.storage;

import edu.escuelaing.techcup.shared.exception.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Streams stored binaries (photos, receipts, rulebook) to authenticated users. */
@RestController
@RequestMapping("/api/files")
@Tag(name = "Files")
public class FileController {

    private final FileStorage fileStorage;

    public FileController(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Download a stored file with its original content type")
    public ResponseEntity<InputStreamResource> download(@PathVariable String id) {
        StoredFile file = fileStorage.find(id).orElseThrow(() -> NotFoundException.of("el archivo", id));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.contentType()))
                .contentLength(file.size())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline().filename(file.filename()).build().toString())
                .header(HttpHeaders.CACHE_CONTROL, "private, max-age=3600")
                .body(new InputStreamResource(file.content()));
    }
}
