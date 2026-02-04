package prs.fmtareco.adventure.loader.json;

import prs.fmtareco.adventure.model.Consequence;

public record ConsequenceJson(
    Consequence.Type type,
    Integer value,
    String text
) { }
