package acs.logic.db;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import acs.dal.ActionDao;
import acs.dal.ElementDao;
import acs.dal.UserDao;
import acs.data.actions.ActionElementEntity;
import acs.data.actions.ActionEntity;
import acs.data.actions.InvokedByEntity;
import acs.data.elements.ElementEntity;
import acs.data.users.UserEntity;
import acs.data.utils.ActionIdEntity;
import acs.data.utils.ElementIdEntity;
import acs.data.utils.UserIdEntity;
import acs.data.utils.UserRole;
import acs.logic.action.ActionConverter;
import acs.logic.action.ActionService;
import acs.logic.action.ExtendedActionService;
import acs.logic.exceptions.EntityNotFoundException;
import acs.logic.exceptions.ForbiddenActionException;
import acs.rest.action.ActionBoundary;
import acs.rest.utils.ValidEmail;

@Service
public class DbActionService implements ExtendedActionService {
	private String projectName;
	private ActionDao actionDao;
	private UserDao userDao;
	private ElementDao elementDao;
	private ActionConverter converter;
	private ValidEmail valid;

	@Autowired
	public DbActionService(ActionDao actionDao, UserDao userDao, ElementDao elementDao, ActionConverter converter,
			ValidEmail valid) {
		this.actionDao = actionDao;
		this.userDao = userDao;
		this.elementDao = elementDao;
		this.converter = converter;
		this.valid = valid;
	}

	// inject value from configuration or use default value
	@Value("${spring.application.name:ofir.cohen}")
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	private UserEntity retrieveUserInfoFromDb(String domain, String email) {
		return this.userDao.findById(new UserIdEntity(domain, email)).orElseThrow(
				() -> new EntityNotFoundException("No User Exists With Domain " + domain + " Email " + email));
	}

	private ElementEntity retrieveElementInfoFromDb(String domain, String id) {
		return this.elementDao.findById(new ElementIdEntity(domain, id))
				.orElseThrow(() -> new EntityNotFoundException("DB No Element With Id " + domain + "!" + id));
	}

	@Override
	@Transactional // (readOnly = false)
	public Object invokeAction(ActionBoundary action) {
		String id;
		ActionEntity entity;
		ElementEntity elementEntity;
		// Search the user that invoked the action in Db
		UserEntity player = retrieveUserInfoFromDb(action.getInvokedBy().getUserId().getDomain(),
				action.getInvokedBy().getUserId().getEmail());

		if (!player.getRole().equals(UserRole.PLAYER)) {
			throw new ForbiddenActionException("Only Player Has Permission To Invoke Actions");
		}

		elementEntity = retrieveElementInfoFromDb(action.getElement().getElementId().getDomain(),
				action.getElement().getElementId().getId());

		// check if element is active
		if (!elementEntity.getActive()) {
			throw new ForbiddenActionException("Action must be active");
		}

		if (action.getType() == null) {
			throw new RuntimeException("Type can not be null");
		}
		if (!valid.isEmailVaild(action.getInvokedBy().getUserId().getEmail())) {
			throw new RuntimeException("Invalid Email");
		}

		id = UUID.randomUUID().toString();

		entity = this.converter.toEntity(action);

		entity.setActionId(new ActionIdEntity(this.projectName, id));

		entity.setCreatedTimestamp(new Date());

		entity.setInvokedBy(new InvokedByEntity(new UserIdEntity(action.getInvokedBy().getUserId().getDomain(),
				action.getInvokedBy().getUserId().getEmail())));

		entity.setElement(new ActionElementEntity(new ElementIdEntity(action.getElement().getElementId().getDomain(),
				action.getElement().getElementId().getId())));

		return this.converter.fromEntity(this.actionDao.save(entity)); // SELECT +INSERT / UPDATE

	}

	@Override
	@Transactional(readOnly = true)
	public List<ActionBoundary> getAllActions(String adminDomain, String adminEmail) {
		// INVOKE SELECT DATABASE
		return StreamSupport.stream(this.actionDao.findAll().spliterator(), false) // Stream<ActionEntity>
				.map(this.converter::fromEntity)// Stream<ActionBoundary>
				.collect(Collectors.toList()); // List<ActionBoundary>
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActionBoundary> getAllActions(String adminDomain, String adminEmail, int page, int size) {
		UserEntity admin = retrieveUserInfoFromDb(adminDomain, adminEmail);
		
		if (!admin.getRole().equals(UserRole.ADMIN)) {
			throw new ForbiddenActionException("Only Admin Has Permission To get all Actions");
		}
		return this.actionDao.findAll(
				// use pagination base on size & page and sort by ID in ascending order
				PageRequest.of(page, size, Direction.ASC, "actionId")).getContent() // List<ActionEntity>
				.stream() // Stream<ActionEntity>
				.map(this.converter::fromEntity) // Stream <ActionBoundary>
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void deleteAllActions(String adminDomain, String adminEmail) {
		UserEntity admin = retrieveUserInfoFromDb(adminDomain, adminEmail);

		if (!admin.getRole().equals(UserRole.ADMIN)) {
			throw new ForbiddenActionException("Only Admin Has Permission To delete Actions");
		}

		// INVOKE DELETE DATABASE: DELETE
		this.actionDao.deleteAll(); // DELETE

	}

}
