package farming.products.service;

import farming.products.dto.ProductDto;
import farming.products.dto.RemoveProductDataDto;
import farming.products.dto.SaleRecordsDto;
import farming.products.entity.SurpriseBag;
import farming.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface IProductsService {

    ProductDto addProduct(ProductDto productDto, User user);

    boolean updateProduct(ProductDto productDto, User user);

    RemoveProductDataDto removeProduct(Long productId, Long farmerId, User user);

    ProductDto getProduct(Long productId);

    Set<ProductDto> getProductsByFarmer(Long farmerId);

    Set<ProductDto> getProductsByPriceRange(double minPrice, double maxPrice, Long productId);

    List<ProductDto> getAllProducts();

    SaleRecordsDto buyProduct(Long customerId, Long productId, int quantity, User user);

    List<ProductDto> getSoldProducts(Long farmerId, User user);

    List<SaleRecordsDto> getPurchasedProducts(Long customerId);

    List<RemoveProductDataDto> getHistoryOfRemovedProducts(Long farmerId);

    SaleRecordsDto buySurpriseBag(Long customerId, Long surpriseBagId, User user);

    SurpriseBag createSurpriseBag(LocalDateTime startTime, LocalDateTime endTime, int quantity, User user);

    List<SurpriseBag> getAvailableSurpriseBags();


}
