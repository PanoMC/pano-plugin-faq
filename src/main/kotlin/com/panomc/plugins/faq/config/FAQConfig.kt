package com.panomc.plugins.faq.config

import com.panomc.platform.api.config.PluginConfig

class FAQConfig(
    var displayLocation: FAQDisplayLocation = FAQDisplayLocation.BOTH,
    version: Int = 1
) : PluginConfig(version)

enum class FAQDisplayLocation {
    THEME_PAGE,
    SUPPORT_PAGE,
    BOTH
}
