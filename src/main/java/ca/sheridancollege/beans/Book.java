package ca.sheridancollege.beans;

import java.text.DecimalFormat;
import java.util.Formatter;
import java.util.List;

import lombok.Data;

@Data
public class Book {
	private Long isbn;
	private String title;
	private String author;
	private Double price;
	private Integer inventoryQuantity;
	private Integer purchaseQuantity;
	private String image;
	private List<Review> reviews;
	
	private String category;
	private final String [] categories = {"Fiction", "Non-Fiction", "Self-helf", "History", "Horror", "Romance",
											"Thriller", "Business", "Science", "Politic", "Art"};

	
	public String getDisplayedPrice() {
		
		Formatter formatter = new Formatter();
        formatter.format("%.2f", price);
        
        return formatter.toString();
	  }
}
