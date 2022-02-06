package com.github.fwi.sbtreeconf;

import java.time.OffsetDateTime;

import javax.validation.constraints.NotBlank;

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
public class IceCreamDTO {
	
	// Z = +0100, XXX = +01:00 (JVM standard)
	public static final String DATE_FORMAT_ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

	private Long id;
	@NotBlank
	private String flavor;
	private String shape;
	
	private OffsetDateTime created;
	private OffsetDateTime modified;
}
