package dev.carlosivis.workoutsmart.screens.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.carlosivis.workoutsmart.Utils.FontSizes
import dev.carlosivis.workoutsmart.Utils.format

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimeWheelPicker(
    range: IntRange,
    initialValue: Int,
    onValueChange: (Int) -> Unit
) {
    val count = range.count()
    val startIndex = Int.MAX_VALUE / 2 - (Int.MAX_VALUE / 2 % count) + initialValue
    val lazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = startIndex - 2
    )
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState)

    val selectedIndex by remember {
        derivedStateOf {
            val layoutInfo = lazyListState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            if (visibleItemsInfo.isEmpty()) {
                -1
            } else {
                val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
                val centerItem = visibleItemsInfo.minByOrNull {
                    kotlin.math.abs((it.offset + it.size / 2) - viewportCenter)
                }
                centerItem?.index ?: -1
            }
        }
    }

    LaunchedEffect(selectedIndex) {
        if (selectedIndex != -1) {
            onValueChange(range.start + (selectedIndex % count))
        }
    }

    LazyColumn(
        state = lazyListState,
        flingBehavior = snapFlingBehavior,
        modifier = Modifier.size(100.dp, 150.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(Int.MAX_VALUE) { index ->
            val value = range.start + (index % count)
            val isSelected = index == selectedIndex
            Text(
                text = value.format(),
                fontSize = if (isSelected) FontSizes.TitleLarge else FontSizes.TitleMedium,
                color = if (isSelected) Color.Black else Color.Gray,
                modifier = Modifier
                    .padding(8.dp)
            )
        }
    }
}
