package com.octomanager.octmgr.App


import com.octomanager.octmgr.Objects.Printer
import com.octomanager.octmgr.OctmgrApplication
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class StatusUpdater {

    @Scheduled(fixedDelay = 10000)
    fun updatePrinterStatus() {
        val printers: MutableList<Any> = OctmgrApplication.pultusORM.find(Printer())

        for (printer in printers) run {
            val castedPrinter = printer as Printer
            println(castedPrinter.name + " " + castedPrinter.ip + " " + castedPrinter.connectedStatus)
            println("------------------------------ INFO ------------------------------")
            printer.updateVersionAndConnectivity()
            printer.updatePrintingStatus()
        }
    }
}