package farming.customer.service;

import farming.customer.dto.CustomerDto;
import farming.customer.entity.Customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ICustomerService {

    void createCustomerProfile(Customer customer);

    CustomerDto getCustomer(UUID customerId);

    List<CustomerDto> getAllCustomers();

    CustomerDto updateCustomer(UUID customerId, CustomerDto dto);

    void deleteCustomer(UUID customerId);

    Optional<CustomerDto> getCustomerByLogin(String login);
}