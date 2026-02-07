package prs.fmtareco.adventure.dtos;

import lombok.Builder;

import java.io.Serializable;

/**
 *
 * Consequence info to add consequence to option
 * DTO Pattern (Data Transfer Object)
 */
@Builder
public record ConsequenceRequest(
        int value,
        String text,
        String type
)  implements Serializable {}

