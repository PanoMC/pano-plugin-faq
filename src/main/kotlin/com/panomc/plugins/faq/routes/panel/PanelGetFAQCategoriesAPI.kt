package com.panomc.plugins.faq.routes.panel

import com.panomc.platform.annotation.Endpoint
import com.panomc.platform.auth.AuthProvider
import com.panomc.platform.db.DatabaseManager
import com.panomc.platform.model.*
import com.panomc.plugins.faq.FAQPlugin
import com.panomc.plugins.faq.db.dao.FaqCategoryDao
import com.panomc.plugins.faq.permission.ManageFAQPermission
import com.panomc.plugins.faq.util.PageInfo
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.validation.ValidationHandler
import com.panomc.platform.schema.dsl.ValidationHandlerBuilder
import io.vertx.json.schema.SchemaRepository

@Endpoint
class PanelGetFAQCategoriesAPI(
    private val plugin: FAQPlugin,
    private val faqCategoryDao: FaqCategoryDao
) : PanelApi() {
    override val paths = listOf(Path("/category/list", RouteType.GET))

    private val authProvider by lazy { plugin.applicationContext.getBean(AuthProvider::class.java) }
    private val databaseManager by lazy { plugin.applicationContext.getBean(DatabaseManager::class.java) }

    override fun getValidationHandler(schemaRepository: SchemaRepository): ValidationHandler =
        ValidationHandlerBuilder.create(schemaRepository).build()

    override suspend fun handle(context: RoutingContext): Result {
        authProvider.requirePermission(ManageFAQPermission(), context)

        val params = context.queryParams()
        val page = params.get("page")?.toLong() ?: 1L
        val search = params.get("search")

        val sqlClient = databaseManager.getSqlClient()
        val categories = faqCategoryDao.getAll(page, search, sqlClient)
        val categoryCount = faqCategoryDao.count(search, sqlClient)

        return Successful(mapOf(
            "items" to categories,
            "page" to PageInfo.of(page, 10, categoryCount)
        ))
    }
}
