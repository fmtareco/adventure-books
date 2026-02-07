package prs.fmtareco.adventure.service;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import prs.fmtareco.adventure.dtos.GameDetails;
import prs.fmtareco.adventure.dtos.GameOption;
import prs.fmtareco.adventure.dtos.GameSummary;
import prs.fmtareco.adventure.exceptions.*;
import prs.fmtareco.adventure.model.*;
import prs.fmtareco.adventure.repository.BookRepository;
import prs.fmtareco.adventure.repository.GameRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GameService {

    private final static String RESTART_OPTION = "Restart Game";
    private final static String STARTED_BEFORE = "Book just Opened";
    private final static String STARTED_MOVE = "Game Started";
    private final static String RESTARTED_MOVE = "Game Restarted";

    private final BookRepository bookRepo;
    private final GameRepository gameRepo;

    public GameService(BookRepository bookRepo, GameRepository gameRepo) {
        this.bookRepo = bookRepo;
        this.gameRepo = gameRepo;
    }

    /**
     * starts a new game, based on the book w/ key=ID
     * - validates if the book exists and is in valid state
     * - sets the initial section to start the game
     * - sets the book status
     * - stores the book on the DB, via JPA
     *
     * @param bookId - identifies the book where the game will be based on
     * @return GameDetails record with the game details & status
     *
     */
    @Transactional
    public GameDetails startGame(Long bookId) {
        Book book = bookRepo.findById(bookId)
                .orElseThrow(()
                    -> new BookNotFoundException(bookId));
        Book.Condition condition = book.getCondition();
        if (condition != Book.Condition.OK)
            throw new InvalidBookException(bookId, condition.toString());
        Section section = book.getInitialSection()
                .orElseThrow(()
                    -> new InvalidBookException(bookId, condition.toString()));
        Game game = new Game();
        game.setBook(book);
        game.setSection(section);
        game.setStatus(Game.Status.STARTED);
        gameRepo.save(game);
        return toGameDetails(game);
    }

    /**
     * action following an option selection
     * - locates the game identified by the id
     * - chooses the action based on the selected option
     * - 0 - restarts the games, restoring health and moving to the initial section
     * - non-zero value, moves to the section determined by the option and applies the
     *   eventual consequence (loses aor adds health)
     *
     * @param gameId - the id of the game being operated
     * @param optionNo - the selected option (0 means RESTART)
     * @return GameDetails record with the game details & status after
     */
    @Transactional
    public GameDetails takeOption(Long gameId, Integer optionNo) {
        Game game = gameRepo.findById(gameId)
                .orElseThrow(()
                        -> new GameNotFoundException(gameId));
        if (optionNo==0)
            restartGame(game);
        else
            applyOption(game, chosenOption(game, optionNo));
        gameRepo.save(game);
        return toGameDetails(game);
    }

    /**
     * allows querying the details & status of a particular game
     *
     * @param gameId - key of the game to be queried
     * @return GameDetails record with the game details & status
     */
    public  GameDetails getGameDetails(Long gameId) {
        Game game = gameRepo.findById(gameId)
                .orElseThrow(()
                        -> new GameNotFoundException(gameId));
        return toGameDetails(game);
    }

    /**
     * returns a list of GameSummary with the selected games info
     *
     * @param pageable - handles the pagination and sorting settings
     * @return List of books (summary)
     */
    public List<GameSummary> listAllGames(Optional<String> status, Pageable pageable) {
        return status
                .map(s -> gameRepo.findAllByStatus(Game.Status.from(s), pageable)
                .stream()
                .map(this::toGameSummary)
                .toList())
                .orElseGet(() -> gameRepo.findAll(pageable)
                .stream()
                .map(this::toGameSummary)
                .toList());
    }


    /**
     * restarts a game by refiling the health and moving to the book initial section
     * Also updates the game status to RESTARTED
     *
     * @param game - context game
     */
    private void restartGame(Game game) {
        game.setPreviousSection(game.getSection());
        game.setChosenOption(null);
        Optional<Section> section = game.getBook().getInitialSection();
        game.setSection(section.orElse(null));
        game.setHealth(Game.INITIAL_HEALTH);
        game.setStatus(Game.Status.RESTARTED);
    }

    /**
     * returns the option in position from the game current section
     *
     * @param game - context game
     * @param optionNo - non-zero position of the option on the current section options array
     * @return - Option instance correspondent to the input position
     */
    private Option chosenOption(Game game, int optionNo) {
        List<Option> currentOptions = game.getSection().getOptions();
        if (optionNo>currentOptions.size())
            throw new InvalidOptionException(optionNo, currentOptions.size());
        return currentOptions.get(optionNo-1);
    }

    /**
     * applies the option section transition and eventual consequence on the game
     * - sets as new current section the one determined by the option go to ID
     * - applies the option consequence (if present) on the game health
     *
     * @param game - context game
     * @param opt - Option instance applied to the game
     */
    private void applyOption(Game game, Option opt) {
        game.setPreviousSection(game.getSection());
        game.setChosenOption(opt);
        int sectionNumber = opt.getGotoSectionNumber();
        Optional<Section> section = game.getBook().getSectionNumber(sectionNumber);
        if (section.isEmpty())
            throw new InvalidSectionException(sectionNumber);
        game.setSection(section.orElse(null));
        setConsequence(game, opt.getConsequence());
        setStatus(game);
    }

    /**
     * applies an option Consequence effect to the game
     * - increments or decrements the game health
     *
     * @param game - context game
     * @param csq - Consequence instance applied to the game
     */
    private void setConsequence(Game game, Consequence csq) {
        if (csq == null)
            return;
        int health = game.getHealth();
        if (csq.getType() == Consequence.Type.LOSE_HEALTH)
            health -= csq.getValue();
        else
            health += csq.getValue();
        game.setHealth(health);
    }

    /**
     * updates the status of the game
     * based on the current section and health
     *
     * @param game - context game
     */
    private void setStatus(Game game) {
        if (game.getSection().getType() == Section.Type.END) {
            game.setStatus(Game.Status.SUCCEEDED);
            return;
        }
        if (game.getHealth() <=0) {
            game.setStatus(Game.Status.FAILED);
            return;
        }
        game.setStatus(Game.Status.ACTIVE);
    }

    /**
     * creates a game option, with the arg position and text
     * @return GameOption instance
     */
    private GameOption createGameOption(int position, String text) {
        return GameOption.builder()
                .option(position)
                .description(text)
                .build();
    }

    /**
     * builds a list of the current game options, based on
     * the current section and game status.
     * Always inserts an option to restart the game, on the start of the list
     *
     * @param game - context game
     * @return list of GameOption instances (pair position+Option)
     */
    private List<GameOption> getCurrentOptions(Game game) {
        List<GameOption> options = new ArrayList<>();
        options.add(createGameOption(0, RESTART_OPTION));
        Game.Status status = game.getStatus();
        if (status!=Game.Status.SUCCEEDED && status!=Game.Status.FAILED) {
            int optionNo = 1;
            for(Option opt : game.getSection().getOptions()) {
                options.add(createGameOption(optionNo++, opt.getDescription()));
            }
        }
        return options;
    }

    /**
     * creates an instance of GameDetails DTO to return from an API call
     * converts the context game details and status into teh DTO fields
     *
     * @param game - context game
     * @return GameDetails DTO instance
     */
    private GameDetails toGameDetails(Game game) {
        Optional<Option> chosenOption = Optional.ofNullable(game.getChosenOption());
        Optional<Consequence> consequence = chosenOption
                .map(Option::getConsequence);
        Optional<Section> previousSection = Optional.ofNullable(game.getPreviousSection());
        String before = previousSection
                .map(Section::getText)
                .orElse("");
        String move = chosenOption
                .map(Option::getDescription)
                .orElse("");
        String outcome = consequence
                .map(Consequence::getText)
                .orElse("");
        switch (game.getStatus()) {
            case STARTED -> {
                before = STARTED_BEFORE;
                move = STARTED_MOVE;
            }
            case RESTARTED ->
                move = RESTARTED_MOVE;
        }
        return GameDetails.builder()
                .game(game.getId())
                .book(game.getBook().getTitle())
                .health(game.getHealth())
                .status(game.getStatus().toString())
                .before(before)
                .move(move)
                .outcome(outcome)
                .position(game.getSection().getText())
                .options(getCurrentOptions(game))
                .build();
    }

    /**
     * creates an instance of GameSummary DTO to return from an API call
     * converts the context game details and status into teh DTO fields
     *
     * @param game - context game
     * @return GameSummary DTO instance
     */
    private GameSummary toGameSummary(Game game) {
        return GameSummary.builder()
                .game(game.getId())
                .book(game.getBook().getId())
                .health(game.getHealth())
                .status(game.getStatus().toString())
                .build();
    }

}
