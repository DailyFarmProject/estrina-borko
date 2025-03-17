package farming.products.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import farming.farmer.entity.Farmer;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "surprise_bags")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SurpriseBag {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name = "Surprise Bag"; // Название по умолчанию
    private int quantity; // Ограниченное количество
    private double price = 5.0; // Низкая фиксированная цена
    private LocalDateTime startTime; // Время появления
    private LocalDateTime endTime; // Время окончания

    @ManyToOne
    @JoinColumn(name = "farmerId")
    private Farmer farmer;

    @OneToMany
    @JoinColumn(name = "product_id")
    private List<Product> products;

    public boolean isAvailable() {
        LocalDateTime now = LocalDateTime.now();
        return quantity > 0 && now.isAfter(startTime) && now.isBefore(endTime);
    }
    
    
    

}
