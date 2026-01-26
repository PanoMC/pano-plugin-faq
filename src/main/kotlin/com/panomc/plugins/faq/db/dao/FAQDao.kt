package com.panomc.plugins.faq.db.dao

import com.panomc.platform.db.Dao
import com.panomc.plugins.faq.db.model.FAQ
import io.vertx.sqlclient.SqlClient

abstract class FAQDao : Dao<FAQ>(FAQ::class.java) {
    abstract suspend fun add(faq: FAQ, sqlClient: SqlClient): Long
    abstract suspend fun update(faq: FAQ, sqlClient: SqlClient)
    abstract suspend fun deleteById(id: Long, sqlClient: SqlClient)
    abstract suspend fun getById(id: Long, sqlClient: SqlClient): FAQ?
    abstract suspend fun getAll(page: Long, status: Boolean?, search: String?, sqlClient: SqlClient): List<FAQ>
    abstract suspend fun count(status: Boolean?, search: String?, sqlClient: SqlClient): Long
    abstract suspend fun getByCategoryId(categoryId: Long, sqlClient: SqlClient): List<FAQ>
    abstract suspend fun getActive(search: String?, sqlClient: SqlClient): List<FAQ>
}
