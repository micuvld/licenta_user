package enitites;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by vlad on 30.03.2017.
 */
public class PatientEntry{
    String givenName;
    String surname;
    String commonName;
    String uid;

    public String getGivenName() {
        return givenName;
    }

    public String getSurname() {
        return surname;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @JsonCreator
    public PatientEntry(@JsonProperty("givenName") String givenName,
                        @JsonProperty("surname") String surname) {
        this.givenName = givenName;
        this.surname = surname;
        this.commonName = surname + " " + surname;
        this.uid = "";
    }
}
