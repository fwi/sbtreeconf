package com.github.fwi.sbtreeconf.db;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * Be very careful when using lombok annotations,
 * for example @Data has side-effects (@EqualsAndHashCode) which can seriously
 * impact equals- and hashcode-functions which in turn can make JPA fail in weird ways.
 * See also https://thorben-janssen.com/lombok-hibernate-how-to-avoid-common-pitfalls/
 */
@Entity
@Table(name = "ice_cream")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IceCreamEntity {
	
	@Id
	@GeneratedValue
	private long id;
	
	private String flavor;
	private String shape;

}
