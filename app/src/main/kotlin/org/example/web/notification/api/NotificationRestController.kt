package org.example.web.notification.api

import org.example.domain.model.ApplicationId
import org.example.notification.common.domain.model.NotificationBody
import org.example.notification.common.domain.model.NotificationTitle
import org.example.notification.publisher.domain.service.MessagePublishService
import org.springframework.web.bind.annotation.*

data class MessagePublishRequestDTO(val title: String, val body: String)

@RestController
@RequestMapping("/api/notifications")
class NotificationRestController(private val messagePublishService: MessagePublishService) {
    @PostMapping("/{id}")
    fun publishMessage(@PathVariable("id") applicationId: String, @RequestBody message: MessagePublishRequestDTO) {
        val id = ApplicationId.fromString(applicationId)
        val title = NotificationTitle(message.title)
        val body = NotificationBody(message.body)
        messagePublishService.publishMessage(id, title, body)
    }
}