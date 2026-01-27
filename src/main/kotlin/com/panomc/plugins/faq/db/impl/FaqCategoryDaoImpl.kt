package com.panomc.plugins.faq.db.impl

import com.panomc.platform.annotation.Dao
import com.panomc.plugins.faq.db.dao.FaqCategoryDao
import com.panomc.plugins.faq.db.model.FaqCategory
import io.vertx.kotlin.coroutines.coAwait
import io.vertx.mysqlclient.MySQLClient
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.RowSet
import io.vertx.sqlclient.SqlClient
import io.vertx.sqlclient.Tuple
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Scope

@Dao
@Lazy
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
class FaqCategoryDaoImpl : FaqCategoryDao() {

    override suspend fun init(sqlClient: SqlClient) {
        sqlClient.query("""
            CREATE TABLE IF NOT EXISTS `${getTablePrefix() + tableName}` (
                `id` bigint NOT NULL AUTO_INCREMENT,
                `name` MEDIUMTEXT NOT NULL,
                `displayOrder` int NOT NULL DEFAULT 0,
                PRIMARY KEY (`id`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """).execute().coAwait()
    }

    override suspend fun add(category: FaqCategory, sqlClient: SqlClient): Long {
        val query = "INSERT INTO `${getTablePrefix() + tableName}` (`name`, `displayOrder`) VALUES (?, ?)"
        val rows = sqlClient.preparedQuery(query)
            .execute(Tuple.of(category.name, category.displayOrder))
            .coAwait()
        return rows.property(MySQLClient.LAST_INSERTED_ID)
    }

    override suspend fun update(category: FaqCategory, sqlClient: SqlClient) {
        val query = "UPDATE `${getTablePrefix() + tableName}` SET `name` = ?, `displayOrder` = ? WHERE `id` = ?"
        sqlClient.preparedQuery(query)
            .execute(Tuple.of(category.name, category.displayOrder, category.id))
            .coAwait()
    }

    override suspend fun deleteById(id: Long, sqlClient: SqlClient) {
        val query = "DELETE FROM `${getTablePrefix() + tableName}` WHERE `id` = ?"
        sqlClient.preparedQuery(query).execute(Tuple.of(id)).coAwait()
    }

    override suspend fun getById(id: Long, sqlClient: SqlClient): FaqCategory? {
        val query = "SELECT ${fields.toTableQuery()} FROM `${getTablePrefix() + tableName}` WHERE `id` = ?"
        val rows = sqlClient.preparedQuery(query).execute(Tuple.of(id)).coAwait()
        return rows.toEntities().firstOrNull()
    }

    override suspend fun getAll(sqlClient: SqlClient): List<FaqCategory> {
        val query = "SELECT ${fields.toTableQuery()} FROM `${getTablePrefix() + tableName}` ORDER BY `displayOrder` ASC, `id` ASC"
        val rows: RowSet<Row> = sqlClient
            .preparedQuery(query)
            .execute()
            .coAwait()
        return rows.toEntities()
    }

    override suspend fun getAll(page: Long, search: String?, sqlClient: SqlClient): List<FaqCategory> {
        val limit = 10L
        val offset = (page - 1) * limit
        val whereClauses = mutableListOf<String>()
        val params = mutableListOf<Any>()

        if (search != null) {
            whereClauses.add("`name` LIKE ?")
            params.add("%$search%")
        }

        val whereQuery = if (whereClauses.isNotEmpty()) "WHERE " + whereClauses.joinToString(" AND ") else ""
        val query = "SELECT ${fields.toTableQuery()} FROM `${getTablePrefix() + tableName}` $whereQuery ORDER BY `displayOrder` ASC, `id` ASC LIMIT ? OFFSET ?"
        params.add(limit)
        params.add(offset)

        val rows = sqlClient.preparedQuery(query)
            .execute(Tuple.from(params))
            .coAwait()
        return rows.toEntities()
    }

    override suspend fun count(search: String?, sqlClient: SqlClient): Long {
        val whereClauses = mutableListOf<String>()
        val params = mutableListOf<Any>()

        if (search != null) {
            whereClauses.add("`name` LIKE ?")
            params.add("%$search%")
        }

        val whereQuery = if (whereClauses.isNotEmpty()) "WHERE " + whereClauses.joinToString(" AND ") else ""
        val query = "SELECT COUNT(*) FROM `${getTablePrefix() + tableName}` $whereQuery"
        val rows = sqlClient.preparedQuery(query)
            .execute(Tuple.from(params))
            .coAwait()
        return rows.first().getLong(0)
    }
}
