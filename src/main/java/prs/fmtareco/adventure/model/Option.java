package prs.fmtareco.adventure.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "options")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "section_id")
    private Section section;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "goto_section_number", nullable = false)
    private Integer gotoSectionNumber;

    @OneToOne(mappedBy = "option", cascade = CascadeType.ALL, orphanRemoval = true)
    private Consequence consequence;

    public void setOptionConsequence(Consequence consequence) {
        this.consequence = consequence;
        this.consequence.setOption(this);
    }

    public static Option create(String description, int gotoSectionNumber) {
        return Option.builder()
                .description(description)
                .gotoSectionNumber(gotoSectionNumber)
                .build();
    }

}
