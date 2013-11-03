package rosalila.studio.hackatonpinocho;

import java.util.ArrayList;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import tv.ouya.console.api.OuyaController;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.KeyEvent;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga
 *
 * @author Nicolas Gramlich
 * @since 11:54:51 - 03.04.2010
 */

public class FightActivity extends SimpleBaseGameActivity implements IOnAreaTouchListener {
	
	protected static final String TAG = FightActivity.class.getSimpleName();
	private BitmapTextureAtlas mBitmapTextureAtlas;
	private TiledTextureRegion mFaceTextureRegion;

	
	private BitmapTextureAtlas bgBitmapTextureAtlas;
	private TiledTextureRegion bgFaceTextureRegion;
	
	private BitmapTextureAtlas victoryBitmapTextureAtlas;
	private TiledTextureRegion victoryFaceTextureRegion;

	ArrayList<Sprite> victories_player1;
	ArrayList<Sprite> victories_player2;
	
	int ROUNDS = 5;
	
	private Player player1,player2;
	
	private PhysicsWorld mPhysicsWorld;

	private Scene mScene;

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

		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		this.mFaceTextureRegion = Util.createTiledTextureRegionFromAsset(getTextureManager(), this, 210, 210, 
				"1.png",
				"2.png",
				"3.png",
				"4.png",
				"5.png");
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

		mScene = new Scene();
		mScene.setBackground(new Background(0.09804f, 0.6274f, 0.8784f));
		
		mPhysicsWorld = new PhysicsWorld(new Vector2(0.0f, SensorManager.GRAVITY_EARTH), false);
		mPhysicsWorld.setContactListener(createContactListener());
		mScene.registerUpdateHandler(mPhysicsWorld);
		
		final float centerX = (GameConstants.CAMERA_WIDTH - this.mFaceTextureRegion.getWidth()) / 2;
		final float centerY = (GameConstants.CAMERA_HEIGHT - this.mFaceTextureRegion.getHeight()) / 2;
		
		final Sprite background = new Sprite(0,0,this.bgFaceTextureRegion,this.getVertexBufferObjectManager());

		player1 = new Player(centerX, centerY, this.mFaceTextureRegion, this.getVertexBufferObjectManager(), mPhysicsWorld,1);
		player2 = new Player(centerX+200, centerY, this.mFaceTextureRegion, this.getVertexBufferObjectManager(), mPhysicsWorld,2);
		final ButtonKick btn_kick1 = new ButtonKick(0, 0, this.mFaceTextureRegion, this.getVertexBufferObjectManager(),player1,this);
		final ButtonKick btn_kick2 = new ButtonKick(100, 0, this.mFaceTextureRegion, this.getVertexBufferObjectManager(),player2,this);
		
		victories_player1=new ArrayList<Sprite>();
		victories_player2=new ArrayList<Sprite>();
		
		for(int i=0;i<ROUNDS;i++)
		{
			Sprite sprite_temp = new Sprite(1280/2-60-i*50,50,this.victoryFaceTextureRegion, this.getVertexBufferObjectManager());
			sprite_temp.setVisible(false);
			victories_player1.add(sprite_temp);
		}
		
		for(int i=0;i<ROUNDS;i++)
		{
			Sprite sprite_temp = new Sprite(1280/2+60+i*50,50,this.victoryFaceTextureRegion, this.getVertexBufferObjectManager());
			sprite_temp.setVisible(false);
			victories_player1.add(sprite_temp);
		}

		mScene.attachChild(background);
		mScene.attachChild(player1);
		mScene.attachChild(player2);
		mScene.attachChild(btn_kick1);
		mScene.attachChild(btn_kick2);
		for(int i=0;i<victories_player1.size();i++)
			mScene.attachChild(victories_player1.get(i));
		for(int i=0;i<victories_player2.size();i++)
			mScene.attachChild(victories_player2.get(i));
		
		mScene.registerTouchArea(btn_kick1);
		mScene.registerTouchArea(btn_kick2);
		mScene.setOnAreaTouchListener(this);
		
		createGroundAndWalls();
		
