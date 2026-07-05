package pl.pja.mas.hackathonmanagementsystem.model;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@Table(name = "participants")
@NoArgsConstructor
@SuperBuilder
public class Participant extends User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;



    //aggregation
    @ManyToMany(mappedBy = "members")
    @Builder.Default
    private List<Team> teams = new ArrayList<>();




    //leads association
    @OneToMany(mappedBy = "teamLead", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Team> leadingTeams = new ArrayList<>();

    // event-participant many tomany
    @ManyToMany(mappedBy = "participants")
    @Builder.Default
    private List<HackathonEvent> registeredEvents = new ArrayList<>();



    public void createNewTeam(String teamName, HackathonEvent event) throws Exception {
        if (event != null && !event.getParticipants().contains(this)) {
            throw new Exception("Error: You cannot form a team without registering for the event!");
        }

        boolean alreadyInTeam = teams.stream().anyMatch(t -> t.getEvent().equals(event));
        if (alreadyInTeam) {
            throw new Exception("Error: You already have a team in this event!");
        }

        Team newTeam = Team.builder()
                .teamName(teamName)
                .createDate(LocalDateTime.now())
                .event(event)
                .build();


        newTeam.addMember(this);


        newTeam.assignLead(this);


        if (event != null) {
            event.addTeam(newTeam);
        }

        System.out.println(getName() + " is created new team " + teamName);
    }

    public void joinExistingTeam(Team team) throws Exception {
        if (team != null) {
            team.addMember(this);
        }

    }

    public void leaveTeam(HackathonEvent event) {

        Team teamToLeave = teams.stream()
                .filter(t -> t.getEvent().equals(event))
                .findFirst()
                .orElse(null);

        if (teamToLeave != null) {
            teamToLeave.removeMember(this);
            this.teams.remove(teamToLeave);
        }
    }

    public boolean isLead() {
        return leadingTeams != null && !leadingTeams.isEmpty();
    }

    public void submitProject(HackathonEvent event, String projectName, String link, Challenge challenge, Map<Tool, String> selectedTools) throws Exception {
        Team myTeam = this.getTeams().stream()
                .filter(t -> t.getEvent().getHackeventId().equals(event.getHackeventId()))
                .findFirst()
                .orElseThrow(() -> new Exception("Error: You don't have a team for this event!"));

        selectTools(selectedTools);

        myTeam.submitProject(projectName, link, challenge, selectedTools);
    }

    private void selectTools(Map<Tool,String> selectedTools) {
        System.out.println("System is recording the " + selectedTools.size() + " tools selected by " + getName());
    }

    @Override
    public void register() {
        System.out.println("Participant registered");
    }



}
