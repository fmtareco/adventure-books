package prs.fmtareco.adventure.dtos;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import prs.fmtareco.adventure.model.Book;
import prs.fmtareco.adventure.model.Category;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * DTO Pattern (Data Transfer Object)
 * Passing all output data to client
 */
@Builder
public record BookResponse(
    Long id,
    String title,
    String author,
    List<String> categories,
    String difficulty
) implements Serializable {}