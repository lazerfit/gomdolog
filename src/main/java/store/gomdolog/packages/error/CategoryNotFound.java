package store.gomdolog.packages.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CategoryNotFound extends CustomErrorMessage{

    private static final String MESSAGE = "Category not found";

    public CategoryNotFound() {
        super(MESSAGE);
    }
}
