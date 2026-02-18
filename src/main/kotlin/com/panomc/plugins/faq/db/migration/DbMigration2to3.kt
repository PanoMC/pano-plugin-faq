package com.panomc.plugins.faq.db.migration

import com.panomc.platform.annotation.Migration
import com.panomc.platform.db.DatabaseMigration
import io.vertx.kotlin.coroutines.coAwait
import io.vertx.sqlclient.SqlClient

@Migration
class DbMigration2to3: DatabaseMigration(
    2,
    3,
    "Rename activity log types"
) {
    override val handlers: List<suspend (SqlClient) -> Unit> = listOf(
        renameActivityLogTypes()
    )

    private fun renameActivityLogTypes(): suspend (sqlClient: SqlClient) -> Unit =
        { sqlClient: SqlClient ->
            val prefix = getTablePrefix()
            val types = mapOf(
                "CREATED_FAQCATEGORY" to "CREATED_FAQ_CATEGORY",
                "DELETED_FAQCATEGORY" to "DELETED_FAQ_CATEGORY",
                "UPDATED_FAQCATEGORY" to "UPDATED_FAQ_CATEGORY",
                "UPDATED_FAQSETTINGS" to "UPDATED_FAQ_SETTINGS"
            )

            types.forEach { (oldType, newType) ->
                val query = "UPDATE `${prefix}panel_activity_log` SET `type` = '$newType' WHERE `type` = '$oldType'"
                sqlClient.query(query).execute().coAwait()
            }
        }
}
