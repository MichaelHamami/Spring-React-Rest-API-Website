package acs.logic.action;

import org.springframework.stereotype.Component;

import acs.data.actions.ActionEntity;
import acs.data.utils.ActionIdEntity;
import acs.data.utils.UserIdEntity;
import acs.rest.action.ActionBoundary;
import acs.rest.action.boundaries.ActionElementBoundary;
import acs.rest.action.boundaries.InvokedByBoundary;
import acs.rest.utils.IdBoundary;
import acs.rest.utils.UserIdBoundary;

@Component
public class ActionConverter {
	public ActionBoundary fromEntity(ActionEntity entity) {

		IdBoundary actionIdBoundary = new IdBoundary();
		IdBoundary elemetIdBoundary = new IdBoundary();
		ActionElementBoundary elementBoundaryA = new ActionElementBoundary();
		InvokedByBoundary invokedByBoundary = new InvokedByBoundary();
		UserIdBoundary userIdBoundary = new UserIdBoundary();

		if (entity.getActionId() != null) {
			actionIdBoundary.setDomain(entity.getActionId().getDomain());
			actionIdBoundary.setId(entity.getActionId().getId());
		} else {
			actionIdBoundary = null;
		}

		if (entity.getElement() != null && entity.getElement().getElementId() != null) {
			elemetIdBoundary.setDomain(entity.getElement().getElementId().getDomain());
			elemetIdBoundary.setId(entity.getElement().getElementId().getId());
			elementBoundaryA.setElementId(elemetIdBoundary);
		} else {
			elementBoundaryA = null;
		}

		if (entity.getInvokedBy() != null && entity.getInvokedBy().getUserId() != null) {
			userIdBoundary.setDomain(entity.getInvokedBy().getUserId().getDomain());
			userIdBoundary.setEmail(entity.getInvokedBy().getUserId().getEmail());
			invokedByBoundary.setUserId(userIdBoundary);
		} else {
			invokedByBoundary = null;
		}

		return new ActionBoundary(actionIdBoundary, entity.getType(), elementBoundaryA, entity.getCreatedTimestamp(),
				invokedByBoundary, entity.getActionAttributes());
	}

	public ActionEntity toEntity(ActionBoundary boundary) {
		ActionEntity entity = new ActionEntity();

		if (boundary.getType() != null) {
			entity.setType(boundary.getType());
		} else {
			throw new RuntimeException("ActionBoundary invalid type");
		}

		if (boundary.getElement() != null && boundary.getElement().getElementId() != null) {
			IdBoundary elementId = new IdBoundary();
			elementId.setDomain(boundary.getElement().getElementId().getDomain());
			elementId.setId(boundary.getElement().getElementId().getId());
		} else {
			throw new RuntimeException("ActionBoundary invalid element");
		}

		entity.setCreatedTimestamp(boundary.getCreatedTimestamp());

		if (boundary.getInvokedBy() != null && boundary.getInvokedBy().getUserId() != null) {
			UserIdEntity userIdEntity = new UserIdEntity();
			userIdEntity.setDomain(boundary.getInvokedBy().getUserId().getDomain());
			userIdEntity.setDomain(boundary.getInvokedBy().getUserId().getEmail());
		} else {
			throw new RuntimeException("ActionBoundary invalid invokedby");
		}

		entity.setActionAttributes(boundary.getActionAttributes());

		return entity;
	}
}
