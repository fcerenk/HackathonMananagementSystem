package pl.pja.mas.hackathonmanagementsystem.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.pja.mas.hackathonmanagementsystem.model.Challenge;
@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    Optional<Challenge> findByEvent_HackeventIdAndTitle(Long eventId, String title);
}
