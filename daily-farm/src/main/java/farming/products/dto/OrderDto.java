package farming.products.dto;

import farming.farmer.dto.AddressDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class OrderDto {

    private ProductItemDto productItem;
    private UUID customerId;
    private UUID farmerId;
    private AddressDto farmerAddress;
    private LocalDateTime orderDate;
    private int quantity;
    private double cost;
    private boolean isRemoval;
}