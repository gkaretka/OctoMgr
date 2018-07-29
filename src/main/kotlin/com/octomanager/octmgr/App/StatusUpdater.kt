package com.octomanager.octmgr.App


import com.octomanager.octmgr.Objects.Printer
import com.octomanager.octmgr.OctmgrApplication
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class StatusUpdater {

    @Scheduled(fixedDelay = 5000)
    fun updatePrinterStatus() {
        val printers: MutableList<Any> = OctmgrApplication.pultusORM.find(Printer())

        for (printer in printers) run {
            printer as Printer

            printer.updateVersionAndConnectivity()
            printer.updatePrintingStatus()
            printer.updateSerialConnection()
        }
    }
}