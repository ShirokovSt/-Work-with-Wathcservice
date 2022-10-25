package com.example.project3.repository

import com.example.project3.model.Person
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import javax.annotation.PostConstruct

@Repository
class PersonRepositoryImpl(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
    private val jdbcTemplateForCreateTable: JdbcTemplate
) : PersonRepository {

    @PostConstruct
    fun init() {
        jdbcTemplateForCreateTable.execute(
            "create table if not exists person(" +
                    "id serial primary key, name varchar(100), last_name varchar(100))"
        )
    }

    override fun addPerson(name: String, lastName: String): Boolean {


        if (jdbcTemplate.query(
                "select * from person where name = :name and last_name = :lastName",
                mapOf(
                    "name" to name,
                    "lastName" to lastName
                ),
                ROW_MAPPER
            ).firstOrNull() != null
        )
            return false

        jdbcTemplate.update(
            "insert into person (name, last_name) values (:name, :lastName)",
            MapSqlParameterSource(
                mapOf(
                    "name" to name,
                    "lastName" to lastName
                )
            ),
        )
        return true
    }

    private companion object {
        val ROW_MAPPER = RowMapper<Person> { rs, _ ->
            Person(
                id = rs.getInt("id"),
                name = rs.getString("name"),
                lastName = rs.getString("last_name")
            )
        }
    }
}