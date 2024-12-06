package com.finalproject.sulbao.partner.service;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class PartnerService {

    @Value("${file.upload-dir}")
    private String uploadPath;


    // 파일 업로드
    // 추가확인 - 파일 용량확인, 에러발생시 에러메세지 어떻게 표시할지?
    // 오류 발생시 메세지가 화면에 표시되도록 조치 필요(중단 되면 안됨) -> 전역 으로 예외처리 구현 필요(예외처리가 아니여도 오류관련 메세지 출력에대한 내용 작성 필요)
    public void uploadFiles(List<MultipartFile> fileList){
        for (MultipartFile file : fileList) {
            try {
                //파일 없을 경우
                if (file.isEmpty()) {
                    continue;
                }

                // 파일 가로세로 px 확인
                // 제시한 파일의 기준 크기에 맞춰서 올렸는지 확인 필요(이부분은 신설사업팀에게 안 맞출경우 늘어난다고 이야기하면 굳이 확인할 필요 없음)
//                BufferedImage image = ImageIO.read(file.getInputStream());
//                if(image !=null){
//                    log.info("파일의 가로 길이 : {}", image.getWidth());
//                    log.info("파일의 세로 길이 : {}", image.getHeight());
//                }


                // 원본파일명 확인
                String originalFilename = file.getOriginalFilename();
                if (originalFilename == null) {
                    log.info("파일명이 없습니다.");
                    continue;
                }

                // uuid파일명 생성
                String uuidFileName = getUuidFileName(originalFilename);

                // 날짜 폴더 생성
                String folderPath = makeFolder();
                String saveFileName = uploadPath + "/" + folderPath + "/" + uuidFileName;

                Path savePath = Paths.get(saveFileName);
                log.info("savePath: {}", savePath);
                // 원본파일 저장
                file.transferTo(savePath);

                // 썸네일 저장위치
                String thumbnailSaveName = uploadPath + File.separator + folderPath + File.separator + "thumbnail" + File.separator + "s_" + uuidFileName;
                log.info("thumbnailSaveName: {}", thumbnailSaveName);
                // 썸네일 생성
                File thumbnailFile = new File(thumbnailSaveName);
                Thumbnailator.createThumbnail(savePath.toFile(),thumbnailFile,100,100);


            }catch (IOException e){
                log.error("파일 업로드 실패", e);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 파일명 중복 방지를 위한 UUID 적용
    private String getUuidFileName(String fileName) {
        String ext = fileName.substring(fileName.indexOf(".") + 1);
        return UUID.randomUUID().toString() + "." + ext;
    }

    // 원본 및 썸네일 이미지 저장하는 폴더 생성
    private String makeFolder() {
        String str= LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        String folderPath = str.replace("/", File.separator);
        String thumbnailFolderPath = folderPath + File.separator + "thumbnail";
        File uploadPathFolder = new File(uploadPath, folderPath);
        File thumbnailPathFolder = new File(uploadPath, thumbnailFolderPath);

        // 원본이미지 폴더
        if(!uploadPathFolder.exists()) {
            boolean mkdirs = uploadPathFolder.mkdirs();
            log.info("-------------------makeFolder------------------");
            log.info("uploadPathFolder.exists(): {}", uploadPathFolder.exists());
            log.info("mkdirs: {}", mkdirs);
        }

        // 썸네일이미지 폴더
        if(!thumbnailPathFolder.exists()) {
            boolean mkdirs = thumbnailPathFolder.mkdirs();
            log.info("-------------------thumbnailPathFolder------------------");
            log.info("thumbnailPathFolder.exists(): {}", thumbnailPathFolder.exists());
            log.info("thumbnailPathFolderMkdirs: {}", mkdirs);
        }

        return folderPath;
    }

}

