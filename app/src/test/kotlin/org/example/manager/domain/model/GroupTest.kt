package org.example.manager.domain.model

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withIts
import io.kotest.matchers.shouldBe

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

class UniqueGroupMembersTest : DescribeSpec({
    describe("empty") {
        context("正常系") {
            it("メンバーが未登録のインスタンスを生成する") {
                // act
                val result = GroupUniqueMembers.empty()
                // assert
                result.isEmpty().shouldBe(true)
            }
        }
    }

    describe("add") {
        context("正常系") {
            it("未登録のIDが入力されたら、入力されたIDを追加したインスタンスを生成する") {
                // arrange
                val id = RecipientId.createRandom()
                val members = GroupUniqueMembers.empty()
                // act
                val result = members.add(id)
                //  assert
                result.contains(id).shouldBe(true)
            }

            it("登録済みのIDが入力されたら、入力されたIDをただ１つ含んだインスタンスを生成する") {
                // arrange
                val id = RecipientId.createRandom()
                val members = GroupUniqueMembers(setOf(id))
                // act
                val result = members.add(id)
                // assert
                result.contains(id).shouldBe(true)
                result.size.shouldBe(1)
            }
        }
    }

    describe("delete") {
        context("正常系") {
            it("登録済みのIDが入力されたら、入力されたIDを削除したインスタンスを生成する") {
                // arrange
                val id = RecipientId.createRandom()
                val members = GroupUniqueMembers(setOf(id))
                // act
                val result = members.delete(id)
                // assert
                result.contains(id).shouldBe(false)
                result.size.shouldBe(0)
            }

            it("未登録のIDが入力されたら、入力されたIDを含まないインスタンスを生成する") {
                // arrange
                val id = RecipientId.createRandom()
                val members = GroupUniqueMembers.empty()
                // act
                val result = members.delete(id)
                // assert
                result.contains(id).shouldBe(false)
            }

            it("入力されたID以外は削除されない") {
                // arrange
                val id = RecipientId.createRandom()
                val members = GroupUniqueMembers(setOf(id))
                // act
                val result = members.delete(RecipientId.createRandom())
                // assert
                result.contains(id).shouldBe(true)
            }
        }
    }

    describe("subtract") {
        context("正常系") {
            it("自身が空の場合、入力されたインスタンスによらず空のインスタンスを返す") {
                // arrange
                val member = GroupUniqueMembers.empty()
                val other = GroupUniqueMembers(setOf(RecipientId.createRandom()))
                // act
                val result = member.subtract(other)
                // assert
                result.isEmpty().shouldBe(true)
            }

            it("自身が空でない場合、入力されたインスタンスに登録されているIDを削除した差分のインスタンスを返す") {
                // arrange
                val id0 = RecipientId.createRandom()
                val id1 = RecipientId.createRandom()
                val id2 = RecipientId.createRandom()
                val member = GroupUniqueMembers(setOf(id0, id1, id2))
                val other = GroupUniqueMembers(setOf(id2, RecipientId.createRandom()))
                // act
                val result = member.subtract(other)
                // assert
                result.contains(id0).shouldBe(true)
                result.contains(id1).shouldBe(true)
                result.contains(id2).shouldBe(false)
            }
        }
    }
})