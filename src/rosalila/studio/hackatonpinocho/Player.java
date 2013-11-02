package rosalila.studio.hackatonpinocho;

import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public  class Player extends AnimatedSprite {
	private final PhysicsHandler mPhysicsHandler;
	
	String state;

	public Player(final float pX, final float pY, final TiledTextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager) {

		super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
		this.mPhysicsHandler = new PhysicsHandler(this);
		this.registerUpdateHandler(this.mPhysicsHandler);
		this.mPhysicsHandler.setVelocity(GameConstants.DEMO_VELOCITY, GameConstants.DEMO_VELOCITY);

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
//			if(this.mX < 0) {
//				this.mPhysicsHandler.setVelocityX(FightActivity.DEMO_VELOCITY);
//			} else if(this.mX + this.getWidth() > FightActivity.CAMERA_WIDTH) {
//				this.mPhysicsHandler.setVelocityX(-FightActivity.DEMO_VELOCITY);
//			}
//
//			if(this.mY < 0) {
//				this.mPhysicsHandler.setVelocityY(FightActivity.DEMO_VELOCITY);
//			} else if(this.mY + this.getHeight() > FightActivity.CAMERA_HEIGHT) {
//				this.mPhysicsHandler.setVelocityY(-FightActivity.DEMO_VELOCITY);
//			}
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