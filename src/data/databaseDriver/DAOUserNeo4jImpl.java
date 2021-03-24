package data.databaseDriver;

import data.dataModel.Coordinate;
import data.dataModel.Intersection;
import data.dataModel.Street;
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

        int ids = v.get("id").asInt();
        String access = v.get("access").asString();
        String area = v.get("area").asString();
        String bridge = v.get("bridge").asString();
        long osmidStart = v.get("osmidStart").asLong();
        long osmidDest = v.get("osmidDest").asLong();
        String highway = v.get("highway").asString();
        String junction = v.get("junction").asString();
        int key = v.get("key").asInt();
        List<Object> listLanes = v.get("arrayLanes").asList();
        ArrayList<Integer> arrayLanes = new ArrayList<>();
        for (Object o : listLanes) {
            arrayLanes.add(((Long) o).intValue());
        }

        double length = v.get("length").asDouble();
        String maxSpeed = v.get("maxSpeed").asString();
        String name = v.get("name").asString();
        boolean oneWay = v.get("oneWay").asBoolean();
        List<Object> listEdges = v.get("osmidEdges").asList();
        ArrayList<Long> osmidEdges = new ArrayList<>();
        for (Object o : listEdges) {
            osmidEdges.add((Long) o);
        }
        String ref = v.get("ref").asString();
        boolean transportService = v.get("transportService").asBoolean();
        String tunnel = v.get("tunnel").asString();
        String width = v.get("width").asString();
        long origId = v.get("origId").asLong();
        double weight = v.get("weight").asDouble();
        double flow = v.get("flow").asDouble();
        double averageTravelTime = v.get("averageTravelTime").asDouble();
        boolean interrupted = v.get("interrupted").asBoolean();

        return new Street(coordinates, ids, access, area, bridge, osmidStart, osmidDest, highway, junction, key,
                arrayLanes, length, maxSpeed, name, oneWay, osmidEdges, ref, transportService, tunnel, width, origId,
                weight, flow, averageTravelTime, interrupted);

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

        int ids = v.get("id").asInt();
        String access = v.get("access").asString();
        String area = v.get("area").asString();
        String bridge = v.get("bridge").asString();
        long osmidStart = v.get("osmidStart").asLong();
        long osmidDest = v.get("osmidDest").asLong();
        String highway = v.get("highway").asString();
        String junction = v.get("junction").asString();
        int key = v.get("key").asInt();
//		List<Object> listLanes = v.get("arrayLanes").asList();
        ArrayList<Integer> arrayLanes = new ArrayList<>();
        // TODO
        // for (Object o : listLanes) {
        // arrayLanes.add(((Long) o).intValue());
        // }

        double length = v.get("length").asDouble();
        String maxSpeed = v.get("maxSpeed").asString();
        String name = v.get("name").asString();
        boolean oneWay = v.get("oneWay").asBoolean();
//		List<Object> listEdges = v.get("osmidEdges").asList();
        // TODO
        ArrayList<Long> osmidEdges = new ArrayList<>();
