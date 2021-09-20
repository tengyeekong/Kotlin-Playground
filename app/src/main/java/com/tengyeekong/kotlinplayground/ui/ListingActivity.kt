package com.tengyeekong.kotlinplayground.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.tengyeekong.kotlinplayground.R
import com.tengyeekong.kotlinplayground.model.List
import com.tengyeekong.kotlinplayground.viewmodel.ListingViewModel
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
class ListingActivity : DaggerAppCompatActivity() {
    private lateinit var viewModel: ListingViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var prefs: SharedPreferences

    private lateinit var focusManager: FocusManager
    private lateinit var dialogFocusManager: FocusManager
    private lateinit var scaffoldState: ScaffoldState
    private lateinit var refreshState: SwipeRefreshState
    private lateinit var listState: LazyListState
    private lateinit var coroutineScope: CoroutineScope
    private lateinit var isLoadingMore: MutableState<Boolean>
    private lateinit var openDialog: MutableState<Boolean>
    private lateinit var isUpdatingItem: MutableState<Boolean>
    private lateinit var updateBtnText: MutableState<String>
    private lateinit var updatePosition: MutableState<Int>
    private lateinit var listId: MutableState<String>
    private lateinit var listName: MutableState<String>
    private lateinit var distance: MutableState<String>
    private lateinit var lazyListing: LazyPagingItems<List>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)
        supportActionBar?.title = prefs.getString(ListingViewModel.USERNAME, "")

        viewModel = ViewModelProvider(this, viewModelFactory).get(ListingViewModel::class.java)

        setContent {
            focusManager = LocalFocusManager.current
            scaffoldState = rememberScaffoldState()
            refreshState = rememberSwipeRefreshState(false)
            listState = rememberLazyListState()
            coroutineScope = rememberCoroutineScope()
            isLoadingMore = remember { mutableStateOf(false) }
            openDialog = remember { mutableStateOf(false) }
            isUpdatingItem = remember { mutableStateOf(false) }
            updateBtnText = remember { mutableStateOf("") }
            updatePosition = remember { mutableStateOf(0) }
            listId = remember { mutableStateOf("") }
            listName = remember { mutableStateOf("") }
            distance = remember { mutableStateOf("") }

            lazyListing = viewModel.flow
                .collectAsLazyPagingItems()
                .apply {
                    if (loadState.refresh is LoadState.NotLoading) {
                        refreshState.isRefreshing = false
                    }
                    isLoadingMore.value = loadState.append is LoadState.Loading
                }

            BackHandler {
                if (listState.firstVisibleItemIndex > 5) {
                    coroutineScope.launch {
                        listState.animateScrollToItem(0)
                    }
                } else {
                    finishAffinity()
                }
            }

            MaterialTheme(
                colors = lightColors(
                    primary = Color(0xff008577),
                    secondary = Color(0xff80CBC4),
                    background = Color(0xff6a9994),
                )
            ) {
                Scaffold(
                    scaffoldState = scaffoldState
                ) {
                    SwipeRefresh(
                        state = refreshState,
                        onRefresh = { lazyListing.refresh() },
                        indicator = { state, trigger ->
                            SwipeRefreshIndicator(
                                state = state,
                                refreshTriggerDistance = trigger,
                                backgroundColor = MaterialTheme.colors.primary,
                                shape = CircleShape,
                            )
                        }
                    ) {
                        Listing(
                            onItemClicked = { list ->
                                coroutineScope.launch {
                                    scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                                    scaffoldState.snackbarHostState.showSnackbar(
                                        message = StringBuilder("List name: ")
                                            .append(list.list_name)
                                            .append("\n")
                                            .append("Distance: ")
                                            .append(list.distance)
                                            .toString(),
                                    )
                                }
                            },
                            onItemLongClicked = { index, list ->
                                updatePosition.value = index
                                list.let {
                                    listId.value = it.id
                                    listName.value = it.list_name
                                    distance.value = it.distance
                                }
                                updateBtnText.value = "Update"
                                openDialog.value = true
                            },
                        )
                    }
                }

                if (openDialog.value) {
                    UpdateDialog(
                        onButtonClicked = {
                            dialogFocusManager.clearFocus()

                            if (!isUpdatingItem.value) {
                                updateBtnText.value = "Updating"
                                isUpdatingItem.value = true
                                viewModel.updateList(
                                    listId.value, listName.value, distance.value
                                ).observe(this@ListingActivity, { isUpdated ->
                                    if (isUpdated) {
                                        lazyListing[updatePosition.value]?.let {
                                            it.list_name = listName.value
                                            it.distance = distance.value
                                        }
                                    }
                                    updateBtnText.value = "Updated"
                                    isUpdatingItem.value = false
                                    coroutineScope.launch(Dispatchers.IO) {
                                        delay(1_000)
                                        openDialog.value = false
                                    }
                                })
                            }
                        }
                    )
                }
            }
        }
    }

    @Composable
    fun Listing(
        onItemClicked: (list: List) -> Unit,
        onItemLongClicked: (index: Int, list: List) -> Unit,
    ) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(top = 4.dp, bottom = 4.dp),
        ) {
            items(lazyListing.itemCount) { index ->
                lazyListing[index]?.let { list ->
                    Column {
                        ListItem(
                            index = index,
                            list = list,
                            onItemClicked = onItemClicked,
                            onItemLongClicked = onItemLongClicked
                        )

                        if (isLoadingMore.value && index == lazyListing.itemCount - 1) {
                            LoadingFooter()
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun ListItem(
        index: Int,
        list: List,
        onItemClicked: (list: List) -> Unit,
        onItemLongClicked: (index: Int, list: List) -> Unit,
    ) {
        Card(
            backgroundColor = Color(0xffdefcfa),
            elevation = 3.dp,
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .combinedClickable(
                        indication = rememberRipple(bounded = true),
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = { onItemClicked(list) },
                        onLongClick = { onItemLongClicked(index, list) }
                    )
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    list.list_name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(list.distance, fontSize = 12.sp)
            }
        }
    }

    @Composable
    fun LoadingFooter() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = Color(0xff8ffff7),
                modifier = Modifier.size(32.dp)
            )
        }
    }

    @ExperimentalComposeUiApi
    @Composable
    fun UpdateDialog(
        onButtonClicked: () -> Unit
    ) {
        AlertDialog(
            backgroundColor = Color(0xff6a9994),
            onDismissRequest = {
                openDialog.value = false
            },
            buttons = {
                dialogFocusManager = LocalFocusManager.current

                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    CustomTextField(
                        label = "List name",
                        text = listName,
                    )
                    CustomTextField(
                        label = "Distance",
                        text = distance,
                    )
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        onClick = onButtonClicked
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AnimatedVisibility(visible = isUpdatingItem.value) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        color = Color(0xffdefcfa),
                                        strokeWidth = 3.dp,
                                        modifier = Modifier
                                            .size(24.dp)
                                            .padding(2.dp)
                                    )
                                    Spacer(modifier = Modifier.size(width = 8.dp, height = 0.dp))
                                }
                            }
                            Text(updateBtnText.value)
                        }
                    }
                }
            }
        )
    }

    @Composable
    fun CustomTextField(
        label: String,
        text: MutableState<String>,
    ) {
        TextField(
            value = text.value,
            onValueChange = { text.value = it },
            label = { Text(label) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color(0xffdefcfa),
                cursorColor = Color.Black,
                disabledLabelColor = Color(0xffdefcfa),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                prefs.edit().clear().apply()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
