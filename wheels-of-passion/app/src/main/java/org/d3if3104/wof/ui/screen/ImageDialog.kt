package org.d3if3104.wof.ui.screen

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.d3if3104.wof.R

@Composable
fun ImageDialog(
    bitmap: Bitmap?,
    onDismissRequest: () -> Unit,
    onConfirmation: (String, String, String) -> Unit
) {
    var nama_mobil by remember { mutableStateOf("") }
    var tipe_mobil by remember { mutableStateOf("") }
    var tempat by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(modifier = Modifier.padding(16.dp), shape = RoundedCornerShape(16.dp)) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )
                OutlinedTextField(
                    value = nama_mobil,
                    onValueChange = { nama_mobil = it },
                    label = {
                        Text(text = stringResource(R.string.nama_mobil_label))
                    },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )
                OutlinedTextField(
                    value = tipe_mobil,
                    onValueChange = { tipe_mobil = it },
                    label = {
                        Text(text = stringResource(id = R.string.tipe_mobil_label))
                    },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )

                OutlinedTextField(
                    value = tempat,
                    onValueChange = { tempat = it },
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
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedButton(
                    onClick = { onDismissRequest() },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(text = stringResource(id = R.string.tombol_batal))
                }
                OutlinedButton(
                    onClick = { onConfirmation(nama_mobil, tipe_mobil, tempat) },
                    enabled = nama_mobil.isNotEmpty() && tipe_mobil.isNotEmpty() && tempat.isNotEmpty(),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(text = stringResource(id = R.string.simpan))
                }
            }
        }
    }
}

@Composable
fun RadiOptions(label: String, isSelected: Boolean, modifier: Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = isSelected, onClick = null)
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
