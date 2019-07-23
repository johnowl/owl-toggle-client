package com.johnowl.toggle.client

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class FeatureToggleIntegration {

    private val restTemplate = RestTemplate()
    private val configuration: ToggleConfiguration

    @Autowired
    constructor(configuration: ToggleConfiguration) {
        this.configuration = configuration
    }

    fun sendVariables(userId: String, variables: Map<String, Any>) {

        val response = restTemplate.postForEntity("${configuration.serverUrl}/variables/$userId", variables, String::class.java)

        if(response.statusCode != HttpStatus.OK) {
            throw ServerCommuncationProblem("Server response was ${response.statusCode}")
        }
    }

    fun isEnabled(featureToggleId: String, userId: String, defaultValue: Boolean = false): Boolean {

        val response = restTemplate.getForEntity("${configuration.serverUrl}/toggles/$featureToggleId/check/$userId", String::class.java)

        if(response.statusCode == HttpStatus.NOT_FOUND) {
            return defaultValue
        }

        return response.body == "true"
    }



}