package org.example.manager.infrastructure.repository

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.example.manager.domain.errors.DataNotFoundException
import org.example.manager.domain.errors.DuplicateDataException
import org.example.manager.domain.model.Recipient
import org.example.manager.domain.model.RecipientEmailAddress
import org.example.manager.domain.model.RecipientName
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.postgresql.PostgreSQLContainer

@TestConfiguration(proxyBeanMethods = false)
class TestContainerConfiguration {
    @Bean
    @ServiceConnection
    fun postgresContainer(): PostgreSQLContainer {
        return PostgreSQLContainer("postgres:18.3").apply {
            withUsername("postgres")
            withPassword("postgres")
            withDatabaseName("testdb")
        }
    }
}

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
