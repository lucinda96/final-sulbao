package com.finalproject.sulbao.product.model.entity;

import com.finalproject.sulbao.common.entity.BaseEntity;
import com.finalproject.sulbao.login.model.entity.Login;
import com.finalproject.sulbao.product.model.dto.ProductDTO;
import com.finalproject.sulbao.product.model.vo.ProductImage;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name="tbl_product")
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_no")
    private Long productNo;

    @Column(name="product_name")
    private String productName;

    @Column(name="product_category")
    @Enumerated(EnumType.STRING)
    private ProductCategory productCategory;

    @Column(name="product_description", columnDefinition = "LONGTEXT")
    private String productDescription;

    @Column(name="product_summary", columnDefinition = "LONGTEXT")
    private String productSummary;

    @Column(name="product_price")
    private int productPrice;

    @Column(name="product_stock")
    private int productStock;

    @Column(name="product_hashtag", length = 500)
    private String productHashTag;

    @Column(name="sell_yn", length = 1, columnDefinition = "CHAR(1) DEFAULT 'n'")
    private String sellYn;

    @Column(name="display_yn", length = 1, columnDefinition = "CHAR(1) DEFAULT 'n'")
    private String displayYn;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name="tbl_product_image", joinColumns = @JoinColumn(name="product_no")
    )
    @OrderColumn(name="idx")
    private List<ProductImage> productImages;

    @ManyToOne
    @JoinColumn(name="comparison_no")
    private ProductComparison comparison;

    //판매자정보
    @ManyToOne
    @JoinColumn(name = "user_no")
    private Login sellerInfo;

    public void update(ProductDTO productDTO){

        this.productPrice = productDTO.getProductPrice();
        this.productStock = productDTO.getProductStock();
        this.displayYn = productDTO.getDisplayYn();
        this.productDescription = productDTO.getProductDescription();
        this.productHashTag = productDTO.getProductHashTag();
        this.productName = productDTO.getProductName();
        this.productSummary = productDTO.getProductSummary();
        this.sellYn = productDTO.getSellYn();
        this.productCategory = productDTO.getProductCategory();
        if(productDTO.getProductImages() != null){
            this.productImages = productDTO.getProductImages();
        }
    }

    public void updateDisplay(ProductDTO productDTO){
        this.displayYn = productDTO.getDisplayYn();
    }

    public void updateSellYn(ProductDTO productDTO){
        this.sellYn = productDTO.getSellYn();
    }

}
