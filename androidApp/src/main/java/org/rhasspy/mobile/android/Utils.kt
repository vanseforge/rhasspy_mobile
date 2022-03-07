package org.rhasspy.mobile.android

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Switch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.launch
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.settings.Setting
import org.rhasspy.mobile.data.DataEnum

val lang = mutableStateOf(StringDesc.localeType)

@Composable
fun Text(
    resource: StringResource,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {
    Text(
        translate(resource),
        modifier,
        color,
        fontSize,
        fontStyle,
        fontWeight,
        fontFamily,
        letterSpacing,
        textDecoration,
        textAlign,
        lineHeight,
        overflow,
        softWrap,
        maxLines,
        onTextLayout,
        style
    )
}

@Composable
fun Icon(
    imageVector: ImageVector,
    contentDescription: StringResource,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Icon(
        painter = rememberVectorPainter(imageVector),
        contentDescription = translate(contentDescription),
        modifier = modifier,
        tint = tint
    )
}

@Composable
fun Icon(
    painter: Painter,
    contentDescription: StringResource,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Icon(
        painter = painter,
        contentDescription = translate(contentDescription),
        modifier = modifier,
        tint = tint
    )
}

@Composable
fun translate(resource: StringResource): String {
    lang.value
    return StringDesc.Resource(resource).toString(LocalContext.current)
}

//https://stackoverflow.com/questions/68389802/how-to-clear-textfield-focus-when-closing-the-keyboard-and-prevent-two-back-pres
fun Modifier.clearFocusOnKeyboardDismiss(): Modifier = composed {
    var isFocused by remember { mutableStateOf(false) }
    var keyboardAppearedSinceLastFocused by remember { mutableStateOf(false) }
    if (isFocused) {
        val imeIsVisible = LocalWindowInsets.current.ime.isVisible
        val focusManager = LocalFocusManager.current
        LaunchedEffect(imeIsVisible) {
            if (imeIsVisible) {
                keyboardAppearedSinceLastFocused = true
            } else if (keyboardAppearedSinceLastFocused) {
                focusManager.clearFocus()
            }
        }
    }
    onFocusEvent {
        if (isFocused != it.isFocused) {
            isFocused = it.isFocused
            if (isFocused) {
                keyboardAppearedSinceLastFocused = false
            }
        }
    }
}

@Composable
fun IndicatedSmallIcon(isIndicated: Boolean, rotationTarget: Float = 180f, icon: @Composable (modifier: Modifier) -> Unit) {
    val rotation = animateFloatAsState(
        targetValue = if (isIndicated) {
            rotationTarget
        } else 0f,
        animationSpec = tween(300)
    )
    val animationProgress: Float by animateFloatAsState(
        targetValue = if (isIndicated) 1f else 0f,
        animationSpec = tween(100)
    )
    Box(
        Modifier
            .background(
                color = NavigationBarItemDefaults.colors().indicatorColor.copy(alpha = animationProgress),
                shape = RoundedCornerShape(16.0.dp)
            )
            .padding(horizontal = 8.dp)
    ) {
        icon(Modifier.rotate(rotation.value))
    }
}

@Composable
fun <E : DataEnum> DropDownEnumListItem(selected: E, onSelect: (item: E) -> Unit, values: () -> Array<E>) {
    var isExpanded by remember { mutableStateOf(false) }

    ListElement(modifier = Modifier
        .clickable { isExpanded = true },
        text = { Text(selected.text) },
        trailing = {
            IndicatedSmallIcon(isExpanded) {
                Icon(
                    modifier = it,
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = MR.strings.expandDropDown,
                )
            }
        })

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopStart)
    ) {
        DropdownMenu(
            modifier = Modifier.fillMaxWidth(),
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }) {
            values().forEach {
                DropdownMenuItem(
                    text = { Text(it.text) },
                    onClick = { isExpanded = false; onSelect(it) })
            }
        }
    }
}

@Composable
fun ExpandableListItem(
    text: StringResource,
    secondaryText: StringResource? = null,
    expandedContent: @Composable () -> Unit
) {
    ExpandableListItemInternal(
        text = text,
        secondaryText = secondaryText?.let { { Text(secondaryText) } } ?: run { null },
        expandedContent = expandedContent
    )
}

