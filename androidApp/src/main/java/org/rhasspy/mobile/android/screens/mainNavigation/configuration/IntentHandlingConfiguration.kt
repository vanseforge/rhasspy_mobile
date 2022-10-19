package org.rhasspy.mobile.android.screens.mainNavigation.configuration

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.utils.DropDownEnumListItem
import org.rhasspy.mobile.android.utils.PageContent
import org.rhasspy.mobile.android.utils.RadioButtonListItem
import org.rhasspy.mobile.android.utils.TextFieldListItem
import org.rhasspy.mobile.android.utils.TextFieldListItemVisibility
import org.rhasspy.mobile.viewModels.configuration.IntentHandlingConfigurationViewModel


/**
 * content for intent handling configuration
 * drop down to select option
 * http configuration
 * home assistant configuration
 */
@Preview
@Composable
fun IntentHandlingConfigurationContent(viewModel: IntentHandlingConfigurationViewModel = viewModel()) {

    PageContent(MR.strings.intentHandling) {

        //drop down to select option
        DropDownEnumListItem(
            selected = viewModel.intentHandlingOption.collectAsState().value,
            onSelect = viewModel::selectIntentHandlingOption,
            values = viewModel.intentHandlingOptionsList
        )

        //http endpoint
        RemoteHTTPOption(viewModel)

        //home assistant settings
        HomeAssistantOption(viewModel)

    }

}

/**
 * http configuration for intent handling
 * field to set endpoint
 */
@Composable
private fun RemoteHTTPOption(viewModel: IntentHandlingConfigurationViewModel) {

    //visibility of endpoint setting
    AnimatedVisibility(
        enter = expandVertically(),
        exit = shrinkVertically(),
        visible = viewModel.isRemoteHttpEndpointVisible.collectAsState().value
    ) {

        //endpoint input field
        TextFieldListItem(
            value = viewModel.intentHandlingHttpEndpoint.collectAsState().value,
            onValueChange = viewModel::changeIntentHandlingHttpEndpoint,
            label = MR.strings.remoteURL
        )

    }

}

/**
 * configuration of home assistant intent handling
 * url
 * access token
 * hass event or intent
 */
@Composable
private fun HomeAssistantOption(viewModel: IntentHandlingConfigurationViewModel) {

    //home assistant settings visibility
    AnimatedVisibility(
        enter = expandVertically(),
        exit = shrinkVertically(),
        visible = viewModel.isHomeAssistantSettingsVisible.collectAsState().value
    ) {

        Column {

            //endpoint url
            TextFieldListItem(
                value = viewModel.intentHandlingHassEndpoint.collectAsState().value,
                onValueChange = viewModel::changeIntentHandlingHassEndpoint,
                label = MR.strings.hassURL
            )

            //hass access token
            TextFieldListItemVisibility(
                value = viewModel.intentHandlingHassAccessToken.collectAsState().value,
                onValueChange = viewModel::changeIntentHandlingHassAccessToken,
                label = MR.strings.accessToken
            )

            //select hass event or hass intent
            RadioButtonListItem(
                text = MR.strings.homeAssistantEvents,
                isChecked = viewModel.isIntentHandlingHassEvent.collectAsState().value,
                onClick = viewModel::selectIntentHandlingHassEvent
            )

            RadioButtonListItem(
                text = MR.strings.homeAssistantIntents,
                isChecked = viewModel.isIntentHandlingHassIntent.collectAsState().value,
                onClick = viewModel::selectIntentHandlingHassIntent
            )

        }

    }

}