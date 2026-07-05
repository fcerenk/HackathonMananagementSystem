package pl.pja.mas.hackathonmanagementsystem.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.Map;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "teams")
@Getter @Setter
public class Team  implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Transient
    private static List<Team> teams = new ArrayList();

    // team reward
    @OneToMany(mappedBy = "winningTeam")
    @Builder.Default
    private List<Reward> wonRewards = new ArrayList<>();

    //aggregation (1..*)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "team_members",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "participant_id")
    )
    @Builder.Default
    private List<Participant> members = new ArrayList<>();

    //event
    @ManyToOne
    @JoinColumn(name = "hackevent_id")
    private HackathonEvent event;


    //team lead association (1..1)
    @ManyToOne
    @JoinColumn(name = "leader_id")
    private Participant teamLead;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamId;

    @NotBlank(message = "Team name is mandatory")
    @Column(nullable = false)
    private String teamName;

    @NotNull(message = "Creation Date is mandatory")
    @Column(nullable = false)
    private LocalDateTime createDate;


    //derived attr.
    @Transient
    public int getMemberCount() {

        return (members != null) ? members.size() : 0;
    }

    public void addMember(Participant p) throws Exception {
        if (p == null) return;

        boolean alreadyInAnotherTeamForThisEvent = p.getTeams().stream()
                .anyMatch(t -> t.getEvent().equals(this.event));

        if (alreadyInAnotherTeamForThisEvent) {
            throw new Exception("Constraint Violation: " + p.getName() +
                    " is already a member of another team at this event!");
        }


        if (event != null && getMemberCount() >= event.getMaxTeamSize()) {
            throw new Exception("Constraint Violation: Team is full (Max: " + event.getMaxTeamSize() + ")");
        }

        if (!members.contains(p)) {
            members.add(p);
            if(!p.getTeams().contains(this)) {
                p.getTeams().add(this);
            }

        }
    }

    public void removeMember(Participant p) {
        if (members.contains(p)) {
            members.remove(p);
            if (p.getTeams().contains(this)) {
                p.getTeams().remove(this);
            }
        }
    }


    @PostLoad
    @PostPersist
    protected void addExtent () {
        if (!teams.contains(this)) {
            teams.add(this);
        }
    }

    public static List<Team> getTeams() {
        return Collections.unmodifiableList(teams);
    }

    public void addWonReward(Reward reward) {
        if (reward != null && !wonRewards.contains(reward)) {
            wonRewards.add(reward);
            reward.setWinningTeam(this);
        }
    }
    public void removeWonReward(Reward reward) {
        if (reward != null && wonRewards.contains(reward)) {
            wonRewards.remove(reward);
            reward.setWinningTeam(null);
            System.out.println("Reward '" + reward.getName() + "'  was removed from the team.");
        }
    }

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Submission> submissions = new ArrayList<>();

    public void addSubmission(Submission submission) {
        if (submission != null && !submissions.contains(submission)) {
            this.submissions.add(submission);
            submission.setTeam(this);
        }
    }

    public void removeSubmission(Submission submission) {
        if (submission != null && submissions.contains(submission)) {
            this.submissions.remove(submission);
            submission.setTeam(null); // Clear the reverse connection
            System.out.println("Submission '" + submission.getProjectName() + "' was removed from Team: " + this.teamName);
        }
    }

    public void submitProject(String projectName, String link, Challenge challenge,Map<Tool, String> selectedTools) throws Exception {

        if (getMemberCount() == 0) {
            throw new Exception("Error: Teams without members cannot submit projects.");
        }

        Submission newSub = Submission.builder()
                .projectName(projectName)
                .projectLink(link)
                .timestamp(LocalDateTime.now())
                .team(this)
                .challenge(challenge)
                .build();
        selectedTools.forEach((tool, version) -> {
            ToolUsage usage = ToolUsage.builder()
                    .tool(tool)
                    .version(version)
                    .submission(newSub)
                    .build();
            newSub.getToolUsages().add(usage);
        });

        this.addSubmission(newSub);
        challenge.addSubmission(newSub);

        System.out.println("Success: Team '" + teamName + "' submitted project: " + projectName);
    }


    public void assignLead(Participant p) throws Exception {
        if (p != null && !this.members.contains(p)) {
            throw new Exception("Error: Only one team member can be selected as leader!");
        }

        if (this.teamLead != null) {
            this.teamLead.getLeadingTeams().remove(this);
        }

        this.teamLead = p;

        if (p != null && !p.getLeadingTeams().contains(this)) {
            p.getLeadingTeams().add(this);
        }

        System.out.println( p.getName() + ", " + this.teamName + "was appointed as the leader of the team. ");
    }




    }

