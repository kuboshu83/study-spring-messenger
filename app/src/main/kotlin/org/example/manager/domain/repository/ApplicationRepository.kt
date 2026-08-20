package org.example.manager.domain.repository

import org.example.manager.domain.model.Application
import org.example.manager.domain.model.ApplicationId
import org.example.manager.domain.model.ApplicationName

interface ApplicationCommand {
    fun save(application: Application)
    fun deleteByApplicationId(applicationId: ApplicationId)
    fun update(application: Application)
}

interface ApplicationQuery {
    fun findAll(): List<Application>
    fun findByApplicationName(applicationName: ApplicationName): Application?
    fun findByApplicationId(applicationId: ApplicationId): Application?
}