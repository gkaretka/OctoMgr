package com.octomanager.octmgr.Views

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.octomanager.octmgr.Objects.Printer
import com.octomanager.octmgr.OctmgrApplication
import ninja.sakib.pultusorm.core.PultusORMCondition
import ninja.sakib.pultusorm.core.PultusORMUpdater
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class PrinterController {

    @PostMapping("/printer/add")
    fun add(@RequestParam(name = "name", required = true) name: String,
            @RequestParam(name = "ip", required = true) ip: String,
            @RequestParam(name = "version", required = false) version: String,
            @RequestParam(name = "api_key", required = false) apiKey: String,
            model: Model
    ): String {
        model.addAttribute("url", "/printer/add")

        val printer = Printer()
        printer.name = name
        printer.ip = ip
        printer.version = version
        printer.apiKey = apiKey
        printer.printingStatus = 0
        printer.connectedStatus = 0

        printer.updateVersionAndConnectivity()
        printer.updateSerialConnection()
        printer.updatePrintingStatus()

        OctmgrApplication.pultusORM.save(printer)

        return "redirect:/printer/list"
    }

    @GetMapping("/printer/delete/{printerId}")
    fun delete(@PathVariable(name = "printerId", required = true) printerId: String,
               model: Model) : String {

        val condition: PultusORMCondition = PultusORMCondition.Builder()
                .eq("printerId", printerId)
                .build()

        OctmgrApplication.pultusORM.delete(Printer(), condition)

        return "redirect:/printer/list"
    }

    @PostMapping("/printer/edit/{printerId}")
    fun edit(@PathVariable(name = "printerId", required = true) printerId: String,
             @RequestParam(name = "name", required = true) name: String,
             @RequestParam(name = "ip", required = true) ip: String,
             @RequestParam(name = "version", required = false) version: String,
             @RequestParam(name = "api_key", required = false) apiKey: String,
             model: Model) : String {

        val condition: PultusORMCondition = PultusORMCondition.Builder()
                .eq("printerId", printerId)
                .build()

        val updater: PultusORMUpdater = PultusORMUpdater.Builder()
                .condition(condition)
                .set("name", name)
                .set("ip", ip)
                .set("apiKey", apiKey)
                .set("version", version)
                .build()

        OctmgrApplication.pultusORM.update(Printer(), updater)

        return "redirect:/printer/list"
    }

    @GetMapping("/printer/edit/{printerId}")
    fun editShow(
            @PathVariable(name="printerId", required = true) printerId: String,
            model: Model): String {

        val condition: PultusORMCondition = PultusORMCondition.Builder()
                .eq("printerId", printerId)
                .build()

        val printer: Printer = OctmgrApplication.pultusORM.find(Printer(), condition)[0] as Printer

        model.addAttribute("printer", printer)
        model.addAttribute("url", "/printer/add")

        return "edit_printer"
    }

    @GetMapping("/printer/add")
    fun addShow(model: Model): String {
        model.addAttribute("url", "/printer/add")
        return "add_printer"
    }

    @GetMapping("/printer/show/{printerId}")
    fun show(@PathVariable(name = "printerId") id: Int): String {
        return "redirect:/printer/list"
    }

    @GetMapping("/printer/list")
    fun list(model: Model): String {
        model.addAttribute("url", "/printer/list")

        val printers: MutableList<Printer> = OctmgrApplication.pultusORM.find(Printer()) as MutableList<Printer>
        for ((count, printer) in printers.withIndex()) {
            val jsonParser = JsonParser()
            val jsonTemp = jsonParser.parse(printer.temperature).asJsonObject

            printers[count].actualNozzleTemperature = (jsonTemp.get("tool0").asJsonObject).get("actual").asString
            printers[count].targetNozzleTemperature = (jsonTemp.get("tool0").asJsonObject).get("target").asString
            printers[count].actualBedTemperature = (jsonTemp.get("bed").asJsonObject).get("actual").asString
            printers[count].targetBedTemperature = (jsonTemp.get("bed").asJsonObject).get("target").asString
        }

        model.addAttribute("printers", printers)

        return "list_printer"
    }
}