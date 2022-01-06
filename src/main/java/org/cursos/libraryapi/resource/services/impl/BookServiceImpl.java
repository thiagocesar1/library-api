package org.cursos.libraryapi.resource.services.impl;

import org.cursos.libraryapi.resource.exception.BusinessException;
import org.cursos.libraryapi.resource.model.entity.Book;
import org.cursos.libraryapi.resource.repository.BookRepository;
import org.cursos.libraryapi.resource.services.BookService;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService{
	
	private BookRepository repository;
	
	public BookServiceImpl(BookRepository repository) {
		this.repository = repository;
	}
	
	@Override
	public Book save(Book book) {
		if(repository.existsByIsbn(book.getIsbn())) {
			throw new BusinessException("Isbn already exists.");
		}
		return repository.save(book);
	}

}
