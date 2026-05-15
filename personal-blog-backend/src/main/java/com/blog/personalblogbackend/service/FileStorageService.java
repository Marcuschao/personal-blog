package com.blog.personalblogbackend.service;

import com.blog.personalblogbackend.config.AppProperties;
import com.blog.personalblogbackend.exception.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "gif", "webp");

    private final Path uploadRoot;

    public FileStorageService(AppProperties appProperties) {
        this.uploadRoot = Path.of(appProperties.getUploadDir()).toAbsolutePath().normalize();
    }

    public String saveDiaryImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ServiceException(400, "请选择图片文件");
        }
        String original = file.getOriginalFilename();
        String ext = extensionOf(original);
        if (!ALLOWED_EXT.contains(ext)) {
            throw new ServiceException(400, "仅支持 jpg、jpeg、png、gif、webp");
        }
        LocalDate now = LocalDate.now();
        String year = String.valueOf(now.getYear());
        String month = String.format("%02d", now.getMonthValue());
        Path dir = uploadRoot.resolve("diary").resolve(year).resolve(month);
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new ServiceException(500, "无法创建上传目录");
        }
        String name = UUID.randomUUID().toString().replace("-", "") + "." + ext;
        Path target = dir.resolve(name);
        try {
            file.transferTo(target);
        } catch (IOException e) {
            throw new ServiceException(500, "图片保存失败");
        }
        return "/upload/diary/" + year + "/" + month + "/" + name;
    }

    private static String extensionOf(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).trim().toLowerCase(Locale.ROOT);
    }
}
