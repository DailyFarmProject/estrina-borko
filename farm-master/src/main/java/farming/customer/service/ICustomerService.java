package farming.customer.service;

import farming.customer.dto.CustomerDto;
import farming.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface ICustomerService {

    void createCustomerProfile(User user);

    CustomerDto getCustomer(Long customerId);

    List<CustomerDto> getAllCustomers();

    CustomerDto updateCustomer(Long customerId, CustomerDto dto);

    void deleteCustomer(Long customerId);

    Optional<CustomerDto> getCustomerByLogin(String login);

    CustomerDto topUpBalance(Long customerId, double amount);


}
