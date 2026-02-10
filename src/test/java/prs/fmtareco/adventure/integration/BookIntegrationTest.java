package prs.fmtareco.adventure.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import prs.fmtareco.adventure.support.TestJson;

import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
//@Testcontainers
public class BookIntegrationTest {
    @Autowired
    private MockMvc mvc;

    /**
     * GET
     * checks the books API Get all books endpoint
     * - validates that the list is not empty
     * - tests the pagination and sort
     * @throws Exception - internal exception
     */
    @Test
    void checkGetBooksApi() throws Exception {
        mvc.perform(get("/api/books?size=3&page=1&sort=desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").isNotEmpty())
                .andExpect(jsonPath("$.totalElements").value(greaterThan(0)));
    }

    /**
     *  GET
     *  checks the books API filtering by book status
     *  - retrieves only the valid books
     */
    @Test
    void checkGetValidBooksApi() throws Exception {
        mvc.perform(get("/api/books?condition=OK"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").isNotEmpty())
            .andExpect(jsonPath("$.totalElements").value(greaterThan(0)))
            .andExpect(jsonPath("$.content[0].id").value(is(102)));
    }

    /**
     * POST
     * validates that the API won't accept invalid JSON specs
     */
    @Test
    void checkInvalidBookUpload() throws Exception {
        mvc.perform(post("/api/books")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestJson.invalidBook()))
            .andExpect(status().isBadRequest());
    }

    /**
     * POST
     * tests the books creation via API
     */
    @Test
    void checkValidBookUpload() throws Exception {
        mvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestJson.validBook()))
                .andExpect(status().is2xxSuccessful());
    }
}
