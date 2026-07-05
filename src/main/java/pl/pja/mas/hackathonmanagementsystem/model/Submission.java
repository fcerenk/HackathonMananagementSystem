package pl.pja.mas.hackathonmanagementsystem.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="submission")
@Builder
@Getter @Setter
public class Submission  implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Transient
    private static List<Submission> extent = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long submissionId;


    @NotBlank
    private String projectLink;

    @NotBlank
    private String projectName;

    @NotNull
    private LocalDateTime timestamp;

    //derived averagescore
    @Transient
    public float getAverageScore() {
        if ( evaluations==null|| evaluations.isEmpty()) return 0.0f;
        float total = 0;
        for (Evaluation eval : evaluations) {
            total += eval.getScore();
        }
        return total / evaluations.size();
    }

    @PostLoad
    @PostPersist
    protected void addExtent () {
        if (!extent.contains(this)) {
            extent.add(this);
        }
    }

    public static List<Submission> getExtent() {
        return Collections.unmodifiableList(extent);
    }



    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL,orphanRemoval=true)
    @Builder.Default
    private List<Evaluation> evaluations = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;



    public void addEvaluation(Evaluation evaluation) {
        if (evaluation != null && !evaluations.contains(evaluation)) {
            this.evaluations.add(evaluation);
            evaluation.setSubmission(this);
        }
    }

    public void removeEvaluation(Evaluation evaluation) {
        if (this.evaluations.contains(evaluation)) {
            this.evaluations.remove(evaluation);
            evaluation.setSubmission(null);
            System.out.println("Evaluation removed from submission. New average score is being calculated...");
        }
    }


    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ToolUsage> toolUsages = new ArrayList<>();

    public void addTool(Tool tool, String version) {
        if (tool == null || version == null) return;

        ToolUsage usage = ToolUsage.builder()
                .tool(tool)
                .version(version)
                .submission(this)
                .build();

        this.toolUsages.add(usage);
        tool.getUsages().add(usage);
        System.out.println("Tool Usage Recorded: " + tool.getToolName() + " (v" + version + ") for project " + projectName);
    }





}
