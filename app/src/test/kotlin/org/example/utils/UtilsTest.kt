package org.example.utils

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class UtilsTest : DescribeSpec({
    describe("ellipsize") {
        val dots = "..."
        describe("正常系") {
            context("入力された文字列が指定サイズ以内の場合") {
                val size = 10
                it("入力された文字列をそのまま返す") {
                    // arrange
                    val text = "a".repeat(10)
                    // act
                    val result = ellipsize(text, size)
                    // assert
                    result.shouldBe(text)
                }

                it("空文字が入力されたら空文字をそのまま返す") {
                    // act
                    val result = ellipsize("", size)
                    // assert
                    result.shouldBe("")
                }
            }

            context("入力された文字列が指定サイズを超える場合") {
                it("入力された文字列を切り詰め、後ろに...をつけた文字列を返す") {
                    // arrange
                    val text = "a".repeat(11)
                    val size = 10
                    // act
                    val result = ellipsize(text, size)
                    // assert
                    val expected = "a".repeat(7) + dots
                    result.shouldBe(expected)
                }
            }
        }

        describe("異常系") {
            context("指定されたサイズが3以下の場合") {
                it("IllegalArgumentExceptionをThrowする") {
                    // act, assert
                    shouldThrow<IllegalArgumentException> {
                        ellipsize("a", 2)
                    }
                }
            }
        }
    }
})