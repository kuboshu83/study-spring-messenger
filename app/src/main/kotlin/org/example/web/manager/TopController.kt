package org.example.web.manager

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/")
class TopController {
    @GetMapping
    fun showTopPage(): String {
        return "topPage"
    }
}