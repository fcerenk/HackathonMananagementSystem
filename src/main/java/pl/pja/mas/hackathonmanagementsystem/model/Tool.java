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
@Table(name="tools")
@Builder
public class Tool implements Serializable {
    @Transient
    private static List<Tool> extent = new ArrayList<>();

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long toolID;

    @NotBlank
    private String toolName;


    @PostLoad
    @PostPersist
    protected void addToExtent() {
        if (!extent.contains(this)) {
            extent.add(this);
        }
    }

    public static List<Tool> getExtent() {
        return Collections.unmodifiableList(extent);
    }

    @OneToMany(mappedBy = "tool", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ToolUsage> usages = new ArrayList<>();


}
