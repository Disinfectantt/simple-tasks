package xyz.cringe.simpletasks.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    private Integer priority = 0;

    private Integer difficulty = 0;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private TaskStatus status;

    @Temporal(TemporalType.DATE)
    private Date startDate;

    private Date endDate;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "workers",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "task_id")
    )
    private Set<User> workers;
}
