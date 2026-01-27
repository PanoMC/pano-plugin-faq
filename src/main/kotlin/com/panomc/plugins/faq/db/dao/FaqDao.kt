package com.panomc.plugins.faq.db.dao

import com.panomc.platform.db.Dao
import com.panomc.plugins.faq.db.model.Faq
import io.vertx.sqlclient.SqlClient

abstract class FaqDao : Dao<Faq>(Faq::class.java) {
    abstract suspend fun add(faq: Faq, sqlClient: SqlClient): Long
    abstract suspend fun update(faq: Faq, sqlClient: SqlClient)
    abstract suspend fun deleteById(id: Long, sqlClient: SqlClient)
    abstract suspend fun getById(id: Long, sqlClient: SqlClient): Faq?
    abstract suspend fun getAll(page: Long, status: Boolean?, search: String?, sqlClient: SqlClient): List<Faq>
    abstract suspend fun count(status: Boolean?, search: String?, sqlClient: SqlClient): Long
    abstract suspend fun getByCategoryId(categoryId: Long, sqlClient: SqlClient): List<Faq>
    abstract suspend fun getActive(search: String?, sqlClient: SqlClient): List<Faq>
}
