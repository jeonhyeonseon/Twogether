package com.think_different.think_different.common.file;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileUploadService {

    public String upload(MultipartFile file, String directory) {

        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            String uploadDir = System.getProperty("user.dir")
                    + "/uploads/"
                    + directory
                    + "/";

            File dir = new File(uploadDir);

            if (!dir.exists()) {
                dir.mkdirs();
            }

            String originalFilename = file.getOriginalFilename();
            String savedFilename = UUID.randomUUID() + "_" + originalFilename;

            File savedFile = new File(uploadDir + savedFilename);

            file.transferTo(savedFile);

            return "/uploads/" + directory + "/" + savedFilename;

        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }
}
