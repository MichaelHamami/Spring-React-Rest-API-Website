package acs.logic.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import acs.dal.ElementDao;
import acs.dal.UserDao;
import acs.data.elements.CreatedByEntity;
import acs.data.elements.ElementEntity;
import acs.data.elements.LocationEntity;
import acs.data.users.UserEntity;
import acs.data.utils.ElementIdEntity;
import acs.data.utils.UserIdEntity;
import acs.data.utils.UserRole;
import acs.logic.element.ElementConverter;
import acs.logic.element.ExtendedElementService;
import acs.logic.exceptions.EntityNotFoundException;
import acs.logic.exceptions.PageNotFound;
import acs.logic.exceptions.ForbiddenActionException;
import acs.rest.element.boundaries.ElementBoundary;
import acs.rest.utils.IdBoundary;
import acs.rest.utils.UserIdBoundary;

@Service
public class DbElementService implements ExtendedElementService {

	private String projectName;
	
	private ElementDao elementDao;
	private UserDao userDao;
	
	private ElementConverter converter;
		
	@Value("${spring.application.name:ofir.cohen}")
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	@Autowired
	public DbElementService(ElementDao elementDao, UserDao userDao, ElementConverter converter) {
		this.converter = converter;
		this.elementDao = elementDao;
		this.userDao = userDao;
		
	}
	
	private UserEntity retrieveUserFromDb (String domain, String email) {
		return this.userDao.findById(new UserIdEntity (domain, email)).orElseThrow(
				() -> new EntityNotFoundException("No User Exists With Domain " + domain + " Email " + email));
	}
	
	private UserEntity retrieveUserFromDb (UserIdBoundary userId) {
		return this.userDao.findById(new UserIdEntity(userId.getDomain(), userId.getEmail())).orElseThrow(
				() -> new EntityNotFoundException("No User Exists With Domain " + userId.getDomain() + " Email " + userId.getEmail()));
	}
	
	private ElementEntity retrieveElementFromDb (String domain, String id) {
		return this.elementDao.findById(new ElementIdEntity(domain, id)).orElseThrow(
				() -> new EntityNotFoundException("DB No Element With Id " + domain + "!" + id));
	}
	
	@Override
	@Transactional
	public ElementBoundary create(String managerDomain, String managerEmail, ElementBoundary element) {
		
		UserEntity manager = retrieveUserFromDb(managerDomain, managerEmail);
		
		if (!manager.getRole().equals(UserRole.MANAGER)) {
			//only a manager should have access to this action
			throw new ForbiddenActionException("User Has No Permission To Create Elements");
		}
				
		ElementEntity entity = this.converter.toEntity(element);
		
		String id = UUID.randomUUID().toString();
		
		entity.setElementId(new ElementIdEntity(this.projectName, id));
		
		entity.setCreatedTimestamp(new Date());
		
		entity.setCreatedBy(new CreatedByEntity (new UserIdEntity (managerDomain, managerEmail)));
		
		// select + insert / update
		return this.converter.fromEntity(this.elementDao.save(entity));
	}

	@Override
	@Transactional
	public ElementBoundary update(String managerDomain, String managerEmail, String elementDomain, String elementId,
			ElementBoundary update) {
		
		UserEntity manager = retrieveUserFromDb(managerDomain, managerEmail);
		
		if (!manager.getRole().equals(UserRole.MANAGER)) {
			//only a manager should have access to this action
			throw new ForbiddenActionException("User Has No Permission To Update Elements");
		}
		
		ElementEntity entity = retrieveElementFromDb(elementDomain, elementId);
		
		if(update.getType() != null  && !update.getType().trim().isEmpty()) {
			entity.setType(update.getType()); 
		}
		else {
			throw new RuntimeException("Invalid type , please try again");
		}
		
		if(update.getName() != null && !update.getName().trim().isEmpty()) {
			entity.setName(update.getName()); 
		}
		else {
			throw new RuntimeException("Invalid name , please try again");
		}
		
		if (update.getActive() != null) {
			entity.setActive(update.getActive());
		}
		else {
			entity.setActive(false);
		}
		
		if (update.getLocation() != null) {
			
			LocationEntity locationEntity = new LocationEntity();
			
			if(update.getLocation().getLat() != null) {
				locationEntity.setLat(update.getLocation().getLat());
			}
						
			if(update.getLocation().getLng() != null) {
				locationEntity.setLng(update.getLocation().getLng());
			}
			
			entity.setLocation(locationEntity);
		} 

		entity.setElementAttributes(update.getElementAttributes());
		
		update = this.converter.fromEntity(entity);
		
		this.elementDao.save(entity);
		
		return update;
	}

