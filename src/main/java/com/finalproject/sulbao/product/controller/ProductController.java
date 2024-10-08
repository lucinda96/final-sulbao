package com.finalproject.sulbao.product.controller;

import com.finalproject.sulbao.board.common.SessionHandler;
import com.finalproject.sulbao.login.model.dto.LoginDetails;
import com.finalproject.sulbao.login.model.entity.RoleType;
import com.finalproject.sulbao.product.model.dto.ProductComparisonDTO;
import com.finalproject.sulbao.product.model.dto.ProductDTO;
import com.finalproject.sulbao.product.model.entity.ProductCategory;
import com.finalproject.sulbao.product.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;
    private final SessionHandler sessionHandler;

    public ProductController(ProductService productService, SessionHandler sessionHandler) {
        this.productService = productService;
        this.sessionHandler = sessionHandler;
    }

    //목록화면
    @GetMapping("/list")
    public String productList(Model model, Authentication authentication) {

        if(!sessionHandler.isLogin(authentication)){
            return "redirect:/login";
        }
        LoginDetails login = (LoginDetails) authentication.getPrincipal();
        String role = login.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        if("MEMBER".equals(role)){
            return "redirect:/";
        }

        ProductDTO productDTO = new ProductDTO();
        productDTO.setUserNo(login.getUserNo());
        List<ProductDTO> productList = productService.findByUserNo(productDTO);

        model.addAttribute("menu","product");
        model.addAttribute("submenu","plist");
        model.addAttribute("product", productDTO);
        model.addAttribute("productList", productList);
        return "admin/product/productList";
    }

    // 관리자 페이지 조회
    @GetMapping("/search")
    public String productSearch(Model model, Authentication authentication, @ModelAttribute ProductDTO productDTO, HttpSession session) {

        if(!sessionHandler.isLogin(authentication)){
            return "redirect:/login";
        }
        LoginDetails login = (LoginDetails) authentication.getPrincipal();
        String role = login.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        if("MEMBER".equals(role)){
            return "redirect:/";
        }

        productDTO.setUserNo(login.getUserNo());
        List<ProductDTO> productList = productService.findBySearchInfo(productDTO);
        model.addAttribute("menu","product");
        model.addAttribute("submenu","list");
        model.addAttribute("product", productDTO);
        model.addAttribute("productList", productList);
        return "admin/product/productList";
    }

    //등록화면으로 이동
    @GetMapping("/detail")
    public String productRegist(Model model, Authentication authentication, HttpSession session) {

        if(!sessionHandler.isLogin(authentication)){
            return "redirect:/login";
        }
        LoginDetails login = (LoginDetails) authentication.getPrincipal();
        String role = login.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        if("MEMBER".equals(role)){
            return "redirect:/";
        }

        List<ProductComparisonDTO> productComparisonList = productService.findByComparison();
        model.addAttribute("menu","product");
        model.addAttribute("submenu","regist");
        model.addAttribute("productComparisonList", productComparisonList);
        model.addAttribute("product", new ProductDTO());
        model.addAttribute("productCategory", ProductCategory.values());
        return "admin/product/productDetail";
    }

    //수정화면으로 이동
    @GetMapping("/update/{productNo}")
    public String productUpdate(Model model,Authentication authentication,@PathVariable(required = false,name = "productNo") Long productNo, HttpSession session) {

        if(!sessionHandler.isLogin(authentication)){
            return "redirect:/login";
        }
        LoginDetails login = (LoginDetails) authentication.getPrincipal();
        String role = login.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        if("MEMBER".equals(role)){
            return "redirect:/";
        }
        List<ProductComparisonDTO> productComparisonList = productService.findByComparison();
        model.addAttribute("productComparisonList", productComparisonList);

        ProductDTO productDTO =  productService.findByProductNo(productNo);
        model.addAttribute("product", productDTO);
        model.addAttribute("productCategory", ProductCategory.values());
        return "admin/product/productUpdate";
    }

    //상품등록
    @PostMapping("/regist")
    public String saveProduct(@ModelAttribute ProductDTO productDTO, Authentication authentication,HttpSession session) {

        log.info("productController saveProduct : {}", productDTO);
        if(!sessionHandler.isLogin(authentication)){
            return "redirect:/login";
        }
        LoginDetails login = (LoginDetails) authentication.getPrincipal();
        String role = login.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        if("MEMBER".equals(role)){
            return "redirect:/";
        }
        productDTO.setUserNo((Long) session.getAttribute("userNo"));
        // 저장
        productService.saveProduct(productDTO);
        return "redirect:/product/list";
    }

    //상품수정
    @PostMapping("/update")
    public String updateProduct(@ModelAttribute ProductDTO productDTO, Authentication authentication, HttpSession session) {

        if(!sessionHandler.isLogin(authentication)){
            return "redirect:/login";
        }
        LoginDetails login = (LoginDetails) authentication.getPrincipal();
        String role = login.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        if("MEMBER".equals(role)){
            return "redirect:/";
        }
        productDTO.setUserNo(login.getUserNo());
        productService.updateProduct(productDTO);

        return "redirect:/product/list";
    }

    //상품삭제
    @DeleteMapping("/delete")
    @ResponseBody
    public String deleteProduct(String productNoList, Authentication authentication) {
        if(!sessionHandler.isLogin(authentication)){
            return "redirect:/login";
        }
        LoginDetails login = (LoginDetails) authentication.getPrincipal();
        String role = login.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        if("MEMBER".equals(role)){
            return "redirect:/";
        }
        productService.delete(productNoList);
        return "success";
    }

    //상품상태변경
    @PutMapping("/status")
    @ResponseBody
    public String updateStatus(String productNoList, Authentication authentication, String type, String status, HttpSession session) {

        if(!sessionHandler.isLogin(authentication)){
            return "redirect:/login";
        }
        LoginDetails login = (LoginDetails) authentication.getPrincipal();
        String role = login.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        if("MEMBER".equals(role)){
            return "redirect:/";
        }

        if(productNoList == null || productNoList.isEmpty()){
            return "fail";
        }
        productService.updateStatus(productNoList,type,status);
        return "success";
    }

}
