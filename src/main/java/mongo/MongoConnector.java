package mongo;

import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;

/**
 * Created by vlad on 16.03.2017.
 */
public class MongoConnector {
    private static MongoClient mongoClient;

    public static MongoClient getClient() {
        if (mongoClient == null) {
            mongoClient = new MongoClient("localhost", 27017);
        }

        return mongoClient;
    }

    public static List<Document> getActiveSensors(String dnPacient) {
        MongoDatabase database = getClient().getDatabase("spital");
        MongoCollection<Document> sensorsCollection = database.getCollection("senzori");

        List<Document> sensors = new ArrayList<>();
        MongoCursor<Document> result = sensorsCollection.find(eq("dn_pacient", dnPacient)).iterator();

        while (result.hasNext()) {
            sensors.add(result.next());
        }

        return sensors;
    }

    public static List<Document> getAllInactiveSensors(String location) {
        MongoDatabase database = getClient().getDatabase("spital");
        MongoCollection<Document> sensorsCollection = database.getCollection("senzori");

        List<Document> inactiveSensors = new ArrayList<>();
        Document inactiveSensorsCondition = new Document("$match", new Document("activ", false));

        List<Document> query = Arrays.asList(inactiveSensorsCondition,
                new Document("$group", new Document("_id",  "$tip_senzor").
                        append("serial_no", new Document("$push", "$serial_no"))));

        MongoCursor<Document> result = sensorsCollection.aggregate(query).iterator();

        while(result.hasNext()) {
            Document entry = result.next();
            inactiveSensors.add(entry);
        }

        return inactiveSensors;
    }

    public static void setSensor(String patientDn, String serialNo) {
        MongoDatabase database = getClient().getDatabase("spital");
        MongoCollection<Document> sensorsCollection = database.getCollection("senzori");

        sensorsCollection.updateOne(new Document("serial_no", Integer.parseInt(serialNo)),
                new Document("$set", new Document("activ", true).append("dn_pacient", patientDn)));
    }

    public static void removeSensor(String serialNo) {
        MongoDatabase database = getClient().getDatabase("spital");
        MongoCollection<Document> sensorsCollection = database.getCollection("senzori");

        sensorsCollection.updateOne(new Document("serial_no", Integer.parseInt(serialNo)),
                new Document("$set", new Document("activ", false)).append("$unset", new Document("dn_pacient","")));
    }
}
