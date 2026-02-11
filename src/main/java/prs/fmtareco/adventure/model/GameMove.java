package prs.fmtareco.adventure.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/*
    simulates all possible paths departing from a option and section (implicit)
 */
@Data
public class GameMove {

    private GameSimulation simulation;
    private GameMove parentMove;
    private List<GameMove> moves = new ArrayList<>();
    private List<GameMove> loops = new ArrayList<>();
    private Option selectedOption;
    private Section reachedSection;
    private int inputHealth;
    private int outputHealth;

    public GameMove(GameSimulation sim, Option selectedOption) {
        this.simulation = sim;
        this.selectedOption = selectedOption;
        this.inputHealth = sim.getHealth();
    }
    public GameMove(GameMove gm, Option selectedOption) {
        this.simulation = gm.simulation;
        this.selectedOption = selectedOption;
        this.inputHealth = gm.getOutputHealth();
    }

    /**
     * determines the reached section by choosing the selected option
     */
    public void setTargetSection() {
        int sectionNumber = selectedOption.getGotoSectionNumber();
        reachedSection = simulation.getBook().getSectionNumber(sectionNumber).orElse(null);
    }

    /**
     * gathers all traversed sections until this move, to detect potential loops
     */
    public List<Section> gatherTraversedSections(List<Section> sections) {
        if (reachedSection == null)
            return sections;
        sections.addFirst(reachedSection);
        if (parentMove != null)
            return parentMove.gatherTraversedSections(sections);
        return sections;
    }

    /**
     * checks if following the moves from this option we will reach a loop
     * @return boolean indicating is if causes a loop
     */
    public boolean checkInLoop() {
        if (reachedSection == null)
            return false;
        if (parentMove == null)
            return false;
        List<Section> traversedSections = parentMove.gatherTraversedSections(new ArrayList<>());
        return traversedSections.contains(reachedSection);
    }

    /**
     * applies the move based on the selected option
     * for all the option departing form the reachedSection we first evaluate if they will cause a loop
     * for all the other we follow the simulation and obtain subpaths
     */
    public void applyMove() {
        applyConsequence(selectedOption.getConsequence());
        if (outputHealth < 0)
            return;
        setTargetSection();
        if (reachedSection == null)
            return;
        if (simulation.alreadyVisited(reachedSection))
            return;
        simulation.registerVisited(reachedSection);
        GameMove gm;
        for(Option opt : reachedSection.getOptions()) {
            System.out.println("applying move from "+ selectedOption.getSection().getSectionNumber() + " to " + opt.getGotoSectionNumber());
            gm = new GameMove(this, opt);
            gm.setTargetSection();
            if (gm.checkInLoop())
                loops.add(gm);
            else {
                moves.add(gm);
                gm.applyMove();
            }
        }
    }

    /**
     * apply the eventual consequence of the selected option,
     * with the eventual impact on the health
     * @param csq Consequence instance
     */
    private void applyConsequence(Consequence csq) {
        outputHealth = inputHealth;
        if (csq == null)
            return;
        int csqValue = csq.getValue();
        if (csqValue == 0)
            return;
        Consequence.Type type = csq.getType();
        if (type == Consequence.Type.LOSE_HEALTH)
            outputHealth -= csqValue;
        else
            outputHealth += csqValue;
    }

    /**
     * return the number of potential loops detected departing from this option
     *
     * @return number of loop paths
     */
    public int numberOfLoops() {
        int numLoops = loops.size();
        for (GameMove gm : moves) {
            numLoops += gm.numberOfLoops();
        }
        return numLoops;
    }

    /**
     * calculates the number of paths that reach a given outcome, from the reachedSection
     *
     * @param status - outcome to reach
     * @return number of paths
     */
    public int numberOfOutcomes(Game.Status  status) {
        if (outputHealth < 0)
            return status==Game.Status.FAILED?1:0;
        if (reachedSection == null)
            return status==Game.Status.FAILED?1:0;
        if (reachedSection.getType() == Section.Type.END)
            return status==Game.Status.SUCCEEDED?1:0;
        int numOutcomes = 0;
        for(GameMove gm : moves) {
            numOutcomes += gm.numberOfOutcomes(status);
        }
        return numOutcomes;
    }
}
