package com.johnowl.toggle.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ToggleConfiguration(@Value("\${owl.toggle.client.server-url}") val serverUrl: String)