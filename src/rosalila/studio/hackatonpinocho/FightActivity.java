package rosalila.studio.hackatonpinocho;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;

import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;

import org.andengine.input.touch.TouchEvent;

import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga
 *
 * @author Nicolas Gramlich
 * @since 11:54:51 - 03.04.2010
 */

public class FightActivity extends SimpleBaseGameActivity implements IOnAreaTouchListener {

	// ===========================================================
	// Fields
	// ===========================================================

	private BitmapTextureAtlas mBitmapTextureAtlas;
	private TiledTextureRegion mFaceTextureRegion;

	
	private BitmapTextureAtlas bgBitmapTextureAtlas;
	private TiledTextureRegion bgFaceTextureRegion;
	
	Player player1,player2;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public EngineOptions onCreateEngineOptions() {
		final Camera camera = new Camera(0, 0, GameConstants.CAMERA_WIDTH, GameConstants.CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(GameConstants.CAMERA_WIDTH, GameConstants.CAMERA_HEIGHT), camera);
	}

	@Override
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 64, 32, TextureOptions.BILINEAR);
		this.mFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "face_circle_tiled.png", 0, 0, 2, 1);
		this.mBitmapTextureAtlas.load();

		
		this.bgBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 1280, 720, TextureOptions.BILINEAR);
		this.bgFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.bgBitmapTextureAtlas, this, "background.png", 0, 0, 1, 1);
		this.bgBitmapTextureAtlas.load();

	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene();
		scene.setBackground(new Background(0.09804f, 0.6274f, 0.8784f));

		final float centerX = (GameConstants.CAMERA_WIDTH - this.mFaceTextureRegion.getWidth()) / 2;
		final float centerY = (GameConstants.CAMERA_HEIGHT - this.mFaceTextureRegion.getHeight()) / 2;
		
		final Sprite background = new Sprite(0,0,this.bgFaceTextureRegion,this.getVertexBufferObjectManager());
		player1 = new Player(centerX, centerY, this.mFaceTextureRegion, this.getVertexBufferObjectManager());
		player2 = new Player(centerX, centerY+100, this.mFaceTextureRegion, this.getVertexBufferObjectManager());
		final ButtonKick btn_kick = new ButtonKick(0, 0, this.mFaceTextureRegion, this.getVertexBufferObjectManager(),player1);		
		

		scene.attachChild(background);
		scene.attachChild(player1);
		scene.attachChild(player2);
		scene.attachChild(btn_kick);
		
		scene.registerTouchArea(btn_kick);
		scene.setOnAreaTouchListener(this);

		return scene;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================


	
	
	
	private static class ButtonKick extends AnimatedSprite {
		private final PhysicsHandler mPhysicsHandler;
		
		String state;
		Player player;

		public ButtonKick(final float pX, final float pY, final TiledTextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager,Player player) {
			super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
			this.mPhysicsHandler = new PhysicsHandler(this);
			this.registerUpdateHandler(this.mPhysicsHandler);
			this.mPhysicsHandler.setVelocity(GameConstants.DEMO_VELOCITY, GameConstants.DEMO_VELOCITY);
			this.state="idle";
			this.player=player;
		}

		@Override
		protected void onManagedUpdate(final float pSecondsElapsed) {
			if(state!="idle")
			{
				if(this.mX < 0) {
					this.mPhysicsHandler.setVelocityX(GameConstants.DEMO_VELOCITY);
				} else if(this.mX + this.getWidth() > GameConstants.CAMERA_WIDTH) {
					this.mPhysicsHandler.setVelocityX(-GameConstants.DEMO_VELOCITY);
				}
	
				if(this.mY < 0) {
					this.mPhysicsHandler.setVelocityY(GameConstants.DEMO_VELOCITY);
				} else if(this.mY + this.getHeight() > GameConstants.CAMERA_HEIGHT) {
					this.mPhysicsHandler.setVelocityY(-GameConstants.DEMO_VELOCITY);
				}
			}else if(state=="idle")
			{
				this.mPhysicsHandler.setVelocityX(0);
				this.mPhysicsHandler.setVelocityY(0);

			}

			super.onManagedUpdate(pSecondsElapsed);
		}

		
		@Override
        public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
			player.touched();
			return mFlippedHorizontal;
		}
	}


	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			ITouchArea pTouchArea, float pTouchAreaLocalX,
			float pTouchAreaLocalY)
	{
		if(pTouchAreaLocalX<1280/2)
		{
			
		}
		return false;
	}
}