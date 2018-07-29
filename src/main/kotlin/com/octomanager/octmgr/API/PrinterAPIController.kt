package com.octomanager.octmgr.API

import com.google.gson.JsonObject
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

    @GetMapping("/printer/api/connected_status/{printerId}")
    fun connectedStatus(@PathVariable(name = "printerId") printerId: String) : String {

        val condition: PultusORMCondition = PultusORMCondition.Builder()
                .eq("printerId", printerId)
                .build()

        val printer = OctmgrApplication.pultusORM.find(Printer(), condition)[0] as Printer

        val jsonStatus = JsonObject()
        var cssClass = ""

        when (printer.connectedStatus.toString()) {
            "0" -> cssClass = "badge-danger"
            "1" -> cssClass = "badge-success"
        }

        jsonStatus.addProperty("status", cssClass)

        return jsonStatus.toString()
    }

    @GetMapping("/printer/api/serial_status/{printerId}")
    fun serialStatus(@PathVariable(name = "printerId") printerId: String) : String {

        val condition: PultusORMCondition = PultusORMCondition.Builder()
                .eq("printerId", printerId)
                .build()

        val printer = OctmgrApplication.pultusORM.find(Printer(), condition)[0] as Printer

        val jsonStatus = JsonObject()
        jsonStatus.addProperty("status", printer.serialConnectionStatus)

        return jsonStatus.toString()
    }

    @GetMapping("/printer/api/printing_status/{printerId}")
    fun printingStatus(@PathVariable(name = "printerId") printerId: String) : String {
        val condition: PultusORMCondition = PultusORMCondition.Builder()
                .eq("printerId", printerId)
                .build()

        val printer = OctmgrApplication.pultusORM.find(Printer(), condition)[0] as Printer

        val jsonStatus = JsonObject()
        var cssClass = ""

        when (printer.printingStatus.toString()) {
            "0" -> cssClass = "badge-danger"
            "1" -> cssClass = "badge-success"
            "2" -> cssClass = "badge-warning"
        }

        jsonStatus.addProperty("status", cssClass)

        return jsonStatus.toString()
    }
}