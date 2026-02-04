package prs.fmtareco.adventure.loader.json;

import prs.fmtareco.adventure.model.Section;

import java.util.List;

public record SectionJson(
    Integer id,
    String text,
    Section.Type type,
    List<OptionJson> options
) {}