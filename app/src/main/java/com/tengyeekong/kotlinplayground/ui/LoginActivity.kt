package com.tengyeekong.kotlinplayground.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.tengyeekong.kotlinplayground.viewmodel.ListingViewModel
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.*
import javax.inject.Inject

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
class LoginActivity : DaggerAppCompatActivity() {

    private lateinit var listingViewModel: ListingViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var username: MutableState<String>
    private lateinit var password: MutableState<String>
    private lateinit var passwordVisibility: MutableState<Boolean>
    private lateinit var loginBtnText: MutableState<String>
    private lateinit var isLoggingIn: MutableState<Boolean>
    private lateinit var coroutineScope: CoroutineScope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // username: admin@advisoryapps.com / movida@advisoryapps.com
        // password: advisoryapps123 / movida123

        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        listingViewModel =
            ViewModelProvider(this, viewModelFactory).get(ListingViewModel::class.java)

        setContent {
            username = remember { mutableStateOf("") }
            password = remember { mutableStateOf("") }
            passwordVisibility = remember { mutableStateOf(false) }
            loginBtnText = remember { mutableStateOf("Login") }
            isLoggingIn = remember { mutableStateOf(false) }
            coroutineScope = rememberCoroutineScope()
            val focusManager = LocalFocusManager.current

            MaterialTheme(
                colors = lightColors(
                    primary = Color(0xff008577),
                    secondary = Color(0xff80CBC4),
                    background = Color(0xff6a9994),
                )
            ) {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.background)
                        .padding(horizontal = 40.dp)
                ) {
                    val (tfUsername, tfPassword, spacer, btnLogin) = createRefs()

                    createVerticalChain(
                        tfUsername,
                        tfPassword,
                        spacer,
                        btnLogin,
                        chainStyle = ChainStyle.Packed
                    )

                    CustomTextField(
                        label = "Username",
                        text = username,
                        KeyboardType.Text,
                        modifier = Modifier
                            .constrainAs(tfUsername) {
                                top.linkTo(parent.top)
                                bottom.linkTo(tfPassword.top)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                    )
                    CustomTextField(
                        label = "Password",
                        text = password,
                        KeyboardType.Password,
                        modifier = Modifier
                            .constrainAs(tfPassword) {
                                top.linkTo(tfUsername.bottom)
                                bottom.linkTo(spacer.top)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                    )
                    Spacer(modifier = Modifier
                        .size(4.dp)
                        .constrainAs(spacer) {
                            top.linkTo(tfPassword.bottom)
                            bottom.linkTo(btnLogin.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        })
                    LoginButton(
                        modifier = Modifier
                            .constrainAs(btnLogin) {
                                top.linkTo(spacer.bottom)
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            },
                        onButtonClicked = {
                            focusManager.clearFocus()
                            if (username.value.isEmpty() || password.value.isEmpty()) {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Please enter username and password",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                loginBtnText.value = "Logging in"
                                isLoggingIn.value = true
                                listingViewModel.login(
                                    username.value,
                                    password.value
                                ).observe(this@LoginActivity, { login ->
                                    isLoggingIn.value = false
                                    if (login != null) {
                                        if (!(login.status == null || login.status.code != "200")) {
                                            loginBtnText.value = "Logged in"
                                            coroutineScope.launch(Dispatchers.IO) {
                                                delay(1_000)
                                                withContext(Dispatchers.Main) {
                                                    val intent =
                                                        Intent(
                                                            this@LoginActivity,
                                                            ListingActivity::class.java
                                                        )
                                                    startActivity(intent)
                                                    Toast.makeText(
                                                        this@LoginActivity,
                                                        login.status.message,
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                            }
                                        } else {
                                            loginBtnText.value = "Login"
                                            if (login.status != null)
                                                Toast.makeText(
                                                    this@LoginActivity,
                                                    login.status.message,
                                                    Toast.LENGTH_LONG
                                                ).show()
                                        }
                                    } else {
                                        loginBtnText.value = "Login"
                                        Toast.makeText(
                                            this@LoginActivity,
                                            "Please try again",
                                            Toast.LENGTH_LONG
                                        ).show()
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
    fun CustomTextField(
        label: String,
        text: MutableState<String>,
        keyboardType: KeyboardType,
        modifier: Modifier,
    ) {
        TextField(
            value = text.value,
            onValueChange = { text.value = it },
            label = { Text(label) },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = if (keyboardType != KeyboardType.Password || passwordVisibility.value)
                VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                if (keyboardType == KeyboardType.Password) {
                    val image = if (passwordVisibility.value)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    IconButton(onClick = {
                        passwordVisibility.value = !passwordVisibility.value
                    }) {
                        Icon(imageVector = image, "")
                    }
                }
            },
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color(0xffdefcfa),
                cursorColor = Color.Black,
                disabledLabelColor = Color(0xffdefcfa),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = MaterialTheme.shapes.small,
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
        )
    }

    @Composable
    fun LoginButton(
        modifier: Modifier,
        onButtonClicked: () -> Unit,
    ) {
        Button(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            onClick = onButtonClicked
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(visible = isLoggingIn.value) {
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
                Text(loginBtnText.value)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}
