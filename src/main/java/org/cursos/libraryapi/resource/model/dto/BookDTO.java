package org.cursos.libraryapi.resource.model.dto;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
	private Long id;
	
	@NotEmpty
	private String title;
	
	@NotEmpty
	private String author;
	
	@NotEmpty
	private String isbn;
}
