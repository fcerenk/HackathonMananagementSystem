package pl.pja.mas.hackathonmanagementsystem.model;


import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="mentorship")
@Builder
@Getter @Setter

public class Mentorship implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Transient
    private static List<Mentorship> extent = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mentorshipId;

    private float availableHours;

    @ManyToOne
    @JoinColumn(name = "mentorId", nullable = false)
    private Mentor mentor;

    @ManyToOne
    @JoinColumn(name= "eventId", nullable = false)
    private HackathonEvent event;

    @PostPersist
    @PostLoad
    protected void addToExtent()
    {
        if (!extent.contains(this)) extent.add(this);
    }

    public static List<Mentorship> getExtent() {
        return Collections.unmodifiableList(extent);
    }
}
