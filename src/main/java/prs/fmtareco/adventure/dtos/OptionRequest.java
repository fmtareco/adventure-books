package prs.fmtareco.adventure.dtos;

import lombok.Builder;

import java.io.Serializable;

/**
 *
 * Consequence info to add option to section
 * DTO Pattern (Data Transfer Object)
 */
@Builder
public record OptionRequest(
        String description,
        Integer gotoId,
        ConsequenceRequest consequence
)  implements Serializable {}
