package com.finalproject.sulbao.common.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class FileUploadController {

    @GetMapping("/uploadFile")
    public String getUploadFile() {
        return "uploadFile";
    }
}
