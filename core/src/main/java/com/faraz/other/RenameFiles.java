package com.faraz.other;

import java.io.File;

public class RenameFiles {
  public static void main(String[] args) {
    String folderPath = "/home/faraz/Downloads/lidarthrujava";
    File folder = new File(folderPath);

    if (folder.exists() && folder.isDirectory()) {
      File[] files = folder.listFiles();

      if (files != null) {
        for (File file : files) {
          if (file.isFile()) { // Ensure it's a file, not a subdirectory
            String fileName = file.getName();
            String fileExtension = ".laz";
            String newFileName = fileName + fileExtension;
            File renamedFile = new File(folderPath + File.separator + newFileName);

            if (file.renameTo(renamedFile)) {
              System.out.println("Renamed: " + fileName + " -> " + newFileName);
            } else {
              System.out.println("Failed to rename: " + fileName);
            }
          }
        }
      } else {
        System.out.println("No files found in the directory.");
      }
    } else {
      System.out.println("Directory does not exist or is not a directory.");
    }
  }
}
