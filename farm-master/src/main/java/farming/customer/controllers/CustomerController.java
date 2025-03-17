package farming.customer.controllers;

import farming.api.constants.CustomerApiConstants;
import farming.customer.dto.CustomerDto;
import farming.customer.service.CustomerService;
import farming.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(CustomerApiConstants.BASE_PATH)
@Slf4j
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping(CustomerApiConstants.ME)
    public ResponseEntity<CustomerDto> getCurrentCustomer(@AuthenticationPrincipal User user) {
        log.info("Fetching current customer for user: {}", user != null ? user.getEmail() : "null");
        if (user == null) {
            log.error("User not authenticated");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        CustomerDto customer = customerService.getCustomerByLogin(user.getEmail())
                .orElseThrow(() -> {
                    log.error("Customer profile not found for user: {}", user.getEmail());
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer profile not found for user: " + user.getEmail());
                });
        return ResponseEntity.ok(customer);
    }

    @GetMapping(CustomerApiConstants.BY_ID)
    public ResponseEntity<CustomerDto> getCustomer(@PathVariable Long customerId) {
        log.info("Fetching customer with ID: {}", customerId);
        CustomerDto customer = customerService.getCustomer(customerId);
        return ResponseEntity.ok(customer);
    }

    @GetMapping(CustomerApiConstants.ALL)
    public ResponseEntity<List<CustomerDto>> getAllCustomers() {
        log.info("Fetching all customers");
        List<CustomerDto> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @PutMapping(CustomerApiConstants.UPDATE)
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable Long customerId, @RequestBody CustomerDto dto) {
        log.info("Updating customer with ID: {}", customerId);
        CustomerDto updatedCustomer = customerService.updateCustomer(customerId, dto);
        return ResponseEntity.ok(updatedCustomer);
    }

    @DeleteMapping(CustomerApiConstants.DELETE)
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long customerId) {
        log.info("Deleting customer with ID: {}", customerId);
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(CustomerApiConstants.TOP_UP)
    public ResponseEntity<CustomerDto> topUpBalance(@PathVariable Long customerId, @RequestParam double amount) {
        log.info("Request to top up balance for customer ID: {} with amount: {}", customerId, amount);
        CustomerDto updatedCustomer = customerService.topUpBalance(customerId, amount);
        return ResponseEntity.ok(updatedCustomer);
    }
}