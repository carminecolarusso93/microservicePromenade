package com.gmail.carminecolarusso93.data.dataModel;

import java.util.HashMap;


public class Intersection {

	private Coordinate coordinate;
	private long osmid;
	private double betweenness;
	private HashMap<Integer, Street> streets;


	/**
	 * @param coordinate  Longitude and Latitude of Intersection in Decimal Degrees
	 *                    (DD).
	 * @param osmid       OpenStreetMap Id of the intersection in the road network
	 * @param betweenness The start value of Betweenness Centrality of the
	 *                    Intersection
	 */
	public Intersection(Coordinate coordinate, long osmid, double betweenness) {
		super();
		this.coordinate = coordinate;
		this.osmid = osmid;
		this.betweenness = betweenness;
		this.streets = null;
	}

	/**
	 * 
	 * @param coordinate  Longitude and Latitude of Intersection in Decimal Degrees
	 *                    (DD).
	 * @param osmid       OpenStreetMap Id of the intersection in the road network
	 * @param betweenness The start value of Betweenness Centrality of the
	 *                    Intersection
	 * @param streets     Streets coming out of the intersection.
	 */
	public Intersection(Coordinate coordinate, long osmid, double betweenness,
			HashMap<Integer, Street> streets) {
		super();
		this.coordinate = coordinate;
		this.osmid = osmid;
		this.betweenness = betweenness;
		this.streets = streets;
	}


	public Coordinate getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}

	public long getOsmid() {
		return osmid;
	}

	public void setOsmid(long osmid) {
		this.osmid = osmid;
	}

	public double getBetweenness() {
		return betweenness;
	}

	public void setBetweenness(double beetweeness) {
		this.betweenness = beetweeness;
	}

	public HashMap<Integer, Street> getStreets() {
		return streets;
	}

	public void setStreets(HashMap<Integer, Street> streets) {
		this.streets = streets;
	}

	@Override
	public String toString() {
		return "Intersection{" +
				"coordinate=" + coordinate +
				", osmid=" + osmid +
				", betweenness=" + betweenness +
				", streets=" + streets +
				'}';
	}
}