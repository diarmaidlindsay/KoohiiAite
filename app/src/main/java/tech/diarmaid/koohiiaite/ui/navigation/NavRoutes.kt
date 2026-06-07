package tech.diarmaid.koohiiaite.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object KanjiListRoute

@Serializable
data class KanjiDetailRoute(
    val heisigId: Int,
    val filteredIds: List<Int>,
    val initialTabIndex: Int = 0
)

@Serializable
object PrimitivesRoute

@Serializable
object ImportStoryRoute
