package org.example.web.application

import org.example.domain.model.ApplicationId
import org.example.domain.model.ApplicationName
import org.example.domain.model.Description
import org.example.domain.service.ApplicationCreateService
import org.example.domain.service.ApplicationDeleteService
import org.example.domain.service.ApplicationSearchService
import org.example.web.ApplicationViewModel
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
    private val applicationDeleteService: ApplicationDeleteService
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
    fun showApplicationDetailInformation(@PathVariable("id") id: String): String {
        return "application/applicationDetailForm"
    }

    @PostMapping("/{id}/delete")
    fun deleteApplicationByApplicationId(@PathVariable("id") id: String): String {
        applicationDeleteService.deleteByApplicationId(ApplicationId.fromString(id))
        return "redirect:/applications"
    }
}