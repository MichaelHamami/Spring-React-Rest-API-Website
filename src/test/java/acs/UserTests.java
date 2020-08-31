package acs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import acs.data.utils.UserRole;
import acs.rest.users.UserBoundary;
import acs.rest.users.UserNewDetails;
import acs.rest.utils.UserIdBoundary;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserTests {

	private int port;
	private RestTemplate restTemplate;
	private String url;
	private String domain;
	
	
	private final static String GET = "users/login/{userDomain}/{userEmail}";
	private final static String PUT = "users/{userDomain}/{userEmail}";
	private final static String POST = "users";
	private final static String DELETE_ALL_USERS = "/admin/users/{adminDomain}/{adminEmail}";
	
	private UserBoundary player;
	private UserBoundary manager;
	private UserBoundary admin;

	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}

	@PostConstruct
	public void init() {
		this.url = "http://localhost:" + this.port + "/acs/";
		this.restTemplate = new RestTemplate();
		this.domain = "2020b.ofir.cohen";
		
		this.player = this.restTemplate.postForObject(this.url + POST ,
				 new UserNewDetails("p@gmail.com", UserRole.PLAYER, "player", ":_"),
	  			UserBoundary.class);
	
	
		this.manager = this.restTemplate.postForObject(this.url + POST,  
				new UserNewDetails("m@gmail.com", UserRole.MANAGER, "manager", ":/"),
		  			UserBoundary.class);
		
		this.admin =  this.restTemplate.postForObject(this.url + POST,  
				new UserNewDetails("a@gmail.com",  UserRole.ADMIN, "admin", ":*"),
	 			UserBoundary.class);
	}

	@AfterEach
	public void tearDown() {
		this.restTemplate.delete(this.url + DELETE_ALL_USERS ,this.admin.getUserId().getDomain() , this.admin.getUserId().getEmail());
	}

	public String userIdToURL(UserIdBoundary userId) {
		return userId.getDomain() + "/" + userId.getEmail();
	}

	@Test
	public void testContext() {

	}

	@Test
	public void testPostNewUserReturnUserWithId() throws Exception {
		// GIVEN server is up

		// WHEN I POST /users AND send a user boundary
		UserNewDetails input = new UserNewDetails("tomer32@gmail.com", UserRole.ADMIN, "tomer test", ";[");

		UserBoundary output = this.restTemplate.postForObject(this.url+ POST , input, UserBoundary.class);

		// THEN the server returns status 2xx
		// AND retrieves a user with non null id
		if (output.getUserId() == null) {
			throw new Exception("expected non null id but id was null");
		}
	}

	@Test
	public void testPostNewUserReturnUserWithSameEmail() throws Exception {
		// GIVEN server is up

		// WHEN I POST /users AND send a user boundary
		UserNewDetails input = new UserNewDetails("tomer32@gmail.com", UserRole.ADMIN, "tomer test", ";[");

		UserBoundary output = this.restTemplate.postForObject(this.url + POST , input, UserBoundary.class);

		// THEN the server returns status 2xx,
		// AND retrieves a user with same email as sent to server
		if (!(output.getUserId().getEmail().equals(input.getEmail()))) {
			throw new Exception("expected simplar message to input but received: " + output.getUserId().getEmail());
		}
	}

	@Test
	public void testPostNewUserAndValidateTheDatabseContainsUserWithTheSameId() throws Exception {
		// GIVEN server is up

		// WHEN I POST /users AND send a user boundary
		UserNewDetails input = new UserNewDetails("tomer32@gmail.com", UserRole.ADMIN, "tomer test", ";[");

		UserBoundary userPost = this.restTemplate.postForObject(this.url + POST , input, UserBoundary.class);

		// THEN server contains a single user in the database
		// AND it's user details is similar input's
		UserBoundary output = this.restTemplate.getForObject(this.url + GET, UserBoundary.class, this.domain,
				input.getEmail());

		assertThat(output.getUserId()).extracting("domain", "email").containsExactly(userPost.getUserId().getDomain(),
				userPost.getUserId().getEmail());

	}

	@Test
	public void testPostNewUserTheDatabaseContainsUserWithTheSameRole() throws Exception {
		// GIVEN server is up

		// WHEN I POST /samples AND send a user boundary
		UserNewDetails input = new UserNewDetails("test23@gmail.com", UserRole.MANAGER, "testy test", ";=}");

		UserBoundary userPost = this.restTemplate.postForObject(this.url + POST , input, UserBoundary.class);

		// THEN server contains user with generated role
		UserBoundary output = this.restTemplate.getForObject(this.url + GET, UserBoundary.class, this.domain,
				input.getEmail());

		assertThat(output.getRole()).isNotNull();

		assertThat(output.getRole()).usingRecursiveComparison().isEqualTo(userPost.getRole());
	}

	@Test
	public void testUpdateUserDetsilsActuallyUpdateDatabse() throws Exception {
		// GIVEN the server is up AND the database contains a user

		UserNewDetails input = new UserNewDetails("test23@gmail.com", UserRole.MANAGER, "testy test", ";=}");

		UserBoundary inputUser = this.restTemplate.postForObject(this.url + POST , input, UserBoundary.class);

		// WHEN I update details to be different avatar
		inputUser.setAvatar(";]");
		this.restTemplate.put(this.url + PUT, inputUser, this.domain, inputUser.getUserId().getEmail());

		// THEN the database is updated with the new avatar
		assertThat(this.restTemplate
				.getForObject(this.url + GET, UserBoundary.class, this.domain, inputUser.getUserId().getEmail())
				.getAvatar()).isEqualTo(";]");
	}

	@Test
	public void testPostUnrelevantDataToServer() throws Exception {
		// Given the server is up
		// WHEN I post {"x":"y"}
		// THEN the server response with status != 2xx

		Object targetObject = Collections.singletonMap("x", "y");
		try {
			this.restTemplate.postForObject(this.url + POST ,

					targetObject, UserBoundary.class);

			fail("server responded with status 2xx - which was not expected");

		} catch (RestClientException e) {

		}
	}

	@Test
	public void testPostUsernameWithSpacesOnlyMessageToServer() {

		// GIVEN the server is up
		// WHEN I POST {"x":"y"}
		// THEN the server responds with status != 2xx
		UserBoundary input = new UserBoundary();
		input.setUserName("  ");

		assertThrows(RestClientException.class, () -> this.restTemplate.postForObject(this.url,

				input, UserBoundary.class));
	}
	

}
