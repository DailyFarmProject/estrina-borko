package farming.user.dto;

import farming.user.entity.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Setter
@EqualsAndHashCode
public class UserRequestDto {

    private String email;

    private String password;

    private UserType userType;

    private String roles;

}
