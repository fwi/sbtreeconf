package com.github.fwi.sbtreeconf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IceCreamDTO {

	private long id;
	private String flavor;
	private String shape;
}
