package com.johnowl.toggle.client

import org.springframework.stereotype.Service

@Service
class FeatureToggleClient(private val integration: FeatureToggleIntegration) {

    fun sendVariables(userId: String, variables: Map<String, Any>) {
        return integration.sendVariables(userId, variables)
    }

    fun isEnabled(featureToggleId: String, userId: String, defaultValue: Boolean = false): Boolean {
        return integration.isEnabled(featureToggleId, userId, defaultValue)
    }

}