package com.johnowl.toggle.client

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class FeatureToggleIntegration {

    private val webClient: WebClient
    private val configuration: ToggleConfiguration

    @Autowired
    constructor(configuration: ToggleConfiguration, webClient: WebClient) {

        if (configuration.serverUrl.isEmpty() || configuration.serverUrl.isBlank())
            throw EmptyServerUrlException()

        this.configuration = configuration
        this.webClient = webClient
    }

    fun sendVariables(userId: String, variables: Map<String, Any>) {

        val url = "${configuration.serverUrl}/variables/$userId"
        val response = webClient.post(url, variables)

        if (response.statusCode != HttpStatus.OK) {
            throw ServerCommuncationProblem("Server response was ${response.statusCode}")
        }
    }

    fun isEnabled(featureToggleId: String, userId: String, defaultValue: Boolean = false): Boolean {

        val url = "${configuration.serverUrl}/toggles/$featureToggleId/check/$userId"
        val response = webClient.get(url)

        if (response.statusCode == HttpStatus.OK) {
            return response.body == "true"
        }

        if (response.statusCode == HttpStatus.NOT_FOUND) {
            return defaultValue
        }

        throw ServerCommuncationProblem("Server response was ${response.statusCode}")
    }
}