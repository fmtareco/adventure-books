package prs.fmtareco.adventure.dtos;

import lombok.Builder;

import java.io.Serializable;
import java.util.List;

/**
 *
 * Consequence info to add new Books
 * DTO Pattern (Data Transfer Object)
 */
@Builder
public record BookRequest(
        String title,
        String author,
        String  difficulty,
        List<String> categories,
        List<SectionRequest> sections
) implements Serializable {}
