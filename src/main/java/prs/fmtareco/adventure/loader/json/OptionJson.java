package prs.fmtareco.adventure.loader.json;

public record OptionJson(
    String description,
    Integer gotoId,
    ConsequenceJson consequence
) {
}
