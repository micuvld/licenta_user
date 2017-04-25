package enitites;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by vlad on 13.04.2017.
 */
public class HttpPatient {
    @JsonProperty("uid")
    private String uid;
    @JsonProperty("displayName")
    private String displayName;
    @JsonProperty("givenName")
    private String givenName;
    @JsonProperty("familyName")
    private String familyName;

    public String getUid() {
        return uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getFamilyName() {
        return familyName;
    }
}
