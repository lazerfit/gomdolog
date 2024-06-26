package store.gomdolog.packages.repository;

import static store.gomdolog.packages.domain.QPost.post;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import store.gomdolog.packages.dto.PostDeletedResponse;
import store.gomdolog.packages.dto.PostResponseWithoutTags;
import store.gomdolog.packages.dto.QPostDeletedResponse;
import store.gomdolog.packages.dto.QPostResponseWithoutTags;

@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public Page<PostResponseWithoutTags> searchPostsByTitle(String q, Pageable pageable) {
        List<PostResponseWithoutTags> postList = query.select(new QPostResponseWithoutTags(
                post.id,
                post.title,
                post.content,
                post.createdDate,
                post.thumbnail,
                post.category.title
            ))
            .from(post)
            .where(post.title.like("%" + q + "%"))
            .where(post.isDeleted.isFalse())
            .orderBy(post.createdDate.desc())
            .limit(pageable.getPageSize())
            .fetch();

        int totalCount = query.select(post.count()).from(post).where(post.title.like("%" + q + "%"))
            .fetch().size();

        return new PageImpl<>(postList, pageable, totalCount);
    }

    @Override
    public Page<PostResponseWithoutTags> fetchAll(Pageable pageable) {
        List<PostResponseWithoutTags> postList = query.select(new QPostResponseWithoutTags(
                post.id,
                post.title,
                post.content,
                post.createdDate,
                post.thumbnail,
                post.category.title
            ))
            .from(post)
            .where(post.isDeleted.isFalse())
            .orderBy(post.createdDate.desc())
            .limit(pageable.getPageSize())
            .fetch();

        int totalCount = query.select(post.count()).from(post).fetch().size();

        return new PageImpl<>(postList,pageable,totalCount);
    }

    @Override
    public Page<PostResponseWithoutTags> searchPostsByCategory(String q, Pageable pageable) {
        List<PostResponseWithoutTags> postList = query.select(new QPostResponseWithoutTags(
                post.id,
                post.title,
                post.content,
                post.createdDate,
                post.thumbnail,
                post.category.title
            ))
            .from(post)
            .where(post.category.title.eq(q))
            .where(post.isDeleted.isFalse())
            .orderBy(post.createdDate.desc())
            .limit(pageable.getPageSize())
            .fetch();

        int totalCount = query.select(post.count()).from(post).where(post.category.title.eq(q))
            .fetch().size();

        return new PageImpl<>(postList,pageable,totalCount);
    }

    @Override
    public List<PostDeletedResponse> fetchDeletedPost() {
        return query.select(new QPostDeletedResponse(
            post.id,
            post.title
        ))
            .from(post)
            .where(post.isDeleted.isTrue())
            .fetch();
    }
}
