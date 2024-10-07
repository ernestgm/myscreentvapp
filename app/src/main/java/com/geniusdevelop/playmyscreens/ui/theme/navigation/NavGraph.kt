package com.geniusdevelop.playmyscreens.ui.theme.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.geniusdevelop.playmyscreens.app.AppBar
import com.geniusdevelop.playmyscreens.app.pages.home.HomePage
import com.geniusdevelop.playmyscreens.app.pages.login.LoginPage
import com.geniusdevelop.playmyscreens.app.pages.splash.SplashScreen
import com.geniusdevelop.playmyscreens.app.pages.player.PlayerPage
import com.geniusdevelop.playmyscreens.app.pages.logout.LogoutPage


@Composable
fun NavigationGraph() {
    val navHostController = LocalNavController.current

    NavHost(navController = navHostController, startDestination = NavGraph.Splash.routeName) {
        destinations.forEach { destination ->
            composable(destination.routeName) {
                destination.composable {
                    AppBar(
                        logoutClick = { navHostController.navigate(NavGraph.Logout.routeName) },
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
                    goToPlayerPage = { navController.navigate(Player.routeName) },
                    goToLogoutPage = { navController.navigate(Logout.routeName) }
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
            val navController = LocalNavController.current
            Column {
                PlayerPage(
                    refreshPlayer = { navController.navigate(Player.routeName) },
                    goToLogout = { navController.navigate(Logout.routeName) }
                )
            }
        }
    ),
    Logout(
        routeName = "motion",
        composable = {
            val navController = LocalNavController.current
            Column {
                LogoutPage(goToLogin = { navController.navigate(Login.routeName) })
            }
        }
    ),
}

val destinations = listOf(
    NavGraph.Splash,
    NavGraph.Home,
    NavGraph.Login,
    NavGraph.Player,
    NavGraph.Logout,
)

val LocalNavController = compositionLocalOf<NavHostController> {
    throw Error("This should not be reached")
}
