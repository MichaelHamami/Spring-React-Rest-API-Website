package acs;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;
import acs.data.utils.UserRole;
import acs.rest.users.UserBoundary;
import acs.rest.users.UserNewDetails;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserTestPagenation {

	private int port;
	private RestTemplate restTemplate;
	private String url;
	
	private UserBoundary admin;
	
	@PostConstruct
	public void init() {
		this.url = "http://localhost:" + this.port + "/acs";
		this.restTemplate = new RestTemplate();
		
		this.admin =  this.restTemplate.postForObject(this.url + "/users",  
				new UserNewDetails("a@gmail.com",  UserRole.ADMIN, "admin", ":*"),
	 			UserBoundary.class);
	}

	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}
	
	@AfterEach
	public void setUp() {
		
		
		UserBoundary admin = this.restTemplate.postForObject(this.url + "/users",
				new UserNewDetails("adminx@gmail.com", UserRole.PLAYER, "x test", ";["), UserBoundary.class);
	}
	
	@AfterEach
	public void tearUp() {
		this.restTemplate.delete(this.url + "/admin/users/{domain}/{email}", admin.getUserId().getDomain(),
				admin.getUserId().getEmail());
	}

	@Test
	public void checkExportAllUsersWithPagination() throws Exception {
		// WHEN the server is up
		// AND the data base contain 20 users

		
		List<UserBoundary> users = new ArrayList<>();
		for (int i = 0; i < 20; i++) {
			users.add(this.restTemplate.postForObject(this.url + "/users",
					new UserNewDetails("x" + i + "@gmail.com", UserRole.PLAYER, "x test " + i, ";["),
					UserBoundary.class, 
					admin.getUserId().getDomain(), admin.getUserId().getEmail()));
			
			
			
		}
		// THEN the data base retrieve 10 users in the second page
		UserBoundary[] getUsers = this.restTemplate.getForObject(this.url + "/admin/users/{domain}/{email}"
		+"?size={size}&page={page}", UserBoundary[].class, admin.getUserId().getDomain(), admin.getUserId().getEmail(), 10,1);
		
		assertThat(getUsers).hasSize(10);
		
		
	

		
	}

}
