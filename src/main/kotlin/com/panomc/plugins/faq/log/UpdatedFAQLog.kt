package com.panomc.plugins.faq.log

import com.panomc.platform.db.model.PluginActivityLog
import io.vertx.core.json.JsonObject

class UpdatedFAQLog(
    userId: Long,
    username: String,
    pluginId: String,
    question: String
) : PluginActivityLog(
    userId = userId,
    pluginId = pluginId,
    details = JsonObject().put("target", question).put("username", username)
)
