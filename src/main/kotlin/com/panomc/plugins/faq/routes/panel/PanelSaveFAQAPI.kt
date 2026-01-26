package com.panomc.plugins.faq.routes.panel

import com.panomc.platform.annotation.Endpoint
import com.panomc.platform.auth.AuthProvider
import com.panomc.platform.db.DatabaseManager
import com.panomc.platform.error.BadRequest
import com.panomc.platform.model.*
import com.panomc.plugins.faq.FAQPlugin
import com.panomc.plugins.faq.db.dao.FAQDao
import com.panomc.plugins.faq.db.model.FAQ
import com.panomc.plugins.faq.log.CreatedFAQLog
import com.panomc.plugins.faq.log.UpdatedFAQLog
import com.panomc.plugins.faq.permission.ManageFAQPermission
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.validation.RequestPredicate
import io.vertx.ext.web.validation.ValidationHandler
import io.vertx.ext.web.validation.builder.Bodies
import io.vertx.ext.web.validation.builder.ValidationHandlerBuilder
import io.vertx.json.schema.SchemaRepository
import io.vertx.json.schema.common.dsl.Schemas.*

@Endpoint
class PanelSaveFAQAPI(
    private val plugin: FAQPlugin,
    private val faqDao: FAQDao
) : PanelApi() {
    override val paths = listOf(Path("/api/panel/faq/save", RouteType.POST))

    private val authProvider by lazy { plugin.applicationContext.getBean(AuthProvider::class.java) }
    private val databaseManager by lazy { plugin.applicationContext.getBean(DatabaseManager::class.java) }

    override fun getValidationHandler(schemaRepository: SchemaRepository): ValidationHandler =
        ValidationHandlerBuilder.create(schemaRepository)
            .body(Bodies.json(
                objectSchema()
                    .optionalProperty("id", numberSchema())
                    .requiredProperty("question", stringSchema())
                    .requiredProperty("answer", stringSchema())
                    .optionalProperty("categoryId", numberSchema())
                    .optionalProperty("displayOrder", numberSchema())
                    .requiredProperty("isActive", booleanSchema())
            ))
            .predicate(RequestPredicate.BODY_REQUIRED)
            .build()

    override suspend fun handle(context: RoutingContext): Result {
        authProvider.requirePermission(ManageFAQPermission(), context)

        val parameters = getParameters(context)
        val data = parameters.body().jsonObject
        
        val id = data.getLong("id")
        val question = data.getString("question")
        val answer = data.getString("answer")
        val categoryId = data.getLong("categoryId")
        val displayOrder = data.getInteger("displayOrder") ?: 0
        val isActive = data.getBoolean("isActive")

        if (question.isNullOrBlank() || answer.isNullOrBlank()) {
            throw BadRequest()
        }

        val sqlClient = databaseManager.getSqlClient()
        val faq = FAQ(id, question, answer, categoryId, displayOrder, isActive)

        val userId = authProvider.getUserIdFromRoutingContext(context)
        val username = databaseManager.userDao.getUsernameFromUserId(userId, sqlClient)!!

        if (id == null) {
            faqDao.add(faq, sqlClient)
            databaseManager.panelActivityLogDao.add(CreatedFAQLog(userId, username, plugin.pluginId, question), sqlClient)
        } else {
            faqDao.update(faq, sqlClient)
            databaseManager.panelActivityLogDao.add(UpdatedFAQLog(userId, username, plugin.pluginId, question), sqlClient)
        }

        return Successful()
    }
}
