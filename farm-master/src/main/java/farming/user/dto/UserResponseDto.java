package farming.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Setter
@EqualsAndHashCode
public class UserResponseDto {

    private Long id;

    private Date createTime;

    private Date updateTime;

    private String name;

    private String email;

    private String password;

    private String roles;
}