		return mScene;
	}
	
	/*
	 * Function called when a player wins a round
	 * */
	private void playerWinsRound(int player_number)
	{
		if(player_number == 2)
		{
			boolean game_over = false;
			for(int i=0;i<victories_player2.size();i++)
			{
				if(!victories_player2.get(i).isVisible())
				{
					victories_player2.get(i).setVisible(true);
					if(i==victories_player2.size()-1)
						game_over=true;
					break;
				}
			}
			if(game_over)
				System.exit(0);
		}
	}
	
	/*
	 * Creates the ground and walls to avoid the players get of
	 * limits
	 * */
	private void createGroundAndWalls() {
		Rectangle ground = new Rectangle(0, 0, GameConstants.CAMERA_WIDTH, 2.0f, getVertexBufferObjectManager());
		Body groundBody = PhysicsFactory.createBoxBody(mPhysicsWorld, ground, BodyType.StaticBody, GameConstants.GROUND_FIXTURE);
		groundBody.setUserData("ground");
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(ground, groundBody));
		mScene.attachChild(ground);
		
		Rectangle leftWall = new Rectangle(0, 0, 2.0f, GameConstants.CAMERA_HEIGHT, getVertexBufferObjectManager());
		Body leftWallBody = PhysicsFactory.createBoxBody(mPhysicsWorld, leftWall, BodyType.StaticBody, GameConstants.GROUND_FIXTURE);
		leftWallBody.setUserData("left");
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(leftWall, leftWallBody));
		mScene.attachChild(leftWall);
		
		Rectangle rightWall = new Rectangle(GameConstants.CAMERA_WIDTH, 0, 2.0f, GameConstants.CAMERA_HEIGHT, getVertexBufferObjectManager());
		Body rightWallBody = PhysicsFactory.createBoxBody(mPhysicsWorld, rightWall, BodyType.StaticBody, GameConstants.GROUND_FIXTURE);
		rightWallBody.setUserData("right");
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(rightWall, rightWallBody));
		mScene.attachChild(rightWall);
		
		Rectangle roof = new Rectangle(0, GameConstants.CAMERA_HEIGHT, GameConstants.CAMERA_WIDTH, 2.0f, getVertexBufferObjectManager());
		Body roofBody = PhysicsFactory.createBoxBody(mPhysicsWorld, roof, BodyType.StaticBody, GameConstants.GROUND_FIXTURE);
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(roof, roofBody));
		roofBody.setUserData("roof");
		mScene.attachChild(roof);
		
	}
	
	
	private static class ButtonKick extends AnimatedSprite {
		private static final String TAG = ButtonKick.class.getSimpleName();
		
		Player mPlayer;
		FightActivity fight_activity;

		public ButtonKick(final float pX, final float pY, final TiledTextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager, Player player,FightActivity fight_activity)
		{
			super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
			this.setAlpha(0.5f);
			mPlayer = player;
			this.fight_activity=fight_activity;
		}

		@Override
		protected void onManagedUpdate(final float pSecondsElapsed)	{
			super.onManagedUpdate(pSecondsElapsed);
		}
		
		@Override
        public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
			if (pSceneTouchEvent.isActionDown())
			{
				this.setAlpha(1.0f);			
				fight_activity.playerWinsRound(mPlayer.number);
				
				if (!mPlayer.isMoving()) {
					Log.d(TAG, "On Ground");
					mPlayer.jump();	
				} else {
					Log.d(TAG, "Jumping");
					mPlayer.dive();
				}
			}
			
			if (pSceneTouchEvent.isActionUp())	{
				this.setAlpha(0.5f);
			}
			
			
			
			return false;
		}
	}


	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			ITouchArea pTouchArea, float pTouchAreaLocalX,
			float pTouchAreaLocalY)	{
		return false;
	}
	
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
    	if(OuyaController.BUTTON_L1==event.getKeyCode())
    	{ 
    		player1.jump();
    	}else if(OuyaController.BUTTON_R1==event.getKeyCode())
    	{
    		player2.jump();
    	}else
    	{
    		System.exit(0);
    	}
        return true;
    }
    
    private ContactListener createContactListener() {
    	return new ContactListener() {
			
			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void endContact(Contact contact) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void beginContact(Contact contact) {
			    Fixture fixtureA = contact.getFixtureA();
			    Fixture fixtureB = contact.getFixtureB();
			    
			    Body bodyA = fixtureA.getBody();
			    Body bodyB = fixtureB.getBody();
			    
			    Object dataA = bodyA.getUserData();
			    Object dataB = bodyB.getUserData();
			    
			    if (dataA instanceof Player && dataB.equals("roof")) {
			        Player p = (Player)dataA;
			        p.setCurrentTileIndex(0);
			    } else if (dataB instanceof Player && dataA.equals("roof")) {
			    	Player p = (Player)dataA;
			        p.setCurrentTileIndex(0);
			    } else if (dataA instanceof Player && dataB instanceof Player) {
			    	Player playerA = (Player)dataA;
			    	Player playerB = (Player)dataB;
			    	
			    	if (playerA.getY() > playerB.getY()) {
			    		playerWinsRound(1);
			    	} else {
			    		playerWinsRound(2);
			    	}
			    }
			}
		};
    }
}