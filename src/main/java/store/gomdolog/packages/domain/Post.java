package store.gomdolog.packages.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@NoArgsConstructor
@Getter
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String text;

    @ColumnDefault("0")
    private Long views;

    @Column(length = 500)
    private String thumbnail;

    @Builder
    public Post(String title, String text, Long views, String thumbnail) {
        this.title = title;
        this.text = text;
        this.views = views;
        this.thumbnail = thumbnail;
    }

    public void addViews() {
        views += 1;
    }
}
