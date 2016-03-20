package com.ionarciso.doodleeditor.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * The doodle editor is a canvas where you can draw persimmons and stuff.
 *
 * Created by null on 19/03/2016.
 */
public class DoodleEditorView extends View implements View.OnTouchListener {

  // region + Class Constants
  private static final float TOUCH_TOLERANCE      = 4;
  private static final int   DEFAULT_STROKE_WIDTH = 2;
  // endregion
  // region - Instance variables
  public  boolean                      _isEraserActive;
  private Canvas                       _canvas;
  private Path                         _path;
  private Paint                        _paint;
  private ArrayList<Pair<Path, Paint>> _paths;
  private float                        _prevX;
  private float                        _prevY;

  // endregion
  // region - Constructors
  public DoodleEditorView(Context context) {
    super(context);
    _paths = new ArrayList<Pair<Path, Paint>>();
    onViewInit();
  }

  public DoodleEditorView(Context context, AttributeSet attrs) {
    super(context, attrs);
    _paths = new ArrayList<Pair<Path, Paint>>();
    onViewInit();
  }

  public DoodleEditorView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    _paths = new ArrayList<Pair<Path, Paint>>();
    onViewInit();
  }

  // endregion
  // region - Lifecycle
  @Override
  protected void onDraw(Canvas canvas) {
    for (Pair<Path, Paint> p : _paths) {
      canvas.drawPath(p.first, p.second);
    }
  }

  // endregion
  // region - Private methods
  private void onViewInit() {
    setFocusable(true);
    setFocusableInTouchMode(true);
    setBackgroundColor(Color.WHITE);
    setOnTouchListener(this);
    onCanvasInit();
  }

  private void onCanvasInit() {
    _canvas = new Canvas();
    initPaint();
    initPaths();
  }

  private void initPaint() {
    _paint = new Paint();
    _paint.setAntiAlias(true);
    _paint.setDither(true);
    _paint.setColor(Color.BLACK);
    _paint.setStyle(Paint.Style.STROKE);
    _paint.setStrokeJoin(Paint.Join.ROUND);
    _paint.setStrokeCap(Paint.Cap.ROUND);
    _paint.setStrokeWidth(DEFAULT_STROKE_WIDTH);
  }

  private void initPaths() {
    _path = new Path();
    Paint newPaint = new Paint(_paint);
    _paths.add(new Pair<Path, Paint>(_path, newPaint));
  }

  // endregion
  // region - OnTouchListener
  @Override
  public boolean onTouch(View v, MotionEvent event) {
    float x = event.getX();
    float y = event.getY();

    switch (event.getAction()) {
    case MotionEvent.ACTION_DOWN:
      onTouchStart(x, y);
      invalidate();
      break;
    case MotionEvent.ACTION_MOVE:
      onTouchMove(x, y);
      invalidate();
      break;
    case MotionEvent.ACTION_UP:
      onTouchUp();
      invalidate();
      break;
    }
    return true;
  }

  private void onTouchStart(final float x, final float y) {
    if (_isEraserActive) {
      _paint.setColor(Color.WHITE);
      Paint newPaint = new Paint(_paint); // Clones the mPaint object
      _paths.add(new Pair<Path, Paint>(_path, newPaint));
    } else {
      Paint newPaint = new Paint(_paint); // Clones the mPaint object
      _paths.add(new Pair<Path, Paint>(_path, newPaint));
    }
    _path.reset();
    _path.moveTo(x, y);
    _prevX = x;
    _prevY = y;
  }

  private void onTouchMove(final float x, final float y) {
    float dx = Math.abs(x - _prevX);
    float dy = Math.abs(y - _prevY);
    if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
      _path.quadTo(_prevX, _prevY, (x + _prevX) / 2, (y + _prevY) / 2);
      _prevX = x;
      _prevY = y;
    }
  }

  private void onTouchUp() {
    _path.lineTo(_prevX, _prevY);
    // commit the path to our offscreen
    _canvas.drawPath(_path, _paint);
    // kill this so we don't double draw
    _path = new Path();
    Paint newPaint = new Paint(_paint); // Clones the mPaint object
    _paths.add(new Pair<Path, Paint>(_path, newPaint));
  }

  // endregion
}
