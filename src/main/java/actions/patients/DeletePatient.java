package actions.patients;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import command.*;
import connection.SecureCommunication;
import org.apache.http.client.methods.CloseableHttpResponse;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by vlad on 20.04.2017.
 */
@WebServlet("/patients/delete")
public class DeletePatient extends HttpServlet {
    public static ObjectMapper objectMapper = new ObjectMapper();
    public final CommandSeverity SEVERITY = CommandSeverity.AUTO_RESOLVE;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonNode requestNode = objectMapper.readTree(request.getReader());

        String patientUid = requestNode.get("patientUid").textValue();
        if (patientUid == null) {
            throw new ServletException("patientUid not found in request!");
        }

        System.out.println(patientUid);
        Cookie[] cookies = request.getCookies();
        String requesterUid = SecureCommunication.getCookie(cookies, "uid");

        Command command = new Command();
        command.setType(CommandType.DELETE);
        command.setIssuer(requesterUid);
        command.setSubject(patientUid);
        command.setTarget(patientUid);
        command.setSeverity(SEVERITY);
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
