package com.esiran.greenpay.pay.service;

import com.esiran.greenpay.pay.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;
import com.esiran.greenpay.pay.entity.ProductDTO;

import java.util.List;

/**
 * <p>
 * 支付产品 服务类
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
public interface IProductService extends IService<Product> {
    List<ProductDTO> findAllProduct(ProductDTO productDTO);
    List<ProductDTO> findAllProductByPayTypeCode(String payTypeCode);
}
