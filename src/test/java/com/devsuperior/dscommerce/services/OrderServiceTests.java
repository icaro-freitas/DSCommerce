package com.devsuperior.dscommerce.services;

import static org.mockito.ArgumentMatchers.any;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscommerce.dto.OrderDTO;
import com.devsuperior.dscommerce.entities.Order;
import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.repositories.OrderRepository;
import com.devsuperior.dscommerce.services.exceptions.ForbiddenException;
import com.devsuperior.dscommerce.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscommerce.tests.OrderFactory;
import com.devsuperior.dscommerce.tests.UserFactory;

@ExtendWith(SpringExtension.class)
public class OrderServiceTests {

	@InjectMocks
	private OrderService service;

	@Mock
	private OrderRepository repository;

	@Mock
	private AuthService authService;

	private Long existingOrderId;
	private Long nonExistingOrderId;
	private Order order;
	private OrderDTO orderDTO;
	private User admin;
	private User client;

	@BeforeEach
	private void setUp() throws Exception {
		existingOrderId = 1L;
		nonExistingOrderId = 2L;

		admin = UserFactory.createCustomAdminUser(1L, "Jef");
		client = UserFactory.createCustomClientUser(2L, "Bob");

		order = OrderFactory.createOrder(client);

		orderDTO = new OrderDTO(order);

		Mockito.when(repository.findById(existingOrderId)).thenReturn(Optional.of(order));
		Mockito.when(repository.findById(nonExistingOrderId)).thenReturn(Optional.empty());
	}

	@Test
	public void findByIdShouldReturnOrderDTOWhenIdExistsAndAdminLogged() {
		Mockito.doNothing().when(authService).validateSelfOrAdmin(admin.getId());

		OrderDTO result = service.findById(existingOrderId);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), existingOrderId);
	}

	@Test
	public void findByIdShouldReturnOrderDTOWhenIdExistsAndSelfClientLogged() {
		Mockito.doNothing().when(authService).validateSelfOrAdmin(any());

		OrderDTO result = service.findById(existingOrderId);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), existingOrderId);

	}
	
	@Test
	public void findByShouldThrowsForbiddenExceptionWhenIdExistAndOtherClientLogged() {
		Mockito.doThrow(ForbiddenException.class).when(authService).validateSelfOrAdmin(any());
		
		Assertions.assertThrows(ForbiddenException.class, ()->{
			service.findById(existingOrderId);
		});		
	}
	
	@Test
	public void findByShouldThrowsResourceNotFoundExceptionWhenIdDoesNotExistAndOtherClientLogged() {
		Mockito.doNothing().when(authService).validateSelfOrAdmin(any());
		
		Assertions.assertThrows(ResourceNotFoundException.class, ()->{
			service.findById(nonExistingOrderId);
		});
	}
	

}
