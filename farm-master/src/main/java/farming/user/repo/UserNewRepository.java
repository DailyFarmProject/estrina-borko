package farming.user.repo;

import farming.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserNewRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User>  findById(Long id);
}
