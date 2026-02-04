package prs.fmtareco.adventure.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "options")
@Getter
@Setter
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "section_id")
    private Section section;

    @Lob
    @Column(nullable = false)
    private String description;

    @Column(name = "goto_section_number", nullable = false)
    private Integer gotoSectionNumber;

    @OneToOne(mappedBy = "option", cascade = CascadeType.ALL, orphanRemoval = true)
    private Consequence consequence;

}
