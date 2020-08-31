package acs.logic.action;

import java.util.List;

import acs.rest.action.ActionBoundary;

public interface ActionService {

	public Object invokeAction(ActionBoundary action);

	public List<ActionBoundary> getAllActions(String adminDomain, String adminEmail);

	public void deleteAllActions(String adminDomain, String adminEmail);
}
