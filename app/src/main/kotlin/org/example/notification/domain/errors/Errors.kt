package org.example.notification.domain.errors

class NotifyException(message: String, cause: Throwable? = null) : Exception(message, cause)
