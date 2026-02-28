package com.example.DepositAPI.security;



public class KeyTest {

    public static void main(String[] args) throws Exception {

        KeyLoader loader = new KeyLoader();

        System.out.println("Loading Private Key...");
        System.out.println(loader.loadPrivateKey());

        System.out.println("Loading Loans Public Key...");
        System.out.println(loader.loadLoansPublicKey());

        System.out.println("Keys Loaded Successfully!");
    }
}