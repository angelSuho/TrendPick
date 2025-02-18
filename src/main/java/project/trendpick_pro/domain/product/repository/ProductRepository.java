package project.trendpick_pro.domain.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.trendpick_pro.domain.product.entity.product.Product;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {
    Optional<Product> findByProductCode(String productCode);
    @Modifying
    @Query("UPDATE Product p SET p.productOption.price = :newPrice WHERE p.id = :productId")
    void updatePrice(@Param("productId") Long productId, @Param("newPrice") int newPrice);
    Page<Product> findAll(Pageable pageable);

    @Query("SELECT p FROM Product p JOIN FETCH p.productOption.brand WHERE p.id = :productId")
    Product findByIdWithBrand(@Param("productId") Long productId);
}
