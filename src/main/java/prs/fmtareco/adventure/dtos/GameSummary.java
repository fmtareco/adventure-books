package prs.fmtareco.adventure.dtos;

import lombok.Builder;

import java.io.Serializable;


@Builder
public record GameSummary(
        Long game,
        Long book,
        Integer health,
        String status
) implements Serializable {}

