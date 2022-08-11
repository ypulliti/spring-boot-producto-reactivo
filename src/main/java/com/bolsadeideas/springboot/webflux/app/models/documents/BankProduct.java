package com.bolsadeideas.springboot.webflux.app.models.documents;

import java.util.Date;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection="bankproduct")
public class BankProduct
{
	@Id
	private String id;
	private String name;
	private String productType;
	private String comision;
	private Date createAt;
	private String limitMovimientos;

	public BankProduct() {}

	public BankProduct(String nombre, String producttype, String comision, String limitMovimientos) {
		this.name = nombre;
		this.productType = producttype;
		this.comision = comision;
		this.limitMovimientos = limitMovimientos;
	}

	public BankProduct(String nombre, String producttype, String id, String comision, String limitMovimientos) {
		this.name = nombre;
		this.productType = producttype;
		this.id = id;
		this.comision = comision;
		this.limitMovimientos = limitMovimientos;
	}
}
