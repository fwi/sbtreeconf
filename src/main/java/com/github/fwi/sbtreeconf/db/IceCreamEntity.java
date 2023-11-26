package com.github.fwi.sbtreeconf.db;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

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
@NoArgsConstructor
@AllArgsConstructor
public class IceCreamEntity {
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Getter
	@Setter
	private String flavor;

	@Getter
	@Setter
	private String shape;
	
	/*
	 * More at
	 * https://blog.actorsfit.in/a?ID=00400-36a32d63-d128-4269-8481-c09a7cacdef2
	 * (can't find original source).
	 */
	@Getter
	@Column(insertable = false, updatable = false)
	@Generated(event = EventType.INSERT)
	private OffsetDateTime created;

	@Getter
	@Column(insertable = false, updatable = false)
	@Generated(event = { EventType.INSERT, EventType.UPDATE })
	private OffsetDateTime modified;

	@Getter
	@Setter
	@Column(name = "modified_by")
	private String modifiedBy;

	@Override
	public String toString() {
		return getClass().getName() + " - id [" + getId() 
		+ "], flavor: [" + getFlavor() + "], shape: [" + getShape() + "]"
		+ "], created: [" + getCreated() + "], modified: [" + getModified() + "], modified by: [" + getModifiedBy() +"]";
	}
}
