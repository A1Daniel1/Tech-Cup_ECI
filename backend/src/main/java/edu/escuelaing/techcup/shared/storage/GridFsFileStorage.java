package edu.escuelaing.techcup.shared.storage;

import com.mongodb.client.gridfs.model.GridFSFile;
import edu.escuelaing.techcup.shared.config.AppProperties;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Optional;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;

/** {@link FileStorage} adapter backed by MongoDB GridFS. */
@Component
public class GridFsFileStorage implements FileStorage {

    private static final String META_CONTENT_TYPE = "contentType";

    private final GridFsTemplate gridFsTemplate;
    private final long maxFileSizeBytes;

    public GridFsFileStorage(GridFsTemplate gridFsTemplate, AppProperties properties) {
        this.gridFsTemplate = gridFsTemplate;
        this.maxFileSizeBytes = properties.storage().maxFileSizeBytes();
    }

    @Override
    public String store(FileUpload upload, FileKind kind) {
        validate(upload, kind);
        Document metadata = new Document(META_CONTENT_TYPE, upload.contentType().toLowerCase());
        ObjectId id = gridFsTemplate.store(upload.content(), upload.filename(), upload.contentType(), metadata);
        return id.toHexString();
    }

    @Override
    public Optional<StoredFile> find(String id) {
        if (id == null || !ObjectId.isValid(id)) {
            return Optional.empty();
        }
        GridFSFile file = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(new ObjectId(id))));
        if (file == null) {
            return Optional.empty();
        }
        GridFsResource resource = gridFsTemplate.getResource(file);
        try {
            String contentType = file.getMetadata() != null
                    ? file.getMetadata().getString(META_CONTENT_TYPE)
                    : null;
            return Optional.of(new StoredFile(id, file.getFilename(),
                    contentType != null ? contentType : "application/octet-stream",
                    file.getLength(), resource.getInputStream()));
        } catch (IOException ex) {
            throw new UncheckedIOException("Could not open stored file " + id, ex);
        }
    }

    private void validate(FileUpload upload, FileKind kind) {
        if (upload.size() <= 0) {
            throw new InvalidFileException("Debe adjuntar un archivo; el archivo enviado está vacío.");
        }
        if (upload.size() > maxFileSizeBytes) {
            throw new InvalidFileException("El archivo supera el tamaño máximo de " + maxFileSizeBytes + " bytes.");
        }
        if (!kind.accepts(upload.contentType())) {
            throw new InvalidFileException("El tipo de archivo '" + upload.contentType()
                    + "' no es compatible. Formatos permitidos: " + String.join(", ", kind.contentTypes()) + ".");
        }
    }
}
