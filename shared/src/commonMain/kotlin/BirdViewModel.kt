


import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import model.BirdImage
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow



data class BirdsUiState(
    val images: List<BirdImage> = emptyList(),
    val selectedCategory:String?=null
){
    val categories = images.map { it.category }.toSet()
    val selectedImages = images.filter { it.category == selectedCategory }
}

class BirdViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<BirdsUiState>(BirdsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        updateBirdState()
    }

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

    fun selectCategory(category:String){
        _uiState.update {
            it.copy(selectedCategory=category)
        }
    }

    private fun updateBirdState() {
        viewModelScope.launch {
            val images = getImages()
            _uiState.update {
                it.copy(images = getImages())
            }
        }
    }

    private suspend fun getImages(): List<BirdImage> {

        val images = httpClient
            .get("https://sebastianaigner.github.io/demo-image-api/pictures.json")
            .body<List<BirdImage>>()
        return images


    }

    override fun onCleared() {
        httpClient.close()
        super.onCleared()
    }
}