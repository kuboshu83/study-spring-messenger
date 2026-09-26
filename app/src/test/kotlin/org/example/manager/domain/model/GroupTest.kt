package org.example.manager.domain.model

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withIts

class GroupNameTest : DescribeSpec({
    describe("constructor") {
        context("正常系") {
            context("有効な文字列が入力されたらインスタンスを生成する") {
                withIts(
                    listOf(
                        "akira",
                        "akira group",
                        "Akira Group",
                        "akira0 group1",
                        "akira alice group",
                        "a".repeat(20),
                    )
                ) { v ->
                    shouldNotThrowAny {
                        GroupName(v)
                    }
                }
            }
        }

        context("異常系") {
            it("長い文字列が入力されたらIllegalArgumentExceptionをThrowする") {
                // arrange
                val longString = "a".repeat(21)
                // act, assert
                val error = shouldThrow<IllegalArgumentException> {
                    GroupName(longString)
                }
                print(error.message)
            }

            context("無効な文字列が入力されたらIllegalArgumentExceptionをThrowする") {
                withIts(
                    mapOf(
                        "無効な文字を使用している" to "akira@",
                        "空文字" to "",
                        "日本語" to "アキラ",
                    )
                ) { v ->
                    val error = shouldThrow<IllegalArgumentException> {
                        GroupName(v)
                    }
                    print(error.message)
                }
            }
        }
    }
})