package prs.fmtareco.adventure.dtos;

import lombok.Builder;

import java.io.Serializable;
import java.util.List;

@Builder
public record GameDetails(
        Long game,
        String book,
        String before,
        String move,
        String outcome,
        String position,
        Integer health,
        String status,
        List<GameOption>options
) implements Serializable {}

