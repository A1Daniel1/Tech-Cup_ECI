package edu.escuelaing.techcup.shared.storage;

import java.util.Set;

/** Allowed content types per upload use case. */
public enum FileKind {
    IMAGE(Set.of("image/png", "image/jpeg", "image/webp")),
    PDF(Set.of("application/pdf")),
    IMAGE_OR_PDF(Set.of("image/png", "image/jpeg", "image/webp", "application/pdf"));

    private final Set<String> contentTypes;

    FileKind(Set<String> contentTypes) {
        this.contentTypes = contentTypes;
    }

    public Set<String> contentTypes() {
        return contentTypes;
    }

    public boolean accepts(String contentType) {
        return contentType != null && contentTypes.contains(contentType.toLowerCase());
    }
}
