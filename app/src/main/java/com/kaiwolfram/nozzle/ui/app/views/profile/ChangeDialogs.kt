package com.kaiwolfram.nozzle.ui.app.views.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.ui.components.*

@Composable
fun ChangeProfilePictureDialog(
    currentUrl: String,
    onChangeUrl: (String) -> Unit,
    onCloseDialog: () -> Unit
) {
    var url by remember { mutableStateOf(currentUrl) }
    AlertDialog(
        shape = dialogShape,
        backgroundColor = dialogBackgroundColor,
        properties = dialogProperties,
        onDismissRequest = onCloseDialog,
        confirmButton = {
            DialogConfirmButton(
                value = url,
                onChangeValue = onChangeUrl,
                onCloseDialog = onCloseDialog
            )
        },
        dismissButton = {
            DialogDismissButton(onCloseDialog = onCloseDialog)
        },
        title = {
            Text(text = stringResource(id = R.string.profile_picture_url))
        },
        text = {
            Column {
                ChangeTextField(
                    currentValue = url,
                    onChangeValue = { change -> url = change },
                    keyboardType = KeyboardType.Uri,
                )
            }
        },
    )
}

@Composable
fun ChangeNameDialog(
    currentName: String,
    onChangeName: (String) -> Unit,
    onCloseDialog: () -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    AlertDialog(
        shape = dialogShape,
        backgroundColor = dialogBackgroundColor,
        properties = dialogProperties,
        onDismissRequest = onCloseDialog,
        confirmButton = {
            DialogConfirmButton(
                value = name,
                onChangeValue = onChangeName,
                onCloseDialog = onCloseDialog
            )
        },
        dismissButton = {
            DialogDismissButton(onCloseDialog = onCloseDialog)
        },
        title = {
            Text(text = stringResource(id = R.string.name))
        },
        text = {
            ChangeTextField(
                currentValue = name,
                onChangeValue = { change -> name = change })
        },
    )
}

@Composable
fun ChangeBioDialog(
    currentBio: String,
    onChangeBio: (String) -> Unit,
    onCloseDialog: () -> Unit
) {
    var bio by remember { mutableStateOf(currentBio) }
    AlertDialog(
        shape = dialogShape,
        backgroundColor = dialogBackgroundColor,
        properties = dialogProperties,
        onDismissRequest = onCloseDialog,
        confirmButton = {
            DialogConfirmButton(
                value = bio,
                onChangeValue = onChangeBio,
                onCloseDialog = onCloseDialog
            )
        },
        dismissButton = {
            DialogDismissButton(onCloseDialog = onCloseDialog)
        },
        title = {
            Text(text = stringResource(id = R.string.bio))
        },
        text = {
            ChangeTextField(
                currentValue = bio,
                onChangeValue = { change -> bio = change })
        },
    )
}

@Composable
fun ChangePrivateKeyDialog(
    currentPrivateKey: String,
    onChangePrivateKey: (String) -> Unit,
    onCloseDialog: () -> Unit
) {
    var privateKey by remember { mutableStateOf(currentPrivateKey) }
    AlertDialog(
        shape = dialogShape,
        backgroundColor = dialogBackgroundColor,
        properties = dialogProperties,
        onDismissRequest = onCloseDialog,
        confirmButton = {
            DialogConfirmButton(
                value = privateKey,
                onChangeValue = onChangePrivateKey,
                onCloseDialog = onCloseDialog
            )
        },
        dismissButton = {
            DialogDismissButton(onCloseDialog = onCloseDialog)
        },
        title = {
            Text(text = stringResource(id = R.string.private_key))
        },
        text = {
            ChangeTextField(
                currentValue = privateKey,
                onChangeValue = { change -> privateKey = change })
        },
    )
}
