package com.aiselp.autox.devapi

import android.util.Log
import com.google.gson.Gson
import com.stardust.app.GlobalAppContext
import com.stardust.autojs.project.ProjectConfig
import com.stardust.autojs.servicecomponents.EngineController
import com.stardust.io.Zip
import com.stardust.pio.PFiles
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.plugins.mutableOriginConnectionPoint
import io.ktor.server.request.receiveStream
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.util.pipeline.PipelineContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class HttpApi(val context: PipelineContext<Unit, ApplicationCall>) {
    private val gson = Gson()


    suspend fun handle() {
        try {
            when (context.call.request.queryParameters["type"]) {
                "getip" -> respondIp(context.call)
                "runProject" -> runProject(context.call)
                "saveProject" -> saveProject(context.call)
            }
            context.call.respondText(
                "unknown type",
                ContentType.Text.Plain, HttpStatusCode.BadRequest
            )
        } catch (e: Exception) {
            Log.e(TAG, e.stackTraceToString())
            context.call.respond(HttpStatusCode.InternalServerError)
            return
        }
    }

    private suspend fun runProject(call: ApplicationCall) {
        val dirName = call.parameters["dirName"]
        withContext(Dispatchers.IO) {
            val cacheDir = GlobalAppContext.get().cacheDir
            val project = File(cacheDir, dirName!!)
            PFiles.deleteRecursively(project)
            val inputStream = call.receiveStream()
            Zip.unzip(inputStream, cacheDir)
            EngineController.launchProject(ProjectConfig.fromProject(project)!!)
        }
        call.respond(HttpStatusCode.OK)
    }

    private suspend fun saveProject(call: ApplicationCall) {
        val dirName = call.parameters["dirName"]!!
        val saveName = call.parameters["saveName"]!!
        withContext(Dispatchers.IO) {
            val cacheDir = GlobalAppContext.get().cacheDir
            val project = File(cacheDir, dirName)
            PFiles.deleteRecursively(project)
            Zip.unzip(call.receiveStream(), cacheDir)
            val scriptDirPath = org.autojs.autojs.Pref.getScriptDirPath()
            val saveDir = File(scriptDirPath, saveName)
            PFiles.deleteRecursively(saveDir)
            PFiles.copyDirectory(project.toPath(),saveDir.toPath())
            Log.i(TAG,"save project to $saveDir")
        }
        call.respond(HttpStatusCode.OK)
    }

    private suspend fun respondIp(call: ApplicationCall) {
        val data = gson.toJson(object {
            val remoteHost = context.call.mutableOriginConnectionPoint.remoteHost
        })
        call.respondText(data, ContentType.Text.Plain, HttpStatusCode.OK)
    }

    companion object {
        private const val TAG = "HttpApi"
        private const val PATH = "/api/v1"
        fun Route.installRoute() {
            post(PATH) {
                val connectionPoint = this.call.mutableOriginConnectionPoint
                Log.i(TAG, connectionPoint.remoteHost + ":" + connectionPoint.port)
                HttpApi(this).handle()
            }
        }
    }
}