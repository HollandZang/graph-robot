package com.holland.graph_robot.enums

enum class Times(val seconds: Long) {
    DAY_180(15552000L),
    DAY_30(2592000L),
    DAY_1(86400L),
    HOUR_18(64800L),
    MINUTES_60(3600L),
    MINUTES_30(1800L),
    MINUTES_5(300L),
    SECONDS_60(60L),
    ;

    val minutes: Long = seconds / 60
    val millSeconds: Long = seconds * 1000
}
