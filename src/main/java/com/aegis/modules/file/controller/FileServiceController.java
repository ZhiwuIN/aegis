package com.aegis.modules.file.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.aegis.common.constant.FileConstants;
import com.aegis.common.exception.BusinessException;
import com.aegis.common.file.FileStorageServiceFactory;
import com.aegis.common.file.StoragePlatform;
import com.aegis.common.file.service.FileStorageService;
import com.aegis.modules.file.domain.entity.FileMetadata;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: xuesong.lei
 * @Date: 2025/8/28 21:24
 * @Description: 文件服务接口
 */
@Slf4j
@RestController
@Api(tags = "文件服务接口")
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileServiceController {

    private final FileStorageServiceFactory fileStorageServiceFactory;

    @ApiOperation("文件上传")
    @PostMapping("/upload")
    public FileMetadata uploadFile(@RequestParam("file") MultipartFile file, @RequestParam(value = "directory", required = false) String directory) {
        FileStorageService storageService = fileStorageServiceFactory.getFileStorageService();
        FileMetadata result = storageService.upload(file, directory);
        log.info("文件上传成功: {}", result.getFileName());
        return result;
    }

    @ApiOperation("批量文件上传")
    @PostMapping("/upload/batch")
    public List<FileMetadata> uploadFiles(@RequestParam("files") MultipartFile[] files, @RequestParam(value = "directory", required = false) String directory) {
        FileStorageService storageService = fileStorageServiceFactory.getFileStorageService();
        List<FileMetadata> results = Arrays.stream(files)
                .map(file -> storageService.upload(file, directory))
                .collect(Collectors.toList());
        log.info("批量文件上传成功，共{}个文件", results.size());
        return results;
    }

    @ApiOperation("文件下载")
    @GetMapping("/download")
    public void download(@RequestParam("filePath") String filePath, HttpServletResponse response) throws Exception {
        FileStorageService storageService = fileStorageServiceFactory.getFileStorageService();

        // 从 filePath 解析文件名
        String fileName = Paths.get(filePath).getFileName().toString();

        try (InputStream inputStream = storageService.download(filePath);
             ServletOutputStream outputStream = response.getOutputStream()) {

            // 设置响应头
            response.reset();
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + URLEncoder.encode(fileName, String.valueOf(StandardCharsets.UTF_8)) + "\"");
            // 不设置 Content-Length，浏览器会自动处理流结束

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
        } catch (Exception e) {
            log.error("文件下载失败->{}", e.getMessage());
            throw new BusinessException("文件下载失败");
        }
    }

    @ApiOperation("指定存储平台上传文件")
    @PostMapping("/upload/{platform}")
    public FileMetadata uploadFileWithPlatform(@PathVariable StoragePlatform platform, @RequestParam("file") MultipartFile file, @RequestParam(value = "directory", required = false) String directory) {
        FileStorageService storageService = fileStorageServiceFactory.getFileStorageService(platform);
        FileMetadata result = storageService.upload(file, directory);
        log.info("文件上传成功到{}: {}", platform.getDescription(), result.getFileName());
        return result;
    }

    @ApiOperation("文件删除")
    @DeleteMapping("/delete")
    public String deleteFile(@RequestParam("filePath") String filePath) {
        FileStorageService storageService = fileStorageServiceFactory.getFileStorageService();
        boolean deleted = storageService.delete(filePath);
        if (deleted) {
            log.info("文件删除成功: {}", filePath);
            return "文件删除成功";
        } else {
            return "文件删除失败";
        }
    }

    @ApiOperation("检查文件是否存在")
    @GetMapping("/exists")
    public String checkFileExists(@RequestParam("filePath") String filePath) {
        FileStorageService storageService = fileStorageServiceFactory.getFileStorageService();
        boolean exists = storageService.exists(filePath);
        if (exists) {
            log.info("文件存在: {}", filePath);
            return "存在";
        } else {
            return "不存在";
        }
    }

    @ApiOperation("获取支持的存储平台")
    @GetMapping("/platforms")
    public List<StoragePlatform> getSupportedPlatforms() {
        return Arrays.asList(StoragePlatform.values());
    }

    /**
     * 获取预签名上传URL - 用于前端直传
     */
    @ApiOperation("获取预签名上传URL")
    @PostMapping("/presigned-upload-url")
    public Map<String, String> getPresignedUploadUrl(
            @RequestParam String fileName,
            @RequestParam(required = false) String directory) {

        FileStorageService storageService = fileStorageServiceFactory.getFileStorageService();

        String filePath = buildFilePath(directory, fileName);
        Duration expiration = Duration.ofMinutes(10); // 10分钟过期

        String presignedUrl = storageService.generatePresignedUploadUrl(filePath, expiration);

        Map<String, String> response = new HashMap<>();
        response.put("uploadUrl", presignedUrl);
        response.put("filePath", filePath);
        response.put("expiresIn", "600"); // 秒

        return response;
    }

    /**
     * 获取临时下载URL
     */
    @ApiOperation("获取临时下载URL")
    @GetMapping("/temporary-download-url")
    public Map<String, String> getTemporaryDownloadUrl(
            @RequestParam String filePath,
            @RequestParam(required = false, defaultValue = "3600") long expirationSeconds) {
        FileStorageService storageService = fileStorageServiceFactory.getFileStorageService();

        if (!storageService.exists(filePath)) {
            return null;
        }

        Duration expiration = Duration.ofSeconds(expirationSeconds);
        String temporaryUrl = storageService.getTemporaryDownloadUrl(filePath, expiration);

        Map<String, String> response = new HashMap<>();
        response.put("downloadUrl", temporaryUrl);
        response.put("expiresIn", String.valueOf(expirationSeconds));

        return response;
    }

    private String buildFilePath(String directory, String fileName) {
        String uniqueFileName = IdUtil.simpleUUID() + FileConstants.POINT + FileUtil.extName(fileName);
        return (StrUtil.isNotBlank(directory) ? directory + FileConstants.SEPARATOR : "")
                + FileConstants.FILE_FOLDER + FileConstants.SEPARATOR + uniqueFileName;
    }
}
