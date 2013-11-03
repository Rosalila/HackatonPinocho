package rosalila.studio.hackatonpinocho;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public  class Player extends AnimatedSprite {

	private static final String TAG = Player.class.getSimpleName();
	public Body mBody;
	private PhysicsWorld mWorld;
	private FixtureDef mFixture = PhysicsFactory.createFixtureDef(0.5f, 0.5f, 0.5f);
	private FacingSide mSide;
	private boolean mIsOnGround;
	int number;
	
	private Vector2 mDesiredSpeed = new Vector2(0.0f, 20.0f);
	
	private boolean mPendingJump;
	private boolean mPendingDive;
	
	public static enum FacingSide {
	    Right, Left	
	}
	
	public Player(final float pX, final float pY, final TiledTextureRegion pTextureRegion, 
			final VertexBufferObjectManager pVertexBufferObjectManager, PhysicsWorld world, int number) {
		super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
		mWorld = world;
		mBody = PhysicsFactory.createBoxBody(mWorld, this, BodyType.DynamicBody, mFixture);
		mBody.setFixedRotation(true);
		mBody.setUserData(this);
		mWorld.registerPhysicsConnector(new PhysicsConnector(this, mBody));
		mPendingJump = false;
		mSide = number == 1? FacingSide.Right : FacingSide.Left;
		this.number=number;
	}
	
	public void jump() {
		mPendingJump = true;
		setCurrentTileIndex(1);
	}
	
	public void dive() {
		mPendingDive = true;
		setCurrentTileIndex(2);
	}
	
	public void onManagedUpdate(float elapsedSeconds) {
		super.onManagedUpdate(elapsedSeconds);
	
		
		if (mPendingJump) {
			changeSpeed(mDesiredSpeed);
			mPendingJump = false;
		}
			
		if (mPendingDive) {
			Vector2 desiredVel = Vector2Pool.obtain();
			switch (mSide) {
			case Right:
				desiredVel.x = 80.0f;
				desiredVel.y = -20.0f;
				break;
				
			case Left:
				desiredVel.x = -80.0f;
				desiredVel.y = -20.0f;
				break;
			}
			
			changeSpeed(desiredVel);			
			Vector2Pool.recycle(desiredVel);
			mPendingDive = false;
		}
	}
	
	public boolean isMoving() {
		return this.getY() < GameConstants.CAMERA_HEIGHT - 300;
	}
	
	private void changeSpeed(Vector2 desiredSpeed) {
		Vector2 velocity = mBody.getLinearVelocity();
		
		float velocityChangeY = desiredSpeed.y - velocity.y;
		float velocityChangeX = desiredSpeed.x - velocity.x;
		
		float impulseY = mBody.getMass() * velocityChangeY;
		float impulseX = mBody.getMass() * velocityChangeX;
		mBody.applyLinearImpulse(new Vector2(impulseX, impulseY), mBody.getWorldCenter());
	}	
}