package store.gomdolog.packages.repository;

import static store.gomdolog.packages.domain.QPost.post;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import store.gomdolog.packages.dto.PostResponseWithoutTags;
import store.gomdolog.packages.dto.QPostResponseWithoutTags;

@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public List<PostResponseWithoutTags> searchPostsByTitle(String q) {
        return query.select(new QPostResponseWithoutTags(
            post.id,
            post.title,
            post.content,
            post.createdDate,
            post.thumbnail,
            post.category.title
        ))
            .from(post)
            .where(post.title.like("%" + q + "%"))
            .orderBy(post.createdDate.desc())
            .fetch();
    }

    @Override
    public List<PostResponseWithoutTags> fetchPosts() {
        return query.select(new QPostResponseWithoutTags(
                post.id,
                post.title,
                post.content,
                post.createdDate,
                post.thumbnail,
                post.category.title
            ))
            .from(post)
            .orderBy(post.createdDate.desc())
            .fetch();
    }

    @Override
    public List<PostResponseWithoutTags> searchPostsByCategory(String q) {
        return query.select(new QPostResponseWithoutTags(
                post.id,
                post.title,
                post.content,
                post.createdDate,
                post.thumbnail,
                post.category.title
            ))
            .from(post)
            .where(post.category.title.eq(q))
            .orderBy(post.createdDate.desc())
            .fetch();
    }
}
