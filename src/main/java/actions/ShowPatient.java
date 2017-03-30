package actions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import enitites.Patient;
import mongo.MongoConnector;
import org.bson.Document;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by vlad on 16.03.2017.
 */
@WebServlet("/patient/show")
public class ShowPatient extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String patientDn = request.getParameter("patientDn");
        System.out.println(patientDn);

        MongoDatabase database = MongoConnector.getClient().getDatabase("spital");
        MongoCollection<Document> patientsCollection = database.getCollection("pacienti");

        Document result = patientsCollection.find(eq("dn", patientDn)).first();

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getWriter(),
                new Patient(result.get("nume") + " " + result.get("prenume"), patientDn));
    }
}
