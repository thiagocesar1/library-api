package org.cursos.libraryapi.resource.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.awt.print.Pageable;
import java.util.Optional;

import org.assertj.core.util.Arrays;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
	
	@Test
	@DisplayName("Should be able to find details of a book")
	public void getBookDetailsTest() throws Exception {
		Long id = 1L;
		Book book = Book.builder().id(id).title("My book").author("Author").isbn("123456").build();
		BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));
		
		MockHttpServletRequestBuilder content = MockMvcRequestBuilders
				.get(BOOK_API.concat("/"+id))
				.accept(MediaType.APPLICATION_JSON);
		
		mvc.perform(content)
		 	.andExpect(MockMvcResultMatchers.status().isOk())
		    .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
		    .andExpect(MockMvcResultMatchers.jsonPath("title").value(book.getTitle()))
		    .andExpect(MockMvcResultMatchers.jsonPath("author").value(book.getAuthor()))
		    .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(book.getIsbn()));
	}
	
	@Test
	@DisplayName("Throw error if book not exists.")
	public void getBookNotFoundTest() throws Exception {
		Long id = 1L;
		BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());
		
		MockHttpServletRequestBuilder content = MockMvcRequestBuilders
				.get(BOOK_API.concat("/"+id))
				.accept(MediaType.APPLICATION_JSON);
		
		mvc.perform(content)
	 	.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
	
	@Test
	@DisplayName("Should be able to delete a book.")
	public void deleteBookTest() throws Exception {
		Long id = 1L;
		Book book = Book.builder().id(id).build();
		BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.of(book));
		
		MockHttpServletRequestBuilder content = MockMvcRequestBuilders
				.delete(BOOK_API.concat("/"+id));
		
		mvc.perform(content)
			.andExpect(MockMvcResultMatchers.status().isNoContent());	
	}
	
	@Test
	@DisplayName("Throw error if book not exists on delete.")
	public void deleteBookNotFoundTest() throws Exception {
		Long id = 1L;
		BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());
		
		MockHttpServletRequestBuilder content = MockMvcRequestBuilders
				.delete(BOOK_API.concat("/"+id));
		
		mvc.perform(content)
	 	.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
	
	@Test
	@DisplayName("Should be able to update a book.")
	public void updateBookTest() throws Exception {
		Long id = 1L;
		BookDTO dto = BookDTO.builder().title("My book").author("Author").build();
		String json = new ObjectMapper().writeValueAsString(dto);
		Book book = Book.builder().id(id).title("Book of update").author("updater").isbn("005").build();
		BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.of(book));
		BDDMockito.given(service.update(Mockito.any(Book.class))).willReturn(book);
		
		MockHttpServletRequestBuilder content = MockMvcRequestBuilders
				.put(BOOK_API.concat("/"+id))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);
		
		mvc.perform(content)
			.andExpect(MockMvcResultMatchers.status().isOk())
		    .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
		    .andExpect(MockMvcResultMatchers.jsonPath("title").value(book.getTitle()))
		    .andExpect(MockMvcResultMatchers.jsonPath("author").value(book.getAuthor()))
		    .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(book.getIsbn()));
	}
	
	@Test
	@DisplayName("Throw error if book not exists on update.")
	public void updateBookNotFoundTest() throws Exception {
		Long id = 1L;
		BookDTO dto = BookDTO.builder().title("My book").author("Author").isbn("123456").build();
		String json = new ObjectMapper().writeValueAsString(dto);
		BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());
		
		MockHttpServletRequestBuilder content = MockMvcRequestBuilders
				.put(BOOK_API.concat("/"+id))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);
		
		mvc.perform(content)
			.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
	
	@Test
	@DisplayName("Should be able to find a book.")
	public void findBooksTest() throws Exception {
		Long id = 1L;
		Book book = Book.builder().title("My book").author("Author").isbn("123456").build();
		
		BDDMockito.given(service.find(Mockito.any(Book.class), Mockito.any(Pageable.class)))
				  .willReturn(new PageImpl<Book>(Arrays.asList(book), PageRequest.of(0, 100), 1) );
	}
	
	
}
