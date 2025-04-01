package farming.accounting.entity;

import farming.accounting.dto.UserResponseDto;
import farming.accounting.dto.UserType;
import farming.farmer.entity.Address;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_accounts")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class UserAccount implements UserDetails {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(unique = true)
    private String login;

    private String hash;

    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    private LocalDateTime activationDate;
    private boolean revoked;
    private LinkedList<String> lastHash = new LinkedList<>();

    public UserAccount(String login, String hash, String firstName, String lastName, UserType userType,
                       String email, String phone, Address address) {
        this.login = login;
        this.hash = hash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userType = userType;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.activationDate = LocalDateTime.now();
        this.revoked = false;
    }

    public UserResponseDto build() {
        return new UserResponseDto(login, firstName, lastName, email, phone, address, userType);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("TYPE_" + userType.name()));
    }

    @Override
    public String getPassword() {
        return hash;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !revoked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !revoked;
    }
}