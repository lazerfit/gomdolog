package store.gomdolog.packages.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PostNotFound extends CustomErrorMessage{

    private static final String MESSAGE = "해당 post가 존재하지 않습니다.";

    public PostNotFound() {
        super(MESSAGE);
    }
}
