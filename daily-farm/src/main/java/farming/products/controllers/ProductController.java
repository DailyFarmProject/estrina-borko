package farming.products.controllers;

import farming.accounting.entity.UserAccount;
import farming.api.ProductApi;
import farming.products.dto.OrderDto;
import farming.products.dto.ProductItemDto;
import farming.products.entity.ProductItem;
import farming.products.service.IProductsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping(ProductApi.BASE)
@Slf4j
public class ProductController {

    private final IProductsService productService;

    public ProductController(IProductsService productService) {
        this.productService = productService;
    }

    @PostMapping(ProductApi.ADD)
    public ResponseEntity<ProductItemDto> addProduct(@RequestBody ProductItemDto productItemDto,
                                                     @AuthenticationPrincipal UserAccount user) {
        log.debug("Received addProduct request with product: {} and user: {}",
                productItemDto != null ? productItemDto.getName() : "null", user != null ? user.getLogin() : "null");
        if (user == null) {
            log.error("User is not authenticated");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User must be authenticated");
        }
        ProductItemDto result = productService.addProduct(productItemDto, user);
        return ResponseEntity.ok(result);
    }

    @PutMapping(ProductApi.UPDATE)
    public ResponseEntity<Boolean> updateProduct(@RequestBody ProductItemDto productItemDto,
                                                 @AuthenticationPrincipal UserAccount user) {
        log.info("Updating product item ID: {} by user: {}", productItemDto.getId(),
                user != null ? user.getLogin() : "null");
        boolean result = productService.updateProduct(productItemDto, user);
        return ResponseEntity.ok(result);
    }

    @GetMapping(ProductApi.BY_ID)
    public ResponseEntity<ProductItemDto> getProduct(@PathVariable UUID productItemId) {
        log.info("Fetching product item ID: {}", productItemId);
        ProductItemDto productItem = productService.getProduct(productItemId);
        return ResponseEntity.ok(productItem);
    }

    @GetMapping(ProductApi.BY_FARMER)
    public ResponseEntity<Set<ProductItemDto>> getProductsByFarmer(@PathVariable UUID farmerId) {
        log.info("Fetching product items for farmer ID: {}", farmerId);
        Set<ProductItemDto> productItems = productService.getProductsByFarmer(farmerId);
        return ResponseEntity.ok(productItems);
    }

    @GetMapping(ProductApi.PRICE_RANGE)
    public ResponseEntity<Set<ProductItemDto>> getProductsByPriceRange(@RequestParam double minPrice,
                                                                       @RequestParam double maxPrice,
                                                                       @RequestParam(required = false) UUID productItemId) {
        log.info("Fetching product items in price range {} - {}, product item ID: {}", minPrice, maxPrice, productItemId);
        Set<ProductItemDto> productItems = productService.getProductsByPriceRange(minPrice, maxPrice, productItemId);
        return ResponseEntity.ok(productItems);
    }

    @GetMapping(ProductApi.ALL)
    public ResponseEntity<List<ProductItemDto>> getAllProducts() {
        log.info("Fetching all product items");
        List<ProductItemDto> productItems = productService.getAllProducts();
        return ResponseEntity.ok(productItems);
    }

    @PostMapping(ProductApi.BUY)
    public ResponseEntity<OrderDto> buyProduct(@RequestParam UUID customerId, @RequestParam UUID productItemId,
                                               @RequestParam int quantity, @AuthenticationPrincipal UserAccount user) {
        log.info("Buying product item ID {} for customer ID {}, quantity: {} by user: {}", productItemId, customerId,
                quantity, user != null ? user.getLogin() : "null");
        OrderDto order = productService.buyProduct(customerId, productItemId, quantity, user);
        return ResponseEntity.ok(order);
    }

    @GetMapping(ProductApi.SOLD)
    public ResponseEntity<List<ProductItemDto>> getSoldProducts(@PathVariable UUID farmerId,
                                                                @AuthenticationPrincipal UserAccount user) {
        log.info("Fetching sold product items for farmer ID: {} by user: {}", farmerId,
                user != null ? user.getLogin() : "null");
        List<ProductItemDto> productItems = productService.getSoldProducts(farmerId, user);
        return ResponseEntity.ok(productItems);
    }

    @GetMapping(ProductApi.PURCHASED)
    public ResponseEntity<List<OrderDto>> getPurchasedProducts(@PathVariable UUID customerId) {
        log.info("Fetching purchased product items for customer ID: {}", customerId);
        List<OrderDto> purchases = productService.getPurchasedProducts(customerId);
        return ResponseEntity.ok(purchases);
    }

    @GetMapping(ProductApi.HISTORY)
    public ResponseEntity<List<OrderDto>> getHistoryOfRemovedProducts(@PathVariable UUID farmerId) {
        log.info("Fetching history of removed product items for farmer ID: {}", farmerId);
        List<OrderDto> history = productService.getHistoryOfRemovedProducts(farmerId);
        return ResponseEntity.ok(history);
    }

    @DeleteMapping(ProductApi.REMOVE)
    public ResponseEntity<OrderDto> removeProduct(@RequestParam UUID productItemId, @RequestParam UUID farmerId,
                                                  @AuthenticationPrincipal UserAccount user) {
        log.info("Removing product item ID {} for farmer ID {} by user: {}", productItemId, farmerId,
                user != null ? user.getLogin() : "null");
        OrderDto removed = productService.removeProduct(productItemId, farmerId, user);
        return ResponseEntity.ok(removed);
    }

    @PostMapping(ProductApi.SURPRISE_BAG_BUY)
    public ResponseEntity<OrderDto> buySurpriseBag(@RequestParam UUID customerId, @RequestParam UUID productItemId,
                                                   @AuthenticationPrincipal UserAccount user) {
        log.info("Buying surprise bag ID {} for customer ID {} by user: {}", productItemId, customerId,
                user != null ? user.getLogin() : "null");
        OrderDto order = productService.buySurpriseBag(customerId, productItemId, user);
        return ResponseEntity.ok(order);
    }

    @PostMapping(ProductApi.SURPRISE_BAG_CREATE)
    public ResponseEntity<ProductItemDto> createSurpriseBag(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam int quantity, @AuthenticationPrincipal UserAccount user) {
        log.info("Creating surprise bag by user: {}", user != null ? user.getLogin() : "null");
        ProductItem surpriseBag = productService.createSurpriseBag(startTime, endTime, quantity, user);
        return ResponseEntity.ok(surpriseBag.toDto());
    }

    @GetMapping(ProductApi.SURPRISE_BAG_AVAILABLE)
    public ResponseEntity<List<ProductItemDto>> getAvailableSurpriseBags() {
        log.info("Fetching available surprise bags");
        List<ProductItemDto> bags = productService.getAvailableSurpriseBags();
        return ResponseEntity.ok(bags);
    }
}