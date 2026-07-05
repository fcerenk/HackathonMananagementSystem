package pl.pja.mas.hackathonmanagementsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pja.mas.hackathonmanagementsystem.model.HackathonEvent;
import pl.pja.mas.hackathonmanagementsystem.repository.HackathonEventRepository;
import pl.pja.mas.hackathonmanagementsystem.model.*;
import pl.pja.mas.hackathonmanagementsystem.repository.*;
import pl.pja.mas.hackathonmanagementsystem.enums.EvaluationStatus;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HackathonService {

    private final HackathonEventRepository eventRepository;
    private final SubmissionRepository submissionRepository;
    private final EvaluationRepository evaluationRepository;
    private final JudgeRepository judgeRepository;
    private final RewardRepository rewardRepository;
    private final TeamRepository teamRepository;

    public List<HackathonEvent> getAllEvents() {
        return eventRepository.findAll();
    }

    public Submission getSubmissionById(Long id) {
        return submissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Submission not found"));
    }

    @Transactional(readOnly = true)
    public HackathonEvent getEventById(Long id) {

        HackathonEvent event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + id));


        if (event.getOrganizer() != null) {
            event.getOrganizer().viewEventDetails();
        }


        return event;
    }

    public List<Judge> getAllJudges() {
        return judgeRepository.findAll();
    }

    @Transactional
    public void addEvaluation(Long submissionId,Long judgeId, int score, String feedback) {
        Submission submission = getSubmissionById(submissionId);
        List<Judge> allJudges = judgeRepository.findAll();

        List<Judge> alreadyVotedJudges = submission.getEvaluations().stream()
                .map(Evaluation::getJudge)
                .toList();

        //Judge activeJudge = allJudges.stream()
                //.filter(j -> !alreadyVotedJudges.contains(j))
                //.findFirst()
                //.orElse(allJudges.get(0));
        //Judge activeJudge = judgeRepository.findAll().stream().findFirst()
          //.orElseThrow(() -> new RuntimeException("No judge found in database!"));
        Judge activeJudge = judgeRepository.findById(judgeId)
                .orElseThrow(() -> new RuntimeException("Selected judge not found!"));
        Evaluation eval = Evaluation.builder()
                .score(score)
                .feedback(feedback)
                .evaluationDate(LocalDateTime.now())
                .status(EvaluationStatus.COMPLETED)
                .submission(submission)
                .judge(activeJudge)
                .build();


        submission.addEvaluation(eval);
        activeJudge.addEvaluation(eval);

        evaluationRepository.save(eval);
        submissionRepository.save(submission);
        if (submission.getAverageScore() >= 90.0) {

            Reward innovativeReward = submission.getTeam().getEvent().getRewards().stream()
                    .filter(r -> r.getName().equals("Most Innovative"))
                    .findFirst()
                    .orElse(null);

            if (innovativeReward != null && !submission.getTeam().getWonRewards().contains(innovativeReward)) {
                submission.getTeam().addWonReward(innovativeReward);


                rewardRepository.save(innovativeReward);
                teamRepository.save(submission.getTeam());

                System.out.println("🎉 AUTOMATIC AWARD: Team " + submission.getTeam().getTeamName() + " scored high and won a prize!");
            }
        }
    }
}