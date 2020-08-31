package acs.data.elements;

import java.util.*;

import javax.persistence.Convert;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import acs.dal.MapToJsonConverter;
import acs.data.utils.ElementIdEntity;
import acs.rest.element.*;
import acs.rest.element.boundaries.CreatedByBoundary;

@Entity 
@Table(name = "ELEMENTS")
public class ElementEntity {
	private ElementIdEntity elementId;
	private String type;
	private String name;
	private boolean active;	
	private Date createdTimestamp;
	private CreatedByEntity createdBy;
	private LocationEntity location; 
	private Map <String, Object> elementAttributes;
	
	private ElementEntity parent;
	private Set<ElementEntity> children;
	
	public ElementEntity() {	
		
		this.children = new HashSet<>();
	}


	public ElementEntity(ElementIdEntity elementId, String type, String name, boolean active, Date createdTimestamp,
			CreatedByEntity createdBy, LocationEntity location, Map<String, Object> elementAttributes) {
		super();
		this.elementId = elementId;
		this.type = type;
		this.name = name;
		this.active = active;
		this.createdTimestamp = createdTimestamp;
		this.createdBy = createdBy;
		this.location = location;
		this.elementAttributes = elementAttributes;
		this.children = new HashSet<>();
	}
	
	@EmbeddedId
	public ElementIdEntity getElementId() {
		return elementId;
	}

	
	public void setElementId(ElementIdEntity elementId) {
		this.elementId = elementId;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public boolean getActive() {
		return active;
	}


	public void setActive(boolean active) {
		this.active = active;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}


	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	@Embedded
	public CreatedByEntity getCreatedBy() {
		return createdBy;
	}


	public void setCreatedBy(CreatedByEntity createdBy) {
		this.createdBy = createdBy;
	}

	@Embedded
	public LocationEntity getLocation() {
		return location;
	}

	
	public void setLocation(LocationEntity location) {
		this.location = location;
	}

	@Convert(converter = MapToJsonConverter.class)
	@Lob
	public Map<String, Object> getElementAttributes() {
		return elementAttributes;
	}

	public void setElementAttributes(Map<String, Object> elementAttributes) {
		this.elementAttributes = elementAttributes;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	public ElementEntity getParent() {	
		return parent;
	}


	public void setParent(ElementEntity parent) {
		this.parent = parent;
	}

	@OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
	public Set<ElementEntity> getChildren() {
		return children;
	}

	public void setChildren(Set<ElementEntity> children) {
		this.children = children;
	}
	
	public void addChild(ElementEntity child) {
		this.children.add(child);
		child.setParent(this);
	}

	@Override
	public String toString() {
		return "ElementBoundary [elementId=" + elementId + ", type=" + type + ", name=" + name + ", active=" + active
				+ ", createdTimestamp=" + createdTimestamp + ", createdBy=" + createdBy + ", location=" + location
				+ ", elementAttributes=" + elementAttributes + "]";
	}

}
