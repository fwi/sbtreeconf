package com.github.fwi.sbtreeconf;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data transfer object - don't care about immutability
 * since these objects live for a very short while.
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_EMPTY)
public class IceCreamRequest {
	
	private Long id;

	@NotBlank
	@Size(min = 2, max = 128)
	private String flavor;

	@NotBlank
	@Size(min = 2, max = 128)
	private String shape;
}
