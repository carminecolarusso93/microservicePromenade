package data.databaseDriver;

import data.dataModel.Coordinate;
import data.dataModel.Intersection;
import data.dataModel.Street;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Interface that gives all methods that define the operations to do on neo4j
 * database to run the application.
 *
 * @author Giovanni Codianni
 * @author Carmine Colarusso
 * @author Chiara Verdone
 *
 */
public interface DAOUser {

	/**
	 * This method instantiate a new session and runs a read query.
	 *
	 * @param query contains a query written in CypherQueryLanguage.
	 * @return StatementResult of query execution.
	 *         <p>
	 *         If driver is not instantiated yet throws
	 *         {@link DatabaseNotConnectException}
	 *
	 */
	Result databaseRead(String query);


	/**
	 * Returns the Intersection with given Id.
	 * <p>
	 * Heavy interrogation of database, returns an Intersection with related
	 * streets.
	 *
	 * @param osmid Id of the Intersection to find.
	 * @return The Java representation of the searched Intersection.
	 */
	Intersection getIntersection(long osmid);

	/**
	 * Returns the Intersection with given Id.
	 * <p>
	 * Lightweight interrogation of database, unlike getIntersection() method it
	 * returns an Intersection without related streets.
	 *
	 * @param osmid Id of the Intersection to find.
	 * @return The Java representation of the searched Intersection.
	 */
	Intersection getIntersectionLight(long osmid);

	/**
	 * Returns the Street with given Id.
	 *
	 * @param id Id of the Street to find.
	 * @return The Java representation of the searched Street.
	 */
	Street getStreet(int id);

	/**
	 * Returns the Street with between two different intersection identified by
	 * given osmids.
	 *
	 * @param osmidStart Id of the starting intersection in the road network.
	 * @param osmidDest  Id of the destination intersection in the road network.
	 * @return The Java representation of the searched Street.
	 */
	Street getStreet(long osmidStart, long osmidDest);

	/**
	 * Returns all streets starting from the Intersection with the given Id.
	 *
	 * @param osmid Id of the Intersection to find.
	 * @return An HashMap of the street starting from the Intersection, the Key is
	 *         the id of the Street and the Value is the Street with the
	 *         corresponding Id.
	 */
	HashMap<Integer, Street> getStreets(long osmid);

	/**
	 * Returns the geometry of a Street in Coordinate
	 *
	 * @param osmidStart Id of the starting Intersection in the road network.
	 * @param osmidDest  Id of the destination Intersection in the road network.
	 * @return an ordered ArrayList of Coordinate that identifies Intersections that
	 *         belong to the street.
	 */
	ArrayList<Coordinate> getStreetGeometry(long osmidStart, long osmidDest);


	/**
	 * This method close the open connection and delete driver.
	 */
	void closeConnection();

	/**
	 * This method instantiate a new neo4j driver and starts a connection with
	 * credentials user and password given by constructor.
	 */
	void openConnection();

	/**
	 * Returns the shortest path from an Intersection to another, both specified by
	 * given vertexKeys, avoiding interrupted streets.
	 *
	 * @param osmidStart Id of the starting Intersection in the road network.
	 * @param osmidDest  Id of the destination Intersection in the road network.
	 * @return an ordered ArrayList of Intersections that belong
	 *         to the shortest path.
	 */
	ArrayList<Intersection> shortestPath(long osmidStart, long osmidDest);

	/**
	 * Returns the shortest path from an Intersection to another, both specified by
	 * given vertexKeys, avoiding interrupted streets.
	 * <p>
	 *
	 * @param osmidStart Id of the starting Intersection in the road network.
	 * @param osmidDest  Id of the destination Intersection in the road network.
	 * @return an ordered ArrayList of Coordinate that identifies Intersections that
	 *         belong to the shortest path.
	 */
	ArrayList<Coordinate> shortestPathCoordinate(long osmidStart, long osmidDest);

	/**
	 * Returns a list of top critical intersections ordered by betweenness
	 * centrality.
	 *
	 * @param top is the number of critical Intersections to display.
	 * @return an ArrayList of Intersection that identify the critical Intersections.
	 */
	ArrayList<Intersection> getTopCriticalNodes(int top);

	/**
	 * Returns a list of critical intersections that have a betweenness centrality
	 * greater than the indicated threshold.
	 *
	 * @param threshold is the value to compare.
	 * @return an arrayList of Intersection that identify the critical Intersections.
	 */
	ArrayList<Intersection> getThresholdCriticalNodes(double threshold);

	/**
	 * Returns the flow in an Intersection adding the weights of Street coming out
	 * of the intersection identified by given vertexKey.
	 *
	 * @param osmid Id of the intersection in the road network.
	 * @return the flow in given Intersection.
	 */
	double nodeFlow(long osmid);

	/**
	 * Returns linkKey of the street between two different intersection identified
	 * by given vertexKeys.
	 *
	 * @param osmidStart Id of the starting intersection in the road network.
	 * @param osmidDest  Id of the destination intersection in the road network.
	 * @return id of street.
	 */
	int getLinkKey(long osmidStart, long osmidDest);

	/**
	 * Querys the database in order to know the nearest intersection considering given position.
	 * @param position Coordinate in DecimalDegrees(DD).
	 * @return The Java representation of the searched Intersection.
	 */
	Intersection getNearestIntersection(Coordinate position);

	/**
	 * Querys the database in order to know the nearest intersection tagged as parking considering given position.
	 * @param position Coordinate in DecimalDegrees(DD).
	 * @return The Java representation of the searched Intersection.
	 */
	Intersection getNearestParking(Coordinate position);

	/**
	 * Querys the database in order to know the nearest intersection tagged as hospital considering given position.
	 * @param position Coordinate in DecimalDegrees(DD).
	 * @return The Java representation of the searched Intersection.
	 */
	Intersection getNearestHospital(Coordinate position) ;

	/**
	 * Querys the database in order to know the all the intersection tagged as parking.
	 * @return A list of Java representation of the tagged Intersection.
	 */
	ArrayList<Intersection> getAllParkings();

	/**
	 * Querys the database in order to know the all the intersection tagged as parking.
	 * @return A list of Java representation of the tagged Intersection.
	 */
	ArrayList<Intersection> getAllHospitals();


	//TODO
	double distanceShortestPathBus(long osmidStart, long osmidDest);

	/**
	 * Returns the shortest path from an Intersection to another, both specified by
	 * given vertexKeys, ignoring interrupted streets.
	 *
	 * @param osmidStart Id of the starting Intersection in the road network.
	 * @param osmidDest  Id of the destination Intersection in the road network.
	 * @return an ordered ArrayList of id that identifies Intersections that belong
	 *         to the shortest path.
	 */
	ArrayList<Intersection> shortestPathIgnoreInterrupted(long osmidStart, long osmidDest);

	/**
	 * Returns the shortest path from an Intersection to another, both specified by
	 * given vertexKeys, ignoring interrupted streets.
	 *
	 * @param osmidStart Id of the starting Intersection in the road network.
	 * @param osmidDest  Id of the destination Intersection in the road network.
	 * @return an ordered ArrayList of Coordinates that identifies Intersections that belong
	 *         to the shortest path.
	 */
	ArrayList<Coordinate> shortestPathCoordinateIgnoreInterrupted(long osmidStart, long osmidDest);

	/**
	 * Querys the database in order to know the timestamp of last update.
	 *
	 * @return Timestamp of last update.
	 */
	LocalDateTime getLastModified();

}