package prs.fmtareco.adventure.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prs.fmtareco.adventure.dtos.*;
import prs.fmtareco.adventure.model.Game;
import prs.fmtareco.adventure.service.BookService;
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
     * POST /api/games/{book_id}
     * starts a game on the book w/ arg id
     * Idempotent
     * @param book_id - identifies the book
     */
    @PostMapping("/{book_id}")
    public ResponseEntity<GameDetails> startGame(@PathVariable Long book_id) {
        GameDetails gd = gameService.startGame(book_id);
        return ResponseEntity.ok(gd);
    }

    @GetMapping("/{game_id}")
    public ResponseEntity<GameDetails> getGameDetails(@PathVariable Long game_id) {
        return ResponseEntity.ok(gameService.getGameDetails(game_id));
    }

    @GetMapping("/{game_id}/options")
    public ResponseEntity<GameChoices> getChoices(@PathVariable Long game_id) {
        return ResponseEntity.ok(gameService.getCurrentChoices(game_id));
    }

    @PostMapping("/{game_id}/options/{option_no}")
    public ResponseEntity<GameDetails> takeOption(@PathVariable Long game_id, @PathVariable Integer option_no) {
        GameDetails gd = gameService.takeOption(game_id, option_no);
        return ResponseEntity.ok(gd);
    }
}
