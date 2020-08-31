package acs.dal;

import org.springframework.data.repository.PagingAndSortingRepository;

import acs.data.actions.ActionEntity;
import acs.data.utils.ActionIdEntity;

//Create Read Update Delete
public interface ActionDao extends PagingAndSortingRepository<ActionEntity, ActionIdEntity> {

}
