package acs;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ActionTests {

	private int port;
	private RestTemplate restTemplate;
	private String url;

	private final static String DELETE_ALL_ELEMENTS_URL = "admin/elements/{adminDomain}/{adminEmail}";
	private final static String DELETE_ALL_ACTIONS_URL = "admin/actions/{adminDomain}/{adminEmail}";
	private final static String CREATE_USER = "users";
	private final static String CREATE_ACTION = "actions";
	private final static String CREATE_ELEMENT = "elements/{managerDomain}/{managerEmail}";
	private final static String DELETE_ALL_URL_USERS =  "admin/users/{adminDomain}/{AdminEmail}";

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

		this.player = this.restTemplate.postForObject(this.url + CREATE_USER,
				new UserNewDetails("p@gmail.com", UserRole.PLAYER, "player", ":_"), UserBoundary.class);

		this.manager = this.restTemplate.postForObject(this.url + CREATE_USER,
				new UserNewDetails("m@gmail.com", UserRole.MANAGER, "manager", ":/"), UserBoundary.class);

		this.admin = this.restTemplate.postForObject(this.url + CREATE_USER,
				new UserNewDetails("a@gmail.com", UserRole.ADMIN, "admin", ":*"), UserBoundary.class);

	}

//	@BeforeEach
//	public void setup() {
//		// do nothing
//	}

	@AfterEach
	public void teardown() {
		this.restTemplate.delete(this.url + DELETE_ALL_ELEMENTS_URL, this.admin.getUserId().getDomain(),
				this.admin.getUserId().getEmail());

		this.restTemplate.delete(this.url + DELETE_ALL_ACTIONS_URL, this.admin.getUserId().getDomain(),
				this.admin.getUserId().getEmail());
		
		this.restTemplate.delete(this.url + DELETE_ALL_URL_USERS, this.admin.getUserId().getDomain(),
				this.admin.getUserId().getEmail());


	}

	@Test
	public void testContext() {
	}

	@Test
	public void testPostNewActionReturnActionWithId() throws Exception {
		// GIVEN server is up

		// WHEN I POST /acs/actions AND send action boundary

		// WHEN I POST /users AND send a user boundary
		ElementBoundary input = this.restTemplate.postForObject(this.url + CREATE_ELEMENT,
				new ElementBoundary(null, "INFO", "testName", true, new Date(),
						new CreatedByBoundary(manager.getUserId()), null, null),
				ElementBoundary.class, manager.getUserId().getDomain(), manager.getUserId().getEmail());

		// Given the database contains an Actions
		ActionBoundary actionInput = new ActionBoundary(new IdBoundary("domain test", "testId"), "update",
				new ActionElementBoundary(
						new IdBoundary(input.getElementId().getDomain(), input.getElementId().getId())),
				new Date(),
//				null, new Date(),
				new InvokedByBoundary(
						new UserIdBoundary(this.player.getUserId().getDomain(), this.player.getUserId().getEmail())),
				new HashMap<>());

		ActionBoundary output2 = this.restTemplate.postForObject(this.url + CREATE_ACTION, actionInput,
				ActionBoundary.class, this.admin.getUserId().getDomain(), this.admin.getUserId().getEmail());

		// THEN the server returns status 2xx
		// AND retrieves a massage with non null id

		if (output2.getActionId().getId() == null) {
			throw new Exception("expected non null id but id was null");
		}
	}

	@Test
	public void testPostNewActionReturnActionWithElemetIdNotNull() throws Exception {
		// GIVEN server is up

		// WHEN I POST /users AND send a user boundary
		ElementBoundary input = this.restTemplate.postForObject(this.url + CREATE_ELEMENT,
				new ElementBoundary(null, "INFO5", "testName", true, new Date(),
						new CreatedByBoundary(manager.getUserId()), null, null),
				ElementBoundary.class, manager.getUserId().getDomain(), manager.getUserId().getEmail());

		// Given the database contains an Actions
		ActionBoundary actionInput = new ActionBoundary(new IdBoundary("domain test", "testId"), "update",
				new ActionElementBoundary(
						new IdBoundary(input.getElementId().getDomain(), input.getElementId().getId())),
				new Date(),
//						null, new Date(),
				new InvokedByBoundary(
						new UserIdBoundary(this.player.getUserId().getDomain(), this.player.getUserId().getEmail())),
				new HashMap<>());

		ActionBoundary output2 = this.restTemplate.postForObject(this.url + CREATE_ACTION, actionInput,
				ActionBoundary.class, this.admin.getUserId().getDomain(), this.admin.getUserId().getEmail());

		if (output2.getElement().getElementId() == null) {
			throw new Exception("expected non null id for elemet but id was null");
		}

	}

	@Test
	public void testPostNewActionReturnActionWithSameType() throws Exception {
		// GIVEN server is up

		// WHEN I POST /samples AND send a Action boundary

		// WHEN I POST /users AND send a user boundary
		ElementBoundary input = this.restTemplate.postForObject(this.url + CREATE_ELEMENT,
				new ElementBoundary(null, "INFO4", "testName", true, new Date(),
						new CreatedByBoundary(manager.getUserId()), null, null),
				ElementBoundary.class, manager.getUserId().getDomain(), manager.getUserId().getEmail());

		// Given the database contains an Actions
		ActionBoundary actionInput = new ActionBoundary(new IdBoundary("domain test", "testId"), "update",
				new ActionElementBoundary(
						new IdBoundary(input.getElementId().getDomain(), input.getElementId().getId())),
				new Date(),
//						null, new Date(),
				new InvokedByBoundary(
						new UserIdBoundary(this.player.getUserId().getDomain(), this.player.getUserId().getEmail())),
				new HashMap<>());

		ActionBoundary output2 = this.restTemplate.postForObject(this.url + CREATE_ACTION, actionInput,
				ActionBoundary.class, this.admin.getUserId().getDomain(), this.admin.getUserId().getEmail());

//		 THEN the server returns status 2xx
//		 AND retrieves a action with same Type as sent to server
		if (!output2.getType().equals(actionInput.getType())) {
			throw new Exception("expected update type to input but received: " + output2.getType());
		}

	}

	@Test
	public void testPostNewActionReturnActionWithUpdaedDate() throws Exception {
		// GIVEN server is up

		// WHEN I POST /samples AND send a Action boundary

		ElementBoundary input = this.restTemplate.postForObject(this.url + CREATE_ELEMENT,
				new ElementBoundary(null, "INFO2", "testName", true, new Date(),
						new CreatedByBoundary(manager.getUserId()), null, null),
				ElementBoundary.class, manager.getUserId().getDomain(), manager.getUserId().getEmail());

		// Given the database contains an Actions
		ActionBoundary actionInput = new ActionBoundary(new IdBoundary("domain test", "testId"), "update",
				new ActionElementBoundary(
						new IdBoundary(input.getElementId().getDomain(), input.getElementId().getId())),
				new Date(),
//				null, new Date(),
				new InvokedByBoundary(
						new UserIdBoundary(this.player.getUserId().getDomain(), this.player.getUserId().getEmail())),
				new HashMap<>());

		ActionBoundary output2 = this.restTemplate.postForObject(this.url + CREATE_ACTION, actionInput,
				ActionBoundary.class, this.admin.getUserId().getDomain(), this.admin.getUserId().getEmail());

		// THEN the server returns status 2xx
		// AND action a Type with different date as sent to server
		if (output2.getCreatedTimestamp().equals(actionInput.getCreatedTimestamp())) {
			throw new Exception("expected update type to input but received: " + output2.getCreatedTimestamp());
		}
	}

	@Test
	public void testInvoke5ActionsCheckThatTheyWereAddedToDatabase() throws Exception {
		// GIVEN - Database contains 5 Actions
		// WHEN - Invoked GET to all A
		// THEN - Confirm database size is 5

		// create element
		ElementBoundary element = this.restTemplate.postForObject(this.url + CREATE_ELEMENT,
				new ElementBoundary(null, "INFO3", "testName", true, new Date(), null, null, null),
				ElementBoundary.class, manager.getUserId().getDomain(), manager.getUserId().getEmail());

		List<ActionBoundary> dbContent = IntStream.range(0, 5).mapToObj(n -> "Object #" + n) // Stream<Strings> to
				.map(current -> // Initialize each object
				new ActionBoundary(new IdBoundary("ofir", null), "update",
						new ActionElementBoundary(
								new IdBoundary(element.getElementId().getDomain(), element.getElementId().getId())),
						new Date(),
						new InvokedByBoundary(
								new UserIdBoundary(player.getUserId().getDomain(), player.getUserId().getEmail())),
						null))
				.map(boundary -> // Invoke POST for each object
				this.restTemplate.postForObject("http://localhost:" + this.port + "/acs/actions", boundary,
						ActionBoundary.class,"1","2")).collect(Collectors.toList());

		// Confirm database size == 5
		assertEquals(dbContent.size(), 5);
	}
}
