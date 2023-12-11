package org.ruslan.customer;

import lombok.AllArgsConstructor;
import org.ruslan.clients.fraud.FraudCheckResponse;
import org.ruslan.clients.fraud.FraudClient;
import org.ruslan.clients.fraud.notification.NotificationClient;
import org.ruslan.clients.fraud.notification.NotificationRequest;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final FraudClient fraudClient;
    private final NotificationClient notificationClient;
    public void registerCustomer(CustomerRegistrationRequest request) {
        Customer customer = Customer.builder()
                .firstName(request.firstName())
                .lastName((request.lastName()))
                .email(request.email())
                .build();
        customerRepository.saveAndFlush(customer);
        FraudCheckResponse fraudCheckResponse = fraudClient.isFraudster(customer.getId());

        if(fraudCheckResponse.isFraudster()) {
            throw new IllegalStateException("fraudster");
        }

        notificationClient.sendNotification(
                new NotificationRequest(
                        customer.getId(),
                        customer.getEmail(),
                        String.format("Hi %s welcome to my app", customer.getId())
                )
        );

    }
}
