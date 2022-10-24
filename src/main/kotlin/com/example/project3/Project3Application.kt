package com.example.project3

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class Project3Application

fun main(args: Array<String>) {
	runApplication<Project3Application>(*args)
}
