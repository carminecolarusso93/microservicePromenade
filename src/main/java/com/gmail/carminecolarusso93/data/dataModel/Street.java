package com.gmail.carminecolarusso93.data.dataModel;

import java.util.ArrayList;

public class Street {

	private long id;
	private double lenght;
	private double ffs;
	private String speedlimit;
	private long from, to;
	private String area, name;
	private ArrayList<Coordinate> coordinates;
	private double weight;


	/**
	 *
	 * @param id                Local id of the Street.
	 * @param lenght            Lenght in meter of the street.
	 * @param speedlimit        Speed limit of the street in km/h.
	 * @param ffs 				Free Flow Speed on link
	 * @param from        		OpenStreetMap id of the starting intersection.
	 * @param to         		OpenStreetMap id of the destination intersection.
	 * @param area				Area Node Name
	 * @param name              Name of the Street.
	 * @param coordinates       Describes Geometry of the street.
	 * @param weight
	 */

	public Street(long id, double lenght, double ffs, String speedlimit, long from, long to, String area, String name, ArrayList<Coordinate> coordinates, double weight) {
		this.id = id;
		this.lenght = lenght;
		this.ffs = ffs;
		this.speedlimit = speedlimit;
		this.from = from;
		this.to = to;
		this.area = area;
		this.name = name;
		this.coordinates = coordinates;
		this.weight = weight;
	}

	/**
	 *
	 * Constructor with Weight equals to street length
	 *
	 * @param id                Local id of the Street.
	 * @param lenght            Lenght in meter of the street.
	 * @param speedlimit        Speed limit of the street in km/h.
	 * @param ffs 				Free Flow Speed on link
	 * @param from        		OpenStreetMap id of the starting intersection.
	 * @param to         		OpenStreetMap id of the destination intersection.
	 * @param area				Area Node Name
	 * @param name              Name of the Street.
	 * @param coordinates       Describes Geometry of the street.
	 */
	public Street(long id, double lenght, double ffs, String speedlimit, long from, long to, String area, String name, ArrayList<Coordinate> coordinates) {
		this.id = id;
		this.lenght = lenght;
		this.ffs = ffs;
		this.speedlimit = speedlimit;
		this.from = from;
		this.to = to;
		this.area = area;
		this.name = name;
		this.coordinates = coordinates;
		this.weight = lenght;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public double getLenght() {
		return lenght;
	}

	public void setLenght(double lenght) {
		this.lenght = lenght;
	}

	public double getFfs() {
		return ffs;
	}

	public void setFfs(double ffs) {
		this.ffs = ffs;
	}

	public String getSpeedlimit() {
		return speedlimit;
	}

	public void setSpeedlimit(String speedlimit) {
		this.speedlimit = speedlimit;
	}

	public long getFrom() {
		return from;
	}

	public void setFrom(long from) {
		this.from = from;
	}

	public long getTo() {
		return to;
	}

	public void setTo(long to) {
		this.to = to;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Coordinate> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(ArrayList<Coordinate> coordinates) {
		this.coordinates = coordinates;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}
}