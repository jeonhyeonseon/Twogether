package com.think_different.think_different.common.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class FileUploadService {

    private static final String UPLOAD_URL_PREFIX = "/uploads/";
    private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB

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

    // 이미지 파일 검증 (확장자/타입/용량) - 문제가 있으면 사용자용 메시지, 없으면 null
    public String validateImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            return null;
        }

        String contentType = file.getContentType();

        if (!IMAGE_EXTENSIONS.contains(imageExtension(file)) || contentType == null || !contentType.startsWith("image/")) {
            return "jpg, png, gif, webp 이미지 파일만 업로드할 수 있습니다.";
        }

        if (file.getSize() > MAX_IMAGE_SIZE) {
            return "이미지는 5MB 이하만 업로드할 수 있습니다.";
        }

        return null;
    }

    // 이미지 전용 업로드 (저장 파일명은 원본 이름을 쓰지 않고 UUID + 확장자)
    public String uploadImage(MultipartFile file, String directory) {

        if (file == null || file.isEmpty()) {
            return null;
        }

        String errorMessage = validateImage(file);
        if (errorMessage != null) {
            throw new IllegalArgumentException(errorMessage);
        }

        String extension = imageExtension(file);

        try {
            Path uploadDir = uploadRoot().resolve(directory);
            Files.createDirectories(uploadDir);

            String savedFilename = UUID.randomUUID() + "." + extension;
            file.transferTo(uploadDir.resolve(savedFilename));

            return UPLOAD_URL_PREFIX + directory + "/" + savedFilename;

        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }

    // 업로드한 파일 삭제 (uploads 폴더 밖의 경로는 무시)
    public void delete(String fileUrl) {

        if (!StringUtils.hasText(fileUrl) || !fileUrl.startsWith(UPLOAD_URL_PREFIX)) {
            return;
        }

        Path root = uploadRoot();
        Path target = root.resolve(fileUrl.substring(UPLOAD_URL_PREFIX.length())).normalize();

        if (!target.startsWith(root)) {
            return;
        }

        try {
            Files.deleteIfExists(target);
        } catch (IOException e) {
            // 파일 삭제 실패는 화면 동작에 영향이 없으므로 로그만 남김
            log.warn("업로드 파일 삭제 실패: {}", fileUrl, e);
        }
    }

    private String imageExtension(MultipartFile file) {
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        return extension == null ? "" : extension.toLowerCase(Locale.ROOT);
    }

    private Path uploadRoot() {
        return Paths.get(System.getProperty("user.dir"), "uploads").toAbsolutePath().normalize();
    }
}
