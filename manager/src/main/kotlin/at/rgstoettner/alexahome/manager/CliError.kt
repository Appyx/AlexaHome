package at.rgstoettner.alexahome.manager

enum class CliError {
    NUMBER_OF_ARGUMENTS,
    UNKNOWN_ARGUMENTS,
    CONFIGURATION_INCOMPLETE,

    GIT_INSTALL_FAILED,

    TLS_CONFIG_FAILED
}