package br.com.easyshop.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import br.com.easyshop.ui.customers.CustomersScreen
import br.com.easyshop.ui.products.ProductsScreen
import br.com.easyshop.ui.theme.EasyShopTheme

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EasyShopTheme {
                var selectedTab by rememberSaveable { mutableIntStateOf(0) }
                Scaffold(
                    topBar = {
                        Column {
                            TopAppBar(title = { Text("EasyShop · Persistência Poliglota") })
                            PrimaryTabRow(selectedTabIndex = selectedTab) {
                                Tab(
                                    selected = selectedTab == 0,
                                    onClick = { selectedTab = 0 },
                                    text = { Text("Produtos (NoSQL)") }
                                )
                                Tab(
                                    selected = selectedTab == 1,
                                    onClick = { selectedTab = 1 },
                                    text = { Text("Clientes (SQL)") }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    val modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .imePadding()
                    when (selectedTab) {
                        0 -> ProductsScreen(modifier)
                        else -> CustomersScreen(modifier)
                    }
                }
            }
        }
    }
}
