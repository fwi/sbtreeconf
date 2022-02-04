package com.github.fwi.sbtreeconf;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IceCreamDTO {

	long id;
	String flavor;
	String shape;
}