@Composable
fun ExpandableListItemString(
    text: StringResource,
    secondaryText: String? = null,
    expandedContent: @Composable () -> Unit
) {
    ExpandableListItemInternal(
        text = text,
        secondaryText = secondaryText?.let { { Text(secondaryText) } } ?: run { null },
        expandedContent = expandedContent
    )
}


@Composable
private fun ExpandableListItemInternal(
    text: StringResource,
    secondaryText: (@Composable () -> Unit)?,
    expandedContent: @Composable () -> Unit
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    ListElement(
        modifier = Modifier
            .clickable { isExpanded = !isExpanded },
        text = { Text(text) },
        secondaryText = secondaryText,
        trailing = {
            IndicatedSmallIcon(isExpanded) {
                Icon(
                    modifier = it,
                    imageVector = Icons.Filled.ExpandMore,
                    contentDescription = MR.strings.expandListItem
                )
            }
        }
    )

    AnimatedVisibility(
        enter = expandVertically(),
        exit = shrinkVertically(),
        visible = isExpanded
    ) {
        Column(modifier = Modifier.padding(bottom = 8.dp)) {
            expandedContent()
        }
    }
}

@Composable
fun SwitchListItem(text: StringResource, secondaryText: StringResource? = null, isChecked: Boolean, onCheckedChange: ((Boolean) -> Unit)) {
    ListElement(modifier = Modifier
        .clickable { onCheckedChange(!isChecked) },
        text = { Text(text) },
        secondaryText = secondaryText?.let { { Text(secondaryText) } } ?: run { null },
        trailing = {
            Switch(
                checked = isChecked,
                onCheckedChange = null
            )
        })
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ListElement(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(vertical = 8.dp),
    icon: @Composable (() -> Unit)? = null,
    secondaryText: @Composable (() -> Unit)? = null,
    singleLineSecondaryText: Boolean = true,
    overlineText: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    text: @Composable () -> Unit
) {
    ListItem(
        modifier = modifier.padding(paddingValues),
        icon = icon,
        secondaryText = secondaryText,
        singleLineSecondaryText = singleLineSecondaryText,
        overlineText = overlineText,
        trailing = trailing,
        text = text
    )
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TextFieldListItem(
    label: StringResource,
    value: String,
    onValueChange: (String) -> Unit,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    paddingValues: PaddingValues = PaddingValues(vertical = 2.dp),
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    ListElement(
        modifier = Modifier.bringIntoViewRequester(bringIntoViewRequester),
        paddingValues = paddingValues
    ) {
        val coroutineScope = rememberCoroutineScope()

        OutlinedTextField(
            singleLine = true,
            value = value,
            onValueChange = onValueChange,
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            modifier = Modifier
                .fillMaxWidth()
                .clearFocusOnKeyboardDismiss()
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

@Composable
fun OutlineButtonListItem(text: StringResource, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .wrapContentSize(Alignment.Center)
    ) {
        OutlinedButton(onClick = onClick) {
            Text(text)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SliderListItem(text: StringResource, value: Float, onValueChange: (Float) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .wrapContentSize(Alignment.Center)
    ) {
        Text("${translate(text)} ($value)")

        Slider(
            modifier = Modifier.padding(top = 12.dp),
            value = value,
            onValueChange = onValueChange
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadioButtonListItem(text: StringResource, isChecked: Boolean, onClick: () -> Unit) {
    ListElement(modifier = Modifier.clickable { onClick() }) {
        Row {
            RadioButton(selected = isChecked, onClick = onClick)
            Text(text, modifier = Modifier.weight(1f))
        }
    }
}

fun Boolean.toText(): StringResource {
    return if (this) MR.strings.enabled else MR.strings.disabled
}

@Composable
fun <T> LiveData<T>.observe(): T {
    return this.ld().observeAsState(this.value).value
}

@Composable
fun <T> Setting<T>.observe(): T {
    return this.unsaved.ld().observeAsState(this.value).value
}

var <T> Setting<T>.data: T
    get() = this.unsaved.value
    set(newValue) {
        this.unsaved.value = newValue
    }