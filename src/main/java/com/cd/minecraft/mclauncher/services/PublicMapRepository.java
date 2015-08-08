package com.cd.minecraft.mclauncher.services;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.cd.minecraft.mclauncher.entity.PublicMap;

public interface PublicMapRepository extends JpaRepository<PublicMap, Long>,JpaSpecificationExecutor<PublicMap> {
		
		List<PublicMap> findByUid(String uid);
		
}
