package acs;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.PostConstruct;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;
import acs.data.utils.UserRole;
import acs.rest.action.ActionBoundary;
import acs.rest.action.boundaries.ActionElementBoundary;
import acs.rest.action.boundaries.InvokedByBoundary;
import acs.rest.element.boundaries.CreatedByBoundary;
import acs.rest.element.boundaries.ElementBoundary;
import acs.rest.users.UserBoundary;
import acs.rest.users.UserNewDetails;
import acs.rest.utils.IdBoundary;
import acs.rest.utils.UserIdBoundary;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AdminTests {

	private int port;
	private RestTemplate restTemplate;
	private String url;

	private final static String DELETE_ALL_ELEMENTS_URL = "admin/elements/{adminDomain}/{adminEmail}";
	private final static String DELETE_ALL_ACTIONS_URL = "admin/actions/{adminDomain}/{adminEmail}";
	private final static String DELETE_ALL_USERS_URL = "admin/users/{adminDomain}/{adminEmail}";
	private final static String GET_ALL_ELEMENTS_OF_USER = "elements/{userDomain}/{userEmail}";
	private final static String GET_ALL_USERS_URL = "admin/users/{adminDomain}/{adminEmail}";
	private final static String GET_ALL_ACTIONS_URL = "admin/actions/{adminDomain}/{adminEmail}";

	private final static String CREATE_USER = "users";
	private final static String CREATE_ACTION = "actions";
	private final static String CREATE_ELEMENT = "elements/{managerDomain}/{managerEmail}";

	private UserBoundary player;
	private UserBoundary manager;
	private UserBoundary admin;

	@PostConstruct
	public void init() {
		this.url = "http://localhost:" + this.port + "/acs/";
		this.restTemplate = new RestTemplate();

		this.player = this.restTemplate.postForObject(this.url + CREATE_USER,
				new UserNewDetails("p@gmail.com", UserRole.PLAYER, "player", ":_"), UserBoundary.class);

		this.manager = this.restTemplate.postForObject(this.url + CREATE_USER,
				new UserNewDetails("m@gmail.com", UserRole.MANAGER, "manager", ":/"), UserBoundary.class);

		this.admin = this.restTemplate.postForObject(this.url + CREATE_USER,
				new UserNewDetails("a@gmail.com", UserRole.ADMIN, "admin", ":*"), UserBoundary.class);
	}

	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}

	@AfterEach
	public void teardown() {
		this.restTemplate.delete(this.url + DELETE_ALL_ELEMENTS_URL, this.admin.getUserId().getDomain(),
				this.admin.getUserId().getEmail());
		
		this.restTemplate.delete(this.url + DELETE_ALL_ACTIONS_URL, this.admin.getUserId().getDomain(),
				this.admin.getUserId().getEmail());
		
		this.restTemplate.delete(this.url + DELETE_ALL_USERS_URL, this.admin.getUserId().getDomain(),
				this.admin.getUserId().getEmail());

	}

	@Test
	public void testContext() {

	}

	@Test
	public void checkDeleteAllActions() throws Exception {
				
		ElementBoundary input = this.restTemplate.postForObject(this.url + CREATE_ELEMENT , 
				new ElementBoundary(null, "INFO", "testName", true, new Date(),
						new CreatedByBoundary(manager.getUserId()), null, null),
								ElementBoundary.class,
								manager.getUserId().getDomain(), manager.getUserId().getEmail());
		
		// Given the database contains an Actions
		ActionBoundary actionInput = new ActionBoundary(new IdBoundary("domain test", "testId"), "update",
				new ActionElementBoundary(new IdBoundary(input.getElementId().getDomain(),
						input.getElementId().getId())),
				new Date(),
//				null, new Date(),
				new InvokedByBoundary(
						new UserIdBoundary(this.player.getUserId().getDomain(), this.player.getUserId().getEmail())),
				new HashMap<>());

		ActionBoundary action = this.restTemplate.postForObject(this.url + CREATE_ACTION, 
				actionInput,
				ActionBoundary.class,
				this.admin.getUserId().getDomain(), this.admin.getUserId().getEmail());

		// When i delete all Actions
		this.restTemplate.delete(this.url + DELETE_ALL_ACTIONS_URL, 
				this.admin.getUserId().getDomain(),	this.admin.getUserId().getEmail());
		
		// Then i get no actions when i check it again
		ActionBoundary[] result = this.restTemplate.getForObject(this.url + GET_ALL_ACTIONS_URL, ActionBoundary[].class,
				this.admin.getUserId().getDomain(), this.admin.getUserId().getEmail());

		assertThat(result).isEmpty();

	}

	@Test
	public void checkDeleteAllUsers() throws Exception {
		// Given the database contains an Users

		// When i delete all users
		// deleting users
		this.restTemplate.delete(this.url + DELETE_ALL_USERS_URL, this.admin.getUserId().getDomain(),
				this.admin.getUserId().getEmail());

		// this deletes the admin users too , so we needed to re-enter another admin for
		// the @aftereach to work - ariel
		
		this.admin =  this.restTemplate.postForObject(this.url + CREATE_USER,  
				new UserNewDetails("a@gmail.com",  UserRole.ADMIN, "admin", ":*"),
	 			UserBoundary.class);
		
		assertThat(this.restTemplate.getForObject(this.url + GET_ALL_USERS_URL, UserBoundary[].class, 
				this.admin.getUserId().getDomain(),this.admin.getUserId().getEmail())).hasSize(1);

	}
	
	@Test
	public void checkDeleteAllElements() throws Exception {
		// Given the database contains an Elements
		// Create element
		ElementBoundary input = new ElementBoundary(null, "INFO", "testName", true, new Date(), null, null, null);

		ElementBoundary output = this.restTemplate.postForObject(this.url + CREATE_ELEMENT, input,
				ElementBoundary.class, manager.getUserId().getDomain(), manager.getUserId().getEmail());

		// When i delete all Elements
		this.restTemplate.delete(this.url + DELETE_ALL_ELEMENTS_URL, this.admin.getUserId().getDomain(),
				this.admin.getUserId().getEmail());

		// getting elements again
		ElementBoundary[] result = this.restTemplate.getForObject(this.url + GET_ALL_ELEMENTS_OF_USER,
				ElementBoundary[].class, this.manager.getUserId().getDomain(), this.manager.getUserId().getEmail());

		// Then i get no Elements when i check it again
		assertThat(result).isEmpty();

	}
	
	
	@Test
	public void testCreateFiveElementsDeleteOneConfirmThatDatabaseHasFourElements() throws Exception {
		
		//GIVEN - Server contains 5 elements
		//WHEN - Delete specific is invoke to delete the first element
		//THEN - Server removes the 1st element from the database
		
		ElementBoundary dbContent[] = IntStream.range(0, 5) 
				.mapToObj(n -> n)
				.map(current -> 				
				new ElementBoundary (null,"DELETE","NAME", true,new Date(),	new CreatedByBoundary(this.manager.getUserId()),null,null))
				.map(boundary -> 
					this.restTemplate.postForObject(this.url + CREATE_ELEMENT, 
													boundary,
													ElementBoundary.class,
													manager.getUserId().getDomain(), manager.getUserId().getEmail()))
				.collect(Collectors.toList())
				.toArray(new ElementBoundary[0]);
		
		assertThat(this.restTemplate
				.getForObject(this.url + GET_ALL_ELEMENTS_OF_USER, 
						ElementBoundary[].class, 
						manager.getUserId().getDomain(), manager.getUserId().getEmail()))
			.hasSize(5);
		
		
		this.restTemplate.postForObject(this.url + CREATE_ACTION, 
				new ActionBoundary(new IdBoundary("ofir", null), "deleteSpecific",
						new ActionElementBoundary(new IdBoundary(
								dbContent[0].getElementId().getDomain(), dbContent[0].getElementId().getId())),
						new Date(),
						new InvokedByBoundary(new UserIdBoundary(admin.getUserId().getDomain(), admin.getUserId().getEmail())),
						null),
				ActionBoundary.class);
		
		assertThat(this.restTemplate
				.getForObject(this.url + GET_ALL_ELEMENTS_OF_USER, 
						ElementBoundary[].class, 
						manager.getUserId().getDomain(), manager.getUserId().getEmail()))
			.hasSize(5);
		
		assertThat(dbContent[0].getActive() == false);
	}
	
	@Test
	public void checkExportAllUsers() throws Exception {
		// Given the database contains an Actions
		// Create user

		UserNewDetails userInput = new UserNewDetails("tomer32@gmail.com", UserRole.ADMIN, "tomer test", ";[");
		UserBoundary user = this.restTemplate.postForObject(this.url + CREATE_USER, userInput, UserBoundary.class);

		// When i get all Users
		UserBoundary[] result = this.restTemplate.getForObject(this.url + GET_ALL_USERS_URL, UserBoundary[].class,
				this.admin.getUserId().getDomain(), this.admin.getUserId().getEmail());

		// Then i check if has same size as i create and check if they equals
		assertThat(result).hasSize(4);

		assertThat(result[3]).usingRecursiveComparison().isEqualTo(user);

	}

	@Test
	public void checkExportAllActions() throws Exception {
		// Given the database contains an Actions
		
		// create element
		ElementBoundary element = this.restTemplate.postForObject(this.url + CREATE_ELEMENT, 
				 new ElementBoundary(null, "INFO", "testName", true, new Date(), null, null, null),
				ElementBoundary.class, this.manager.getUserId().getDomain(), this.manager.getUserId().getEmail());
		
		// create Action
		ActionBoundary action = this.restTemplate.postForObject(this.url + CREATE_ACTION,
				new ActionBoundary(new IdBoundary("ofir", null), "update",
						new ActionElementBoundary(new IdBoundary(
								element.getElementId().getDomain(), element.getElementId().getId())),
						new Date(),
						new InvokedByBoundary(new UserIdBoundary(this.player.getUserId().getDomain(), this.player.getUserId().getEmail())),
						null), 
				ActionBoundary.class);

		// When i get all Actions

		ActionBoundary[] result = this.restTemplate.getForObject(this.url + GET_ALL_ACTIONS_URL, ActionBoundary[].class,
				this.admin.getUserId().getDomain(), this.admin.getUserId().getEmail());

		// Then i get list of the actions
		assertThat(result).hasSize(1);

		assertThat(result[0]).usingRecursiveComparison().isEqualTo(action);
	}

	@Test
	public void checkExportAllActionsWithPagination() throws Exception {
		// GIVEN the server is up
		// AND the database contains 20 actions
		
		//create element
		ElementBoundary element = this.restTemplate.postForObject(this.url + CREATE_ELEMENT, 
				 new ElementBoundary(null, "INFO", "testName", true, new Date(), null, null, null),
				ElementBoundary.class, this.manager.getUserId().getDomain(), this.manager.getUserId().getEmail());
		
		IntStream.range(0, 20).mapToObj(n -> "Object #" + n) // Stream<Strings> to Stream <Objects>
				.map(current -> // Initialize each object
				new ActionBoundary(new IdBoundary("ofir", null), "update",
						new ActionElementBoundary(new IdBoundary(
								element.getElementId().getDomain(), element.getElementId().getId())),
						new Date(),
						new InvokedByBoundary(new UserIdBoundary(this.player.getUserId().getDomain(), this.player.getUserId().getEmail())), null))
				.forEach(boundary -> // Invoke POST for each object
				this.restTemplate.postForObject("http://localhost:" + this.port + "/acs/actions", boundary,
						ActionBoundary.class));

		// WHEN GET samples/byMessagePattern/?size=6&page=3
		ActionBoundary[] actualResults = this.restTemplate.getForObject(
				this.url + GET_ALL_ACTIONS_URL + "?size={size}&page={page}", ActionBoundary[].class,
				admin.getUserId().getDomain(), admin.getUserId().getEmail(), 6, 3);

		// THEN the result contains 2 results
		assertThat(actualResults).hasSize(2);
	}
	
	
	@Test
	public void checkExportAllActionsWithPaginationResultZero() throws Exception {
		// GIVEN the server is up
		// AND the database contains 20 actions
		
		//create element
		ElementBoundary element = this.restTemplate.postForObject(this.url + CREATE_ELEMENT, 
				 new ElementBoundary(null, "INFO", "testName", true, new Date(), null, null, null),
				ElementBoundary.class, this.manager.getUserId().getDomain(), this.manager.getUserId().getEmail());
		
		IntStream.range(0, 20).mapToObj(n -> "Object #" + n) // Stream<Strings> to Stream <Objects>
				.map(current -> // Initialize each object
				new ActionBoundary(new IdBoundary("ofir", null), "update",
						new ActionElementBoundary(new IdBoundary(
								element.getElementId().getDomain(), element.getElementId().getId())),
						new Date(),
						new InvokedByBoundary(new UserIdBoundary(this.player.getUserId().getDomain(), this.player.getUserId().getEmail())), null))
				.forEach(boundary -> // Invoke POST for each object
				this.restTemplate.postForObject("http://localhost:" + this.port + "/acs/actions", boundary,
						ActionBoundary.class));

		// WHEN GET samples/byMessagePattern/?size=6&page=3
		ActionBoundary[] actualResults = this.restTemplate.getForObject(
				this.url + GET_ALL_ACTIONS_URL + "?size={size}&page={page}", ActionBoundary[].class,
				this.admin.getUserId().getDomain(), this.admin.getUserId().getEmail(), 10, 2);

		// THEN the result contains 2 results
		assertThat(actualResults).hasSize(0);
	}

	@Test
	public void checkExportAllActionsWithPaginationCreate2ActionAndInPage3Results() throws Exception {
		// GIVEN the server is up
		// AND the database contains 20 actions
		
		//create element
		ElementBoundary element = this.restTemplate.postForObject(this.url + CREATE_ELEMENT, 
				 new ElementBoundary(null, "INFO", "testName", true, new Date(), null, null, null),
				ElementBoundary.class, this.manager.getUserId().getDomain(), this.manager.getUserId().getEmail());
		
		IntStream.range(0, 2).mapToObj(n -> "Object #" + n) // Stream<Strings> to Stream <Objects>
				.map(current -> // Initialize each object
				new ActionBoundary(new IdBoundary("ofir", null), "update",
						new ActionElementBoundary(new IdBoundary(
								element.getElementId().getDomain(), element.getElementId().getId())),
						new Date(),
						new InvokedByBoundary(new UserIdBoundary(player.getUserId().getDomain(), player.getUserId().getEmail())), null))
				.forEach(boundary -> // Invoke POST for each object
				this.restTemplate.postForObject("http://localhost:" + this.port + "/acs/actions", boundary,
						ActionBoundary.class));

		// WHEN GET samples/byMessagePattern/?size=6&page=3
		ActionBoundary[] actualResults = this.restTemplate.getForObject(
				this.url + GET_ALL_ACTIONS_URL + "?size={size}&page={page}", ActionBoundary[].class,
				this.admin.getUserId().getDomain(), this.admin.getUserId().getEmail(), 3, 0);

		// THEN the result contains 2 results
		assertThat(actualResults).hasSize(2);
	}


}
