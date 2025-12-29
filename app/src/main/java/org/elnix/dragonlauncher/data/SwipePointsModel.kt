package org.elnix.dragonlauncher.data

data class UiCircle(
    val id: Int,
    var radius: Float,
//    val points: SnapshotStateList<UiSwipePoint>
)

data class UiSwipePoint(
    val id: String,
    var angleDeg: Double,
    var action: SwipeActionSerializable,
    var circleNumber: Int,
    val nestId: Int
)
