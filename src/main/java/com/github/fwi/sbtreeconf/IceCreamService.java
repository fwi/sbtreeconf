package com.github.fwi.sbtreeconf;

import java.util.LinkedList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Component;

import com.github.fwi.sbtreeconf.db.IceCreamEntity;
import com.github.fwi.sbtreeconf.db.IceCreamRepo;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class IceCreamService {

	final IceCreamRepo repo;
	final ModelMapper mapper;
	
	TypeMap<IceCreamDTO, IceCreamEntity> dto2entity;

	List<IceCreamDTO> findAll() {
		
		var iceCreams = new LinkedList<IceCreamDTO>();
		repo.findAll().forEach(r -> iceCreams.add(mapper.map(r, IceCreamDTO.class)));
		return iceCreams;
	}
	
	IceCreamDTO findOne(long id) {
		return repo.findById(id).map(r -> mapper.map(r, IceCreamDTO.class)).orElse(null);
	}

	int countFlavor(String flavor) {
		return countFlavor(findAll(), flavor);
	}
	
	int countFlavor(List<IceCreamDTO> iceCreams, String flavor) {
		return (int) iceCreams.stream().filter(i -> i.getFlavor().equals(flavor)).count();
	}
	
	IceCreamDTO upsert(IceCreamDTO iceCream) {
		
		IceCreamEntity record = null;
		if (iceCream.getId() == null) {
			record = repo.save(mapper.map(iceCream, IceCreamEntity.class));
		} else {
			record = repo.findById(iceCream.getId()).orElse(null);
			if (record == null) {
				return null;
			}
			mapper.map(iceCream, record);
			record = repo.save(record);
		}
		return mapper.map(record, IceCreamDTO.class);
	}

}
