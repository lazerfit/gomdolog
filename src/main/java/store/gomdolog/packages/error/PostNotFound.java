package store.gomdolog.packages.error;

public class PostNotFound extends CustomErrorMessage{

    private static final String MESSAGE = "해당 post가 존재하지 않습니다.";

    public PostNotFound() {
        super(MESSAGE);
    }
}
