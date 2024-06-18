package org.d3if3104.wof.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import org.d3if3104.wof.R
import org.d3if3104.wof.model.Cars
import org.d3if3104.wof.network.Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HapusDialog(
    car: Cars,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(modifier = Modifier.padding(16.dp), shape = RoundedCornerShape(16.dp)) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(id = R.string.pesan_hapus))
                Spacer(modifier = Modifier.height(8.dp))
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(Api.getImageUrl(car.image_id))
                        .crossfade(true)
                        .build(),
                    contentDescription = stringResource(
                        id = R.string.gambar, car.nama_mobil
                    ),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.loading_img),
                    error = painterResource(id = R.drawable.broken_image),
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(10.dp))
                )
                OutlinedTextField(
                    value = car.nama_mobil,
                    readOnly = true,
                    onValueChange = { },
                    label = {
                        Text(text = stringResource(R.string.nama_mobil_label))
                    },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 8.dp)
                )
                OutlinedTextField(
                    value = car.tipe_mobil,
                    readOnly = true,
                    onValueChange = { },
                    label = {
                        Text(text = stringResource(id = R.string.tipe_mobil_label))
                    },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 8.dp)
                )
                OutlinedTextField(
                    value = car.tempat,
                    readOnly = true,
                    onValueChange = { },
                    label = {
                        Text(text = stringResource(R.string.tempat_label))
                    },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedButton(
                    onClick = { onDismissRequest() },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(text = stringResource(id = R.string.tombol_batal))
                }
                OutlinedButton(
                    onClick = { onConfirmation() },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(text = "Hapus")
                }
            }
        }
    }
}

@Preview
@Composable
private fun Prev() {
//    HapusDialog(onDismissRequest = { /*TODO*/ }) {
//
//    }
}
