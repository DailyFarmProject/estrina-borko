package farming.customer.controllers;

import farming.accounting.entity.UserAccount;
import farming.api.CustomerApi;
import farming.customer.dto.CustomerDto;
import farming.customer.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(CustomerApi.BASE)
@Slf4j
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping(CustomerApi.ME)
    public ResponseEntity<CustomerDto> getCurrentCustomer(@AuthenticationPrincipal UserAccount user) {
        log.info("Fetching current customer for user: {}", user != null ? user.getLogin() : "null");
        if (user == null) {
            log.error("User not authenticated");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        CustomerDto customer = customerService.getCustomerByLogin(user.getLogin())
                .orElseThrow(() -> {
                    log.error("Customer profile not found for user: {}", user.getLogin());
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer profile not found for user: " + user.getLogin());
                });
        return ResponseEntity.ok(customer);
    }

    @GetMapping(CustomerApi.BY_ID)
    public ResponseEntity<CustomerDto> getCustomer(@PathVariable UUID customerId) {
        log.info("Fetching customer with ID: {}", customerId);
        CustomerDto customer = customerService.getCustomer(customerId);
        return ResponseEntity.ok(customer);
    }

    @GetMapping(CustomerApi.ALL)
    public ResponseEntity<List<CustomerDto>> getAllCustomers() {
        log.info("Fetching all customers");
        List<CustomerDto> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @PutMapping(CustomerApi.UPDATE)
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable UUID customerId, @RequestBody CustomerDto dto) {
        log.info("Updating customer with ID: {}", customerId);
        CustomerDto updatedCustomer = customerService.updateCustomer(customerId, dto);
        return ResponseEntity.ok(updatedCustomer);
    }

    @DeleteMapping(CustomerApi.DELETE)
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID customerId) {
        log.info("Deleting customer with ID: {}", customerId);
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }
}