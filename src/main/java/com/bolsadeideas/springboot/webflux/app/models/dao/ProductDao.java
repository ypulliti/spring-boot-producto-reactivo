package com.bolsadeideas.springboot.webflux.app.models.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.bolsadeideas.springboot.webflux.app.models.documents.BankProduct;

public interface ProductDao extends ReactiveMongoRepository<BankProduct, String>{

}
