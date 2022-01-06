package org.cursos.libraryapi.resource.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.cursos.libraryapi.resource.exception.BusinessException;
import org.cursos.libraryapi.resource.model.dto.BookDTO;
import org.cursos.libraryapi.resource.model.entity.Book;
import org.cursos.libraryapi.resource.services.BookService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {
	
	static String BOOK_API = "/api/books";
	
	@Autowired
	MockMvc mvc;
	
	@MockBean
	BookService service;
	
	@Test
	@DisplayName("Should be able to create a book.")
	public void createBookTest() throws Exception{
		BookDTO dto = BookDTO.builder().title("My book").author("Author").isbn("123456").build();
		Book book = Book.builder().id(10L).title("My book").author("Author").isbn("123456").build();
		
		BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(book);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		MockHttpServletRequestBuilder content = MockMvcRequestBuilders
													.post(BOOK_API)
													.contentType(MediaType.APPLICATION_JSON)
													.accept(MediaType.APPLICATION_JSON)
													.content(json);
		
		mvc.perform(content)
		   .andExpect(MockMvcResultMatchers.status().isCreated())
		   .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
		   .andExpect(MockMvcResultMatchers.jsonPath("title").value(book.getTitle()))
		   .andExpect(MockMvcResultMatchers.jsonPath("author").value(book.getAuthor()))
		   .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(book.getIsbn()));
		
	}
	
	@Test
	@DisplayName("Should not be able to create a book with invalid data.")
	public void createInvalidBookTest() throws Exception {
		String json = new ObjectMapper().writeValueAsString(new BookDTO());
		MockHttpServletRequestBuilder content = MockMvcRequestBuilders
				.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);
		
		mvc.perform(content)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("errors", Matchers.hasSize(3)));	
	}
	
	@Test
	@DisplayName("Throw error if exists any book with this isbn.")
	public void createBookWithDuplcatedIsbn() throws Exception {
		BookDTO dto = BookDTO.builder().title("My book").author("Author").isbn("123456").build();
		String json = new ObjectMapper().writeValueAsString(dto);
		String errorMessage = "Isbn already exists.";
		BDDMockito.given(service.save(Mockito.any(Book.class))).willThrow(new BusinessException(errorMessage));
		
		
		MockHttpServletRequestBuilder content = MockMvcRequestBuilders
				.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);
		
		mvc.perform(content)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("errors", Matchers.hasSize(1)))
			.andExpect(jsonPath("errors[0]").value(errorMessage));
	}
}
