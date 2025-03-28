package com.finalproject.sulbao.common.file;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@Slf4j
public class FileService {

    private final AmazonS3Client amazonS3Client;

    @Value("${spring.s3.bucket}")
    private String bucketName;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public FileService(AmazonS3Client amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
    }

    public String getUuidFileName(String fileName) {
        String ext = fileName.substring(fileName.indexOf(".") + 1);
        return UUID.randomUUID().toString() + "." + ext;
    }

    // 파일정보 세팅
    public ObjectMetadata setMetadata(MultipartFile multipartFile){

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());
        return objectMetadata;
    }

    //NOTICE: filePath의 맨 앞에 /는 안붙여도됨. ex) history/images
    public FileDto uploadFiles(MultipartFile multipartFile, String filePath) {

        return getFileDto(multipartFile, filePath, bucketName);
    }

    // 상품, 매거진 업로드
    public FileDto uploadFile(MultipartFile multipartFile, String filePath, String ncpBucketName) {

        return getFileDto(multipartFile, filePath, ncpBucketName);
    }

    private FileDto getFileDto(MultipartFile multipartFile, String filePath, String bucketName) {
        String originalFileName = multipartFile.getOriginalFilename();
        String uploadFileName = getUuidFileName(originalFileName);
        String uploadFileUrl = "";

        // 업로드할 폴더 생성(년/월)
        String folderPath = makeFolder();
        // 저장할 파일경로 생성
        String saveFileName = uploadDir + folderPath + "/" + uploadFileName;

// 이전 파일 업로드 코드
//        ObjectMetadata objectMetadata = setMetadata(multipartFile);
//
//        try (InputStream inputStream = multipartFile.getInputStream()) {
//
//            // S3에 폴더 및 파일 업로드
//            amazonS3Client.putObject(
//                    new PutObjectRequest(bucketName, uploadFileName, inputStream, objectMetadata)
//                            .withCannedAcl(CannedAccessControlList.PublicRead));
//
//            // S3에 업로드한 폴더 및 파일 URL
//            uploadFileUrl = "https://kr.object.ncloudstorage.com/"+ bucketName + "/" + uploadFileName;
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return FileDto.builder()
                            .originalFileName(originalFileName)
                            .uploadFileName(uploadFileName)
                            .uploadFilePath(filePath)
                            .uploadFileUrl(uploadFileUrl)
                            .build();
    }

    // 원본 및 썸네일 이미지 저장하는 폴더 생성
    private String makeFolder() {
        String str= LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        String folderPath = str.replace("/", File.separator);
        File uploadPathFolder = new File(uploadDir, folderPath);

        // 원본이미지 폴더
        if(!uploadPathFolder.exists()) {
            boolean mkdirs = uploadPathFolder.mkdirs();
            log.info("-------------------makeFolder------------------");
            log.info("uploadPathFolder.exists(): {}", uploadPathFolder.exists());
            log.info("mkdirs: {}", mkdirs);
        }

        return folderPath;
    }

}
