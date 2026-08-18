package org.example.infrastructure.repository

import org.apache.ibatis.annotations.Mapper
import org.example.domain.model.*
import org.example.domain.repository.ApplicationCommand
import org.example.domain.repository.ApplicationQuery
import org.springframework.stereotype.Repository

@Repository
class ApplicationCommandImpl(
    private val applicationCommandDAO: ApplicationCommandDAO,
    private val applicationGroupDAO: ApplicationGroupCommandDAO
) : ApplicationCommand {
    override fun save(application: Application) {
        val applicationDTO = ApplicationDtoCollection.fromApplication(application).toList().first()
        applicationCommandDAO.save(applicationDTO)

        val registrations = ApplicationGroupRegistrationDtoCollection.fromApplication(application).toList()
        if (registrations.isNotEmpty()) {
            applicationGroupDAO.save(registrations)
        }
    }

    override fun deleteByApplicationId(applicationId: ApplicationId) {
        applicationCommandDAO.deleteByApplicationId(applicationId.value)
    }
}

@Repository
class ApplicationQueryImpl(private val applicationQueryDAO: ApplicationQueryDAO) : ApplicationQuery {
    override fun findAll(): List<Application> {
        val applicationDTOs = applicationQueryDAO.findAll()
        return ApplicationDtoCollection(applicationDTOs).toApplications()
    }

    override fun findByApplicationName(applicationName: ApplicationName): Application? {
        val applicationDTOs = applicationQueryDAO.findByApplicationName(applicationName.value)
        val applications = ApplicationDtoCollection(applicationDTOs).toApplications()
        return when (applications.size) {
            0 -> null
            1 -> applications.first()
            else -> throw IllegalStateException("application search result by name must be 0 or 1: resultCount=${applications.size}")
        }
    }
}

data class ApplicationDTO(
    val applicationId: String,
    val name: String,
    val locked: Boolean,
    val groupId: String?,
    val description: String
) {
    constructor(
        applicationId: ApplicationId,
        name: ApplicationName,
        locked: Boolean,
        groupId: GroupId?,
        description: Description
    ) : this(applicationId.value, name.value, locked, groupId?.value, description.value)
}

class ApplicationDtoCollection(private val applicationDTOs: List<ApplicationDTO>) {
    companion object {
        fun fromApplication(application: Application): ApplicationDtoCollection {
            if (application.groups.isEmpty()) {
                return ApplicationDtoCollection(
                    listOf(
                        ApplicationDTO(
                            application.id,
                            application.name,
                            application.locked,
                            null,
                            application.description
                        )
                    )
                )
            }

            val dtos = mutableListOf<ApplicationDTO>()
            application.groups.toList().forEach { groupId ->
                dtos.add(
                    ApplicationDTO(
                        application.id,
                        application.name,
                        application.locked,
                        groupId,
                        application.description
                    )
                )
            }
            return ApplicationDtoCollection(dtos.toList())
        }
    }

    fun toList(): List<ApplicationDTO> {
        return applicationDTOs
    }

    fun toApplications(): List<Application> {
        val applications = mutableMapOf<ApplicationId, Application>()

        for (applicationDTO in applicationDTOs) {
            val applicationId = ApplicationId.fromString(applicationDTO.applicationId)
            val name = ApplicationName(applicationDTO.name)
            val locked = applicationDTO.locked
            val groupId = applicationDTO.groupId?.let { GroupId.fromString(it) }
            val description = Description(applicationDTO.description)

            val application = applications[applicationId]
            if (application != null) {
                if (groupId != null) {
                    application.addGroup(groupId)
                }
            } else {
                val newApplication = Application(applicationId, name, locked, description)
                applications[applicationId] = if (groupId == null) {
                    newApplication
                } else {
                    newApplication.addGroup(groupId)
                }
            }
        }
        return applications.values.toList()
    }
}

data class ApplicationGroupRegistrationDTO(val applicationId: String, val groupId: String) {
    constructor(applicationId: ApplicationId, groupId: GroupId) : this(applicationId.value, groupId.value)
}

class ApplicationGroupRegistrationDtoCollection(private val applicationGroupRegistrationDTOs: List<ApplicationGroupRegistrationDTO>) {
    companion object {
        fun fromApplication(application: Application): ApplicationGroupRegistrationDtoCollection {
            val registrations = mutableListOf<ApplicationGroupRegistrationDTO>()
            val applicationId = application.id
            for (groupId in application.groups.toList()) {
                registrations.add(ApplicationGroupRegistrationDTO(applicationId, groupId))
            }
            return ApplicationGroupRegistrationDtoCollection(registrations.toList())
        }
    }

    fun toList(): List<ApplicationGroupRegistrationDTO> {
        return applicationGroupRegistrationDTOs
    }
}

@Mapper
interface ApplicationQueryDAO {
    fun findAll(): List<ApplicationDTO>
    fun findByApplicationName(applicationName: String): List<ApplicationDTO>
}

@Mapper
interface ApplicationCommandDAO {
    fun save(application: ApplicationDTO)
    fun deleteByApplicationId(applicationId: String)
}

@Mapper
interface ApplicationGroupCommandDAO {
    fun save(registrations: List<ApplicationGroupRegistrationDTO>)
}