package com.bolsadeideas.springboot.webflux.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;

import com.bolsadeideas.springboot.webflux.app.models.dao.ProductDao;
import com.bolsadeideas.springboot.webflux.app.models.documents.BankProduct;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductoRestController {
	@Autowired
	private ProductDao dao;

	@GetMapping
	public Mono<ResponseEntity<Flux<BankProduct>>> lista(){
		return Mono.just(
				ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(dao.findAll())
				);
	}
	
	@GetMapping("/{id}")
	public Mono<ResponseEntity<BankProduct>> ver(@PathVariable String id){
		return dao.findById(id).map(p -> ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(p))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}
	
	@PostMapping
	public Mono<ResponseEntity<Map<String, Object>>> crear(@RequestBody Mono<BankProduct> bankProduct){
		
		Map<String, Object> respuesta = new HashMap<String, Object>();
		
		return bankProduct.flatMap(producto -> {
			if(producto.getCreateAt()==null) {
				producto.setCreateAt(new Date());
			}
			
			return dao.save(producto).map(p-> {
				respuesta.put("producto", p);
				respuesta.put("mensaje", "Producto creado con éxito");
				respuesta.put("timestamp", new Date());
				return ResponseEntity
					.created(URI.create("/api/productos/".concat(p.getId())))
					.contentType(MediaType.APPLICATION_JSON)
					.body(respuesta);
				});
			
		}).onErrorResume(t -> {
			return Mono.just(t).cast(WebExchangeBindException.class)
					.flatMap(e -> Mono.just(e.getFieldErrors()))
					.flatMapMany(Flux::fromIterable)
					.map(fieldError -> "El campo "+fieldError.getField() + " " + fieldError.getDefaultMessage())
					.collectList()
					.flatMap(list -> {
						respuesta.put("errors", list);
						respuesta.put("timestamp", new Date());
						respuesta.put("status", HttpStatus.BAD_REQUEST.value());
						return Mono.just(ResponseEntity.badRequest().body(respuesta));
					});
							
		});
		

	}
	
	
	@PutMapping("/{id}")
	public Mono<ResponseEntity<BankProduct>> editar(@RequestBody BankProduct bankProduct, @PathVariable String id){
		return dao.findById(id).flatMap(p -> {
			p.setName(bankProduct.getName());
			p.setProductType(bankProduct.getProductType());
			p.setComision(bankProduct.getComision());
			p.setLimitMovimientos(bankProduct.getLimitMovimientos());

			return dao.save(p);
		}).map(p->ResponseEntity.created(URI.create("/api/products/".concat(p.getId())))
				.contentType(MediaType.APPLICATION_JSON)
				.body(p))
		.defaultIfEmpty(ResponseEntity.notFound().build());
	}
	
	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Void>> eliminar(@PathVariable String id){
		return dao.findById(id).flatMap(p ->{
			return dao.delete(p).then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
		}).defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
	}
}
