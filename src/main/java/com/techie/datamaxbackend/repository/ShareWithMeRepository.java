package com.techie.datamaxbackend.repository;


import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.techie.datamaxbackend.model.SharedWithMe;

@Repository
public interface ShareWithMeRepository extends CrudRepository<SharedWithMe, Integer> {
	
	public List<SharedWithMe> findBySharedToUserEmail(String sharedToUserEmail);
	
}
