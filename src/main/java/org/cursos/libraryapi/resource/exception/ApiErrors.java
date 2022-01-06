package org.cursos.libraryapi.resource.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.validation.BindingResult;

public class ApiErrors {
	List<String> errors;
	
	public ApiErrors(BindingResult bindingResult) {
		this.errors = new ArrayList<>();
		bindingResult.getAllErrors().forEach(error -> this.errors.add(error.getDefaultMessage()));
	}
	
	public ApiErrors(BusinessException businessException) {
		this.errors = Arrays.asList(businessException.getMessage());
	}
	
	public List<String> getErrors() {
		return errors;
	}
}
