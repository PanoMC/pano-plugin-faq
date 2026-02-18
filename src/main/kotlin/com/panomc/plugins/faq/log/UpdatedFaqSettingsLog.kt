package com.panomc.plugins.faq.log

import com.panomc.platform.db.model.PluginActivityLog
import io.vertx.core.json.JsonObject

class UpdatedFaqSettingsLog(
    userId: Long,
    username: String,
    pluginId: String
) : PluginActivityLog(
    userId = userId,
    pluginId = pluginId,
    details = JsonObject().put("username", username)
)
