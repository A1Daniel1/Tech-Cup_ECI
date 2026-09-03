package edu.escuelaing.techcup.shared.storage;

import java.io.InputStream;

/** A file read back from storage, ready to be streamed to the client. */
public record StoredFile(String id, String filename, String contentType, long size, InputStream content) {
}
