package prs.fmtareco.adventure.model;

import jakarta.persistence.*;
import lombok.*;
import prs.fmtareco.adventure.exceptions.InvalidEnumValueException;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.stream.Collectors;

@Entity
@Table(name = "consequences")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Consequence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "option_id")
    private Option option;

    @Column(nullable = false)
    private Integer value;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    public enum Type implements Serializable {
        GAIN_HEALTH,
        LOSE_HEALTH;
        public static Type from(String type) {
            try {
                return Type.valueOf(type);
            } catch (IllegalArgumentException e) {
                throw new InvalidEnumValueException("Consequence Type", type,valuesToString());
            }
        }
        public static String valuesToString() {
            return EnumSet.allOf(Type.class).stream().map(Enum::toString).collect(Collectors.joining(","));
        }
    }
}
