package org.example.domain.repository

import org.example.domain.model.Application
import org.example.domain.model.ApplicationId
import org.example.domain.model.ApplicationName

interface ApplicationCommand {
    fun save(application: Application)
    fun deleteByApplicationId(applicationId: ApplicationId)
}

interface ApplicationQuery {
    fun findAll(): List<Application>
    fun findByApplicationName(applicationName: ApplicationName): Application?
}