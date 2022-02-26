package com.github.fwi.sbtreeconf.db;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IceCreamRepo extends CrudRepository<IceCreamEntity, Long> {

	long countByFlavor(String flavor);
}
