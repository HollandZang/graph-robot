package com.holland.graph_robot.extension

import com.holland.graph_robot.enums.Messages
import org.springframework.context.MessageSource
import java.util.*

object I18nExt {
    @JvmName("MessageSource")
    fun MessageSource.getMessage(
        code: Messages,
        vararg args: Any = arrayOf(),
        locale: Locale = Locale.CHINA
    ) =
        this.getMessage(code.name, args, locale)
}