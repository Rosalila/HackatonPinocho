package rosalila.studio.hackatonpinocho;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
<<<<<<< HEAD
=======
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
>>>>>>> 6c2597b6b1b1dc730c0eaf020ff8d265e7639da5
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
<<<<<<< HEAD
=======
import org.andengine.input.touch.TouchEvent;
>>>>>>> 6c2597b6b1b1dc730c0eaf020ff8d265e7639da5
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
<<<<<<< HEAD
public class FightActivity extends SimpleBaseGameActivity {
=======
public class FightActivity extends SimpleBaseGameActivity implements IOnAreaTouchListener {
>>>>>>> 6c2597b6b1b1dc730c0eaf020ff8d265e7639da5
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 1280;
	private static final int CAMERA_HEIGHT = 720;

	private static final float DEMO_VELOCITY = 100.0f;
<<<<<<< HEAD
	
	Sprite background;
=======
>>>>>>> 6c2597b6b1b1dc730c0eaf020ff8d265e7639da5

	// ===========================================================
	// Fields
	// ===========================================================

	private BitmapTextureAtlas mBitmapTextureAtlas;
	private TiledTextureRegion mFaceTextureRegion;
<<<<<<< HEAD
=======
	
	private BitmapTextureAtlas bgBitmapTextureAtlas;
	private TiledTextureRegion bgFaceTextureRegion;
	
	Player player1,player2;
>>>>>>> 6c2597b6b1b1dc730c0eaf020ff8d265e7639da5

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
		final Camera camera = new Camera(0, 0, FightActivity.CAMERA_WIDTH, FightActivity.CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(FightActivity.CAMERA_WIDTH, FightActivity.CAMERA_HEIGHT), camera);
	}

	@Override
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 64, 32, TextureOptions.BILINEAR);
		this.mFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "face_circle_tiled.png", 0, 0, 2, 1);
		this.mBitmapTextureAtlas.load();
<<<<<<< HEAD
=======
		
		this.bgBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 1280, 720, TextureOptions.BILINEAR);
		this.bgFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.bgBitmapTextureAtlas, this, "background.png", 0, 0, 1, 1);
		this.bgBitmapTextureAtlas.load();
>>>>>>> 6c2597b6b1b1dc730c0eaf020ff8d265e7639da5
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene();
		scene.setBackground(new Background(0.09804f, 0.6274f, 0.8784f));

		final float centerX = (FightActivity.CAMERA_WIDTH - this.mFaceTextureRegion.getWidth()) / 2;
		final float centerY = (FightActivity.CAMERA_HEIGHT - this.mFaceTextureRegion.getHeight()) / 2;
<<<<<<< HEAD
		final Ball ball = new Ball(centerX, centerY, this.mFaceTextureRegion, this.getVertexBufferObjectManager());

		scene.attachChild(ball);
=======
		
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
>>>>>>> 6c2597b6b1b1dc730c0eaf020ff8d265e7639da5

		return scene;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

<<<<<<< HEAD
	private static class Ball extends AnimatedSprite {
		private final PhysicsHandler mPhysicsHandler;

		public Ball(final float pX, final float pY, final TiledTextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager) {
=======
	private static class Player extends AnimatedSprite
	{
		private final PhysicsHandler mPhysicsHandler;
		
		String state;

		public Player(final float pX, final float pY, final TiledTextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager) {
>>>>>>> 6c2597b6b1b1dc730c0eaf020ff8d265e7639da5
			super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
			this.mPhysicsHandler = new PhysicsHandler(this);
			this.registerUpdateHandler(this.mPhysicsHandler);
			this.mPhysicsHandler.setVelocity(FightActivity.DEMO_VELOCITY, FightActivity.DEMO_VELOCITY);
<<<<<<< HEAD
		}

		@Override
		protected void onManagedUpdate(final float pSecondsElapsed) {
			if(this.mX < 0) {
				this.mPhysicsHandler.setVelocityX(FightActivity.DEMO_VELOCITY);
			} else if(this.mX + this.getWidth() > FightActivity.CAMERA_WIDTH) {
				this.mPhysicsHandler.setVelocityX(-FightActivity.DEMO_VELOCITY);
			}

			if(this.mY < 0) {
				this.mPhysicsHandler.setVelocityY(FightActivity.DEMO_VELOCITY);
			} else if(this.mY + this.getHeight() > FightActivity.CAMERA_HEIGHT) {
				this.mPhysicsHandler.setVelocityY(-FightActivity.DEMO_VELOCITY);
=======
			this.state="idle";
		}

		@Override
		protected void onManagedUpdate(final float pSecondsElapsed)
		{
			if(this.getY()>650)
			{
				this.state="idle";
				this.setX(650);
			}
			if(state!="idle")
			{
				this.mPhysicsHandler.setVelocityX(10);
				this.mPhysicsHandler.setVelocityY(100);
//				if(this.mX < 0) {
//					this.mPhysicsHandler.setVelocityX(FightActivity.DEMO_VELOCITY);
//				} else if(this.mX + this.getWidth() > FightActivity.CAMERA_WIDTH) {
//					this.mPhysicsHandler.setVelocityX(-FightActivity.DEMO_VELOCITY);
//				}
//	
//				if(this.mY < 0) {
//					this.mPhysicsHandler.setVelocityY(FightActivity.DEMO_VELOCITY);
//				} else if(this.mY + this.getHeight() > FightActivity.CAMERA_HEIGHT) {
//					this.mPhysicsHandler.setVelocityY(-FightActivity.DEMO_VELOCITY);
//				}
			}else if(state=="idle")
			{
				this.mPhysicsHandler.setVelocityX(0);
				this.mPhysicsHandler.setVelocityY(0);
			}

			super.onManagedUpdate(pSecondsElapsed);
		}
		
		public void touched()
		{
			this.state="jump";
		}
	}
	
	
	private static class ButtonKick extends AnimatedSprite {
		private final PhysicsHandler mPhysicsHandler;
		
		String state;
		Player player;

		public ButtonKick(final float pX, final float pY, final TiledTextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager,Player player) {
			super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
			this.mPhysicsHandler = new PhysicsHandler(this);
			this.registerUpdateHandler(this.mPhysicsHandler);
			this.mPhysicsHandler.setVelocity(FightActivity.DEMO_VELOCITY, FightActivity.DEMO_VELOCITY);
			this.state="idle";
			this.player=player;
		}

		@Override
		protected void onManagedUpdate(final float pSecondsElapsed) {
			if(state!="idle")
			{
				if(this.mX < 0) {
					this.mPhysicsHandler.setVelocityX(FightActivity.DEMO_VELOCITY);
				} else if(this.mX + this.getWidth() > FightActivity.CAMERA_WIDTH) {
					this.mPhysicsHandler.setVelocityX(-FightActivity.DEMO_VELOCITY);
				}
	
				if(this.mY < 0) {
					this.mPhysicsHandler.setVelocityY(FightActivity.DEMO_VELOCITY);
				} else if(this.mY + this.getHeight() > FightActivity.CAMERA_HEIGHT) {
					this.mPhysicsHandler.setVelocityY(-FightActivity.DEMO_VELOCITY);
				}
			}else if(state=="idle")
			{
				this.mPhysicsHandler.setVelocityX(0);
				this.mPhysicsHandler.setVelocityY(0);
>>>>>>> 6c2597b6b1b1dc730c0eaf020ff8d265e7639da5
			}

			super.onManagedUpdate(pSecondsElapsed);
		}
<<<<<<< HEAD
=======
		
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
>>>>>>> 6c2597b6b1b1dc730c0eaf020ff8d265e7639da5
	}
}