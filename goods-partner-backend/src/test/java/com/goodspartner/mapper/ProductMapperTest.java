package com.goodspartner.mapper;

import com.goodspartner.dto.ProductDto;
import com.goodspartner.entity.OrderedProduct;
import com.goodspartner.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductMapperTest {

    private final ProductMapper productMapper = new ProductMapperImpl();

    private OrderedProduct orderedProduct;

    @BeforeEach
    public void setUp() {
        Product product = new Product();
        product.setName("Product");
        product.setKg(111);

        orderedProduct = new OrderedProduct();
        orderedProduct.setProduct(product);
        orderedProduct.setCount(11);
    }

    @Test
    @DisplayName("Test map OrderedProduct to ProductDto")
    public void testMapProduct() {
        ProductDto productDto = productMapper.mapProduct(orderedProduct);

        assertEquals("Product", productDto.getProductName());
        assertEquals(111, productDto.getUnitWeight());
        assertEquals(11, productDto.getAmount());
    }
}