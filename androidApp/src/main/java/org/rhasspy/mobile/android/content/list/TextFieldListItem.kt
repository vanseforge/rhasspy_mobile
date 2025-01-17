package org.rhasspy.mobile.android.content.list

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.launch
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.content.elements.Icon
import org.rhasspy.mobile.android.content.elements.translate

@Composable
fun TextFieldListItemVisibility(
    modifier: Modifier = Modifier,
    label: StringResource,
    value: String,
    readOnly: Boolean = false,
    autoCorrect: Boolean = false,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isLastItem: Boolean = true,
    onValueChange: ((String) -> Unit)? = null,
) {
    var isShowPassword by rememberSaveable { mutableStateOf(false) }

    TextFieldListItem(
        modifier = modifier,
        label = label,
        value = value,
        readOnly = readOnly,
        autoCorrect = autoCorrect,
        enabled = enabled,
        onValueChange = onValueChange,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        isLastItem = isLastItem,
        trailingIcon = {
            IconButton(onClick = { isShowPassword = !isShowPassword }) {
                Icon(
                    if (isShowPassword) {
                        Icons.Filled.Visibility
                    } else {
                        Icons.Filled.VisibilityOff
                    },
                    contentDescription = MR.strings.visibility,
                )
            }
        },
        visualTransformation = if (isShowPassword) VisualTransformation.None else PasswordVisualTransformation()
    )
}

@Composable
fun TextFieldListItem(
    modifier: Modifier = Modifier,
    label: StringResource,
    value: String,
    readOnly: Boolean = false,
    autoCorrect: Boolean = false,
    enabled: Boolean = true,
    onValueChange: ((String) -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isLastItem: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    TextFieldListItem(
        modifier = modifier,
        label = translate(label),
        value = value,
        readOnly = readOnly,
        autoCorrect = autoCorrect,
        enabled = enabled,
        onValueChange = onValueChange,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        isLastItem = isLastItem,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation
    )
}

@Composable
fun TextFieldListItem(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    readOnly: Boolean = false,
    autoCorrect: Boolean = false,
    enabled: Boolean = true,
    onValueChange: ((String) -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isLastItem: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    ListElement(
        modifier = Modifier.bringIntoViewRequester(bringIntoViewRequester)
    ) {
        val coroutineScope = rememberCoroutineScope()
        val focusManager = LocalFocusManager.current

        OutlinedTextField(
            singleLine = true,
            value = value,
            readOnly = readOnly,
            enabled = enabled,
            textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onBackground),
            keyboardOptions = if (isLastItem) {
                keyboardOptions
            } else {
                keyboardOptions.copy(
                    imeAction = ImeAction.Next,
                    autoCorrect = autoCorrect
                )
            },
            keyboardActions = if (isLastItem) {
                keyboardActions
            } else {
                KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            },
            onValueChange = { onValueChange?.invoke(it) },
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            modifier = modifier
                .padding(bottom = 4.dp)
                .fillMaxWidth()
                .onFocusEvent {
                    if (it.isFocused) {
                        coroutineScope.launch {
                            bringIntoViewRequester.bringIntoView()
                        }
                    }
                },
            label = { Text(label) }
        )
    }
}