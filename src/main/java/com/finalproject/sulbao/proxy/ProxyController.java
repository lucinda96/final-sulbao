package com.finalproject.sulbao.proxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/proxy")
public class ProxyController {

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/law")
    public ResponseEntity<String> proxyLaw() {

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl("https://law.go.kr/DRF/lawSearch.do")
                .queryParam("OC","kdexp")
                .queryParam("target","prec")
                .queryParam("type","json");


        String result = restTemplate.getForObject(builder.toUriString(), String.class);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(result);
    }

}
