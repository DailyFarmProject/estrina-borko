package farming.farmer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class FarmerDto {

    private UUID farmerId;
    private String firstName;
    private String lastName;
    private String phone;
    private AddressDto address;
}