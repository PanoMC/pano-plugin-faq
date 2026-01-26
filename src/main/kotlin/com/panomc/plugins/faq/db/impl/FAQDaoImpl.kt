package com.panomc.plugins.faq.db.impl

import com.panomc.platform.annotation.Dao
import com.panomc.plugins.faq.db.dao.FAQDao
import com.panomc.plugins.faq.db.model.FAQ
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
class FAQDaoImpl : FAQDao() {
    override val fields = listOf("id", "question", "answer", "categoryId", "displayOrder", "active")

    override suspend fun init(sqlClient: SqlClient) {
        sqlClient.query("""
            CREATE TABLE IF NOT EXISTS `${getTablePrefix() + tableName}` (
                `id` bigint NOT NULL AUTO_INCREMENT,
                `question` MEDIUMTEXT NOT NULL,
                `answer` MEDIUMTEXT NOT NULL,
                `categoryId` bigint,
                `displayOrder` int NOT NULL DEFAULT 0,
                `active` tinyint(1) NOT NULL DEFAULT 1,
                PRIMARY KEY (`id`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """).execute().coAwait()

        try {
            sqlClient.query("ALTER TABLE `${getTablePrefix() + tableName}` CHANGE COLUMN `isActive` `active` tinyint(1) NOT NULL DEFAULT 1").execute().coAwait()
        } catch (_: Exception) {
        }
    }

    override suspend fun add(faq: FAQ, sqlClient: SqlClient): Long {
        val query = "INSERT INTO `${getTablePrefix() + tableName}` (`question`, `answer`, `categoryId`, `displayOrder`, `active`) VALUES (?, ?, ?, ?, ?)"
        val rows = sqlClient.preparedQuery(query)
            .execute(Tuple.of(faq.question, faq.answer, faq.categoryId, faq.displayOrder, faq.active))
            .coAwait()
        return rows.property(MySQLClient.LAST_INSERTED_ID)
    }

    override suspend fun update(faq: FAQ, sqlClient: SqlClient) {
        val query = "UPDATE `${getTablePrefix() + tableName}` SET `question` = ?, `answer` = ?, `categoryId` = ?, `displayOrder` = ?, `active` = ? WHERE `id` = ?"
        sqlClient.preparedQuery(query)
            .execute(Tuple.of(faq.question, faq.answer, faq.categoryId, faq.displayOrder, faq.active, faq.id))
            .coAwait()
    }

    override suspend fun deleteById(id: Long, sqlClient: SqlClient) {
        val query = "DELETE FROM `${getTablePrefix() + tableName}` WHERE `id` = ?"
        sqlClient.preparedQuery(query).execute(Tuple.of(id)).coAwait()
    }

    override suspend fun getById(id: Long, sqlClient: SqlClient): FAQ? {
        val query = "SELECT ${fields.toTableQuery()} FROM `${getTablePrefix() + tableName}` WHERE `id` = ?"
        val rows = sqlClient.preparedQuery(query).execute(Tuple.of(id)).coAwait()
        return rows.toEntities().firstOrNull()
    }

    override suspend fun getAll(page: Long, status: Boolean?, search: String?, sqlClient: SqlClient): List<FAQ> {
        val offset = (page - 1) * 10
        val query = StringBuilder("SELECT ${fields.toTableQuery()} FROM `${getTablePrefix() + tableName}` WHERE 1=1")
        val params = Tuple.tuple()

        if (status != null) {
            query.append(" AND `active` = ?")
            params.addBoolean(status)
        }

        if (!search.isNullOrBlank()) {
            query.append(" AND (`question` LIKE ? OR `answer` LIKE ?)")
            val searchParam = "%$search%"
            params.addString(searchParam)
            params.addString(searchParam)
        }

        query.append(" ORDER BY `displayOrder` ASC, `id` ASC LIMIT 10 OFFSET ?")
        params.addLong(offset)

        val rows: RowSet<Row> = sqlClient
            .preparedQuery(query.toString())
            .execute(params)
            .coAwait()
        return rows.toEntities()
    }

    override suspend fun count(status: Boolean?, search: String?, sqlClient: SqlClient): Long {
        val query = StringBuilder("SELECT COUNT(`id`) FROM `${getTablePrefix() + tableName}` WHERE 1=1")
        val params = Tuple.tuple()

        if (status != null) {
            query.append(" AND `active` = ?")
            params.addBoolean(status)
        }

        if (!search.isNullOrBlank()) {
            query.append(" AND (`question` LIKE ? OR `answer` LIKE ?)")
            val searchParam = "%$search%"
            params.addString(searchParam)
            params.addString(searchParam)
        }

        val rows: RowSet<Row> = sqlClient
            .preparedQuery(query.toString())
            .execute(params)
            .coAwait()

        if (rows.size() == 0) return 0L
        return rows.toList()[0].getLong(0)
    }

    override suspend fun getByCategoryId(categoryId: Long, sqlClient: SqlClient): List<FAQ> {
        val rows: RowSet<Row> = sqlClient
            .preparedQuery("SELECT ${fields.toTableQuery()} FROM `${getTablePrefix() + tableName}` WHERE `categoryId` = ? ORDER BY `displayOrder` ASC, `id` ASC")
            .execute(Tuple.of(categoryId))
            .coAwait()
        return rows.toEntities()
    }

    override suspend fun getActive(search: String?, sqlClient: SqlClient): List<FAQ> {
        val query = StringBuilder("SELECT ${fields.toTableQuery()} FROM `${getTablePrefix() + tableName}` WHERE `active` = ?")
        val params = Tuple.of(true)

        if (!search.isNullOrBlank()) {
            query.append(" AND (`question` LIKE ? OR `answer` LIKE ?)")
            val searchParam = "%$search%"
            params.addString(searchParam)
            params.addString(searchParam)
        }

        query.append(" ORDER BY `displayOrder` ASC, `id` ASC")

        val rows: RowSet<Row> = sqlClient
            .preparedQuery(query.toString())
            .execute(params)
            .coAwait()
        return rows.toEntities()
    }
}
