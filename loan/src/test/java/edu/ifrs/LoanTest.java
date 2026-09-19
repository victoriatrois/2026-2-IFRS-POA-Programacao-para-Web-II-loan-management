package edu.ifrs;

import edu.ifrs.model.LoanStore;

import io.quarkus.test.junit.QuarkusTest;

import jakarta.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class LoanTest {

    private static final String CATALOG_BASE_URI = System.getProperty("catalog.base-uri", "http://localhost:8082");

    @Inject
    LoanStore loanStore;

    @BeforeEach
    void setUp() {
        loanStore.reset();
    }

    private Long createBookInCatalog() {
        Object id = given()
            .baseUri(CATALOG_BASE_URI)
            .accept("application/json")
            .contentType("application/json")
            .body("""
                {
                  "title": "Clean Code",
                  "author": "Robert C. Martin",
                  "loaned": false
                }
                """)
            .when()
            .post("/books")
            .then()
            .statusCode(201)
            .extract()
            .path("id");
        return id instanceof Long ? (Long) id : ((Number) id).longValue();
    }

    @Nested
    class PostLoans {

        @Test
        void shouldCreateLoanForExistingAvailableBook() {
            Long bookId = createBookInCatalog();

            given()
                .contentType("application/json")
                .body("""
                    {
                      "bookId": %d,
                      "borrower": "Ana"
                    }
                    """.formatted(bookId))
                .when()
                .post("/loans")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("bookId", is(bookId.intValue()))
                .body("borrower", is("Ana"));
        }

        @Nested
        class ErrorCases {

            @Test
            void shouldReturnNotFoundWhenBookDoesNotExist() {
                given()
                    .contentType("application/json")
                    .body("""
                        {
                          "bookId": 999,
                          "borrower": "Ana"
                        }
                        """)
                    .when()
                    .post("/loans")
                    .then()
                    .statusCode(404);
            }

            @Test
            void shouldReturnConflictWhenBookIsNotAvailable() {
                Long bookId = createBookInCatalog();

                given()
                    .contentType("application/json")
                    .body("""
                        {
                          "bookId": %d,
                          "borrower": "Ana"
                        }
                        """.formatted(bookId))
                    .when()
                    .post("/loans")
                    .then()
                    .statusCode(201);

                given()
                    .contentType("application/json")
                    .body("""
                        {
                          "bookId": %d,
                          "borrower": "João"
                        }
                        """.formatted(bookId))
                    .when()
                    .post("/loans")
                    .then()
                    .statusCode(409);
            }
        }
    }
}
