package com.lectra.jdbc

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.MSSQLServerContainer
import org.testcontainers.junit.jupiter.Testcontainers

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
@ContextConfiguration(initializers = [MssqlContextInitializer::class])
class LegoModelTest(@Autowired val legoModelRepository: LegoModelRepository, @Autowired val brickRepository: BrickRepository) {

    @Test
    fun modelReferencingBricks() {
        val thin2x2 = Brick("2x2 - thin")
        val thin2x4 = Brick("2x4 - thin")
        val normal2x2 = Brick("2x2 - normal")
        val bricks: List<Brick> = brickRepository.saveAll(listOf(thin2x2, thin2x4, normal2x2)).toList()

        val womenOfNasa = LegoModel("Women of NASA")
        womenOfNasa.add(bricks[0], 6)
        womenOfNasa.add(bricks[1], 3)
        womenOfNasa.add(bricks[2], 4)

        val legoModel = legoModelRepository.save(womenOfNasa)

        assertThat(brickRepository.findAll().toList().size).isEqualTo(3)

        assertThat(legoModel.id).isNotNull()

        val expected = listOf(BrickContentItem(bricks[0].Id, 6), BrickContentItem(bricks[1].Id, 3), BrickContentItem(bricks[2].Id, 4))
        assertThat(legoModel.brickContent).containsExactlyInAnyOrderElementsOf(expected)
    }

}

class MssqlContextInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    override fun initialize(context: ConfigurableApplicationContext) {
        mssqlContainer.withConnectTimeoutSeconds(240).withStartupTimeoutSeconds(240).start()
        TestPropertyValues.of(
            "spring.datasource.driver-class-name=${mssqlContainer.driverClassName}",
            "spring.datasource.url=${mssqlContainer.jdbcUrl}",
            "spring.datasource.username=${mssqlContainer.username}",
            "spring.datasource.password=${mssqlContainer.password}"
        )
            .applyTo(context.environment)
    }

    companion object {
        val mssqlContainer: KMssqlContainer = KMssqlContainer("mcr.microsoft.com/mssql/server:2017-latest").withInitScript("schema.sql")
    }


    class KMssqlContainer(imageName: String?) : MSSQLServerContainer<KMssqlContainer>(imageName)

}