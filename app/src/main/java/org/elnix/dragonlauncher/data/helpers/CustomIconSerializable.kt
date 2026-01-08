package org.elnix.dragonlauncher.data.helpers

import com.google.gson.annotations.SerializedName

/**
 * Describes a highly customizable icon that can be rendered from multiple sources
 * (vector, bitmap, text, or procedural shape) with advanced visual controls.
 *
 * This model is renderer-agnostic and supports extreme theming and animation use cases.
 */
data class CustomIconSerializable(

    /** Icon source type determining how `source` is interpreted. */
    @SerializedName("a")
    var type: IconType? = null,

    /**
     * Icon source reference.
     * - BITMAP: base64-encoded image
     * - ICON_PACK: base64-encoded image
     * - TEXT: emoji or glyph
     * - SHAPE: renderer-defined primitive
     */
    @SerializedName("b")
    var source: String? = null,

    /** Tint color (ARGB) applied after rendering. */
    @SerializedName("c")
    var tint: Long? = null,

    /** Icon opacity multiplier (0.0 – 1.0). */
    @SerializedName("d")
    var opacity: Float? = null,

    /** Explicit icon size in dp. */
    @SerializedName("e")
    var sizeDp: Float? = null,

    /** Per-corner radius override for icon clipping. */
    @SerializedName("f")
    var corners: CornerRadiusSerializable? = null,

    /** Stroke width (dp) around the icon shape. */
    @SerializedName("g")
    var strokeWidth: Float? = null,

    /** Stroke color (ARGB) around the icon. */
    @SerializedName("h")
    var strokeColor: Long? = null,

    /** Blur radius for icon shadow. */
    @SerializedName("i")
    var shadowRadius: Float? = null,

    /** Shadow color (ARGB). */
    @SerializedName("j")
    var shadowColor: Long? = null,

    /** Horizontal shadow offset (dp). */
    @SerializedName("k")
    var shadowOffsetX: Float? = null,

    /** Vertical shadow offset (dp). */
    @SerializedName("l")
    var shadowOffsetY: Float? = null,

    /** Rotation applied to the icon in degrees. */
    @SerializedName("m")
    var rotationDeg: Float? = null,

    /** Horizontal scale multiplier. */
    @SerializedName("n")
    var scaleX: Float? = null,

    /** Vertical scale multiplier. */
    @SerializedName("o")
    var scaleY: Float? = null,

    /** Optional blend mode name (renderer-defined, e.g. SRC_IN, MULTIPLY). */
    @SerializedName("p")
    var blendMode: String? = null,

    /** Whether this icon supports animation (Lottie, animated vector, etc.). */
    @SerializedName("q")
    var animated: Boolean? = null
)

/**
 * Defines how a custom icon should be interpreted and rendered.
 */
enum class IconType {

    /** Icon sourced from an installed icon pack. */
    ICON_PACK,

    /** icon sourced from and image (PNG, JPG, WEBP). */
    BITMAP,

    /** Text-based icon (emoji, glyph, font icon). */
    TEXT,

    /** Procedural or primitive shape rendered by code. */
    SHAPE
}


/**
 * Defines independent corner radii for a rectangular shape.
 *
 * Any null value falls back to renderer defaults or global radius.
 */
data class CornerRadiusSerializable(
    @SerializedName("a") var topLeft: Float? = null,
    @SerializedName("b") var topRight: Float? = null,
    @SerializedName("c") var bottomRight: Float? = null,
    @SerializedName("d") var bottomLeft: Float? = null
)
