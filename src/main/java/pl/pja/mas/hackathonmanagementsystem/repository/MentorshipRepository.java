package pl.pja.mas.hackathonmanagementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.pja.mas.hackathonmanagementsystem.model.Mentorship;

@Repository
public interface MentorshipRepository extends JpaRepository<Mentorship, Long> {
}
