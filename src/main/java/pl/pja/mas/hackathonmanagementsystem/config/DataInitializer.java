package pl.pja.mas.hackathonmanagementsystem.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.pja.mas.hackathonmanagementsystem.enums.EvaluationStatus;
import pl.pja.mas.hackathonmanagementsystem.enums.EventStatus;
import pl.pja.mas.hackathonmanagementsystem.enums.RewardType;
import pl.pja.mas.hackathonmanagementsystem.model.*;
import pl.pja.mas.hackathonmanagementsystem.repository.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final OrganizerRepository organizerRepository;
    private final ParticipantRepository participantRepository;
    private final JudgeRepository judgeRepository;
    private final HackathonEventRepository eventRepository;
    private final TeamRepository teamRepository;
    private final ToolRepository toolRepository;
    private final MentorRepository mentorRepository;
    private final ChallengeRepository challengeRepository;
    private final EvaluationRepository evaluationRepository;
    private final MentorshipRepository mentorshipRepository;
    private final RewardRepository rewardRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final ToolUsageRepository toolUsageRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("--- DATA INITIALIZER:  ---");


        Organizer organizer = Organizer.builder().name("Alice").surname("Admin").age(30).email("alice@admin.com").password("123").build();
        organizerRepository.save(organizer);

        Participant p1 = Participant.builder().name("Bob").surname("Marley").age(22).email("bob@mail.com").password("1234").build();
        Participant p2 = Participant.builder().name("John").surname("Doe").age(25).email("john@gmail.com").password("1235").build();
        Participant p3 = Participant.builder().name("Jane").surname("Smith").age(19).email("jane@mail.com").password("pass").build();
        Participant p4 = Participant.builder().name("Gary").surname("Code").age(24).email("gary@mail.com").password("123").build();

        participantRepository.save(p1);
        participantRepository.save(p2);
        participantRepository.save(p3);
        participantRepository.save(p4);

        Judge judge1 = Judge.builder().name("Eva").surname("Brown").age(35).email("eva@judge.com").password("pass1234").build();
        judgeRepository.save(judge1);
        Judge judge2 = Judge.builder().name("Ceren").surname("Kilic").age(24).email("ceren@kilic.com").password("123456").build();
        judgeRepository.save(judge2);

        Mentor mentor1 = Mentor.builder().name("Charlie").surname("Green").age(45).email("charlie@mentor.com").password("pass123").build();
        mentorRepository.save(mentor1);

        Tool tool1 = Tool.builder().toolName("TensorFlow").build();
        Tool tool2 = Tool.builder().toolName("PyTorch").build();
        toolRepository.save(tool1);
        toolRepository.save(tool2);



        HackathonEvent event1 = HackathonEvent.builder()
                .name("AI Hackathon")
                .description("AI challenge event")
                .location("Warsaw")
                .duration("2 days")
                .startDate(LocalDateTime.now().minusDays(1))
                .eventStatus(EventStatus.ACTIVE)
                .organizer(organizer)
                .build();

        event1.registerParticipant(p1);
        event1.registerParticipant(p2);
        event1.registerParticipant(p3);
        event1.registerParticipant(p4);
        event1.assignJudge(judge1);
        event1.assignJudge(judge2);

        event1.manageChallenges("Image Classification", "Classify images using ML");
        event1.manageChallenges("Chatbot", "Build a chatbot");


        event1.manageRewards("Best Project", RewardType.PRIZE);
        event1.manageRewards("Most Innovative", RewardType.CERTIFICATE);

        eventRepository.save(event1);
        p1.createNewTeam("Team Alpha", event1);


        Team team1 = p1.getTeams().stream().filter(t -> t.getEvent().equals(event1)).findFirst().orElseThrow();
        p4.joinExistingTeam(team1);
        teamRepository.save(team1);
        p3.createNewTeam("Team Beta", event1);

        Team team2 = p3.getTeams().stream().filter(t -> t.getEvent().equals(event1)).findFirst().orElseThrow();
        p2.joinExistingTeam(team2);
        teamRepository.save(team2);
        Map<Tool, String> toolsTeam1 = new HashMap<>();
        toolsTeam1.put(tool1, "2.0");
        team1.submitProject("Cat Detector", "http://github.com/catdetector",
                event1.getChallengeByTitle("Image Classification"), toolsTeam1);
        teamRepository.save(team1);

        Map<Tool, String> toolsTeam2 = new HashMap<>();
        toolsTeam2.put(tool2, "1.7");
        team2.submitProject("Smart Chatbot", "http://github.com/chatbot",
                event1.getChallengeByTitle("Chatbot"), toolsTeam2);
        teamRepository.save(team2);
        event1.endDuration();
        eventRepository.save(event1);


        HackathonEvent event2 = HackathonEvent.builder()
                .name("Java Marathon")
                .description("Pure Java coding challenges for experts.")
                .location("Online")
                .duration("48 hours")
                .startDate(LocalDateTime.now().plusDays(5))
                .organizer(organizer)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        event2.manageRewards("Grand Prize", RewardType.PRIZE);
        try {
            event2.assignJudge(judge2);
            //event2.assignJudge(judge1);
        } catch (Exception e) {

        }
        event2.manageChallenges("Algorithm Optimization", "Optimize Java stream performance");
        event2.manageChallenges("Spring Boot API", "Create a REST API with Spring");

        eventRepository.save(event2);





        /*Submission sub1 = team1.getSubmissions().get(0);
        Evaluation eval1 = Evaluation.builder()
                .score(85)
                .feedback("Good work!")
                .evaluationDate(LocalDateTime.now())
                .status(EvaluationStatus.COMPLETED)
                .submission(sub1)
                .judge(judge1)
                .build();
        sub1.addEvaluation(eval1);
        judge1.addEvaluation(eval1);
        evaluationRepository.save(eval1);


        Submission sub2 = team2.getSubmissions().get(0);
        Evaluation eval2 = Evaluation.builder()
                .score(90)
                .feedback("Excellent!")
                .evaluationDate(LocalDateTime.now())
                .status(EvaluationStatus.COMPLETED)
                .submission(sub2)
                .judge(judge1)
                .build();
        sub2.addEvaluation(eval2);
        judge1.addEvaluation(eval2);
        evaluationRepository.save(eval2);

         */




        /*Reward bestProjectReward = event1.getRewards().stream()
                .filter(r -> r.getName().equals("Best Project"))
                .findFirst().orElseThrow();

        team2.addWonReward(bestProjectReward);
        rewardRepository.save(bestProjectReward);
        teamRepository.save(team2);

         */

        // Mentorship
        mentor1.joinEventAsMentor(event1, 10.5f);
        mentorshipRepository.saveAll(mentor1.getMentorships());

        System.out.println("--- ✅ Full DataInitializer complete. Team Beta won! ---");
    }
}