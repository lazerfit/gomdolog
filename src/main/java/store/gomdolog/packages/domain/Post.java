package store.gomdolog.packages.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import store.gomdolog.packages.dto.PostUpdate;

@Entity
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POST_ID")
    private Long id;

    @Column(nullable = false)
    private String title;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ColumnDefault("0")
    private Long views;

    @Column(length = 500)
    private String thumbnail;

    @OneToMany(mappedBy = "post")
    private List<PostTag> postTags = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID")
    private Category category;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "POSTSUMMARY_ID")
    private PostSummary postSummary;

    @Column
    private Boolean isDeleted;

    @Builder
    public Post(String title, String content, Long views, String thumbnail, Category category,
        PostSummary postSummary) {

        this.title = title;
        this.content = content;
        this.views = views;
        this.thumbnail = thumbnail;
        this.category = category;
        this.postSummary = postSummary;
        isDeleted = false;
    }

    public void updateCategory(Category category) {
        this.category = category;
    }

    public void update(PostUpdate update) {
        title = update.title();
        content = update.content();
    }

    public void moveToRecycleBin() {
        isDeleted = true;
    }

    public void revertDelete() {
        isDeleted = false;
    }

    public void addViews() {
        views += 1;
    }
}
