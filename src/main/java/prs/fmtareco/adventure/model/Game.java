package prs.fmtareco.adventure.model;

import jakarta.persistence.*;
import lombok.*;
import prs.fmtareco.adventure.exceptions.InvalidEnumValueException;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.stream.Collectors;

@Entity
@Table(name = "games")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Game {

    public static final int INITIAL_HEALTH = 10;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne(optional = false)
    @JoinColumn(name = "section_id")
    private Section section;

    @ManyToOne
    @JoinColumn(name = "previous_section_id")
    private Section previousSection;

    @ManyToOne
    @JoinColumn(name = "option_id")
    private Option chosenOption;

    @Builder.Default
    @Column(nullable = false)
    private Integer health = INITIAL_HEALTH;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    public enum Status implements Serializable {
        STARTED,
        RESTARTED,
        ACTIVE,
        FAILED,
        SUCCEEDED;
        public static Status from(String type) {
            try {
                return Status.valueOf(type);
            } catch (IllegalArgumentException e) {
                throw new InvalidEnumValueException("Game Status", type,valuesToString());
            }
        }
        public static String valuesToString() {
            return EnumSet.allOf(Status.class).stream().map(Enum::toString).collect(Collectors.joining(","));
        }
    }

    public static Game create(Book book, Section section) {
        Game game = new Game();
        game.setBook(book);
        game.setSection(section);
        game.setStatus(Game.Status.STARTED);
        return game;
    }

}
