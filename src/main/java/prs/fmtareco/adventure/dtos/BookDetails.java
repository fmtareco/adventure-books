package prs.fmtareco.adventure.dtos;

import lombok.Builder;

import java.io.Serializable;
import java.util.List;

/**
 *
 * Book Details to be retrieved via API
 * DTO Pattern (Data Transfer Object)
 */
@Builder
public record BookDetails(
    Long id,
    String title,
    String author,
    String condition,
    String difficulty,
    List<String> categories,
    List<SectionSummary> sections
) implements Serializable {}