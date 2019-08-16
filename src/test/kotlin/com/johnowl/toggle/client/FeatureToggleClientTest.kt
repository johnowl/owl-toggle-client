package com.johnowl.toggle.client

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.anyString
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class FeatureToggleClientTest {

    private val configuration: ToggleConfiguration = mock {
        on { serverUrl } doReturn "http://localhost/"
    }

    private val emptyUrlConfiguration: ToggleConfiguration = mock {
        on { serverUrl } doReturn ""
    }

    @Test
    fun `should return false when toggle does not exist and defaultValue is omitted`() {

        val json = "{\"code\":\"toggle_not_found\",\"message\":\"Feature toggle not found.\"}"
        val webClient: WebClient = mock {
            on { get(anyString()) } doReturn ResponseEntity(json, HttpStatus.NOT_FOUND)
        }

        val integration = FeatureToggleIntegration(configuration, webClient)

        val client = FeatureToggleClient(integration)

        val response = client.isEnabled("my_toggle", "user123")
        assertFalse(response)
    }

    @Test
    fun `should return true when toggle does not exist and defaultValue is set to true`() {

        val json = "{\"code\":\"toggle_not_found\",\"message\":\"Feature toggle not found.\"}"
        val webClient: WebClient = mock {
            on { get(anyString()) } doReturn ResponseEntity(json, HttpStatus.NOT_FOUND)
        }

        val integration = FeatureToggleIntegration(configuration, webClient)

        val client = FeatureToggleClient(integration)

        val response = client.isEnabled("my_toggle", "user123", true)
        assert(response)
    }

    @Test
    fun `should return true when server response is true`() {

        val webClient: WebClient = mock {
            on { get(anyString()) } doReturn ResponseEntity("true", HttpStatus.OK)
        }

        val integration = FeatureToggleIntegration(configuration, webClient)

        val client = FeatureToggleClient(integration)

        val response = client.isEnabled("my_toggle", "user123")
        assert(response)
    }

    @Test
    fun `should return false when server response is false`() {

        val webClient: WebClient = mock {
            on { get(anyString()) } doReturn ResponseEntity("false", HttpStatus.OK)
        }

        val integration = FeatureToggleIntegration(configuration, webClient)

        val client = FeatureToggleClient(integration)

        val response = client.isEnabled("my_toggle", "user123")
        assertFalse(response)
    }

    @Test
    fun `should throw exception when server response status code is 500`() {

        val json = "{\"code\":\"internal_server_error\",\"message\":\"Erro desconhecido.\"}"

        val webClient: WebClient = mock {
            on { get(anyString()) } doReturn ResponseEntity(json, HttpStatus.INTERNAL_SERVER_ERROR)
        }

        val integration = FeatureToggleIntegration(configuration, webClient)

        val client = FeatureToggleClient(integration)

        assertThrows<ServerCommuncationProblem> {
            client.isEnabled("my_toggle", "user123")
        }
    }

    @Test
    fun `should throw exception when server response status code is 400`() {

        val json = "{\"code\":\"validation_error\",\"message\":\"Field invalid.\"}"

        val webClient: WebClient = mock {
            on { get(anyString()) } doReturn ResponseEntity(json, HttpStatus.BAD_REQUEST)
        }

        val integration = FeatureToggleIntegration(configuration, webClient)

        val client = FeatureToggleClient(integration)

        assertThrows<ServerCommuncationProblem> {
            client.isEnabled("my_toggle", "user123")
        }
    }

    @Test
    fun `should throw exception when server url configuration is empty`() {

        val webClient: WebClient = mock()

        assertThrows<EmptyServerUrlException> {
            FeatureToggleIntegration(emptyUrlConfiguration, webClient)
        }
    }
}