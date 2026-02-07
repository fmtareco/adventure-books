package prs.fmtareco.adventure.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prs.fmtareco.adventure.dtos.GameDetails;
import prs.fmtareco.adventure.service.GameService;

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
