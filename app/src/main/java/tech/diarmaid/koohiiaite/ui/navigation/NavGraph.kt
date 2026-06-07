package tech.diarmaid.koohiiaite.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import androidx.navigation.compose.rememberNavController
import tech.diarmaid.koohiiaite.ui.kanjilist.KanjiListScreen
import tech.diarmaid.koohiiaite.ui.kanjidetail.KanjiDetailScreen
import tech.diarmaid.koohiiaite.ui.primitives.PrimitivesScreen
import tech.diarmaid.koohiiaite.ui.importstory.ImportStoryScreen

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = KanjiListRoute,
        modifier = modifier
    ) {
        composable<KanjiListRoute> {
            val shouldRefresh = navController.currentBackStackEntry
                ?.savedStateHandle
                ?.get<Boolean>("storiesImported") ?: false

            KanjiListScreen(
                onKanjiClick = { heisigId, filteredIds ->
                    navController.navigate(
                        KanjiDetailRoute(heisigId = heisigId, filteredIds = filteredIds)
                    )
                },
                onPrimitivesClick = {
                    navController.navigate(PrimitivesRoute)
                },
                onImportStoryClick = {
                    navController.navigate(ImportStoryRoute)
                },
                shouldRefresh = shouldRefresh
            )
        }

        composable<KanjiDetailRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<KanjiDetailRoute>()
            KanjiDetailScreen(
                heisigId = args.heisigId,
                filteredIds = args.filteredIds,
                initialTabIndex = args.initialTabIndex,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToKanji = { heisigId ->
                    navController.navigate(
                        KanjiDetailRoute(heisigId = heisigId, filteredIds = listOf(heisigId))
                    )
                }
            )
        }

        composable<PrimitivesRoute> {
            PrimitivesScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<ImportStoryRoute> {
            ImportStoryScreen(
                onBack = { navController.popBackStack() },
                onImportComplete = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("storiesImported", true)
                    navController.popBackStack()
                }
            )
        }
    }
}
