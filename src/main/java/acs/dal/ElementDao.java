package acs.dal;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import acs.data.elements.CreatedByEntity;
import acs.data.elements.ElementEntity;
import acs.data.utils.ElementIdEntity;											
public interface ElementDao extends PagingAndSortingRepository<ElementEntity, ElementIdEntity>{
	
	// select * where name is ?
	public List<ElementEntity> findAllByName(
			@Param("name") String name,
			Pageable pageable); 
	
	// select * where name is ? and active is ?
	public List<ElementEntity> findAllByNameAndActive(
			@Param("name") String name,
			@Param("active") boolean active, 
			Pageable pageable);
	
	// select * where type is ?
	public List<ElementEntity> findAllByType(
			@Param("type") String type,
			Pageable pageable); 
	
	// select * where type is ? and active is ?
	public List<ElementEntity> findAllByTypeAndActive(
			@Param("type") String name,
			@Param("active") boolean active, 
			Pageable pageable);
	
	// select * where parentId is ?
	public List<ElementEntity> findAllChildrenByParent_ElementId(
			@Param("parentId") ElementIdEntity parentId, 
			Pageable pageable);
	
	// select * where parentId is ? and active is ?
		public List<ElementEntity> findAllChildrenByParent_ElementIdAndActive(
				@Param("parentId") ElementIdEntity parentId, 
				@Param("active") boolean active, 
				Pageable pageable);
	
	// select * where childId is ?
	public List<ElementEntity> findAllParentsByChildren_ElementId(
			@Param("parentId") ElementIdEntity parentId, 
			Pageable pageable);
	
	// select * where childId is ? and active is true
	public List<ElementEntity> findAllParentsByChildren_ElementIdAndActive(
			@Param("parentId") ElementIdEntity parentId, 
			@Param("active") boolean active, 
			Pageable pageable);
	
	// select * where active is ?
	public List<ElementEntity> findAllByActive(
			@Param("active") boolean active, 
			Pageable pageable);
	
	
	// select * where lat+
	public List<ElementEntity> findAllBylocationLatBetweenAndLocationLngBetween(
			@Param("lat1") Double lat1,
			@Param("lat2") Double lat2,
			@Param("lng1") Double lng1,
			@Param("lng2") Double lng2,
			Pageable pageable);
	
	public List<ElementEntity> findAllBylocationLatBetweenAndLocationLngBetweenAndActive(
			@Param("lat1") Double lat1,
			@Param("lat2") Double lat2,
			@Param("lng1") Double lng1,
			@Param("lng2") Double lng2,
			@Param("active") boolean active, 
			Pageable pageable);
	
	// select * where created by is ?
		public List<ElementEntity> findAllByCreatedBy(
				@Param("createdBy") CreatedByEntity createdBy,
				Pageable pageable);
		
	// select * where created by is ? and active is ?
	public List<ElementEntity> findAllByCreatedByAndActive(
			@Param("createdBy") CreatedByEntity createdBy,
			@Param("active") boolean active, 
			Pageable pageable);
	
	// select * where name is ? and type is ?
	public List<ElementEntity> findAllByNameAndType(
			@Param("name") String name,
			@Param("type") String type, 
			Pageable pageable);
	
	// select * where name is ? and type is ? and active is ?
	public List<ElementEntity> findAllByNameAndTypeAndActive(
			@Param("name") String name,
			@Param("type") String type,
			@Param("active") boolean active, 
			Pageable pageable);
	
}
