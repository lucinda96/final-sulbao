package com.finalproject.sulbao.product.model.dto;

import com.finalproject.sulbao.common.entity.BaseEntity;
import com.finalproject.sulbao.product.model.vo.ProductImage;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ProductComparisonDTO extends BaseEntity {

    private Long comparisonNo;

    private String comparisonName;

    private String comparisonDescription;

    private List<ProductImage> productImages;

}