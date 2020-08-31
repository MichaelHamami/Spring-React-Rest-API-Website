package acs.logic.db;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import acs.dal.UserDao;
import acs.data.users.UserEntity;
import acs.data.utils.UserIdEntity;
import acs.data.utils.UserRole;
import acs.logic.exceptions.EntityNotFoundException;
import acs.logic.exceptions.ForbiddenActionException;
import acs.logic.user.ExtentedUserService;
import acs.logic.user.UserConvertor;
import acs.rest.users.UserBoundary;
import acs.rest.utils.UserIdBoundary;
import acs.rest.utils.ValidEmail;

@Service
public class DbUserService implements ExtentedUserService {

	private String projectName;
	private UserDao userDao;
	private UserConvertor convertor;
	private ValidEmail valid;

	@Value("${spring.application.name:ofir.cohen}")
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	@Autowired
	public DbUserService(UserDao userDao, UserConvertor convertor, ValidEmail valid) {
		this.userDao = userDao;
		this.convertor = convertor;
		this.valid = valid;

	}

	@Override
	@Transactional
	public UserBoundary createUser(UserBoundary user) {

		if (!valid.isEmailVaild(user.getUserId().getEmail())) {
			throw new RuntimeException("Email invalid!!");
		}

		if (user.getAvatar() == null && user.getAvatar().trim().isEmpty()) {
			throw new RuntimeException("Avatar invalid!!");
		}
		if (user.getUsername() == null) {
			throw new RuntimeException("User Name invalid!!");
		}

		user.setUserId(new UserIdBoundary(this.projectName, user.getUserId().getEmail()));
		UserIdEntity userId = new UserIdEntity(user.getUserId().getDomain(), user.getUserId().getEmail());

		Optional<UserEntity> entityOptional = this.userDao.findById(userId);
		if (entityOptional.isPresent()) {
			throw new RuntimeException("the User alreay exsite in the data base!!");
		} else {
			if (user.getRole() != null) {
				UserEntity entity = this.convertor.toEntity(user);
				this.userDao.save(entity);
				return this.convertor.fromEntity(entity);
			} else {
				throw new RuntimeException("the role is not from UserRole type");
			}
		}
	}

	@Override
	@Transactional
	public UserBoundary login(String userDomain, String userEmail) {

		if (!(valid.isEmailVaild(userEmail))) {
			throw new RuntimeException("Email invalid!!");
		}

		UserIdEntity userId = new UserIdEntity(userDomain, userEmail);
		Optional<UserEntity> entityOptional = this.userDao.findById(userId);
		if (entityOptional.isPresent()) {
			UserEntity entity = entityOptional.get();
			UserBoundary boundary = this.convertor.fromEntity(entity);
			return boundary;

		} else {
			throw new RuntimeException("the login action failed! please check the user details again");
		}

	}

	@Override
	@Transactional
	public UserBoundary updateUser(String userDomain, String userEmail, UserBoundary update) {

		if (!valid.isEmailVaild(userEmail)) {
			throw new RuntimeException("Email invalid!!");
		}

		if (update.getAvatar() == null && update.getAvatar().trim().isEmpty()) {
			throw new RuntimeException("Avatar invalid!!");
		}
		if (update.getUsername() == null) {
			throw new RuntimeException("User Name invalid!!");
		}

		UserIdEntity userId = new UserIdEntity(userDomain, userEmail);
		Optional<UserEntity> entityOptional = this.userDao.findById(userId);
		if (entityOptional.isPresent()) {
			UserEntity entity = entityOptional.get();
			entity.setUsername(update.getUsername());
			entity.setRole(update.getRole());
			entity.setAvatar(update.getAvatar());
			return this.convertor.fromEntity(entity);
		} else {
			throw new RuntimeException("invalid user details");
		}
	}

	@Override
	@Transactional
	public List<UserBoundary> getAllUsers(String adminDomain, String adminEmail) {
		if (!valid.isEmailVaild(adminEmail)) {
			throw new RuntimeException("Email invalid");
		}

		return StreamSupport.stream(this.userDao.findAll().spliterator(), false).map(this.convertor::fromEntity)
				.collect(Collectors.toList());

	}

	@Override
	@Transactional
	public List<UserBoundary> getAllUsers(String adminDomain, String adminEmail, int page, int size) {
		UserEntity admin = this.userDao.findById(new UserIdEntity(adminDomain, adminEmail))
				.orElseThrow(() -> new EntityNotFoundException(
						"No User Exists With Domain: " + adminDomain + "Email: " + adminEmail));

		if (!admin.getRole().equals(UserRole.ADMIN)) {
			throw new ForbiddenActionException("Only Admin Has Permission To get all users");
		}

		if (!valid.isEmailVaild(adminEmail)) {
			throw new RuntimeException("Email invalid");
		}
		return this.userDao.findAll(
				// use pagination base on size & page and sort by ID in
				PageRequest.of(page, size, Direction.ASC, "userId")).getContent() // List<userBoudary>
				.stream() // Stream<userBoudary>
				.map(this.convertor::fromEntity) // Stream <userBoudary>
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void deleteAllUsers(String adminDomain, String adminEmail) {
		// Search the user that the action in Db
		UserEntity admin = this.userDao.findById(new UserIdEntity(adminDomain, adminEmail))
				.orElseThrow(() -> new EntityNotFoundException(
						"No User Exists With Domain " + adminDomain + " Email " + adminEmail));

		if (!admin.getRole().equals(UserRole.ADMIN)) {
			throw new ForbiddenActionException("Only Admin Has Permission To delete Actions");
		}

		this.userDao.deleteAll();

	}
}
