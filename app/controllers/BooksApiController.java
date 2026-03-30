package controllers;

import model.Book;
import play.mvc.*;
import proto.BookProto;

import java.util.Set;

/**
 * Protobuf REST API for Books.
 * Content-Type: application/x-protobuf
 *
 * GET    /api/books         -> all books as BookList protobuf
 * GET    /api/books/:id     -> single book as Book protobuf
 * POST   /api/books         -> create book from Book protobuf body
 * DELETE /api/books/:id     -> delete book
 */
public class BooksApiController extends Controller {

    // GET /api/books
    public Result index() {
        Set<Book> books = Book.allBooks();
        BookProto.BookList.Builder listBuilder = BookProto.BookList.newBuilder();
        for (Book b : books) {
            listBuilder.addBooks(toProto(b));
        }
        return ok(listBuilder.build().toByteArray())
                .as("application/x-protobuf");
    }

    // GET /api/books/:id
    public Result show(Integer id) {
        Book book = Book.findById(id);
        if (book == null) return notFound();
        return ok(toProto(book).toByteArray())
                .as("application/x-protobuf");
    }

    // POST /api/books
    public Result save(Http.Request request) {
        try {
            // support both JSON and protobuf
            String contentType = request.contentType().orElse("");
            if (contentType.contains("application/json")) {
                com.fasterxml.jackson.databind.JsonNode json = request.body().asJson();
                Book book = new Book(
                    json.get("id").asInt(),
                    json.get("title").asText(),
                    json.get("author").asText(),
                    json.get("price").asInt()
                );
                Book.add(book);
                return created(toProto(book).toByteArray()).as("application/x-protobuf");
            } else {
                byte[] body = request.body().asBytes().toArray();
                BookProto.Book protoBook = BookProto.Book.parseFrom(body);
                Book book = fromProto(protoBook);
                Book.add(book);
                return created(toProto(book).toByteArray()).as("application/x-protobuf");
            }
        } catch (Exception e) {
            return badRequest("Invalid body: " + e.getMessage());
        }
    }

    // DELETE /api/books/:id
    public Result destroy(Integer id) {
        Book book = Book.findById(id);
        if (book == null) return notFound();
        Book.remove(book);
        return noContent();
    }

    // PUT /api/books/:id
    public Result update(Http.Request request, Integer id) {
        try {
            Book existing = Book.findById(id);
            if (existing == null) return notFound();
            String contentType = request.contentType().orElse("");
            if (contentType.contains("application/json")) {
                com.fasterxml.jackson.databind.JsonNode json = request.body().asJson();
                existing.setTitle(json.get("title").asText());
                existing.setAuthor(json.get("author").asText());
                existing.setPrice(json.get("price").asInt());
            } else {
                byte[] body = request.body().asBytes().toArray();
                BookProto.Book protoBook = BookProto.Book.parseFrom(body);
                existing.setTitle(protoBook.getTitle());
                existing.setAuthor(protoBook.getAuthor());
                existing.setPrice(protoBook.getPrice());
            }
            return ok(toProto(existing).toByteArray()).as("application/x-protobuf");
        } catch (Exception e) {
            return badRequest("Invalid body: " + e.getMessage());
        }
    }

    // --- helpers ---

    private BookProto.Book toProto(Book b) {
        return BookProto.Book.newBuilder()
                .setId(b.getId())
                .setTitle(b.getTitle())
                .setAuthor(b.getAuthor())
                .setPrice(b.getPrice())
                .build();
    }

    private Book fromProto(BookProto.Book p) {
        return new Book(p.getId(), p.getTitle(), p.getAuthor(), p.getPrice());
    }
}
