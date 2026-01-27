package com.panomc.plugins.faq.routes.api

import com.panomc.platform.annotation.Endpoint
import com.panomc.platform.api.config.PluginConfigManager
import com.panomc.platform.db.DatabaseManager
import com.panomc.platform.model.*
import com.panomc.plugins.faq.FAQPlugin
import com.panomc.plugins.faq.config.FAQConfig
import com.panomc.plugins.faq.db.dao.FaqCategoryDao
import com.panomc.plugins.faq.db.dao.FaqDao
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.validation.ValidationHandler
import io.vertx.ext.web.validation.builder.ValidationHandlerBuilder
import io.vertx.json.schema.SchemaRepository

@Endpoint
class GetFAQsAPI(
    private val plugin: FAQPlugin,
    private val faqDao: FaqDao,
    private val faqCategoryDao: FaqCategoryDao
) : Api() {
    override val paths = listOf(Path("/api/faq/list", RouteType.GET))

    private val databaseManager by lazy { plugin.applicationContext.getBean(DatabaseManager::class.java) }
    private val configManager by lazy {
        plugin.pluginBeanContext.getBean(PluginConfigManager::class.java) as PluginConfigManager<FAQConfig>
    }

    override fun getValidationHandler(schemaRepository: SchemaRepository): ValidationHandler =
        ValidationHandlerBuilder.create(schemaRepository).build()

    override suspend fun handle(context: RoutingContext): Result {
        val search = context.queryParams().get("search")
        val sqlClient = databaseManager.getSqlClient()
        val faqs = faqDao.getActive(search, sqlClient)
        val categories = faqCategoryDao.getAll(sqlClient)
        val config = configManager.config

        return Successful(mapOf(
            "faqs" to faqs,
            "categories" to categories,
            "config" to config
        ))
    }
}
