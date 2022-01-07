package org.cursos.libraryapi.resource.services.impl;

import java.awt.print.Pageable;
import java.util.Optional;

import org.cursos.libraryapi.resource.exception.BusinessException;
import org.cursos.libraryapi.resource.model.entity.Book;
import org.cursos.libraryapi.resource.repository.BookRepository;
import org.cursos.libraryapi.resource.services.BookService;
import org.springframework.data.domain.Page;
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

	@Override
	public Optional<Book> getById(Long id) {
		return repository.findById(id);
	}

	@Override
	public void delete(Book book) {
		if(book == null || book.getId() == null) {
			throw new IllegalArgumentException("Book id cant be null");
		}
		repository.delete(book);
	}

	@Override
	public Book update(Book book) {
		if(book == null || book.getId() == null) {
			throw new IllegalArgumentException("Book id cant be null");
		}
		return repository.save(book);
	}

	@Override
	public Page<Book> find(Book filter, Pageable pageRequest) {
		// TODO Auto-generated method stub
		return null;
	}

}
