package com.example.DepositAPI.Repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.DepositAPI.Entity.DepositEntity;


public interface DepositRepository extends JpaRepository<DepositEntity, Long> {
}