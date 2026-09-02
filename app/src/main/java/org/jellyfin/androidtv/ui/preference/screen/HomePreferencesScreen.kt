package org.jellyfin.androidtv.ui.preference.screen

import org.jellyfin.androidtv.R
import org.jellyfin.androidtv.constant.HomeSectionType
import org.jellyfin.androidtv.preference.UserPreferences
import org.jellyfin.androidtv.preference.UserSettingPreferences
import org.jellyfin.androidtv.ui.preference.dsl.OptionsFragment
import org.jellyfin.androidtv.ui.preference.dsl.enum
import org.jellyfin.androidtv.ui.preference.dsl.list
import org.jellyfin.androidtv.ui.preference.dsl.optionsScreen
import org.jellyfin.preference.store.PreferenceStore
import org.koin.android.ext.android.inject

class HomePreferencesScreen : OptionsFragment() {
	private val userSettingPreferences: UserSettingPreferences by inject()
	private val userPreferences: UserPreferences by inject()

	override val stores: Array<PreferenceStore<*, *>>
		get() = arrayOf(userSettingPreferences, userPreferences)

	override val screen by optionsScreen {
		setTitle(R.string.home_prefs)

		category {
			list {
				setTitle(R.string.pref_next_up_cutoff)
				entries = mapOf(
					"0" to getString(R.string.pref_next_up_cutoff_disabled),
					"7" to "7 days",
					"14" to "14 days",
					"30" to "30 days",
					"60" to "60 days",
					"90" to "90 days",
					"180" to "180 days",
					"365" to "365 days",
				)
				bind {
					get { userPreferences[UserPreferences.maxDaysInNextUp].toString() }
					set { value -> userPreferences[UserPreferences.maxDaysInNextUp] = value.toIntOrNull() ?: 0 }
					default { UserPreferences.maxDaysInNextUp.defaultValue.toString() }
				}
			}
		}

		category {
			setTitle(R.string.home_sections)

			userSettingPreferences.homesections.forEachIndexed { index, section ->
				enum<HomeSectionType> {
					title = getString(R.string.home_section_i, index + 1)
					bind(userSettingPreferences, section)
				}
			}
		}
	}
}
