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
 * Request body must look like this:
 * {
 *     "patientUid": "patient1",
 *     "severity": "NORMAL"
 *     [, "subjectUid": "doctor1"]
 * }
 * Created by vlad on 16.03.2017.
 */
@WebServlet("/patients/set")
public class SetPatient extends HttpServlet {
    public static ObjectMapper objectMapper = new ObjectMapper();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        String requesterUid = SecureCommunication.getCookie(cookies, "uid");

        JsonNode requestNode = objectMapper.readTree(request.getReader());
        String patientUid = utils.HttpUtils.readJsonField(requestNode, "patientUid");
        String severity = HttpUtils.readJsonField(requestNode, "severity");
        String targetUid;
        try {
            targetUid = HttpUtils.readJsonField(requestNode, "targetUid");
        } catch (ServletException e) {
            targetUid = requesterUid;
            System.out.println("Target uid not present. Using requester uid.");
        }

        Command command = new Command();
        command.setType(CommandType.SET);
        command.setIssuer(requesterUid);
        command.setSubject(patientUid);
        command.setTarget(targetUid);
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

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
