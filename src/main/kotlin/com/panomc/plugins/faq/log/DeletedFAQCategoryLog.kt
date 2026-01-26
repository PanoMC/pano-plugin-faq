package com.panomc.plugins.faq.log

import com.panomc.platform.db.model.PluginActivityLog
import io.vertx.core.json.JsonObject

class DeletedFAQCategoryLog(
    userId: Long,
    username: String,
    pluginId: String,
    name: String
) : PluginActivityLog(
    userId = userId,
    pluginId = pluginId,
    details = JsonObject().put("target", name).put("username", username)
)
