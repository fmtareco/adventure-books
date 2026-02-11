package prs.fmtareco.adventure.dtos;

import lombok.Builder;

import java.io.Serializable;
import java.util.List;

/**
 *
 * Section Summary to be retrieved on Book Details
 * DTO Pattern (Data Transfer Object)
 */
@Builder
public record SectionDetails(
    Integer sectionNumber,
    String type,
    String text,
    List<OptionSummary> options
) implements Serializable {}


