package farming.products.service;

import farming.accounting.entity.UserAccount;
import farming.products.dto.OrderDto;
import farming.products.dto.ProductItemDto;
import farming.products.entity.ProductItem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface IProductsService {
    ProductItemDto addProduct(ProductItemDto productItemDto, UserAccount user);
    boolean updateProduct(ProductItemDto productItemDto, UserAccount user);
    ProductItemDto getProduct(UUID productItemId);
    Set<ProductItemDto> getProductsByFarmer(UUID farmerId);
    Set<ProductItemDto> getProductsByPriceRange(double minPrice, double maxPrice, UUID productItemId);
    List<ProductItemDto> getAllProducts();
    OrderDto buyProduct(UUID customerId, UUID productItemId, int quantity, UserAccount user);
    List<ProductItemDto> getSoldProducts(UUID farmerId, UserAccount user);
    List<OrderDto> getPurchasedProducts(UUID customerId);
    List<OrderDto> getHistoryOfRemovedProducts(UUID farmerId);
    OrderDto removeProduct(UUID productItemId, UUID farmerId, UserAccount user);
    OrderDto buySurpriseBag(UUID customerId, UUID productItemId, UserAccount user);
    ProductItem createSurpriseBag(LocalDateTime startTime, LocalDateTime endTime, int quantity, UserAccount user);
    List<ProductItemDto> getAvailableSurpriseBags();
}