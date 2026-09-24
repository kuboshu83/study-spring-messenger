package org.example.manager.domain.errors

class TooLongException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class NotAllowedDomainException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class InvalidFormatException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class DataConflictedException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class DataNotFoundException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)