package org.cursos.libraryapi.resource.service;

import org.assertj.core.api.Assertions;
import org.cursos.libraryapi.resource.exception.BusinessException;
import org.cursos.libraryapi.resource.model.entity.Book;
import org.cursos.libraryapi.resource.repository.BookRepository;
import org.cursos.libraryapi.resource.services.BookService;
import org.cursos.libraryapi.resource.services.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

	BookService service;
	
	@MockBean
	BookRepository repository;
	
	@BeforeEach
	public void setUp() {
		this.service = new BookServiceImpl(repository);
	}
	
	@Test
	@DisplayName("Should be able to save a book.")
	public void saveBookTest() {
		Book book = Book.builder().id(10L).title("My book").author("Author").isbn("123456").build();
		Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
		Mockito.when(repository.save(book))
				.thenReturn(Book.builder().id(10L).title("My book").author("Author").isbn("123456").build());
		
		Book savedBook = service.save(book);
		
		Assertions.assertThat(savedBook.getId()).isNotNull();
		Assertions.assertThat(savedBook.getIsbn()).isEqualTo("123456");
		Assertions.assertThat(savedBook.getAuthor()).isEqualTo("Author");
		Assertions.assertThat(savedBook.getTitle()).isEqualTo("My book");
	}
	
	@Test
	@DisplayName("Should not save a book with duplicated isbn.")
	public void saveBookWithDuplicatedIsbnTest() {
		Book book = Book.builder().id(10L).title("My book").author("Author").isbn("123456").build();
		Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);
		
		Throwable exception = Assertions.catchThrowable( () -> service.save(book));
		Assertions.assertThat(exception)
					.isInstanceOf(BusinessException.class)
					.hasMessage("Isbn already exists.");
		
		Mockito.verify(repository, Mockito.never()).save(book);
	}
}
