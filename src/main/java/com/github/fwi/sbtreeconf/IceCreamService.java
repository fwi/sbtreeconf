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
	
	TypeMap<IceCreamResponse, IceCreamEntity> dto2entity;

	List<IceCreamResponse> findAll() {
		
		var iceCreams = new LinkedList<IceCreamResponse>();
		repo.findAll().forEach(r -> iceCreams.add(mapper.map(r, IceCreamResponse.class)));
		return iceCreams;
	}
	
	IceCreamResponse findOne(long id) {
		return repo.findById(id).map(r -> mapper.map(r, IceCreamResponse.class)).orElse(null);
	}

	long count() {
		return repo.count();
	}

	long countFlavor(String flavor) {
		return repo.countByFlavor(flavor);
		// return countFlavor(findAll(), flavor);
	}
	
	// this is just as an example, normally repo would be updated with filter-query.
	int countFlavor(List<IceCreamResponse> iceCreams, String flavor) {
		return (int) iceCreams.stream().filter(i -> i.getFlavor().equals(flavor)).count();
	}
	
	IceCreamResponse upsert(IceCreamRequest iceCream) {
		
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
		return mapper.map(record, IceCreamResponse.class);
	}

}
