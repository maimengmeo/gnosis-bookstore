package ca.sheridancollege.database;

import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import ca.sheridancollege.beans.Book;
import ca.sheridancollege.beans.Review;

@Repository
public class DatabaseAccess {
	
	private NamedParameterJdbcTemplate jdbc;
	
	public DatabaseAccess (NamedParameterJdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}
	
	
	public List<Book> getBooks() {
		
		
		String query = "SELECT * FROM books";
		
		BeanPropertyRowMapper <Book> mapper = new BeanPropertyRowMapper <Book> (Book.class);
		
		List <Book> books = jdbc.query(query, mapper);
		
		return books;
	}
	
	
	public List<Book> getBooksByTitle(String keyword) {
		
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		
		String query = "SELECT DISTINCT * FROM books WHERE LOWER(title) LIKE LOWER('%" + keyword + "%')";
		
		namedParameters.addValue("title", keyword);
		
		BeanPropertyRowMapper <Book> mapper = new BeanPropertyRowMapper <Book> (Book.class);
		
		List<Book> books = jdbc.query(query, namedParameters, mapper);
		
		return books;
		
	}
	
	public Book getBook(Long isbn) {
		
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		
		String queryBooks = "SELECT * FROM books WHERE isbn = :isbn";
		String queryReviews = "SELECT * FROM reviews WHERE isbn = :isbn";
		
		namedParameters.addValue("isbn", isbn);
		
		BeanPropertyRowMapper <Book> mapper = new BeanPropertyRowMapper <Book> (Book.class);
		
		Book book = null;
		
		try {
			book = jdbc.queryForObject(queryBooks, namedParameters, mapper);
		} catch (EmptyResultDataAccessException e) {
			System.out.println("Book not found for isbn: " + isbn);
		}
		
		return book;
	}
	
	
	public int addBook(Book book) {
		
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		
		String query = "INSERT INTO books (isbn, image, title, author, category, price, inventoryQuantity) "
				+ "VALUES (:isbn, :image, :title, :author, :category, :price, :inventoryQuantity)";
		
		namedParameters
			.addValue("isbn", book.getIsbn())
			.addValue("image", "image/" + book.getImage())
			.addValue("title", book.getTitle())
			.addValue("author", book.getAuthor())
			.addValue("category", book.getCategory())
			.addValue("price", book.getPrice())
			.addValue("inventoryQuantity", book.getInventoryQuantity());
		
		int returnValue = jdbc.update(query, namedParameters);
		
		return returnValue;
	}
	
	public int deleteBook (Long isbn) {
		
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		
		String query = "DELETE FROM books WHERE isbn = :isbn";
		
		namedParameters.addValue("isbn", isbn);
		
		int returnValue = jdbc.update(query, namedParameters);
		
		return returnValue;
	}
	
	public int updateBook(Book book) {
		
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		
		String query = "UPDATE books SET isbn = :isbn, image = :image, title = :title, "
				+ "author = :author, category = :category, price = :price, inventoryQuantity = :inventoryQuantity WHERE isbn = :isbn";
		
		namedParameters
		.addValue("isbn", book.getIsbn())
		.addValue("image", book.getImage())
		.addValue("title", book.getTitle())
		.addValue("author", book.getAuthor())
		.addValue("category", book.getCategory())
		.addValue("price", book.getPrice())
		.addValue("inventoryQuantity", book.getInventoryQuantity());
		
		int returnValue = jdbc.update(query, namedParameters);
		
		return returnValue;
		
	}
	
	public int updateStock(Book book) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		String query = "UPDATE books SET inventoryQuantity = :inventoryQuantity WHERE isbn = :isbn";
		params.addValue("isbn", book.getIsbn())
				.addValue("inventoryQuantity", book.getInventoryQuantity());
		int returnValue = jdbc.update(query, params);
		return returnValue;
	}
	
	public List<String>getAuthorities() {
		
		MapSqlParameterSource params = new MapSqlParameterSource();
		String query = "SELECT DISTINCT authority FROM authorities";
		List<String> authorities = jdbc.queryForList(query, params, String.class);
		
		return authorities;
	}
	
	public List<Review> getReviews(Long isbn) {
		
		String query = "SELECT * FROM reviews WHERE isbn = :isbn";
		
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("isbn", isbn);
		
		BeanPropertyRowMapper<Review> mapper = new BeanPropertyRowMapper <Review> (Review.class);
		
		List <Review> reviews = jdbc.query(query,params, mapper);
		
		System.out.println(reviews);
		
		return reviews;
	}
	
	public Review getReview(Long isbn, Long id) {
		
		String query = "SELECT * FROM reviews WHERE isbn = :isbn AND id = :id";
		
		MapSqlParameterSource params = new MapSqlParameterSource();
		
		params.addValue("isbn", isbn)
				.addValue("id", id);
		
		BeanPropertyRowMapper<Review> mapper = new BeanPropertyRowMapper <Review> (Review.class);
		
		Review review = jdbc.queryForObject(query, params, mapper);
		
		return review;
		
	}
	
	public Long addReview(Review review, Long isbn) {
		
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		
		String query = "INSERT INTO reviews (username, text, isbn) "
				+ "VALUES (:username, :text, :isbn)";
		
		namedParameters
			.addValue("isbn", isbn)
			.addValue("text", review.getText())
			.addValue("username", review.getUsername());

		KeyHolder key = new GeneratedKeyHolder();
		
		Long returnValue = (long) jdbc.update(query, namedParameters, key);
		
		Long id = (Long) key.getKey();
		
		return (returnValue > 0) ? id: 0;
		
	}
	
	
}
