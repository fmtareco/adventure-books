package prs.fmtareco.adventure.integration;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import prs.fmtareco.adventure.model.Game;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
public class GameIntegrationTest {
    @Autowired
    private MockMvc mvc;

    /**
     * GET
     * tests the games API w/ different filters
     */
    @Test
    void checkGetGamesList() throws Exception {
        mvc.perform(get("/api/games"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").isNotEmpty());
        mvc.perform(get("/api/games?status=ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
        mvc.perform(get("/api/games?status=FAILED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    /**
     * GET/POST/GET
     * test the games api endpoint that returns the details of a game
     * - obtains the first valid book
     * - starts a new game
     * - queries the new game details
     */
    @Test
    void checkGetGameDetails() throws Exception {
        int bookId = getValidBookId();
        int gameId = startGame(bookId);
        mvc.perform(get("/api/games/" +  gameId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.health").value(10))
                .andExpect(jsonPath("$.status").value("STARTED"))
                .andExpect(jsonPath("$.position").isNotEmpty());
    }

    /**
     * GET/POST/POST*
     * executes a full successful  game flow
     * - retrieves the first valid book available
     * - starts a new game based on that book
     * - executes a sequence of POSTs corresponding toe selecting the options
     * that lead to a successful end of the game
     */
    @Test
    void successfulPiratesGame() throws Exception {
        int bookId = getValidBookId();
        int gameId = startGame(bookId);
        successfulPiratesFlow(gameId);
    }

    /**
     * GET/POST/POST*
     * executes a full unsuccessful  game flow
     * - retrieves the first valid book available
     * - starts a new game based on that book
     * - executes a sequence of POSTs corresponding toe selecting the options
     * that lead to a failed end of the game
     */
    @Test
    void unsuccessfulPiratesGame() throws Exception {
        int bookId = getValidBookId();
        int gameId = startGame(bookId);
        unsuccessfulPiratesFlow(gameId);
    }

    /**
     * GET/POST/POST*
     * executes a full successful game flow followed by a restart
     * - retrieves the first valid book available
     * - starts a new game based on that book
     * - executes a sequence of POSTs corresponding toe selecting the options
     * that lead to a successful end of the game
     * - restarts the game to check the status and health and confirms that
     * the current position is the book initial section
     */
    @Test
    void restartAfterSuccessfulConclusion() throws Exception {
        int bookId = getValidBookId();
        int gameId = startGame(bookId);
        successfulPiratesFlow(gameId);
        postMove(gameId, 0, 10, Game.Status.RESTARTED,
                "The salty breeze carries the cries of distant gulls as your ship sails across the shimmering Jade Sea. Rumors speak of hidden treasure on the Isle of Serpents.");
    }

    /**
     * GET/POST/POST*
     * executes a full unsuccessful game flow followed by a restart
     * - retrieves the first valid book available
     * - starts a new game based on that book
     * - executes a sequence of POSTs corresponding toe selecting the options
     * that lead to a failed end of the game
     * - restarts the game to check the status and health and confirms that
     * the current position is the book initial section
     */
    @Test
    void restartAfterUnsuccessfulConclusion() throws Exception {
        int bookId = getValidBookId();
        int gameId = startGame(bookId);
        unsuccessfulPiratesFlow(gameId);
        postMove(gameId, 0, 10, Game.Status.RESTARTED,
                "The salty breeze carries the cries of distant gulls as your ship sails across the shimmering Jade Sea. Rumors speak of hidden treasure on the Isle of Serpents.");
    }


    /**
     * POST
     * starts a new game, based on the book identified by the bookId arg
     * @param bookId - key of the book to retrieve from the repo*
     * @return - id of the new game created by the POST submission
     * @throws Exception - internal exception
     */
    int startGame(int bookId) throws Exception {
        String startResponse = mvc.perform(post("/api/games/start/" + bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.health").value(10))
                .andExpect(jsonPath("$.status").value("STARTED"))
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(startResponse, "$.game");
    }

    /**
     * GET
     * submits a get command to gather the id of the first valid book
     * @return id of the first valid book on the list
     * @throws Exception - internal exception
     */
    int getValidBookId() throws Exception {
        String startResponse = mvc.perform(get("/api/books?condition=OK"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(startResponse, "$.content[0].id");
    }

    /**
     * POST
     * utility method to submit a POST to the game API
     * checks the returned health, status and the new position
     * @param gameId - identifies the played game
     * @param optionNo - determines the selected option on the game
     * @param health - indicates the expected value of the game health
     * @param status - indicates the expected status of the game after the POST
     * @param position - indicates the position (= current section) of the game after the POST
     * @throws Exception - internal exception
     */
    void postMove(int gameId, int optionNo, int health, Game.Status status, String position) throws Exception {
        mvc.perform(post("/api/games/" + gameId + "/options/" +  optionNo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.position").value(position))
                .andExpect(jsonPath("$.health").value(health))
                .andExpect(jsonPath("$.status").value(status.toString()));
    }


    /**
     * executes a series of POST submissions corresponding to the options that lead to
     * a successful execution of the game based on the Pirates book
     * @param gameId - id of the game being played
     * @throws Exception - internal exception
     */
    void successfulPiratesFlow(int gameId) throws Exception {
        postMove(gameId, 1, 10, Game.Status.ACTIVE,
                "After hours of waiting, the storm clears and the Isle of Serpents looms ahead, shrouded in mist.");
        postMove(gameId, 1, 10, Game.Status.ACTIVE,
                "You step onto the white sands of the Isle of Serpents. Ancient ruins rise above the jungle canopy.");
        postMove(gameId, 1, 10, Game.Status.ACTIVE,
                "From the top of the ruins, you spot a hidden lagoon surrounded by jagged cliffs.");
        postMove(gameId, 1, 10, Game.Status.SUCCEEDED,
                "You reach a secret pirate hideout filled with treasure. The crew celebrates your discovery.");
    }

    /**
     * executes a series of POST submissions corresponding to the options that lead to
     * a failed execution of the game based on the Pirates book
     * @param gameId - id of the played game
     * @throws Exception - internal exception
     */
    void unsuccessfulPiratesFlow(int gameId) throws Exception {
        postMove(gameId, 2, 10, Game.Status.ACTIVE,
                "The captured pirate snarls but whispers about a secret cove guarded by a sea monster.");
        postMove(gameId, 1, 7, Game.Status.ACTIVE,
                "Shaken, the pirate reveals that the sea monster can be lulled to sleep by an enchanted conch shell hidden deep within a wrecked galleon.");
        postMove(gameId, 1, 7, Game.Status.ACTIVE,
                "You steer toward the mysterious Isle of Serpents. As you approach, dark storm clouds gather overhead.");
        postMove(gameId, 2, 7, Game.Status.ACTIVE,
                "The storm rages violently. Lightning flashes illuminate shadowy shapes beneath the waves.");
        postMove(gameId, 1, 0, Game.Status.FAILED,
                "Beneath the waves, you glimpse the massive serpent coiled around a treasure-laden wreck.");
    }

}
