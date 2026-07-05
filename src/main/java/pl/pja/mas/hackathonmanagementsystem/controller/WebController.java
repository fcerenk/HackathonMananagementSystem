package pl.pja.mas.hackathonmanagementsystem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pl.pja.mas.hackathonmanagementsystem.model.Submission;
import pl.pja.mas.hackathonmanagementsystem.service.HackathonService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
@Controller
@RequiredArgsConstructor
public class WebController {

    private final HackathonService hackathonService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("events", hackathonService.getAllEvents());
        return "index";
    }

    @GetMapping("/event/{id}")
    public String eventDetails(@PathVariable Long id, Model model) {
        model.addAttribute("event", hackathonService.getEventById(id));
        return "details";
    }


    @GetMapping("/submission/{id}/evaluate")
    public String showEvaluationForm(@PathVariable Long id, Model model) {
        model.addAttribute("submission", hackathonService.getSubmissionById(id));
        model.addAttribute("judges", hackathonService.getAllJudges());
        return "evaluation_form";
    }


    @PostMapping("/evaluate")
    public String submitEvaluation(@RequestParam Long submissionId,
                                   @RequestParam Long judgeId,
                                   @RequestParam int score,
                                   @RequestParam String feedback) {

        hackathonService.addEvaluation(submissionId, judgeId,score, feedback);

        Submission sub = hackathonService.getSubmissionById(submissionId);
        Long eventId = sub.getTeam().getEvent().getHackeventId();

        return "redirect:/event/" + eventId;
    }
}