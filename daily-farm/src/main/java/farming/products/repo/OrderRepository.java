package farming.products.repo;

import farming.products.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByFarmerIdAndIsRemovalFalse(UUID farmerId);
    List<Order> findByCustomerIdAndIsRemovalFalse(UUID customerId);
    List<Order> findByFarmerIdAndIsRemovalTrue(UUID farmerId);
}