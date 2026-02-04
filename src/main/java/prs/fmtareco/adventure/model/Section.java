package prs.fmtareco.adventure.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import prs.fmtareco.adventure.exceptions.InvalidEnumValueException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(
    name = "sections",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"book_id", "section_number"})
    }
)
@Getter
@Setter
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "section_number", nullable = false)
    private Integer sectionNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id")
    private Book book;

    @Lob
    @Column(nullable = false)
    private String text;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> options = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    public enum Type implements Serializable {
        BEGIN,
        NODE,
        END;
        public static Type from(String type) {
            try {
                return Type.valueOf(type);
            } catch (IllegalArgumentException e) {
                throw new InvalidEnumValueException("Section Type", type,valuesToString());
            }
        }
        public static String valuesToString() {
            return EnumSet.allOf(Type.class).stream().map(Enum::toString).collect(Collectors.joining(","));
        }
    }

    public void addOption(Option option) {
        options.add(option);
        option.setSection(this);
    }


}
