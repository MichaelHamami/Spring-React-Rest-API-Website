package acs.logic.element;

import org.springframework.stereotype.Component;
import acs.data.elements.ElementEntity;
import acs.data.elements.LocationEntity;
import acs.data.utils.ElementIdEntity;
import acs.data.utils.UserIdEntity;
import acs.rest.element.boundaries.CreatedByBoundary;
import acs.rest.element.boundaries.ElementBoundary;
import acs.rest.element.boundaries.LocationBoundary;
import acs.rest.utils.IdBoundary;
import acs.rest.utils.UserIdBoundary;


@Component
public class ElementConverter {

	public ElementBoundary fromEntity (ElementEntity entity) {
		
		IdBoundary elementIdBoundary = new IdBoundary();
		CreatedByBoundary createdByBoundary = new CreatedByBoundary();
		UserIdBoundary userIdBoundary = new UserIdBoundary();
		LocationBoundary locationBoundary = new LocationBoundary();
		
		if (entity.getElementId() != null) {
			elementIdBoundary.setDomain(entity.getElementId().getDomain());
			elementIdBoundary.setId(entity.getElementId().getId());
		}
		else {
			elementIdBoundary = null;
		}
		
		
		if (entity.getCreatedBy() != null && entity.getCreatedBy().getUserId() != null) {
			userIdBoundary.setDomain(entity.getCreatedBy().getUserId().getDomain());
			userIdBoundary.setEmail(entity.getCreatedBy().getUserId().getEmail());
			createdByBoundary.setUserId(userIdBoundary);
		}
		else {
			createdByBoundary = null;
		}
		
		if(entity.getLocation() != null) {
			locationBoundary.setLat(entity.getLocation().getLat());
			locationBoundary.setLng(entity.getLocation().getLng());
		}
		else {
			//locationBoundary.setLat(0.0);
			//locationBoundary.setLng(0.0);
		}
		
		
		return new ElementBoundary(elementIdBoundary,
				entity.getType(),
				entity.getName(),
				entity.getActive(),
				entity.getCreatedTimestamp(),
				createdByBoundary,
				locationBoundary,
				entity.getElementAttributes());
	}
	
	public ElementEntity toEntity (ElementBoundary boundary) {
		ElementEntity entity = new ElementEntity();
		
		if (boundary.getElementId() != null) {
			ElementIdEntity elementIdEntity = new ElementIdEntity();
			elementIdEntity.setDomain(boundary.getElementId().getDomain());
			elementIdEntity.setId(boundary.getElementId().getId());
		}
		else {
			entity.setElementId(new ElementIdEntity());
		}
				
		if(boundary.getType() != null && !boundary.getType().trim().isEmpty()) {
			entity.setType(boundary.getType());
		} 
		else {
			throw new RuntimeException("Invalid ElementBoundary Type");
		}
		
		if(boundary.getName() != null && !boundary.getName().trim().isEmpty()) {
			entity.setName(boundary.getName());
		} 
		else {
			throw new RuntimeException("Invalid ElementBoundary Name");
		}
		
		if(boundary.getActive() != null) {
			entity.setActive(boundary.getActive());
		} 
		else {
			entity.setActive(true);
		}
		
		
		entity.setCreatedTimestamp(boundary.getCreatedTimestamp());
		
		if(boundary.getCreatedBy()!= null && boundary.getCreatedBy().getUserId() != null) {
			UserIdEntity userIdEntity = new UserIdEntity();
			userIdEntity.setDomain(boundary.getCreatedBy().getUserId().getDomain());
			userIdEntity.setEmail(boundary.getCreatedBy().getUserId().getEmail());
		}
		else {
			entity.setCreatedBy(null);
		}
		
		if (boundary.getLocation() != null) {
			
			LocationEntity locationEntity = new LocationEntity();
			
			if(boundary.getLocation().getLat() != null) {
				locationEntity.setLat(boundary.getLocation().getLat());
			}
		
			
			if(boundary.getLocation().getLng() != null) {
				locationEntity.setLng(boundary.getLocation().getLng());
			}

			
			entity.setLocation(locationEntity);
		} 
		else {
			
			entity.setLocation(new LocationEntity(0, 0));
		}
		
		entity.setElementAttributes(boundary.getElementAttributes());
		
		return entity;
	}
}
