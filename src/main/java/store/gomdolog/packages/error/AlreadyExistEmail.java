package store.gomdolog.packages.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AlreadyExistEmail extends CustomErrorMessage{

    public AlreadyExistEmail() {
        super("Email address already in use");
    }
}
