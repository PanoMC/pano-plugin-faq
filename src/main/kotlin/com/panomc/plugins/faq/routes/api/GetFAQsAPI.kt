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
import com.panomc.platform.schema.dsl.ValidationHandlerBuilder
import io.vertx.json.schema.SchemaRepository
import com.panomc.platform.schema.EndpointDoc
import io.vertx.json.schema.common.dsl.Schemas.*

@Endpoint
class GetFAQsAPI(
    private val plugin: FAQPlugin,
    private val faqDao: FaqDao,
    private val faqCategoryDao: FaqCategoryDao
) : Api() {
    override val paths = listOf(Path("/list", RouteType.GET))

    override val doc = EndpointDoc(
        summary = "The active questions with their categories and the display settings; ?search= filters the questions.",
        tag = "faq",
        response = objectSchema()
            .requiredProperty(
                "faqs",
                arraySchema().items(
                    objectSchema()
                        .requiredProperty("id", intSchema())
                        .requiredProperty("question", stringSchema())
                        .requiredProperty("answer", stringSchema())
                        .optionalProperty("categoryId", intSchema().nullable())
                        .optionalProperty("displayOrder", intSchema())
                        .optionalProperty("active", booleanSchema())
                )
            )
            .requiredProperty(
                "categories",
                arraySchema().items(
                    objectSchema()
                        .requiredProperty("id", intSchema())
                        .requiredProperty("name", stringSchema())
                        .optionalProperty("displayOrder", intSchema())
                )
            )
            .requiredProperty(
                "config",
                objectSchema()
                    .requiredProperty("displayLocation", stringSchema())
                    .requiredProperty("showSearch", booleanSchema())
                    .requiredProperty("questionLimit", intSchema())
            )
    )

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
