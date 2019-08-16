package com.johnowl.toggle.client

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
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

    @Test
    fun `should send variables silently when status code is 200`() {
        val webClient: WebClient = mock {
            on { post(anyString(), any()) } doReturn ResponseEntity("", HttpStatus.OK)
        }

        val integration = FeatureToggleIntegration(configuration, webClient)
        integration.sendVariables("", emptyMap())

        verify(webClient).post("http://localhost/variables/", emptyMap<String, Any>())
    }

    @Test
    fun `should throw exception when try to send variables and receiver status code 400`() {
        val webClient: WebClient = mock {
            on { post(anyString(), any()) } doReturn ResponseEntity("", HttpStatus.BAD_REQUEST)
        }

        val integration = FeatureToggleIntegration(configuration, webClient)

        assertThrows<ServerCommuncationProblem> {
            integration.sendVariables("", emptyMap())
        }
    }

    @Test
    fun `should remove last char from server url when it is a slash and we call post method`() {
        val webClient: WebClient = mock {
            on { post(anyString(), any()) } doReturn ResponseEntity("", HttpStatus.OK)
        }

        val configuration: ToggleConfiguration = mock {
            on { serverUrl } doReturn "http://localhost/"
        }

        val integration = FeatureToggleIntegration(configuration, webClient)
        integration.sendVariables("", emptyMap())

        verify(webClient).post("http://localhost/variables/", emptyMap<String, Any>())
    }

    @Test
    fun `should remove last char from server url when it is a slash and we call get method`() {
        val webClient: WebClient = mock {
            on { get(anyString()) } doReturn ResponseEntity("", HttpStatus.OK)
        }

        val configuration: ToggleConfiguration = mock {
            on { serverUrl } doReturn "http://localhost/"
        }

        val integration = FeatureToggleIntegration(configuration, webClient)
        integration.isEnabled("toggle", "user")

        verify(webClient).get("http://localhost/toggles/toggle/check/user")
    }
}