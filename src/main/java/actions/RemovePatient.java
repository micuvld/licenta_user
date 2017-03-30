package actions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import connect.LdapConnector;
import mongo.MongoConnector;
import org.bson.Document;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by vlad on 16.03.2017.
 */

@WebServlet("/patient/remove")
public class RemovePatient extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String patientDn = request.getParameter("patientDn");
        System.out.println(patientDn);

        HttpSession session = request.getSession();
        LdapConnector ldapConnector = (LdapConnector) session.getAttribute("connector");
        ldapConnector.removePatient(patientDn);
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
