package farming.accounting.dto;

import farming.farmer.dto.AddressDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserRequestDto {

	
	private String login;
	private String password;
	private String firstName;
	private String lastName;
	private String email;
	private String phone;       
    private AddressDto address; 
	private UserType userType;
}
