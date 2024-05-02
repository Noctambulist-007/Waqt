package com.heapiphy101.waqt.dialogs

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.heapiphy101.waqt.R
import com.heapiphy101.waqt.helper.LocaleHelper

class BottomSheetDialog : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.bottom_sheet_layout, container, false)
        val dialogViewConnect = inflater.inflate(R.layout.connect, null, false)
        val dialogViewSadaqah = inflater.inflate(R.layout.sadaqah, null, false)

        val tvAboutApp = rootView.findViewById<TextView>(R.id.tvAboutApp)
        val tvTutorial = rootView.findViewById<TextView>(R.id.tvTutorial)
        val tvLanguage = rootView.findViewById<TextView>(R.id.tvLanguage)
        val tvNotificaiton = rootView.findViewById<TextView>(R.id.tvNotification)
        val tvForbiddenTime = rootView.findViewById<TextView>(R.id.tvForbiddenTime)
        val tvAboutUs = rootView.findViewById<TextView>(R.id.tvAboutUs)
        val tvSadaqah = rootView.findViewById<TextView>(R.id.tvSadaqah)
        val tvConnectWith = rootView.findViewById<TextView>(R.id.tvConnectWith)
        val tvGiveFeedback = rootView.findViewById<TextView>(R.id.tvGiveFeedback)

        tvAboutApp.setOnClickListener {
            showAppInfoDialog()
        }

        tvTutorial.setOnClickListener {
            val youtubeUrl = "https://www.youtube.com/watch?v=DskXsgpsZTs"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl))
            startActivity(intent)
        }

        tvLanguage.setOnClickListener {
            showLanguageSelectionDialog()
        }

        tvNotificaiton.setOnClickListener {
            showNotificationSelectionDialog()
        }

        tvForbiddenTime.setOnClickListener {
            showForbiddenTimeDialog()
        }

        tvAboutUs.setOnClickListener {
            showAboutUsInfoDialog()
        }

        tvSadaqah.setOnClickListener {
            context?.let { context ->
                showSadaqahInfoDialog(dialogViewSadaqah, context)
            }
        }

        tvConnectWith.setOnClickListener {
            context?.let { context ->
                showConnectInfoDialog(dialogViewConnect, context)
            }
        }

        tvGiveFeedback.setOnClickListener {
            val webViewIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://docs.google.com/forms/d/e/1FAIpQLSe5s69VjmaD7ppok9LahH-s-SBMrd2NgLzfNd6ysx-UTLIHkA/viewform?usp=sharing")
            )
            startActivity(webViewIntent)
        }

        return rootView
    }


    private fun showNotificationSelectionDialog() {
        val dialogView = layoutInflater.inflate(R.layout.notificaiton_selection_dialog, null)
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setView(dialogView)

        val alertDialog = alertDialogBuilder.create()

        val radioGroupNotification = dialogView.findViewById<RadioGroup>(R.id.radioGroupNotification)
        val radioNotificationOn = dialogView.findViewById<RadioButton>(R.id.radioNotificationOn)
        val radioNotificationOff = dialogView.findViewById<RadioButton>(R.id.radioNotificationOff)

        // Retrieve the current notification setting from SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("NotificationSettings", Context.MODE_PRIVATE)
        val isNotificationOn = sharedPreferences.getBoolean("isNotificationOn", false)

        // Set the initial state of radio buttons based on the SharedPreferences
        radioNotificationOn.isChecked = isNotificationOn
        radioNotificationOff.isChecked = !isNotificationOn

        radioGroupNotification.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioNotificationOn -> {
                    // Save the notification setting to SharedPreferences
                    saveNotificationSetting(true)

                    alertDialog.dismiss()
                    (context as? Activity)?.recreate()
                }
                R.id.radioNotificationOff -> {
                    // Save the notification setting to SharedPreferences
                    saveNotificationSetting(false)

                    alertDialog.dismiss()
                    (context as? Activity)?.recreate()
                }
            }
        }

        alertDialog.window?.attributes?.windowAnimations = R.style.TimePickerDialogAnimation
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()
    }

    private fun saveNotificationSetting(isNotificationOn: Boolean) {
        val sharedPreferences = requireContext().getSharedPreferences("NotificationSettings", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isNotificationOn", isNotificationOn)
        editor.apply()
    }


    private fun showLanguageSelectionDialog() {
        val dialogView = layoutInflater.inflate(R.layout.language_selection_dialog, null)
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setView(dialogView)
        val alertDialog = alertDialogBuilder.create()

        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radioGroup)
        val radioEn = dialogView.findViewById<RadioButton>(R.id.radioEn)
        val radioBn = dialogView.findViewById<RadioButton>(R.id.radioBn)

        // Retrieve the last selected language from SharedPreferences
        val savedLanguage = LocaleHelper.getSavedLanguage(requireContext())

        // Set the default checked radio button based on the saved language
        if (savedLanguage == "en") {
            radioEn.isChecked = true


        } else if (savedLanguage == "bn") {
            radioBn.isChecked = true
        }

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioEn -> {
                    // Use the utility class function
                    LocaleHelper.setLocale(requireContext(), "en") // Set language to English
                    alertDialog.dismiss()
                }
                R.id.radioBn -> {
                    // Use the utility class function
                    LocaleHelper.setLocale(requireContext(), "bn") // Set language to Bengali
                    alertDialog.dismiss()
                }
            }
        }

        alertDialog.window?.attributes?.windowAnimations = R.style.TimePickerDialogAnimation
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()
    }


