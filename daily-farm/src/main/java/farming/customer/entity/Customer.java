package farming.customer.entity;

import farming.accounting.entity.UserAccount;
import farming.accounting.dto.UserResponseDto;
import farming.accounting.dto.UserType;
import farming.customer.dto.CustomerDto;
import farming.farmer.entity.Address;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "customers")
@PrimaryKeyJoinColumn(name = "id")
public class Customer extends UserAccount {

    public Customer(String login, String hash, String firstName, String lastName,
                    String email, String phone, Address address) {
        super(login, hash, firstName, lastName, UserType.CUSTOMER, email, phone, address);
    }

    public CustomerDto toDto() { // Заменили build на toDto
        return new CustomerDto(
            this.getLogin(),
            this.getFirstName(),
            this.getLastName(),
            this.getEmail(),
            this.getPhone(),
            this.getAddress(),
            this.getUserType(),
            this.getId()
        );
    }

    @Override
    public UserResponseDto build() { // Оставляем для совместимости с UserAccount
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
}