package com.github.fwi.sbtreeconf;

import java.time.OffsetDateTime;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonFormat;

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
	
	// TODO: this should really not be needed, by default Spring sets "write_dates_as_timestamps" to "false"
	// which should always return dates as strings. But dates are always returned as array, e.g. [2022,2,5,22,31,24,486315000]
	// by the controller for some reason.
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT_ISO_8601)
	private OffsetDateTime created;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT_ISO_8601)
	private OffsetDateTime modified;
}
