package com.geniusdevelop.playmyscreens.ui.theme.navigation

import android.os.Build
import androidx.annotation.RequiresApi
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
import com.geniusdevelop.playmyscreens.app.pages.logout.SwitchAccountPage


@RequiresApi(Build.VERSION_CODES.O)
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
            val navController = LocalNavController.current
            Column {
                SplashScreen(
                    reloadPage = { navController.navigate(Splash.routeName) },
                )
            }
        }
    ),

    @RequiresApi(Build.VERSION_CODES.O)
    Home(
        routeName = "home",
        composable = { appBar ->
            val navController = LocalNavController.current
            Column {
                appBar()
                HomePage(
                    goToSplashPage = { navController.navigate(Splash.routeName) },
                    goToPlayerPage = { navController.navigate(Player.routeName) },
                    goToLogoutPage = { switchAccount ->
                        if (switchAccount) {
                            navController.navigate(SwitchAccount.routeName)
                        } else {
                            navController.navigate(Logout.routeName)
                        }
                    }
                )
            }
        }
    ),

    @RequiresApi(Build.VERSION_CODES.O)
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
    @RequiresApi(Build.VERSION_CODES.O)
    Player(
        routeName = "player",
        composable = {
            val navController = LocalNavController.current
            Column {
                PlayerPage(
                    goToSplashPage = { navController.navigate(Splash.routeName) },
                    refreshPlayer = { navController.navigate(Player.routeName) },
                    goToLogout = { switchAccount ->
                        if (switchAccount) {
                            navController.navigate(SwitchAccount.routeName)
                        } else {
                            navController.navigate(Logout.routeName)
                        }
                    }
                )
            }
        }
    ),
    @RequiresApi(Build.VERSION_CODES.O)
    Logout(
        routeName = "logout",
        composable = {
            val navController = LocalNavController.current
            Column {
                LogoutPage(goToLogin = { navController.navigate(Login.routeName) })
            }
        }
    ),
    @RequiresApi(Build.VERSION_CODES.O)
    SwitchAccount(
        routeName = "switch",
        composable = {
            val navController = LocalNavController.current
            Column {
                SwitchAccountPage(
                    goToLogin = { navController.navigate(Login.routeName) },
                    goToHomePage = { navController.navigate(Home.routeName) }
                )
            }
        }
    ),
}

@RequiresApi(Build.VERSION_CODES.O)
val destinations = listOf(
    NavGraph.Splash,
    NavGraph.Home,
    NavGraph.Login,
    NavGraph.Player,
    NavGraph.Logout,
    NavGraph.SwitchAccount,
)

val LocalNavController = compositionLocalOf<NavHostController> {
    throw Error("This should not be reached")
}
