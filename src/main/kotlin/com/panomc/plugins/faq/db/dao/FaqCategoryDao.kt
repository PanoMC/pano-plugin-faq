package com.panomc.plugins.faq.db.dao

import com.panomc.platform.db.Dao
import com.panomc.plugins.faq.db.model.FaqCategory
import io.vertx.sqlclient.SqlClient

abstract class FaqCategoryDao : Dao<FaqCategory>(FaqCategory::class.java) {
    abstract suspend fun add(category: FaqCategory, sqlClient: SqlClient): Long
    abstract suspend fun update(category: FaqCategory, sqlClient: SqlClient)
    abstract suspend fun deleteById(id: Long, sqlClient: SqlClient)
    abstract suspend fun getById(id: Long, sqlClient: SqlClient): FaqCategory?
    abstract suspend fun getAll(sqlClient: SqlClient): List<FaqCategory>
    abstract suspend fun getAll(page: Long, search: String?, sqlClient: SqlClient): List<FaqCategory>
    abstract suspend fun count(search: String?, sqlClient: SqlClient): Long
}
