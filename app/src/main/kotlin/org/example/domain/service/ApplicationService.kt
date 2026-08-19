package org.example.domain.service

import org.example.domain.errors.DataConflictedException
import org.example.domain.errors.DataNotFoundException
import org.example.domain.model.Application
import org.example.domain.model.ApplicationId
import org.example.domain.model.ApplicationName
import org.example.domain.model.Description
import org.example.domain.repository.ApplicationCommand
import org.example.domain.repository.ApplicationQuery
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ApplicationSearchService(private val applicationQuery: ApplicationQuery) {
    fun findAll(): List<Application> {
        return applicationQuery.findAll()
    }

    fun findByApplicationId(applicationId: ApplicationId): Application {
        val application = applicationQuery.findByApplicationId(applicationId)
            ?: throw DataNotFoundException("specified application not found: applicationId=${applicationId.value}")
        return application
    }
}

@Service
class ApplicationCreateService(
    private val applicationCommand: ApplicationCommand,
    private val applicationQuery: ApplicationQuery
) {
    @Transactional
    fun create(applicationName: ApplicationName, description: Description): ApplicationId {
        if (applicationQuery.findByApplicationName(applicationName) != null) {
            throw DataConflictedException("specified application name already used: applicationName=${applicationName.value}")
        }

        val newApplication = Application.create(applicationName, description)
        applicationCommand.save(newApplication)
        return newApplication.id
    }
}

@Service
class ApplicationDeleteService(private val applicationCommand: ApplicationCommand) {
    fun deleteByApplicationId(applicationId: ApplicationId) {
        applicationCommand.deleteByApplicationId(applicationId)
    }
}

@Service
class ApplicationUpdateService(
    private val applicationCommand: ApplicationCommand,
    private val applicationQuery: ApplicationQuery
) {
    @Transactional
    fun update(application: Application) {
        if (applicationQuery.findByApplicationId(application.id) == null) {
            throw DataNotFoundException("specified application not found: applicationId=${application.id.value}")
        }

        applicationCommand.update(application)
    }
}