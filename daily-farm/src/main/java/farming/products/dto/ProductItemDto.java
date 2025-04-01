package farming.products.dto;

import farming.farmer.dto.FarmerDto;
import farming.products.entity.ProductItem;
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
public class ProductItemDto {

    private UUID id;
    private String name;
    private int quantity;
    private Double price;
    private String imgUrl;
    private boolean isSurpriseBag;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private FarmerDto farmer;

    public static ProductItemDto of(ProductItem item) {
        return ProductItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .imgUrl(item.getImgUrl())
                .isSurpriseBag(item.isSurpriseBag())
                .startTime(item.getStartTime())
                .endTime(item.getEndTime())
                .farmer(item.getFarmer() != null ? item.getFarmer().toDto() : null) // Используем toDto вместо build
                .build();
    }
}