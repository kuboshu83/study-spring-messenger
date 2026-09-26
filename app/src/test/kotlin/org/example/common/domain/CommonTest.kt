package org.example.common.domain

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withIts
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

class EmailAddressTest : DescribeSpec({
    describe("constructor") {
        describe("正常系") {
            context("有効な文字列を渡されたらインスタンスを生成する") {
                withIts(
                    listOf(
                        "akira@example.com",
                        "akira.alice@example.com",
                        "akira-alice@example.com",
                        "akira_alice@example.com",
                        "AKIRA_ALICE@example.com",
                        "AKIRA0_ALICE1@example.com",
                    )
                ) { v ->
                    shouldNotThrowAny {
                        EmailAddress(v)
                    }
                }
            }
        }

        describe("異常系") {
            context("無効な文字列を渡されたらIllegalArgumentExceptionをスローする") {
                withIts(
                    mapOf(
                        "-から始まる" to "-akira@example.com",
                        "_から始まる" to "_akira@example.com",
                        "-で終わる" to "akira-@example.com",
                        "_で終わる" to "akira_@example.com",
                        "@が２つ以上" to "akira@alice@example.com",
                        "-が２つ以上連続する" to "akira--alice@example.com",
                        "_が２つ以上連続する" to "akira__alice@example.com",
                        "両端のどちらかに空白文字" to " akira@example.com",
                    )
                ) { v ->
                    shouldThrow<IllegalArgumentException> {
                        EmailAddress(v)
                    }
                }
            }
        }
    }

    describe("domain") {
        context("正常系") {
            it("アドレスのドメインを取得する") {
                // arrange
                val address = EmailAddress("akira@example.com")
                // act
                val domain = address.domain
                // assert
                domain.shouldBe("example.com")
            }
        }
    }
})

class UniqueElementsTest : DescribeSpec({
    describe("constructor") {
        context("正常系") {
            it("Set型が入力されるとインスタンスを生成して返す") {
                val elements = UniqueElements(setOf("akira", "alice", "aika", "akari"))
                with(elements) {
                    size.shouldBe(4)
                    contains("akira").shouldBeTrue()
                    contains("alice").shouldBeTrue()
                    contains("aika").shouldBeTrue()
                    contains("akari").shouldBeTrue()
                }
            }
        }
    }

    describe("add") {
        context("正常系") {
            it("未登録の要素が入力されると、その要素を登録したインスタンスを生成して返す") {
                val element = UniqueElements.empty<String>()
                val result = element.add("akira")
                with(result) {
                    size.shouldBe(1)
                    contains("akira").shouldBeTrue()
                }
            }

            it("登録済みの要素が入力されると、その要素をただ１つ含むインスタンスを生成して返す") {
                val element = UniqueElements(setOf("akira"))
                val result = element.add("akira")
                with(result) {
                    size.shouldBe(1)
                    contains("akira").shouldBeTrue()
                }
            }
        }
    }

    describe("delete") {
        context("正常系") {
            it("未登録の要素が入力されると、その要素を含まない新しいインスタンスを生成して返す") {
                val element = UniqueElements.empty<String>()
                val result = element.delete("akira")
                result.contains("akira").shouldBeFalse()
            }

            it("登録済みの要素が入力されると、その要素を削除した新しいインスタンスを生成して返す") {
                val element = UniqueElements(setOf("akira"))
                val result = element.delete("akira")
                result.contains("akira").shouldBeFalse()
            }

            it("入力された要素以外は削除しない") {
                val element = UniqueElements(setOf("akira", "alice", "akari", "aika"))
                val result = element.delete("akira")
                with(result) {
                    size.shouldBe(3)
                    contains("alice").shouldBeTrue()
                    contains("akari").shouldBeTrue()
                    contains("aika").shouldBeTrue()
                }
            }
        }
    }
})