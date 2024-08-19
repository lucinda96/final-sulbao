package com.finalproject.sulbao.cart.service;

import com.finalproject.sulbao.cart.domain.Carts;
import com.finalproject.sulbao.cart.dto.CartDTO;
import com.finalproject.sulbao.cart.dto.OrderDTO;
import com.finalproject.sulbao.cart.repository.CartRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class CartService {
    private final CartRepository cartRepository;
    private final ModelMapper modelMapper;

    public CartService(CartRepository cartRepository,
                       ModelMapper modelMapper) {
        this.cartRepository = cartRepository;
        this.modelMapper = modelMapper;
    }

    public List<CartDTO> findCartByUserId(String userId) {
        List<Carts> carts = cartRepository.findByUserIdAndIsOrder(userId,false, Sort.by("cartCode"));
        return carts.stream()
                .map(cart -> modelMapper.map(cart, CartDTO.class))
                .collect(Collectors.toList());

    }

    public void deleteByCartCode(Long cartCode){
        cartRepository.deleteByCartCode(cartCode);
    }

    public Carts updateQuantity(Long cartCode, boolean quantity){
        Optional<Carts> optionalCart = cartRepository.findByCartCode(cartCode);
        if(optionalCart.isPresent()){
            Carts carts = optionalCart.get();
            if(quantity){
                carts.increaseAmount();
            } else {
                carts.decreaseAmount();
            }
            return cartRepository.save(carts);
        }else{
            throw new RuntimeException("Cart not found");
        }

    }

    public List<CartDTO> findCartByCartCodeIn(List<Long> cartCodes) {
        List<Carts> orders = cartRepository.findByCartCodeIn(cartCodes, Sort.by("cartCode"));
        return orders.stream()
                        .map(cart -> modelMapper.map(cart, CartDTO.class))
                        .collect(Collectors.toList());
    }

    public int sumCartByCartCodeIn(List<Long> cartCodes) {
        int sumPurchasePrice = cartRepository.findTotalPurchasePriceByCartCodeIn(cartCodes);
        return sumPurchasePrice;
    }

    public CartDTO findCartByCartCode(Long aLong) {
        Carts orders = cartRepository.findByCartCodes(aLong);
        return orders == null ? null : modelMapper.map(orders, CartDTO.class);
    }

    public void updateIsOrder(Long cartCode) {
        Optional<Carts> optionalCart = cartRepository.findByCartCode(cartCode);
        if (optionalCart.isPresent()) {
            Carts carts = optionalCart.get();
            carts.markAsOrdered();  // isOrder 필드를 true로 설정
            cartRepository.save(carts);
        } else {
            throw new RuntimeException("Cart not found");
        }
    }
}
