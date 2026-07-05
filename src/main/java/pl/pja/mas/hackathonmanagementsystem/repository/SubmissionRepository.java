package pl.pja.mas.hackathonmanagementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.pja.mas.hackathonmanagementsystem.model.Submission;
@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
}
