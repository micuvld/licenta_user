package actions;

import com.fasterxml.jackson.databind.ObjectMapper;
import mongo.MongoConnector;
import org.bson.Document;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by vlad on 16.03.2017.
 */

@WebServlet("/sensors")
public class GetSensors extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String status = request.getParameter("status");
        List<Document> sensors;

        if (status.equals("inactive")) {
            String location = request.getParameter("location");

            sensors = MongoConnector.getAllInactiveSensors(location);
        } else {
            String patientDn = request.getParameter("patientDn");

            sensors = MongoConnector.getActiveSensors(patientDn);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(sensors));
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
