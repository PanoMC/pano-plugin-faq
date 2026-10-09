package com.panomc.plugins.faq.util

/** The `page` object of a list answer (doc 04 section 4): `{ number, size, totalItems, totalPages }`. */
object PageInfo {
    fun of(number: Long, size: Int, totalItems: Long): Map<String, Any> = mapOf(
        "number" to number,
        "size" to size,
        "totalItems" to totalItems,
        "totalPages" to Math.ceil(totalItems.toDouble() / size).toLong()
    )
}
