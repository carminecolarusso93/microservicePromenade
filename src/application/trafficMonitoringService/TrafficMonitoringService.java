package application.trafficMonitoringService;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;

import data.dataModel.*;
import data.databaseDriver.*;
import org.jboss.logging.Logger;
import util.ServerUtilities;

/**
 * Session Bean implementation class BigServiceController
 * <p>
 * Encapsulates the driver to access the traffic management database with common user role.
 */

@Stateless
public class TrafficMonitoringService implements TrafficMonitoringServiceLocal, TrafficMonitoringServiceRemote{

	DAOUser database;
	protected String databeseURI = null;
	protected String databaseUser = null;
	protected String databasePass = null;
	Logger logger;

	/**
	 * Default constructor.
	 * <p>
	 * Instantiate a driver to access the Neo4j Graph Database for common user with default bolt uri and credentials.
	 * @throws FileNotFoundException 
	 */
	public TrafficMonitoringService() {
		logger = Logger.getLogger(TrafficMonitoringService.class);
		logger.info("TrafficMonitoringService.TrafficMonitoringService");
		try {
			ServerUtilities serverUtilities = new ServerUtilities();
			this.databeseURI = serverUtilities.getDatabaseReplicaUri();
			this.databaseUser = serverUtilities.getDatabaseReplicaUser();
			this.databasePass = serverUtilities.getDatabaseReplicaPass();
			database = new DAOUserNeo4jImpl(databeseURI, databaseUser, databasePass);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Called after the EJB construction.
	 * Open the connection to the database.
	 */
	@PostConstruct
	public void connect() {
		logger.info("TrafficMonitoringService.connect");
		database.openConnection();
	}
	
	/**
	 * Called before the EJB destruction.
	 * Close the connection to the database.
	 */
	@PreDestroy
	public void preDestroy() {
		logger.info("TrafficMonitoringService.preDestroy");
		database.closeConnection();
	}

	@Override
	public ArrayList<Intersection> getTopCriticalNodes(int top) {
		logger.info("TrafficMonitoringService.getTopCriticalNodes: top = " + top);
		return database.getTopCriticalNodes(top);
	}

	@Override
	public ArrayList<Intersection> getThresholdCriticalNodes(double threshold) {
		logger.info("TrafficMonitoringService.getThresholdCriticalNodes: threshold = " + threshold);
		return database.getThresholdCriticalNodes(threshold);
	}

	@Override
	public double nodeFlow(long osmid) {
		logger.info("TrafficMonitoringService.nodeFlow: osmid = " + osmid);
		return database.nodeFlow(osmid);
	}
	
	@Override
	public Intersection getIntersection(long osmid) {
		logger.info("TrafficMonitoringService.getIntersection: osmid = " + osmid);
		return database.getIntersectionLight(osmid);
	}

	@Override
	public Street getStreet(int id) {
		logger.info("TrafficMonitoringService.getStreet: id = " + id);
		return database.getStreet(id);
	}
	
	@Override
	public int getLinkKey(long osmidStart, long osmidDest) {
		logger.info("TrafficMonitoringService.getLinkKey: osmidStart = " + osmidStart + ", osmidDest = " + osmidDest);
		return database.getLinkKey(osmidStart, osmidDest);
	}

	@Override
	public ArrayList<Intersection> shortestPath(long osmidStart, long osmidDest) {
		logger.info("TrafficMonitoringService.shortestPath: osmidStart = " + osmidStart + ", osmidDest = " + osmidDest);
		return database.shortestPath(osmidStart, osmidDest);
	}

	@Override
	public ArrayList<Coordinate> shortestPathCoordinate(long osmidStart, long osmidDest) {
		logger.info("TrafficMonitoringService.shortestPathCoordinate: osmidStart = " + osmidStart + ", osmidDest = " + osmidDest);
		return database.shortestPathCoordinate(osmidStart, osmidDest);
	}

	@Override
	public Intersection getNearestIntersection(Coordinate position){
		logger.info("TrafficMonitoringService.getNearestIntersection: position = " + position);
		return database.getNearestIntersection(position);
	}

	@Override
	public ArrayList<Coordinate> shortestPathCoordinateIgnoreInterrupted(long osmidStart, long osmidDest) {
		logger.info("TrafficMonitoringService.shortestPathCoordinateIgnoreInterrupted: osmidStart = " + osmidStart + ", osmidDest = " + osmidDest);
		return database.shortestPathCoordinateIgnoreInterrupted(osmidStart,osmidDest);
	}

	@Override
	public ArrayList<Intersection> shortestPathIgnoreInterrupted(long osmidStart, long osmidDest) {
		logger.info("TrafficMonitoringService.shortestPathIgnoreInterrupted: osmidStart = " + osmidStart + ", osmidDest = " + osmidDest);
		return database.shortestPathIgnoreInterrupted(osmidStart, osmidDest);
	}

}
