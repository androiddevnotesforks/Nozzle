package com.kaiwolfram.nozzle.ui.app.views.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kaiwolfram.nozzle.R

@Composable
fun ProfileScreen(
    uiState: ProfileViewModelState,
    onChangeProfilePictureUrl: (String) -> Unit,
    onChangeName: (String) -> Unit,
    onChangeBio: (String) -> Unit,
    onChangePrivateKey: (String) -> Unit,
) {
    var openProfilePicDialog by remember { mutableStateOf(false) }
    var openNameDialog by remember { mutableStateOf(false) }
    var openBioDialog by remember { mutableStateOf(false) }
    var openKeyDialog by remember { mutableStateOf(false) }

    if (openProfilePicDialog) {
        ChangeProfilePictureDialog(
            currentUrl = uiState.profilePictureUrl,
            onChangeUrl = onChangeProfilePictureUrl,
            onCloseDialog = { openProfilePicDialog = false })
    }
    if (openNameDialog) {
        ChangeNameDialog(
            currentName = uiState.name,
            onChangeName = onChangeName,
            onCloseDialog = { openNameDialog = false })
    }
    if (openBioDialog) {
        ChangeBioDialog(
            currentBio = uiState.bio,
            onChangeBio = onChangeBio,
            onCloseDialog = { openBioDialog = false })
    }
    if (openKeyDialog) {
        ChangePrivateKeyDialog(
            currentPrivateKey = uiState.privateKey,
            onChangePrivateKey = onChangePrivateKey,
            onCloseDialog = { openKeyDialog = false })
    }
    ProfileData(
        profilePicture = uiState.profilePicture,
        name = uiState.name,
        bio = uiState.bio,
        shortenedPubKey = uiState.shortenedPubKey,
        onOpenProfilePicDialog = { openProfilePicDialog = true },
        onOpenNameDialog = { openNameDialog = true },
        onOpenBioDialog = { openBioDialog = true },
    ) { openKeyDialog = true }
}

@Composable
private fun ProfileData(
    profilePicture: Painter,
    name: String,
    bio: String,
    shortenedPubKey: String,
    onOpenProfilePicDialog: () -> Unit,
    onOpenNameDialog: () -> Unit,
    onOpenBioDialog: () -> Unit,
    onOpenKeyDialog: () -> Unit,
) {
    Row(modifier = Modifier.padding(2.dp)) {
        Icon(
            painter = profilePicture,
            contentDescription = stringResource(id = R.string.profile_picture),
            tint = Color.Unspecified,
            modifier = Modifier
                .padding(6.dp)
                .fillMaxWidth(0.25f)
                .aspectRatio(1f)
                .clip(CircleShape)
                .clickable { onOpenProfilePicDialog() }

        )
        Column {
            Text(
                modifier = Modifier.clickable { onOpenNameDialog() },
                text = name,
                fontWeight = FontWeight.Bold
            )
            Text(
                modifier = Modifier.clickable { onOpenKeyDialog() },
                text = shortenedPubKey,
                color = Color.Gray.copy(alpha = 0.8f),
            )
            Text(
                modifier = Modifier.clickable { onOpenBioDialog() },
                text = bio
            )
        }
    }
}
