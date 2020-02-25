package com.sma6871.cardentry

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import java.util.*


class CardEntry : AppCompatEditText {

    var maxLength = 16 // default length
    var partCount = 4 // AAAA BBBB CCCC DDDD

    private var mCharSize: Float = 0f
    private val spaceSize
        get() = getCharSize(" ")
    private var spaceCount = 0
    private val mSpace: Float by lazy { spaceCount * spaceSize }

    private val mPartLength
        get() = maxLength / partCount

    private val mPartSize: Float by lazy { mCharSize * mPartLength }

    private var mLineSpacing = toPxF(12)
    private var mLineSpacingAnimated = toPxF(12)


    private var textWidths = floatArrayOf()


    var hasAnimation = false
    var hasLine = true
    private var isAnimating = false
    private var animatedAlpha = 255


    var linePaint: Paint = Paint().apply {
        isAntiAlias = true
        color = getColor(R.color.silverGray)
        style = Paint.Style.FILL
    }


    var lineColor = getColor(R.color.silverGray)
    var selectionColor = getColor(R.color.selection)
    var filledLineColor = getColor(R.color.green)

    var selectionRect = Rect()
    var selectionPaint = Paint()

    val rawText: String
        get() = text?.replace(Regex(" "), "") ?: ""
    val spaces: String
        get() = " ".repeat(spaceCount)
    val chunkedText: String
        get() {
            return rawText.getChunked()
        }
    var oldText = ""

    private val partNumbersCount:Int by lazy {
        maxLength/partCount
    }

    private fun String.getChunked(): String {
        return replace(" ", "").chunked(4).joinToString(separator = spaces)
    }

