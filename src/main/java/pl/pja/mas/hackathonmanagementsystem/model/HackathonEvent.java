package pl.pja.mas.hackathonmanagementsystem.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pl.pja.mas.hackathonmanagementsystem.enums.EventStatus;
import pl.pja.mas.hackathonmanagementsystem.enums.RewardType;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="hackathonevent")
@SuperBuilder
public class HackathonEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Transient
    private static List<HackathonEvent> events = new ArrayList<>();


    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long hackeventId;

    @NotBlank
    private String name;

    @NotBlank
    private String duration;

    @NotNull
    private LocalDateTime startDate;

    @NotBlank
    private String location;

    @NotBlank
    private String description;

    private final int maxTeamSize =5;

    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus = EventStatus.DRAFT;


    //mentroship
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Mentorship> mentorship = new ArrayList<>();

    //organizer association
    @ManyToOne
    @JoinColumn(name="organizer_id")
    private Organizer organizer;

    //team association- composition
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Team> teams = new ArrayList<>();


    //xor
    @ManyToMany
    @JoinTable(
            name = "event_registrations",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "participant_id")
    )
    @Builder.Default
    private List<Participant> participants = new ArrayList<>();


    @ManyToMany
    @JoinTable(
            name = "event_judges",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "judge_id")
    )
    @Builder.Default
    private List<Judge> judges = new ArrayList<>();

    //reward
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Reward> rewards = new ArrayList<>();

    //qualified to challenge
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @MapKey(name = "title")
    @Builder.Default
    private Map<String, Challenge> challenges = new HashMap<>();

    @PostPersist
    @PostLoad
    protected void addToExtent(){
        if(!events.contains(this)){
            events.add(this);
        }
    }

    public static List<HackathonEvent> getExtent() {
        return Collections.unmodifiableList(events);
    }

    public void addTeam(Team team) {
        if (!teams.contains(team)) {
            teams.add(team);
            team.setEvent(this);
        }
    }

    public void removeTeam(Team team) {
        if (teams.contains(team)) {
            teams.remove(team);
            team.setEvent(null);
        }
    }




    //xor
    public void registerParticipant(Participant p) throws Exception {
        if(p==null) return;
        if(judges.contains(p)){
            throw new Exception("XOR Constraint Violation: " + p.getName() + "Judge already registered");
        }
        if(!participants.contains(p)){
            participants.add(p);
            if(!p.getRegisteredEvents().contains(this)){
                p.getRegisteredEvents().add(this);
            }
            System.out.println(" Participant " + p.getName() + " saved to the event");
        }

    }

    public void removeParticipant(Participant p) {
        if (p != null && participants.contains(p)) {
            participants.remove(p);


            p.getRegisteredEvents().remove(this);
            System.out.println("Participant " + p.getName() + " left the event.");
        }
    }
    public void assignJudge(Judge j) throws Exception {
        if (j == null) return;

        // XOR CONSTRAINT CHECK
        if (participants.contains(j)) {
            throw new Exception("XOR Constraint Violation: " + j.getName() +
                    " This person is already a PARTICIPANT in this event. They cannot be appointed as a juror!");
        }

        if (!judges.contains(j)) {
            judges.add(j);
            if (!j.getAssignedEvents().contains(this)) {
                j.getAssignedEvents().add(this);
            }
            System.out.println(j.getName() + " was appointed as a juror for the event.");
        }

    }

    public void removeJudge(Judge j) {
        if (j != null && judges.contains(j)) {
            judges.remove(j);


            j.getAssignedEvents().remove(this);
            System.out.println("Judge " + j.getName() + " removed from the event.");
        }
    }

    public void manageChallenges(String title, String description) throws Exception {
        if (challenges.containsKey(title)) {
            throw new Exception("Constraint Violation: Challenge title '" + title + "' must be unique within this event.");
        }

        Challenge challenge = Challenge.builder()
                .title(title)
                .description(description)
                .event(this)
                .build();

        this.challenges.put(title, challenge);
        System.out.println("New challenge '" + title + "' added to event: " + this.getName());
    }

    //retrieve challenge usign quailified title

    public Challenge getChallengeByTitle(String title) {
        return challenges.get(title);
    }

    public void manageRewards(String name, RewardType type) {
        Reward reward = Reward.builder()
                .name(name)
                .type(type)
                .event(this)
                .build();
        this.rewards.add(reward);
        System.out.println("Reward '" + name + "' (Type: " + type + ") has been added to the event: " + this.getName());
    }



    public void publish() {
        this.eventStatus = EventStatus.PUBLISHED;
        System.out.println("Event '" + name + "' is now Published.");
    }

    public void cancel() {
        this.eventStatus = EventStatus.CANCELLED;
        System.out.println("Event '" + name + "' has been Cancelled.");
    }


    //startevent
    public void startEvent() {
        this.eventStatus = EventStatus.ACTIVE; // Published -> Active [cite: 550]
        System.out.println("Event '" + name + "' has started!");
    }




    //endduration
    public void endDuration() {
        this.eventStatus = EventStatus.EVALUATION;
        System.out.println("Active phase ended. Moved to Evaluation.");
    }

    public static List<HackathonEvent> getAllEvents() {
        return getExtent();
    }


    public void getStandings() {
        System.out.println("\n--- 🏆 LIVE STANDINGS: " + this.name + " ---");

        teams.stream()
                .filter(t -> t.getSubmissions() != null && !t.getSubmissions().isEmpty())
                .sorted((t1, t2) -> {
                    float score1 = t1.getSubmissions().get(t1.getSubmissions().size()-1).getAverageScore();
                    float score2 = t2.getSubmissions().get(t2.getSubmissions().size()-1).getAverageScore();
                    return Float.compare(score2, score1);
                })
                .forEach(t -> {

                    float score = t.getSubmissions().get(0).getAverageScore();
                    System.out.println("Ranking: Team " + t.getTeamName() + " | Score: " + score);
                });

        System.out.println("------------------------------------------");
    }

    public void finalizeResult() {
        if (this.eventStatus != EventStatus.EVALUATION) {
            System.out.println("Error: Event must be in EVALUATION phase to finalize results.");
            return;
        }

        // Change status
        this.eventStatus = EventStatus.COMPLETED; // [cite: 36]
        System.out.println("🏁 Event '" + name + "' is now COMPLETED.");

        // Show final winners
        System.out.println("🎉 CONGRATULATIONS TO THE WINNERS!");
        getStandings();

    }







}
