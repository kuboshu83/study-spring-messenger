package org.example.web.application

import org.example.manager.domain.model.*
import org.example.manager.domain.service.*
import org.example.web.ApplicationViewModel
import org.example.web.GroupViewModel
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/applications")
class ApplicationController(
    private val applicationSearchService: ApplicationSearchService,
    private val applicationCreateService: ApplicationCreateService,
    private val applicationDeleteService: ApplicationDeleteService,
    private val applicationUpdateService: ApplicationUpdateService,
    private val groupSearchService: GroupSearchService
) {
    companion object {
        private const val REDIRECT_TO_APPLICATION_TOP = "redirect:/applications"
    }

    @GetMapping
    fun showTopPage(model: Model): String {
        val applications = applicationSearchService.findAll()
        val applicationViewModels = applications.map { ApplicationViewModel.fromDomain(it) }
        model.addAttribute("applications", applicationViewModels)
        return "application/topPage"
    }

    @GetMapping("/create")
    fun showApplicationCreationFrom(): String {
        return "application/applicationCreationForm"
    }

    @PostMapping("/create")
    fun createApplication(form: ApplicationCreationForm): String {
        applicationCreateService.create(ApplicationName(form.name), Description(form.description))
        return "redirect:/applications"
    }

    @GetMapping("/{id}")
    fun showApplicationDetailInformation(@PathVariable("id") id: String, model: Model): String {
        val application = applicationSearchService.findByApplicationId(ApplicationId.fromString(id))
        val applicationViewModel = ApplicationViewModel.fromDomain(application)
        model.addAttribute("app", applicationViewModel)

        val groups = if (!application.isEmpty()) {
            groupSearchService.findGroupsByGroupIds(application.groups.toList().toSet())
        } else {
            emptyList()
        }
        val groupViewModels = groups.map { group -> GroupViewModel.fromDomain(group) }
        model.addAttribute("registeredGroups", groupViewModels)
        return "application/applicationDetailForm"
    }

    @PostMapping("/{id}/delete")
    fun deleteApplicationByApplicationId(@PathVariable("id") id: String): String {
        applicationDeleteService.deleteByApplicationId(ApplicationId.fromString(id))
        return "redirect:/applications"
    }

    @PostMapping("/{id}/update")
    fun updateApplication(@PathVariable("id") id: String, form: ApplicationUpdateForm): String {
        val groups = ApplicationUniqueMembers(form.groups.map { GroupId.fromString(it) })
        val application = Application(
            ApplicationId.fromString(id),
            ApplicationName(form.name),
            form.locked,
            groups,
            Description(form.description)
        )
        applicationUpdateService.update(application)
        return REDIRECT_TO_APPLICATION_TOP
    }
}