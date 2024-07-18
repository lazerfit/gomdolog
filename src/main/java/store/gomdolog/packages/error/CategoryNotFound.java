package store.gomdolog.packages.error;

public class CategoryNotFound extends CustomErrorMessage{

    private static final String MESSAGE = "Category not found";

    public CategoryNotFound() {
        super(MESSAGE);
    }
}
