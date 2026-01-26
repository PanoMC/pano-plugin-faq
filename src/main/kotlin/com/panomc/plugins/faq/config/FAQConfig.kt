package com.panomc.plugins.faq.config

import com.panomc.platform.api.config.PluginConfig

class FAQConfig(
    var displayLocation: FAQDisplayLocation = FAQDisplayLocation.THEME_PAGE,
    var showSearch: Boolean = true,
    var questionLimit: Int = 0,
    version: Int = 1
) : PluginConfig(version)

enum class FAQDisplayLocation {
    THEME_PAGE,
    SUPPORT_PAGE
}
