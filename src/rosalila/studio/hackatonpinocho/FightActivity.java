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
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
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
	
	private BitmapTextureAtlas victory1BitmapTextureAtlas;
	private TiledTextureRegion victory1FaceTextureRegion;
	
	private BitmapTextureAtlas victory2BitmapTextureAtlas;
	private TiledTextureRegion victory2FaceTextureRegion;
	
	private BitmapTextureAtlas player1winsBitmapTextureAtlas;
	private TiledTextureRegion player1winsFaceTextureRegion;
	
	private BitmapTextureAtlas player2winsBitmapTextureAtlas;
	private TiledTextureRegion player2winsFaceTextureRegion;
	
	private BitmapTextureAtlas koBitmapTextureAtlas;
	private TiledTextureRegion koFaceTextureRegion;

	ArrayList<Sprite> victories_player1;
	ArrayList<Sprite> victories_player2;
	
	Sprite player1_wins;
	Sprite player2_wins;
	Sprite ko;
	
	int ROUNDS = 5;
	int PLAYER1_INITIAL_X = 1280/2-400;
	int PLAYER2_INITIAL_X = 1280/2+400;
	
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
		
		this.victory1BitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 40, 60, TextureOptions.BILINEAR);
		this.victory1FaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.victory1BitmapTextureAtlas, this, "victory1.png", 0, 0, 1, 1);
		this.victory1BitmapTextureAtlas.load();
		
		this.victory2BitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 40, 60, TextureOptions.BILINEAR);
		this.victory2FaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.victory2BitmapTextureAtlas, this, "victory2.png", 0, 0, 1, 1);
		this.victory2BitmapTextureAtlas.load();
		
		this.player1winsBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 687, 460, TextureOptions.BILINEAR);
		this.player1winsFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.player1winsBitmapTextureAtlas, this, "player1_wins.png", 0, 0, 1, 1);
		this.player1winsBitmapTextureAtlas.load();
		
		this.player2winsBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 687, 460, TextureOptions.BILINEAR);
		this.player2winsFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.player2winsBitmapTextureAtlas, this, "player2_wins.png", 0, 0, 1, 1);
		this.player2winsBitmapTextureAtlas.load();
		
		this.koBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 687, 460, TextureOptions.BILINEAR);
		this.koFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.koBitmapTextureAtlas, this, "knockout.png", 0, 0, 1, 1);
		this.koBitmapTextureAtlas.load();
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
		final ButtonKick btn_kick2 = new ButtonKick(1280-this.mFaceTextureRegion.getWidth(), 0, this.mFaceTextureRegion, this.getVertexBufferObjectManager(),player2,this);
		
		victories_player1=new ArrayList<Sprite>();
		victories_player2=new ArrayList<Sprite>();
		
		for(int i=0;i<ROUNDS;i++)
		{
			Sprite sprite_temp = new Sprite(1280/2-60-i*50,50,this.victory1FaceTextureRegion, this.getVertexBufferObjectManager());
			sprite_temp.setVisible(false);
			victories_player1.add(sprite_temp);
		}
		
		for(int i=0;i<ROUNDS;i++)
		{
			Sprite sprite_temp = new Sprite(1280/2+60+i*50,50,this.victory2FaceTextureRegion, this.getVertexBufferObjectManager());
			sprite_temp.setVisible(false);
			victories_player2.add(sprite_temp);
		}
		player1_wins=new Sprite(1280/2-player1winsFaceTextureRegion.getWidth()/2, 0, player1winsFaceTextureRegion, this.getVertexBufferObjectManager());
		player1_wins.setVisible(false);
		player2_wins=new Sprite(1280/2-player2winsFaceTextureRegion.getWidth()/2, 0, player2winsFaceTextureRegion, this.getVertexBufferObjectManager());
		player2_wins.setVisible(false);
		ko=new Sprite(1280/2-koFaceTextureRegion.getWidth()/2, 0, koFaceTextureRegion, this.getVertexBufferObjectManager());
		ko.setVisible(false);

		mScene.attachChild(background);
		mScene.attachChild(player1);
		mScene.attachChild(player2);
		mScene.attachChild(btn_kick1);
		mScene.attachChild(btn_kick2);
		for(int i=0;i<victories_player1.size();i++)
			mScene.attachChild(victories_player1.get(i));
		for(int i=0;i<victories_player2.size();i++)
			mScene.attachChild(victories_player2.get(i));
		mScene.attachChild(player1_wins);
		mScene.attachChild(player2_wins);
		mScene.attachChild(ko);
		
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
		if(player_number == 1)
		{
			boolean game_over = false;
			for(int i=0;i<victories_player1.size();i++)
			{
				if(!victories_player1.get(i).isVisible())
				{
					ko.setVisible(true);
					victories_player1.get(i).setVisible(true);
					if(i==victories_player1.size()-1)
						game_over=true;
					break;
				}
			}
			if(game_over)
				player1_wins.setVisible(true);
		}
		if(player_number == 2)
		{
			boolean game_over = false;
			for(int i=0;i<victories_player2.size();i++)
			{
				ko.setVisible(true);
				if(!victories_player2.get(i).isVisible())
				{
					victories_player2.get(i).setVisible(true);
					if(i==victories_player2.size()-1)
						game_over=true;
					break;
				}
			}
			if(game_over)
				player2_wins.setVisible(true);
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
        public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY)
		{
			if (pSceneTouchEvent.isActionDown())
			{
				this.setAlpha(1.0f);
				if(fight_activity.player1_wins.isVisible()
				|| fight_activity.player2_wins.isVisible())
				{
					System.exit(0);
				}
				if(fight_activity.ko.isVisible())
				{
					fight_activity.resetRound();
				}else
				{
					fight_activity.playerWinsRound(mPlayer.number);
				}
				
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
	
	void resetRound()
	{
		ko.setVisible(false);
		player1.mBody.setTransform(PLAYER1_INITIAL_X/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 720/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 0);
		player2.mBody.setTransform(PLAYER2_INITIAL_X/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 720/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 0);
		player1.mBody.setLinearVelocity(new Vector2(0,0));
		player2.mBody.setLinearVelocity(new Vector2(0,0));
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
			    	
			    }
			}
		};
    }
}