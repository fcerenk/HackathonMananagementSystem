package pl.pja.mas.hackathonmanagementsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tool_usages")
public class ToolUsage implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Transient
    private static List<ToolUsage> extent = new ArrayList<>();

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long toolUsageId;

    @NotBlank
    private String version; // Specific version used in this submission

    @ManyToOne
    @JoinColumn(name = "submission_id", nullable = false)
    private Submission submission;

    @ManyToOne
    @JoinColumn(name = "tool_id", nullable = false)
    private Tool tool;

    @PostPersist @PostLoad
    protected void addToExtent() {
        if (!extent.contains(this)) extent.add(this);
    }

    public static List<ToolUsage> getExtent() {
        return Collections.unmodifiableList(extent);
    }
}