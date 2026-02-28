package com.example.DepositAPI.Controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.DepositAPI.DTO.EncryptedRequest;
import com.example.DepositAPI.DTO.EncryptedResponse;
import com.example.DepositAPI.Entity.DepositEntity;
import com.example.DepositAPI.Service.DepositService;
import com.example.DepositAPI.security.HybridEncryptionService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/Bank")
public class DepositController {

    @Autowired
    private DepositService depositService;

    @Autowired
    private HybridEncryptionService hybridService;

    @PostMapping("/Deposit")
    public EncryptedResponse deposit(@RequestBody EncryptedRequest encryptedRequest) throws Exception {

        // 🔓 1️⃣ Decrypt request
        DepositEntity request =
                hybridService.decryptRequest(encryptedRequest, DepositEntity.class);


  ObjectMapper mapper = new ObjectMapper();
    String prettyJson = mapper.writerWithDefaultPrettyPrinter()
                              .writeValueAsString(request);

    System.out.println("\n==== DECRYPTED REQUEST AT DEPOSIT ====");
    System.out.println(prettyJson);
    System.out.println("======================================\n");


        // 💾 2️⃣ Save to DB
        String result = depositService.saveDeposit(request);

        // 🔐 3️⃣ Encrypt response
        return hybridService.encryptResponse(result);
    }
}