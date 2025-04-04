package farming.accounting.dto;

import farming.farmer.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class UserResponseDto {

    private String login;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Address address;
    private UserType userType;
}