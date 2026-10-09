package com.panomc.plugins.faq.sitemap

import com.panomc.platform.api.SitemapEntry
import com.panomc.platform.api.SitemapProvider
import com.panomc.platform.api.config.PluginConfigManager
import com.panomc.plugins.faq.FAQPlugin
import com.panomc.plugins.faq.config.FAQConfig
import com.panomc.plugins.faq.config.FAQDisplayLocation
import com.panomc.plugins.faq.db.dao.FaqDao
import io.vertx.sqlclient.SqlClient
import org.springframework.stereotype.Component

/** The FAQ page, when it is a page of its own and has at least one active question, for `GET /api/v1/sitemap`. */
@Component
class FaqSitemapProvider(
    private val plugin: FAQPlugin,
    private val faqDao: FaqDao
) : SitemapProvider {
    private val configManager by lazy {
        plugin.pluginBeanContext.getBean(PluginConfigManager::class.java) as PluginConfigManager<FAQConfig>
    }

    override suspend fun entries(sqlClient: SqlClient): List<SitemapEntry> {
        if (configManager.config.displayLocation != FAQDisplayLocation.THEME_PAGE) {
            return emptyList()
        }

        if (faqDao.getActive(null, sqlClient).isEmpty()) {
            return emptyList()
        }

        return listOf(SitemapEntry("pano-plugin-faq:faq", emptyMap()))
    }
}
