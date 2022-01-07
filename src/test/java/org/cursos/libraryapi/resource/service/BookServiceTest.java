package org.cursos.libraryapi.resource.service;

import java.util.Optional;

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
	
	@Test
	@DisplayName("Should be able to get a book by id.")
	public void getBookByIdTest() {
		Long id = 1L;
		Book book = Book.builder().id(id).title("My book").author("Author").isbn("123456").build();
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));
		
		Optional<Book> foundBook = service.getById(id);
		
		Assertions.assertThat(foundBook.isPresent()).isTrue();
		Assertions.assertThat(foundBook.get().getId()).isEqualTo(id);
		Assertions.assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
		Assertions.assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
		Assertions.assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());	
	}
	
	@Test
	@DisplayName("Should not find a book with non-existing id.")
	public void getBookByIdNotFoundTest() {
		Long id = 1L;
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
		
		Optional<Book> book = service.getById(id);
		
		Assertions.assertThat(book.isPresent()).isFalse();
	}
	
	@Test
	@DisplayName("Should be able to delete a book.")
	public void deleteBookTest() {
		Book book = Book.builder().id(10L).build();
		
		org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.delete(book));
		
		Mockito.verify(repository, Mockito.times(1)).delete(book);
	}
	
	@Test
	@DisplayName("Should not be able to delete a book with non-existing id.")
	public void deleteInvalidBookTest() {
		Book book = new Book();
		
		org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.delete(book));
		
		Mockito.verify(repository, Mockito.never()).delete(book);
	}
	
	@Test
	@DisplayName("Should be able to update a book.")
	public void updateBookTest() {
		Long id = 10L;
		Book updatingBook = Book.builder().id(id).build();
		Book updatedBook = Book.builder().id(id).title("My book").author("Author").isbn("123456").build();
		
		Mockito.when(repository.save(updatingBook)).thenReturn(updatedBook);
		
		Book book = service.update(updatingBook);
		
		Assertions.assertThat(book.getId()).isEqualTo(updatedBook.getId());
		Assertions.assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());
		Assertions.assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());
		Assertions.assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());	
	}
	
	@Test
	@DisplayName("Should not be able to update a book with non-existing id.")
	public void updateInvalidBookTest() {
		Book book = new Book();
		
		org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.update(book));
		
		Mockito.verify(repository, Mockito.never()).save(book);
	}
	
	
}
