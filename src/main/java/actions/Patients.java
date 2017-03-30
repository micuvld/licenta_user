package actions;

import com.fasterxml.jackson.databind.ObjectMapper;
import connect.LdapConnector;
import enitites.Patient;
import org.forgerock.opendj.ldap.*;
import org.forgerock.opendj.ldap.responses.SearchResultEntry;
import org.forgerock.opendj.ldif.ConnectionEntryReader;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by vlad on 15.03.2017.
 */
@WebServlet("/patients")
public class Patients extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Patient patient = objectMapper.readValue(request.getReader(), Patient.class);

        HttpSession session = request.getSession();
        LdapConnector ldapConnector = (LdapConnector) session.getAttribute("connector");


    }

    public boolean addPatient() {
        ldapConnector.
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        LdapConnector ldapConnector = (LdapConnector)session.getAttribute("connector");
        String uid = (String)session.getAttribute("uid");

        List<Patient> patients;
        String filter = request.getParameter("filter");
        switch (filter) {
            case "all":
                patients = getAllPatients(ldapConnector);
                break;
            case "claimed":
                patients = getDoctorPatients(ldapConnector, uid);
                break;
            case "unclaimed":
                List<Patient> allPatients = getAllPatients(ldapConnector);
                List<Patient> claimedPatients = getDoctorPatients(ldapConnector, uid);

                patients = difference(allPatients, claimedPatients);
                break;
            default:
                throw new ServletException("Invalid filter!");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getWriter(), patients);
    }

    private List<Patient> getDoctorPatients(LdapConnector ldapConnector, String doctorUid) {
        SearchResultEntry entry;
        List<Patient> patients = new ArrayList<>();
        try {
            System.out.println("Filter: " + "(uid=" + doctorUid + ")");
            entry = ldapConnector.getConnection().searchSingleEntry("dc=spital,dc=moinesti,dc=com",
                    SearchScope.WHOLE_SUBTREE, "(uid=" + doctorUid + ")");
            Attribute patientsDn = entry.getAttribute("patientdn");

            for (ByteString patientDn : patientsDn.toArray()) {
                System.out.println(patientDn);
                List<String> cnAndDn = getEntryAttributesValue(ldapConnector, patientDn.toString(), "cn", "dn");
                patients.add(new Patient(cnAndDn.get(0), cnAndDn.get(1)));
            }

            return patients;
        } catch (ErrorResultException e) {
            System.out.println("Error at retrieving patients from LDAP");
            e.printStackTrace();
        }

        return patients;
    }

    private List<Patient> getAllPatients(LdapConnector ldapConnector) {
        ConnectionEntryReader entry;
        List<Patient> patients = new ArrayList<>();
        try {
            entry = ldapConnector.getConnection().search("ou=Patients,ou=Users,dc=spital,dc=moinesti,dc=com",
                    SearchScope.WHOLE_SUBTREE, "uid=*");

            while (entry.hasNext()) {
                Entry patient = entry.readEntry();

                patients.add(new Patient(patient.getAttribute("cn").firstValue().toString(),
                        patient.getName().toString()));
            }

            return patients;
        } catch (ErrorResultIOException e) {
            e.printStackTrace();
        } catch (SearchResultReferenceIOException e) {
            e.printStackTrace();
        }

        return patients;
    }

    private List<Patient> difference(List<Patient> l1, List<Patient> l2) {
        List<Patient> differenceList = new ArrayList<>();

        for (Patient element : l1) {
            if (!l2.contains(element)) {
                differenceList.add(element);
            }
        }

        return differenceList;
    }

    private List<String> getEntryAttributesValue(LdapConnector ldapConnector, String dn, String... attributes) {
        SearchResultEntry entry;
        String attributeValue;
        List<String> attributesValues = new ArrayList<>();
        try {
            entry = ldapConnector.getConnection().readEntry(dn);
            for (String attribute : attributes ) {
                if (attribute.equals("dn")) {
                    attributeValue = entry.getName().toString();
                } else {
                    attributeValue = entry.getAttribute(attribute).firstValue().toString();
                }
                attributesValues.add(attributeValue);
            }

        } catch (ErrorResultException e) {
            System.out.println("Error at retrieving attribute values for: " + dn);
            e.printStackTrace();
        }

        return attributesValues;
    }
}