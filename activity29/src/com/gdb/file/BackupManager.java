package com.gdb.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * Manages timestamped backups of data files and maintains retention limits.
 */
public class BackupManager {

    public static void backup(String path, int maxBackups) throws IOException {
        Path targetPath = Paths.get(path);
        if (!Files.exists(targetPath)) {
            return;
        }

        Path backupPath = Paths.get(path + ".bak." + System.currentTimeMillis());
        Files.copy(targetPath, backupPath, StandardCopyOption.REPLACE_EXISTING);

        if (maxBackups > 0) {
            pruneBackups(path, maxBackups);
        }
    }

    private static void pruneBackups(String basePath, int maxBackups) {
        File parentDir = new File(basePath).getParentFile();
        if (parentDir == null || !parentDir.exists()) {
            parentDir = new File(".");
        }

        String fileName = new File(basePath).getName();
        File[] backups = parentDir.listFiles((dir, name) -> name.startsWith(fileName + ".bak."));
        if (backups != null && backups.length > maxBackups) {
            Arrays.sort(backups, Comparator.comparingLong(File::lastModified));
            int toDelete = backups.length - maxBackups;
            for (int i = 0; i < toDelete; i++) {
                backups[i].delete();
            }
        }
    }
}
