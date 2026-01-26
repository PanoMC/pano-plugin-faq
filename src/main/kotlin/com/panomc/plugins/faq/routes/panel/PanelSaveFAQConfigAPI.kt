package com.panomc.plugins.faq.routes.panel

import com.panomc.platform.annotation.Endpoint
import com.panomc.platform.api.config.PluginConfigManager
import com.panomc.platform.auth.AuthProvider
import com.panomc.platform.model.*
import com.panomc.plugins.faq.FAQPlugin
import com.panomc.plugins.faq.config.FAQConfig
import com.panomc.plugins.faq.config.FAQDisplayLocation
import com.panomc.plugins.faq.permission.ManageFAQPermission
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.validation.ValidationHandler
import io.vertx.ext.web.validation.builder.Bodies
import io.vertx.ext.web.validation.builder.ValidationHandlerBuilder
import io.vertx.json.schema.SchemaRepository
import io.vertx.json.schema.common.dsl.Schemas.objectSchema
import io.vertx.json.schema.common.dsl.Schemas.stringSchema

@Endpoint
class PanelSaveFAQConfigAPI(
    private val plugin: FAQPlugin
) : PanelApi() {
    override val paths = listOf(Path("/api/panel/faq/config", RouteType.POST))

    private val authProvider by lazy { plugin.applicationContext.getBean(AuthProvider::class.java) }
    private val configManager by lazy {
        plugin.pluginBeanContext.getBean(PluginConfigManager::class.java) as PluginConfigManager<FAQConfig>
    }

    override fun getValidationHandler(schemaRepository: SchemaRepository): ValidationHandler =
        ValidationHandlerBuilder.create(schemaRepository)
            .body(Bodies.json(
                objectSchema()
                    .optionalProperty("displayLocation", stringSchema())
            ))
            .build()

    override suspend fun handle(context: RoutingContext): Result {
        authProvider.requirePermission(ManageFAQPermission(), context)

        val body = context.body().asJsonObject()
        
        val currentConfig = configManager.config

        body.getString("displayLocation")?.let { currentConfig.displayLocation = FAQDisplayLocation.valueOf(it) }

        configManager.saveConfig(JsonObject.mapFrom(currentConfig))

        return Successful(mapOf("config" to currentConfig))
    }
}
