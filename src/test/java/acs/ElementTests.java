package acs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;
import acs.data.utils.UserRole;
import acs.rest.action.ActionBoundary;
import acs.rest.action.boundaries.InvokedByBoundary;
import acs.rest.element.boundaries.CreatedByBoundary;
import acs.rest.element.boundaries.ElementBoundary;
import acs.rest.element.boundaries.LocationBoundary;
import acs.rest.users.UserBoundary;
import acs.rest.users.UserNewDetails;
import acs.rest.utils.IdBoundary;
import acs.rest.utils.UserIdBoundary;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ElementTests {
	
	private int port;
	private RestTemplate restTemplate;
	private String url;
	
	private final static String GET_URL = "elements/{userDomain}/{userEmail}/";
	private final static String GET_URL_SPECIFIC = "elements/{userDomain}/{userEmail}/{elementDomain}/{elementId}";
	private final static String POST_URL = "elements/{managerDomain}/{managerEmail}/";
	private final static String UPDATE_URL = "elements/{managerDomain}/{managerEmail}/{elementDomain}/{elementId}";
	
	private final static String DELETE_ALL_URL_ELEMENTS =  "admin/elements/{adminDomain}/{adminEmail}";
	private final static String DELETE_ALL_URL_USERS =  "admin/users/{adminDomain}/{AdminEmail}";
	
	private UserBoundary player;
	private UserBoundary manager;
	private UserBoundary admin;
	
	private CreatedByBoundary createdByPlayer;
	private CreatedByBoundary createdByManager;
	private CreatedByBoundary createdByAdmin;
	
	@PostConstruct
	public void init() {
		this.url = "http://localhost:" + this.port + "/acs/";
		this.restTemplate = new RestTemplate();
		
		this.player = this.restTemplate.postForObject(this.url + "/users",  
					 new UserNewDetails("p@gmail.com", UserRole.PLAYER, "player", ":_"),
		  			UserBoundary.class);
		
		
		this.manager = this.restTemplate.postForObject(this.url + "/users",  
				new UserNewDetails("m@gmail.com", UserRole.MANAGER, "manager", ":/"),
		  			UserBoundary.class);
		
		this.admin =  this.restTemplate.postForObject(this.url + "/users",  
				new UserNewDetails("a@gmail.com",  UserRole.ADMIN, "admin", ":*"),
	  			UserBoundary.class);
		
		this.createdByPlayer = new CreatedByBoundary(this.player.getUserId());
		this.createdByManager = new CreatedByBoundary(this.manager.getUserId());
		this.createdByAdmin = new CreatedByBoundary(this.admin.getUserId());
		
		
	}
	
	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}
	
	@AfterEach
	public void teardown() {
		this.restTemplate.delete(this.url + DELETE_ALL_URL_ELEMENTS ,this.admin.getUserId().getDomain() , this.admin.getUserId().getEmail());
		this.restTemplate.delete(this.url + DELETE_ALL_URL_USERS ,this.admin.getUserId().getDomain() , this.admin.getUserId().getEmail());
	}
	
	
    public String elementIdToURL(IdBoundary eib) {
		return eib.getDomain() + "/" + eib.getId();
	}
    
    @Test
	public void testContext() {	
	}
	
	
	@Test
	public void testPostNewElementThenCheckIfCreatedElementHasSameName() throws Exception {
		// GIVEN - Server is up
		// WHEN -  POST /acs/elements to create new ElementBoundary with specific name
		// THEN - Server creates new ElementBoundary with given name and gives 2xx message
		
		
		ElementBoundary input = new ElementBoundary(null,
													"INFO", 
													"testName", 
													true,
													new Date(), 
													createdByManager, 
													null ,
													null);
		
		ElementBoundary output = this.restTemplate.postForObject(this.url + POST_URL , 
																input,
																ElementBoundary.class,
																createdByManager.getUserId().getDomain(), createdByManager.getUserId().getEmail());
		
		assertEquals(output.getName(), input.getName());
	}
	
	@Test
	public void testAttemptToCreateNewElementAsUserAndAdminExpectForExceptionsForBoth() throws Exception {

		assertThrows(RuntimeException.class, ()->
		this.restTemplate.postForObject(this.url + POST_URL, 
										new ElementBoundary(null,"INFO","testName",	true,new Date(),createdByPlayer,null ,null),
										ElementBoundary.class,
										player.getUserId().getDomain(), player.getUserId().getEmail()));
		
		assertThrows(RuntimeException.class, ()->
		this.restTemplate.postForObject(this.url + POST_URL, 
										new ElementBoundary(null,"INFO","testName",	true,new Date(),createdByAdmin,null ,null),
										ElementBoundary.class,
										admin.getUserId().getDomain(), admin.getUserId().getEmail()));
	}

	@Test
	public void testGetSpecificElementWithSpecificAttributesInDatabaseAndValidateObjectReturnedByDatabase() throws Exception{
		
		// GIVEN - server is up and contains a single Element
		// WHEN - invoke GET /acs/elements/{userDomain}/{userEmail}/{elementDomain}/{elementId}
		// THEN - server verifies that the element given has same attributes as element which was created,
		//			return 2xx afterwards
		
		Map <String , Object> elementAttributes = new HashMap<String, Object>();
		elementAttributes.put("value1", 1);
		elementAttributes.put("value2", 2.0);
		elementAttributes.put("value3", "Three");
		elementAttributes.put("value4", Collections.singletonMap("Four", "IV"));
		
		
		ElementBoundary newElementObject = 
				  this.restTemplate
					.postForObject(this.url + POST_URL, 
							new ElementBoundary(null,
									"INFO", 
									"Name", 
									true,
									new Date(), 
									createdByManager, 
									null,
									elementAttributes),
							ElementBoundary.class,
							manager.getUserId().getDomain(), manager.getUserId().getEmail());
		
		
		ElementBoundary resultElementObject = this.restTemplate.getForObject(this.url + GET_URL + elementIdToURL(newElementObject.getElementId()),
																ElementBoundary.class,
																manager.getUserId().getDomain(), manager.getUserId().getEmail());
		
		assertThat(resultElementObject.getElementAttributes().equals(newElementObject.getElementAttributes()));
	}
	
	@Test
	public void testPlayerAndAdminAttempsToCreateNewElementCheckForForbiddenAccessException() throws Exception {
		
		//GIVEN - server is up  
		//WHEN - admin and player attempt to create new elemnt
		//THEN  - server returns an exception
		
		assertThrows(RuntimeException.class, ()->
		this.restTemplate.postForObject(this.url + POST_URL, 
										new ElementBoundary(null,"INFO","testName",	true,new Date(),createdByPlayer,null ,null),
										ElementBoundary.class,
										player.getUserId().getDomain(), player.getUserId().getEmail()));
		
		assertThrows(RuntimeException.class, ()->
		this.restTemplate.postForObject(this.url + POST_URL, 
										new ElementBoundary(null,"INFO","testName",	true,new Date(),createdByAdmin,null ,null),
										ElementBoundary.class,
										admin.getUserId().getDomain(), admin.getUserId().getEmail()));
		
	}
	
	@Test	
	public void testCreateElementWithFalseActiveThenCheckForExceptionWhenPlayerAttemptsToGetIt() throws Exception {
		// GIVEN - server is up and contains an element with active=false
		//WHEN - player attempts to get that element
		//THEN - server returns an exception
		
		ElementBoundary element = this.restTemplate.postForObject(this.url + POST_URL, 
										new ElementBoundary(null,"INFO","testName",	false , new Date(),createdByPlayer,null ,null),				
										ElementBoundary.class, 
										manager.getUserId().getDomain(), manager.getUserId().getEmail());
		
		assertThrows(RuntimeException.class, ()->
		this.restTemplate.getForObject(this.url + GET_URL_SPECIFIC , 
										ElementBoundary.class,
										admin.getUserId().getDomain(), admin.getUserId().getEmail(), element.getElementId().getDomain(), element.getElementId().getId()) );
		
	}
	
	@Test
	public void testCreate5ElementsCheckThatTheyWereAddedToDatabaseThenDeleteDatabaseAndCheckThatItIsEmptyAndNotNull() throws Exception{
		// GIVEN - Database contains 5 Elements
		// WHEN - Invoked GET to all Elements
		// THEN - Confirm database size is 5,  delete the entire database , confirm it is not null , is empty and return 2xx message from server
		
		// Create 5 Elements for database
		List <ElementBoundary> dbContent = IntStream.range(0, 5) //Stream <Integer> with size of 5 (0,1,2,3,4)
				.mapToObj(n -> "Object #" + n) // Stream<Strings> to Stream <Objects>
				.map(current -> 				// Initialize each object 
				new ElementBoundary (null,
									"INFO", 
									"Name", 
									 true,
									new Date(), 
									createdByManager, 
									null,
									null))
				.map(boundary -> //Invoke POST for each object
					this.restTemplate.postForObject(this.url + POST_URL, 
													boundary,
													ElementBoundary.class,
													manager.getUserId().getDomain(), manager.getUserId().getEmail()))
				.collect(Collectors.toList());
		
		// Confirm database size == 5
		assertEquals(dbContent.size(), 5);
		
		// Delete all elements from database
		this.restTemplate.delete(this.url + DELETE_ALL_URL_ELEMENTS , this.admin.getUserId().getDomain() , this.admin.getUserId().getEmail());
		
		// Retrieve all elements from database
		ElementBoundary result[] = this.restTemplate.getForObject(this.url + GET_URL,
																  ElementBoundary[].class, 
																  manager.getUserId().getDomain(), manager.getUserId().getEmail());
		
		// Confirm that the database is empty yet is not null
		assertThat(result).isNotNull().isEmpty();
	}
	
	@Test
	public void testCreateNewElementAttemptToChangeItsIdAndVerifyThatTheDatabaseHadNotChangedTheId () throws Exception {
		// GIVEN - Database contains an element 
		// WHEN - I change elementAttributes with UPDATE method
		// THEN - Check that the new id has not been updated in the database and server returns 2xx message
		
		// Creating new ElementBoundary and adding it to the database through POST
		ElementBoundary element = this.restTemplate.postForObject(this.url + POST_URL,
																new ElementBoundary(null,
																					"INFO", 
																					"NameTest4", 
																					false,
																					new Date(), 
																					createdByManager, 
																					null,
																					null
																					),
																ElementBoundary.class,
																manager.getUserId().getDomain(), manager.getUserId().getEmail());
		
		// Creating the new elementId to update
		IdBoundary newElementId = new IdBoundary("testDomain", "testId");
		
		IdBoundary orgElementId = element.getElementId();
		element.setElementId(newElementId);
			
		//Invoke the UPDATE method
		this.restTemplate.put(this.url + UPDATE_URL,
							  element,
							  manager.getUserId().getDomain(), manager.getUserId().getEmail(),  orgElementId.getDomain(),orgElementId.getId());
		
		
		// Retrieve the entire database content
		ElementBoundary database[] = this.restTemplate.getForObject(this.url + GET_URL, 
																	ElementBoundary[].class, 
																	manager.getUserId().getDomain(), manager.getUserId().getEmail());
		
		// Check that the databases' old key was kept 
		assertThat(database[0].getElementId()).isNotEqualTo(element.getElementId());
		
	}
	
	@Test
	public void testCreateNewElementThenUpdateItWithNullNameExpectAndCheckForRuntimeException() throws Exception{
		//GIVEN - An element in the database
		//WHEN - Attempt to set the elements name to 'null'
		//THEN - Server receives an exception and return 2xx message
		ElementBoundary element = this.restTemplate.postForObject(this.url + POST_URL,
																	new ElementBoundary(null,
																						"INFO", 
																						"NameTest4", 
																						false,
																						new Date(), 
																						createdByManager, 
																						null,
																						null
																						),
																	ElementBoundary.class,
																	manager.getUserId().getDomain(), manager.getUserId().getEmail());
		String newName = null;
		element.setName(newName);
		try {
			this.restTemplate.put(this.url + UPDATE_URL + elementIdToURL(element.getElementId()),
				  element ,
				  newName);
			fail("Expected Invalid Name Exception");
		}
		catch (RuntimeException e) {
		}
	}
	
	@Test
	public void testManagerCreatesNewElementAdminAndPlayerAttemptToUpdateItCheckForReturnedException() throws Exception {
		
		//GIVEN - server is up and contains an element
		// WHEN - player or admin attemps to update that element
		// THEN - server throws an exception
		
		ElementBoundary element = this.restTemplate.postForObject(this.url + POST_URL,
				new ElementBoundary(null,
									"INFO", 
									"NameTest4", 
									false,
									new Date(), 
									createdByManager, 
									null,
									null
									),
				ElementBoundary.class,
				manager.getUserId().getDomain(), manager.getUserId().getEmail());
		
		String newName = "new_name";
		
		assertThrows(RuntimeException.class, ()->
		this.restTemplate.put(this.url + UPDATE_URL , 
										ElementBoundary.class,
										newName,
										admin.getUserId().getDomain(), admin.getUserId().getEmail(), element.getElementId().getDomain(), element.getElementId().getId()) );
		
		assertThrows(RuntimeException.class, ()->
		this.restTemplate.put(this.url + UPDATE_URL , 
										ElementBoundary.class,
										newName,
										player.getUserId().getDomain(), player.getUserId().getEmail(), element.getElementId().getDomain(), element.getElementId().getId()) );
	}
	
	@Test
	public void testCreateNewElementThenUpdateItWithEmptyNameWith5SpacesExpectAndCheckForRuntimeException() throws Exception{
		//GIVEN - An element in the database
		//WHEN - Attempt to set the elements name to a name with 5 spaces "    "
		//THEN - Server receives an exception and return 2xx message
		
		ElementBoundary element = this.restTemplate.postForObject(this.url + POST_URL,
																	new ElementBoundary(null,
																						"INFO", 
																						"NameTest4", 
																						false,
																						new Date(), 
																						createdByManager, 
																						null,
																						null
																						),
																	ElementBoundary.class,
																	manager.getUserId().getDomain(), manager.getUserId().getEmail());
		String newName = "     ";
		element.setName(newName);
		try {
			this.restTemplate.put(this.url + UPDATE_URL + elementIdToURL(element.getElementId()),
				  element ,
				  newName);
			fail("Expected Invalid Name Exception");
		}
		catch (RuntimeException e) {
		}
	}
	
	@Test
	public void testCreateThreeActiveElementsAnotherTwoWithFalseCheckThatManagerGetAllReturnsFiveElementsPlayerWithThree() throws Exception {
		
		//GIVEN - Server with 5 elements - 3 with active=true, 2 with active=false
		//WHEN - manager and player invoke get all method
		//THEN - verify that manager has 5 elements and player has 3
		
		List <ElementBoundary> activeElements = IntStream.range(0, 5) //Stream <Integer> with size of 5 (0,1,2,3,4)
		.mapToObj(n -> n) // Stream<Strings> to Stream <Objects>
		.map(current -> 				// Initialize each object 
		new ElementBoundary (null,
							"JustAType", 
							"Name #" + current, 
							(current % 2 == 0),
							new Date(), 
							createdByManager, 
							null,
							null))
		.map(boundary -> //Invoke POST for each object
			this.restTemplate.postForObject(this.url + POST_URL, 
											boundary,
											ElementBoundary.class,
											manager.getUserId().getDomain(), manager.getUserId().getEmail()))
		.collect(Collectors.toList());

		
				assertThat(this.restTemplate.getForObject(this.url + GET_URL,
				ElementBoundary[].class, 
				manager.getUserId().getDomain(), manager.getUserId().getEmail())).hasSize(5);
		
		assertThat(this.restTemplate.getForObject(this.url + GET_URL,
				ElementBoundary[].class, 
				player.getUserId().getDomain(), player.getUserId().getEmail())).hasSize(3);
	}
	
	@Test
	public void testCreateNewElementThenUpdateItWithNullTypeExpectAndCheckForRuntimeException() throws Exception{
		//GIVEN - An element in the database
		//WHEN - Attempt to set the elements' type to 'null'
		//THEN - Server receives an exception and return 2xx message
		ElementBoundary element = this.restTemplate.postForObject(this.url + POST_URL,
																	new ElementBoundary(null,
																						"INFO", 
																						"NameTest4", 
																						false,
																						new Date(), 
																						createdByManager, 
																						null,
																						null
																						),
																	ElementBoundary.class,
																	manager.getUserId().getDomain(), manager.getUserId().getEmail());
		String newType = null;
		element.setType(newType);
		try {
			this.restTemplate.put(this.url + UPDATE_URL + elementIdToURL(element.getElementId()),
				  element ,
				  newType);
			fail("Expected Invalid Type Exception");
		}
		catch (RuntimeException e) {
		}
	}
	
	@Test
	public void testCreateNewElementThenUpdateItWithTypeThatHas3SpacestAndCheckForRuntimeException() throws Exception{
		//GIVEN - An element in the database
		//WHEN - Attempt to set the elements type to "   "
		//THEN - Server receives an exception and return 2xx message
		ElementBoundary element = this.restTemplate.postForObject(this.url + POST_URL,
																	new ElementBoundary(null,
																						"INFO", 
																						"NameTest4", 
																						false,
																						new Date(), 
																						createdByManager, 
																						null,
																						null
																						),
																	ElementBoundary.class,
																	manager.getUserId().getDomain(), manager.getUserId().getEmail());
		String newType = "   ";
		element.setName(newType);
		try {
			this.restTemplate.put(this.url + UPDATE_URL + elementIdToURL(element.getElementId()),
				  element ,
				  newType);
			fail("Expected Invalid Type Exception");
		}
		catch (RuntimeException e) {
		}
	}
	
	
	@Test
	public void testBindTwoChildrenToParentConfirmAfterwards() throws Exception {
			
		//GIVEN - Database has 3 elements - 1 parent and 2 children
		//WHEN - Bind 2 children elements to parent
		//THEN - Confirm parent has 2 children and validate attributes, server gives 2xx message afterwards
		
		ElementBoundary parent = this.restTemplate.postForObject(this.url + POST_URL,
																new ElementBoundary(null,
																					"INFO", 
																					"Parent", 
																					true,
																					new Date(), 
																					createdByManager, 
																					null,
																					null
																					),
																ElementBoundary.class,
																manager.getUserId().getDomain(), manager.getUserId().getEmail());
		
		ElementBoundary child1 = this.restTemplate.postForObject(this.url + POST_URL,
																new ElementBoundary(null,
																					"INFO", 
																					"Child #1", 
																					true,
																					new Date(), 
																					null, 
																					null,
																					null
																					),
																ElementBoundary.class,
																manager.getUserId().getDomain(), manager.getUserId().getEmail());
		
		ElementBoundary child2 = this.restTemplate.postForObject(this.url + POST_URL,
																new ElementBoundary(null,
																					"INFO", 
																					"Child #2", 
																					true,
																					new Date(), 
																					createdByManager, 
																					null,
																					null
																					),
																ElementBoundary.class,
																manager.getUserId().getDomain(), manager.getUserId().getEmail());
		Stream.of(child1, child2)
		.map(ElementBoundary::getElementId)
		.forEach(childIdBoundary->
			this.restTemplate.put(this.url + POST_URL + "/{elementDomain}/{elementId}/children", 
					childIdBoundary,
					manager.getUserId().getDomain(), manager.getUserId().getEmail(), parent.getElementId().getDomain(), parent.getElementId().getId()));
		
		assertThat(this.restTemplate
				.getForObject(this.url + GET_URL + "/{elementDomain}/{elementId}/children", 
						ElementBoundary[].class, 
						manager.getUserId().getDomain(), manager.getUserId().getEmail(), parent.getElementId().getDomain(), parent.getElementId().getId()))
			.hasSize(2)
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactlyInAnyOrder(child1, child2);
	}
	
	
	@Test
	public void testCreateAnElementAndAttemptToBindItToItself() throws Exception {
		//GIVEN - Database contains a single element
		//WHEN - Invoke binding child to parent method with the same element
		//THEN - Expect to receive an exception
		ElementBoundary element = this.restTemplate.postForObject(this.url + POST_URL,
				new ElementBoundary(null,
									"INFO", 
									"BindingElement", 
									true,
									new Date(), 
									createdByManager, 
									null,
									null
									),
				ElementBoundary.class,
				manager.getUserId().getDomain(), manager.getUserId().getEmail());
		
		String domain = element.getElementId().getDomain();
		String id = element.getElementId().getId();
			
		assertThrows(RuntimeException.class, ()->
		this.restTemplate.put(this.url + UPDATE_URL + "/children", 
				  new IdBoundary(domain, id), 
				  manager.getUserId().getDomain(), manager.getUserId().getEmail(), domain, id));
	}
	
	@Test
	public void testBindParentToChildCheckThatChildHasAParentAndValidateItstheInitialParent() throws Exception {
		//GIVEN - Database contains a parent and child element
		//WHEN - Child is bound to parent
		//THEN - Get all parents of the child , confirm it has the same Id of the initial parent element
		
		ElementBoundary parent = this.restTemplate.postForObject(this.url + POST_URL,
																new ElementBoundary(null,
																					"parentType", 
																					"Parent", 
																					true,
																					new Date(), 
																					createdByManager, 
																					null,
																					null
																					),
																ElementBoundary.class,
																manager.getUserId().getDomain(), manager.getUserId().getEmail());

		ElementBoundary child = this.restTemplate.postForObject(this.url + POST_URL,
																new ElementBoundary(null,
																					"childType", 
																					"Child", 
																					true,
																					new Date(), 
																					createdByManager, 
																					null,
																					null
																					),
																ElementBoundary.class,
																manager.getUserId().getDomain(), manager.getUserId().getEmail());
	
		this.restTemplate.put(this.url + POST_URL + "/{elementDomain}/{elementId}/children", 
				child.getElementId(),
				manager.getUserId().getDomain(), manager.getUserId().getEmail(), parent.getElementId().getDomain(), parent.getElementId().getId());
		
		ElementBoundary allParents[] = this.restTemplate.getForObject(this.url + GET_URL + "/{elementDomain}/{elementId}/parents", 
				ElementBoundary[].class, 
				manager.getUserId().getDomain(), manager.getUserId().getEmail(), child.getElementId().getDomain(), child.getElementId().getId());
		
		assertThat(allParents[0].getElementId().getId()).isEqualTo(parent.getElementId().getId());
		
	}
	
	@Test
	public void testCreateTwoElementsPlayerAndAdminAttemptToInvokeBindMethodConfirmReturnedException() throws Exception {
		
		//GIVEN - Server with child element and parent element
		// WHEN - admin or player attempt to bind child to parent
		// THEN - Server throws an exception
		
		ElementBoundary parent = this.restTemplate.postForObject(this.url + POST_URL,
				new ElementBoundary(null, "parentType",	"Parent",	true,	new Date(),	createdByManager,null,	null),
				ElementBoundary.class,
				manager.getUserId().getDomain(), manager.getUserId().getEmail());

		ElementBoundary child = this.restTemplate.postForObject(this.url + POST_URL,
						new ElementBoundary(null,"childType","Child",true,	new Date(),createdByManager,null,null),
						ElementBoundary.class,
						manager.getUserId().getDomain(), manager.getUserId().getEmail());
		
		assertThrows(RuntimeException.class, ()-> 
		this.restTemplate.put(this.url + POST_URL + "/{elementDomain}/{elementId}/children", 
			child.getElementId(),
			player.getUserId().getDomain(), player.getUserId().getEmail(), 
				parent.getElementId().getDomain(), parent.getElementId().getId()));
		
		assertThrows(RuntimeException.class, ()-> 
		this.restTemplate.put(this.url + POST_URL + "/{elementDomain}/{elementId}/children", 
			child.getElementId(),
			admin.getUserId().getDomain(), admin.getUserId().getEmail(), 
				parent.getElementId().getDomain(), parent.getElementId().getId()));
	}
	
	@Test
	public void testBindChildToFirstParentThenBindToSecondParentCheckThatFirstParentLostChildAndSecondParentHasIt() throws Exception {
		// GIVEN - Database contains 2 parent elements , 1 child element
		// WHEN - bind child to first parent, then to second parent
		// THEN - Confirm that only the second parent has a child 
		ElementBoundary parent1 = this.restTemplate.postForObject(this.url + POST_URL,
																new ElementBoundary(null,
																					"parentType", 
																					"Parent #1", 
																					true,
																					new Date(), 
																					createdByManager, 
																					null,
																					null
																					),
																ElementBoundary.class,
																manager.getUserId().getDomain(), manager.getUserId().getEmail());

		ElementBoundary parent2 = this.restTemplate.postForObject(this.url + POST_URL,
																new ElementBoundary(null,
																					"parentType", 
																					"Parent #2", 
																					true,
																					new Date(), 
																					createdByManager, 
																					null,
																					null
																					),
																ElementBoundary.class,
																manager.getUserId().getDomain(), manager.getUserId().getEmail());
		
		ElementBoundary child = this.restTemplate.postForObject(this.url + POST_URL,
																new ElementBoundary(null,
																					"childType", 
																					"Child", 
																					true,
																					new Date(), 
																					createdByManager, 
																					null,
																					null
																					),
																ElementBoundary.class,
																manager.getUserId().getDomain(), manager.getUserId().getEmail());
		
		Stream.of(parent1, parent2)
		.map(ElementBoundary::getElementId)
		.forEach(parentIdBoundary->
			this.restTemplate.put(this.url + POST_URL + "/{elementDomain}/{elementId}/children", 
					child.getElementId(),
					manager.getUserId().getDomain(), manager.getUserId().getEmail(), parentIdBoundary.getDomain(), parentIdBoundary.getId()));
		
		assertThat(this.restTemplate
				.getForObject(this.url + GET_URL + "/{elementDomain}/{elementId}/children", 
						ElementBoundary[].class, 
						manager.getUserId().getDomain(), manager.getUserId().getEmail(), parent1.getElementId().getDomain(), parent1.getElementId().getId()))
			.hasSize(0);
		
		assertThat(this.restTemplate
				.getForObject(this.url + GET_URL + "/{elementDomain}/{elementId}/children", 
						ElementBoundary[].class, 
						manager.getUserId().getDomain(), manager.getUserId().getEmail(), parent2.getElementId().getDomain(), parent2.getElementId().getId()))
			.hasSize(1);
		
		ElementBoundary allParents[] = this.restTemplate.getForObject(this.url + GET_URL + "/{elementDomain}/{elementId}/parents", 
				ElementBoundary[].class, 
				manager.getUserId().getDomain(), manager.getUserId().getEmail(), child.getElementId().getDomain(), child.getElementId().getId());
		
		
		assertThat(allParents[0]).usingRecursiveComparison().isEqualTo(parent2);
		
	}
	
	
	@Test
	public void testCreateFiveElementsOneWithSpecialNameInvokeSearchByNameAndConfirmThatOnlyOneReturned() throws Exception{
		
		//GIVEN - Server contains 5 elements
		// WHEN - search by name is invoked on a special name that appears once
		//THEN - a single element is returned
		
		List <ElementBoundary> dbContent = IntStream.range(0, 5) //Stream <Integer> with size of 5 (0,1,2,3,4)
		.mapToObj(n -> n) // Stream<Strings> to Stream <Objects>
		.map(current -> 				// Initialize each object 
		new ElementBoundary (null,
							"INFO", 
							"Name #" + current, 
							 true,
							new Date(), 
							createdByManager, 
							null,
							null))
		.map(boundary -> //Invoke POST for each object
			this.restTemplate.postForObject(this.url + POST_URL, 
											boundary,
											ElementBoundary.class,
											manager.getUserId().getDomain(), manager.getUserId().getEmail()))
		.collect(Collectors.toList());
							
		ElementBoundary result[] = this.restTemplate.getForObject(this.url + GET_URL + "search/byName/{name}", 
																ElementBoundary[].class,
																manager.getUserId().getDomain(), manager.getUserId().getEmail(), "Name #2");
		
		assertThat(result).hasSize(1);
		
	}
	
	@Test
	public void testCreateThreeElementsOneWithSpecialTypeInvokeSearchByNameAndConfirmThatOnlyOneReturned() throws Exception{
		//GIVEN - Server contains 5 elements
		// WHEN - search by name is invoked on a special type that appears once
		//THEN - a single element is returned
		
		List <ElementBoundary> dbContent = IntStream.range(0, 5) //Stream <Integer> with size of 5 (0,1,2,3,4)
		.mapToObj(n -> n) // Stream<Strings> to Stream <Objects>
		.map(current -> 				// Initialize each object 
		new ElementBoundary (null,
							"INFO #"  + current, 
							"NAME", 
							 true,
							new Date(), 
							createdByManager, 
							null,
							null))
		.map(boundary -> //Invoke POST for each object
			this.restTemplate.postForObject(this.url + POST_URL, 
											boundary,
											ElementBoundary.class,
											manager.getUserId().getDomain(), manager.getUserId().getEmail()))
		.collect(Collectors.toList());
							
		ElementBoundary result[] = this.restTemplate.getForObject(this.url + GET_URL + "search/byType/{type}", 
																ElementBoundary[].class,
																manager.getUserId().getDomain(), manager.getUserId().getEmail(), "INFO #2");
		
		assertThat(result).hasSize(1);
		
	}
	
	@Test
	public void testCreateFourElementsWithLatLngIsIndexTimesTenThenInvokeSearchNearOfLatLngTwentyFiveWithDistanceTenExpectTwoResults() throws Exception {
		
		// GIVE - Server with 4 elements with lat lng (0,0) (10,10), (20,20) (30,30)
		// WHEN - Search near is invoked with lat/lng = 25, distance = 5
		//THEN - Output is 2 elements
		
		List <ElementBoundary> dbContent = IntStream.range(0, 4) //Stream <Integer> with size of 5 (0,1,2,3,4)
				.mapToObj(n -> n) // Stream<Strings> to Stream <Objects>
				.map(current -> 				// Initialize each object 
				new ElementBoundary (null,
									"INFO", 
									"NAME", 
									 true,
									new Date(), 
									createdByManager, 
									new LocationBoundary(current * 10.0, current * 10.0),
									null))
				.map(boundary -> //Invoke POST for each object
					this.restTemplate.postForObject(this.url + POST_URL, 
													boundary,
													ElementBoundary.class,
													manager.getUserId().getDomain(), manager.getUserId().getEmail()))
				.collect(Collectors.toList());
		
		System.err.println("\n\n" + dbContent.size()+"\n\n");
		
		assertThat(this.restTemplate.getForObject(this.url + GET_URL + "/search/near/{lat}/{lng}/{distance}", 
												  ElementBoundary[].class, 
												  manager.getUserId().getDomain(), manager.getUserId().getEmail(),
												  25, 25, 10)
				).hasSize(2);
		
	}
	
	@Test
	public void testCreatedParentWithTrueActiveBindTwoChildElementsOneWithFalseActiveOneWithTrueGetAllAsPlayerAndManagerConfirmPlayerHasOneChildAndManagerTwo() throws Exception {
		
		//GIVEN - Server with 1 parent elements with active=true, 2 children elements one with active=false the other with active=true
		// 			bound to parent
		//	WHEN - player and manager invoke get children method 
		// THEN - confirm that parent has 2 elements and that player has 1 server returns 2xx ok
		ElementBoundary parent = this.restTemplate.postForObject(this.url + POST_URL,
				new ElementBoundary(null, "parentType",	"Parent #1", true, new Date(),	createdByManager, null,	null),
				ElementBoundary.class,
				manager.getUserId().getDomain(), manager.getUserId().getEmail());

		ElementBoundary child1 = this.restTemplate.postForObject(this.url + POST_URL,
						new ElementBoundary(null, "childType",	"Child #1", true, new Date(),	createdByManager, null,	null),
						ElementBoundary.class,
						manager.getUserId().getDomain(), manager.getUserId().getEmail());
		
		ElementBoundary child2 = this.restTemplate.postForObject(this.url + POST_URL,
						new ElementBoundary(null, "childType",	"Child #2", false, new Date(),	createdByManager, null,	null),
						ElementBoundary.class,
						manager.getUserId().getDomain(), manager.getUserId().getEmail());
		
		Stream.of(child1, child2)
		.map(ElementBoundary::getElementId)
		.forEach(childIdBoundary->
			this.restTemplate.put(this.url + POST_URL + "/{elementDomain}/{elementId}/children", 
					childIdBoundary,
					manager.getUserId().getDomain(), manager.getUserId().getEmail(), parent.getElementId().getDomain(), parent.getElementId().getId()));
		
		assertThat(this.restTemplate
				.getForObject(this.url + GET_URL + "/{elementDomain}/{elementId}/children", 
						ElementBoundary[].class, 
						manager.getUserId().getDomain(), manager.getUserId().getEmail(), parent.getElementId().getDomain(), parent.getElementId().getId()))
			.hasSize(2);
		
		assertThat(this.restTemplate
				.getForObject(this.url + GET_URL + "/{elementDomain}/{elementId}/children", 
						ElementBoundary[].class, 
						player.getUserId().getDomain(), player.getUserId().getEmail(), parent.getElementId().getDomain(), parent.getElementId().getId()))
			.hasSize(1);
			
	}
	
	@Test
	public void testBindTwoChildrenOneWithFalseActiveOtherWithTrueToParentWithFalseActiveInvokeGetAllWithPlayerAndManagerConfirmPlayerHasZeroElementsManagerTwo() throws Exception{
		
		//GIVEN - server contains parent with 'active=false' and two child elements bound to it, one with 'active=false'
		//				and another with 'active=true'
		//WHEN	- manager and player invoke get all children method
		//THEN - manager gets both children and player has none.
		
		ElementBoundary parent = this.restTemplate.postForObject(this.url + POST_URL,
				new ElementBoundary(null, "parentType",	"Parent #1", false, new Date(),	createdByManager, null,	null),
				ElementBoundary.class,
				manager.getUserId().getDomain(), manager.getUserId().getEmail());

		ElementBoundary child1 = this.restTemplate.postForObject(this.url + POST_URL,
						new ElementBoundary(null, "childType",	"Child #1", true, new Date(),	createdByManager, null,	null),
						ElementBoundary.class,
						manager.getUserId().getDomain(), manager.getUserId().getEmail());
		
		ElementBoundary child2 = this.restTemplate.postForObject(this.url + POST_URL,
						new ElementBoundary(null, "childType",	"Child #2", false, new Date(),	createdByManager, null,	null),
						ElementBoundary.class,
						manager.getUserId().getDomain(), manager.getUserId().getEmail());
		
		Stream.of(child1, child2)
		.map(ElementBoundary::getElementId)
		.forEach(childIdBoundary->
			this.restTemplate.put(this.url + POST_URL + "/{elementDomain}/{elementId}/children", 
					childIdBoundary,
					manager.getUserId().getDomain(), manager.getUserId().getEmail(), parent.getElementId().getDomain(), parent.getElementId().getId()));
		
		assertThat(this.restTemplate
				.getForObject(this.url + GET_URL + "/{elementDomain}/{elementId}/children", 
						ElementBoundary[].class, 
						manager.getUserId().getDomain(), manager.getUserId().getEmail(), parent.getElementId().getDomain(), parent.getElementId().getId()))
			.hasSize(2);
		
		assertThat(this.restTemplate
				.getForObject(this.url + GET_URL + "/{elementDomain}/{elementId}/children", 
						ElementBoundary[].class, 
						player.getUserId().getDomain(), player.getUserId().getEmail(), parent.getElementId().getDomain(), parent.getElementId().getId()))
			.hasSize(0);
	}
	
	@Test
	public void testPlyaerAndMangerGetParentsWhenBoundChildWithTrueActiveToParentWithFalseActiveMangerGetsTheParentPlayerDoesnt() throws Exception {
		
		//GIVEN - server where parent with 'active=false' with child that has 'active=true' bounded to it
		// WHEN - player and manager invoke get parents
		// THEN - player gets 0 elements , manager gets 1
		
		ElementBoundary parent = this.restTemplate.postForObject(this.url + POST_URL,
				new ElementBoundary(null, "parentType",	"Parent #1", false, new Date(),	createdByManager, null,	null),
				ElementBoundary.class,
				manager.getUserId().getDomain(), manager.getUserId().getEmail());

		ElementBoundary child = this.restTemplate.postForObject(this.url + POST_URL,
						new ElementBoundary(null, "childType",	"Child", true, new Date(),	createdByManager, null,	null),
						ElementBoundary.class,
						manager.getUserId().getDomain(), manager.getUserId().getEmail());
		
		this.restTemplate.put(this.url + POST_URL + "/{elementDomain}/{elementId}/children", 
				child.getElementId(),
				manager.getUserId().getDomain(), manager.getUserId().getEmail(), parent.getElementId().getDomain(), parent.getElementId().getId());
		
		assertThat(this.restTemplate
				.getForObject(this.url + GET_URL + "/{elementDomain}/{elementId}/parents", 
						ElementBoundary[].class, 
						manager.getUserId().getDomain(), manager.getUserId().getEmail(), child.getElementId().getDomain(), child.getElementId().getId()))
			.hasSize(1);
		
		assertThat(this.restTemplate
				.getForObject(this.url + GET_URL + "/{elementDomain}/{elementId}/parents", 
						ElementBoundary[].class, 
						player.getUserId().getDomain(), player.getUserId().getEmail(), child.getElementId().getDomain(), child.getElementId().getId()))
			.hasSize(0);
	}	
	
	@Test
	public void testPlyaerAndMangerGetParentsWhenBoundChildWithFalseActiveToParentWithTrueActiveMangerGetsTheParentPlayerDoesnt() throws Exception {
		
		//GIVEN - server where parent with 'active=true' with child that has 'active=false' bounded to it
		// WHEN - player and manager invoke get parents
		// THEN - player gets 0 elements , manager gets 1
		
		ElementBoundary parent = this.restTemplate.postForObject(this.url + POST_URL,
				new ElementBoundary(null, "parentType",	"Parent #1", true, new Date(),	createdByManager, null,	null),
				ElementBoundary.class,
				manager.getUserId().getDomain(), manager.getUserId().getEmail());

		ElementBoundary child = this.restTemplate.postForObject(this.url + POST_URL,
						new ElementBoundary(null, "childType",	"Child", false, new Date(),	createdByManager, null,	null),
						ElementBoundary.class,
						manager.getUserId().getDomain(), manager.getUserId().getEmail());
		
		this.restTemplate.put(this.url + POST_URL + "/{elementDomain}/{elementId}/children", 
				child.getElementId(),
				manager.getUserId().getDomain(), manager.getUserId().getEmail(), parent.getElementId().getDomain(), parent.getElementId().getId());
		
		assertThat(this.restTemplate
				.getForObject(this.url + GET_URL + "/{elementDomain}/{elementId}/parents", 
						ElementBoundary[].class, 
						manager.getUserId().getDomain(), manager.getUserId().getEmail(), child.getElementId().getDomain(), child.getElementId().getId()))
			.hasSize(1);
		
		assertThat(this.restTemplate
				.getForObject(this.url + GET_URL + "/{elementDomain}/{elementId}/parents", 
						ElementBoundary[].class, 
						player.getUserId().getDomain(), player.getUserId().getEmail(), child.getElementId().getDomain(), child.getElementId().getId()))
			.hasSize(0);
	}
	
	@Test
	public void testCreateTwelveElementsUsingPaginationTestWithSizeIsEightThatPageZeroHasEightElementsAndPageOneHasFour() throws Exception {
		//GIVEN - Server contains 12 elements
		//WHEN - Manager invokes get all method using pagination size=10,page=0 and then size=10,page=1 
		//THEN - Receives 10 elements for the first invocation and then 2 for the second invocation
		
		List <ElementBoundary> dbContent = IntStream.range(1, 13) //Stream <Integer> with size of 12
				.mapToObj(n -> n) // Stream<Strings> to Stream <Objects>
				.map(current -> 				// Initialize each object 
				new ElementBoundary (null,
									"JustAType", 
									"Name", 
									 true,
									new Date(), 
									createdByManager, 
									null,
									null))
				.map(boundary -> //Invoke POST for each object
					this.restTemplate.postForObject(this.url + POST_URL, 
													boundary,
													ElementBoundary.class,
													manager.getUserId().getDomain(), manager.getUserId().getEmail()))
				.collect(Collectors.toList());
		
		assertThat(this.restTemplate
				.getForObject(this.url + GET_URL + "?size={size}&page={page}", 
						ElementBoundary[].class, 
						player.getUserId().getDomain(), player.getUserId().getEmail(), 8, 0))
			.hasSize(8);
		
		
		assertThat(this.restTemplate
				.getForObject(this.url + GET_URL + "?size={size}&page={page}", 
						ElementBoundary[].class, 
						player.getUserId().getDomain(), player.getUserId().getEmail(), 8, 1))
			.hasSize(4);
		
	}
	
	@Test
	public void testRecieveInvalidSizeAndPageParametersCheckForException() throws Exception {
		//GIVEN - Server contains some elements
		//WHEN - Get all is invokes with negative size or negative page values
		//THEN - Server throws exception 
		
		List <ElementBoundary> dbContent = IntStream.range(1, 20) //Stream <Integer> with size of 12
				.mapToObj(n -> n) // Stream<Strings> to Stream <Objects>
				.map(current -> 				// Initialize each object 
				new ElementBoundary (null,
									"JustAType", 
									"Name", 
									 true,
									new Date(), 
									createdByManager, 
									null,
									null))
				.map(boundary -> //Invoke POST for each object
					this.restTemplate.postForObject(this.url + POST_URL, 
													boundary,
													ElementBoundary.class,
													manager.getUserId().getDomain(), manager.getUserId().getEmail()))
				.collect(Collectors.toList());
		
		assertThrows(RuntimeException.class, ()->
		this.restTemplate.postForObject(this.url + GET_URL + "?size={size}&page={page}", 
										new ElementBoundary(null,"INFO","testName",	true,new Date(),createdByPlayer,null ,null),
										ElementBoundary.class,
										player.getUserId().getDomain(), player.getUserId().getEmail(), -1, 1));
		
		assertThrows(RuntimeException.class, ()->
		this.restTemplate.postForObject(this.url + GET_URL + "?size={size}&page={page}", 
										new ElementBoundary(null,"INFO","testName",	true,new Date(),createdByPlayer,null ,null),
										ElementBoundary.class,
										player.getUserId().getDomain(), player.getUserId().getEmail(), 1, -1));
	}
	
	@Test
	public void testCreateTwoElementsWithBuildingTypeSixWithApartmentInvokeFindAllByTypeAndNameConfirmReturnedSizeTwoAndSix() throws Exception {
		
		//GIVEN - Server is up and contains 8 elements , 6 are apartments and 2 are buildings
		//WHEN - Search by type and name where type is apartment and name is shelmo and then search where type is building
		//THEN - Return 6 elements and 2 elements
		
		ElementBoundary dbContent[] = IntStream.range(0, 2) 
				.mapToObj(n -> n)
				.map(current -> 				
				new ElementBoundary (null,"Building", "Shelmo",  true,	new Date(), new CreatedByBoundary(this.manager.getUserId()), 
									null,
									null))
				.map(boundary -> //Invoke POST for each object
					this.restTemplate.postForObject(this.url + POST_URL, 
													boundary,
													ElementBoundary.class,
													manager.getUserId().getDomain(), manager.getUserId().getEmail()))
				.collect(Collectors.toList())
				.toArray(new ElementBoundary[0]);
						
		ElementBoundary dbContent2[] = IntStream.range(2, 8) 
				.mapToObj(n -> n) 
				.map(current -> 				
				new ElementBoundary (null,
									"Apartment", 
									"Shelmo", 
									 true,
									new Date(), 
									new CreatedByBoundary(this.manager.getUserId()), 
									null,
									null))
				.map(boundary -> //Invoke POST for each object
					this.restTemplate.postForObject(this.url + POST_URL, 
													boundary,
													ElementBoundary.class,
													manager.getUserId().getDomain(), manager.getUserId().getEmail()))
				.collect(Collectors.toList())
				.toArray(new ElementBoundary[0]);
		
		
		
		assertThat(this.restTemplate.getForObject(this.url + GET_URL,
				  ElementBoundary[].class, 
				  manager.getUserId().getDomain(), manager.getUserId().getEmail())).hasSize(8);
		
		Map<String, Object> actionAttributes = new HashMap<String, Object>();
		actionAttributes.put("name", "Shelmo");
		actionAttributes.put("type", "Apartment");
		
		assertThat(this.restTemplate.postForObject(this.url + "actions",
				new ActionBoundary(new IdBoundary("ofir", null), "searchElementsByNameAndType",
						null,new Date(), new InvokedByBoundary(new UserIdBoundary(this.player.getUserId().getDomain(), this.player.getUserId().getEmail())), 
						actionAttributes),
				  ElementBoundary[].class, 
				  manager.getUserId().getDomain(), manager.getUserId().getEmail())).hasSize(6);
		
		
		actionAttributes.put("type", "Building");
		assertThat(this.restTemplate.postForObject(this.url + "actions",
				new ActionBoundary(new IdBoundary("ofir", null), "searchElementsByNameAndType",
						null,new Date(), new InvokedByBoundary(new UserIdBoundary(this.player.getUserId().getDomain(), this.player.getUserId().getEmail())), 
						actionAttributes),
				  ElementBoundary[].class, 
				  manager.getUserId().getDomain(), manager.getUserId().getEmail())).hasSize(2);
	}
	
	@Test
	public void testCreateElementsWithTwoManagerInvokeSearchElementsByCreatorAndVerifyTheReturnedSize() throws Exception {
		//GIVEN - Two managers , one created 2 elements, other created 5
		//WHEN - Search all elements by the user of created them
		//THEN - return 2 elements for the first manager, 5 for the second
		UserBoundary manager2 = this.restTemplate.postForObject(this.url + "/users",  
				new UserNewDetails("m2@gmail.com", UserRole.MANAGER, "manager2", ":/"),
		  			UserBoundary.class);
		
		ElementBoundary dbContent[] = IntStream.range(0, 2) 
				.mapToObj(n -> n)
				.map(current -> 				
				new ElementBoundary (null,"Building", "Shelmo",  true,	new Date(), new CreatedByBoundary(this.manager.getUserId()), 
									null,null))
				.map(boundary -> //Invoke POST for each object
					this.restTemplate.postForObject(this.url + POST_URL, 
													boundary,
													ElementBoundary.class,
													manager.getUserId().getDomain(), manager.getUserId().getEmail()))
				.collect(Collectors.toList())
				.toArray(new ElementBoundary[0]);
						
		ElementBoundary dbContent2[] = IntStream.range(2, 7) 
				.mapToObj(n -> n) 
				.map(current -> 				
				new ElementBoundary (null,"Apartment", "Shelmo",  true,new Date(), new CreatedByBoundary(manager2.getUserId()), 
									null, null))
				.map(boundary -> //Invoke POST for each object
					this.restTemplate.postForObject(this.url + POST_URL, 
													boundary,
													ElementBoundary.class,
													manager2.getUserId().getDomain(), manager2.getUserId().getEmail()))
				.collect(Collectors.toList())
				.toArray(new ElementBoundary[0]);
	
		assertThat(this.restTemplate.getForObject(this.url + GET_URL,
				  ElementBoundary[].class, 
				  manager.getUserId().getDomain(), manager.getUserId().getEmail())).hasSize(7);
		
		
		assertThat(this.restTemplate.postForObject(this.url + "actions",
				new ActionBoundary(new IdBoundary("ofir", null), "searchElementsOfUser",
						null,new Date(), new InvokedByBoundary(new UserIdBoundary(manager2.getUserId().getDomain(), manager2.getUserId().getEmail())), 
						null),
				  ElementBoundary[].class)).hasSize(5);
		
		assertThat(this.restTemplate.postForObject(this.url + "actions",
				new ActionBoundary(new IdBoundary("ofir", null), "searchElementsOfUser",
						null,new Date(), new InvokedByBoundary(new UserIdBoundary(manager.getUserId().getDomain(), manager.getUserId().getEmail())), 
						null),
				  ElementBoundary[].class)).hasSize(2);
	}
}

