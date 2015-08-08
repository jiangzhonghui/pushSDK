package com.cd.minecraft.mclauncher.services;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.cd.minecraft.mclauncher.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long>,JpaSpecificationExecutor<Account> {
	
	Account findByUid(String uid);

}
