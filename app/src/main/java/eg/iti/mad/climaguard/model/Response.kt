package eg.iti.mad.climaguard.model

sealed class Response {

    data object Loading: Response()
    data class Success(val data: CurrentResponse): Response()
    data class Failure(val error: Throwable): Response()

}