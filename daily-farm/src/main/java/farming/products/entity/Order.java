package farming.products.entity;

import farming.customer.entity.Customer;
import farming.farmer.dto.AddressDto;
import farming.farmer.entity.Farmer;
import farming.products.dto.OrderDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "product_item_id")
    private ProductItem productItem;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;

    private LocalDateTime orderDate;
    private int quantity;
    private double cost;
    private boolean isRemoval;

    public static Order of(OrderDto dto) {
        return Order.builder()
                .orderDate(dto.getOrderDate())
                .quantity(dto.getQuantity())
                .cost(dto.getCost())
                .isRemoval(dto.isRemoval())
                .build();
    }

    public OrderDto toDto() {
        AddressDto farmerAddress = farmer != null && farmer.getAddress() != null
                ? new AddressDto(farmer.getAddress().getCountry(), farmer.getAddress().getCity(), farmer.getAddress().getStreet())
                : null;
        return OrderDto.builder()
                .productItem(productItem != null ? productItem.toDto() : null)
                .customerId(customer != null ? customer.getId() : null)
                .farmerId(farmer != null ? farmer.getId() : null)
                .farmerAddress(farmerAddress)
                .orderDate(orderDate)
                .quantity(quantity)
                .cost(cost)
                .isRemoval(isRemoval)
                .build();
    }
}