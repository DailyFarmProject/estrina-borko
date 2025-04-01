package farming.accounting.repo;

import farming.accounting.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserAccount, UUID> {
    boolean existsByLogin(String login);
    UserAccount findByLogin(String login);
}