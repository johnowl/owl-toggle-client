package com.johnowl.toggle.client

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FeatureToggleClient {

    private val integration: FeatureToggleIntegration

    @Autowired
    constructor(integration: FeatureToggleIntegration) {
        this.integration = integration
    }

    fun sendVariables(userId: String, variables: Map<String, Any>) {
        return integration.sendVariables(userId, variables)
    }

    fun isEnabled(featureToggleId: String, userId: String, defaultValue: Boolean = false): Boolean {
        return integration.isEnabled(featureToggleId, userId, defaultValue)
    }

}