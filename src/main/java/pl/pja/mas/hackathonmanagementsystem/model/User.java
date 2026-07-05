package pl.pja.mas.hackathonmanagementsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
public  abstract class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @Transient
    protected static List<User> allUsers = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotBlank(message = "Name is mandatory")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Surname is mandatory")
    @Column(nullable = false)
    private String surname;

    @Min(value = 18, message = "Minimum age is 18")
    @Column(nullable = false)
    private int age;


    @Column(unique = true, nullable = false)
    @NotBlank @Email
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;




    @PostLoad
    @PostPersist
    protected void addToExtent() {
        if (!allUsers.contains(this)) {
            allUsers.add(this);
        }
    }

    public static List<User> getExtent() {
        return Collections.unmodifiableList(allUsers);
    }

    public static User findByEmail(String email) {
        return allUsers.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst().orElse(null);
    }


    //abstract method
    public  abstract void register();

    public boolean login(String email, String password) {
        return this.email.equals(email) && this.password.equals(password);
    }

    public void updateProfile() {
        System.out.println("Profile updated for: " + this.email);
    }


    public void viewEventDetails() {
        System.out.println("Viewing event details...");
        calculateTeamScore();
    }

    private void calculateTeamScore() {
        System.out.println("Dynamically calculating team scores...");
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }







}
