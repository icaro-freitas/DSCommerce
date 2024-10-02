package com.devsuperior.dscommerce.services;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.projections.UserDetailsProjection;
import com.devsuperior.dscommerce.repositories.UserRepository;
import com.devsuperior.dscommerce.tests.UserDetailsFactory;
import com.devsuperior.dscommerce.tests.UserFactory;

@ExtendWith(SpringExtension.class)
public class UserServiceTests {

	@InjectMocks
	private UserService service;

	@Mock
	private UserRepository repository;

	private String existingUserName;
	private String nonExistingUserName;
	private User user;
	private List<UserDetailsProjection> userDetails;

	@BeforeEach
	private void setUp() throws Exception {
		existingUserName = "maria@gmail.com";
		nonExistingUserName = "user@gmail.com";

		user = UserFactory.createCustomClientUser(1L, existingUserName);
		userDetails = UserDetailsFactory.createCustomAdminUser(existingUserName);

		Mockito.when(repository.searchUserAndRolesByEmail(existingUserName)).thenReturn(userDetails);
		Mockito.when(repository.searchUserAndRolesByEmail(nonExistingUserName)).thenReturn(new ArrayList<>());
	}

	@Test
	public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {
		UserDetails result = service.loadUserByUsername(existingUserName);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getUsername(), existingUserName);
	}

	@Test
	public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExist() {
		Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			service.loadUserByUsername(nonExistingUserName);
		});
	}

}
