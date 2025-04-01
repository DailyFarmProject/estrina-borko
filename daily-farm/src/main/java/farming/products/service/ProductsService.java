package farming.products.service;

import farming.accounting.dto.UserType;
import farming.accounting.entity.UserAccount;
import farming.customer.entity.Customer;
import farming.customer.repo.CustomerRepository;
import farming.farmer.entity.Farmer;
import farming.farmer.repo.FarmerRepository;
import farming.products.dto.OrderDto;
import farming.products.dto.ProductItemDto;
import farming.products.entity.Order;
import farming.products.entity.ProductItem;
import farming.products.repo.OrderRepository;
import farming.products.repo.ProductItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductsService implements IProductsService {

    private final ProductItemRepository productItemRepo;
    private final FarmerRepository farmerRepo;
    private final OrderRepository orderRepo;
    private final CustomerRepository customerRepo;

    private void checkFarmerRole(UserAccount user) {
        if (user == null || !user.getUserType().equals(UserType.FARMER)) {
            log.warn("User {} is not a farmer, access denied", user != null ? user.getLogin() : "null");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only farmers can perform this operation");
        }
    }

    private void checkCustomerRole(UserAccount user) {
        if (user == null || !user.getUserType().equals(UserType.CUSTOMER)) {
            log.warn("User {} is not a customer, access denied", user != null ? user.getLogin() : "null");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only customers can perform this operation");
        }
    }

    @Override
    @Transactional
    public ProductItemDto addProduct(ProductItemDto productItemDto, UserAccount user) {
        log.info("Adding product item: {} by user: {}", productItemDto.getName(), user.getLogin());
        checkFarmerRole(user);
        Farmer farmer = farmerRepo.findByLogin(user.getLogin())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farmer profile not found"));
        ProductItem productItem = ProductItem.of(productItemDto);
        farmer.addProductItem(productItem);
        productItemRepo.save(productItem);
        return productItem.toDto();
    }

    @Override
    @Transactional
    public boolean updateProduct(ProductItemDto productItemDto, UserAccount user) {
        log.info("Updating product item with ID: {} by user: {}", productItemDto.getId(), user.getLogin());
        checkFarmerRole(user);
        ProductItem productItem = productItemRepo.findById(productItemDto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product item not found"));
        Farmer farmer = farmerRepo.findByLogin(user.getLogin())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farmer profile not found"));
        if (!productItem.getFarmer().getId().equals(farmer.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only update your own products");
        }
        if (productItemDto.getName() != null) productItem.setName(productItemDto.getName());
        if (productItemDto.getQuantity() >= 0) productItem.setQuantity(productItemDto.getQuantity());
        if (productItemDto.getPrice() != null) productItem.setPrice(productItemDto.getPrice());
        if (productItemDto.getImgUrl() != null) productItem.setImgUrl(productItemDto.getImgUrl());
        if (productItem.isSurpriseBag()) {
            productItem.setStartTime(productItemDto.getStartTime());
            productItem.setEndTime(productItemDto.getEndTime());
        }
        productItemRepo.save(productItem);
        return true;
    }

    @Override
    @Transactional
    public OrderDto removeProduct(UUID productItemId, UUID farmerId, UserAccount user) {
        log.info("Removing product item ID {} for farmer ID {} by user: {}", productItemId, farmerId, user.getLogin());
        checkFarmerRole(user);
        ProductItem productItem = productItemRepo.findById(productItemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product item not found"));
        Farmer farmer = farmerRepo.findById(farmerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farmer not found"));
        if (!farmer.getLogin().equals(user.getLogin())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only remove products you own");
        }
        if (!productItem.getFarmer().getId().equals(farmerId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Farmer doesn't own this product item");
        }
        productItem.setDeleted(true);
        productItemRepo.save(productItem);
        Order order = Order.builder()
                .productItem(productItem)
                .farmer(farmer)
                .orderDate(LocalDateTime.now())
                .quantity(productItem.getQuantity())
                .cost(0.0)
                .isRemoval(true)
                .build();
        orderRepo.save(order);
        farmer.getProductItems().remove(productItem);
        farmerRepo.save(farmer);
        return order.toDto();
    }

    @Override
    public ProductItemDto getProduct(UUID productItemId) {
        log.info("Fetching product item with ID: {}", productItemId);
        return productItemRepo.findById(productItemId)
                .map(ProductItem::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product item not found"));
    }

    @Override
    public Set<ProductItemDto> getProductsByFarmer(UUID farmerId) {
        log.info("Fetching product items for farmer ID: {}", farmerId);
        Farmer farmer = farmerRepo.findById(farmerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farmer not found"));
        return farmer.getProductItems().stream()
                .map(ProductItem::toDto)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<ProductItemDto> getProductsByPriceRange(double minPrice, double maxPrice, UUID productItemId) {
        log.info("Fetching product items with price range {} - {} and ID: {}", minPrice, maxPrice, productItemId);
        List<ProductItem> productItems = productItemRepo.findByPriceBetween(minPrice, maxPrice);
        return productItems.stream()
                .filter(p -> productItemId == null || p.getId().equals(productItemId))
                .map(ProductItem::toDto)
                .collect(Collectors.toSet());
    }

    @Override
    public List<ProductItemDto> getAllProducts() {
        log.info("Fetching all product items");
        return productItemRepo.findAll().stream()
                .map(ProductItem::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderDto buyProduct(UUID customerId, UUID productItemId, int quantity, UserAccount user) {
        log.info("Customer ID {} buying {} units of product item ID {} by user: {}", customerId, quantity, productItemId, user.getLogin());
        checkCustomerRole(user);
        ProductItem productItem = productItemRepo.findById(productItemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product item not found"));
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        if (!customer.getLogin().equals(user.getLogin())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only buy as yourself");
        }
        if (!productItem.isAvailable() || productItem.getQuantity() < quantity) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Not enough stock available");
        }
        double cost = productItem.getPrice() * quantity;
        Farmer farmer = productItem.getFarmer();
        productItem.setQuantity(productItem.getQuantity() - quantity);
        productItemRepo.save(productItem);
        Order order = Order.builder()
                .productItem(productItem)
                .customer(customer)
                .farmer(farmer)
                .orderDate(LocalDateTime.now())
                .quantity(quantity)
                .cost(cost)
                .isRemoval(false)
                .build();
        orderRepo.save(order);
        return order.toDto();
    }

    @Override
    public List<ProductItemDto> getSoldProducts(UUID farmerId, UserAccount user) {
        log.info("Fetching sold product items for farmer ID: {} by user: {}", farmerId, user.getLogin());
        checkFarmerRole(user);
        Farmer farmer = farmerRepo.findById(farmerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farmer not found"));
        if (!farmer.getLogin().equals(user.getLogin())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view your own sold products");
        }
        List<Order> orders = orderRepo.findByFarmerIdAndIsRemovalFalse(farmerId);
        return orders.stream()
                .map(Order::getProductItem)
                .distinct()
                .map(ProductItem::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDto> getPurchasedProducts(UUID customerId) {
        log.info("Fetching purchased product items for customer ID: {}", customerId);
        customerRepo.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        return orderRepo.findByCustomerIdAndIsRemovalFalse(customerId).stream()
                .map(Order::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDto> getHistoryOfRemovedProducts(UUID farmerId) {
        log.info("Fetching history of removed product items for farmer ID: {}", farmerId);
        farmerRepo.findById(farmerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farmer not found"));
        return orderRepo.findByFarmerIdAndIsRemovalTrue(farmerId).stream()
                .map(Order::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderDto buySurpriseBag(UUID customerId, UUID productItemId, UserAccount user) {
        log.info("Customer ID {} buying surprise bag ID {} by user: {}", customerId, productItemId, user.getLogin());
        checkCustomerRole(user);
        ProductItem productItem = productItemRepo.findById(productItemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product item not found"));
        if (!productItem.isSurpriseBag()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This is not a surprise bag");
        }
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        if (!customer.getLogin().equals(user.getLogin())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only buy as yourself");
        }
        if (!productItem.isAvailable()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Surprise bag is not available");
        }
        if (productItem.getQuantity() < 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No surprise bags available");
        }
        double cost = productItem.getPrice();
        Farmer farmer = productItem.getFarmer();
        productItem.setQuantity(productItem.getQuantity() - 1);
        productItemRepo.save(productItem);
        Order order = Order.builder()
                .productItem(productItem)
                .customer(customer)
                .farmer(farmer)
                .orderDate(LocalDateTime.now())
                .quantity(1)
                .cost(cost)
                .isRemoval(false)
                .build();
        orderRepo.save(order);
        return order.toDto();
    }

    @Override
    @Transactional
    public ProductItem createSurpriseBag(LocalDateTime startTime, LocalDateTime endTime, int quantity, UserAccount user) {
        log.info("Creating surprise bag by user: {}", user.getLogin());
        checkFarmerRole(user);
        Farmer farmer = farmerRepo.findByLogin(user.getLogin())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farmer profile not found"));
        ProductItem surpriseBag = ProductItem.builder()
                .name("Surprise Bag")
                .quantity(quantity)
                .price(5.0)
                .startTime(startTime)
                .endTime(endTime)
                .imgUrl("http://example.com/surprise_bag.jpg")
                .isSurpriseBag(true)
                .farmer(farmer)
                .build();
        farmer.addProductItem(surpriseBag);
        productItemRepo.save(surpriseBag);
        return surpriseBag;
    }

    @Override
    public List<ProductItemDto> getAvailableSurpriseBags() {
        log.info("Fetching available surprise bags");
        return productItemRepo.findByIsSurpriseBagTrueAndDeletedFalse()
                .stream()
                .filter(ProductItem::isAvailable)
                .map(ProductItem::toDto)
                .collect(Collectors.toList());
    }

    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void cleanUpExpiredSurpriseBags() {
        log.info("Cleaning up expired surprise bags");
        LocalDateTime now = LocalDateTime.now();
        List<ProductItem> expiredBags = productItemRepo.findByIsSurpriseBagTrueAndDeletedFalse()
                .stream()
                .filter(bag -> bag.getEndTime().isBefore(now))
                .collect(Collectors.toList());
        if (!expiredBags.isEmpty()) {
            productItemRepo.deleteAll(expiredBags);
            log.info("Deleted {} expired surprise bags", expiredBags.size());
        } else {
            log.debug("No expired surprise bags found");
        }
    }
}