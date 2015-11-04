package interdroid.swan.crossdevice.swanplus;

/**
 * Created by vladimir on 11/3/15.
 */
public class SwanUser {

    String username;
    String regId;

    public SwanUser(String username, String regId) {
        this.username = username;
        this.regId = regId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    @Override
    public String toString() {
        return username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SwanUser swanUser = (SwanUser) o;

        return username.equals(swanUser.username);

    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }
}
