package pl.pja.mas.hackathonmanagementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.pja.mas.hackathonmanagementsystem.model.Reward;
import java.util.List;

@Repository
public interface RewardRepository extends JpaRepository<Reward, Long> {

    List<Reward> findByEvent_HackeventId(Long hackeventId);

    List<Reward> findByWinningTeam_TeamId(Long teamId);
}
