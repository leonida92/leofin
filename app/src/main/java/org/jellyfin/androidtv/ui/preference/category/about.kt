package org.jellyfin.androidtv.ui.preference.category

import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jellyfin.androidtv.BuildConfig
import org.jellyfin.androidtv.R
import org.jellyfin.androidtv.data.service.AppUpdateService
import org.jellyfin.androidtv.ui.preference.dsl.OptionsScreen
import org.jellyfin.androidtv.ui.preference.dsl.action
import org.jellyfin.androidtv.ui.preference.dsl.link
import org.jellyfin.androidtv.ui.preference.screen.LicensesScreen
import org.koin.java.KoinJavaComponent

fun OptionsScreen.aboutCategory() = category {
	setTitle(R.string.pref_about_title)

	var checking = false
	action {
		setTitle(R.string.check_for_updates)
		content = context.getString(R.string.check_for_updates_description, BuildConfig.VERSION_NAME)
		icon = R.drawable.ic_loop
		onActivate = {
			if (!checking) {
				checking = true
				val updateService = KoinJavaComponent.get<AppUpdateService>(AppUpdateService::class.java)
				CoroutineScope(Dispatchers.Main).launch {
					updateService.checkAndInstallUpdate(
						context = context,
						onStatusChange = { status ->
							content = status
							notifyChange()
						},
						onFinished = {
							checking = false
							notifyChange()
						}
					)
				}
			}
		}
	}

	link {
		// Hardcoded strings for troubleshooting purposes
		title = "Leofin app version"
		content = "leofin-androidtv ${BuildConfig.VERSION_NAME} ${BuildConfig.BUILD_TYPE}"
		icon = R.drawable.ic_jellyfin
	}

	link {
		setTitle(R.string.pref_device_model)
		content = "${Build.MANUFACTURER} ${Build.MODEL}"
		icon = R.drawable.ic_tv
	}

	link {
		setTitle(R.string.licenses_link)
		setContent(R.string.licenses_link_description)
		icon = R.drawable.ic_guide
		withFragment<LicensesScreen>()
	}
}
