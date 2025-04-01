package farming.customer.service;

import farming.customer.dto.CustomerDto;
import farming.customer.entity.Customer;
import farming.customer.repo.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CustomerService implements ICustomerService {

    private final CustomerRepository customerRepo;

    public CustomerService(CustomerRepository customerRepo) {
        this.customerRepo = customerRepo;
    }

    @Override
    @Transactional
    public void createCustomerProfile(Customer customer) {
        log.info("Creating customer profile for user: {}", customer.getLogin());
        if (customerRepo.existsByLogin(customer.getLogin())) {
            log.warn("Customer profile already exists for login: {}", customer.getLogin());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Customer profile already exists for login: " + customer.getLogin());
        }
        customerRepo.save(customer);
        log.info("Customer profile created for login: {}", customer.getLogin());
    }

    @Override
    public CustomerDto getCustomer(UUID customerId) {
        log.info("Finding customer with ID: {}", customerId);
        return customerRepo.findById(customerId)
                .map(Customer::toDto) // Используем toDto вместо build
                .orElseThrow(() -> {
                    log.error("Customer not found with ID: {}", customerId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
                });
    }

    @Override
    public List<CustomerDto> getAllCustomers() {
        log.info("Fetching all customers");
        List<CustomerDto> customers = customerRepo.findAll().stream()
                .map(Customer::toDto) // Используем toDto вместо build
                .collect(Collectors.toList());
        log.debug("Total customers found: {}", customers.size());
        return customers;
    }

    @Override
    @Transactional
    public CustomerDto updateCustomer(UUID customerId, CustomerDto dto) {
        log.info("Updating customer with ID: {}", customerId);
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> {
                    log.error("Customer not found with ID: {}", customerId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
                });
        if (dto.getFirstName() != null) customer.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) customer.setLastName(dto.getLastName());
        if (dto.getEmail() != null) customer.setEmail(dto.getEmail());
        customerRepo.save(customer);
        log.info("Customer updated successfully: {}", customerId);
        return customer.toDto(); // Используем toDto вместо build
    }

    @Override
    @Transactional
    public void deleteCustomer(UUID customerId) {
        log.info("Deleting customer with ID: {}", customerId);
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> {
                    log.error("Customer not found with ID: {}", customerId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
                });
        customerRepo.delete(customer);
        log.info("Customer deleted: {}", customerId);
    }

    @Override
    public Optional<CustomerDto> getCustomerByLogin(String login) {
        log.info("Fetching customer by login: {}", login);
        return customerRepo.findByLogin(login).map(Customer::toDto); // Используем toDto вместо build
    }
}