package org.example.manager.infrastructure.repository

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.example.config.TestContainerConfiguration
import org.example.manager.domain.errors.DataNotFoundException
import org.example.manager.domain.errors.DuplicateDataException
import org.example.manager.domain.model.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ApplyExtension(SpringExtension::class)
@Import(TestContainerConfiguration::class)
@Testcontainers
@Transactional
class GroupCommandImplNormalTest(
    private val recipientCommand: RecipientCommandImpl,
    private val groupCommand: GroupCommandImpl,
    private val template: JdbcTemplate,
) : DescribeSpec() {
    companion object {
        private val akira = Recipient.create(RecipientName("akira"), RecipientEmailAddress.of("akira@example.com"))
        private val alice = Recipient.create(RecipientName("alice"), RecipientEmailAddress.of("alice@example.com"))

        private val group01 = Group.create(GroupName("group01"), GroupDescription.of("test group 01"))
    }

    init {
        describe("save") {
            context("正常系") {
                context("メンバーを所有していないグループ作成") {
                    it("未登録の名前のデータが入力された場合はデータを保存する") {
                        // arrange
                        groupCommand.save(group01)
                        // assert
                        val result = template.queryForObject(
                            "SELECT count(*) FROM groups WHERE id=CAST(? AS uuid)",
                            Int::class.java,
                            group01.id.value,
                        )

                        result shouldBe 1
                    }
                }

                context("メンバーを所有しているグループの作成") {
                    it("登録済みの受信者のみを所有している場合はグループの保存と同時にメンバーとして登録する") {
                        // arrange
                        recipientCommand.save(akira)
                        recipientCommand.save(alice)
                        val group = group01.addMember(akira.id).addMember(alice.id)
                        // act
                        groupCommand.save(group)
                        // assert
                        val result: List<Map<String, Any?>> = template.queryForList(
                            """
                            SELECT
                                c.name
                            FROM groups AS a
                            INNER JOIN recipients_groups AS b
                            ON a.id = b.group_id
                            INNER JOIN recipients AS c
                            ON b.recipient_id = c.id
                            WHERE a.id = CAST(? AS uuid)
                        """.trimIndent(),
                            group.id.value,
                        )

                        result shouldHaveSize 2
                        result.map { r -> r["name"] as String }.sorted() shouldBe listOf("akira", "alice").sorted()
                    }
                }
            }

            context("異常系") {
                it("登録済みの名前のデータが入力された場合はDuplicateDataExceptionをThrowする") {
                    // arrange
                    groupCommand.save(group01)
                    // act, assert
                    val error = shouldThrow<DuplicateDataException> {
                        groupCommand.save(group01)
                    }
                    println(error)
                }

                context("メンバーを所有しているグループの作成") {
                    it("未登録の受信者を含んでいる場合はDataNotFoundExceptionをThrowする") {
                        // arrange
                        recipientCommand.save(akira)
                        // aliceは未登録
                        val group = group01.addMember(akira.id).addMember(alice.id)
                        // act, assert
                        shouldThrow<DataNotFoundException> {
                            groupCommand.save(group)
                        }
                        // ロールバックはサービス層で保証するため、ロールバックに関するテストは
                        // サービス層のテストで実施する。
                    }
                }
            }
        }

        describe("deleteByGroupId") {
            context("正常系") {
                it("登録済みのグループIDが入力されたら削除する") {
                    // arrange
                    recipientCommand.save(akira)
                    recipientCommand.save(alice)
                    groupCommand.save(group01.addMember(akira.id).addMember(alice.id))
                    // act
                    groupCommand.deleteByGroupId(group01.id)
                    // assert
                    template.queryForObject(
                        "SELECT COUNT(*) FROM groups WHERE id=CAST(? AS uuid)",
                        Int::class.java,
                        group01.id.value,
                    ) shouldBe 0
                    template.queryForObject(
                        "SELECT COUNT(*) FROM recipients_groups WHERE group_id=CAST(? AS uuid)",
                        Int::class.java,
                        group01.id.value,
                    ) shouldBe 0
                }

                it("未登録のグループIDが入力されたら何もしない") {
                    // act
                    groupCommand.deleteByGroupId(group01.id)
                    // assert
                    template.queryForObject(
                        "SELECT COUNT(*) FROM groups WHERE id=CAST(? AS uuid)",
                        Int::class.java,
                        group01.id.value,
                    ) shouldBe 0
                }
            }
        }

        describe("update") {
            context("正常系") {
                it("登録済みのグループのデータが指定されたらデータを更新する") {
                    // arrange
                    recipientCommand.save(akira)
                    groupCommand.save(group01)
                    // act
                    groupCommand.update(group01.withName(GroupName("group01a")).addMember(akira.id))
                    // assert
                    template.queryForObject(
                        "SELECT name FROM groups WHERE id=CAST(? AS uuid)",
                        String::class.java,
                        group01.id.value,
                    ) shouldBe "group01a"

                    template.queryForObject(
                        "SELECT COUNT(*) FROM recipients_groups WHERE group_id=CAST(? AS uuid)",
                        Int::class.java,
                        group01.id.value,
                    ) shouldBe 1
                }
            }

            context("異常系") {
                it("未登録のグループが指定されたらDataNotFoundExceptionをThrowする") {
                    // act, assert
                    val error = shouldThrow<DataNotFoundException> {
                        groupCommand.update(group01.withName(GroupName("group01a")))
                    }
                    print(error)
                }

                it("未登録の受信者をメンバーにもつグループが入力されたらDataNotFoundExceptionをThrowする") {
                    // arrange
                    groupCommand.save(group01)
                    // act, assert
                    val error = shouldThrow<DataNotFoundException> {
                        groupCommand.update(group01.addMember(akira.id))
                    }
                    println(error)
                }
            }
        }
    }
}

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ApplyExtension(SpringExtension::class)
@Testcontainers
@Import(TestContainerConfiguration::class)
@Transactional
class GroupQueryImplTest(
    private val groupCommand: GroupCommandImpl,
    private val groupQuery: GroupQueryImpl,
    private val template: JdbcTemplate,
) : DescribeSpec() {
    companion object {
        private val group01 = Group.create(GroupName("group01"), GroupDescription.of("test group01"))
        private val group02 = Group.create(GroupName("group02"), GroupDescription.of("test group02"))
        private val group10 = Group.create(GroupName("group10"), GroupDescription.of("test group10"))
    }

    init {
        describe("findAll") {
            context("正常系") {
                it("登録されている全グループのリストを返す") {
                    // arrange
                    groupCommand.save(group01)
                    groupCommand.save(group02)
                    // act
                    val result = groupQuery.findAll()
                    // assert
                    with(result.sortedBy { it.name.value }) {
                        shouldHaveSize(2)
                        get(0) shouldBeEqualToComparingFields group01
                        get(1) shouldBeEqualToComparingFields group02
                    }
                }

                it("登録データがなければ空のリストを返す") {
                    // act
                    val result = groupQuery.findAll()
                    // assert
                    result.shouldBeEmpty()
                }
            }
        }

        describe("findByGroupName") {
            context("正常系") {
                it("検索データが見つかればそれを返す") {
                    // arrange
                    groupCommand.save(group01)
                    // act
                    val result = groupQuery.findByGroupName(group01.name)
                    // assert
                    result.shouldNotBeNull()
                    result shouldBeEqualToComparingFields group01
                }

                it("検索データが見つからなければnullを返す") {
                    // act
                    val result = groupQuery.findByGroupName(group01.name)
                    // assert
                    result.shouldBeNull()
                }
            }
        }

        describe("findByGroupId") {
            context("正常系") {
                it("検索データが見つかればそのデータを返す") {
                    // arrange
                    groupCommand.save(group01)
                    // act
                    val result = groupQuery.findByGroupId(group01.id)
                    // assert
                    result.shouldNotBeNull()
                    result shouldBeEqualToComparingFields group01
                }

                it("検索データが見つからなければnullを返す") {
                    // act
                    val result = groupQuery.findByGroupId(group01.id)
                    // assert
                    result.shouldBeNull()
                }
            }
        }

        describe("fuzzyFindGroupsByGroupName") {
            context("正常系") {
                it("検索データが見つかればそのリストを返す") {
                    // arrange
                    groupCommand.save(group01)
                    groupCommand.save(group02)
                    groupCommand.save(group10)
                    // act
                    val result = groupQuery.fuzzyFindGroupsByGroupName(GroupName("group0"))
                    // assert
                    with(result.sortedBy { it.name.value }) {
                        shouldHaveSize(2)
                        get(0) shouldBeEqualToComparingFields group01
                        get(1) shouldBeEqualToComparingFields group02
                    }
                }

                it("検索データが見つからなければ空のリストを返す") {
                    // act
                    val result = groupQuery.fuzzyFindGroupsByGroupName(GroupName("group0"))
                    // assert
                    result.shouldBeEmpty()
                }
            }
        }
    }
}
