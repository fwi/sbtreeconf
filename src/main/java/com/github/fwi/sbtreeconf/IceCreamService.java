package com.github.fwi.sbtreeconf;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class IceCreamService {

	final JdbcTemplate jdbcTemplate;

	List<IceCreamDTO> findAll() {
		return jdbcTemplate.query("select id, flavor, shape from ice_cream", 
				new BeanPropertyRowMapper<IceCreamDTO>(IceCreamDTO.class));
	}
	
	int countFlavor(String flavor) {
		return countFlavor(findAll(), flavor);
	}
	
	int countFlavor(List<IceCreamDTO> iceCreams, String flavor) {
		return (int) iceCreams.stream().filter(i -> i.getFlavor().equals(flavor)).count();
	}
	
}
