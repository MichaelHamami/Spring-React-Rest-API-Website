package acs.logic.action;

import java.util.List;

import acs.rest.action.ActionBoundary;

public interface ExtendedActionService extends ActionService {
	
	public List<ActionBoundary> getAllActions(String adminDomain, String adminEmail,int page, int size);
}
