package com.geniusdevelop.myscreens.ui.theme.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.geniusdevelop.myscreens.app.AppBar
import com.geniusdevelop.myscreens.app.pages.*
import com.geniusdevelop.myscreens.app.pages.home.HomePage
import com.geniusdevelop.myscreens.app.pages.login.LoginPage
import com.geniusdevelop.myscreens.app.pages.splash.SplashScreen
import com.geniusdevelop.myscreens.app.pages.player.PlayerPage


@Composable
fun NavigationGraph() {
    val navHostController = LocalNavController.current

    NavHost(navController = navHostController, startDestination = NavGraph.Splash.routeName) {
        destinations.forEach { destination ->
            composable(destination.routeName) {
                destination.composable {
                    AppBar(
                        logoutClick = { navHostController.navigate(NavGraph.Login.routeName) },
                    )
                }
            }
        }
    }
}

enum class NavGraph(
    val routeName: String,
    val composable: @Composable (appBar: @Composable () -> Unit) -> Unit
) {
    Splash(
        routeName = "splash",
        composable = {
            Column {
                SplashScreen()
            }
        }
    ),

    Home(
        routeName = "home",
        composable = { appBar ->
            val navController = LocalNavController.current
            Column {
                appBar()
                HomePage(
                    goToPlayerPage = {navController.navigate(NavGraph.Player.routeName)}
                )
            }
        }
    ),

    @OptIn(ExperimentalTvMaterial3Api::class)
    Login(
        routeName = "login",
        composable = {
            val navController = LocalNavController.current
            
            Column {
                LoginPage(
                    goToHomePage = { navController.navigate(Home.routeName) } ,
                )
            }
        }
    ),
    // Player
    Player(
        routeName = "player",
        composable = {
            Column {
                PlayerPage()
            }
        }
    ),
//    Typography(
//        routeName = "typography",
//        composable = { appBar ->
//            Column {
//                appBar()
//                //TypographyScreen()
//            }
//        }
//    ),
//    Motion(
//        routeName = "motion",
//        composable = { appBar ->
//            Column {
//                appBar()
//                //MotionScreen()
//            }
//        }
//    ),
//    Interaction(
//        routeName = "interaction",
//        composable = { appBar ->
//            Column {
//                appBar()
//                //InteractionsScreen()
//            }
//        }
//    ),
//
//    // components
//    Buttons(
//        routeName = "buttons",
//        composable = { appBar ->
//            Column {
//                appBar()
//                //ButtonsScreen()
//            }
//        }
//    ),
//    Cards(
//        routeName = "cards",
//        composable = { appBar ->
//            Column {
//                appBar()
//                //CardsScreen()
//            }
//        }
//    ),
//    Chips(
//        routeName = "chips",
//        composable = { appBar ->
//            Column {
//                appBar()
//                //ChipsScreen()
//            }
//        }
//    ),
//    Lists(
//        routeName = "lists",
//        composable = { appBar ->
//            Column {
//                appBar()
//                //ListsScreen()
//            }
//        }
//    ),
//    ImmersiveList(
//        routeName = "immersive-list",
//        composable = { appBar ->
//            Box {
//                //ImmersiveListScreen()
//                appBar()
//            }
//        }
//    ),
//    FeaturedCarousel(
//        routeName = "featured-carousel",
//        composable = { appBar ->
//            Box {
//                //FeaturedCarouselScreen()
//                appBar()
//            }
//        }
//    ),
//    NavigationDrawer(
//        routeName = "nav-drawer",
//        composable = {
//            //WorkInProgressScreen()
//        }
//    ),
//    TabRow(
//        routeName = "tab-row",
//        composable = { appBar ->
//            Column {
//                appBar()
//                //TabRowScreen()
//            }
//        }
//    ),
//    ModalDialog(
//        routeName = "modal-dialog",
//        composable = {
//            //WorkInProgressScreen()
//        }
//    ),
//    TextFields(
//        routeName = "text-fields",
//        composable = {
//            //WorkInProgressScreen()
//        }
//    ),
//    VideoPlayer(
//        routeName = "video-player",
//        composable = {
//            //WorkInProgressScreen()
//        }
//    ),;
}

val destinations = listOf(
    NavGraph.Splash,
    NavGraph.Home,
    NavGraph.Login,
    NavGraph.Player,
)

val LocalNavController = compositionLocalOf<NavHostController> {
    throw Error("This should not be reached")
}
