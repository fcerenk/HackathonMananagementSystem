package pl.pja.mas.hackathonmanagementsystem.model;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor

@Table(name= "mentors")
public class Mentor  extends User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;


    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Mentorship> mentorships = new ArrayList<>();


    public void joinEventAsMentor(HackathonEvent event, float availableHours) {

        Mentorship mentorship = Mentorship.builder()
                .mentor(this)
                .event(event)
                .availableHours(availableHours)
                .build();

        this.mentorships.add(mentorship);
        event.getMentorship().add(mentorship);
        System.out.println(getName() + " has joined the event as a mentor.");
    }

    public void viewSubmissionsAndEvaluationResults() {
        System.out.println("📖 Reviewing submissions (Read-Only access)."); // [cite: 26]
    }
    //manage avialbiltiy

    public void updateAvailability(HackathonEvent event, float availableHours) {
        this.mentorships.stream()
                .filter(m -> m.getEvent().equals(event))
                .findFirst()
                .ifPresentOrElse(
                        m -> {
                            m.setAvailableHours(availableHours);
                            System.out.println("⏰ " + getName() + " için " + event.getName() +
                                    " hours of events " + availableHours + " updated");
                        },
                        () -> System.out.println("Mentor is not available.")
                );
    }

    public void leaveEvent(HackathonEvent event) {
        Mentorship toRemove = this.mentorships.stream()
                .filter(m -> m.getEvent().equals(event))
                .findFirst()
                .orElse(null);

        if (toRemove != null) {
            this.mentorships.remove(toRemove);

            if (event.getMentorship().contains(toRemove)) {
                event.getMentorship().remove(toRemove);
            }


            System.out.println(getName() + ", " + event.getName() + " resigned from his mentoring role at the event.");
        }
    }

    @Override
    public void register() {
        System.out.println("Mentor registered");
    }

}
