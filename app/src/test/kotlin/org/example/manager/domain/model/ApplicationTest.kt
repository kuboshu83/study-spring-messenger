package org.example.manager.domain.model

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withIts

class ApplicationNameTest : DescribeSpec({
    describe("constructor") {
        context("正常系") {
            context("有効な文字列が入力されるとインスタンスを生成して返す") {
                withIts(
                    listOf(
                        "application",
                        "Application",
                        "application01",
                        "01_application",
                        "sample-application",
                        "sample application",
                        "sample_application",
                        "this is sample application",
                        "a".repeat(50),
                    )
                ) {
                    shouldNotThrowAny { ApplicationName(it) }
                }
            }
        }

        context("異常系") {
            it("長すぎる文字列が入力された場合はIllegalArgumentExceptionをThrowする") {
                // arrange
                val longString = "a".repeat(51)
                // act, assert
                shouldThrow<IllegalArgumentException> {
                    ApplicationName(longString)
                }
            }

            context("無効な文字列が入力されたらIllegalArgumentExceptionをThrowする") {
                withIts(
                    mapOf(
                        "無効な文字を使用している" to "application@",
                        "空白文字から始まる" to " application",
                        "空白文字で終わる" to "application ",
                        "空文字" to "",
                    )
                ) {
                    shouldThrow<IllegalArgumentException> {
                        ApplicationName(it)
                    }
                }
            }
        }
    }
})