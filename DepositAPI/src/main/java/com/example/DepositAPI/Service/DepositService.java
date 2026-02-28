package com.example.DepositAPI.Service;





import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.DepositAPI.Entity.DepositEntity;
import com.example.DepositAPI.Repo.DepositRepository;

@Service
public class DepositService {

    @Autowired
    private DepositRepository repository;

    public String saveDeposit(DepositEntity request) {

        DepositEntity entity = new DepositEntity();
        entity.setName(request.getName());
        entity.setLastName(request.getLastName());
        entity.setEmail(request.getEmail());

        repository.save(entity);

        return "OK";
    }
}