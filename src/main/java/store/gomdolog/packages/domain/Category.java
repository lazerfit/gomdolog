package store.gomdolog.packages.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.gomdolog.packages.dto.CategoryUpdate;

@Entity
@Getter
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CATEGORY_ID")
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String title;

    public Category(String title) {
        this.title = title;
    }

    public void update(CategoryUpdate update) {
        this.title = update.title();
    }
}
