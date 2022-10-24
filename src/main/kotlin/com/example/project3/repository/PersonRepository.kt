package com.example.project3.repository

interface PersonRepository {

    fun addPerson(name: String, lastName: String) : Boolean
}