package com.finalproject.sulbao.auth.controller;

import com.finalproject.sulbao.product.model.dto.ProductComparisonDTO;
import com.finalproject.sulbao.product.model.dto.ProductDTO;
import com.finalproject.sulbao.product.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AuthController {

    private final ProductService productService;

    public AuthController(ProductService productService) {
        this.productService = productService;
    }


    @GetMapping("/main")
    public String adminMain(Model model) {
        model.addAttribute("menu","home");
        return "admin/index";
    }

    @GetMapping(value = {"/","/index"})
    public String index(Model model) {

        // 최근 등록된 최저가격 비교 상품
        //TODO: 고민후 등록된 상품 많은 순으로 정렬변경할 가능성있음
        List<ProductComparisonDTO> productList = productService.findByProductComparsionOrderByDesc();
        model.addAttribute("productList", productList);

        return "index";
    }

}
