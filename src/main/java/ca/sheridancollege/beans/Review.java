package ca.sheridancollege.beans;

import lombok.Data;

@Data
public class Review {
	private Long id;
	private Long isbn;
	private String text;
	private String username;
	
}
