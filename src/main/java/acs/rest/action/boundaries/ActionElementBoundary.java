package acs.rest.action.boundaries;

import acs.rest.utils.IdBoundary;

public class ActionElementBoundary {
	private IdBoundary elementId;
	
	public ActionElementBoundary() {
	}
	
	public ActionElementBoundary(IdBoundary elementId) {
		super();
		this.elementId = elementId;
	}
	
	public IdBoundary getElementId() {
		return elementId;
	}

	public void setElementId(IdBoundary elementId) {
		this.elementId = elementId;
	}

	@Override
	public String toString() {
		return "elementBoundary [elementId=" + elementId + "]";
	}


}
