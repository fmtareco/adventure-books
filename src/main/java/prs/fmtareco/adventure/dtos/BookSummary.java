package prs.fmtareco.adventure.dtos;

import lombok.Builder;

import java.io.Serializable;
import java.util.List;

/**
 *
 * Book Summary to be retrieved on books browsing
 * DTO Pattern (Data Transfer Object)
 */
@Builder
public record BookSummary(
    Long id,
    String title,
    String author,
    String condition,
    List<String> categories,
    String difficulty
) implements Serializable {}