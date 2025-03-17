package farming.products.entity;

import java.util.ArrayList;
import java.util.List;

import farming.farmer.dto.FarmerDto;
import farming.farmer.entity.Farmer;
import farming.products.dto.ProductDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productName;
    private int quantity;
    private Double price;
    private String imgUrl;

	@ManyToOne
	@JoinColumn(name = "farmer_id", referencedColumnName = "farmerId")
	private Farmer farmer;
	
	private boolean deleted = false;
	
	public static Product of(ProductDto dto) {
		return Product.builder()
	            .id(dto.getProductId())
	            .productName(dto.getProductName())
	            .quantity(dto.getQuantity())
	            .price(dto.getPrice())
	            .imgUrl(dto.getImgUrl())
	            .build();
    }

	public ProductDto toDto() {
        FarmerDto farmerDto = farmer.build();
      
        return ProductDto.builder()
                .productId(id)
                .productName(productName)
                .quantity(quantity)
                .price(price)
                .imgUrl(imgUrl)
                .farmer(farmerDto)  
                .build();
    }
}
