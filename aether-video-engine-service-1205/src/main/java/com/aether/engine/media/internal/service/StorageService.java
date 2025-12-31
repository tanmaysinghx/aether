package com.aether.engine.media.internal.service;

import java.io.InputStream;
import java.nio.file.Path;

public interface StorageService {
    /**
     * Store a file from an input stream.
     * 
     * @param inputStream The content to store.
     * @param filename    The requested filename (may be adjusted for uniqueness or
     *                    path).
     * @return The resulting path relative to storage root.
     */
    Path store(InputStream inputStream, String filename);

    /**
     * Get the absolute path to a stored file (for FFmpeg etc).
     */
    Path getAbsolutePath(String filename);

    /**
     * Create a directory if it doesn't exist.
     */
    void createDirectory(String dirName);
}
