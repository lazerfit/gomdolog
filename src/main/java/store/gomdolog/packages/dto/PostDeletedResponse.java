package store.gomdolog.packages.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class PostDeletedResponse {

    private final Long id;
    private final String title;
    private final LocalDateTime deletedDate;

    @QueryProjection
    public PostDeletedResponse(Long id, String title) {
        this.id = id;
        this.title = title;
        deletedDate = LocalDateTime.now();
    }
}
