package com.lectra.jpa

import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.MSSQLServerContainer
import org.testcontainers.junit.jupiter.Testcontainers

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
@ContextConfiguration(initializers = [MssqlContextInitializer::class])
class LegoModelJpaTest() {

    @Test
    fun modelReferencingBricks() {
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