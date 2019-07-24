package com.johnowl.toggle.client

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class WebClient {

    private val restTemplate = RestTemplate()

    fun get(url: String): ResponseEntity<String> = restTemplate.getForEntity(url, String::class.java)
    fun post(url: String, body: Any) = restTemplate.postForEntity(url, body, String::class.java)
}