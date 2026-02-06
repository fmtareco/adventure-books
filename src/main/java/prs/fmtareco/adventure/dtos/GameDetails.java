package prs.fmtareco.adventure.dtos;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record GameDetails(
        Long game,
        Long book,
        Integer health,
        Integer section,
        String status
) implements Serializable {}

