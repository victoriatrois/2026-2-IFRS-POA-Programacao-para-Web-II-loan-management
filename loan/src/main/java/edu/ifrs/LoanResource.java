package edu.ifrs;

import edu.ifrs.client.IBookCatalog;
import edu.ifrs.model.Book;
import edu.ifrs.model.Loan;
import edu.ifrs.model.LoanRequest;
import edu.ifrs.model.LoanStore;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@Path("/loans")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoanResource {

    private final LoanStore loanStore;
    private final IBookCatalog bookCatalog;

    public LoanResource(LoanStore loanStore, @RestClient IBookCatalog bookCatalog) {
        this.loanStore = loanStore;
        this.bookCatalog = bookCatalog;
    }

    @POST
    public Response create(LoanRequest request) {
        try {
            Book book = bookCatalog.getBook(request.bookId());

            if (book.loaned()) {
                return Response.status(Response.Status.CONFLICT).build();
            }

            bookCatalog.markAsLoaned(request.bookId());
            Loan created = loanStore.add(request);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (WebApplicationException e) {
            if (e.getResponse() != null && e.getResponse().getStatus() == 404) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            throw e;
        }
    }

    @GET
    public List<Loan> list() {
        return loanStore.list();
    }

    @GET
    @Path("/books")
    public List<Book> listAvailableBooks() {
        return bookCatalog.listBooks().stream()
            .filter(book -> !book.loaned())
            .toList();
    }
}
