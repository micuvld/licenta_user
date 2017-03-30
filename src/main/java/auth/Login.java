package auth;

import connect.LdapConnector;
import enitites.UserType;
import org.forgerock.opendj.ldap.ErrorResultException;

import javax.naming.AuthenticationException;
import javax.naming.InvalidNameException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by vlad on 13.03.2017.
 */
@WebServlet("/login")
public class Login extends HttpServlet {
    private final String DOCTOR_ACTIONS_HREF = "/views/doctor/actions.html";
    private final HashMap<UserType, String> userActionsHrefs = new HashMap<>();

    public void init() {
        userActionsHrefs.put(UserType.ADMIN, "/views/admin/actions.html");
        userActionsHrefs.put(UserType.DOCTOR, "/views/doctor/actions.html");
        userActionsHrefs.put(UserType.PATIENT, "/views/patient/actions.html");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        String uid = request.getParameter("uid");
        String password = request.getParameter("password");

        LdapConnector ldapConnector = new LdapConnector("","","");
        try {
            UserType userType = ldapConnector.login(uid, password);
            HttpSession session = request.getSession();
            session.setAttribute("connector", ldapConnector);
            session.setAttribute("uid", uid);
            session.setAttribute("userType", userType);

            response.getWriter().write(userActionsHrefs.get(userType));
        } catch (ErrorResultException | AuthenticationException | InvalidNameException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            e.printStackTrace();
        }
    }

    private void redirectToSpecificActionsPage(HttpServletRequest request, HttpServletResponse response, UserType userType) {
        try {
            request.getRequestDispatcher(userActionsHrefs.get(userType)).forward(request,response);
        } catch (ServletException | IOException e) {
            System.out.println("Error at redirecting! (from /login)");
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {

    }
}
