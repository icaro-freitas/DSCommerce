package com.devsuperior.dscommerce.controllers.it;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Propagation;

import com.devsuperior.dscommerce.dto.ProductDTO;
import com.devsuperior.dscommerce.entities.Category;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.tests.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TokenUtil tokenUtil;

	@Autowired
	private ObjectMapper objectMapper;

	private Long existingProductId;

	private Long nonExistingProductId;

	private Long dependentProductId;

	private String clientUserName;

	private String clientPassword;

	private String adminUserName;

	private String adminPassword;

	private String clientToken;

	private String adminToken;

	private String invalidToken;

	private String productName;

	private Product product;
	private ProductDTO productDTO;

	@BeforeEach
	void setUp() throws Exception {

		clientUserName = "maria@gmail.com";
		clientPassword = "123456";
		adminUserName = "alex@gmail.com";
		adminPassword = "123456";

		productName = "Macbook";

		existingProductId = 2L;

		nonExistingProductId = 100L;

		dependentProductId = 3L;

		adminToken = tokenUtil.obtainAccessToken(mockMvc, adminUserName, adminPassword);
		clientToken = tokenUtil.obtainAccessToken(mockMvc, clientUserName, clientPassword);
		invalidToken = adminToken + "xpto";

		Category category = new Category(2L, "Electro");
		product = new Product(null, "Console Playstation 5",
				"Lorem ipsum, dolor sit amet consectetur adipisicing elit.", 3999.90,
				"https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg");
		product.getCategories().add(category);
		productDTO = new ProductDTO(product);

	}

	@Test
	public void findAllShouldReturnPageWhenNameParamIsNotEmpty() throws Exception {

		ResultActions result = mockMvc
				.perform(get("/products?name={productName}", productName).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.content[0].id").value(3L));
		result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
		result.andExpect(jsonPath("$.content[0].price").value(1250.0));
		result.andExpect(jsonPath("$.content[0].imgUrl").value(
				"https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/3-big.jpg"));
	}

	@Test
	public void findAllShouldReturnPageWhenNameParamIsEmpty() throws Exception {

		ResultActions result = mockMvc.perform(get("/products").accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.content[0].id").value(1L));
		result.andExpect(jsonPath("$.content[0].name").value("The Lord of the Rings"));
		result.andExpect(jsonPath("$.content[0].price").value(90.5));
		result.andExpect(jsonPath("$.content[0].imgUrl").value(
				"https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg"));
	}

	@Test
	public void insertShouldReturnProductDTOCreatedWhenAdminLogged() throws Exception {

		String jsonBody = objectMapper.writeValueAsString(productDTO);

		ResultActions result = mockMvc
				.perform(post("/products").header("Authorization", "Bearer " + adminToken).content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andDo(MockMvcResultHandlers.print());

		result.andExpect(status().isCreated());
		result.andExpect(jsonPath("$.id").value(26L));
		result.andExpect(jsonPath("$.name").value(product.getName()));
		result.andExpect(jsonPath("$.description").value(product.getDescription()));
		result.andExpect(jsonPath("$.price").value(product.getPrice()));
		result.andExpect(jsonPath("$.imgUrl").value(product.getImgUrl()));
		result.andExpect(jsonPath("$.categories[0].id").value(2L));

	}

	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidDescription() throws Exception {

		product.setDescription("Ab");
		productDTO = new ProductDTO(product);
		String jsonBody = objectMapper.writeValueAsString(productDTO);

		ResultActions result = mockMvc.perform(post("/products").header("Authorization", "Bearer " + adminToken)
				.content(jsonBody).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndPriceIsNegative() throws Exception {

		product.setPrice(-50.0);
		productDTO = new ProductDTO(product);
		String jsonBody = objectMapper.writeValueAsString(productDTO);

		ResultActions result = mockMvc.perform(post("/products").header("Authorization", "Bearer " + adminToken)
				.content(jsonBody).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndPriceIsZero() throws Exception {

		product.setPrice(0.0);
		productDTO = new ProductDTO(product);
		String jsonBody = objectMapper.writeValueAsString(productDTO);

		ResultActions result = mockMvc.perform(post("/products").header("Authorization", "Bearer " + adminToken)
				.content(jsonBody).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndProductHasNotCategories() throws Exception {

		product.getCategories().clear();
		productDTO = new ProductDTO(product);
		String jsonBody = objectMapper.writeValueAsString(productDTO);

		ResultActions result = mockMvc.perform(post("/products").header("Authorization", "Bearer " + adminToken)
				.content(jsonBody).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void insertShouldReturnForbiddenWhenClientLogged() throws Exception {

		String jsonBody = objectMapper.writeValueAsString(productDTO);

		ResultActions result = mockMvc.perform(post("/products").header("Authorization", "Bearer " + clientToken)
				.content(jsonBody).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));
		result.andExpect(status().isForbidden());
	}

	@Test
	public void insertShouldReturnUnautorizedWhenInvalidToken() throws Exception {

		String jsonBody = objectMapper.writeValueAsString(productDTO);

		ResultActions result = mockMvc.perform(post("/products").header("Authorization", "Bearer " + invalidToken)
				.content(jsonBody).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));
		result.andExpect(status().isUnauthorized());
	}

	@Test
	@Transactional(propagation = Propagation.SUPPORTS)
	public void deleteShouldReturnNoContentWhenIdExistsAndAdminLogged() throws Exception {

		ResultActions result = mockMvc.perform(delete("/products/{id}", existingProductId)
				.header("Authorization", "Bearer " + adminToken).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNoContent());

	}

	@Test
	@Transactional(propagation = Propagation.SUPPORTS)
	public void deleteShouldReturnNotFoundWhenIdExistsAndAdminLogged() throws Exception {

		ResultActions result = mockMvc.perform(delete("/products/{id}", nonExistingProductId)
				.header("Authorization", "Bearer " + adminToken).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNotFound());

	}

	@Test
	@Transactional(propagation = Propagation.SUPPORTS)
	public void deleteShouldReturnBadRequestWhenIdExistsAndDependentAndAdminLogged() throws Exception {

		ResultActions result = mockMvc.perform(delete("/products/{id}", dependentProductId)
				.header("Authorization", "Bearer " + adminToken).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isBadRequest());

	}

	@Test
	@Transactional(propagation = Propagation.SUPPORTS)
	public void deleteShouldReturnUnauthorizedWhenIdExistsAndClientLogged() throws Exception {

		ResultActions result = mockMvc.perform(delete("/products/{id}", existingProductId)
				.header("Authorization", "Bearer " + invalidToken).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isUnauthorized());

	}

}
