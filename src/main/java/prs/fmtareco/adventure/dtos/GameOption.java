package prs.fmtareco.adventure.dtos;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record GameOption(
        Integer option,
        String description
) implements Serializable {}

