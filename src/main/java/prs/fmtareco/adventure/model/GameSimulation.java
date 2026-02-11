package prs.fmtareco.adventure.model;

import java.util.ArrayList;
import java.util.List;

/*
    simulates all possible paths departing from the initial section of a given book
 */
public class GameSimulation extends Game {

    private final List<GameMove> moves = new ArrayList<>();
    public List<Section> visitedSections = new ArrayList<>();

    public GameSimulation(Book book) {
        setBook(book);
        setSection(book.getInitialSection()
                .orElse(null));
        setStatus(Game.Status.STARTED);
    }

    /**
     * applies all possible moves departing from the book initial section
     */
    public void applyMoves() {
        if (getSection() == null)
            return;
        for(Option opt: getSection().getOptions()) {
            GameMove gm = new GameMove(this, opt);
            try {
                moves.add(gm);
                gm.applyMove();
            }
            catch (Exception ex ) {
                System.out.println(ex.getMessage());
            }
        }
    }

    /**
     * controls which sections were already visited, to avoid loops
     *
     * @param section - setion to check if repeated
     * @return booelan indicateing if it was visited
     */
    public boolean alreadyVisited(Section section) {
        return visitedSections.contains(section);
    }

    /**
     * register a section as visited, to avoid loops
     *
     * @param section - section to check
     */
    public void registerVisited(Section section) {
        visitedSections.add(section);
    }

    /**
     * returns the number of detected loop paths on the simulated games
     *
     * @return number of paths
     */
    public int numberOfLoopPaths() {
        int loops = 0;
        for (GameMove gm : moves) {
            loops += gm.numberOfLoops();
        }
        return loops;
    }

    /**
     * determins the number of simulated paths that reach a given outcome
     * @param status - expected outcome: success or failure
     * @return number of paths
     */
    private int numberOfOutcomes(Status status) {
        int paths = 0;
        for (GameMove gm : moves) {
            paths += gm.numberOfOutcomes(status);
        }
        return paths;
    }

    /**
     * returns the number of simulated paths that end in FAILURE
     * @return number of paths
     */
    public int numberOfFailedPaths() {
        return numberOfOutcomes(Status.FAILED);
    }

    /**
     * returns the number of successful simulated paths
     * @return number of paths
     */
    public int numberOfSuccessfulPaths() {
        return numberOfOutcomes(Status.SUCCEEDED);
    }


}
