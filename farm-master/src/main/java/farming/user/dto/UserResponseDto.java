package farming.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    private Long id;

    private Date createTime;

    private Date updateTime;

    private String name;

    private String email;

    private String password;

    private String roles;
}
