package store.gomdolog.packages.error;

public abstract class CustomErrorMessage extends RuntimeException {

    protected CustomErrorMessage(final String message) {
        super(message);
    }
}


