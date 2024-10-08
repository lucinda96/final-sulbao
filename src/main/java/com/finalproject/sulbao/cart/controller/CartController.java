package com.finalproject.sulbao.cart.controller;

import com.finalproject.sulbao.board.common.SessionHandler;
import com.finalproject.sulbao.cart.domain.Carts;
import com.finalproject.sulbao.cart.dto.CartDTO;
import com.finalproject.sulbao.cart.service.CartService;
import com.finalproject.sulbao.login.model.dto.LoginDetails;
import com.finalproject.sulbao.login.model.entity.Login;
import com.finalproject.sulbao.login.model.repository.LoginRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/orders")
@Slf4j
public class CartController {

    private final CartService cartService;
    private final SessionHandler sessionHandler;
    private final LoginRepository loginRepository;

    @Autowired
    public CartController(CartService cartService, SessionHandler sessionHandler, LoginRepository loginRepository) {
        this.cartService = cartService;
        this.sessionHandler = sessionHandler;
        this.loginRepository = loginRepository;
    }

    /***
     * 장바구니 로딩 및 카트 출력 메소드
     * @param model
     * @return
     */
    @GetMapping("/cart")
    public String viewCart(Model model,Authentication authentication, HttpSession session){
        if(!sessionHandler.isLogin(authentication)){
            return "redirect:/login";
        }
        Long userNo = sessionHandler.getUserId(authentication);
        Login login1 = loginRepository.findById(userNo).orElseThrow();
        String userId1 = login1.getUserId();

        List<CartDTO> cartList = cartService.findCartByUserId(userId1);
        model.addAttribute("carts", cartList);
        return "cart/cart";
    }

    /***
     * 장바구니 항목 삭제 메소드
     * @param cartCode
     * @return
     */
    @DeleteMapping("/cart/delete/{cartCode}")
    public ResponseEntity<String> deleteCart(@PathVariable Long cartCode, HttpServletRequest request, Authentication authentication){
        HttpSession session = request.getSession();
        Long userNo = sessionHandler.getUserId(authentication);
        Login login1 = loginRepository.findById(userNo).orElseThrow();
        String userId = login1.getUserId();
        try{
            cartService.deleteByCartCode(cartCode);
            int cartList = cartService.findCartCountByUserId(userId);
            session.setAttribute("cartList", cartList);
            return ResponseEntity.ok("Product deleted successfully");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting product");
        }
    }

    /***
     * 장바구니 상품 수량 변경 메소드
     * @param cartCode
     * @param increment
     * @return
     */
    @PutMapping("/cart/updateQuantity/{cartCode}/{increment}")
    public ResponseEntity<Carts> updateQuantity(@PathVariable Long cartCode, @PathVariable boolean increment){
        try {
            Carts updatedCart = cartService.updateQuantity(cartCode, increment);
            return ResponseEntity.ok(updatedCart);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping("/cart/order_form")
    public String orderForm(){
        return "redirect:/login";
    }

    /***
     * 일반 주문 페이지로 이동 메소드
     * @param cartCodes
     * @return
     */
    @PostMapping("/cart/order_form")
    public String orderForm(@RequestParam("cartCodes") List<Long> cartCodes, Model model, HttpSession session, Authentication authentication) {
        System.out.println("이끄어");
        if(!sessionHandler.isLogin(authentication)){
            return "redirect:/login";
        }
        Long userNo = sessionHandler.getUserId(authentication);
        Login login1 = loginRepository.findById(userNo).orElseThrow();
        String userId1 = login1.getUserId();

        if (cartCodes == null || cartCodes.isEmpty()) {
            throw new IllegalArgumentException("No products selected.");
        }
        session.setAttribute("cartCodes", cartCodes);
        System.out.println(cartCodes);
        List<CartDTO> checkOutList = cartService.findCartByCartCodeIn(cartCodes);
        int totalPurchasePrice = cartService.sumCartByCartCodeIn(cartCodes);
        model.addAttribute("carts", checkOutList);
        model.addAttribute("totalPurchasePrice", totalPurchasePrice);
        return "cart/order";
    }


    @GetMapping("/cart/present_form")
    public String presentForm(){
        return "redirect:/login";
    }

    /***
     * 선물하기 주문 페이지로 이동 메소드
     * @param cartCodes
     * @param model
     * @return
     */
    @PostMapping("/cart/present_form")
    public String presentForm(@RequestParam("cartCodes") List<Long> cartCodes, Model model, HttpSession session, Authentication authentication) {
        Long userNo = sessionHandler.getUserId(authentication);
        Login login1 = loginRepository.findById(userNo).orElseThrow();

        String userId1 = login1.getUserId();
        if(!sessionHandler.isLogin(authentication)){
            return "redirect:/login";
        }

        if (cartCodes == null || cartCodes.isEmpty()) {
            throw new IllegalArgumentException("No products selected.");
        }
        List<CartDTO> checkOutList = cartService.findCartByCartCodeIn(cartCodes);
        int totalPurchasePrice = cartService.sumCartByCartCodeIn(cartCodes);
        model.addAttribute("carts", checkOutList);
        model.addAttribute("totalPurchasePrice", totalPurchasePrice);
        return "cart/presentOrder"; // View 또는 redirect 경로
    }
}