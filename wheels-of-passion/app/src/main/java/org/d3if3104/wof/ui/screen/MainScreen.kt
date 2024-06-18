package org.d3if3104.wof.ui.screen

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.d3if3104.wof.R
import org.d3if3104.wof.network.UserDataStore
import org.d3if3104.wof.ui.theme.WOFTheme
import org.d3if3104.wof.model.Cars
import org.d3if3104.wof.model.User
import org.d3if3104.wof.network.Api
import org.d3if3104.wof.network.ApiStatus
import org.d3if3104.wof.BuildConfig
import org.d3if3104.wof.ui.theme.Montserrat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {

    val context = LocalContext.current
    val viewModel: MainViewModel = viewModel()
    val errorMessage by viewModel.errorMessage
    val dataStore = UserDataStore(context)
    val user by dataStore.userFlow.collectAsState(User())
    var showDialog by remember { mutableStateOf(false) }
    var showImgDialog by remember { mutableStateOf(false) }

    var bitmap: Bitmap? by remember { mutableStateOf(null) }
    val launcher = rememberLauncherForActivityResult(CropImageContract()) {
        bitmap = getCroppedImage(context.contentResolver, it)
        if (bitmap != null) showImgDialog = true
    }

    val isUploading by viewModel.isUploading
    val isSuccess by viewModel.querySuccess

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            Toast.makeText(context, "Berhasil!", Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }
    LaunchedEffect(isUploading) {
        if (isUploading) {
            Toast.makeText(context, "Sedang mengupload...", Toast.LENGTH_LONG).show()
            viewModel.clearMessage()
        }
    }

    val showList by dataStore.layoutFlow.collectAsState(true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        if (user.name != "") {
                            Spacer(modifier = Modifier.height(18.dp))
                            Text(
                                text = "Selamat Datang, ",
                                style = MaterialTheme.typography.displayMedium
                            )
                            Text(text = user.name, fontSize = 16.sp, fontFamily = Montserrat)
                        } else {
                            Text(
                                text = "Selamat Datang",
                                style = MaterialTheme.typography.displayLarge
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            dataStore.saveLayout(!showList)
                        }
                    }) {
                        Icon(
                            painter = painterResource(
                                if (!showList) R.drawable.grid
                                else R.drawable.list
                            ),
                            contentDescription = stringResource(
                                if (showList) R.string.list
                                else R.string.grid
                            ),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = {
                        if (user.email.isEmpty()) {
                            CoroutineScope(Dispatchers.IO).launch { signIn(context, dataStore) }
                        } else {
                            showDialog = true
                        }
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.account_circle),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (user.email.isNotEmpty() && user.email != "") {
                    val options = CropImageContractOptions(
                        null, CropImageOptions(
                            imageSourceIncludeGallery = true,
                            imageSourceIncludeCamera = true,
                            fixAspectRatio = true
                        )
                    )
                    launcher.launch(options)
                } else {
                    Toast.makeText(context, "Harap login terlebih dahulu!", Toast.LENGTH_SHORT)
                        .show()
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(id = R.string.tambah_foto),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    ) { padding ->
        ScreenContent(Modifier.padding(padding), viewModel, user, showList)

        if (showDialog) {
            ProfilDialog(user = user, onDismissRequest = { showDialog = false }) {
                CoroutineScope(Dispatchers.IO).launch { signOut(context, dataStore) }
                showDialog = false
            }
        }

        if (showImgDialog) {
            ImageDialog(
                bitmap = bitmap,
                onDismissRequest = { showImgDialog = false }) { nama_mobil, tipe_mobil, tempat ->
                viewModel.saveData(user.email, nama_mobil, tipe_mobil, tempat, bitmap!!)
                showImgDialog = false
            }
        }

        LaunchedEffect(errorMessage) {
            if (errorMessage != null) {
                Log.d("MainScreen", "$errorMessage")
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                viewModel.clearMessage()
            }
        }
    }
}

@Composable
fun ScreenContent(modifier: Modifier, viewModel: MainViewModel, user: User, showList: Boolean) {
    val data by viewModel.data
    val status by viewModel.status.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var wofData by remember { mutableStateOf<Cars?>(null) }
    val retrieveErrorMessage by viewModel.errorMessageNoToast

    LaunchedEffect(data) {
        viewModel.retrieveData(user.email)
    }

    LaunchedEffect(user) {
        viewModel.retrieveData(user.email)
    }


    when (status) {
        ApiStatus.LOADING -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        ApiStatus.SUCCESS -> {
            if (showList) {
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceDim),
                    contentPadding = PaddingValues(bottom = 84.dp, top = 16.dp)
                ) {
                    item {
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                            shape = RoundedCornerShape(15.dp),
                            value = "",
                            onValueChange = {},
                            placeholder = { Text(text = "Cari sesuatu") }
                        )
                    }
                    item {
                        Text(
                            text = "Postinganku",
                            style = MaterialTheme.typography.headlineLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp, vertical = 16.dp)
                        )
                    }
                    items(data) {
                        ListItem(car = it) {
                            wofData = it
                            showDeleteDialog = true
                        }
                    }
                }
            } else {
                LazyVerticalStaggeredGrid(
                    modifier = modifier.fillMaxSize(),
                    columns = StaggeredGridCells.Fixed(2),
                    verticalItemSpacing = 8.dp,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 84.dp)
                ) {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                            shape = RoundedCornerShape(15.dp),
                            value = "",
                            onValueChange = {},
                            placeholder = { Text(text = "Cari sesuatu") }
                        )
                    }
                    item(span = StaggeredGridItemSpan.FullLine) {
                        Text(
                            text = "Postinganku",
                            style = MaterialTheme.typography.headlineLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp, vertical = 16.dp)
                        )
                    }
                    items(data) {
                        GridItem(it) {
                            wofData = it
                            showDeleteDialog = true
                        }
                    }
                }
                if (showDeleteDialog) {
                    HapusDialog(
                        car = wofData!!,
                        onDismissRequest = { showDeleteDialog = false }) {
                        viewModel.deleteData(user.email, wofData!!.id)
                        showDeleteDialog = false
                    }
                }
            }
            if (showDeleteDialog) {
                HapusDialog(wofData!!, onDismissRequest = { showDeleteDialog = false }) {
                    viewModel.deleteData(user.email, wofData!!.id)
                    showDeleteDialog = false
                }
            }
        }

        ApiStatus.FAILED -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (user.email.isEmpty()) {
                    Text(text = "Anda belum login.")
                } else {
                    if (retrieveErrorMessage != null) {
                        when (retrieveErrorMessage) {
                            "Anda belum memasukkan data." -> {
                                Image(
                                    painter = painterResource(id = R.drawable.empty_state),
                                    contentDescription = "Empty Data Image"
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(id = R.string.list_kosong),
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }

                            else -> {
                                Text(text = retrieveErrorMessage!!)
                                Button(
                                    onClick = { viewModel.retrieveData(user.email) },
                                    modifier = Modifier.padding(top = 16.dp),
                                    contentPadding = PaddingValues(
                                        horizontal = 32.dp,
                                        vertical = 16.dp
                                    )
                                ) {
                                    Text(text = stringResource(id = R.string.tombol_coba_lagi))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ListItem(car: Cars, onClick: () -> Unit) {
    Card(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceBright,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceBright)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(8.dp))
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(Api.getImageUrl(car.image_id))
                    .crossfade(true)
                    .build(),
                contentDescription = stringResource(
                    id = R.string.gambar, car.image_id
                ),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.loading_img),
                error = painterResource(id = R.drawable.broken_image),
                modifier = Modifier
                    .size(100.dp)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(MaterialTheme.colorScheme.surfaceBright),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.width(125.dp),
                        text = car.nama_mobil,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.displayMedium
                    )
                    Text(text = car.tipe_mobil)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.width(175.dp),
                        text = car.tempat,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.displaySmall
                    )
                    IconButton(onClick = { onClick() }) {
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete Icon")
                    }
                }
            }
        }
    }
}

