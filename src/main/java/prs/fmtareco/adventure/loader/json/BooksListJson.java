package prs.fmtareco.adventure.loader.json;

import prs.fmtareco.adventure.model.Book;

import java.util.List;

public record BooksListJson(
    List<BookJson> books
){}

