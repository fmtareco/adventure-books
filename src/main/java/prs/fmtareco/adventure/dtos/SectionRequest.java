package prs.fmtareco.adventure.dtos;

import lombok.Builder;

import java.io.Serializable;
import java.util.List;

/**
 *
 * Consequence info to add section to book
 * DTO Pattern (Data Transfer Object)
 */
@Builder
public record SectionRequest(
        int id,
        String text,
        String type,
        List<OptionRequest> options
)  implements Serializable {}
