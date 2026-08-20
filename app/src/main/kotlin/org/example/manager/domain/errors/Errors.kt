package org.example.manager.domain.errors

class TooLongException(message: String, cause: Throwable? = null) : Exception(message, cause)

class NotAllowedDomainException(message: String, cause: Throwable? = null) : Exception(message, cause)

class InvalidFormatException(message: String, cause: Throwable? = null) : Exception(message, cause)

class DataConflictedException(message: String, cause: Throwable? = null) : Exception(message, cause)

class DataNotFoundException(message: String, cause: Throwable? = null) : Exception(message, cause)