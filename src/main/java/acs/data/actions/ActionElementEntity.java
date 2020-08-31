package acs.data.actions;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import acs.data.utils.ElementIdEntity;

@Embeddable
public class ActionElementEntity  {
	private ElementIdEntity elementId;

	public ActionElementEntity() {
	}
	
	public ActionElementEntity(ElementIdEntity elementId) {
		super();
		this.elementId = elementId;
	}
	
	@Embedded
	public ElementIdEntity getElementId() {
		return elementId;
	}

	public void setElementId(ElementIdEntity elementId) {
		this.elementId = elementId;
	}

	@Override
	public String toString() {
		return "elementEntiny [elementId=" + elementId + "]";
	}
}
