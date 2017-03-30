package enitites;

/**
 * Created by vlad on 16.03.2017.
 */
public class Patient {
    //cn
    private String fullName;
    //dn
    private String distinguishedName;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDistinguishedName() {
        return distinguishedName;
    }

    public void setDistinguishedName(String distinguishedName) {
        this.distinguishedName = distinguishedName;
    }

    public Patient() {

    }

    public Patient(String fullName, String distinguishedName) {
        this.fullName = fullName;
        this.distinguishedName = distinguishedName;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Patient) {
            return ((Patient)o).distinguishedName.equals(this.distinguishedName);
        }

        return false;
    }
}
