package store.gomdolog.packages.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.gomdolog.packages.domain.Post;
import store.gomdolog.packages.domain.PostTag;
import store.gomdolog.packages.domain.Tag;
import store.gomdolog.packages.repository.PostTagRepository;

@Slf4j
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
            return;
        }

        postTagRepository.deleteByPostId(postId);
        for (PostTag postTag : postTagList) {
            postTag.getPost().getPostTags().remove(postTag);
            postTag.getTag().getPostTags().remove(postTag);
        }
    }
}

