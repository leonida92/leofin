package org.jellyfin.androidtv.integration.dream.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jellyfin.androidtv.R
import org.jellyfin.androidtv.ui.base.Text

@Composable
fun DreamContentLogo() = Box(
	modifier = Modifier
		.fillMaxSize()
		.background(Color.Black),
	contentAlignment = Alignment.Center,
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.Center,
	) {
		Image(
			painter = painterResource(R.drawable.app_logo_mark),
			contentDescription = stringResource(R.string.app_name),
			modifier = Modifier.size(80.dp),
		)
		Spacer(modifier = Modifier.width(18.dp))
		Text(
			text = stringResource(R.string.app_name_release),
			color = Color.White,
			fontSize = 56.sp,
			fontWeight = FontWeight.Bold,
			fontFamily = FontFamily.SansSerif,
			letterSpacing = 1.sp,
		)
	}
}
