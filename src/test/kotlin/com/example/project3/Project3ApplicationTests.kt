package com.example.project3

import com.example.project3.repository.PersonRepository
import com.example.project3.service.PersonWatchService
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.test.context.junit4.SpringRunner
import java.io.File
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.StandardCopyOption

@RunWith(SpringRunner::class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class Project3ApplicationTests {

    @Autowired
    private lateinit var personRepository: PersonRepository

    @Autowired
    private lateinit var jdbcTemplate: NamedParameterJdbcTemplate

    @Autowired
    private lateinit var personService: PersonWatchService

    class ThreadTestService(private val personsService: PersonWatchService) : Runnable {

        override fun run() {
            try {
                personsService.personSearch()
            } catch (exc: InterruptedException) {
                println(Thread.currentThread().name + " has been interrupted");
            }
        }
    }

    companion object {
        @JvmStatic
        @BeforeClass
        fun init() {
            val writer = FileWriter(System.getProperty("user.dir") + "/tmp.txt", false);
            writer.write(
                """ [
		  {
			"name": "alex555",
			"lastName": "pushkin555"
		  },
		  {
			"name": "leva555",
			"lastName": "tolstoi555"
		  }
		]""".trimIndent()
            )
            writer.close()
        }

        @JvmStatic
        @AfterClass
        fun destroyAllTestFiles() {
            var file = File(System.getProperty("user.dir") + "/tmp.txt")
            file.delete()
            file = File(System.getProperty("user.dir") + "/persons/tmp.txt")
            file.delete()
        }
    }

    @Test
    fun `1 - testWatchService`() {
        val thr = Thread(ThreadTestService(personService))
        thr.start()
        Files.copy(
            File(System.getProperty("user.dir") + "/tmp.txt").toPath(),
            File(System.getProperty("user.dir") + "/persons/tmp.txt").toPath(),
            StandardCopyOption.REPLACE_EXISTING
        )
        thr.join(300)
        thr.interrupt()
        assertFalse(personRepository.addPerson("alex555", "pushkin555"))
        assertFalse(personRepository.addPerson("leva555", "tolstoi555"))
        destroyTestPerson("alex555", "pushkin555")
        destroyTestPerson("leva555", "tolstoi555")
    }

    @Test
    fun `2 - testAddToRepository`() {
        assertTrue(personRepository.addPerson("Zahar555", "Dostoevskii555"))
    }

    @Test
    fun `3 - testAddDuplicateToRepository`() {
        assertFalse(personRepository.addPerson("Zahar555", "Dostoevskii555"))
        destroyTestPerson("Zahar555", "Dostoevskii555")
    }

    fun destroyTestPerson(name: String, lastName: String) {
        jdbcTemplate.update(
            "delete from person where name = :name and last_name = :lastName",
            mapOf(
                "name" to name,
                "lastName" to lastName
            )
        )
    }

}
