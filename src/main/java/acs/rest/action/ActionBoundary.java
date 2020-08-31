package acs.rest.action;

import java.util.Date;
import java.util.Map;

import acs.rest.action.boundaries.InvokedByBoundary;
import acs.rest.action.boundaries.ActionElementBoundary;
import acs.rest.utils.IdBoundary;

public class ActionBoundary {
	private IdBoundary actionId;
	private String type;
	private ActionElementBoundary element;
	private Date createdTimestamp;
	private InvokedByBoundary invokedBy;
	private Map<String, Object> actionAttributes;

	public ActionBoundary() {

	}

	public ActionBoundary(IdBoundary actionId, String type, ActionElementBoundary element, Date createdTimestamp,
			InvokedByBoundary invokedBy, Map<String, Object> actionAttributes) {
		super();
		this.actionId = actionId;
		this.type = type;
		this.element = element;
		this.createdTimestamp = createdTimestamp;
		this.invokedBy = invokedBy;
		this.actionAttributes = actionAttributes;
	}

	public IdBoundary getActionId() {
		return actionId;
	}

	public void setActionId(IdBoundary actionId) {
		this.actionId = actionId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ActionElementBoundary getElement() {
		return element;
	}

	public void setElement(ActionElementBoundary element) {
		this.element = element;
	}

	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public InvokedByBoundary getInvokedBy() {
		return invokedBy;
	}

	public void setInvokedBy(InvokedByBoundary invokedBy) {
		this.invokedBy = invokedBy;
	}

	public Map<String, Object> getActionAttributes() {
		return actionAttributes;
	}

	public void setActionAttributes(Map<String, Object> actionAttributes) {
		this.actionAttributes = actionAttributes;
	}

	@Override
	public String toString() {
		return "ActionBoundary [actionId=" + actionId + ",type=" + type + ",element=" + element + ",createdTimestamp="
				+ createdTimestamp + ",invokedBy=" + invokedBy + ",actionAttributes=" + actionAttributes + "]";
	}

}
