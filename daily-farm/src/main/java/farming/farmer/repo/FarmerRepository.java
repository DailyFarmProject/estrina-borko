package farming.farmer.repo;

import farming.farmer.entity.Farmer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FarmerRepository extends JpaRepository<Farmer, UUID> {
    boolean existsByLogin(String login);
    Optional<Farmer> findByLogin(String login);
}