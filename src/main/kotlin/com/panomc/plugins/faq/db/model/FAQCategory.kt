package com.panomc.plugins.faq.db.model

import com.panomc.platform.db.DBEntity

open class FAQCategory(
    val id: Long? = null,
    val name: String = "",
    val displayOrder: Int = 0
) : DBEntity()
