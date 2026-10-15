package com.gdb.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * Manages timestamped backups of data files and maintains retention limits.
 */
public class BackupManager {

    // ============================================================
    // 📝 STEP 6: Implement backup(String path, int maxBackups)
    //
    // INSTRUCTIONS:
    //   1. If target path does not exist, return early.
    //   2. Copy target file to path + ".bak." + System.currentTimeMillis().
    //   3. Find all existing backup files for target path.
    //   4. If backup count exceeds maxBackups, delete the oldest backups.
    // ============================================================
    public static void backup(String path, int maxBackups) throws IOException {
        // TODO: Step 6 - create timestamped backup and prune excess backups
    }
}
