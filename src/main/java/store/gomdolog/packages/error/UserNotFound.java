package store.gomdolog.packages.error;

public class UserNotFound extends CustomErrorMessage {

    public UserNotFound() {
        super("Invalid username or password");
    }
}
