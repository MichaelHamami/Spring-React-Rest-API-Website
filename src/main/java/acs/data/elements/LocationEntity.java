package acs.data.elements;

import javax.persistence.Embeddable;
import javax.persistence.Entity;

@Embeddable
public class LocationEntity {
	
	private double lat;
	private double lng;
	
	public LocationEntity() {
		
	}

	public LocationEntity(double lat, double lng) {
		super();
		this.lat = lat;
		this.lng = lng;
	}

	
	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	@Override
	public String toString() {
		return "LocationEntity [lat=" + lat + ", lng=" + lng + "]";
	}
	
	
	
}
