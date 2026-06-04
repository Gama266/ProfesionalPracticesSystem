package logic.businessObject;

/**
 *
 * @author akyer
 */
public class User {
    private int    idUser;
    private String gmail;
    private String plainPassword;

    private UserType userType;
    private boolean  active;

    public User() {
        this.active = true;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUsuario) {
        this.idUser = idUsuario;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public String getPlainPassword() {
        return plainPassword;
    }

    public void setPlainPassword(String plainPassword) {
        this.plainPassword = plainPassword;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
