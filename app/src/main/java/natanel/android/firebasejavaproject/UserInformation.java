package natanel.android.firebasejavaproject;

public class UserInformation {
    private String name;
    private String email;
    private String phone_num;


    public UserInformation() {
    }

    public UserInformation(String name, String email, String phone_num) {
        this.name = name;
        this.email = email;
        this.phone_num = phone_num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_num() {
        return phone_num;
    }

    public void setPhone_num(String phone_num) {
        this.phone_num = phone_num;
    }
}
