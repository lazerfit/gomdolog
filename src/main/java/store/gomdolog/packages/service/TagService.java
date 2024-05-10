package store.gomdolog.packages.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.gomdolog.packages.domain.Tag;
import store.gomdolog.packages.repository.TagRepository;

@RequiredArgsConstructor
@Service
public class TagService {

    private final TagRepository tagRepository;

    @Transactional
    public List<Tag> save(List<String> tags) {
        List<Tag> tagList = new ArrayList<>();
        for (String tag : tags) {
            Tag existTag = tagRepository.findByName(tag).orElse(null);
            if(existTag != null) {
                tagList.add(existTag);
            } else {
                Tag t = new Tag(tag);
                Tag savedT = tagRepository.save(t);
                tagList.add(savedT);
            }
        }
        return tagList;
    }
}
