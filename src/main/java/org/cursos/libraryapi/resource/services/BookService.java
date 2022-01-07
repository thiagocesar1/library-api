package org.cursos.libraryapi.resource.services;

import java.awt.print.Pageable;
import java.util.Optional;

import org.cursos.libraryapi.resource.model.entity.Book;
import org.springframework.data.domain.Page;

public interface BookService {
	Book save(Book book);
	
	Optional<Book> getById(Long id);

	void delete(Book book);

	Book update(Book book);

	Page<Book> find(Book filter, Pageable pageRequest);
}
