package com.uden.tau.main

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp

enum class TableAlignment {
    Left,
    Center,
    Right,
}

@Composable
fun Table(
    modifier: Modifier = Modifier,
    rowModifier: ((rowIndex: Int) -> Modifier) =  { Modifier },
    verticalLazyListState: LazyListState = rememberLazyListState(),
    horizontalScrollState: ScrollState = rememberScrollState(),
    columnCount: Int,
    rowCount: Int,
    beforeRow: (@Composable (rowIndex: Int) -> Unit)? = null,
    afterRow: (@Composable (rowIndex: Int) -> Unit)? = null,
    cellContent: @Composable (columnIndex: Int, rowIndex: Int) -> Unit,
    alignments: List<TableAlignment> = listOf(),
    onRowClick: ((rowIndex: Int) -> Unit) = {},
) {
    val columnWidths = remember { mutableStateMapOf<Int, Int>() }
    val rowHeights = remember { mutableStateMapOf<Int, Int>() }

    Box(modifier = modifier.then(Modifier.horizontalScroll(horizontalScrollState))) {
        LazyColumn(state = verticalLazyListState) {
            items(rowCount) { rowIndex ->
                Column {
                    beforeRow?.invoke(rowIndex)

                    Row(modifier = rowModifier(rowIndex).clickable { onRowClick(rowIndex) }) {
                        (0 until columnCount).forEach { columnIndex ->
                            Box(modifier = Modifier.layout { measurable, constraints ->
                                val placeable = measurable.measure(constraints)

                                val existingWidth =
                                    columnWidths[columnIndex] ?: 0
                                val maxWidth =
                                    maxOf(existingWidth, placeable.width)
                                val existingHeight = rowHeights[rowIndex] ?: 0
                                val maxHeight = maxOf(existingHeight, placeable.height)

                                if (maxWidth > existingWidth) {
                                    columnWidths[columnIndex] = maxWidth
                                }
                                if (maxHeight > existingHeight) {
                                    rowHeights[rowIndex] = maxHeight
                                }

                                layout(
                                    width = maxWidth,
                                    height = maxHeight,
                                ) {
                                    placeable.placeRelative(
                                        when (alignments.elementAtOrNull(
                                            columnIndex
                                        ) ?: TableAlignment.Left) {
                                            TableAlignment.Left -> 0
                                            TableAlignment.Center -> (maxWidth - placeable.width) / 2
                                            TableAlignment.Right -> maxWidth - placeable.width
                                        },
                                        0,
                                    )
                                }
                            }) {
                                cellContent(columnIndex, rowIndex)
                            }
                        }
                    }

                    afterRow?.invoke(rowIndex)
                }
            }
        }
    }
}