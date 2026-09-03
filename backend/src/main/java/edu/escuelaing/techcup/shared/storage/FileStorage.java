package edu.escuelaing.techcup.shared.storage;

import java.util.Optional;

/**
 * Port for binary storage (hexagonal architecture). Domain modules depend on this interface;
 * the only adapter today is {@link GridFsFileStorage} (MongoDB GridFS).
 */
public interface FileStorage {

    /**
     * Validates the upload against {@code kind} (content type) and the configured size limit,
     * stores it and returns its opaque id.
     *
     * @throws InvalidFileException when the file is empty, too large or of an unsupported type
     */
    String store(FileUpload upload, FileKind kind);

    /** Loads a stored file by id; empty when the id is malformed or unknown. */
    Optional<StoredFile> find(String id);
}
