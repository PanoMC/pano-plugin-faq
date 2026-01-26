package com.panomc.plugins.faq.db.dao

import com.panomc.platform.db.Dao
import com.panomc.plugins.faq.db.model.FAQCategory
import io.vertx.sqlclient.SqlClient

abstract class FAQCategoryDao : Dao<FAQCategory>(FAQCategory::class.java) {
    abstract suspend fun add(category: FAQCategory, sqlClient: SqlClient): Long
    abstract suspend fun update(category: FAQCategory, sqlClient: SqlClient)
    abstract suspend fun deleteById(id: Long, sqlClient: SqlClient)
    abstract suspend fun getById(id: Long, sqlClient: SqlClient): FAQCategory?
    abstract suspend fun getAll(sqlClient: SqlClient): List<FAQCategory>
}
