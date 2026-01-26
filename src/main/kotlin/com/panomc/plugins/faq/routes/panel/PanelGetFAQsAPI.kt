package com.panomc.plugins.faq.routes.panel

import com.panomc.platform.annotation.Endpoint
import com.panomc.platform.auth.AuthProvider
import com.panomc.platform.db.DatabaseManager
import com.panomc.platform.model.*
import com.panomc.plugins.faq.FAQPlugin
import com.panomc.plugins.faq.db.dao.FAQCategoryDao
import com.panomc.plugins.faq.db.dao.FAQDao
import com.panomc.plugins.faq.permission.ManageFAQPermission
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.validation.ValidationHandler
import io.vertx.ext.web.validation.builder.ValidationHandlerBuilder
import io.vertx.json.schema.SchemaRepository

@Endpoint
class PanelGetFAQsAPI(
    private val plugin: FAQPlugin,
    private val faqDao: FAQDao,
    private val faqCategoryDao: FAQCategoryDao
) : PanelApi() {
    override val paths = listOf(Path("/api/panel/faq/list", RouteType.GET))

    private val authProvider by lazy { plugin.applicationContext.getBean(AuthProvider::class.java) }
    private val databaseManager by lazy { plugin.applicationContext.getBean(DatabaseManager::class.java) }

    override fun getValidationHandler(schemaRepository: SchemaRepository): ValidationHandler =
        ValidationHandlerBuilder.create(schemaRepository).build()

    override suspend fun handle(context: RoutingContext): Result {
        authProvider.requirePermission(ManageFAQPermission(), context)

        val sqlClient = databaseManager.getSqlClient()
        val faqs = faqDao.getAll(sqlClient)
        val categories = faqCategoryDao.getAll(sqlClient)

        return Successful(mapOf(
            "faqs" to faqs,
            "categories" to categories
        ))
    }
}
