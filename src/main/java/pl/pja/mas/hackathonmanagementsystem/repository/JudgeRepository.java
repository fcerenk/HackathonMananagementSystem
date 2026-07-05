package pl.pja.mas.hackathonmanagementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.pja.mas.hackathonmanagementsystem.model.Judge;

@Repository
public interface JudgeRepository extends JpaRepository<Judge, Long> {
}
