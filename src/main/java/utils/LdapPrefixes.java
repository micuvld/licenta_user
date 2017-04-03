package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by vlad on 03.04.2017.
 */
public class LdapPrefixes {
    public final static String BASE_DN;
    public final static String ADMIN_DN;
    public final static String DOCTORS_OU;
    public final static String PATIENTS_OU;

    static {
        Properties prop = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream("config.properties");

            prop.load(input);
            BASE_DN = prop.getProperty("base_dn");
            ADMIN_DN = prop.getProperty("admin_dn");
            DOCTORS_OU = prop.getProperty("doctors_ou");
            PATIENTS_OU = prop.getProperty("patients_ou");

        } catch (IOException ex) {
            System.out.println("Failed to load properties file!");
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
