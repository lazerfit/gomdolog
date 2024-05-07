package store.gomdolog.packages.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserNotFound extends CustomErrorMessage {

    public UserNotFound() {
        super("Invalid username or password");
    }
}
