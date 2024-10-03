package com.devsuperior.dscommerce.tests;

import java.time.LocalDate;

import com.devsuperior.dscommerce.entities.Role;
import com.devsuperior.dscommerce.entities.User;

public class UserFactory {

	public static User createClientUser() {
		User user = new User(1L, "Maria", "maria@gmail.com", "988888888", LocalDate.parse("2001-07-25"),
				"$2a$10$1M.YfYbPpz03119u1OBn/.KMNS33GqRMAJ9W8MTfJ3vno4Zfsm/X6");
		user.addRole(new Role(1L, "ROLE_CLIENT"));
		return user;
	}

	public static User createAdminUser() {
		User user = new User(2L, "Alex", "alex@gmail.com", "988888888", LocalDate.parse("1987-12-13"),
				"$2a$10$1M.YfYbPpz03119u1OBn/.KMNS33GqRMAJ9W8MTfJ3vno4Zfsm/X6");
		user.addRole(new Role(2L, "ROLE_ADMIN"));
		return user;
	}

	public static User createCustomClientUser(Long id, String username) {
		User user = new User(id, "Maria", username, "988888888", LocalDate.parse("2001-07-25"),
				"$2a$10$1M.YfYbPpz03119u1OBn/.KMNS33GqRMAJ9W8MTfJ3vno4Zfsm/X6");
		user.addRole(new Role(1L, "ROLE_CLIENT"));
		return user;
	}

	public static User createCustomAdminUser(Long id, String username) {
		User user = new User(id, "alex@gmail.com", username, "988888888", LocalDate.parse("1987-12-13"),
				"$2a$10$1M.YfYbPpz03119u1OBn/.KMNS33GqRMAJ9W8MTfJ3vno4Zfsm/X6");
		user.addRole(new Role(2L, "ADMIN_CLIENT"));
		return user;
	}

}
