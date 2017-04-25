package actions.patients;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.*;
import connection.SecureCommunication;
import enitites.HttpPatient;
import org.apache.http.client.methods.CloseableHttpResponse;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Created by vlad on 13.04.2017.
 */
@WebServlet("/patients/add")
public class AddPatient extends HttpServlet {
    public static ObjectMapper objectMapper = new ObjectMapper();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpPatient patientToAdd = objectMapper.readValue(request.getReader(), HttpPatient.class);

        Cookie[] cookies = request.getCookies();
        String requesterUid = SecureCommunication.getCookie(cookies, "uid");

        String severity = request.getParameter("severity");

        Command command = new Command();
        command.setType(CommandType.ADD);
        command.setIssuer(requesterUid);
        command.setSubject(objectMapper.writeValueAsString(patientToAdd));
        command.setTarget(patientToAdd.getUid());
        command.setSeverity(CommandSeverity.valueOf(severity));
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
}