package prs.fmtareco.adventure.dtos;

import lombok.Builder;

import java.io.Serializable;

/**
 *
 * Consequence info to add option to section
 * DTO Pattern (Data Transfer Object)
 */
@Builder
public record OptionSummary(
        String description,
        Integer gotoSectionNumber,
        String consequence
)  implements Serializable {}
