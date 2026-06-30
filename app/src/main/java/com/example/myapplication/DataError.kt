package com.example.myapplication

sealed class DataError(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {
    class Auth(cause: Throwable, message: String? = null) : DataError(message ?: "Authentication error", cause)
    class Network(cause: Throwable) : DataError("Network error", cause)
    class Server(cause: Throwable, message: String? = null) : DataError(message ?: "Server error", cause)
    class NotFound(message: String = "Data not found") : DataError(message)
    class Validation(message: String) : DataError(message)
    class Unknown(cause: Throwable) : DataError("Unknown error", cause)
}
