package at.rgstoettner.alexahome.manager.controller

import com.google.gson.Gson
import java.io.File
import java.util.concurrent.TimeUnit

abstract class AbstractController {
    protected var log = ""
        private set
    protected val gson = Gson()
    protected val isInstalled = home.tls.private.file("ca.key").exists()

    protected object home : Directory("AlexaHome") {
        object lambda : Directory("AlexaHome/lambda") {
            object tls : Directory("AlexaHome/lambda/tls") {
                object users : Directory("AlexaHome/lambda/tls/users") {
                }
            }
        }

        object tls : Directory("AlexaHome/tls") {
            object private : Directory("AlexaHome/tls/private")
            object certs : Directory("AlexaHome/tls/certs")
            object server : Directory("AlexaHome/tls/server")
            object client : Directory("AlexaHome/tls/client")
        }

        object executor : Directory("AlexaHome/executor") {
            object srcMainRes : Directory("AlexaHome/executor/src/main/resources") {
                object tls : Directory("AlexaHome/executor/src/main/resources/tls") {
                }
            }

            object buildLibs : Directory("AlexaHome/executor/build/libs")
        }

        object skill : Directory("AlexaHome/skill") {
            object srcMainRes : Directory("AlexaHome/skill/src/main/resources") {
                object tls : Directory("AlexaHome/skill/src/main/resources/tls") {
                }
            }

            object buildLibs : Directory("AlexaHome/skill/build/libs")

        }

        object manager : Directory("AlexaHome/manager") {
            object srcMainRes : Directory("AlexaHome/manager/src/main/resources") {
                object tls : Directory("AlexaHome/manager/src/main/resources/tls") {
                }
            }

            object buildLibs : Directory("AlexaHome/manager/build/libs")
        }
    }

    protected object updateTemp : Directory("update_temp_dir") {
        object lambda : Directory("update_temp_dir/lambda") {
            object tls : Directory("update_temp_dir/lambda/tls") {
            }
        }

        object general : Directory("update_temp_dir/general") {
            object tls : Directory("update_temp_dir/general/tls") {
            }
        }
    }

    protected object userTemp : Directory("user_temp_dir")
    protected object root : Directory(".")

    protected fun String.runCommandInside(inside: Directory, output: Boolean = true) {
        val str = "cd $inside && $this"
        str.runCommand(output)

    }


    fun String.runCommand(output: Boolean = true) {
        val parts = this.split("//s".toRegex())
        val builder = ProcessBuilder("/bin/bash", "-c", *parts.toTypedArray())
        builder.redirectErrorStream(true)
        val proc = builder.start()
        proc.waitFor(60, TimeUnit.MINUTES)
        if (output) {
            val text = proc.inputStream.bufferedReader().readText()
            log = log.plus(text)
            println(text)
        }
    }

    protected fun logContainsError(): Boolean {
        return log.contains("exception", true)
                || log.contains("error", true)
                || log.contains("failed", true)
    }


    open class Directory(path: String) : File(path) {

        fun file(name: String): File {
            return File(path.plus("/").plus(name))
        }

        fun dir(name: String): Directory {
            return Directory(path.plus("/").plus(name))
        }
    }

    fun File.override(file: File) {
        if (file.isDirectory) {
            file.deleteRecursively()
        }
        this.copyTo(file, overwrite = true)
    }

    fun <T> File.asClass(clazz: Class<T>): T {
        return gson.fromJson(this.bufferedReader(), clazz)
    }

    fun File.asString(): String {
        return this.bufferedReader().readText()
    }
}

