package at.rgstoettner.alexahome.manager

enum class CliError {
    NUMBER_OF_ARGUMENTS,

    UNKNOWN_ARGUMENTS,

    CONFIGURATION_INCOMPLETE,

    GIT_INSTALL_FAILED,

    TLS_CONFIG_FAILED,

    INSTALLATION_FAILED,

    INPUT_REQUIRED,

    ALREADY_INSTALLED,

    NOT_INSTALLED,

    USER_ALREADY_EXISTS,

    UNKNOWN_USER,

    ARGUMENTS_NOT_SUPPORTED,

    NOT_IMPLEMENTED,

    BUILD_FAILED
}