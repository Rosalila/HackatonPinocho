package rosalila.studio.hackatonpinocho;

import java.util.ArrayList;

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

import tv.ouya.console.api.OuyaController;
import android.view.KeyEvent;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga
 *
 * @author Nicolas Gramlich
 * @since 11:54:51 - 03.04.2010
 */
public class FightActivity extends SimpleBaseGameActivity implements IOnAreaTouchListener {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 1280;
	private static final int CAMERA_HEIGHT = 720;

	private static final float DEMO_VELOCITY = 100.0f;

	// ===========================================================
	// Fields
	// ===========================================================

	private BitmapTextureAtlas mBitmapTextureAtlas;
	private TiledTextureRegion mFaceTextureRegion;
	
	private BitmapTextureAtlas bgBitmapTextureAtlas;
	private TiledTextureRegion bgFaceTextureRegion;
	
	private BitmapTextureAtlas victoryBitmapTextureAtlas;
	private TiledTextureRegion victoryFaceTextureRegion;
	
	Player player1,player2;
	
	ArrayList<Sprite> victories_player1;
	ArrayList<Sprite> victories_player2;

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
		
		this.bgBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 1280, 720, TextureOptions.BILINEAR);
		this.bgFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.bgBitmapTextureAtlas, this, "background.png", 0, 0, 1, 1);
		this.bgBitmapTextureAtlas.load();
		
		this.victoryBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 40, 60, TextureOptions.BILINEAR);
		this.victoryFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.victoryBitmapTextureAtlas, this, "victory.png", 0, 0, 1, 1);
		this.victoryBitmapTextureAtlas.load();
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene();
		scene.setBackground(new Background(0.09804f, 0.6274f, 0.8784f));

		final float centerX = (FightActivity.CAMERA_WIDTH - this.mFaceTextureRegion.getWidth()) / 2;
		final float centerY = (FightActivity.CAMERA_HEIGHT - this.mFaceTextureRegion.getHeight()) / 2;
		
		final Sprite background = new Sprite(0,0,this.bgFaceTextureRegion,this.getVertexBufferObjectManager());
		player1 = new Player(centerX, centerY, this.mFaceTextureRegion, this.getVertexBufferObjectManager());
		player2 = new Player(centerX+200, centerY, this.mFaceTextureRegion, this.getVertexBufferObjectManager());
		final ButtonKick btn_kick1 = new ButtonKick(0, 0, this.mFaceTextureRegion, this.getVertexBufferObjectManager(),player1);
		final ButtonKick btn_kick2 = new ButtonKick(100, 0, this.mFaceTextureRegion, this.getVertexBufferObjectManager(),player2);
		
		victories_player1=new ArrayList<Sprite>();
		victories_player2=new ArrayList<Sprite>();
		
		for(int i=0;i<5;i++)
		{
			victories_player1.add(new Sprite(1280/2-60-i*50,50,this.victoryFaceTextureRegion, this.getVertexBufferObjectManager()));
		}
		
		for(int i=0;i<5;i++)
		{
			victories_player1.add(new Sprite(1280/2+60+i*50,50,this.victoryFaceTextureRegion, this.getVertexBufferObjectManager()));
		}

		scene.attachChild(background);
		scene.attachChild(player1);
		scene.attachChild(player2);
		scene.attachChild(btn_kick1);
		scene.attachChild(btn_kick2);
		for(int i=0;i<victories_player1.size();i++)
			scene.attachChild(victories_player1.get(i));
		for(int i=0;i<victories_player2.size();i++)
			scene.attachChild(victories_player2.get(i));
		
		scene.registerTouchArea(btn_kick1);
		scene.registerTouchArea(btn_kick2);
		scene.setOnAreaTouchListener(this);

		return scene;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private static class Player extends AnimatedSprite
	{
		private final PhysicsHandler mPhysicsHandler;
		
		String state;

		public Player(final float pX, final float pY, final TiledTextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager) {
			super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
			this.mPhysicsHandler = new PhysicsHandler(this);
			this.registerUpdateHandler(this.mPhysicsHandler);
			this.mPhysicsHandler.setVelocity(FightActivity.DEMO_VELOCITY, FightActivity.DEMO_VELOCITY);
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
			this.setAlpha(0.2f);
		}

		@Override
		protected void onManagedUpdate(final float pSecondsElapsed)
		{
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
			}

			super.onManagedUpdate(pSecondsElapsed);
		}
		
		@Override
        public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
			if (pSceneTouchEvent.isActionDown())
			{
				this.setAlpha(1.0f);
			}
			if (pSceneTouchEvent.isActionUp())
			{
				this.setAlpha(0.5f);
			}
			player.touched();
			return mFlippedHorizontal;
		}
	}


	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			ITouchArea pTouchArea, float pTouchAreaLocalX,
			float pTouchAreaLocalY)
	{
		return false;
	}
	
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
    	if(OuyaController.BUTTON_L1==event.getKeyCode())
    	{
    		player1.touched();
    	}else if(OuyaController.BUTTON_R1==event.getKeyCode())
    	{
    		player2.touched();
    	}else
    	{
    		System.exit(0);
    	}
        return true;
    }
}