package org.cursos.libraryapi.resource.repository;

import org.cursos.libraryapi.resource.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
	boolean existsByIsbn(String isbn);
}
