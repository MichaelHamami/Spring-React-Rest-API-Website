package acs.rest.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import acs.logic.action.ExtendedActionService;
import acs.logic.element.ElementService;
import acs.logic.user.ExtentedUserService;
import acs.rest.action.ActionBoundary;
import acs.rest.users.UserBoundary;


@RestController
public class AdminController {

	private final String DEFAULT_PAGE_SIZE = "10";
	private final String DEFAULT_PAGE_NUM = "0";
	
	private ExtentedUserService dbUserService;
	private ElementService dbElementeService;
	private ExtendedActionService actionService;

	@Autowired
	public AdminController(ExtentedUserService dbUserService, ElementService dbElementeService,
			ExtendedActionService actionService) {
		this.dbUserService = dbUserService;
		this.dbElementeService = dbElementeService;
		this.actionService = actionService;
	}

	// Delete all users in the system

	@RequestMapping(path = "/acs/admin/users/{adminDomain}/{adminEmail}", method = RequestMethod.DELETE)
	public void delete_AllUsers(@PathVariable("adminDomain") String adminDomain,
			@PathVariable("adminEmail") String adminEmail

	) {
		this.dbUserService.deleteAllUsers(adminDomain, adminEmail);

	}

	// Delete all elements in the system
	@RequestMapping(path = "/acs/admin/elements/{adminDomain}/{adminEmail}", method = RequestMethod.DELETE)
	public void delete_AllElements(@PathVariable("adminDomain") String adminDomain,
			@PathVariable("adminEmail") String adminEmail) {
		this.dbElementeService.deleteAllElements(adminDomain, adminEmail);

	}

	// Delete all actions in the system
	@RequestMapping(path = "/acs/admin/actions/{adminDomain}/{adminEmail}", method = RequestMethod.DELETE)

	public void delete_AllActions(@PathVariable("adminDomain") String adminDomain,
			@PathVariable("adminEmail") String adminEmail) {
		this.actionService.deleteAllActions(adminDomain, adminEmail);

	}

//	 Export all users
	@RequestMapping(path = "/acs/admin/users/{adminDomain}/{adminEmail}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)

	public UserBoundary[] exports_AllUsers(@RequestParam(name = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) int size,
			@RequestParam(name = "page", required = false, defaultValue = DEFAULT_PAGE_NUM) int page, 
			@PathVariable("adminDomain") String adminDomain,
			@PathVariable("adminEmail") String adminEmail) {
		if(size <= 0) {
			throw new RuntimeException("Page size must be positive");
		}
		if(page < 0) {
			throw new RuntimeException("Page number must be zero or higher");
		}
		return this.dbUserService.getAllUsers(adminDomain, adminEmail, page, size).toArray(new UserBoundary[0]);

	}

	// Export all actions
	@RequestMapping(path = "/acs/admin/actions/{adminDomain}/{adminEmail}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ActionBoundary[] exports_AllActions(
			@RequestParam(name = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) int size,
			@RequestParam(name = "page", required = false, defaultValue = DEFAULT_PAGE_NUM) int page,
			@PathVariable("adminDomain") String adminDomain, @PathVariable("adminEmail") String adminEmail) {
		if (size <= 0) {
			throw new RuntimeException("Page size must be positive");
		}

		if (page < 0) {
			throw new RuntimeException("Page number must be zero or higher");
		}
		return this.actionService.getAllActions(adminDomain, adminEmail, page, size).toArray(new ActionBoundary[0]);
	}
}