/*    private fun setLocale(languageCode: String) {
        val sharedPreferences: SharedPreferences =
            requireContext().getSharedPreferences("LanguagePrefs", Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        editor.putString("language", languageCode)
        editor.apply()

        val resources: Resources = resources
        val config: Configuration = resources.configuration
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        config.locale = locale
        resources.updateConfiguration(config, resources.displayMetrics)

        // You may need to recreate your activity or fragments to apply the changes
        // For example, you can recreate the current activity
        requireActivity().recreate()
    }*/


    private fun showConnectInfoDialog(dialogViewConnect: View?, context: Context) {
        dialogViewConnect?.let { view ->
            val parent = view.parent as? ViewGroup
            parent?.removeView(view)

            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setView(view)
            val alertDialog = alertDialogBuilder.create()

            val ivFacebook = view.findViewById<ImageView>(R.id.facebook)
            val ivWebsite = view.findViewById<ImageView>(R.id.website)
            val ivLinkedin = view.findViewById<ImageView>(R.id.linkedin)
            val ivInstagram = view.findViewById<ImageView>(R.id.instagram)
            val ivYoutube = view.findViewById<ImageView>(R.id.youtube)

            ivWebsite?.setOnClickListener {
                openUrl(context, "https://heapiphy.com/")
            }

            ivFacebook?.setOnClickListener {
                openUrl(context, "https://www.facebook.com/profile.php?id=61551984022164")
            }

            ivInstagram?.setOnClickListener {
                openUrl(context, "https://www.instagram.com/heapiphy/")
            }

            ivLinkedin?.setOnClickListener {
                openUrl(context, "https://www.linkedin.com/company/97403912/")
            }

            ivYoutube?.setOnClickListener {
                openUrl(context, "https://www.youtube.com/channel/UC3LDgUIeBx0ot3ROjtyV8dA")
            }

            alertDialog.window?.attributes?.windowAnimations = R.style.TimePickerDialogAnimation
            alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent) // Remove default background
            alertDialog.show()
        }
    }

    private fun openUrl(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    private fun showForbiddenTimeDialog() {
        val dialogView = layoutInflater.inflate(R.layout.forbidden_time, null)
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setView(dialogView)
        val alertDialog = alertDialogBuilder.create()

        alertDialog.window?.attributes?.windowAnimations = R.style.TimePickerDialogAnimation
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()
    }

    private fun showSadaqahInfoDialog(dialogViewSadaqah: View, context: Context) {
        val parent = dialogViewSadaqah.parent as? ViewGroup
        parent?.removeView(dialogViewSadaqah)

        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setView(dialogViewSadaqah)
        val alertDialog = alertDialogBuilder.create()

        val cardViewBikash = dialogViewSadaqah.findViewById<CardView>(R.id.cardViewBikash)
        val cardViewNagad = dialogViewSadaqah.findViewById<CardView>(R.id.cardViewNagad)
        val cardViewDBBL = dialogViewSadaqah.findViewById<CardView>(R.id.cardViewdbbl)

        cardViewBikash.setOnClickListener {
            copyToClipboardAndShowToast(context, "01756964475")
        }

        cardViewNagad.setOnClickListener {
            copyToClipboardAndShowToast(context, "01785889470")
        }

        cardViewDBBL.setOnClickListener {
            copyToClipboardAndShowToast(context, "7017322426284")
        }

        alertDialog.window?.attributes?.windowAnimations = R.style.TimePickerDialogAnimation
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent) // Remove default background
        alertDialog.show()
    }

    private fun copyToClipboardAndShowToast(context: Context, text: String) {
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        clipboardManager?.let { clipboard ->
            val clip: ClipData = ClipData.newPlainText("label", text)
            clipboard.setPrimaryClip(clip)
            val toast = Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT)
            toast.show()
        } ?: run {
            val toast = Toast.makeText(context, "Clipboard not available", Toast.LENGTH_SHORT)
            toast.show()
        }
    }

    private fun showAboutUsInfoDialog() {
        val dialogView = layoutInflater.inflate(R.layout.about_us, null)
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setView(dialogView)
        val alertDialog = alertDialogBuilder.create()

        alertDialog.window?.attributes?.windowAnimations = R.style.TimePickerDialogAnimation
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()
    }

    private fun showAppInfoDialog() {
        val dialogView = layoutInflater.inflate(R.layout.about_app, null)
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setView(dialogView)
        val alertDialog = alertDialogBuilder.create()

        alertDialog.window?.attributes?.windowAnimations = R.style.TimePickerDialogAnimation
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.window?.navigationBarColor =
            ContextCompat.getColor(requireContext(), R.color.black)

        return dialog
    }
}
