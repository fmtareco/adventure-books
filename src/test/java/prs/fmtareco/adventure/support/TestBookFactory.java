package prs.fmtareco.adventure.support;


import prs.fmtareco.adventure.model.Book;
import prs.fmtareco.adventure.model.Consequence;
import prs.fmtareco.adventure.model.Option;
import prs.fmtareco.adventure.model.Section;

import java.util.HashMap;
import java.util.Map;


public class TestBookFactory {

    /**
     * creates a new valid book (w/ all the expected sections and basic options)
     * @return the new book
     */
    public static Book createValidBook() {
        Book book = Book.create(
                "The most Great and Valid Book",
                "Bill Validator",
                Book.Difficulty.EASY);
        addSection(book,Section.Type.BEGIN, 1, new int[] { 2 });
        addSection(book,Section.Type.NODE, 2, new int[] { 3 });
        addSection(book,Section.Type.END, 3);
        book.setBookCondition();
        return book;
    }

    /**
     * creates a new valid book (w/ some consequences that lead to success or failure)
     * @return the new book
     */
    public static Book createBookWithConsequences() {
        Book book = Book.create(
                "The Consequences of a Book",
                "Albert Consequence",
                Book.Difficulty.MEDIUM);
        Map<Integer, Integer> options = new HashMap<>();
        options.put(20, 7);
        options.put(30, -7);
        addSection(book,Section.Type.BEGIN, 10, options);

        options.clear();
        options.put(50, 7);
        options.put(30, -7);
        addSection(book,Section.Type.NODE, 20, options);

        options.clear();
        options.put(10, -7);
        addSection(book,Section.Type.NODE, 30, options);

        addSection(book,Section.Type.END, 50);
        book.setBookCondition();
        return book;
    }

    /**
     * creates an invalid book without an initial section
     * @return the new book
     */
    public static Book createNoBeginBook() {
        Book book = Book.create(
                "The Book with no Begin",
                "Bill Beginner",
                Book.Difficulty.HARD);

        addSection(book,Section.Type.NODE, 2, new int[] { 3 });
        addSection(book,Section.Type.END, 3);
        book.setBookCondition();
        return book;
    }

    /**
     * creates an invalid book with multiple initial sections
     * @return the new book
     */
    public static Book createMultipleBeginsBook() {
        Book book = Book.create(
                "The Book of Many Begins",
                "Bill Beginner",
                Book.Difficulty.MEDIUM);
        addSection(book,Section.Type.BEGIN, 10, new int[] { 20 });
        addSection(book,Section.Type.BEGIN, 11, new int[] { 20 });
        addSection(book,Section.Type.NODE, 20, new int[] { 30 });
        addSection(book,Section.Type.END, 30);
        book.setBookCondition();
        return book;
    }

    /**
     * creates an invalid book without a final (END) section
     * @return the new book
     */
    public static Book createNoEndBook() {
        Book book = Book.create(
                "Never ending Book",
                "Adam Finisher",
                Book.Difficulty.MEDIUM);
        addSection(book,Section.Type.BEGIN, 10, new int[] { 20 });
        addSection(book,Section.Type.NODE, 20, new int[] { 30 });
        book.setBookCondition();
        return book;
    }

    /**
     * creates an invalid book with options leading to unknown sections
     * @return the new book
     */
    public static Book createBookWithInvalidGoTo() {
        Book book = Book.create(
                "The Book with Invalid GoTo",
                "Charles Wanderer",
                Book.Difficulty.EASY);
        addSection(book,Section.Type.BEGIN, 1, new int[] { 22 });
        addSection(book,Section.Type.NODE, 2, new int[] { 33 });
        addSection(book,Section.Type.END, 3);
        book.setBookCondition();
        return book;
    }

    /**
     * creates an invalid book with an intermediate section without options to jump to other sections
     * @return the new book
     */
    public static Book createBookWithNonEndWithoutOptions() {
        Book book = Book.create(
                "The Book without Options",
                "David Chooser",
                Book.Difficulty.EASY);
        addSection(book,Section.Type.BEGIN, 1, new int[] { 2 });
        addSection(book,Section.Type.NODE, 2);
        addSection(book,Section.Type.END, 3);
        book.setBookCondition();
        return book;
    }

    /**
     * adds a new section without options to jump to others
     * @param book - the book being built
     * @param type - the type of section
     * @param sectionNo - the number (sectionNumber) of the section
     */
    private static void addSection(Book book, Section.Type type, int sectionNo) {
        addSection(book, type, sectionNo, (Map<Integer,Integer>)null);
    }

    /**
     * adds a new section with a list of options based on the argument int array
     * @param book - the book being built
     * @param type - the type of section
     * @param sectionNo - the number (sectionNumber) of the section
     * @param gotoSections - array of int values corresponding to the options the add to the new section
     */
    private static void addSection(Book book, Section.Type type, int sectionNo, int[] gotoSections) {
        Map<Integer,Integer> options = new HashMap<>();
        for(int i = 0; i < gotoSections.length; i++){
            options.put(gotoSections[i], (i+1)*10);
        }
        addSection(book, type, sectionNo, options);
    }

    /**
     * adds a new section with a list of options (and implicit consequences) based on the argument map
     * @param book - the book being built
     * @param type - the type of section
     * @param sectionNo - the number (sectionNumber) of the section
     * @param options - map of pair values (goto section + health value) corresponding to the options
     * and consequences to add to the new section
     */
    private static void addSection(Book book, Section.Type type, int sectionNo, Map<Integer,Integer> options) {
        String description = switch (type) {
            case Section.Type.BEGIN -> "Initial Section";
            case Section.Type.NODE -> "Middle Section";
            case Section.Type.END -> "End Section";
        } + " " + sectionNo;

        Section newSection = Section.create(
                sectionNo, description, type);
        book.addSection(newSection);
        if (options==null)
            return ;
        for (Integer gtNo : options.keySet()) {
            Option option = Option.create("Move to Section " + gtNo, gtNo);
            newSection.addOption(option);
            int health = options.get(gtNo);
            if (health==0)
                continue;
            option.setConsequence(Consequence.create(
                    health>0?Consequence.Type.GAIN_HEALTH:Consequence.Type.LOSE_HEALTH,
                    Math.abs(health),
                    option.getDescription() + " Consequence(" + health+")"));
        }
    }

}
