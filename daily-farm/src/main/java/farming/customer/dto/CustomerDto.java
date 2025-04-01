package farming.customer.dto;

import farming.accounting.dto.UserResponseDto;
import farming.farmer.entity.Address;
import farming.accounting.dto.UserType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@NoArgsConstructor
@Getter
@SuperBuilder

public class CustomerDto extends UserResponseDto {
    private UUID customerId;
    
    public CustomerDto(String login, String firstName, String lastName, String email, 
                      String phone, Address address, UserType userType, UUID customerId) {
        super(login, firstName, lastName, email, phone, address, userType);
        this.customerId = customerId;
    }
}