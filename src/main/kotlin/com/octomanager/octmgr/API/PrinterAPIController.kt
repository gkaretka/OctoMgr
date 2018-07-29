package com.octomanager.octmgr.API

import com.octomanager.octmgr.Objects.Printer
import com.octomanager.octmgr.OctmgrApplication
import ninja.sakib.pultusorm.core.PultusORMCondition
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class PrinterAPIController {

    @GetMapping("/printer/api/temperature/{printerId}")
    fun temperature(@PathVariable(name = "printerId") printerId: String) : String {

        val condition: PultusORMCondition = PultusORMCondition.Builder()
                .eq("printerId", printerId)
                .build()

        val printer = OctmgrApplication.pultusORM.find(Printer(), condition)[0] as Printer

        return printer.temperature
    }
}