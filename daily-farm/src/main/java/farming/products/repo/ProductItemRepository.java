package farming.products.repo;

import farming.products.entity.ProductItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductItemRepository extends JpaRepository<ProductItem, UUID> {
    List<ProductItem> findByPriceBetween(double minPrice, double maxPrice);
    List<ProductItem> findByIsSurpriseBagTrueAndDeletedFalse();
}