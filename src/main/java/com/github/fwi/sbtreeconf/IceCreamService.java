package com.github.fwi.sbtreeconf;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class IceCreamService {

	List<IceCreamDTO> findAll() {
		return createData();
	}
	
	int countFlavor(String flavor) {
		return countFlavor(createData(), flavor);
	}
	
	int countFlavor(List<IceCreamDTO> iceCreams, String flavor) {
		return (int) iceCreams.stream().filter(i -> i.getFlavor().equals(flavor)).count();
	}
	
	List<IceCreamDTO> createData() {
		
		return List.of(
				IceCreamDTO.builder().id(1).flavor("vanilla").shape("cone").build(),
				IceCreamDTO.builder().id(2).flavor("vanilla").shape("waffle").build(),
				IceCreamDTO.builder().id(3).flavor("Neapolitan").shape("cone").build()
				);
	}
}
