package prs.fmtareco.adventure.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import prs.fmtareco.adventure.model.Book;
import prs.fmtareco.adventure.model.Consequence;
import prs.fmtareco.adventure.model.Game;
import prs.fmtareco.adventure.model.Section;
import prs.fmtareco.adventure.service.GameService;
import prs.fmtareco.adventure.support.TestBookFactory;
import prs.fmtareco.adventure.support.TestGameFactory;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class GameValidationTest {

    @InjectMocks
    GameService service;

    /**
     * executes the flow of basic game crossing the 3 sections of a valid book
     */
    @Test
    void concludeSimpleGame() {
        Book book = TestBookFactory.createValidBook();
        Game game = TestGameFactory.createGame(book);
        assertSame(Game.Status.STARTED, game.getStatus());
        assertEquals(10, game.getHealth());
        assertNotNull(game.getSection());

        service.applyOption(game, 1);
        assertEquals(Section.Type.NODE, game.getSection().getType());
        assertEquals(2, game.getSection().getSectionNumber());

        service.applyOption(game, 1);
        assertEquals(Section.Type.END, game.getSection().getType());
        assertEquals(30, game.getHealth());
        assertEquals(3, game.getSection().getSectionNumber());
    }

    /**
     * restarts a game after executes the basic successful flow
     */
    @Test
    public void restartSimpleGame() {
        Book book = TestBookFactory.createValidBook();
        Game game = TestGameFactory.createGame(book);
        assertSame(Game.Status.STARTED, game.getStatus());
        assertNotNull(game.getSection());
        assertEquals(10, game.getHealth());

        service.applyOption(game, 1);
        assertEquals(Section.Type.NODE, game.getSection().getType());
        assertEquals(2, game.getSection().getSectionNumber());
        assertEquals(20, game.getHealth());

        service.applyOption(game, 0);
        assertEquals(Section.Type.BEGIN, game.getSection().getType());
        assertEquals(1, game.getSection().getSectionNumber());
        assertEquals(10, game.getHealth());
    }

    /**
     * executes a successful game flow
     */
    @Test
    public void playGameGoodOptions() {
        Book book = TestBookFactory.createBookWithConsequences();
        Game game = TestGameFactory.createGame(book);
        assertSame(Game.Status.STARTED, game.getStatus());
        assertNotNull(game.getSection());
        assertEquals(10, game.getSection().getSectionNumber());

        service.applyOption(game, 1);
        assertEquals(Section.Type.NODE, game.getSection().getType());
        assertEquals(20, game.getSection().getSectionNumber());
        assertEquals(17, game.getHealth());

        service.applyOption(game, 1);
        assertEquals(Section.Type.END, game.getSection().getType());
        assertEquals(50, game.getSection().getSectionNumber());
        assertEquals(24, game.getHealth());
        assertSame(Game.Status.SUCCEEDED, game.getStatus());

    }

    /**
     * executes a unsuccessful game flow
     */
    @Test
    public void playGameBadOptions() {
        Book book = TestBookFactory.createBookWithConsequences();
        Game game = TestGameFactory.createGame(book);
        assertSame(Game.Status.STARTED, game.getStatus());
        assertNotNull(game.getSection());
        assertEquals(10, game.getSection().getSectionNumber());

        service.applyOption(game, 2);
        assertEquals(Section.Type.NODE, game.getSection().getType());
        assertEquals(30, game.getSection().getSectionNumber());
        assertEquals(3, game.getHealth());

        service.applyOption(game, 1);
        assertEquals(Section.Type.BEGIN, game.getSection().getType());
        assertEquals(10, game.getSection().getSectionNumber());
        assertEquals(-4, game.getHealth());
        assertSame(Game.Status.FAILED, game.getStatus());

        service.applyOption(game, 0);
        assertEquals(Section.Type.BEGIN, game.getSection().getType());
        assertEquals(10, game.getSection().getSectionNumber());
    }

    /**
     * applies directly a series of consequences on an active game
     */
    @Test
    public void applyConsequences() {
        Book book = TestBookFactory.createBookWithConsequences();
        Game game = TestGameFactory.createGame(book);
        assertSame(Game.Status.STARTED, game.getStatus());
        assertNotNull(game.getSection());
        assertEquals(10, game.getSection().getSectionNumber());

        Consequence csqDown = Consequence.create(
                Consequence.Type.LOSE_HEALTH, 6, "Lose 6");
        service.applyConsequence(game, csqDown);
        assertEquals(4, game.getHealth());

        Consequence csqUp = Consequence.create(
                Consequence.Type.GAIN_HEALTH, 7, "Gain 7");
        service.applyConsequence(game, csqUp);
        assertEquals(11, game.getHealth());

        Consequence csqEnd = Consequence.create(
                Consequence.Type.LOSE_HEALTH, 17, "Lose 17");
        service.applyConsequence(game, csqEnd);
        assertEquals(-6, game.getHealth());
        assertSame(Game.Status.FAILED, game.getStatus());
    }
}