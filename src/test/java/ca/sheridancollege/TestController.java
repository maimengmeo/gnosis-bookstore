package ca.sheridancollege;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import ca.sheridancollege.database.DatabaseAccess;

@SpringBootTest
@AutoConfigureMockMvc
class TestController {

	private DatabaseAccess database;
	private MockMvc mockMvc;
	
	@Autowired
	public void setDatabase(DatabaseAccess database) {
		this.database = database;
	}
	
	@Autowired
	public void setMockMvc (MockMvc mockMvc) {
		this.mockMvc = mockMvc;
	}
	
	@Test
	@WithMockUser(username="testUser", roles={"CLIENT"})
	public void testUserLoggedInAndAuthorized() throws Exception {
		mockMvc.perform(get("/client"))
				.andExpect(status().isOk())
				.andExpect(view().name("/secured/client/index"));
		
	}
	
	@Test
	@WithMockUser (username="testUser", roles={"CLIENT"})
	public void testUserLoggedInNotAuthorized() throws Exception {
		mockMvc.perform(get("/staff"))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/permission-denied"));
			
	}

}
