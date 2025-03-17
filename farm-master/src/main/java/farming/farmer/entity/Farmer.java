package farming.farmer.entity;

import farming.farmer.dto.AddressDto;
import farming.farmer.dto.FarmerDto;
import farming.products.entity.Product;
import farming.products.entity.SurpriseBag;
import farming.user.entity.User;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "farmers")
public class Farmer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long farmerId;

    //	String firstName;
//	String lastName;
    String email;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    public User userAccount;

    String phone;

    @Embedded
    Address address;

    @OneToMany(mappedBy = "farmer")
    List<Product> products;

    @OneToMany(mappedBy = "farmer")
    List<SurpriseBag> surpriseBags;

    Double balance;

    public static Farmer of(FarmerDto dto) {
        return Farmer.builder()
                .farmerId(dto.getFarmerId()).phone(dto.getPhone())
                .address(dto.getAddress() != null ? new Address(dto.getAddress().getCountry(), dto.getAddress().getCity(),
                        dto.getAddress().getStreet()) : null)
                .balance(dto.getBalance()).build();
    }


    public FarmerDto build() {
        return FarmerDto.builder()
                .farmerId(farmerId)
                .phone(phone)
                .address(address != null ? new AddressDto(address.getCountry(), address.getCity(), address.getStreet()) : null)
                .balance(balance)
                .build();
    }


}
