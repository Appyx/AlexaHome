package at.rgstoettner.alexahome.manager.controller

import at.rgstoettner.alexahome.manager.CliError
import at.rgstoettner.alexahome.manager.handleFatalError

class CliParser {

    fun install() {
        InstallController().install()
    }

    fun add(args: List<String>) {
        when {
            args.size == 2 -> {
                when {
                    args[1] == "device" -> AddController().addDevice()
                    args[1] == "scence" -> AddController().addScene()
                    else -> handleFatalError(CliError.UNKNOWN_ARGUMENTS)
                }
            }
            else -> handleFatalError(CliError.NUMBER_OF_ARGUMENTS)
        }
    }

    fun list(args: List<String>) {
        when {
            args.size == 1 -> ListController().listAll()
            args.size == 2 -> {
                when {
                    args[1] == "devices" -> ListController().listDevices()
                    args[1] == "scenes" -> ListController().listScenes()
                    else -> handleFatalError(CliError.UNKNOWN_ARGUMENTS)
                }
            }
            else -> handleFatalError(CliError.NUMBER_OF_ARGUMENTS)
        }
    }

    fun remove(args: List<String>) {
        when {
            args.size == 3 -> {
                when {
                    args[1] == "device" -> RemoveController().removeDevice(args[2])
                    args[1] == "scene" -> RemoveController().removeScene(args[2])
                    else -> handleFatalError(CliError.UNKNOWN_ARGUMENTS)
                }
            }
            else -> handleFatalError(CliError.NUMBER_OF_ARGUMENTS)
        }
    }

    fun edit(args: List<String>) {
        when {
            args.size == 3 -> {
                when {
                    args[1] == "device" -> EditController().editDevice(args[2])
                    args[1] == "scene" -> EditController().editScene(args[2])
                    else -> handleFatalError(CliError.UNKNOWN_ARGUMENTS)
                }
            }
            else -> handleFatalError(CliError.NUMBER_OF_ARGUMENTS)
        }
    }

    fun wipe(args: List<String>) {
        when {
            args.size == 1 -> {
                RemoveController().wipeAll()
            }
            else -> handleFatalError(CliError.NUMBER_OF_ARGUMENTS)
        }
    }
}