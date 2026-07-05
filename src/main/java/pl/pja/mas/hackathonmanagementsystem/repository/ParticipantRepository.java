package pl.pja.mas.hackathonmanagementsystem.repository;

import org.hibernate.sql.exec.spi.JdbcCallParameterExtractor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.pja.mas.hackathonmanagementsystem.model.Participant;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {
}
