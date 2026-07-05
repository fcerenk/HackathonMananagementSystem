package pl.pja.mas.hackathonmanagementsystem.model;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pl.pja.mas.hackathonmanagementsystem.enums.EventStatus;
import pl.pja.mas.hackathonmanagementsystem.enums.RewardType;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@SuperBuilder
@Getter @Setter
@NoArgsConstructor
@Table(name = "organizers")
public class Organizer  extends User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @OneToMany(mappedBy = "organizer", cascade = CascadeType.ALL)
    private List<HackathonEvent> events = new ArrayList<>();

    public void addEvent(HackathonEvent event) {
        if(!events.contains(event)) {
            events.add(event);
            event.setOrganizer(this);
        }
    }

    public void removeEvent(HackathonEvent event) {
        if(events.contains(event)) {
            events.remove(event);
            event.setOrganizer(null);
        }

    }

    public void createNewEvent(String eventName, String location, LocalDateTime date) {

        HackathonEvent newEvent = HackathonEvent.builder()
                .name(eventName)
                .location(location)
                .startDate(date)
                .organizer(this)
                .build();
        this.addEvent(newEvent);
        System.out.println("New event is created " + eventName);
    }



    public void publishEvent(HackathonEvent event) {
        if (event != null) event.publish();
    }

    public void assignJudge(HackathonEvent event, Judge judge) throws Exception {
        if (event != null) event.assignJudge(judge);
    }

    public void manageChallenges(HackathonEvent event,String title, String description) {
        if (event != null) {
            try {
                event.manageChallenges(title, description);
                System.out.println("Organizer " + getName() + " defined a challenge: " + title);
            } catch (Exception e) {
                System.out.println("Error adding challenge: " + e.getMessage());
            }
        }
    }

    public void manageReward(HackathonEvent event, String rewardName, RewardType type) {
        if (event != null) {
            event.manageRewards(rewardName,type);
            System.out.println("Organizer " + getName() + " added a new reward to event: " + event.getName());
        } else{
            System.out.println("Error: Cannot manage rewards for a null event.");
        }
    }


    //change event status
    public void changeEventStatus(HackathonEvent event, EventStatus newStatus) {
        if (event != null) {
            event.setEventStatus(newStatus);
            System.out.println("Event status is updated: " + newStatus);
        }
    }

    @Override
    public void register() {
        System.out.println("Organizer registered");
    }


}
