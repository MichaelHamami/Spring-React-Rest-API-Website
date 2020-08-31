package acs.rest.element.boundaries;

public class LocationBoundary {
	
	private Double lat;
	private Double lng;
	
	public LocationBoundary() {
		
	}
	
	public LocationBoundary(Double lat, Double lng) {
		super();
		this.lat = lat;
		this.lng = lng;
	}
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	public Double getLng() {
		return lng;
	}
	public void setLng(Double lng) {
		this.lng = lng;
	}
	@Override
	public String toString() {
		return "LocationBoundary [lat=" + lat + ", lng=" + lng + "]";
	}
	
	
	
}
