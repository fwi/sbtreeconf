package com.github.fwi.sbtreeconf;

import java.util.LinkedList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.github.fwi.sbtreeconf.db.IceCreamEntity;
import com.github.fwi.sbtreeconf.db.IceCreamRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class IceCreamService {

	private final IceCreamRepo repo;
	private final ModelMapper mapper;
	
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
	}
	
	// this is just as an example, normally repo would be updated with filter-query.
	int countFlavor(List<IceCreamResponse> iceCreams, String flavor) {
		return (int) iceCreams.stream().filter(i -> i.getFlavor().equals(flavor)).count();
	}
	
	IceCreamResponse upsert(IceCreamRequest iceCream, String user) {
		
		IceCreamEntity dbRecord = null;
		if (iceCream.getId() == null) {
			dbRecord = mapper.map(iceCream, IceCreamEntity.class);
			dbRecord.setModifiedBy(user);
			dbRecord = repo.save(dbRecord);
		} else {
			dbRecord = repo.findById(iceCream.getId()).orElse(null);
			if (dbRecord == null) {
				return null;
			}
			mapper.map(iceCream, dbRecord);
			dbRecord = repo.save(dbRecord);
		}
		return mapper.map(dbRecord, IceCreamResponse.class);
	}

	IceCreamResponse delete(long id, String user) {
		
		var deleted = findOne(id);
		if (deleted != null) {
			log.info("Deleting ice-cream {} (user {})", id, user);
			repo.deleteById(id);
		}
		return deleted;
	}

}
