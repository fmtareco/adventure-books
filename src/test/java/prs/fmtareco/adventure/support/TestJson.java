package prs.fmtareco.adventure.support;


import java.time.Instant;

public final class TestJson {

    private TestJson() {}

    public static String invalidBook() {
        return String.format("""
        {
          "titled": "Broken Adventure %s",
          "author": "Tester",
          "difficulty": "EASY",
          "categories": ["FICTION"],
          "sections": []
        }
        """, Instant.now().toString());
    }

    public static String validBook() {
        return String.format("""
        {
          "title": "Valid Adventure %s",
          "author": "Tester",
          "difficulty": "EASY",
          "categories": ["FICTION"],
          "sections": [
            {
              "id": 1,
              "text": "Start",
              "type": "BEGIN",
              "options": [
                { "description": "End", "gotoId": 2 }
              ]
            },
            {
              "id": 2,
              "text": "Finish",
              "type": "END",
              "options": []
            }
          ]
        }
        """, Instant.now().toString());
    }
}
