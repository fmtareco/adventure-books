package prs.fmtareco.adventure.dtos;

import lombok.Builder;

import java.io.Serializable;


/**
 *
 * Book Summary to be retrieved on books browsing
 * DTO Pattern (Data Transfer Object)
 */
@Builder
public record BookSimulation(
    Long id,
    String title,
    int numberOfSuccessfulPaths,
    int numberOfFailurePaths,
    int numberOfLoopPaths
) implements Serializable {}