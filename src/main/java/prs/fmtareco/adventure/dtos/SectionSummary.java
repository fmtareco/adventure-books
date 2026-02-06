package prs.fmtareco.adventure.dtos;

import jakarta.persistence.*;
import lombok.Builder;
import prs.fmtareco.adventure.model.Book;

import java.io.Serializable;
import java.util.List;

/**
 *
 * Section Summary to be retrieved on Book Details
 *
 * DTO Pattern (Data Transfer Object)
 */
@Builder
public record SectionSummary(
    Integer id,
    Integer sectionNumber,
    String text
) implements Serializable {}


