package prs.fmtareco.adventure.model;

import jakarta.persistence.*;
import lombok.*;
import prs.fmtareco.adventure.exceptions.InvalidEnumValueException;
import prs.fmtareco.adventure.exceptions.InvalidSectionException;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.Optional;
import java.util.stream.Collectors;

@Entity
@Table(name = "games")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne(optional = false)
    @JoinColumn(name = "section_id")
    private Section section;

    @Column(nullable = false)
    private Integer health = 10;

    @Column(nullable = false)
    private String status;

    public void setConsequence(Consequence csq) {
        if (csq == null)
            return;
        setStatus(csq.getText());
        if (csq.getType() == Consequence.Type.LOSE_HEALTH)
            setHealth(getHealth() - csq.getValue());
        else
            setHealth(getHealth() + csq.getValue());
    }

    public void setOption(Option opt) {
        int sectionNumber = opt.getGotoSectionNumber();
        Optional<Section> section = getBook().getSectionNumber(sectionNumber);
        if (section.isEmpty())
            throw new InvalidSectionException(sectionNumber);
        setSection(section.get());
        setConsequence(opt.getConsequence());
    }
}
