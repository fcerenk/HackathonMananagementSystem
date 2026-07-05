package pl.pja.mas.hackathonmanagementsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import pl.pja.mas.hackathonmanagementsystem.enums.EvaluationStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="evaluations")
@Builder
public class Evaluation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Transient
    private static List<Evaluation> extent  = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long evaluationID;

    //constraint
    @Min(value = 0, message = "Score must be at least 0")
    @Max(value = 100, message = "Score cannot exceed 100")
    private float score;

    private String feedback;

    @NotNull
    private LocalDateTime evaluationDate;

    @Enumerated(EnumType.STRING)
    private EvaluationStatus status;



    @PostLoad
    @PostPersist
    protected void addToExtent() {
        if (!extent.contains(this)) {
            extent.add(this);
        }
    }

    public static List<Evaluation> getExtent() {
        return Collections.unmodifiableList(extent);
    }

    @ManyToOne
    @JoinColumn(name = "submission_id", nullable = false)
    private Submission submission;

    @ManyToOne
    @JoinColumn(name = "judge_id", nullable = false)
    private Judge judge;

    public void updateScore(float newScore) {
        this.score = newScore;
    }

    public void saveAsDraft() {
        this.status = EvaluationStatus.DRAFT;
        System.out.println("Evaluation saved as draft.");
    }

    public void validateScore() throws Exception {
        if (score < 0 || score > 100) {
            throw new Exception("Constraint Violation: Score must be between 0 and 100");
        }
    }

    public void submitEvaluation() throws Exception {
        validateScore();
        this.status = EvaluationStatus.COMPLETED;
        System.out.println("Evaluation submitted for team: " + submission.getTeam().getTeamName());
    }




}
