package com.gdb.file;

import java.io.*;
import java.nio.file.*;
import java.util.List;

/**
 * Provides atomic, crash-resilient file write operations using temp files and atomic rename.
 */
public class AtomicFileWriter {

    // ============================================================
    // 📝 STEP 1: Implement write(String path, List<String> lines)
    //
    // INSTRUCTIONS:
    //   1. Create parent directories if they don't exist.
    //   2. Write lines to a temporary file (path + ".tmp").
    //   3. Flush and close the temporary writer.
    //   4. Atomically move the temporary file to the target path (StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING).
    //   5. If ATOMIC_MOVE fails, fall back to REPLACE_EXISTING.
    // ============================================================
    public static void write(String path, List<String> lines) throws IOException {
        // TODO: Step 1 - Write lines atomically to file
    }
}
