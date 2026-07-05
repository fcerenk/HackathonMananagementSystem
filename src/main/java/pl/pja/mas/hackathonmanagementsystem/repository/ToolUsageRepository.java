package pl.pja.mas.hackathonmanagementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.pja.mas.hackathonmanagementsystem.model.ToolUsage;

@Repository
public interface ToolUsageRepository extends JpaRepository<ToolUsage, Long> {

}
