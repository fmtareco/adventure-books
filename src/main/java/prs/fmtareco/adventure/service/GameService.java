package prs.fmtareco.adventure.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import prs.fmtareco.adventure.dtos.BookSummary;
import prs.fmtareco.adventure.dtos.GameChoices;
import prs.fmtareco.adventure.dtos.GameDetails;
import prs.fmtareco.adventure.dtos.GameOption;
import prs.fmtareco.adventure.exceptions.*;
import prs.fmtareco.adventure.model.*;
import prs.fmtareco.adventure.repository.BookRepository;
import prs.fmtareco.adventure.repository.CategoryRepository;
import prs.fmtareco.adventure.repository.GameRepository;
import prs.fmtareco.adventure.repository.SectionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GameService {


    private final BookRepository bookRepo;
    private final GameRepository gameRepo;

    public GameService(BookRepository bookRepo, GameRepository gameRepo) {
        this.bookRepo = bookRepo;
        this.gameRepo = gameRepo;
    }

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
        game.setStatus("The game just started !!!");
        gameRepo.save(game);
        return toGameDetails(game);
    }

    public  GameDetails getGameDetails(Long gameId) {
        Game game = gameRepo.findById(gameId)
                .orElseThrow(()
                        -> new GameNotFoundException(gameId));
        return toGameDetails(game);
    }

    public GameChoices getCurrentChoices(Long gameId) {
        Game game = gameRepo.findById(gameId)
                .orElseThrow(()
                        -> new GameNotFoundException(gameId));
        List<GameOption> options = new ArrayList<>();
        int optionNo = 1;
        for(Option opt : game.getSection().getOptions()) {
            options.add(
                    GameOption.builder()
                            .option(optionNo++)
                            .description(opt.getDescription())
                            .build()
            );
        }
        GameChoices gc = GameChoices.builder()
                .options(options)
                .build();
        return gc;
    }

    public GameDetails takeOption(Long gameId, Integer optionNo) {
        Game game = gameRepo.findById(gameId)
                .orElseThrow(()
                        -> new GameNotFoundException(gameId));
        List<Option> currentOptions = game.getSection().getOptions();
        if (optionNo>currentOptions.size())
            throw new InvalidOptionException(optionNo, currentOptions.size());
        Option opt = currentOptions.get(optionNo-1);
        game.setOption(opt);
        gameRepo.save(game);
        return toGameDetails(game);
    }

    private GameDetails toGameDetails(Game game) {
        return GameDetails.builder()
                .game(game.getId())
                .book(game.getBook().getId())
                .health(game.getHealth())
                .section(game.getSection().getSectionNumber())
                .status(game.getStatus())
                .build();
    }


}
