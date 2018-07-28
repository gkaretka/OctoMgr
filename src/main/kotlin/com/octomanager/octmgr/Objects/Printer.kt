package com.octomanager.octmgr.Objects

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpGet
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.octomanager.octmgr.OctmgrApplication
import ninja.sakib.pultusorm.annotations.AutoIncrement
import ninja.sakib.pultusorm.annotations.PrimaryKey
import ninja.sakib.pultusorm.core.PultusORMCondition
import ninja.sakib.pultusorm.core.PultusORMUpdater

class Printer {

    @PrimaryKey
    @AutoIncrement
    var printerId: Int = 0
    var name: String? = null
    var ip: String? = null
    var version: String? = null
    var apiKey: String? = null
    var server: String? = null
    var printingStatus: Int = 0
    var connectedStatus: Int = 0


    data class Version(var api: String = "",
                       var server: String = "") {

        class Deserializer : ResponseDeserializable<Version> {
            override fun deserialize(content: String) = Gson().fromJson(content, Version::class.java)
        }
    }

    data class PrintingStatus(var state: com.google.gson.JsonObject) {
        class Deserializer : ResponseDeserializable<PrintingStatus> {
            override fun deserialize(content: String) = Gson().fromJson(content, PrintingStatus::class.java)
        }
    }

    fun updatePrintingStatus() {
        val url: String = "http://" + this.ip + "/api/printer" + "?apikey=" + this.apiKey
        url.httpGet().timeout(5000).responseObject(Printer.PrintingStatus.Deserializer()) { request, response, result ->
            val (printingStatus, err) = result

            val condition: PultusORMCondition = PultusORMCondition.Builder()
                    .eq("printerId", this.printerId)
                    .build()

            val updater: PultusORMUpdater.Builder = PultusORMUpdater.Builder()
                    .condition(condition)

            if (err != null) {
                print(err.message)
                updater.set("printingStatus", 0)
            } else if (printingStatus != null) {
                val flags: JsonObject = printingStatus.state.get("flags").asJsonObject
                if (flags.get("printing").asBoolean) {
                    updater.set("printingStatus", 1)
                } else if (flags.get("ready").asBoolean) {
                    updater.set("printingStatus", 2)
                }
            }
            OctmgrApplication.pultusORM.update(Printer(), updater.build())
        }
    }

    fun updateVersionAndConnectivity() {
        val url: String = "http://" + this.ip + "/api/version" + "?apikey=" + this.apiKey
        url.httpGet().timeout(5000).responseObject(Printer.Version.Deserializer()) { request, response, result ->
            val (version, err) = result

            val condition: PultusORMCondition = PultusORMCondition.Builder()
                    .eq("printerId", this.printerId)
                    .build()

            val updater: PultusORMUpdater.Builder = PultusORMUpdater.Builder()
                    .condition(condition)

            if (err != null) {
                print(err.message)
                updater.set("version", "not loaded")
                updater.set("server", "not loaded")
                updater.set("connectedStatus", 0)
            } else if (version != null) {
                updater.set("version", version.api)
                updater.set("server", version.server)
                updater.set("connectedStatus", 1)
            }

            OctmgrApplication.pultusORM.update(Printer(), updater.build())
        }
    }
}