    fun onPinChange(onChange: (isComplete: Boolean, length: Int) -> Unit) {
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (oldText != rawText)
                    onChange(rawText.length == maxLength, rawText.length)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
    }
    /**
     * Call this method to get raw text (without spaces)
     * */
    fun onNumberChange(onChange: (number: String) -> Unit) {
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (oldText != rawText)
                    onChange(rawText)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
    }

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CardEntry, 0, 0)

        if (typedArray.hasValue(R.styleable.CardEntry_ce_number_count)) {
            maxLength = typedArray.getInt(R.styleable.CardEntry_ce_number_count, 16)
            textWidths = FloatArray(maxLength)
        }
        if (typedArray.hasValue(R.styleable.CardEntry_ce_part_count)) {
            partCount = typedArray.getInt(R.styleable.CardEntry_ce_part_count, 4)
        }

        if (typedArray.hasValue(R.styleable.CardEntry_ce_line_color)) {
            lineColor = typedArray.getColor(
                    R.styleable.CardEntry_ce_line_color,
                    ContextCompat.getColor(context, R.color.silverGray)
            )
        }
        if (typedArray.hasValue(R.styleable.CardEntry_ce_selection_color)) {
            selectionColor = typedArray.getColor(
                    R.styleable.CardEntry_ce_selection_color,
                    ContextCompat.getColor(context, R.color.selection)
            )
        }

        if (typedArray.hasValue(R.styleable.CardEntry_ce_filled_line_color))
            filledLineColor = typedArray.getInt(
                    R.styleable.CardEntry_ce_filled_line_color,
                    ContextCompat.getColor(context, R.color.coal)
            )

        if (typedArray.hasValue(R.styleable.CardEntry_ce_has_animation))
            hasAnimation = typedArray.getBoolean(R.styleable.CardEntry_ce_has_animation, false)

        if (typedArray.hasValue(R.styleable.CardEntry_ce_show_lines))
            hasLine = typedArray.getBoolean(R.styleable.CardEntry_ce_show_lines, true)

        if (typedArray.hasValue(R.styleable.CardEntry_ce_digit_width))
            mCharSize = typedArray.getDimension(R.styleable.CardEntry_ce_digit_width, mCharSize)

        if (mCharSize == 0f) {
            mCharSize = getCharSize("8")
        }


        if (typedArray.hasValue(R.styleable.CardEntry_ce_parts_space))
            spaceCount = typedArray.getInt(R.styleable.CardEntry_ce_parts_space, spaceCount)


        if (typedArray.hasValue(R.styleable.CardEntry_ce_digit_line_spacing)) {
            mLineSpacingAnimated = typedArray.getDimension(R.styleable.CardEntry_ce_digit_line_spacing, toPxF(12))
            mLineSpacing = typedArray.getDimension(R.styleable.CardEntry_ce_digit_line_spacing, toPxF(12))
        }


        typedArray.recycle()

        textWidths = FloatArray(maxLength + (partCount * spaceCount - 1))

        setBackgroundResource(0)
        inputType = InputType.TYPE_CLASS_NUMBER
        keyListener = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DigitsKeyListener.getInstance(Locale.US)
        } else {
            DigitsKeyListener.getInstance()
        }

        val lengthFilter = InputFilter.LengthFilter(maxLength + (spaces.length * 3))
        filters = arrayOf(lengthFilter, CardInputFilter(spaces, maxLength))


        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                oldText = s.toString()
                if (hasAnimation) {
                    if (start == s!!.length && !isAnimating) {
                        animate1()
                    }
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        setOnTouchListener { v, event ->
            val newText = chunkedText
            if (event.action == MotionEvent.ACTION_DOWN && oldText != newText) {
                setTextKeepState(newText)
                setSelection(length())
            }
            false
        }
        text = text // for add spaces at start

        mLineSpacingAnimated = if (hasAnimation) 0f else mLineSpacing

    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            if (selectionStart == selectionEnd && selectionStart > maxLength / partCount) {
                if (text?.get(selectionStart - 1) == ' ') {
                    val startIndex = selectionStart - spaceCount
                    setText(text?.removeRange(startIndex, selectionStart).toString().getChunked())
                    if (startIndex >= 0)
                        setSelection(startIndex)
                    return true
                }
            }
        }
        return super.onKeyUp(keyCode, event)
    }

    private fun getCharSize(s: String): Float {
        return paint.measureText(s)
    }

    override fun onTextContextMenuItem(id: Int): Boolean {
        when (id) {
            android.R.id.copy -> copySelectedNumberWithoutSpaces()
            android.R.id.paste -> pasteNumbers()
            else -> return super.onTextContextMenuItem(id)
        }
        return true
    }

    private fun copySelectedNumberWithoutSpaces() {
        val clipboardManager = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val copiedText = text?.substring(selectionStart until selectionEnd)?.replace(" ", "")
        clipboardManager.setPrimaryClip(ClipData.newPlainText(copiedText, copiedText))
    }

    private fun pasteNumbers() {
        val clipboardManager = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = clipboardManager.primaryClip
        setText(clipData?.getItemAt(0)?.text?.onlyNumber())
    }

    override fun onDraw(canvas: Canvas) {
        //super.onDraw(canvas)

        setWillNotDraw(false)
        if (paint.color != textColors.defaultColor)
            paint.color = textColors.defaultColor
        var startX = paddingLeft
        val top = height - paddingBottom

        val charSequence = rawText
        val textLength = charSequence.length
        paint.getTextWidths(charSequence, 0, textLength, textWidths)

        //draw lines
        var i = 0
        if (hasLine) {
            while (i < partCount) {
                linePaint.color = when {
                    i < textLength / mPartLength -> filledLineColor
                    else -> lineColor
                }
                canvas.drawRect(
                        startX.toFloat(),
                        top.toFloat() + 0,
                        startX + mPartSize,
                        (top + toPxF(2)),
                        linePaint
                )

                startX += (mPartSize + mSpace).toInt()
                i++
            }
        }

        //draw characters
        startX = paddingLeft
        i = 0
        if (!hasAnimation) {
            while (i < textLength) {
                val middle = startX + mCharSize / 2
                drawNumber(canvas, charSequence, i, middle, top, false)

                startX += if (i % mPartLength == mPartLength - 1)
                    (mCharSize + mSpace).toInt()
                else (mCharSize).toInt()
                i++
            }
        } else {//last character must be animate

            for (k in 0 until textLength) {
                val middle = startX + mCharSize / 2
                if ((k < textLength - 1)) {
                    drawNumber(canvas, charSequence, k, middle, top, false)
                    startX += if (k % mPartLength == mPartLength - 1)
                        (mCharSize + mSpace).toInt()
                    else (mCharSize).toInt()
                } else {
                    drawNumber(canvas, charSequence, k, middle, top, true)
                }

            }

        }
    }


    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        setNonSpaceSelected(selStart, selEnd)
    }

    private fun setNonSpaceSelected(selStart: Int, selEnd: Int) {
        if (text?.getOrNull(selStart) == ' ' || text?.getOrNull(selEnd) == ' ') {
            val start = text?.substring(0 until selStart)?.indexOfLast { it.isDigit() } ?: 0
            val stop = text?.substring(0 until selEnd)?.indexOfLast { it.isDigit() } ?: 0
            setSelection(start + 1.coerceIn(0, text?.length), stop + 1.coerceIn(0, text?.length))
        }
    }

    private fun drawNumber(canvas: Canvas, text: CharSequence, i: Int, middle: Float, top: Int, animated: Boolean) {
        val diff = spaceCount * (selectionStart / (spaceCount + partNumbersCount))
        val diffEnd = spaceCount * (selectionEnd / (spaceCount + partNumbersCount))
        val isSelected = i in selectionStart - diff until selectionEnd - diffEnd
        if (animated) {
            paint.alpha = animatedAlpha
        } else {
            paint.alpha = 255
        }
        //TODO: draw selected background
        paint.color = if (isSelected)
            selectionColor
        else
            textColors.defaultColor
        canvas.drawText(
                text,
                i,
                i + 1,
                middle - textWidths[i] / 2,
                top - if (animated) mLineSpacingAnimated else mLineSpacing,
                paint
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        setMeasuredDimension(
                (maxLength * mCharSize).toInt() + ((partCount - 1) * mSpace).toInt() + paddingLeft + paddingRight,
                measuredHeight
        )

    }


    private fun animate1() {
        val valueAnimator = ValueAnimator.ofFloat(0F, mLineSpacing)
        valueAnimator.duration = 200
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()
        valueAnimator.addUpdateListener {
            mLineSpacingAnimated = it.animatedValue as Float
            animatedAlpha = ((it.animatedValue as Float) / mLineSpacing * 255).toInt()
            postInvalidate()
        }
        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                // mLineSpacingAnimated = 0F
                isAnimating = false
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
                isAnimating = true
            }

        })
        valueAnimator.start()
    }


}