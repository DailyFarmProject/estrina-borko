package farming.products.entity;

import farming.farmer.entity.Farmer;
import farming.products.dto.ProductItemDto;
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
@Table(name = "product_items")
public class ProductItem {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    private String name;
    private int quantity;
    private Double price;
    private String imgUrl;
    private boolean isSurpriseBag;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean deleted = false;

    @ManyToOne
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;

    public boolean isAvailable() {
        if (!isSurpriseBag) {
            return quantity > 0 && !deleted;
        }
        LocalDateTime now = LocalDateTime.now();
        return quantity > 0 && now.isAfter(startTime) && now.isBefore(endTime) && !deleted;
    }

    public static ProductItem of(ProductItemDto dto) {
        return ProductItem.builder()
                .id(dto.getId())
                .name(dto.getName())
                .quantity(dto.getQuantity())
                .price(dto.getPrice())
                .imgUrl(dto.getImgUrl())
                .isSurpriseBag(dto.isSurpriseBag())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .build();
    }

    public ProductItemDto toDto() {
        return ProductItemDto.builder()
                .id(id)
                .name(name)
                .quantity(quantity)
                .price(price)
                .imgUrl(imgUrl)
                .isSurpriseBag(isSurpriseBag)
                .startTime(startTime)
                .endTime(endTime)
                .farmer(farmer != null ? farmer.toDto() : null) // Используем toDto вместо build
                .build();
    }
}