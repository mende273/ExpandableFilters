package mende273.expandablefilters

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ExpandableFilters(
    filters: List<String>,
    onActiveFilterChanged: (String) -> Unit
) {
    var activeFilter by remember { mutableStateOf(filters.first()) }

    val coroutineScope = rememberCoroutineScope()
    val horizontalScrollState = rememberScrollState()

    var activeFilterExpanded by remember { mutableStateOf(false) }
    var inactiveFiltersExpanded by remember { mutableStateOf(false) }

    Row(modifier = Modifier.horizontalScroll(horizontalScrollState)) {
        ActiveItem(
            title = activeFilter,
            isExpanded = activeFilterExpanded,
            onUpdateItemExpanded = {
                coroutineScope.launch {
                    when (activeFilterExpanded) {
                        true -> {
                            inactiveFiltersExpanded = false
                            delay(250)
                            activeFilterExpanded = false
                        }

                        false -> {
                            activeFilterExpanded = true
                            delay(250)
                            inactiveFiltersExpanded = true
                        }
                    }
                }
            }
        )

        Spacer(modifier = Modifier.width(8.dp))

        AnimatedVisibility(
            visible = inactiveFiltersExpanded,
            enter = expandHorizontally(),
            exit = shrinkHorizontally()
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                filters.filter { it != activeFilter }.forEach {
                    InactiveItem(
                        title = it,
                        onClick = {
                            activeFilter = it
                            onActiveFilterChanged(it)
                            coroutineScope.launch {
                                horizontalScrollState.animateScrollTo(0)
                                inactiveFiltersExpanded = false
                                delay(250)
                                activeFilterExpanded = false
                            }
                        })
                }
            }
        }
    }
}

@Preview
@Composable
private fun ExpandableFiltersPreview() {
    ExpandableFilters(
        filters = listOf(
            "Archery",
            "Athletics",
            "Basketball",
            "Boxing",
            "Diving",
            "Football"
        ), onActiveFilterChanged = {})
}

@Composable
private fun ActiveItem(
    title: String,
    isExpanded: Boolean,
    textColor: Color = Color.White,
    background: Color = Color(0xFF0052B4),
    onUpdateItemExpanded: () -> Unit
) {
    val minHeight: Dp = 35.dp
    val maxHeight: Dp = 50.dp

    val height = animateDpAsState(
        animationSpec = keyframes {
            this.durationMillis = 200
            this.delayMillis = if (isExpanded) 0 else 450
        },
        targetValue = when (isExpanded) {
            true -> maxHeight
            false -> minHeight
        }, label = ""
    )

    RoundedBox(
        onClick = onUpdateItemExpanded,
        height = height.value,
        background = background,
        paddingValues = PaddingValues(horizontal = 8.dp, vertical = 5.dp)
    ) {
        Row(
            modifier = Modifier.animateContentSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ImageIcon(res = R.drawable.baseline_filter_list_24, tint = Color.White)

            Text(text = title, color = textColor)

            Spacer(modifier = Modifier.width(16.dp))

            AnimatedVisibility(visible = !isExpanded) {
                ImageIcon(res = R.drawable.baseline_arrow_right_24, tint = Color.White)
            }
        }
    }
}

@Preview
@Composable
private fun ActiveItemPreview(
    @PreviewParameter(ExpandedPreviewParameter::class) isExpanded: Boolean
) {
    ActiveItem(title = "Active Item", isExpanded = isExpanded) {}
}

@Composable
private fun ImageIcon(
    @DrawableRes res: Int,
    tint: Color,
    contentDescription: String? = null
) {
    Icon(
        modifier = Modifier.height(24.dp),
        painter = painterResource(id = res),
        contentDescription = contentDescription,
        tint = tint
    )
}

@Composable
private fun InactiveItem(
    title: String,
    textColor: Color = Color.Black,
    background: Color = Color(color = 0xFFE7E7E8),
    onClick: () -> Unit
) {
    RoundedBox(
        onClick = onClick,
        height = 50.dp,
        background = background,
        paddingValues = PaddingValues(12.dp)
    ) {
        Text(
            text = title,
            color = textColor
        )
    }
}

@Preview
@Composable
private fun InactiveItemPreview() {
    InactiveItem(title = "Inactive item") {}
}

@Composable
private fun RoundedBox(
    onClick: () -> Unit,
    minWidth: Dp = 80.dp,
    height: Dp,
    background: Color,
    paddingValues: PaddingValues,
    contents: @Composable () -> Unit
) {
    Box(modifier = Modifier
        .clip(RoundedCornerShape(16f))
        .clickable { onClick() }
        .background(color = background)
        .widthIn(min = minWidth)
        .height(height)
        .padding(paddingValues),
        contentAlignment = Alignment.Center) {
        contents()
    }
}

@Preview
@Composable
private fun RoundedBoxPreview() {
    RoundedBox(onClick = {},
        height = 50.dp,
        background = Color(color = 0xFFE7E7E8),
        paddingValues = PaddingValues(12.dp),
        contents = {}
    )
}

private class ExpandedPreviewParameter : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean> = sequenceOf(
        false,
        true,
    )
}