	@Override
	@Transactional(readOnly = true)
	public List <ElementBoundary> getAll(String userDomain, String userEmail) {
		// invoke select * from database
		
		return StreamSupport.stream(this.elementDao.findAll().spliterator(), false)
				.map(this.converter :: fromEntity)
				.collect(Collectors.toList());
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ElementBoundary> getAll(String userDomain, String userEmail,int page, int size) {
		
		UserEntity user = retrieveUserFromDb(userDomain, userEmail);
			
		if(user.getRole().equals(UserRole.MANAGER)) {
			// if manager -> regulat find all 
			return this.elementDao.findAll(
					PageRequest.of(page, size, Direction.ASC, "elementId")) // paginate according to page and size
					.getContent() 	//List<ElementEntity>
					.stream() 	// Stream<ElementEntity>
					.map(this.converter::fromEntity)	 //Stream<ElementBoundary>
					.collect(Collectors.toList());
		}
		else {
			// if player -> find all with 'active is true'
			if (user.getRole().equals(UserRole.PLAYER)) {
				return this.elementDao.findAllByActive(true, PageRequest.of(page, size, Direction.ASC, "elementId")) // List<ElementEntity>
						.stream() // Stream <ElementEntity>
						.map(this.converter::fromEntity) // Stream <ElementBoundary>
						.collect(Collectors.toList()); // List <ElementBoundary>
			}
			else { // user role must be admin by process of elimination 
				throw new ForbiddenActionException("User Doesn't Have Permission To Get Elements");
			}
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ElementBoundary getSpecificElement(String userDomain, String userEmail, String elementDomain, String elementId) {
		// invoke select database
		
		UserEntity user = retrieveUserFromDb(userDomain, userEmail);
		
		ElementEntity element = retrieveElementFromDb(elementDomain, elementId);
		
		if(user.getRole().equals(UserRole.MANAGER) || (user.getRole().equals(UserRole.PLAYER) && element.getActive())) {
			// manager has access to all elements , player can only get 'active is true' elements
			
			System.err.println("LOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOL\n" + this.converter.fromEntity(element));
			
			return this.converter.fromEntity(element);		
		}
		else {
			// role is admin or player is trying to access 'active = false' element
			throw new PageNotFound("User Doesn't Have Access To Get This Element");
		}
	}
	
	
	@Override
	@Transactional
	public void deleteAllElements(String adminDomain, String adminEmail) {
		//Invoke delete database
		
		UserEntity user = retrieveUserFromDb(adminDomain, adminEmail);
		
		if(!user.getRole().equals(UserRole.ADMIN)) {
			throw new PageNotFound("User Isn't Allowed To Delete");
		}
		
		this.elementDao.deleteAll();
	}

	@Override
	@Transactional
	public void bindChildToParent(String managerDomain, String managerEmail, String elementDomain , String elementId, IdBoundary childId) {
		
		UserEntity user = retrieveUserFromDb(elementDomain, managerEmail);
		
		if(!user.getRole().equals(UserRole.MANAGER)) {
			throw new ForbiddenActionException("User Doesn't Have Permission To Bind Elements");
		}
		
		if(childId == null) {
			throw new EntityNotFoundException("Child ElementId Isn't Initialized");
		}
		
		ElementEntity parent = retrieveElementFromDb(elementDomain, elementId);
				
		ElementEntity child =  retrieveElementFromDb(childId.getDomain(), childId.getId());
						
		if(parent.getElementId().equals(child.getElementId())) {
			throw new RuntimeException("Parent and Child share Id - Cannot Bind Element To Itself");
		}
		
		parent.addChild(child);
		this.elementDao.save(parent);
	}
		
		// Get all children elements from parentId
	@Override
	@Transactional(readOnly = true)
	public Set<ElementBoundary> getChildren(String userDomain, String userEmail, String elementDomain , String elementId, int page,int size) {
		
		UserEntity user = retrieveUserFromDb(userDomain, userEmail);
		
		ElementEntity parent = retrieveElementFromDb(elementDomain, elementId);
		
		List <ElementEntity> children;
		
		if(user.getRole().equals(UserRole.MANAGER)) { // if manager -> no checks required
			children = this.elementDao.findAllChildrenByParent_ElementId(parent.getElementId(),
					   PageRequest.of(page, size,Direction.ASC, "elementId"));
		}
		else if (user.getRole().equals(UserRole.PLAYER)) { 
			if(parent.getActive()) { // if player and 'active' parent is true - search 'active' children
				children = this.elementDao.findAllChildrenByParent_ElementIdAndActive(parent.getElementId(), true,
						   PageRequest.of(page, size,Direction.ASC, "elementId"));
			}
			else { // when player and active is false return empty list
				children = new ArrayList<ElementEntity>();
			}
			
		}
		else { // role is admin - doesn't have permission for this action
			throw new ForbiddenActionException("User Doesn't Have Permission To Get Children Elements");
		}
		
		return children.stream()
				.map(this.converter::fromEntity)
				.collect(Collectors.toSet());
	}
		
		// Get parent elemnt from childId
	@Override
	@Transactional(readOnly = true)
	public Set<ElementBoundary> getParent(String userDomain, String userEmail, String elementDomain , String elementId, int page,int size) {
		
		UserEntity user = retrieveUserFromDb(userDomain, userEmail);
				
		ElementEntity child = retrieveElementFromDb(elementDomain, elementId);
		
		List<ElementEntity> parents;
		
		if(user.getRole().equals(UserRole.MANAGER)) {
			// if manager , no need to do any checks
			parents = this.elementDao.findAllParentsByChildren_ElementId(child.getElementId(), PageRequest.of(page, size, Direction.ASC, "elementId"));
		}
		else {
			if(user.getRole().equals(UserRole.PLAYER)) {
				if(child.getActive()) {
					// child's 'active' attribute is true - player can access this method 
					parents = this.elementDao.findAllParentsByChildren_ElementIdAndActive(child.getElementId(), 
																						true, 
																						PageRequest.of(page, size, Direction.ASC, "elementId"));
				} 
				else {
					// if child's 'active' attribute is false - return empty set
					parents = new ArrayList<ElementEntity>();
				}
			}
			else { // admin doesn't have access to this action
				throw new ForbiddenActionException("User Doesn't Have Permission To Get Parents of Element");
			}
		}
		
		
		return parents.stream()
					  .map(this.converter::fromEntity)
					  .collect(Collectors.toSet());	
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ElementBoundary> searchElementsByName(String userDomain, String userEmail, String name, int page, int size) {
		
		UserEntity user = retrieveUserFromDb(userDomain, userEmail);
		
		List <ElementEntity> results;
		if(user.getRole().equals(UserRole.MANAGER)) {
			results = this.elementDao.findAllByName(name, PageRequest.of(page, size, Direction.ASC, "elementId"));
		}
		else {
			if (user.getRole().equals(UserRole.PLAYER)) {
				results =  this.elementDao.findAllByNameAndActive(name, true, PageRequest.of(page, size, Direction.ASC, "elementId"));
			}
			else {
				// user role must be admin by process of elimination 
				throw new ForbiddenActionException("User Doesn't Have Permission To Get Elements");
			}
		}
			
		return results.stream()  // Stream <ElementEntity>
				.map(this.converter::fromEntity)  // Stream <ElementBoundary>
				.collect(Collectors.toList()); // List <ElementBoundary>
	}
	
	
	@Override
	@Transactional(readOnly = true)
	public List<ElementBoundary> searchElementsByType(String userDomain, String userEmail, String type, int page, int size) {
		
		UserEntity user = retrieveUserFromDb(userDomain, userEmail);
		
		List <ElementEntity> results;
		
		if(user.getRole().equals(UserRole.MANAGER)) {
			results = this.elementDao.findAllByType(type, PageRequest.of(page, size, Direction.ASC, "elementId"));
		}
		else {
			if (user.getRole().equals(UserRole.PLAYER)) {
				results =  this.elementDao.findAllByTypeAndActive(type, true, PageRequest.of(page, size, Direction.ASC, "elementId"));
			}
			else {
				// user role must be admin by process of elimination 
				throw new ForbiddenActionException("User Doesn't Have Permission To Get Elements");
			}
		}
		
		return results.stream()  // Stream <ElementEntity>
				.map(this.converter::fromEntity)  // Stream <ElementBoundary>
				.collect(Collectors.toList()); // List <ElementBoundary>
	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementBoundary> searchElementsByLocation(String userDomain, String userEmail, double lat, double lng, double distance , int page, int size) {
		
		// Checks if user is in the database
		UserEntity user = retrieveUserFromDb(userDomain, userEmail);
		
		// Set max/min distance for the lng/lat
		double lat_start = lat - distance;
		double lat_end = lat + distance;
		double lng_start = lng - distance; 
		double lng_end = lng + distance;
		
		List <ElementEntity> results = this.elementDao.findAllBylocationLatBetweenAndLocationLngBetween(lat_start,lat_end,lng_start,lng_end, PageRequest.of(page, size, Direction.ASC, "elementId"));
		
		if(user.getRole().equals(UserRole.MANAGER)) {
			results = this.elementDao.findAllBylocationLatBetweenAndLocationLngBetween(lat_start,
																					   lat_end,
																					   lng_start,lng_end,
																					   PageRequest.of(page, size, Direction.ASC, "elementId"));
		}
		else {
			if (user.getRole().equals(UserRole.PLAYER)) {
				results =  this.elementDao.findAllBylocationLatBetweenAndLocationLngBetweenAndActive(lat_start,
																									lat_end,
																									lng_start,
																									lng_end,
																									true,
																									PageRequest.of(page, size, Direction.ASC, "elementId"));
			}
			else {
				// user role must be admin by process of elimination 
				throw new ForbiddenActionException("User Doesn't Have Permission To Get Elements");
			}
		}
		
		return results.stream()  // Stream <ElementEntity>
				.map(this.converter::fromEntity)  // Stream <ElementBoundary>
				.collect(Collectors.toList());	// List <ElementBoundary>
		}

	@Override
	@Transactional(readOnly = true)
	public List<ElementBoundary> searchAllElementsOfUser(UserIdBoundary userId, int page, int size) {
		// Check if user exists
		UserEntity user = retrieveUserFromDb(userId);
		
		// Gets all elements of that user
		List <ElementEntity> results;
		
		// Gets all elements that were created by user
		if (user.getRole().equals(UserRole.MANAGER)) {
			results = this.elementDao.findAllByCreatedBy(
					new CreatedByEntity(new UserIdEntity(userId.getDomain(), userId.getEmail())),
					PageRequest.of(page, size, Direction.ASC, "elementId"));
		}
		else if (user.getRole().equals(UserRole.PLAYER)) {
			results = this.elementDao.findAllByCreatedByAndActive(
					new CreatedByEntity(new UserIdEntity(userId.getDomain(), userId.getEmail())), true,
					PageRequest.of(page, size, Direction.ASC, "elementId"));
		}
		else {
			throw new ForbiddenActionException("User Doesn't Have Permission To Get Elements");
		}
		
		if(results.size() == 0) {
			throw new PageNotFound("No results found for " + userId.getEmail());
		}
		
		return results.stream()
				.map(this.converter::fromEntity)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementBoundary> searchElementsByNameAndType(UserIdBoundary userId, String name, String type, int page, int size) {
		// Check if user exists
		UserEntity user = retrieveUserFromDb(userId);
		
		List <ElementEntity> results;	
		
		// Gets all elements with a specific name and type
		if(user.getRole().equals(UserRole.PLAYER)) {
			results = this.elementDao.findAllByNameAndTypeAndActive(
					name, type, true, PageRequest.of(page, size, Direction.ASC, "elementId"));
		}
		else if (user.getRole().equals(UserRole.MANAGER)) {
			results = this.elementDao.findAllByNameAndType(
					name, type, PageRequest.of(page, size, Direction.ASC, "elementId"));			
		}
		else {
			throw new ForbiddenActionException("User Doesn't Have Permission To Get Elements");
		}
		
		if(results.size() == 0) {
			throw new PageNotFound("No results found for " + type + " : " + name);
		}
		
		return results.stream()
				.map(this.converter::fromEntity)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void deleteSpecificElement(UserIdBoundary userId, IdBoundary element) {
		
		// Check if user exists
		UserEntity user = retrieveUserFromDb(userId);
		
		if(!user.getRole().equals(UserRole.ADMIN)) {
			throw new PageNotFound("User Isn't Allowed To Delete");
		}
		
		// Check if element exists
		ElementEntity entity = retrieveElementFromDb(element.getDomain(), element.getId());
		
		// Performs delete by setting 'active' to false
		entity.setActive(false);
		
		// Deletes the element
		this.elementDao.save(entity);
	}
	
}
