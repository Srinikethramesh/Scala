package controllers;

import model.Book;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.mvc.*;
import views.html.books.*;

import javax.inject.Inject;
import java.util.*;

public class BooksController extends Controller {

    @Inject
    FormFactory formFactory;

    @Inject
    MessagesApi messagesApi;


    //For all book
    public Result index() {
        Set<Book> books = Book.allBooks();
        return ok(index.render(books));
    }

    //TO CREATE BOOK
    public Result create(Http.Request request){
        Form<Book> bookForm = formFactory.form(Book.class);
        return ok(create.render(bookForm, messagesApi.preferred(request), request));
    }

    //TO SAVE BOOK
    public Result save(Http.Request request){
        Form<Book> bookForm = formFactory.form(Book.class).bindFromRequest(request);
        Book book = bookForm.get();
        Book.add(book);
        return redirect(routes.BooksController.index());
    }

    //TO EDOT BOOK
    public Result edit(Http.Request request, Integer id){
        Book book = Book.findById(id);
        if(book == null){
            return notFound("Book not found");
        }
        Form<Book> bookForm = formFactory.form(Book.class).fill(book);  
        return ok(edit.render(bookForm, messagesApi.preferred(request), request));
    }


    //TO UPDATE BOOK
    public Result update(Http.Request request){
        Form<Book> bookForm = formFactory.form(Book.class).bindFromRequest(request);
        Book updated = bookForm.get();
        Book existing = Book.findById(updated.id);
        if(existing == null) return notFound("Book not found");
        existing.setTitle(updated.title);
        existing.setAuthor(updated.author);
        existing.setPrice(updated.price);
        return redirect(routes.BooksController.index());
    }

    //TO DELETE BOOK
    public Result destroy(Integer id){
        Book book = Book.findById(id);
        if(book == null) return notFound("Book not found");
        Book.remove(book);
        return redirect(routes.BooksController.index());
    }

    //TO SHOW ONE BOOK DETAILS
    public Result show(Integer id){
        Book book = Book.findById(id);
        if(book == null) return notFound("Book not found");
        return ok(show.render(book));
    }
}
