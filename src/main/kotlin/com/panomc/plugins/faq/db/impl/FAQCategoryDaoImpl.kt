package com.panomc.plugins.faq.db.impl

import com.panomc.platform.annotation.Dao
import com.panomc.plugins.faq.db.dao.FAQCategoryDao
import com.panomc.plugins.faq.db.model.FAQCategory
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
class FAQCategoryDaoImpl : FAQCategoryDao() {

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

    override suspend fun add(category: FAQCategory, sqlClient: SqlClient): Long {
        val query = "INSERT INTO `${getTablePrefix() + tableName}` (`name`, `displayOrder`) VALUES (?, ?)"
        val rows = sqlClient.preparedQuery(query)
            .execute(Tuple.of(category.name, category.displayOrder))
            .coAwait()
        return rows.property(MySQLClient.LAST_INSERTED_ID)
    }

    override suspend fun update(category: FAQCategory, sqlClient: SqlClient) {
        val query = "UPDATE `${getTablePrefix() + tableName}` SET `name` = ?, `displayOrder` = ? WHERE `id` = ?"
        sqlClient.preparedQuery(query)
            .execute(Tuple.of(category.name, category.displayOrder, category.id))
            .coAwait()
    }

    override suspend fun deleteById(id: Long, sqlClient: SqlClient) {
        val query = "DELETE FROM `${getTablePrefix() + tableName}` WHERE `id` = ?"
        sqlClient.preparedQuery(query).execute(Tuple.of(id)).coAwait()
    }

    override suspend fun getById(id: Long, sqlClient: SqlClient): FAQCategory? {
        val query = "SELECT ${fields.toTableQuery()} FROM `${getTablePrefix() + tableName}` WHERE `id` = ?"
        val rows = sqlClient.preparedQuery(query).execute(Tuple.of(id)).coAwait()
        return rows.toEntities().firstOrNull()
    }

    override suspend fun getAll(sqlClient: SqlClient): List<FAQCategory> {
        val rows: RowSet<Row> = sqlClient
            .query("SELECT ${fields.toTableQuery()} FROM `${getTablePrefix() + tableName}` ORDER BY `displayOrder` ASC, `id` ASC")
            .execute()
            .coAwait()
        return rows.toEntities()
    }
}
