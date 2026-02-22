package com.oytask.app.data.model

enum class TaskFilter {
    ALL, PENDING, COMPLETED, TODAY, OVERDUE, THIS_WEEK
}

enum class SortOrder {
    BY_DATE_ASC, BY_DATE_DESC, BY_PRIORITY, BY_CREATED, BY_NAME
}
