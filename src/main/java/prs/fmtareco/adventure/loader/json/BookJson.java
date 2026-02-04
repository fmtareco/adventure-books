package prs.fmtareco.adventure.loader.json;

import prs.fmtareco.adventure.model.Book;
import java.util.List;

public record BookJson(
    String title,
    String author,
    Book.Difficulty difficulty,
    List<SectionJson> sections
){}

