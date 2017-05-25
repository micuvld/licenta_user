package auth;

import com.cloudera.alfredo.client.KerberosAuthenticator;
import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.Mongo;
import ldap.HttpLdapConnector;
import ldap.LdapConnector;
import ldap.LdapUtils;
import ldap.UserType;
import mongo.MongoConnector;
import org.forgerock.opendj.ldap.ErrorResultException;
import utils.Configs;

import javax.naming.AuthenticationException;
import javax.naming.InvalidNameException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

/**
 * Created by vlad on 13.03.2017.
 */
@WebServlet("/login")
public class Login extends HttpServlet {
    private final String DOCTOR_CERT = "client-cert";
    private final String ADMIN_CERT = "admin-cert";
    private final String DOCTOR_ACTIONS_HREF = "/views/doctor/actions.html";
    private final HashMap<UserType, String> userActionsHrefs = new HashMap<>();

    public void init() {
        userActionsHrefs.put(UserType.ADMIN, "/views/admin/actions.html");
        userActionsHrefs.put(UserType.DOCTOR, "/views/doctor/actions.html");
        userActionsHrefs.put(UserType.PATIENT, "/views/patient/actions.html");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {
//        String uid = request.getParameter("uid");
//        String password = request.getParameter("password");
//
//        //LdapConnector ldapConnector = new LdapConnector(Configs.CLIENT_KEYSTORE,"qwerty",Configs.TRUSTSTORE);
////        try {
//            //UserType userType = ldapConnector.login(uid, password);
//            Cookie uidCookie = new Cookie("uid", uid);
//            response.addCookie(uidCookie);
//            uidCookie.setMaxAge(60*60*24);
////            HttpSession session = request.getSession();
////            session.setAttribute("connector", ldapConnector);
////            session.setAttribute("dn", ldapConnector.getDnForUid(uid));
////            session.setAttribute("uid", uid);
////            session.setAttribute("userType", userType);
//
//            //redirectToSpecificActionsPage(request,response, uid);
//        UserType userType = LdapUtils.getUserTypeForUid(uid);
//            response.getWriter().write(userActionsHrefs.get(userType));
////        } catch (ErrorResultException | AuthenticationException | InvalidNameException e) {
////            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
////            e.printStackTrace();
////        }

        try {
            simpleLogin(request, response);
        } catch (URISyntaxException e) {
            System.out.println("Failed to login.");
            e.printStackTrace();
        }
    }

    private void simpleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException, URISyntaxException {
        String uid = request.getParameter("uid");
        String password = request.getParameter("password");

        //LDAP get user fullName

        JsonNode userData = HttpLdapConnector.getEntryAsJsonNode(uid);
        Integer sessionId = MongoConnector.createUserSessionDocument(uid, userData.get("displayName").textValue());

        //add id as cookie
        Cookie sessionIdCookie = new Cookie("sessionId", sessionId.toString());
        response.addCookie(sessionIdCookie);
        sessionIdCookie.setMaxAge(60*60*24);

        redirectToSpecificActionsPage(request, response, uid);
    }

    //TO DO
    private void kerberosLogin() {

    }

    private void redirectToSpecificActionsPage(HttpServletRequest request, HttpServletResponse response, String uid) {
        try {
            UserType userType = LdapUtils.getUserTypeForUid(uid);
            response.sendRedirect(userActionsHrefs.get(userType));
        } catch (IOException e) {
            System.out.println("Error at redirecting! (from /login)");
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {

    }
}
