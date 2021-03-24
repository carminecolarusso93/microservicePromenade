package com.gmail.carminecolarusso93.data.databaseDriver;


import com.gmail.carminecolarusso93.data.dataModel.*;
import org.jboss.logging.Logger;
import org.neo4j.driver.*;
import org.neo4j.driver.exceptions.NoSuchRecordException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DAOUserNeo4jImpl implements DAOUser {

    private String uri, user, password;
    private Driver driver;
    private org.jboss.logging.Logger logger;
    private SessionConfig readSessionConfig;
//    private final SessionConfig writeSessionConfig;


    /**
     * @param uri      is the bolt address to access neo4j database.
     * @param user     is the username to access neo4j database.
     * @param password is the password to access neo4j database.
     */
    public DAOUserNeo4jImpl(String uri, String user, String password) {
        logger = Logger.getLogger(DAOUserNeo4jImpl.class);
        System.out.println();
        logger.info("DAOUserNeo4jImpl.DAOUserNeo4jImpl: uri = " + uri + ", user = " + user + ", password = " + password);
        this.uri = uri;
        this.user = user;
        this.password = password;
        this.driver = null;
        this.readSessionConfig = SessionConfig.builder()
                .withDefaultAccessMode(AccessMode.READ)
                .build();
    }

    @Override
    public void openConnection() {
        logger.info("DAOUserNeo4jImpl.openConnection");
        logger.info("Opening Connection to DataBase URI[" + uri + "]");
        this.driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    @Override
    public void closeConnection() {
        logger.info("DAOUserNeo4jImpl.closeConnection");
        logger.info("Closing Connection to DataBase URI[" + uri + "]");
        // Logica bloccante
        // driver.close();

        // Logica non bloccante
        driver.closeAsync();
        driver = null;
    }

    public Result databaseRead(String query) {
        logger.info("DAOUserNeo4jImpl.databaseRead:query = " + query);
        try {
            if (driver == null)
                throw new DatabaseNotConnectException("Database Non Connesso");
            Session session = driver.session(readSessionConfig);
            return session.run(query);
        } catch (DatabaseNotConnectException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Executed Query <br>
     * MATCH (a:Intersection {osmid: osmid}) RETURN properties(a)
     */
    @Override
    public Intersection getIntersection(long osmid) {
        logger.info("DAOUserNeo4jImpl.getIntersection: osmid = " + osmid);

        String query = "MATCH (a:Intersection {osmid:" + osmid + "}) RETURN properties(a)";


        Record resultRecord = databaseRead(query).single();
        Value v = resultRecord.get("properties(a)");

        return convertIntersection(v);

    }

    /**
     * Executed Query <br>
     * MATCH (a:Intersection {osmid: osmid}) RETURN properties(a)
     */
    @Override
    public Intersection getIntersectionLight(long osmid) {
        logger.info("DAOUserNeo4jImpl.getIntersectionLight: osmid = " + osmid);

        String query = "MATCH (a:Intersection {osmid:" + osmid + "}) RETURN properties(a)";


        Record resultRecord = databaseRead(query).single();
        try {
            Value v = resultRecord.get("properties(a)");

            return convertIntersection(v);
        } catch (NoSuchRecordException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Executed Query <br>
     * MATCH (a:Intersection{osmid: osmidS})-[r:STREET]->(b:Intersection{osmid: osmidD}) RETURN properties(r)
     */
    @Override
    public Street getStreet(long osmidS, long osmidD) {
        logger.info("DAOUserNeo4jImpl.getStreet: osmidS = " + osmidS + ", osmidD = " + osmidD);
        // TEST NODES 13445152 3991897787
        String query = "MATCH (a:Intersection{osmid:" + osmidS + "})-[r:STREET]->(b:Intersection{osmid:" + osmidD
                + "}) RETURN properties(r)";

        Result result = databaseRead(query);
        Record r = result.next();

        Value v = r.get("properties(r)");

        String sCord = v.get("coordinates").asString();

        ArrayList<Coordinate> coordinates = getCoordinateList(sCord);

        long id = v.get("id").asLong();
        double length = v.get("length").asDouble();
        double ffs = v.get("ffs").asDouble();
        String speedlimit = v.get("speedlimit").asString();
        long from = v.get("from").asLong();
        long to = v.get("to").asLong();
        String area = v.get("area").asString();
        String name = v.get("name").asString();
        double weight = v.get("weight").asDouble();

        return new Street(id, length, ffs, speedlimit, from, to, area, name, coordinates, weight);

    }


    /**
     * Executed Query <br>
     * MATCH (a:Intersection{osmid: osmidS})-[r:STREET]->(b:Intersection{osmid: osmidD}) RETURN r.coordinates
     */
    @Override
    public ArrayList<Coordinate> getStreetGeometry(long osmidS, long osmidD) {
        logger.info("DAOUserNeo4jImpl.getStreetGeometry: osmidS = " + osmidS + ", osmidD = " + osmidD);
        String query = "MATCH (a:Intersection{osmid:" + osmidS + "})-[r:STREET]->(b:Intersection{osmid:" + osmidD
                + "}) RETURN r.coordinates";

        String sCord = databaseRead(query).list().get(0).get("r.coordinates").asString();
        return getCoordinateList(sCord);
    }

    /**
     * Executed Query <br>
     * MATCH (a:Intersection{osmid: osmidS})-[r:STREET]->(b:Intersection{osmid: osmidD}) RETURN properties(r)
     */
    @Override
    public Street getStreet(int id) {
        logger.info("DAOUserNeo4jImpl.getStreet: id = " + id);

        String query = "MATCH (a:Intersection)-[r:STREET]->(b:Intersection) WHERE r.id=" + id + " RETURN properties(r)";
        Result result = databaseRead(query);
        Record r = result.single();
        Value v = r.get("properties(r)");

        String sCord = v.get("coordinates").asString();

        ArrayList<Coordinate> coordinates = getCoordinateList(sCord);

        long idr = v.get("id").asLong();
        double length = v.get("length").asDouble();
        double ffs = v.get("ffs").asDouble();
        String speedlimit = v.get("speedlimit").asString();
        long from = v.get("from").asLong();
        long to = v.get("to").asLong();
        String area = v.get("areaname").asString();
        String name = v.get("name").asString();
        double weight = v.get("weight").asDouble();

        return new Street(idr, length, ffs, speedlimit, from, to, area, name, coordinates, weight);

    }

    private ArrayList<Coordinate> getCoordinateList(String sCord) {
        logger.info("DAOUserNeo4jImpl.getCoordinateList: sCord = " + sCord);

        sCord = sCord.replaceAll("Coordinate ", "");
        sCord = sCord.replaceAll("\\[longitude\\=", "");
        sCord = sCord.replaceAll("latitude\\=", "");
        sCord = sCord.replaceAll("\\],", ";");
        sCord = sCord.replace(" ", "");
        sCord = sCord.replaceAll("\\]", "");

        String[] split = sCord.split(";");
        String[] split2;
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        Coordinate c;
        for (String s : split) {
            split2 = s.split(",");
            c = new Coordinate(Double.parseDouble(split2[0]), Double.parseDouble(split2[1]));
            coordinates.add(c);
        }
        return coordinates;
    }

    /**
     * Executed Query <br>
     * MATCH (a:Intersection{osmid: osmidS})-[r:STREET]->(b:Intersection{osmid: osmidD}) RETURN collect(r.id) as ids
     */
    @Override
    public HashMap<Integer, Street> getStreets(long osmid) {
        logger.info("DAOUserNeo4jImpl.getStreets: osmid = " + osmid);

        String query = "MATCH (a:Intersection {osmid:" + osmid + "})-[r:STREET]->(b:Intersection)"
                + " RETURN collect(r.id) as ids";
        Result result = databaseRead(query);

        HashMap<Integer, Street> strade = new HashMap<>();
        List<Object> ids = result.single().get("ids").asList();
        for (Object o : ids) {
            int id = ((Long) o).intValue();
            Street s = getStreet(id);
            strade.put(id, s);
        }
        return strade;
    }


    // SERVICE METHODS

    /**
     * Executed Query
     * MATCH (start:Intersection{osmid: osmidStart}), (end:Intersection{osmid: osmidDest})<br>
     * CALL algo.shortestPath.stream(start, end, 'weight',{direction:'OUTGOING'})<br>
     * YIELD nodeId, cost <br>
     * RETURN algo.asNode(nodeId).osmid as vertexKeys
     */
    @Override
    public ArrayList<Coordinate> shortestPathCoordinate(long osmidStart, long osmidDest) {
        logger.info("DAOUserNeo4jImpl.shortestPathCoordinateIgnoreInterrupted: osmidStart = " + osmidStart + ", osmidDest = " + osmidDest);

        String query = "MATCH (start:Intersection{osmid:" + osmidStart + "}), (end:Intersection{osmid:" + osmidDest
                + "})\r\n" + "CALL algo.shortestPath.stream(start, end, 'weight',{direction:'OUTGOING'})\r\n"
                + "YIELD nodeId, cost\r\n" + "RETURN algo.asNode(nodeId).osmid as vertexKeys";
        Result result = databaseRead(query);
        ArrayList<Long> shortestPath = new ArrayList<>();
        Record r;
        long vertexKey;
        while (result.hasNext()) {
            r = result.next();
            vertexKey = r.get("vertexKeys").asLong();

            shortestPath.add(vertexKey);
        }
        ArrayList<Coordinate> coordstmp = new ArrayList<>();
        ArrayList<Coordinate> coords = new ArrayList<>();

        for (int i = 0; i < shortestPath.size() - 1; i++) {
            coordstmp = getStreetGeometry(shortestPath.get(i), shortestPath.get(i + 1));
            coords.addAll(coordstmp);
        }
        return coords;

    }

    /**
     * Executed Query
     * MATCH (start:Intersection{osmid: osmidStart}), (end:Intersection{osmid: osmidDest})<br>
     * CALL algo.shortestPath.stream(start, end, 'weight',{direction:'OUTGOING'})<br>
     * YIELD nodeId, cost <br>
     * RETURN algo.asNode(nodeId).osmid as vertexKeys
     */
    @Override
    public ArrayList<Intersection> shortestPath(long osmidStart, long osmidDest) {
        logger.info("DAOUserNeo4jImpl.shortestPathIgnoreInterrupted: osmidStart = " + osmidStart + ", osmidDest = " + osmidDest);

        String query = "MATCH (start:Intersection{osmid:" + osmidStart + "}), (end:Intersection{osmid:" + osmidDest
                + "})\r\n" + "CALL algo.shortestPath.stream(start, end, 'weight',{direction:'OUTGOING'})\r\n"
                + "YIELD nodeId, cost\r\n" + "RETURN algo.asNode(nodeId) as intersections";

        Result result = databaseRead(query);
        return extractIntersectionArrayList(result);
    }


    private ArrayList<Intersection> extractIntersectionArrayList(Result result) {
        logger.info("DAOUserNeo4jImpl.extractIntersectionArrayList: result = " + result);

        ArrayList<Intersection> shortestPath = new ArrayList<>();
        for (Record r : result.list()) {
            Map<String, Object> mapValues = r.get("intersections").asMap();
            long osmid = (long) mapValues.get("osmid");
            double betweenness = (double) mapValues.get("betweenness");
            double latitude = (double) mapValues.get("latitude");
            double longitude = (double) mapValues.get("longitude");

            Intersection i = new Intersection(new Coordinate(longitude, latitude), osmid, betweenness);
            shortestPath.add(i);
        }
        return shortestPath;
    }

    /**
     * Executed Query
     * MATCH (i:Intersection) RETURN properties(i) ORDER BY i.betweenness DESC LIMIT top
     */
    @Override
    public ArrayList<Intersection> getTopCriticalNodes(int top) {
        logger.info("DAOUserNeo4jImpl.getTopCriticalNodes: top = " + top);
        String query = "MATCH (i:Intersection) RETURN properties(i) ORDER BY i.betweenness DESC LIMIT " + top;
        Result result = databaseRead(query);
        return convertToIntersectionArrayList(result, "i");
    }

    /**
     * MATCH (i:Intersection) WHERE i.betweenness > threshold RETURN properties(i)
     */
    @Override
    public ArrayList<Intersection> getThresholdCriticalNodes(double threshold) {
        logger.info("DAOUserNeo4jImpl.getThresholdCriticalNodes: threshold = " + threshold);
        String query = "MATCH (i:Intersection) WHERE i.betweenness > " + threshold + " RETURN properties(i)";
        Result result = databaseRead(query);
        return convertToIntersectionArrayList(result, "i");

    }

    /**
     * Executed Query
     * MATCH (a:Intersection {osmid: osmid}) RETURN a.betweenness as flow
     */
    @Override
    public double nodeFlow(long osmid) {
        logger.info("DAOUserNeo4jImpl.nodeFlow: osmid = " + osmid);
        String query = "MATCH (a:Intersection {osmid:" + osmid + "}) RETURN a.betweenness as flow";
        Result result = databaseRead(query);

        return result.single().get("flow").asDouble();
    }

    @Override
    public int getLinkKey(long osmidStart, long osmidDest) {
        logger.info("DAOUserNeo4jImpl.getLinkKey: osmidStart = " + osmidStart + ", osmidDest = " + osmidDest);
        String query = "MATCH (a:Intersection{osmid:" + osmidStart + "})-[r:STREET]->(b:Intersection{osmid:" + osmidDest
                + "}) RETURN r.id";
        Result result = databaseRead(query);

        return result.single().get("r.id").asInt();
    }

    @Override
    public Intersection getNearestIntersection(Coordinate position) {
        logger.info("DAOUserNeo4jImpl.getNearestIntersection: position = " + position);

        String query = "MATCH (t:Intersection)  " + "WITH t , distance(point({ longitude: " + position.getLongitude()
                + ", latitude: " + position.getLatitude()
                + " }), point({ longitude: t.longitude, latitude: t.latitude })) AS distance " + "order by distance "
                + "return properties(t), distance  LIMIT 1";

        Result result = databaseRead(query);
        Record r = result.single();

        double dist = r.get("distance").asDouble();
        if (dist > 10000) {
            return null; // TODO verifica
        }
        Value v = r.get("properties(t)");

        return convertIntersection(v);
    }


    private ArrayList<Intersection> convertToIntersectionArrayList(Result result, String nodeName) {
        logger.info("DAOUserNeo4jImpl.convertToIntersectionArrayList: result = " + result + ", nodeName = " + nodeName);
        ArrayList<Intersection> intersections = new ArrayList<>();
        Record r;
        Value v;
        while (result.hasNext()) {
            r = result.next();
            v = r.get("properties(" + nodeName + ")");
            Coordinate c = new Coordinate(v.get("longitude").asDouble(), v.get("latitude").asDouble());

            long id = v.get("osmid").asLong();
            double betweenness = v.get("betweenness").asDouble();

            intersections.add(new Intersection(c, id, betweenness));
        }
        return intersections;
    }

    private Intersection convertIntersection(Value r) {
        logger.info("DAOUserNeo4jImpl.convertIntersection: r = " + r);

        try {
            // String nome = r.get("name").asString();
            Coordinate c = new Coordinate(r.get("longitude").asDouble(), r.get("latitude").asDouble());

            long id = r.get("osmid").asLong();
            double betweenness = r.get("betweenness").asDouble();

            return new Intersection(c, id, betweenness);
        } catch (NoSuchRecordException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public LocalDateTime getLastModified() {
        logger.info("DAOAdminNeo4jImpl.getLastModified");
        Result result = databaseRead("MATCH (a:Control) Return a.timestamp");
        Record r = result.single();
        LocalDateTime ldt = r.get("a.timestamp").asLocalDateTime();
        return ldt;
    }
}
