package org.example.manager.infrastructure.repository

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.example.config.TestContainerConfiguration
import org.example.manager.domain.errors.DataNotFoundException
import org.example.manager.domain.errors.DuplicateDataException
import org.example.manager.domain.model.Recipient
import org.example.manager.domain.model.RecipientEmailAddress
import org.example.manager.domain.model.RecipientName
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.junit.jupiter.Testcontainers


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ApplyExtension(SpringExtension::class)
@Testcontainers
@Import(TestContainerConfiguration::class)
@Transactional
class RecipientCommandImplTest(
    private val command: RecipientCommandImpl,
    private val template: JdbcTemplate
) : DescribeSpec() {
    companion object {
        private val akira = Recipient.create(RecipientName("akira"), RecipientEmailAddress.of("akira@example.com"))
    }

    init {
        describe("save") {
            context("正常系") {
                it("未登録のメールアドレスを持つ受信者が入力されたら保存する") {
                    // act
                    command.save(akira)
                    // assert
                    val name = template.queryForObject(
                        "SELECT name FROM recipients WHERE id=CAST(? AS uuid)", String::class.java, akira.id.value
                    )
                    with(name) {
                        shouldNotBeNull()
                        shouldBe(akira.name.value)
                    }
                }
            }

            context("異常系") {
                it("登録済みのメールアドレスを持つ受信者が入力されたらDuplicateDataExceptionをスローする") {
                    // arrange
                    command.save(akira)
                    // act, assert
                    shouldThrow<DuplicateDataException> {
                        command.save(akira)
                    }
                }
            }
        }

        describe("update") {
            context("正常系") {
                it("登録済みの受信者のデータが入力されたらデータを更新する") {
                    // arrange
                    command.save(akira)
                    val newAddress = "AKIRA@example.com"
                    // act
                    command.update(
                        akira.withEmail(RecipientEmailAddress.of(newAddress))
                    )
                    // assert
                    val mail = template.queryForObject(
                        "SELECT email FROM recipients WHERE id=CAST(? AS uuid)",
                        String::class.java,
                        akira.id.value
                    )
                    with(mail) {
                        shouldNotBeNull()
                        shouldBe(newAddress)
                    }
                }
            }

            context("異常系") {
                it("未登録の受信者のデータが入力されたらDataNotFoundExceptionをThrowする") {
                    // act, assert
                    shouldThrow<DataNotFoundException> {
                        command.update(akira)
                    }
                }
            }
        }

        describe("delete") {
            context("正常系") {
                it("登録済みの受信者IDが入力されたら削除する") {
                    // arrange
                    command.save(akira)
                    // act
                    command.deleteByRecipientId(akira.id)
                    // assert
                    val count = template.queryForObject(
                        "SELECT count(*) FROM recipients WHERE id=CAST(? AS uuid)",
                        Int::class.java,
                        akira.id.value
                    )
                    count shouldBe 0
                }

                it("未登録の受信者IDが入力されたら何もしない") {
                    // act, assert
                    command.deleteByRecipientId(akira.id)
                    // assert
                    val count = template.queryForObject(
                        "SELECT count(*) FROM recipients WHERE id=CAST(? AS uuid)",
                        Int::class.java,
                        akira.id.value
                    )
                    count shouldBe 0
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
class RecipientQueryImplTest(
    private val command: RecipientCommandImpl,
    private val query: RecipientQueryImpl,
    private val template: JdbcTemplate,
) : DescribeSpec() {
    companion object {
        private val akira = Recipient.create(RecipientName("akira"), RecipientEmailAddress.of("akira@example.com"))
        private val alice = Recipient.create(RecipientName("alice"), RecipientEmailAddress.of("alice@example.com"))
        private val akari = Recipient.create(RecipientName("akari"), RecipientEmailAddress.of("akari@example.com"))
    }

    init {
        describe("findAll") {
            context("正常系") {
                it("全受信者データのリストを返す") {
                    // arrange
                    command.save(akira)
                    command.save(alice)
                    // act
                    val results = query.findAll()
                    // assert
                    with(results) {
                        shouldHaveSize(2)
                    }
                }

                it("データが未登録ならからのリストを返す") {
                    // act
                    val result = query.findAll()
                    // assert
                    result.shouldBeEmpty()
                }
            }
        }

        describe("findByRecipientId") {
            context("正常系") {
                it("登録済みの受信者IDが入力された場合はデータを返す") {
                    // arrange
                    command.save(akira)
                    // act
                    val result = query.findByRecipientId(akira.id)
                    // assert
                    with(result) {
                        shouldNotBeNull()
                        name.value.shouldBe("akira")
                    }
                }

                it("未登録の受信者IDが入力された場合はnullを返す") {
                    // act
                    val result = query.findByRecipientId(akira.id)
                    // assert
                    result.shouldBeNull()
                }
            }
        }

        describe("findByEmail") {
            context("正常系") {
                it("登録済みのEmailが入力された場合はデータを返す") {
                    // arrange
                    command.save(akira)
                    // act
                    val result = query.findByEmail(akira.email)
                    // assert
                    with(result) {
                        shouldNotBeNull()
                        id.shouldBe(akira.id)
                    }
                }

                it("未登録のEmailが入力された場合はnullを変えす") {
                    // act
                    val result = query.findByEmail(akira.email)
                    // assert
                    result.shouldBeNull()
                }
            }
        }

        describe("findRecipientsByRecipientIds") {
            context("正常系") {
                it("登録済みのIdのみが渡された場合は対象のデータを全て取得する") {
                    // arrange
                    command.save(akira)
                    command.save(alice)
                    command.save(akari)
                    // act
                    val result = query.findRecipientsByRecipientIds(setOf(akira.id, alice.id))
                    // assert
                    result.shouldHaveSize(2)
                }

                it("一部登録済みのIdが渡された場合は登録済みのデータを取得する") {
                    // arrange
                    command.save(akira)
                    // act
                    val result = query.findRecipientsByRecipientIds(setOf(akira.id, alice.id))
                    // assert
                    with(result) {
                        shouldHaveSize(1)
                        first().id.shouldBe(akira.id)
                    }
                }

                it("未登録のIdのみが渡された場合はからのリストを返す") {
                    // act
                    val result = query.findRecipientsByRecipientIds(setOf(akira.id, alice.id))
                    // assert
                    result.shouldBeEmpty()
                }
            }
        }
    }
}