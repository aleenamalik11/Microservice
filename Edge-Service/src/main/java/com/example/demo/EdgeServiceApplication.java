package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.Data;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.EntityModel;

import java.util.ArrayList;
import java.util.Collection;

@Data
class Item {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
Retrofit retrofit = new Retrofit.Builder()
  .baseUrl("https://api.github.com/")
  .addConverterFactory(GsonConverterFactory.create())
  .client(httpClient.build())
  .build();

interface ItemClient {

	@GetMapping("/items")
	EntityModel<Item> readItems();
}

@RestController
class GoodItemApiAdapterRestController {

    private final ItemClient itemClient;

    public GoodItemApiAdapterRestController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @GetMapping("/top-brands")
    public Collection<Item> goodItems() {
    	
    	Collection<Item> items = (Collection<Item>) itemClient.readItems();
    	Collection<Item> filteredItems = new ArrayList<Item>();
    	// for-each loop
    	for (Item i : items) {
    		if(isGreat(i)) {
    			filteredItems.add(i);
    		}
    	}
    	
        return filteredItems;
    }

    private boolean isGreat(Item item) {
        return !item.getName().equals("Nike") &&
                !item.getName().equals("Adidas") &&
                !item.getName().equals("Reebok");
    }
}


@EnableFeignClients
@EnableCircuitBreaker
@EnableDiscoveryClient
@SpringBootApplication
public class EdgeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EdgeServiceApplication.class, args);
	}

}
