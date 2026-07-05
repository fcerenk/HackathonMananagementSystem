package pl.pja.mas.hackathonmanagementsystem.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.ValueGenerationType;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="challenges")
@Getter @Setter

public class Challenge  implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Transient
    private static List<Challenge> extent = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long challengeId;

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @PostLoad
    @PostPersist
    protected void addToExtent() {
        if (!extent.contains(this)) {
            extent.add(this);
        }
    }

    public static List<Challenge> getExtent() {
        return Collections.unmodifiableList(extent);
    }

    @ManyToOne
    @JoinColumn(name="event_id", nullable = false)
    private HackathonEvent event;

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Submission> submissions = new ArrayList<>();

    public void addSubmission(Submission submission) {
        if (submission != null && !submissions.contains(submission)) {
            submissions.add(submission);
            submission.setChallenge(this);
        }
    }

}
