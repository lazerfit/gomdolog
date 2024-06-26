package store.gomdolog.packages.error;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class CustomErrorMessage extends RuntimeException {

    protected CustomErrorMessage(final String message) {
        super(message);
        log.info(message);
    }
}


