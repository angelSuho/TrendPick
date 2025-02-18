package project.trendpick_pro.domain.product.service;

import com.amazonaws.services.s3.AmazonS3Client;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import project.trendpick_pro.domain.brand.entity.Brand;
import project.trendpick_pro.domain.brand.service.BrandService;
import project.trendpick_pro.domain.category.entity.MainCategory;
import project.trendpick_pro.domain.category.entity.SubCategory;
import project.trendpick_pro.domain.category.service.MainCategoryService;
import project.trendpick_pro.domain.category.service.SubCategoryService;
import project.trendpick_pro.domain.common.file.CommonFile;
import project.trendpick_pro.domain.common.file.FileTranslator;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.MemberRole;
import project.trendpick_pro.domain.member.service.MemberService;
import project.trendpick_pro.domain.product.entity.dto.ProductRequest;
import project.trendpick_pro.domain.product.entity.product.Product;
import project.trendpick_pro.domain.product.entity.product.ProductStatus;
import project.trendpick_pro.domain.product.entity.product.dto.request.ProductSaveRequest;
import project.trendpick_pro.domain.product.entity.product.dto.request.ProductSearchCond;
import project.trendpick_pro.domain.product.entity.product.dto.response.ProductByRecommended;
import project.trendpick_pro.domain.product.entity.product.dto.response.ProductListResponse;
import project.trendpick_pro.domain.product.entity.product.dto.response.ProductResponse;
import project.trendpick_pro.domain.product.entity.productOption.ProductOption;
import project.trendpick_pro.domain.product.entity.productOption.dto.ProductOptionSaveRequest;
import project.trendpick_pro.domain.product.repository.ProductRepository;
import project.trendpick_pro.domain.tags.favoritetag.entity.FavoriteTag;
import project.trendpick_pro.domain.tags.favoritetag.service.FavoriteTagService;
import project.trendpick_pro.domain.tags.tag.entity.Tag;
import project.trendpick_pro.domain.tags.tag.entity.TagType;
import project.trendpick_pro.domain.tags.tag.service.TagService;
import project.trendpick_pro.global.config.AmazonProperties;
import project.trendpick_pro.global.exception.BaseException;
import project.trendpick_pro.global.exception.ErrorCode;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    private final MemberService memberService;
    private final MainCategoryService mainCategoryService;
    private final SubCategoryService subCategoryService;
    private final BrandService brandService;
    private final FavoriteTagService favoriteTagService;
    private final TagService tagService;

    private final FileTranslator fileTranslator;
    private final AmazonS3Client amazonS3;
    private final AmazonProperties amazonProperties;

    @Transactional
    public Long saveProduct(ProductRequest request, MultipartFile requestMainFile, List<MultipartFile> requestSubFiles) {
        ProductSaveRequest productSaveRequest = request.getSaveRequest();
        ProductOptionSaveRequest optionSaveRequest = request.getOptionSaveRequest();

        CommonFile mainFile = fileTranslator.saveFile(requestMainFile);
        List<CommonFile> subFiles = fileTranslator.saveFiles(requestSubFiles);
        subFiles.forEach(mainFile::connectFile);

        Set<Tag> tags = new LinkedHashSet<>();
        productSaveRequest.getTags().forEach(tagName -> tags.add(new Tag(tagName)));

        MainCategory mainCategory = mainCategoryService.findByName(productSaveRequest.getMainCategory());
        SubCategory subCategory = subCategoryService.findByName(productSaveRequest.getSubCategory());
        Brand brand = brandService.findByName(productSaveRequest.getBrand());

        ProductOption productOption = ProductOption.of(optionSaveRequest);
        productOption.settingConnection(brand, mainCategory, subCategory, mainFile, ProductStatus.SALE);
        Product product = Product.of(productSaveRequest.getName(), productSaveRequest.getDescription());
        product.updateTags(tags);

        Product saveProduct = productRepository.save(product);
        return saveProduct.getId();
    }

    @Transactional
    public Long modifyProduct(Long productId, ProductRequest productRequest, MultipartFile requestMainFile, List<MultipartFile> requestSubFiles) throws IOException {
        ProductSaveRequest productSaveRequest = productRequest.getSaveRequest();
        ProductOptionSaveRequest optionSaveRequest = productRequest.getOptionSaveRequest();

        Product product = productRepository.findById(productId).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND, "존재하지 않는 상품입니다."));
        CommonFile mainFile = fileTranslator.saveFile(requestMainFile);
        List<CommonFile> subFiles = fileTranslator.saveFiles(requestSubFiles);
        subFiles.forEach(mainFile::connectFile);

        product.getProductOption().getFile().deleteFile(amazonS3, amazonProperties.getS3().getBucket());
        product.getProductOption().updateFile(mainFile);

        Set<Tag> tags = new LinkedHashSet<>();
        productSaveRequest.getTags().forEach(tagName -> tags.add(new Tag(tagName)));

        tagService.delete(product.getTags());
        product.updateTags(tags);
        product.update(productSaveRequest, optionSaveRequest);
        return product.getId();
    }

    @Transactional
    public void delete(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND, "존재하지 않는 상품입니다."));
        product.getProductOption().getFile().deleteFile(amazonS3, amazonProperties.getS3().getBucket());
        productRepository.delete(product);
    }

    public ProductResponse getProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND, "존재하지 않는 상품입니다."));
        return ProductResponse.of(product, amazonProperties);
    }

    public Page<ProductListResponse> getProducts(int offset, String mainCategory, String subCategory) {
        ProductSearchCond cond = new ProductSearchCond(mainCategory, subCategory);
        PageRequest pageable = PageRequest.of(offset, 18);
        return productRepository.findAllByCategoryId(cond, pageable);
    }

    public Page<ProductListResponse> findAllByKeyword(String query, int offset) {
        ProductSearchCond cond = new ProductSearchCond(query);
        PageRequest pageable = PageRequest.of(offset, 18);
        Page<ProductListResponse> products = productRepository.findAllByKeyword(cond, pageable);
        return products;
    }

    public List<Product> getRecommendProduct(Member member) {
        List<ProductByRecommended> tags = productRepository.findRecommendProduct(member.getEmail());

        Map<String, List<Long>> productIdListByTagName = new HashMap<>();
        for (ProductByRecommended tag : tags) {
            if (!productIdListByTagName.containsKey(tag.getTagName()))
                productIdListByTagName.put(tag.getTagName(), new ArrayList<>());
            productIdListByTagName.get(tag.getTagName()).add(tag.getProductId());
        }

        Map<Long, ProductByRecommended> recommendProductByProductId = new HashMap<>();
        for (ProductByRecommended response : tags) {
            if (recommendProductByProductId.containsKey(response.getProductId()))
                continue;
            recommendProductByProductId.put(response.getProductId(), response);
        }

        for (FavoriteTag memberTag : member.getTags()) {
            if (productIdListByTagName.containsKey(memberTag.getName())) {
                List<Long> productIdList = productIdListByTagName.get(memberTag.getName());
                for (Long id : productIdList) {
                    recommendProductByProductId.get(id).plusTotalScore(memberTag.getScore());
                }
            }
        }

        List<ProductByRecommended> recommendProductList = new ArrayList<>(recommendProductByProductId.values()).stream()
                .sorted(Comparator.comparing(ProductByRecommended::getTotalScore).reversed())
                .toList();

        List<Product> products = new ArrayList<>();
        for (ProductByRecommended recommendProduct : recommendProductList) {
            products.add(productRepository.findById(recommendProduct.getProductId()).orElseThrow(
                    () -> new BaseException(ErrorCode.NOT_FOUND, "존재하지 않는 상품입니다.")
            ));
        }
        return products;
    }

    @Transactional
    public void applyDiscount(Long productId, double discountRate) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND, "존재하지 않는 상품입니다."));
        product.applyDiscount(discountRate);
    }

    public Product findById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND, "존재하지 않는 상품입니다."));
    }

    public Product findByIdWithBrand(Long productId) {
        return productRepository.findByIdWithBrand(productId);
    }
}
