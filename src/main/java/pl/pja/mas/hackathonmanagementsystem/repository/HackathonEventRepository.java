package pl.pja.mas.hackathonmanagementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.pja.mas.hackathonmanagementsystem.model.HackathonEvent;
import java.util.Optional;

@Repository
public interface HackathonEventRepository extends JpaRepository<HackathonEvent, Long> {
    // Sadece isme göre arama kalsın, diğerine gerek yok.
    Optional<HackathonEvent> findByName(String name);
}