package farming.farmer.entity;

import farming.accounting.entity.UserAccount;
import farming.accounting.dto.UserResponseDto;
import farming.accounting.dto.UserType;
import farming.farmer.dto.AddressDto;
import farming.farmer.dto.FarmerDto;
import farming.products.entity.ProductItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "farmers")
@PrimaryKeyJoinColumn(name = "id")
public class Farmer extends UserAccount {

    private String additionalPhone;

    @OneToMany(mappedBy = "farmer", cascade = CascadeType.ALL)
    private List<ProductItem> productItems = new ArrayList<>();

    public Farmer(String login, String hash, String firstName, String lastName,
                  String email, String phone, Address address, String additionalPhone) {
        super(login, hash, firstName, lastName, UserType.FARMER, email, phone, address);
        this.additionalPhone = additionalPhone;
    }

    public FarmerDto toDto() {
        return FarmerDto.builder()
                .farmerId(this.getId())
                .firstName(this.getFirstName())
                .lastName(this.getLastName())
                .phone(this.getPhone())
                .address(this.getAddress() != null ?
                        new AddressDto(this.getAddress().getCountry(), this.getAddress().getCity(), this.getAddress().getStreet()) : null)
                .build();
    }

    @Override
    public UserResponseDto build() { // Для совместимости с UserAccount
        return new UserResponseDto(
                this.getLogin(),
                this.getFirstName(),
                this.getLastName(),
                this.getEmail(),
                this.getPhone(),
                this.getAddress(),
                this.getUserType()
        );
    }

    public void addProductItem(ProductItem productItem) {
        productItems.add(productItem);
        productItem.setFarmer(this);
    }
}