package com.example.sfmclient

import org.springframework.boot.ApplicationRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

@SpringBootApplication
class SfmClientApplication {

    @Bean
    fun webClient(): WebClient {
        return WebClient
                .create("http://localhost:8080/products")
                .mutate()
                .build()
    }

    @Bean
    fun runner(wc: WebClient) = ApplicationRunner {
        wc.get()
                .uri("")
                .retrieve()
                .bodyToFlux(Product::class.java)
                .filter({ p -> p.title.equals("Hatchimals") })
                .flatMap({ p ->
                    wc.get().uri("/{id}/events", p.id)
                            .retrieve()
                            .bodyToFlux(ProductEvent::class.java)
                })
                .subscribe({ println(it) })
    }
}



fun main(args: Array<String>) {
    SpringApplication.run(SfmClientApplication::class.java, *args)
}

data class ProductEvent (val productId: String? = null, val date: Date? = null)

data class Product(val id: String? = null, val title: String? = null)