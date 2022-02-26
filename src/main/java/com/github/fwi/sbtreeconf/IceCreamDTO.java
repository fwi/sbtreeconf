package com.github.fwi.sbtreeconf;

import java.time.OffsetDateTime;

import javax.validation.constraints.NotBlank;

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
public class IceCreamDTO {
	
	private Long id;
	@NotBlank
	private String flavor;
	private String shape;
	
	private OffsetDateTime created;
	private OffsetDateTime modified;
}
