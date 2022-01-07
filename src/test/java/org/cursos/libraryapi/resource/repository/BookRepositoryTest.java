package org.cursos.libraryapi.resource.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.cursos.libraryapi.resource.model.entity.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {
	@Autowired
	TestEntityManager entityManager;
	
	@Autowired
	BookRepository repository;
	
	@Test
	@DisplayName("Should be return true if book exists with a isbn.")
	public void returnTrueWhenIsbnExists() {
		String isbn = "123";
		entityManager.persist(Book.builder().author("test").title("test").isbn(isbn).build());
		
		boolean exists = repository.existsByIsbn(isbn);
		
		Assertions.assertThat(exists).isTrue();
	}
	
	@Test
	@DisplayName("Should be return false if book doesnt exists with a isbn.")
	public void returnFalseWhenIsbnNotExists() {
		String isbn = "123";
		
		boolean exists = repository.existsByIsbn(isbn);
		
		Assertions.assertThat(exists).isFalse();
	}
	
	@Test
	@DisplayName("Should able to find a book by id.")
	public void findByIdTest() {
		Book book = Book.builder().author("test").title("test").isbn("1234").build();
		entityManager.persist(book);
		
		Optional<Book> foundBook = repository.findById(book.getId());
		
		Assertions.assertThat(foundBook.isPresent()).isTrue();
	}
	
	@Test
	@DisplayName("Should able to save book.")
	public void saveBookTest() {
		Book book = Book.builder().author("test").title("test").isbn("1234").build();
		
		Book savedBook = repository.save(book);
		
		Assertions.assertThat(savedBook.getId()).isNotNull();
	}
	
	@Test
	@DisplayName("Should able to delete book.")
	public void deleteBookTest() {
		Book book = Book.builder().author("test").title("test").isbn("1234").build();
		entityManager.persist(book);
		
		Book foundBook = entityManager.find(Book.class, book.getId());
		
		repository.delete(foundBook);
		
		Book deletedBook = entityManager.find(Book.class, book.getId());
		
		Assertions.assertThat(deletedBook).isNull();
	}
}
