package com.panomc.plugins.faq.db.migration

import com.panomc.platform.annotation.Migration
import com.panomc.platform.db.DatabaseMigration
import io.vertx.kotlin.coroutines.coAwait
import io.vertx.sqlclient.SqlClient

@Migration
class DbMigration1to2: DatabaseMigration(
    1,
    2,
    "Rename faqcategory table to faq_category"
) {
    override val handlers: List<suspend (SqlClient) -> Unit> = listOf(
        renameFaqCategoryTable()
    )

    private fun renameFaqCategoryTable(): suspend (sqlClient: SqlClient) -> Unit =
        { sqlClient: SqlClient ->
            val prefix = getTablePrefix()
            val query = "RENAME TABLE `${prefix}faqcategory` TO `${prefix}faq_category`"
            sqlClient.query(query).execute().coAwait()
        }
}
