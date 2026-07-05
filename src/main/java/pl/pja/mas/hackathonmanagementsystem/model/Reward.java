package pl.pja.mas.hackathonmanagementsystem.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import pl.pja.mas.hackathonmanagementsystem.enums.RewardType;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
@Table(name="reward")
public class Reward  implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Transient
    private static List<Reward> extent = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rewardID;

    @NotBlank
    private String name;

    @Enumerated(EnumType.STRING)
    private RewardType type;

    //event
    @ManyToOne
    @JoinColumn(name="hackeventid", nullable = false)
    private HackathonEvent event;

    //win - team
    @ManyToOne
    @JoinColumn(name="winningTeam_ID")
    private Team winningTeam;

    public static List<Reward> getExtent() {
        return Collections.unmodifiableList(extent);
    }

    @PostLoad
    @PostPersist
    protected void addExtent () {
        if (!extent.contains(this)) {
            extent.add(this);
        }
    }



}