@Composable
fun GridItem(car: Cars, onClick: () -> Unit) {
    Card(
        onClick = { onClick() },
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceBright,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceBright)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(Api.getImageUrl(car.image_id))
                .crossfade(true)
                .build(),
            contentDescription = stringResource(
                id = R.string.gambar, car.image_id
            ),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.loading_img),
            error = painterResource(id = R.drawable.broken_image),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .clip(RoundedCornerShape(10.dp))
        )
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = car.nama_mobil,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.displayMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = car.tipe_mobil)
                IconButton(onClick = { onClick() }) {
                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete Icon")
                }
            }
            Text(
                text = car.tempat,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.displaySmall
            )
        }
    }
}

private fun shareData(context: Context, message: String) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, message)
    }
    if (shareIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(shareIntent)
    }
}

private suspend fun signIn(
    context: Context,
    dataStore: UserDataStore
) {
    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(BuildConfig.API_KEY)
        .build()

    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    try {
        val credentialManager = CredentialManager.create(context)
        val result = credentialManager.getCredential(context, request)
        handleSignIn(result, dataStore)
    } catch (e: GetCredentialException) {
        Log.e("SIGN-IN", "Error: ${e.errorMessage}")
    }
}

private suspend fun handleSignIn(
    result: GetCredentialResponse,
    dataStore: UserDataStore
) {
    val credential = result.credential
    if (credential is CustomCredential &&
        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
    ) {
        try {
            val googleId = GoogleIdTokenCredential.createFrom(credential.data)
            val nama = googleId.displayName ?: ""
            val email = googleId.id
            val photoUrl = googleId.profilePictureUri.toString()
            dataStore.saveData(User(nama, email, photoUrl))
        } catch (e: GoogleIdTokenParsingException) {
            Log.e("SIGN-IN", "Error: ${e.message}")
        }
    } else {
        Log.e("SIGN-IN", "Error: unrecognized custom credential type")
    }
}

private suspend fun signOut(context: Context, dataStore: UserDataStore) {
    try {
        val credentialManager = CredentialManager.create(context)
        credentialManager.clearCredentialState(
            ClearCredentialStateRequest()
        )
        dataStore.saveData(User())
    } catch (e: ClearCredentialException) {
        Log.e("SIGN-IN", "Error: ${e.errorMessage}")
    }
}

private fun getCroppedImage(
    resolver: ContentResolver,
    result: CropImageView.CropResult
): Bitmap? {
    if (!result.isSuccessful) {
        Log.e("IMAGE", "Error: ${result.error}")
        return null
    }
    val uri = result.uriContent ?: return null

    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
        MediaStore.Images.Media.getBitmap(resolver, uri)
    } else {
        val source = ImageDecoder.createSource(resolver, uri)
        ImageDecoder.decodeBitmap(source)
    }
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ScreenPreview() {
    WOFTheme {
        MainScreen()
    }
}