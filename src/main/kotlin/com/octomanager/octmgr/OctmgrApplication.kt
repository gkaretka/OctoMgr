package com.octomanager.octmgr

import ninja.sakib.pultusorm.core.PultusORM
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling


@EnableScheduling
@SpringBootApplication()
class OctmgrApplication {

    companion object {
        val pultusORM: PultusORM = PultusORM("test.db", ".")
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(OctmgrApplication::class.java, *args)
}
