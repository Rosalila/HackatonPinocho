package rosalila.studio.hackatonpinocho;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.hardware.SensorManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public  class Player extends AnimatedSprite {

	private Body mBody;
	private PhysicsWorld mWorld;
	private FixtureDef mFixture = PhysicsFactory.createFixtureDef(0.5f, 0.5f, 0.5f);
	int number;
	
	private boolean mPendingJump;
	
	public Player(final float pX, final float pY, final TiledTextureRegion pTextureRegion, 
			final VertexBufferObjectManager pVertexBufferObjectManager, PhysicsWorld world,int number) {
		super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
		mWorld = world;
		mBody = PhysicsFactory.createBoxBody(mWorld, this, BodyType.DynamicBody, mFixture);
		mWorld.registerPhysicsConnector(new PhysicsConnector(this, mBody));
		mPendingJump = false;
		this.number=number;
	}
	
	public void jump() {
		mPendingJump = true;
	}
	
	public void onManagedUpdate(float elapsedSeconds) {
		super.onManagedUpdate(elapsedSeconds);
		
		if (mPendingJump) {
			mBody.applyForce(new Vector2(0.0f, -SensorManager.GRAVITY_EARTH * 2), mBody.getWorldCenter());
		}
	}

	
}