package store.gomdolog.packages.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class NetworkError extends CustomErrorMessage{

    public NetworkError() {
        super("Network Error");
    }
}
