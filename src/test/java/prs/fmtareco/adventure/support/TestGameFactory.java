package prs.fmtareco.adventure.support;

import prs.fmtareco.adventure.model.Book;
import prs.fmtareco.adventure.model.Game;
import prs.fmtareco.adventure.model.Section;

public class TestGameFactory {

    public static Game createGame(Book book) {
        Section section = book.getInitialSection()
                .orElse(null);
        return Game.create(book, section);
    }

}
