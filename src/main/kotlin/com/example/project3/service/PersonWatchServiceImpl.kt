package com.example.project3.service

import com.example.project3.dto.PersonDto
import com.example.project3.repository.PersonRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory.getLogger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.*
import java.util.*

@Service
class PersonWatchServiceImpl(
    private val watchService: WatchService = FileSystems.getDefault().newWatchService(),
    private val path: Path = Paths.get(System.getProperty("user.dir") + "/persons"),
    private val objectMapper: ObjectMapper,
    private val personRepository: PersonRepository,

    ) : PersonWatchService {

    companion object {
        private val logger = getLogger(PersonWatchService::class.java)
    }

    @Scheduled(fixedDelay = Long.MAX_VALUE)
    override fun personSearch() {
        path.register(
            watchService,
            StandardWatchEventKinds.ENTRY_CREATE
        )
        var key: WatchKey
        while (watchService.take().also { key = it } != null) {
            for (event in key.pollEvents()) {
                val listPersons: List<PersonDto> =
                    objectMapper.readValue(
                        File(event.context().toString()),
                        Array<PersonDto>::class.java
                    ).toList()
                for (person in listPersons)
                    if (!personRepository.addPerson(person.name, person.lastName))
                        logger.info("ignoring Person{ ${person.name}, ${person.lastName}}")
            }
            key.reset()
        }
    }
}