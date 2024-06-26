package store.gomdolog.packages.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import store.gomdolog.packages.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String username);
}
