package project.trendpick_pro.domain.product.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.product.entity.dto.ProductRequest;
import project.trendpick_pro.domain.product.entity.product.Product;
import project.trendpick_pro.domain.product.entity.product.dto.response.*;
import project.trendpick_pro.domain.product.entity.productOption.ProductOption;
import project.trendpick_pro.global.rsData.RsData;


import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;


public interface ProductService {

    RsData<Long> register(ProductRequest request, MultipartFile requestMainFile, List<MultipartFile> requestSubFiles) throws IOException, ExecutionException, InterruptedException;

    RsData<Long> modify(Long productId, ProductRequest productRequest, MultipartFile requestMainFile, List<MultipartFile> requestSubFiles) throws IOException;

    void delete(Long productId);

    ProductResponse getProduct(Long productId);

    ProductListResponse getProducts(Long productId);

    void updateFavoriteTag(Product product);

    void deleteCache(ProductOption productOption);

    Page<ProductListResponse> getProducts(int offset, String mainCategory, String subCategory);

    Page<ProductListResponse> findAllByKeyword(String keyword, int offset);

    Page<ProductListResponse> getAllProducts(Pageable pageable);

    List<Product> getRecommendProduct(Member member);

    RsData<Page<ProductListResponseBySeller>> findProductsBySeller(Member member, int offset);

    void applyDiscount(Long productId, double discountRate);

    ProductListResponse convertToProductListResponse(Product product);

    Product findById(Long id);
    Product findByIdWithBrand(Long productId);
}