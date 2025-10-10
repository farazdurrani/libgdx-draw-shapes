package com.faraz.other;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class DownloadFile {
  private static final String urlFilePath = "/home/faraz/Downloads/downloadlist(4).txt";
  private static final String finalDestinationOfFiles = "/home/faraz/Downloads/lidarthrujava/";

  private static final List<CompletableFuture<Void>> FUTURES = new ArrayList<>();
  private static final List<FileObject> FILES = new ArrayList<>();
  private static final Executor THREAD_POOL = Executors.newFixedThreadPool(5);

  public static void main(String[] args) throws IOException {
    DownloadFile downloadFile = new DownloadFile();
    List<String> paths = Files.readAllLines(Paths.get(urlFilePath));
    if (paths.contains("SUCCESS")) {
      System.out.println("This file is complete.");
      return;
    }
    paths.remove("FAILURE");
    System.out.println("Number of files to download " + paths.size());
    IntStream.range(0, paths.size()).forEach(index -> downloadFile.downloadFile(paths.get(index), index));
    CompletableFuture.allOf(FUTURES.toArray(new CompletableFuture[0])).join();
    System.out.println("Failed Files");
    FILES.stream().filter(f -> !f.isSuccess()).forEach(System.out::println);
    FILES.stream().filter(f -> !f.isSuccess()).findAny().ifPresent(ignore -> paths.add(0, "FAILURE"));
    if (FILES.stream().allMatch(FileObject::isSuccess)) {
      paths.add(0, "SUCCESS");
    }
    Files.delete(Paths.get(urlFilePath));
    Files.write(Paths.get(urlFilePath), paths);
  }

  private static long remoteFileSize(String path) {
    HttpURLConnection connection = null;
    try {
      URL url = new URL(path);
      connection = (HttpURLConnection) url.openConnection();
      long len = connection.getContentLengthLong();
      connection.disconnect();
      return len;
    } catch (Exception e) {
      if (connection != null) {
        connection.disconnect();
      }
      throw new RuntimeException(e);
    }
  }

  private static File createFileIfDoesntExist(String path) {
    File destination = new File(path);
    if (!destination.exists()) {
      try {
        boolean ignore = destination.createNewFile();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return destination;
  }

  private static long localFileSize(File file) {
    try {
      return Files.size(file.toPath());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void downloadFile(String path, int index) {
    FUTURES.add(CompletableFuture.runAsync(() -> {
      FileObject file = new FileObject();
      FILES.add(file);
      file.setIndex(index);
      file.setUrlPath(path);
      file.setLocalFile(createFileIfDoesntExist(finalDestinationOfFiles + index));
      file.setRemoteSize(remoteFileSize(path));
      try {
        downloadFileWithResume(file.getIndex(), file.getRemoteSize(), file.getLocalFile(), file.getUrlPath());
      } catch (Exception ignored) {
        System.out.println(localFileSize(file.getLocalFile()) + "/" + file.getRemoteSize() + " at index " + index);
      }
      file.setLocalSize(localFileSize(file.getLocalFile()));
      file.setSuccess(file.getLocalSize() == file.getRemoteSize());
      if (file.getLocalSize() == file.getRemoteSize()) {
        System.out.println(file.getIndex() + " is complete: " + file);
      }
    }, THREAD_POOL));
  }

  private void downloadFileWithResume(long index, long remoteSize, File destination, String urlPath) {
    long localFileSize = localFileSize(destination);
    if (remoteSize == localFileSize) {
      return;
    }
    System.out.println((localFileSize == 0 ? "Downloading " : "Resuming ") + index + " -> " + localFileSize + "/" + remoteSize);
    BufferedInputStream inputStream = null;
    BufferedOutputStream outputStream = null;
    try {
      URL url = new URL(urlPath);
      long DownloadedSoFar;
      URLConnection urlconnection = url.openConnection();
      FileOutputStream FOS;
      if (destination.exists()) {
        DownloadedSoFar = destination.length();
        urlconnection.setRequestProperty("Range", "bytes=" + DownloadedSoFar + "-");
        FOS = new FileOutputStream(destination, true);
        outputStream = new BufferedOutputStream(FOS);
      } else {
        FOS = new FileOutputStream(destination);
        outputStream = new BufferedOutputStream(FOS);
      }
      urlconnection.connect();
      inputStream = new BufferedInputStream(urlconnection.getInputStream());
      byte[] buffer = new byte[8192];
      int byteCount;
      while ((byteCount = inputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, byteCount);
      } // while
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      try {
        if (inputStream != null) {
          inputStream.close();
        }
        if (outputStream != null) {
          outputStream.flush();
          outputStream.close();
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private static class FileObject {
    private long index;
    private String urlPath;
    private File localFile;
    private long remoteSize;
    private long localSize;
    private boolean success;

    public long getIndex() {
      return index;
    }

    public void setIndex(long index) {
      this.index = index;
    }

    public String getUrlPath() {
      return urlPath;
    }

    public void setUrlPath(String urlPath) {
      this.urlPath = urlPath;
    }

    public File getLocalFile() {
      return localFile;
    }

    public void setLocalFile(File localFile) {
      this.localFile = localFile;
    }

    public long getRemoteSize() {
      return remoteSize;
    }

    public void setRemoteSize(long remoteSize) {
      this.remoteSize = remoteSize;
    }

    public long getLocalSize() {
      return localSize;
    }

    public void setLocalSize(long localSize) {
      this.localSize = localSize;
    }

    public boolean isSuccess() {
      return success;
    }

    public void setSuccess(boolean success) {
      this.success = success;
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof FileObject file)) return false;
      return getIndex() == file.getIndex() && getRemoteSize() == file.getRemoteSize() && getLocalSize() == file.getLocalSize() && isSuccess() == file.isSuccess() && Objects.equals(getUrlPath(), file.getUrlPath()) && Objects.equals(getLocalFile(), file.getLocalFile());
    }

    @Override
    public int hashCode() {
      return Objects.hash(getIndex(), getUrlPath(), getLocalFile(), getRemoteSize(), getLocalSize(), isSuccess());
    }

    @Override
    public String toString() {
      return "FileObject{" + "index=" + index + ", localFile='" + localFile + '\'' + ", remoteSize=" + remoteSize +
              ", " + "localSize=" + localSize + ", success=" + success + ", urlPath='" + urlPath + '\'' + '}';
    }
  }
}
