package com.panomc.plugins.faq.routes.panel

import com.panomc.platform.annotation.Endpoint
import com.panomc.platform.auth.AuthProvider
import com.panomc.platform.db.DatabaseManager
import com.panomc.platform.error.BadRequest
import com.panomc.platform.model.*
import com.panomc.plugins.faq.FAQPlugin
import com.panomc.plugins.faq.db.dao.FAQCategoryDao
import com.panomc.plugins.faq.db.model.FAQCategory
import com.panomc.plugins.faq.log.CreatedFAQCategoryLog
import com.panomc.plugins.faq.log.UpdatedFAQCategoryLog
import com.panomc.plugins.faq.permission.ManageFAQPermission
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.validation.RequestPredicate
import io.vertx.ext.web.validation.ValidationHandler
import io.vertx.ext.web.validation.builder.Bodies
import io.vertx.ext.web.validation.builder.ValidationHandlerBuilder
import io.vertx.json.schema.SchemaRepository
import io.vertx.json.schema.common.dsl.Schemas.*

@Endpoint
class PanelSaveFAQCategoryAPI(
    private val plugin: FAQPlugin,
    private val faqCategoryDao: FAQCategoryDao
) : PanelApi() {
    override val paths = listOf(Path("/api/panel/faq/category/save", RouteType.POST))

    private val authProvider by lazy { plugin.applicationContext.getBean(AuthProvider::class.java) }
    private val databaseManager by lazy { plugin.applicationContext.getBean(DatabaseManager::class.java) }

    override fun getValidationHandler(schemaRepository: SchemaRepository): ValidationHandler =
        ValidationHandlerBuilder.create(schemaRepository)
            .body(Bodies.json(
                objectSchema()
                    .optionalProperty("id", numberSchema())
                    .requiredProperty("name", stringSchema())
                    .optionalProperty("displayOrder", numberSchema())
            ))
            .predicate(RequestPredicate.BODY_REQUIRED)
            .build()

    override suspend fun handle(context: RoutingContext): Result {
        authProvider.requirePermission(ManageFAQPermission(), context)

        val parameters = getParameters(context)
        val data = parameters.body().jsonObject
        
        val id = data.getLong("id")
        val name = data.getString("name")
        val displayOrder = data.getInteger("displayOrder") ?: 0

        if (name.isNullOrBlank()) {
            throw BadRequest()
        }

        val sqlClient = databaseManager.getSqlClient()
        val category = FAQCategory(id, name, displayOrder)

        val userId = authProvider.getUserIdFromRoutingContext(context)
        val username = databaseManager.userDao.getUsernameFromUserId(userId, sqlClient)!!

        if (id == null) {
            faqCategoryDao.add(category, sqlClient)
            databaseManager.panelActivityLogDao.add(CreatedFAQCategoryLog(userId, username, plugin.pluginId, name), sqlClient)
        } else {
            faqCategoryDao.update(category, sqlClient)
            databaseManager.panelActivityLogDao.add(UpdatedFAQCategoryLog(userId, username, plugin.pluginId, name), sqlClient)
        }

        return Successful()
    }
}
