package acs.rest.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import acs.logic.action.ActionService;
import acs.logic.element.ExtendedElementService;

@RestController
public class ActionController {
	
	private final int DEFAULT_PAGE_SIZE = 10;
	private final int DEFAULT_PAGE_NUM = 0;
	private final String EXPECTED_SEARCH_FIELDS[] = {"type", "name"};
	private final String ACTION_TYPES[] = {"searchElementsOfUser", "searchElementsByNameAndType", "deleteSpecific"};
	private ActionService actionService;
	private ExtendedElementService elementService;
	
	@Autowired
	public void setActionService(ActionService actionService, ExtendedElementService elementService) {
		this.actionService = actionService;
		this.elementService = elementService;
	}


	// Invoke an action (POST)
	@RequestMapping(path = "/acs/actions", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Object invokeAnAction(@RequestBody ActionBoundary action) {
		
		// Check if  'searchElementsOfUser' is invoked
		if(action.getType().equals(ACTION_TYPES[0])) {
			return this.elementService.searchAllElementsOfUser(action.getInvokedBy().getUserId(), DEFAULT_PAGE_NUM, DEFAULT_PAGE_SIZE);
		}
		
		// check if 'searchElementsByNameAndType' is invoked
		if(action.getType().equals(ACTION_TYPES[1])) {
			String type , name;
			
			type = action.getActionAttributes().get(EXPECTED_SEARCH_FIELDS[0]).toString();
			name = action.getActionAttributes().get(EXPECTED_SEARCH_FIELDS[1]).toString();
						
			return this.elementService.searchElementsByNameAndType(action.getInvokedBy().getUserId(), 
					name, 
					type, 
					DEFAULT_PAGE_NUM,
					DEFAULT_PAGE_SIZE);
		}
		
		// Check if 'deleteSpecific' is invoked
		if(action.getType().equals(ACTION_TYPES[2])) {
			this.elementService.deleteSpecificElement(action.getInvokedBy().getUserId(), action.getElement().getElementId());
			return null;
		}
				
		return this.actionService.invokeAction(action);
	}

}
