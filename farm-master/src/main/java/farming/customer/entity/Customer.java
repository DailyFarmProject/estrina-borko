package farming.customer.entity;

import farming.customer.dto.CustomerDto;
import farming.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Getter
@Entity
@Builder
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User userAccount;

    private double balance;

    public static Customer of(CustomerDto dto) {
        return Customer.builder()
                .id(dto.getCustomerId())
                .balance(dto.getBalance())
                .build();
    }

    public CustomerDto build() {
        return CustomerDto.builder()
                .customerId(id)
                .firstName(userAccount != null ? userAccount.getFirstName() : null)
                .lastName(userAccount != null ? userAccount.getLastName() : null)
                .email(userAccount != null ? userAccount.getEmail() : null)
                .balance(balance)
                .build();
    }
}