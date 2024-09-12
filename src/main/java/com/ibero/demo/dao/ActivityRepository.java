package com.ibero.demo.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.ibero.demo.entity.ActivityEntity;

public interface ActivityRepository extends CrudRepository<ActivityEntity,Integer>{

	@Query("SELECT a FROM ActivityEntity a WHERE a.username = :username AND a.endTime IS NULL")
	Optional<ActivityEntity> findOngoingActivityByEventAndUser(@Param("username") String username);
}
