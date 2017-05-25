package actions.doctors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import command.*;
import connection.SecureCommunication;
import org.apache.http.client.methods.CloseableHttpResponse;
import utils.HttpUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Created by vlad on 16.03.2017.
 */

@WebServlet("/patients/remove")
public class    RemovePatient extends HttpServlet {
    public static ObjectMapper objectMapper = new ObjectMapper();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        String requesterUid = SecureCommunication.getCookie(cookies, "uid");

        JsonNode requestNode = objectMapper.readTree(request.getReader());
        String patientUid = HttpUtils.readJsonField(requestNode, "patientUid");
        String targetUid;
        try {
            targetUid = HttpUtils.readJsonField(requestNode, "targetUid");
        } catch (ServletException e) {
            targetUid = requesterUid;
            System.out.println("Target uid not present. Using requester uid.");
        }

        CommandSeverity severity = CommandSeverity.AUTO_RESOLVE;

        Command command = new Command();
        command.setType(CommandType.REMOVE);
        command.setIssuer(requesterUid);
        command.setSubject(patientUid);
        command.setTarget(targetUid);
        command.setSeverity(severity);
        command.setState(CommandState.PENDING);
//        HttpSession session = request.getSession();
//        LdapConnector ldapConnector = (LdapConnector) session.getAttribute("connector");
//        ldapConnector.removePatient(patientDn);

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

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
