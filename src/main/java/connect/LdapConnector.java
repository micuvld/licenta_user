package connect;

import enitites.UserType;
import org.forgerock.opendj.ldap.*;
import org.forgerock.opendj.ldap.requests.BindRequest;
import org.forgerock.opendj.ldap.responses.BindResult;
import org.forgerock.opendj.ldap.responses.Result;
import org.forgerock.opendj.ldap.responses.SearchResultEntry;

import javax.jws.soap.SOAPBinding;
import javax.naming.*;
import javax.naming.AuthenticationException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509KeyManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.cert.*;

/**
 * Created by vlad on 13.03.2017.
 */
public class LdapConnector {
    public static final String HOST_NAME="vlad-PC";
    public static final int PORT= 2636;
    LDAPConnectionFactory factory;
    Connection connection;

    private String boundDn = "";
    private UserType userType;

    public Connection getConnection() {
        return connection;
    }

    public String getBoundDn() {
        return boundDn;
    }

    public LdapConnector(String keystorePath, String storepass, String truststorePath) {
        try {
            factory = new LDAPConnectionFactory(HOST_NAME, PORT,
                    getTrustOptions("", ""));
            connection = factory.getConnection();

            System.out.println("Connection handler creating succeeded!");
        } catch (ErrorResultException e) {
            System.out.println("Connection handler creating failed!");
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private LDAPOptions getTrustOptions(String keystorePath, String truststore)
            throws GeneralSecurityException, IOException {
        LDAPOptions ldapOptions = new LDAPOptions();
        SSLContextBuilder contextBuilder = new SSLContextBuilder();
//        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//        keyStore.load(new FileInputStream(new File("/home/vlad/Licenta/certificates/client/clientks")), "qwerty".toCharArray());
//
//        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
//        keyManagerFactory.init(keyStore, "qwerty".toCharArray());
//
//        //X509KeyManager keyManager = KeyManagers.useKeyStoreFile("/home/vlad/Licenta/certificates/client/clientks", "qwerty".toCharArray(), null);
//        KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();
//        for (KeyManager keyManager : keyManagers) {
//            System.out.println(keyManager.toString());
//        }
        contextBuilder.setKeyManager(KeyManagers.useKeyStoreFile("/home/vlad/Licenta/certificates/client/client-keystore", "qwerty".toCharArray(), null));
        contextBuilder.setTrustManager(TrustManagers
                .checkUsingTrustStore("/home/vlad/Licenta/certificates/server/server-truststore"));

        SSLContext sslContext = contextBuilder.getSSLContext();
        ldapOptions.setSSLContext(sslContext);
        ldapOptions.setUseStartTLS(false);
        return ldapOptions;
    }

    public UserType login(String uid, String password) throws AuthenticationException, ErrorResultException, InvalidNameException {
        String dn = getDnForUid(uid);

        if (certificateMatchesDn(dn, "")) {

            BindResult bindResult = connection.bind(dn, password.toCharArray());
            if (bindResult.isSuccess()) {
                boundDn = dn;
                System.out.println("Success!");
                setUserTypeUsingDn(dn);
                return userType;
            } else {
                throw new javax.naming.AuthenticationException("Wrong credentials!");
            }
        } else {
            throw new javax.naming.AuthenticationException("Certificate DNs not matching!");
        }
    }

    private void setUserTypeUsingDn(String dn) throws InvalidNameException {
        LdapName ldapName = new LdapName(dn);

        for (Rdn rdn : ldapName.getRdns()) {
            switch((String)rdn.getValue()) {
                case "Admins":
                    userType = UserType.ADMIN;
                    break;
                case "Doctors":
                    userType = UserType.DOCTOR;
                    break;
                case "Patients":
                    userType = UserType.PATIENT;
                    break;
            }
        }
    }

    public String getDnForUid(String uid) throws ErrorResultException {
        SearchResultEntry entry = connection.searchSingleEntry("dc=spital,dc=moinesti,dc=com",
                SearchScope.WHOLE_SUBTREE, "(uid=" + uid + ")");

        return entry.getName().toString();
    }

    public boolean certificateMatchesDn(String dn, String keyStorePath) {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(new FileInputStream("/home/vlad/Licenta/certificates/client/client-keystore"), "qwerty".toCharArray());
            java.security.cert.Certificate cert = keyStore.getCertificate("admin-cert");

            X509Certificate x509Certificate = (X509Certificate)cert;

            System.out.println(dn);
            System.out.println(x509Certificate.getSubjectDN().getName());
            LdapName ldapDn = new LdapName(dn);
            LdapName ldapSubjectDn = new LdapName(x509Certificate.getSubjectDN().getName());
            return ldapDn.equals(ldapSubjectDn);
        } catch (KeyStoreException e) {
            System.out.println("KeyStore creating failed!");
            e.printStackTrace();
        } catch (CertificateException | NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        } catch (InvalidNameException e) {
            System.out.println("Invalid DN (either certificate Subject DN or Ldap DN");
            e.printStackTrace();
        }

        return false;
    }

    public void setPatient(String patientDn) {
        try {
            Result result = this.connection.modify("dn:" + boundDn,
                    "changetype: modify",
                    "add:patientdn",
                    "patientdn:" + patientDn);
            System.out.println(result.toString());
        } catch (ErrorResultException e) {
            System.out.println("Error at setting patient!");
            e.printStackTrace();
        }
    }

    public void removePatient(String patientDn) {
        try {
            Result result = this.connection.modify("dn:" + boundDn,
                    "changetype: modify",
                    "delete:patientdn",
                    "patientdn:" + patientDn);
            System.out.println(result.toString());
        } catch (ErrorResultException e) {
            System.out.println("Error at setting patient!");
            e.printStackTrace();
        }
    }

    public void addPatient() {

    }
}
