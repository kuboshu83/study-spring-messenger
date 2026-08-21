package org.example.notification.web.api

import org.example.manager.domain.model.ApplicationId
import org.example.notification.domain.model.MessageBody
import org.example.notification.domain.model.MessageTitle
import org.example.notification.domain.service.MessagePublishService
import org.springframework.web.bind.annotation.*

data class MessagePublishRequestDTO(val title: String, val body: String)

@RestController
@RequestMapping("/api/notifications")
class NotificationRestController(private val messagePublishService: MessagePublishService) {
    @PostMapping("/{id}")
    fun publishMessage(@PathVariable("id") applicationId: String, @RequestBody message: MessagePublishRequestDTO) {
        // TODO: 入力値からドメインオブジェクトへの変換が失敗した場合は"Bad Request"を返すようにする
        val id = ApplicationId.fromString(applicationId)
        val title = MessageTitle(message.title)
        val body = MessageBody(message.body)

        // TODO: 送信中にエラーが発生した場合はリトライする
        messagePublishService.publishMessage(id, title, body)
    }
}