//		for (Object o : listEdges) {
//			osmidEdges.add((Long) o);
//		}
        String ref = v.get("ref").asString();
        boolean transportService = v.get("transportService").asBoolean();
        String tunnel = v.get("tunnel").asString();
        String width = v.get("width").asString();
        long origId = v.get("origId").asLong();
        double weight = v.get("weight").asDouble();
        double flow = v.get("flow").asDouble();
        double averageTravelTime = v.get("averageTravelTime").asDouble();
        boolean interrupted = v.get("interrupted").asBoolean();

        return new Street(coordinates, ids, access, area, bridge, osmidStart, osmidDest, highway, junction, key,
                arrayLanes, length, maxSpeed, name, oneWay, osmidEdges, ref, transportService, tunnel, width, origId,
                weight, flow, averageTravelTime, interrupted);

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
     * Executed Query <br>
     * MATCH (start:Intersection{osmid: osmidStart}), (end:Intersection{osmid: osmidDest}) <br>
     * CALL algo.shortestPath.stream(start, end, 'weight',{direction:'OUTGOING',
     * nodeQuery:'MATCH(i:Intersection) RETURN id(i) as id',
     * relationshipQuery:'MATCH(a:Intersection)-[s:STREET{interrupted:false}]->(b:Intersection) RETURN id(a) as source, id(b) as target, s.weight as weight', graph:'cypher'})
     * YIELD nodeId<br>
     * RETURN algo.asNode(nodeId).osmid as vertexKey;
     */
    @Override
    public ArrayList<Intersection> shortestPath(long osmidStart, long osmidDest) {
        logger.info("DAOUserNeo4jImpl.shortestPath: osmidStart = " + osmidStart + ", osmidDest = " + osmidDest);
        String query = "MATCH (start:Intersection{osmid:" + osmidStart + "}), (end:Intersection{osmid:" + osmidDest
                + "})\r\n"
                + "CALL algo.shortestPath.stream(start, end, 'weight',{direction:'OUTGOING', nodeQuery:'MATCH(i:Intersection) RETURN id(i) as id',\r\n"
                + "relationshipQuery:'MATCH(a:Intersection)-[s:STREET{interrupted:false}]->(b:Intersection) RETURN id(a) as source, id(b) as target, s.weight as weight', graph:'cypher'})"
                + "YIELD nodeId\r\n" + "RETURN algo.asNode(nodeId) as intersections";

        Result result = databaseRead(query);
        return extractIntersectionArrayList(result);
    }

    /**
     * Executed Query <br>
     * MATCH (start:Intersection{osmid: osmidStart}), (end:Intersection{osmid: osmidDest}) <br>
     * CALL algo.shortestPath.stream(start, end, 'weight',{direction:'OUTGOING',
     * nodeQuery:'MATCH(i:Intersection) RETURN id(i) as id',
     * relationshipQuery:'MATCH(a:Intersection)-[s:STREET{interrupted:false}]->(b:Intersection) RETURN id(a) as source, id(b) as target, s.weight as weight', graph:'cypher'})
     * YIELD nodeId<br>
     * RETURN algo.asNode(nodeId).osmid as vertexKeys
     */
    @Override
    public ArrayList<Coordinate> shortestPathCoordinate(long osmid1, long osmid2) {
        logger.info("DAOUserNeo4jImpl.shortestPathCoordinate: osmid1 = " + osmid1 + ", osmid2 = " + osmid2);

        String query = "MATCH (start:Intersection{osmid:" + osmid1 + "}), (end:Intersection{osmid:" + osmid2 + "})\r\n"
                + "CALL algo.shortestPath.stream(start, end, 'weight',{direction:'OUTGOING', nodeQuery:'MATCH(i:Intersection) RETURN id(i) as id',\r\n"
                + "relationshipQuery:'MATCH(a:Intersection)-[s:STREET{interrupted:false}]->(b:Intersection) RETURN id(a) as source, id(b) as target, s.weight as weight', graph:'cypher'})"
                + "YIELD nodeId\r\n" + "RETURN algo.asNode(nodeId).osmid as vertexKeys";

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
    public ArrayList<Coordinate> shortestPathCoordinateIgnoreInterrupted(long osmidStart, long osmidDest) {
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
    public ArrayList<Intersection> shortestPathIgnoreInterrupted(long osmidStart, long osmidDest) {
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
            boolean parking = (boolean) mapValues.get("parking");
            boolean busStop = (boolean) mapValues.get("busStop");
            boolean hospital = (boolean) mapValues.get("hospital");
            boolean museum = (boolean) mapValues.get("museum");
            String highway = (String) mapValues.get("highway");
            String ref = (String) mapValues.get("ref");
            Intersection i = new Intersection(new Coordinate(longitude, latitude), highway, osmid, ref, betweenness, parking, hospital, busStop, museum);
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

    @Override
    public Intersection getNearestParking(Coordinate position) {
        logger.info("DAOUserNeo4jImpl.getNearestParking: position = " + position);

        String query = "with " + position.getLatitude() + " as lat , " + position.getLongitude()
                + " as lon, 0.7 as range " + "match (t:Intersection {parking : true}) "
                + "with (lon - t.longitude) as lenX, (lat - t.latitude) as lenY, t.osmid as osmid, t "
                // + "where lenX < range and lenX > -range and lenY < range and lenY > -range "
                + "with sqrt((lenX*lenX)+(lenY*lenY)) as distance, lenX, lenY, osmid, t  " + "order by distance "
                + "return  osmid, distance, properties(t) limit 1";
        Result result = databaseRead(query);
        Record r = result.single();

        double dist = r.get("distance").asDouble();

        if (dist > 1000) {
            return null; // TODO verifica
        }
        Value v = r.get("properties(t)");

        return convertIntersection(v);
    }

    public Intersection getNearestHospital(Coordinate position) {
        logger.info("DAOUserNeo4jImpl.getNearestHospital: position = " + position);
        String query = "with " + position.getLatitude() + " as lat , " + position.getLongitude()
                + " as lon, 0.7 as range " + "match (t:Intersection {hospital : true}) "
                + "with (lon - t.longitude) as lenX, (lat - t.latitude) as lenY, t.osmid as osmid, t "
                // + "where lenX < range and lenX > -range and lenY < range and lenY > -range "
                + "with sqrt((lenX*lenX)+(lenY*lenY)) as distance, lenX, lenY, osmid, t  " + "order by distance "
                + "return  osmid, distance, properties(t) limit 1";

        Result result = databaseRead(query);
        Record r = result.single();

        double dist = r.get("distance").asDouble();
        /*
         * if (dist > 1000) { return null; // TODO verifica }
         */
        Value v = r.get("properties(t)");

        return convertIntersection(v);
    }

    @Override
    public ArrayList<Intersection> getAllParkings() {
        logger.info("DAOUserNeo4jImpl.getAllParkings");

        String query = "match (i:Intersection {parking:true}) return properties(i)";
        Result result = databaseRead(query);
        return convertToIntersectionArrayList(result, "i");
    }

    @Override
    public ArrayList<Intersection> getAllHospitals() {
        logger.info("DAOUserNeo4jImpl.getAllHospitals");

        String query = "match (i:Intersection {hospital:true}) return properties(i)";
        Result result = databaseRead(query);
        return convertToIntersectionArrayList(result, "i");
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

            String highway = v.get("highway").asString();
            long id = v.get("osmid").asLong();
            String ref = v.get("ref").asString();
            double betweenness = v.get("betweenness").asDouble();
            boolean parking = v.get("parking").asBoolean();
            boolean hospital = v.get("hospital").asBoolean();
            boolean busStop = v.get("busStop").asBoolean();
            boolean museum = v.get("museum").asBoolean();

            intersections.add(new Intersection(c, highway, id, ref, betweenness, parking, hospital, busStop, museum));
        }
        return intersections;
    }

    private Intersection convertIntersection(Value r) {
        logger.info("DAOUserNeo4jImpl.convertIntersection: r = " + r);

        try {

            // String nome = r.get("name").asString();
            Coordinate c = new Coordinate(r.get("longitude").asDouble(), r.get("latitude").asDouble());

            String highway = r.get("highway").asString();
            long id = r.get("osmid").asLong();
            String ref = r.get("ref").asString();

            double betweenness = r.get("betweenness").asDouble();

            boolean parking = r.get("parking").asBoolean();
            boolean hospital = r.get("hospital").asBoolean();
            boolean busStop = r.get("busStop").asBoolean();
            boolean museum = r.get("museum").asBoolean();

            return new Intersection(c, highway, id, ref, betweenness, parking, hospital, busStop, museum);
        } catch (NoSuchRecordException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public double distanceShortestPathBus(long osmidStart, long osmidDest) {
        logger.info("DAOUserNeo4jImpl.distanceShortestPathBus: osmidStart = " + osmidStart + ", osmidDest = " + osmidDest);

        String query = "MATCH (start:Intersection{osmid:" + osmidStart + "}), (end:Intersection{osmid:" + osmidDest
                + "})\r\n"
                + "CALL algo.shortestPath.stream(start, end, 'weight',{direction:'OUTGOING', nodeQuery:'MATCH(i:Intersection) RETURN id(i) as id',\r\n"
                + "relationshipQuery:'MATCH(a:Intersection)-[s:STREET{interrupted:false}]->(b:Intersection) RETURN id(a) as source, id(b) as target, s.weight as weight', graph:'cypher'})"
                + "YIELD nodeId, cost\r\n" + "RETURN algo.asNode(nodeId).osmid as vertexKeys, cost order by cost desc limit 1";

        Result result = databaseRead(query);

        Record r = result.single();
        return r.get("cost").asDouble();
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
