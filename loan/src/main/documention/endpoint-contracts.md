## API contracts

Base path: `/loans`

| Method | Route          | Request body         | Response body | Status |
|--------|----------------|----------------------|---------------------------|---|
| POST   | `/loans`       | `LoanRequest` (JSON) | `Loan` created             | `201 Created`, `404 Not Found`, `409 Conflict` |
| GET    | `/loans/books` | -                    | `List<Book>`              | `200 OK` |

### Types

```java
public record Book(Long id, String title, String author, boolean loaned) {}
public record Loan(Long id, Long bookId, String borrower) {}
public record LoanRequest(Long bookId, String borrower) {}
```

## Integration with the catalog

The loan service consumes the book catalog via MicroProfile Rest Client:

```java
@RegisterRestClient(baseUri = "http://localhost:9080/books")
public interface IBookCatalog {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<Book> listBooks();

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    Book getBook(@PathParam("id") Long id);

    @PUT
    @Path("/{id}/loan")
    @Produces(MediaType.APPLICATION_JSON)
    Book markAsLoaned(@PathParam("id") Long id);
}
```