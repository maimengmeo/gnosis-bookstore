package ca.sheridancollege.controllers;

import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import ca.sheridancollege.beans.Book;
import ca.sheridancollege.beans.Message;
import ca.sheridancollege.beans.Review;
import ca.sheridancollege.database.DatabaseAccess;
import lombok.AllArgsConstructor;

@Controller

public class HomeController {

	private DatabaseAccess database;
	private BCryptPasswordEncoder encoder;
	private JdbcUserDetailsManager manager;
	
	public HomeController (DatabaseAccess database, BCryptPasswordEncoder encoder, JdbcUserDetailsManager manager) {
		this.database = database;
		this.encoder = encoder;
		this.manager = manager;
	}

//	NAVIGATION ==============================================================================
	
	@GetMapping("/")
	public String goHome(HttpSession session, Model model, HttpServletRequest request) {
		
		System.out.println("Username: cleo - Password: doggo - Roles: Staff, Client");
		System.out.println("Username: beef - Password: cow - Roles: Client");
		
		if (session.isNew()) {
			session.setAttribute("selectedBooks", new ArrayList<Book>());
			session.setAttribute("message", "");
		}
		List <Book> books = database.getBooks();
		
		model.addAttribute("books", books);
		
		if (request.isUserInRole("STAFF")) {
			return "/secured/staff/index";
		} else if (request.isUserInRole("CLIENT")) {
			return "/secured/client/index";
		} else {
			return "/index";
		}
	}
	
	@GetMapping("/registerPage")
	public String register(Model model) {
		
		List<String> auths = database.getAuthorities();
		
		model.addAttribute("authorities", auths);
		
		return "/register";
	}
	
	@GetMapping("/login")
	public String login() {
		return "/login";
	}
	
	@GetMapping("/client")
	public String goToClientSecured(HttpSession session, Model model) {
		
		if (session.isNew()) {
			session.setAttribute("selectedBooks", new ArrayList<Book>());
			session.setAttribute("message", "");
		}
		
		List <Book> books = database.getBooks();
		
		model.addAttribute("books", books);
		
		return "/secured/client/index";
	}
	
	@GetMapping("/staff") 
	public String goToStaffSecured(Model model) {
		
		List <Book> books = database.getBooks();
		
		model.addAttribute("books", books);
		
		return "/secured/staff/index";
	}
	
//	REST API ==============================================================================
	
	//GET BOOK
	@SuppressWarnings("unused")
	@GetMapping("/book/{isbn}")
	public ResponseEntity<?> getBook(@PathVariable Long isbn) {
		
		Book book = database.getBook(isbn);
		
		book.setReviews(database.getReviews(isbn));
		
		if (book != null) {
			return ResponseEntity.ok(book);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("error", "No such record"));
		}
	}
	
	//GET REVIEWS
	@GetMapping("/book/{isbn}/reviews")
	public ResponseEntity<?> getReviews (@PathVariable Long isbn) {
		
		List<Review> reviews = database.getReviews(isbn);
		
		if (reviews != null) {
			return ResponseEntity.ok(reviews);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("error", "No such record"));
		}
	}
	
	@GetMapping("/book/{isbn}/{id}")
	public ResponseEntity<?> getReview (@PathVariable Long isbn, @PathVariable Long id) {
		
		Review review = database.getReview(isbn, id);
		
		if (review != null) {
			return ResponseEntity.ok(review);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("error", "No such record"));
		}
	}
	
	//POST REVIEW
	@PostMapping (value="/book", consumes="application/json", produces="application/json")
	public ResponseEntity<?> postReview (@RequestBody Review review) {
		
		try {
			Long id = (long) database.addReview(review, review.getIsbn());
			review.setId(id);
			URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
			return ResponseEntity.created(location).body(review);
			
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.LENGTH_REQUIRED).body(new Message("error", "Can't submit empty review"));
		}
	}
	
//	SECURITY ==============================================================================
	
	@GetMapping("/secured")
	public String goToSecured(Principal principal, HttpServletRequest request) {
		
		String username = principal.getName();
		System.out.println(username + " just logged in");
		
		return (request.isUserInRole("STAFF")) ? "redirect:/staff" : "redirect:/client"; 
		//AFTER LOG IN, STAFF WILL BE NAVIATED TO STAFF AREA, CLIENT WILL BE NAVIGATED TO CLIENT AREA
	}
	
	@GetMapping("/permission-denied")
	public String goToDenied() {
		return "/error/permission-denied";
	}
	
	@PostMapping("/registerUser")
	public String register (@RequestParam String username, 
							@RequestParam String password,
							@RequestParam String[] authorities, Model model) {
		
		List<GrantedAuthority> authList = new ArrayList<>();
		 			
			for (String auth: authorities) {		
				if (auth == null) {
					authList.add(new SimpleGrantedAuthority("ROLE_CLIENT")); //WHEN REGISTER, CLIENT WILL HAS ROL_CLIENT BY DEFAULT
				} else {
				authList.add(new SimpleGrantedAuthority(auth));				
				}
		}
		
		String encodedPwd = encoder.encode(password);
		
		User user = new User (username, encodedPwd, authList);
		
		manager.createUser(user);
		
		model.addAttribute("message", "Successfully Registered");
		
		return "/login";
	}
	
	
