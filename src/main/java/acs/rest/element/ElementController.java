package acs.rest.element;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import acs.logic.element.ExtendedElementService;
import acs.rest.element.boundaries.ElementBoundary;
import acs.rest.utils.IdBoundary;

@RestController
@CrossOrigin(origins="http://localhost:3000/",allowedHeaders = "Access-Control-Allow-Origin")
public class ElementController {	
	private final String DEFAULT_PAGE_SIZE = "10";
	private final String DEFAULT_PAGE_NUM = "0";
	
	private ExtendedElementService elementService;

	@Autowired
	public void setElementService(ExtendedElementService elementService) {
		this.elementService = elementService;
	}
	
	private void varifyPageAndSize(int page, int size) {
		if (size <= 0 ) {
			throw new RuntimeException("Page size must be positive");
		}
		
		if (page < 0) {
			throw new RuntimeException("Page number must be zero or higher");
		}
	}
	
		// Create (POST) new element
	@RequestMapping(path = "/acs/elements/{managerDomain}/{managerEmail}",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary createNewElement(@PathVariable("managerDomain") String managerDomain ,
		     					  @PathVariable("managerEmail") String managerEmail,
		     					  @RequestBody ElementBoundary element) {
		
		return this.elementService.create(managerDomain, managerEmail, element);
	}
		
		//Update an Element
	@RequestMapping(path = "/acs/elements/{managerDomain}/{managerEmail}/{elementDomain}/{elementId}",
			method = RequestMethod.PUT,			
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateElement(@PathVariable("managerDomain") String managerDomain ,
		     				  @PathVariable("managerEmail") String managerEmail,
		     				  @PathVariable("elementDomain") String elementDomain,
		     				  @PathVariable("elementId") String elementId,
		     				  @RequestBody ElementBoundary eb) {
		eb = this.elementService.update(managerDomain, managerEmail, elementDomain, elementId, eb);
		System.err.println("After Update\n"+eb);
	}
	
		// Get (GET) Specific element by elementId (key)
	@RequestMapping(path = "/acs/elements/{userDomain}/{userEmail}/{elementDomain}/{elementId}",
			method = RequestMethod.GET,		
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary getSpecificElement(@PathVariable("userDomain") String userDomain,
		  						  @PathVariable("userEmail") String userEmail,
		  						  @PathVariable("elementDomain") String elementDomain,
		  						  @PathVariable("elementId") String elementId) {
		return this.elementService.getSpecificElement(userDomain, userEmail, elementDomain, elementId);
	}
		
		//Get All (GET) elements
	@RequestMapping(path = "/acs/elements/{userDomain}/{userEmail}",
			method = RequestMethod.GET,		
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] getAllElements(@RequestParam(name = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) int size,
											@RequestParam(name = "page", required = false, defaultValue = DEFAULT_PAGE_NUM) int page,
											@PathVariable("userDomain") String userDomain,
										  	@PathVariable("userEmail") String userEmail) {
		
		varifyPageAndSize(page, size);
		return this.elementService.getAll(userDomain, userEmail, page, size).toArray(new ElementBoundary[0]);
	}
	
	
		// Bind child to parent
	@RequestMapping(path = "/acs/elements/{managerDomain}/{managerEmail}/{elementDomain}/{elementId}/children",
			method = RequestMethod.PUT,			
			consumes = MediaType.APPLICATION_JSON_VALUE)	
	public void addChildElementToParentElement(@PathVariable("managerDomain") String managerDomain ,
											  @PathVariable("managerEmail") String managerEmail,
											  @PathVariable("elementDomain") String elementDomain,
											  @PathVariable("elementId") String elementId,
											  @RequestBody IdBoundary childId) {
			
		this.elementService.bindChildToParent(managerDomain, managerEmail, elementDomain, elementId, childId);
		
	}
		
		//  Get all children from parent
	@RequestMapping(path = "/acs/elements/{userDomain}/{userEmail}/{elementDomain}/{elementId}/children",
			method = RequestMethod.GET,		
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] getAllElementChildrenFromElementParent(
										@RequestParam(name = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) int size,
										@RequestParam(name = "page", required = false, defaultValue = DEFAULT_PAGE_NUM) int page,
										@PathVariable("userDomain") String userDomain ,
										@PathVariable("userEmail") String userEmail,
										@PathVariable("elementDomain") String elementDomain,
										@PathVariable("elementId") String elementId) {
		varifyPageAndSize(page, size);
		
		return this.elementService.getChildren(userDomain, userEmail, elementDomain, elementId, page, size)
				.toArray(new ElementBoundary[0]);
	}
		
		// Get all parents from child
	@RequestMapping(path = "/acs/elements/{userDomain}/{userEmail}/{elementDomain}/{elementId}/parents",
			method = RequestMethod.GET,			
			produces = MediaType.APPLICATION_JSON_VALUE)	
	public ElementBoundary[] getParentsFromChild(@RequestParam(name = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) int size,
												  @RequestParam(name = "page", required = false, defaultValue = DEFAULT_PAGE_NUM) int page,
												  @PathVariable("userDomain") String userDomain ,
												  @PathVariable("userEmail") String userEmail,
												  @PathVariable("elementDomain") String elementDomain,
												  @PathVariable("elementId") String elementId) {
		varifyPageAndSize(page, size);
		
		ElementBoundary[] parents = this.elementService.getParent(userDomain, userEmail, elementDomain , elementId, page, size)
									.toArray(new ElementBoundary[0]);
		
		return parents;
	}
		// Get all elements , filtered by name
	@RequestMapping(path = "/acs/elements/{userDomain}/{userEmail}/search/byName/{name}",
			method = RequestMethod.GET,		
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] searchByName (@RequestParam(name = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) int size,
										   @RequestParam(name = "page", required = false, defaultValue = DEFAULT_PAGE_NUM) int page,
										   @PathVariable("userDomain") String userDomain,
										   @PathVariable("userEmail") String userEmail,
										   @PathVariable("name") String name){
		
		varifyPageAndSize(page, size);
		
		return this.elementService.searchElementsByName(userDomain, userEmail, name, page, size)
				.toArray(new ElementBoundary[0]);	}
	
		// Get all elements , filtered by type
	@RequestMapping(path = "/acs/elements/{userDomain}/{userEmail}/search/byType/{type}",
			method = RequestMethod.GET,		
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] searchByType (@RequestParam(name = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) int size,
										   @RequestParam(name = "page", required = false, defaultValue = DEFAULT_PAGE_NUM) int page,
										   @PathVariable("userDomain") String userDomain,
										   @PathVariable("userEmail") String userEmail,
										   @PathVariable("type") String type){
		
		varifyPageAndSize(page, size);
		
		return this.elementService.searchElementsByType(userDomain, userEmail, type, page, size)
				.toArray(new ElementBoundary[0]);
	}
	
	
		// Get all elements , filtered by distance from lat and lng 
	@RequestMapping(path = "/acs/elements/{userDomain}/{userEmail}/search/near/{lat}/{lng}/{distance}",
			method = RequestMethod.GET,		
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] searchByDistance (@RequestParam(name = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) int size,
										   @RequestParam(name = "page", required = false, defaultValue = DEFAULT_PAGE_NUM) int page,
										   @PathVariable("userDomain") String userDomain,  @PathVariable("userEmail") String userEmail,
										   @PathVariable("lat") Double lat,  @PathVariable("lng") Double lng,
										   @PathVariable("distance") Double distance){
		
		varifyPageAndSize(page, size);
		
		return this.elementService.searchElementsByLocation(userDomain, userEmail, lat, lng, distance, page, size)
				.toArray(new ElementBoundary[0]);

	}
	
}
