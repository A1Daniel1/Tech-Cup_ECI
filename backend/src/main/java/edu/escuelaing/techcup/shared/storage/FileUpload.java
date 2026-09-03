package edu.escuelaing.techcup.shared.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import org.springframework.web.multipart.MultipartFile;

/** Framework-neutral view of an uploaded file, so the {@link FileStorage} port has no web imports. */
public record FileUpload(String filename, String contentType, long size, InputStream content) {

    public static FileUpload from(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("Debe adjuntar un archivo; el archivo enviado está vacío.");
        }
        try {
            String name = file.getOriginalFilename() == null || file.getOriginalFilename().isBlank()
                    ? "upload"
                    : file.getOriginalFilename();
            return new FileUpload(name, file.getContentType(), file.getSize(), file.getInputStream());
        } catch (IOException ex) {
            throw new UncheckedIOException("Could not read uploaded file", ex);
        }
    }
}
