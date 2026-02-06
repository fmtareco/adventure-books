package prs.fmtareco.adventure.dtos;

import lombok.Builder;

import java.io.Serializable;
import java.util.List;

@Builder
public record GameChoices(
        List<GameOption> options
) implements Serializable {}

