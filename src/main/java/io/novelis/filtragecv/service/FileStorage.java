package io.novelis.filtragecv.service;

import io.novelis.filtragecv.config.ApplicationProperties;
import io.novelis.filtragecv.service.textprocessing.DocProcessor;
import io.novelis.filtragecv.service.textprocessing.PdfProcessor;
import io.novelis.filtragecv.service.textprocessing.TextProcessor;
import io.novelis.filtragecv.web.rest.errors.FileStorageException;
import io.novelis.filtragecv.web.rest.errors.NotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class FileStorage {
    public Path getFileStorageLocation() {
        return fileStorageLocation;
    }

    private final Path fileStorageLocation;
    private TextProcessor textProcessor;
    public FileStorage(ApplicationProperties applicationProperties) {
        this.fileStorageLocation = Paths.get(applicationProperties.getUploadDir())
            .toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String getFileExtension(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileStorageException("Failed to store empty file");
        }
        String fileName = file.getOriginalFilename();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        else return "";
    }

    public String storeFile(MultipartFile file) {

        String fileExtension = getFileExtension(file);
        if (!fileExtension.equals("pdf") && !fileExtension.equals("docx") && !fileExtension.equals("doc")) {
            throw new FileStorageException("unrecognized file type :" + fileExtension);
        }
        String fileName = "" + new Date().getTime() + "." + fileExtension;
        try {
            // Copy file to the target location
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            String filePath = this.fileStorageLocation.resolve(fileName).normalize().toString();

            if (fileExtension.equals("pdf")) {
                textProcessor = new PdfProcessor(filePath);
            } else {
                textProcessor = new DocProcessor(filePath);
            }
            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new NotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new NotFoundException("File not found " + fileName, ex);
        }
    }
    public HashMap<String, Integer> getWords() {
        return textProcessor.getWords();
    }
    public void deleteFile(String fileName) {
        try {
            Files.delete(this.fileStorageLocation.resolve(fileName).normalize());
        } catch (IOException e) {
            throw new NotFoundException("file not found to delete it");
        }
    }
    public boolean validWord(String word) {
        return textProcessor.validWord(word);
    }
}
