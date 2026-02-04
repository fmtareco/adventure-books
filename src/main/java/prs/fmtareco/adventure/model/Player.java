package prs.fmtareco.adventure.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import prs.fmtareco.adventure.exceptions.InvalidEnumValueException;

import java.io.Serializable;

@Entity
@Table(name = "players")
@Getter
@Setter
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(name = "current_section_number", nullable = false)
    private Integer currentSectionNumber;

    @Column(nullable = false)
    private Integer currentHealth = 10;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    public enum Status implements Serializable {
        ACTIVE,
        PAUSED,
        FAILED,
        SUCCEEDED;
        public static Section.Type from(String type) {
            try {
                return Section.Type.valueOf(type);
            } catch (IllegalArgumentException e) {
                throw new InvalidEnumValueException("Player Status", type, Status.values().toString());
            }
        }
    }

}
