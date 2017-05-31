package garg.sushant.mymovielist;

/**
 * Created by Sushant on 3/14/2017.
 */

public class Users {
    private String user;
    private String email;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotUrl() {
        return photUrl;
    }

    public void setPhotUrl(String photUrl) {
        this.photUrl = photUrl;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    private String photUrl;
    private String Uid;

    public Users(){

    }

    public Users(String user){
        this.user = user;
    }

    public Users(String user, String email, String photUrl, String uid){
        this.user = user;
        this.email = email;
        this.photUrl = photUrl;
        Uid = uid;
    }

}
