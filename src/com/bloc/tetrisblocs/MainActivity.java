package com.bloc.tetrisblocs;

import java.util.Random;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.util.Log;
import android.widget.Toast;


public class MainActivity extends SimpleBaseGameActivity {
	private static final int GRID_WIDTH = 10;
	private static final int GRID_HEIGHT = 16;
	private static final int BLOCK_SIZE = 32;
	private static final int CAMERA_WIDTH = BLOCK_SIZE * GRID_WIDTH;
	private static final int CAMERA_HEIGHT = BLOCK_SIZE * GRID_HEIGHT;
	private static final float MOVE_TIMER_THRESHOLD_SECS = 0.5f;
	private static final float MOVE_TOUCH_THRESHOLD = 50;
	private static final long TAP_TOUCH_THRESHOLD = 0;
	
	private BitmapTextureAtlas mBitmapTextureAtlas;
	private BitmapTextureAtlas mBlockTextureAtlas;
	private TextureRegion mBackgroundTextureRegion;
	private TextureRegion mBlockTextureRegion;
	
	private Random mRandom = new Random();
	private Sprite mCurrentPiece = null;
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		
		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	}

	@Override
	protected void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		
		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), CAMERA_WIDTH, CAMERA_HEIGHT, TextureOptions.BILINEAR);
		this.mBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "play_grid.png", 0, 0);
		this.mBitmapTextureAtlas.load();
		
		this.mBlockTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), BLOCK_SIZE, BLOCK_SIZE, TextureOptions.BILINEAR);
		this.mBlockTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBlockTextureAtlas, this, "block_blue.png", 0, 0);
		this.mBlockTextureAtlas.load();
	}

	@Override
	protected Scene onCreateScene() {
		final Scene scene = new Scene();
		scene.setBackground(new SpriteBackground(new Sprite(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, mBackgroundTextureRegion, this.getVertexBufferObjectManager())));
		scene.setTouchAreaBindingOnActionDownEnabled(true);
		scene.setTouchAreaBindingOnActionMoveEnabled(true);
		scene.setOnSceneTouchListener(new IOnSceneTouchListener() {
			private float originX = 0.0f;
			private long startTime = 0;
			
			@Override
			public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
				if ( pSceneTouchEvent.isActionDown() ) {
					originX = pSceneTouchEvent.getX();
					startTime = pSceneTouchEvent.getMotionEvent().getEventTime();
				} else if ( pSceneTouchEvent.isActionMove() ) {
					float deltaX = pSceneTouchEvent.getX() - originX;
					int movement = Math.round(deltaX / MOVE_TOUCH_THRESHOLD);
					
					if( mCurrentPiece != null && movement != 0 ) {
						mCurrentPiece.setPosition(mCurrentPiece.getX() + (BLOCK_SIZE * movement), mCurrentPiece.getY());
						originX += (movement * MOVE_TOUCH_THRESHOLD); // Move logical origin to prevent large jumps after move
					}
				} else if ( pSceneTouchEvent.isActionUp() ) {
					float deltaTime = pSceneTouchEvent.getMotionEvent().getEventTime() - startTime;
					if( deltaTime <= TAP_TOUCH_THRESHOLD ) {
						rotatePiece();
					}
				}
				
				return true;
			}
		});
		
		this.mEngine.registerUpdateHandler(new IUpdateHandler() {
			private double mSecondsElapsed = 0.0f;
			
			@Override
			public void onUpdate(float pSecondsElapsed) {
				mSecondsElapsed += pSecondsElapsed;
				
				while( mSecondsElapsed > MOVE_TIMER_THRESHOLD_SECS ) {
					// Check that piece is in play
					if ( mCurrentPiece == null ) {
						playNextPiece(scene);
					// Check if we can move down, if so, DO IT!
					} else if ( canMoveDown() ){
						movePieceDown();
					// If we can't move down, this timer trigger results in our block being "placed"
					} else {
						// Gray it out?
						float gray = 100f / 255f;
						mCurrentPiece.setColor(gray, gray, gray);
						mCurrentPiece = null;
					}
					
					mSecondsElapsed -= MOVE_TIMER_THRESHOLD_SECS;
				}
			}

			@Override
			public void reset() {
				Toast.makeText(MainActivity.this, "Reset received on handler", Toast.LENGTH_LONG).show();
			}
		});
		
		return scene;
	}
	
	private void playNextPiece(Scene scene) {
		Sprite s = new Sprite(BLOCK_SIZE * (GRID_WIDTH / 2), 0f, mBlockTextureRegion, MainActivity.this.getVertexBufferObjectManager());
		scene.attachChild(s);
		
		mCurrentPiece = s;
	}
	
	private int count = 0;
	private boolean canMoveDown() {
		++count;
		if( count > 13 ) {
			count = 0;
			return false;
		}
		return true;
	}
	
	private void movePieceDown() {
		mCurrentPiece.setPosition(mCurrentPiece.getX(), mCurrentPiece.getY() + BLOCK_SIZE);
	}
	
	private void rotatePiece() {
		
	}
}
