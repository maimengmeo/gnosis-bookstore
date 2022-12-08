package ca.sheridancollege;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.sheridancollege.beans.Book;
import ca.sheridancollege.database.DatabaseAccess;

@SpringBootTest
public class TestDatabase {

		private DatabaseAccess database;
		
		@Autowired
		public void setDatabse(DatabaseAccess database) {
			this.database = database;
		}
		
		@Test
		public void testDatabseAdd() {
			
			Book book = new Book();
			
			book.setIsbn((long) 987654321);
			book.setTitle("Squirrels");
			book.setAuthor("Cleo The Doggo");
			book.setCategory("Romance");
			book.setPrice(99.99);
			book.setInventoryQuantity(999);
			
			int origSize = database.getBooks().size();
			
			database.addBook(book);
			
			int newSize = database.getBooks().size();
			
			assertEquals(newSize, origSize + 1);
		}
		
		@Test
		public void testDatabaseDelete() {
			List<Book> books = database.getBooks();
			Long isbn = books.get(0).getIsbn();
			int origSize = database.getBooks().size();
			database.deleteBook(isbn);
			int newSize = database.getBooks().size();
			assertEquals(newSize, origSize - 1);
		}
	
	

}
