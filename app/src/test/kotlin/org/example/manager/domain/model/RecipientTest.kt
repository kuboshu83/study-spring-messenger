package org.example.manager.domain.model

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withIts
import org.example.manager.domain.errors.NotAllowedEmailDomainException

class RecipientNameTest : DescribeSpec({
    describe("constructor") {
        context("正常系") {
            context("有効な文字列が入力された場合はインスタンスを生成する") {
                withIts(listOf("tanaka-ichiro", "john", "a".repeat(20))) { v ->
                    shouldNotThrowAny {
                        RecipientName(v)
                    }
                }
            }
        }

        context("異常系") {
            it("長すぎる文字列が入力された場合はIllegalArgumentExceptionをスローする") {
                // arrange
                val longText = "a".repeat(21)
                shouldThrow<IllegalArgumentException> {
                    RecipientName(longText)
                }
            }

            context("無効な文字列が入力された場合はIllegalArgumentExceptionをスローする") {
                withIts(
                    mapOf(
                        "大文字を使用" to "John",
                        "数字を使用" to "john1",
                        "ハイフンで終わる" to "tanaka-",
                        "ハイフンを２つ以上使用" to "tanaka-john-ichiro",
                    )
                ) { v ->
                    shouldThrow<IllegalArgumentException> {
                        RecipientName(v)
                    }
                }
            }
        }
    }
})

class RecipientEmailAddressTest : DescribeSpec({
    describe("of") {
        context("正常系") {
            context("有効な文字列が入力されたらインスタンスを生成する") {
                withIts(
                    listOf(
                        "akira@example.com",
                        "akira-alice@example.com",
                    )
                ) { v ->
                    shouldNotThrowAny {
                        RecipientEmailAddress.of(v)
                    }
                }
            }


        }
        context("異常系") {
            it("無効な文字列が入力されたらIllegalArgumentExceptionをスローする") {
                // arrange
                val invalidAddress = "akira"
                // act, assert
                shouldThrow<IllegalArgumentException> {
                    RecipientEmailAddress.of(invalidAddress)
                }
            }

            it("未許可のドメインを持つアドレスが入力されたらNotAllowedEmailDomainExceptionをスローする") {
                // arrange
                val notAllowedAddress = "akira@example.net"
                // act, assert
                shouldThrowExactly<NotAllowedEmailDomainException> {
                    RecipientEmailAddress.of(notAllowedAddress)
                }
            }
        }
    }
})