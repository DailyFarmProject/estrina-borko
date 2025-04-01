package farming.accounting.service;

import farming.accounting.dto.UserRequestDto;
import farming.accounting.dto.UserResponseDto;
import farming.accounting.dto.UserType;
import farming.accounting.dto.exceptions.*;
import farming.accounting.entity.Admin;
import farming.accounting.entity.UserAccount;
import farming.accounting.repo.UserRepository;
import farming.customer.entity.Customer;
import farming.customer.service.CustomerService;
import farming.farmer.entity.Address;
import farming.farmer.entity.Farmer;
import farming.farmer.service.FarmerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedList;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountingManagement implements IAccountingManagement, CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FarmerService farmerService;
    private final CustomerService customerService;

    @Value("${password.length:5}")
    private int passwordLength;

    @Value("${last_hash:3}")
    private int nLastHash;

    @Override
    @Transactional
    public UserResponseDto registration(UserRequestDto userDto, UserType userType) {
        log.info("Registering user with login: {}", userDto.getLogin());
        String password = userDto.getPassword();
        String login = userDto.getLogin();
        if (isPasswordValid(password)) {
            log.error("Invalid password for login: {}", login);
            throw new PasswordValidException(password);
        }
        if (userRepository.existsByLogin(login)) {
            log.warn("User already exists: {}", login);
            throw new UserExistsException(login);
        }
        Address address = userDto.getAddress() != null
                ? new Address(userDto.getAddress().getCountry(), userDto.getAddress().getCity(), userDto.getAddress().getStreet())
                : null;

        UserAccount user;
        switch (userType) {
            case FARMER:
                user = new Farmer(login, passwordEncoder.encode(password), userDto.getFirstName(),
                        userDto.getLastName(), userDto.getEmail(), userDto.getPhone(), address, null);
                farmerService.createFarmerProfile((Farmer) user);
                break;
            case CUSTOMER:
                user = new Customer(login, passwordEncoder.encode(password), userDto.getFirstName(),
                        userDto.getLastName(), userDto.getEmail(), userDto.getPhone(), address);
                customerService.createCustomerProfile((Customer) user);
                break;
            case ADMIN:
                user = new Admin(login, passwordEncoder.encode(password), userDto.getFirstName(),
                        userDto.getLastName(), userDto.getEmail(), userDto.getPhone(), address);
                break;
            default:
                throw new IllegalArgumentException("Unknown user type: " + userType);
        }

        userRepository.save(user);
        log.info("User registered: {}", login);
        return user.build();
    }

    @Override
    public UserResponseDto getUser(String login) {
        log.info("Fetching user with login: {}", login);
        UserAccount user = getUserAccount(login);
        return user.build();
    }

    @Override
    @Transactional
    public UserResponseDto editUser(UserResponseDto user, String login) {
        UserAccount account = getUserAccount(login);
        if (user.getFirstName() != null) account.setFirstName(user.getFirstName());
        if (user.getLastName() != null) account.setLastName(user.getLastName());
        userRepository.save(account);
        return account.build();
    }

    @Override
    @Transactional
    public boolean updatePassword(String login, String newPassword) {
        log.info("Updating password for login: {}", login);
        if (newPassword == null || isPasswordValid(newPassword)) {
            log.error("Invalid password: {}", newPassword);
            throw new PasswordValidException(newPassword);
        }
        UserAccount user = getUserAccount(login);
        if (passwordEncoder.matches(newPassword, user.getHash())) {
            log.warn("New password matches current password for login: {}", login);
            throw new PasswordValidException(newPassword);
        }
        LinkedList<String> lastHash = user.getLastHash();
        if (lastHash == null) {
            lastHash = new LinkedList<>();
            user.setLastHash(lastHash);
        }
        if (isPasswordFromLast(newPassword, lastHash)) {
            log.warn("New password matches a previous password for login: {}", login);
            throw new PasswordValidException(newPassword);
        }
        if (lastHash.size() >= nLastHash) {
            lastHash.removeFirst();
        }
        lastHash.add(user.getHash());
        user.setHash(passwordEncoder.encode(newPassword));
        user.setActivationDate(LocalDateTime.now());
        userRepository.save(user);
        log.info("Password updated successfully for login: {}", login);
        return true;
    }

    private boolean isPasswordValid(String password) {
        return password == null || password.length() < passwordLength;
    }

    private boolean isPasswordFromLast(String newPassword, LinkedList<String> lastHash) {
        return lastHash.stream().anyMatch(h -> passwordEncoder.matches(newPassword, h));
    }

    @Override
    @Transactional
    public UserResponseDto removeUser(String login) {
        log.info("Removing user with login: {}", login);
        UserAccount user = getUserAccount(login);
        UserResponseDto response = user.build();
        userRepository.delete(user);
        return response;
    }

    @Override
    @Transactional
    public boolean revokeAccount(String login) {
        log.info("Revoking account with login: {}", login);
        UserAccount user = getUserAccount(login);
        if (user.isRevoked()) {
            log.warn("Account already revoked: {}", login);
            throw new AccountRevokeException(login);
        }
        user.setRevoked(true);
        userRepository.save(user);
        log.info("Account revoked: {}", login);
        return true;
    }

    @Override
    @Transactional
    public boolean activateAccount(String login) {
        log.info("Activating account with login: {}", login);
        UserAccount user = getUserAccount(login);
        if (!user.isRevoked()) {
            log.warn("Account already active: {}", login);
            throw new AccountActivateException(login);
        }
        user.setRevoked(false);
        user.setActivationDate(LocalDateTime.now());
        userRepository.save(user);
        log.info("Account activated: {}", login);
        return true;
    }

    @Override
    public UserType getUserType(String login) {
        log.info("Fetching user type for login: {}", login);
        UserAccount user = userRepository.findByLogin(login);
        if (user == null) {
            throw new UserNotFoundException(login);
        }
        return user.getUserType();
    }

    private UserAccount getUserAccount(String login) {
        UserAccount user = userRepository.findByLogin(login);
        if (user == null) {
            throw new UserNotFoundException(login);
        }
        return user;
    }

    @Override
    public String getPasswordHash(String login) {
        log.info("Fetching password hash for login: {}", login);
        UserAccount user = getUserAccount(login);
        return user.getHash();
    }

    @Override
    public LocalDateTime getActivationDate(String login) {
        log.info("Fetching activation date for login: {}", login);
        UserAccount user = getUserAccount(login);
        return user.getActivationDate();
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Checking for admin user...");
        if (!userRepository.existsByLogin("admin")) {
            log.info("Creating admin user...");
            UserAccount admin = new Admin("admin", passwordEncoder.encode("admin"), "Admin", "Admin",
                    "admin@farm.com", null, null);
            userRepository.save(admin);
            log.info("Admin user created.");
        } else {
            log.info("Admin user already exists.");
        }
    }
}