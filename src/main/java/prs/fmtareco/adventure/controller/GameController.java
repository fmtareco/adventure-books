package prs.fmtareco.adventure.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prs.fmtareco.adventure.dtos.GameDetails;
import prs.fmtareco.adventure.dtos.GameSummary;
import prs.fmtareco.adventure.service.GameService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService svc) {
        this.gameService = svc;
    }

    /**
     * POST /api/games/start/{book_id}
     * starts a game on the book identified w/ arg id
     * @param book_id - identifies the book
     * @return GameDetails record with the game details & status
     *
     */
    @PostMapping("/start/{book_id}")
    public ResponseEntity<GameDetails> startGame(@PathVariable Long book_id) {
        GameDetails gd = gameService.startGame(book_id);
        return ResponseEntity.ok(gd);
    }

    /**
     * GET - /api/games
     * fetches a list of SectionSummary with the sections of identified book
     * @param page - determines the page from where the list starts
     * @param size - determines the number of books returned
     * @param ascending - indicates the sort direction
     * @return List of SectionSummary
     */
    @GetMapping
    public ResponseEntity<List<GameSummary>> getAllGames(
            @RequestParam(required = false) String status,
            @RequestParam(value = "page", defaultValue = "0" ) int page,
            @RequestParam(value = "size", defaultValue = "10" ) int size,
            @RequestParam (defaultValue = "true") boolean ascending) {
        Sort sort = getGamesSort(ascending);
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(
                gameService.listAllGames(Optional.of(status), pageable)
        );
    }
    /**
     * return the Games Sort criteria
     * @param ascending - indicates the input sort direction
     * @return Sort criteria
     */
    protected Sort getGamesSort(
            boolean ascending
    ) {
        Sort.Direction direction = ascending ?
                Sort.Direction.ASC:
                Sort.Direction.DESC;
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(direction, "id"));
        return Sort.by(orders);
    }

    /**
     * GET /{game_id}
     * queries an existent game
     * @param game_id - ID of the game to consult
     * @return GameDetails record with the game details & status
     */
    @GetMapping("/{game_id}")
    public ResponseEntity<GameDetails> getGameDetails(@PathVariable Long game_id) {
        return ResponseEntity.ok(gameService.getGameDetails(game_id));
    }

    /**
     * POST /{game_id}/options/{option_no}
     * takes an option of the currently available on the game
     * @param game_id - ID of the game to consult
     * @return GameDetails record with the game details & status
     */
    @PostMapping("/{game_id}/options/{option_no}")
    public ResponseEntity<GameDetails> takeOption(@PathVariable Long game_id, @PathVariable Integer option_no) {
        GameDetails gd = gameService.takeOption(game_id, option_no);
        return ResponseEntity.ok(gd);
    }
}