//	BOOK CONTROLLER CLIENT SIDE =============================================================	
	
	@GetMapping("/book-detail/{isbn}")
	public String getBookDetail (@PathVariable Long isbn, Model model) {
		
		Book book = database.getBook(isbn);
		List<Review> reviews = database.getReviews(isbn);

		if (book == null) 
		{
			System.out.println("No book is found for ISBN: "  + isbn);
			return "redirect:/client";
		}
		
		book.setPurchaseQuantity(1);
		model.addAttribute("review", new Review());
		model.addAttribute("book", book);
		model.addAttribute("reviews", reviews);
		
		return "secured/client/book-detail";
	}
	
	@PostMapping("/addReview/{isbn}")
	public String addReview(Principal principal, Model model, @ModelAttribute Review review, @PathVariable Long isbn) {
		
		String username = principal.getName();
		
		review.setUsername(username);
		
		Long returnValue = database.addReview(review, isbn);
		
		return "redirect:/book-detail/{isbn}";
	}
	
	@GetMapping("/cart")
	public String cartPage(Model model, HttpSession session) {
		
		if (session.getAttribute("selectedBooks") == null) {
			session.setAttribute("selectedBooks", new ArrayList<Book>());
		}
		
		model.addAttribute("selectedBooks", session.getAttribute("selectedBooks"));
		
		return "/secured/client/cart";
	}
	
	@GetMapping("/addToCart")
	public String addToCart(Model model, @ModelAttribute Book book, HttpSession session) {
		
		@SuppressWarnings("unchecked")
		ArrayList<Book> selectedBooks = (ArrayList<Book>) session.getAttribute("selectedBooks");
		
		if(selectedBooks == null) {
			selectedBooks = new ArrayList<Book>();
		}
		
		Book selectedBook = database.getBook(book.getIsbn());
		
		selectedBook.setPurchaseQuantity(book.getPurchaseQuantity());
		
		selectedBooks.add(selectedBook);
		
		session.setAttribute("selectedBooks", selectedBooks);
		
		model.addAttribute("selectedBooks", selectedBooks);
		model.addAttribute("selectedBook", selectedBook);
		
		
		return "/secured/client/cart";
	}
	
	@GetMapping("/checkOut")
	public String checkOut (HttpSession session, Model model) {
		
		@SuppressWarnings("unchecked")
		List<Book> selectedBooks = (List<Book>) session.getAttribute("selectedBooks");
		
		model.addAttribute("selectedBooks", selectedBooks);
		model.addAttribute("isCheckOut", true);
		
		//UPDATE BOOK'S STOCK AFTER PURCHASE
		for (Book book: selectedBooks) {
			book.setInventoryQuantity(book.getInventoryQuantity() - book.getPurchaseQuantity());
			database.updateStock(book);
		}
				
		session.setAttribute("selectedBooks", new ArrayList<Book>());
		
		return "/secured/client/cart";
		
	}
	
	//SEARCH FOR BOOK BY NAME (SEARCH ENGINE IN NAVBAR)
	@GetMapping("/search")
	public String searchBook (@RequestParam String keyword, Model model, HttpServletRequest request) {
		
		List<Book> books = database.getBooksByTitle(keyword);
		model.addAttribute("books", books);
		
		if (request.isUserInRole("STAFF")) {
			return "/secured/staff/index";
		} else if (request.isUserInRole("CLIENT")) {
			return "/secured/client/index";
		} else {
			return "/index";
		}
	}

//	BOOK CONTROLLER STAFF SIDE =============================================================
	
	@GetMapping("/addPage")
	public String goToAdd(Model model) {
		model.addAttribute("book", new Book());
		return "/secured/staff/add-book";
	}
	
	@PostMapping ("/staffAdd")
	public String addBook (@ModelAttribute Book book) {
		
		int returnValue = database.addBook(book);
		
		System.out.println("return value is: " + returnValue);
		
		return "redirect:/staff";
		
	}
	
	@GetMapping("/staffDelete/{isbn}")
	public String deleteBook(@PathVariable Long isbn) {
		
		int returnValue = database.deleteBook(isbn);
		
		System.out.println("return value is: " + returnValue);
		
		return "redirect:/staff";
	}
	
	@GetMapping("/staffEdit/{isbn}")
	public String getBook (@PathVariable Long isbn, Model model) {
		
		Book book = database.getBook(isbn);
		
		if (book == null) 
		{
			System.out.println("No book is found for ISBN: "  + isbn);
			return "redirect:/staff";
		}
		
		model.addAttribute("book", book);
		model.addAttribute("originalImage", book.getImage());
		System.out.println(book.getImage());
		return "secured/staff/edit-book";
	}
	
	@PostMapping("/updateBook")
	public String updateBook (@ModelAttribute Book book, @RequestParam String originalImage) {
		
		if (book.getImage() == null || book.getImage() == "") {
			book.setImage(originalImage);
		} else {
			book.setImage("image/" + book.getImage());
		}
		
		int returnValue = database.updateBook(book);
		
		System.out.println("return value is: " + returnValue);
		
		return "redirect:/staff";
	}
	
	
}
