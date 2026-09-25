package org.example.manager.domain.errors

open class BusinessException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class NotAllowedEmailDomainException(message: String, cause: Throwable? = null) : BusinessException(message, cause)

class DataConflictedException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class DataNotFoundException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)