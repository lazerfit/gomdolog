package store.gomdolog.packages.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.gomdolog.packages.domain.Post;
import store.gomdolog.packages.domain.PostTag;
import store.gomdolog.packages.domain.Tag;
import store.gomdolog.packages.error.PostNotFound;
import store.gomdolog.packages.repository.PostTagRepository;

@RequiredArgsConstructor
@Service
public class PostTagService {

    private final PostTagRepository postTagRepository;

    @Transactional
    public void save(Post post, List<Tag> tagList) {
        for (Tag tag : tagList) {
            PostTag postTag = new PostTag(post, tag);
            postTagRepository.save(postTag);
            post.getPostTags().add(postTag);
            tag.getPostTags().add(postTag);
        }
    }

    @Transactional
    public void delete(Long postId) {
        List<PostTag> postTagList = postTagRepository.findByPostId(postId);

        if(postTagList.isEmpty()) {
            throw new PostNotFound();
        }

        postTagRepository.deleteByPostId(postId);
        for (PostTag postTag : postTagList) {
            postTag.getPost().getPostTags().remove(postTag);
            postTag.getTag().getPostTags().remove(postTag);
        }
    }
}

