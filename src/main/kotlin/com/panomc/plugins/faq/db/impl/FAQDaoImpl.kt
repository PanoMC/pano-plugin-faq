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

    override suspend fun init(sqlClient: SqlClient) {
        sqlClient.query("""
            CREATE TABLE IF NOT EXISTS `${getTablePrefix() + tableName}` (
                `id` bigint NOT NULL AUTO_INCREMENT,
                `question` MEDIUMTEXT NOT NULL,
                `answer` MEDIUMTEXT NOT NULL,
                `categoryId` bigint,
                `displayOrder` int NOT NULL DEFAULT 0,
                `isActive` tinyint(1) NOT NULL DEFAULT 1,
                PRIMARY KEY (`id`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """).execute().coAwait()
    }

    override suspend fun add(faq: FAQ, sqlClient: SqlClient): Long {
        val query = "INSERT INTO `${getTablePrefix() + tableName}` (`question`, `answer`, `categoryId`, `displayOrder`, `isActive`) VALUES (?, ?, ?, ?, ?)"
        val rows = sqlClient.preparedQuery(query)
            .execute(Tuple.of(faq.question, faq.answer, faq.categoryId, faq.displayOrder, faq.isActive))
            .coAwait()
        return rows.property(MySQLClient.LAST_INSERTED_ID)
    }

    override suspend fun update(faq: FAQ, sqlClient: SqlClient) {
        val query = "UPDATE `${getTablePrefix() + tableName}` SET `question` = ?, `answer` = ?, `categoryId` = ?, `displayOrder` = ?, `isActive` = ? WHERE `id` = ?"
        sqlClient.preparedQuery(query)
            .execute(Tuple.of(faq.question, faq.answer, faq.categoryId, faq.displayOrder, faq.isActive, faq.id))
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

    override suspend fun getAll(sqlClient: SqlClient): List<FAQ> {
        val rows: RowSet<Row> = sqlClient
            .query("SELECT ${fields.toTableQuery()} FROM `${getTablePrefix() + tableName}` ORDER BY `displayOrder` ASC, `id` ASC")
            .execute()
            .coAwait()
        return rows.toEntities()
    }

    override suspend fun getByCategoryId(categoryId: Long, sqlClient: SqlClient): List<FAQ> {
        val rows: RowSet<Row> = sqlClient
            .preparedQuery("SELECT ${fields.toTableQuery()} FROM `${getTablePrefix() + tableName}` WHERE `categoryId` = ? ORDER BY `displayOrder` ASC, `id` ASC")
            .execute(Tuple.of(categoryId))
            .coAwait()
        return rows.toEntities()
    }

    override suspend fun getActive(sqlClient: SqlClient): List<FAQ> {
        val rows: RowSet<Row> = sqlClient
            .query("SELECT ${fields.toTableQuery()} FROM `${getTablePrefix() + tableName}` WHERE `isActive` = 1 ORDER BY `displayOrder` ASC, `id` ASC")
            .execute()
            .coAwait()
        return rows.toEntities()
    }
}
