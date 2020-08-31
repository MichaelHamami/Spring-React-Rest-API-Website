package acs.dal;



import org.springframework.data.repository.PagingAndSortingRepository;

import acs.data.users.UserEntity;
import acs.data.utils.UserIdEntity;

public interface UserDao extends PagingAndSortingRepository<UserEntity, UserIdEntity>{
	

}
