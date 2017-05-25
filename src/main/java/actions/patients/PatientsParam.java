package actions.patients;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import command.*;
import connection.SecureCommunication;
import enitites.HttpPatient;
import enitites.Patient;
import mongo.MongoConnector;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.bson.Document;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by vlad on 13.04.2017.
 */
@WebServlet("/patients/*")
public class PatientsParam extends HttpServlet {
    public static ObjectMapper objectMapper = new ObjectMapper();

    //SET/UNSET SENSOR
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String patientId = getPatientFromRequest(request);
        System.out.println(patientId);
    }

    //GET PARAMS WITH
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String patientId = getPatientFromRequest(request);
        System.out.println(patientId);

        MongoDatabase database = MongoConnector.getClient().getDatabase("spital");
        MongoCollection<Document> patientsCollection = database.getCollection("patients");

        Document result = patientsCollection.find(eq("uid", patientId)).first();

        if (result != null) {
            objectMapper.writeValue(response.getWriter(),
                    new HttpPatient(patientId, result.getString("givenName"), result.getString("familyName")));
        } else {

        }

    }

    //DELETE PATIENT
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String patientId = getPatientFromRequest(request);
        System.out.println(patientId);

        System.out.println(patientId);
        Cookie[] cookies = request.getCookies();
        String requesterUid = SecureCommunication.getCookie(cookies, "uid");

        Command command = new Command();
        command.setType(CommandType.DELETE);
        command.setIssuer(requesterUid);
        command.setSubject(patientId);
        command.setTarget(patientId);
        command.setSeverity(CommandSeverity.AUTO_RESOLVE);
        command.setState(CommandState.PENDING);

        CloseableHttpResponse commandResponse;
        try {
            commandResponse = CommandActions.sendCommand(command);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new ServletException();
        }
        System.out.println(commandResponse.toString());
    }

    private String getPatientFromRequest(HttpServletRequest request) {
        String[] pathInfo = request.getPathInfo().split("/");
        return pathInfo[1];
    }
}