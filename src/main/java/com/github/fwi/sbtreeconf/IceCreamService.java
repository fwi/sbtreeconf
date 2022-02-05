package com.github.fwi.sbtreeconf;

import java.util.LinkedList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.github.fwi.sbtreeconf.db.IceCreamRepo;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class IceCreamService {

	final IceCreamRepo repo;
	final ModelMapper mapper;
	
	List<IceCreamDTO> findAll() {
		
		var iceCreams = new LinkedList<IceCreamDTO>();
		repo.findAll().forEach(r -> iceCreams.add(mapper.map(r, IceCreamDTO.class)));
		return iceCreams;
	}
	
	int countFlavor(String flavor) {
		return countFlavor(findAll(), flavor);
	}
	
	int countFlavor(List<IceCreamDTO> iceCreams, String flavor) {
		return (int) iceCreams.stream().filter(i -> i.getFlavor().equals(flavor)).count();
	}
	
}
