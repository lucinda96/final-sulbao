package com.finalproject.sulbao.partner.controller;


import com.finalproject.sulbao.partner.dto.PartnerDTO;
import com.finalproject.sulbao.partner.service.PartnerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
public class PartnerController {

    private final PartnerService partnerService;
    // 생성자 주입
    public PartnerController(PartnerService partnerService) {
        this.partnerService = partnerService;
    }


    @GetMapping("/mainpartner")
    public String main() {
        return "main";
    }

    @PostMapping("/mainpartner")
    public String partner(@ModelAttribute PartnerDTO partnerDTO, @RequestParam(required = false) List<MultipartFile> fileList, Model model) {

        log.info("request Info = {}", partnerDTO);
        log.info("fileListSize = {}", fileList == null? 0 :fileList.size());

        //파일 업로드
        if(fileList != null) {
            try {
                // 파일 업로드(원본파일 저장후 썸네일 생성)
                partnerService.uploadFiles(fileList);
            } catch (Exception e) {
                model.addAttribute("errorMessage", "파일 크기가 너무 큽니다. 50MB 이하로 줄여주세요.");
            }

        }



        return "redirect:/main";
    }

}
