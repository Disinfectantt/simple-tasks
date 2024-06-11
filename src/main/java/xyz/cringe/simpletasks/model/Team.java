package xyz.cringe.simpletasks.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "teams")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private Boolean enabled = false;
}
