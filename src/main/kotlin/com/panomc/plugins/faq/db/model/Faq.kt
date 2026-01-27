package com.panomc.plugins.faq.db.model

import com.panomc.platform.db.DBEntity

open class Faq(
    val id: Long? = null,
    val question: String = "",
    val answer: String = "",
    val categoryId: Long? = null,
    val displayOrder: Int = 0,
    val active: Boolean = true
) : DBEntity()
