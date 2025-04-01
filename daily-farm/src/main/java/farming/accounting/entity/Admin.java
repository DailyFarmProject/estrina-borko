package farming.accounting.entity;

import farming.accounting.dto.UserType;
import farming.farmer.entity.Address;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "admins")
@PrimaryKeyJoinColumn(name = "id")
public class Admin extends UserAccount {

    public Admin(String login, String hash, String firstName, String lastName,
                 String email, String phone, Address address) {
        super(login, hash, firstName, lastName, UserType.ADMIN, email, phone, address);
    }
}