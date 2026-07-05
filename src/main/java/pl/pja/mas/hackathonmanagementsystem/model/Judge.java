package pl.pja.mas.hackathonmanagementsystem.model;


import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pl.pja.mas.hackathonmanagementsystem.enums.EvaluationStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@SuperBuilder
@Table(name = "judges")
@NoArgsConstructor

public class Judge extends User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @ManyToMany(mappedBy = "judges")
    @Builder.Default
    private List<HackathonEvent> assignedEvents = new ArrayList<>();

    public void evaluateSubmission(String projectName, int score, String feedback) {
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("Score must be 0-100");
        }
        System.out.println("Evaluated '" + projectName + "' - Score: " + score);
    }


    @OneToMany(mappedBy = "judge")
    @Builder.Default
    private List<Evaluation> evaluations = new ArrayList<>();

    public void addEvaluation(Evaluation evaluation) {
        if (evaluation != null && !evaluations.contains(evaluation)) {
            this.evaluations.add(evaluation);
            evaluation.setJudge(this);
        }
    }



    public void removeEvaluation(Evaluation evaluation) {
        if (this.evaluations.contains(evaluation)) {
            this.evaluations.remove(evaluation);
            evaluation.setJudge(null);
            System.out.println("Evaluation removed from Judge " + getName() + "'s records.");
        }
    }
    private void provideScore(Evaluation evaluation, float score) {
        evaluation.setScore(score);
        System.out.println("Score " + score + " assigned to evaluation " + evaluation.getEvaluationID());
    }

    private void provideFeedback(Evaluation evaluation, String feedback) {
        if (feedback != null && !feedback.isEmpty()) {
            evaluation.setFeedback(feedback);
            System.out.println("Feedback provided for evaluation " + evaluation.getEvaluationID());
        }
    }


    public void viewAssignedSubmissions() {
        System.out.println("--- Assigned Submissions for Judge: " + getName() + " ---");

        for (HackathonEvent event : assignedEvents) {
            System.out.println("Event: " + event.getName());
            for (Team team : event.getTeams()) {
                if (team.getSubmissions() != null && !team.getSubmissions().isEmpty()) {
                    team.getSubmissions().forEach(sub ->
                            System.out.println("  - Project: " + sub.getProjectName() + " (Team: " + team.getTeamName() + ")")
                    );
                }
            }
        }
    }

    public void evaluateSubmission(Submission submission, float score, String feedback) throws Exception {
        Evaluation evaluation = Evaluation.builder()
                .score(score)
                .feedback(feedback)
                .evaluationDate(LocalDateTime.now())
                .status(EvaluationStatus.DRAFT)
                .build();

        this.addEvaluation(evaluation);
        submission.addEvaluation(evaluation);

        evaluation.validateScore();

        System.out.println("Judge " + getName() + " started evaluation for: " + submission.getProjectName());
    }

    @Override
    public void register() {
        System.out.println("Judge registered");
    }

}

